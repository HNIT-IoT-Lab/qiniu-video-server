package com.qiniu.video.service.impl;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.common.page.Page;
import cn.hnit.sdk.orm.mongodb.entity.BaseEntity;
import cn.hnit.sdk.orm.mongodb.entity.PageVO;
import cn.hnit.utils.context.UserContext;
import com.qiniu.video.dao.ArticleDao;
import com.qiniu.video.dao.UserArticleInteractionDao;
import com.qiniu.video.entity.constant.UserArticleInteractionConstant;
import com.qiniu.video.entity.enums.InteractionTypeEnum;
import com.qiniu.video.entity.model.Article;
import com.qiniu.video.entity.constant.UserFileConstant;
import com.qiniu.video.entity.model.User;
import com.qiniu.video.entity.model.UserArticleInteraction;
import com.qiniu.video.entity.req.ArticleReq;
import com.qiniu.video.es.entity.EsArticle;
import com.qiniu.video.es.service.EsArticleService;
import com.qiniu.video.service.ArticleService;
import com.qiniu.video.service.FilesService;
import com.qiniu.video.service.UserService;
import javafx.scene.shape.Circle;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private UserService userService;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private UserArticleInteractionDao userArticleInteractionDao;
    @Autowired
    private EsArticleService esArticleService;
    @Autowired
    private ThreadPoolExecutor asyncExecutor;
    @Autowired
    private FilesServiceImpl filesService;

    @Override
    public Article AddArticle(MultipartFile file, ArticleReq req) {
        String upload;
        if (file != null) {
            //上传到kodo，拿到url，存到mongo
            upload = userService.upload(file);
        } else {
            upload = "";
        }
        //拿到视频封面
        String cover = filesService.GenCover(upload, UserFileConstant.UserFileKind.VIDEO);
        Future<Article> f1 = asyncExecutor.submit(() -> {
            // 保存到mongo
            return articleDao.save(Article.builder()
                    .uid(UserContext.getUserId())
                    .title(req.getTitle())
                    .author(userService.FindById(UserContext.getUserId()).getUserName())
                    .keyWord(req.getKeyWord())
                    .content(req.getContent())
                    .cover(cover)
                    .articleKind(GenKind(upload))
                    .urlList(new ArrayList<>(Arrays.asList(upload)))
                    .likeCounts(0)
                    .collectionCounts(0)
                    .isLike(false)
                    .isCollect(false)
                    .build());
        });
        Future<?> f2 = asyncExecutor.submit(() -> {
            // 保存到es
            esArticleService.addArticle(EsArticle.builder()
                    .author(userService.FindById(UserContext.getUserId()).getUserName())
                    .title(req.getTitle())
                    .content(req.getContent())
                    .createTime(new Date())
                    .build());
        });

        Article article;

        try {
            article = f1.get();
            f2.get();
        } catch (Exception e) {
            log.error("AddArticle fail {}", e.getMessage());
            throw AppException.pop("保存失败");
        }
        return article;
    }

    private UserFileConstant.UserFileKind GenKind(String filePath) {
        UserFileConstant.UserFileKind kind = UserFileConstant.UserFileKind.of(filePath);
        return kind == null ? UserFileConstant.UserFileKind.DOCUMENT : kind;
    }

    @Override
    public SearchHits<EsArticle> search(String keyword) {
        return esArticleService.searchArticle(keyword);
    }


    /**
     * 每次请求去拿到三条数据
     * @return
     */
    @Override
    public List<Article> getVideoUrl(String tag) {
        List<Long> ids = new ArrayList<>();
        //过滤数据根据关键词去过滤
        Query query = new Query();
        if(!StringUtils.isEmpty(tag)){
            query.addCriteria(Criteria.where(Article.Fields.keyWord).is(tag));
        }
        query.addCriteria(Criteria.where(Article.Fields.articleKind).is(UserFileConstant.UserFileKind.VIDEO));
        Iterator<Article> iterator = articleDao.find(query).iterator();
        while (iterator.hasNext()) {
            Article article = iterator.next();
            ids.add(article.getId());
        }
        Collections.shuffle(ids);
        List<Long> randomIds = ids.subList(0, 5);
        List<Article> articles = articleDao.find(Query.query(Criteria.where(BaseEntity.Fields.id).in(randomIds)));
        return articles;
    }
    /**
     * 获取文章数据:分页查询, 每次查10条数据
     *
     * @return
     */
    @Override
    public Page<Article> getArticleList(PageVO pageVo) {
        // 定义分页参数
        pageVo.setSkip(Long.valueOf((pageVo.getCurrentPage()-1) * pageVo.getPageSize()));
        // 定义一个convert函数：将T类型的对象转换为R类型的对象
        Function<Article, Article> convert = t -> {
            // 进行具体的转换操作
            return t;
        };
        // 执行分页查询
        Page<Article> articlePage = articleDao.page(pageVo, convert ,null);
        //当前页
        articlePage.setPageNo(pageVo.getCurrentPage());
        //这一页的条数
        articlePage.setPageSize(pageVo.getPageSize());

        return articlePage;
    }

    /**
     * 热门视频推荐
     * 协同过滤算法 + LRU最近最少使用算法
     * @return
     */
    @Override
    public List<Article> getHotArticle() {
        //获取的当前用户
        Long userId = UserContext.getUserId();
        //给当前用户推荐视频
        //根据用户Id推荐
        List<UserArticleInteraction> userArticleInteractions = userArticleInteractionDao.find(Query.query(Criteria.where(UserArticleInteraction.Fields.userId).is(userId)));
        List<String> articleIds = userArticleInteractions.stream().map(UserArticleInteraction::getArticleId).collect(Collectors.toList());

        // 根据用户的历史交互(阅读、点赞、收藏)，推荐相似的文章
        List<String> recommendedArticleIds = getRecommendedArticles(articleIds);

        List<Long> longList = recommendedArticleIds.stream().map(Long::parseLong).collect(Collectors.toList());
        return articleDao.queryList(Query.query(Criteria.where(BaseEntity.Fields.id).in(longList)));
    }

    /**
     * 点赞
     * 用户文章交互类中增加一条记录
     * 点赞或者收藏时在Article里根据articleId更新数据
     * @param articleId
     */
    @Override
    public UserArticleInteraction starArticle(String articleId,String type,Boolean flag) {
        //拿到当前用户
        Long userId = UserContext.getUserId();
        Article article = articleDao.findOne(Query.query(Criteria.where(BaseEntity.Fields.id).is(articleId)));
        //判断是点赞还是收藏
        if(type.equals(UserArticleInteractionConstant.InteractionType.LIKE)){
            //Article表里增加点赞数
            if(flag){
                articleDao.save(Article.builder()
                        .isLike(true)
                        .likeCounts(article.getLikeCounts() + 1)
                        .build());
            }else{
                articleDao.save(Article.builder()
                        .isLike(false)
                        .likeCounts(article.getLikeCounts() - 1)
                        .build());
            }

            return userArticleInteractionDao.save(UserArticleInteraction.builder()
                    .articleId(articleId)
                    .userId(String.valueOf(userId))
                    .interactionType(UserArticleInteractionConstant.InteractionType.LIKE)
                    .build()
            );
        }else{
            if(flag){
                articleDao.save(Article.builder()
                        .isLike(true)
                        .collectionCounts(article.getCollectionCounts() + 1)
                        .build());
            }else{
                articleDao.save(Article.builder()
                        .isLike(false)
                        .collectionCounts(article.getCollectionCounts() - 1)
                        .build());
            }
            return userArticleInteractionDao.save(UserArticleInteraction.builder()
                    .articleId(articleId)
                    .userId(String.valueOf(userId))
                    .interactionType(UserArticleInteractionConstant.InteractionType.COLLECTION)
                    .build()
            );
        }
    }

    /**
     * 获取收藏视频列表
     * @return
     */
    @Override
    public List<Article> getCollectArticle() {
        //先拿到收藏视频的id
        List<UserArticleInteraction> userArticleInteractions = userArticleInteractionDao.find(Query.query(Criteria.where(UserArticleInteraction.Fields.interactionType)
                                                                                            .is(UserArticleInteractionConstant.InteractionType.COLLECTION)));
        List<String> articleIds = userArticleInteractions.stream().map(UserArticleInteraction::getArticleId).collect(Collectors.toList());
        List<Long> id = articleIds.stream().map(Long::parseLong).collect(Collectors.toList());
        return articleDao.listByIds(id);
    }


    /**
     * LRUCache的缓存机制
     * @param articleIds
     * @return
     */
    public List<String> getRecommendedArticles(List<String> articleIds) {
        LRUCache<List<String>, List<String>> cache = new LRUCache<>(10);
        // 查找缓存中是否存在指定的文章ID列表
        if (cache.containsKey(articleIds)) {
            return cache.get(articleIds);
        }

        // 如果缓存中不存在，则进行推荐算法的计算
        List<String> recommendedArticleIds = recommendSimilarArticles(articleIds);

        // 将计算结果存储到LRUCache中
        cache.put(articleIds, recommendedArticleIds);

        return recommendedArticleIds;
    }

    /**
     * 实现协同过滤算法
     * 根据用户的历史交互数据，推荐相似的文章
     * @param articleIds
     * @return
     */
    private List<String> recommendSimilarArticles(List<String> articleIds) {
        // 统计文章得分
        Map<String, Integer> articleScores = new HashMap<>();

        for (String articleId : articleIds) {
            // 根据文章id获取相似的用户
            List<User> similarUsers = getSimilarUsers(articleId);
            // 统计相似用户喜欢的文章得分
            for (User user : similarUsers) {
                //获取用户喜欢的文章
                List<Article> userLikedArticles = getUserLikedArticles(user.getId().toString());
                for (Article likedArticle : userLikedArticles) {
                    if (!articleIds.contains(likedArticle.getId())) {
                        articleScores.put(String.valueOf(likedArticle.getId()), articleScores.getOrDefault(likedArticle.getId(), 0) + 1);
                    }
                }
            }
        }
        // 对得分进行排序并返回推荐的文章ID列表
        return articleScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取相似的用户
     * @param articleId
     * @return
     */
    private List<User> getSimilarUsers(String articleId) {

        // 根据给定文章的特征，获取相关文章列表
        List<Article> relatedArticles = getRelatedArticles(articleId);

        // 统计用户点赞相关文章的次数
        Map<Long, Integer> userLikesCount = new HashMap<>();
        for (Article relatedArticle : relatedArticles) {
            List<User> usersLikedArticles = getUsersLikedArticle(relatedArticle.getId().toString());
            for (User usersLikedArticle : usersLikedArticles) {
                userLikesCount.put(usersLikedArticle.getId(), userLikesCount.getOrDefault(usersLikedArticle.getId(), 0) + 1);
            }
        }
        // 根据喜好次数对用户进行排序，并选择得分最高的一些用户作为相似用户
        List<Long> userId = userLikesCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return userService.FindByIds(userId);
    }

    /**
     * 获取相关联的文章
     * @param articleId
     * @return
     */
    private List<Article> getRelatedArticles(String articleId) {
        List<Article> relatedArticles = new ArrayList<>();
        // 获取文章列表
        List<Article> allArticles = articleDao.find(null);

        // 遍历所有文章，根据特征匹配相关文章
        for (Article article : allArticles) {
            if (isArticleRelated(article, articleId)) {
                relatedArticles.add(article);
            }
        }
        return relatedArticles;
    }

    /**
     * 根据关键词进行匹配,判断两篇文章是否相关
     * @TODO 关键词添加的时候最好指定一下，比如：体育，动漫，电影
     * @param article
     * @param articleId
     * @return
     */
    private boolean isArticleRelated(Article article, String articleId) {
        //根据articleId拿到keyWord
        Article articleDaoOne = articleDao.findOne(Query.query(Criteria.where(BaseEntity.Fields.id).is(Long.valueOf(articleId))));
        String keyWord = articleDaoOne.getKeyWord();
        String articleKeyWord = article.getKeyWord();

//        boolean isMatch = keyWord.stream()
//                .anyMatch(key -> articleKeyWord.stream()
//                        .anyMatch(articleKey -> key.equals(articleKey)));
        boolean isMatch = keyWord.equals(articleKeyWord);
        return isMatch;
    }

    /**
     * 获取喜好该文章的用户列表
     * @param articleId
     * @return
     */
    private List<User> getUsersLikedArticle(String articleId) {
        //思路:根据articleId以及interactionType的值为点赞或关注就去拿userId,
        //因为我们认为只要用户点赞、关注中的任一种行为，就认为该用户喜欢
        List<UserArticleInteraction> userArticleInteractions = userArticleInteractionDao.find(Query.query(Criteria.where(UserArticleInteraction.Fields.articleId).is(articleId)));
        List<String> userIds = userArticleInteractions.stream().map(UserArticleInteraction::getUserId).collect(Collectors.toList());
        List<Long> userIdsLong = userIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return userService.FindByIds(userIdsLong);
    }

    /**
     * 获取用户喜欢的文章
     * @param userId
     * @return
     */
    private List<Article> getUserLikedArticles(String userId) {
        // 根据用户ID获取用户喜好的文章列表
        // 用户点赞并收藏的文章
        List<UserArticleInteraction> userArticleInteractions = userArticleInteractionDao.find(Query.query(Criteria.where(UserArticleInteraction.Fields.userId).is(userId)));
        List<String> articleIds = userArticleInteractions.stream().map(UserArticleInteraction::getArticleId).collect(Collectors.toList());
        List<Long> articleIdsLong = articleIds.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        return articleDao.find(Query.query(Criteria.where(BaseEntity.Fields.id).in(articleIdsLong)));
    }
    /**
     * LRU缓存的实现,用于存储最近访问过的文章
     * @param <K>
     * @param <V>
     */
    public class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }
}
