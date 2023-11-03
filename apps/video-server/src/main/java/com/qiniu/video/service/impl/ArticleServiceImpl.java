package com.qiniu.video.service.impl;

import cn.hnit.common.exception.base.AppException;
import cn.hnit.common.page.Page;
import cn.hnit.sdk.orm.mongodb.entity.PageVO;
import cn.hnit.utils.context.UserContext;
import com.qiniu.video.dao.ArticleDao;
import com.qiniu.video.entity.model.Article;
import com.qiniu.video.entity.constant.UserFileConstant;
import com.qiniu.video.entity.req.ArticleReq;
import com.qiniu.video.es.entity.EsArticle;
import com.qiniu.video.es.service.EsArticleService;
import com.qiniu.video.service.ArticleService;
import com.qiniu.video.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private UserService userService;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private EsArticleService esArticleService;
    @Autowired
    private ThreadPoolExecutor asyncExecutor;

    @Override
    public Article AddArticle(MultipartFile file, ArticleReq req) {
        String upload;
        if (file != null) {
            //上传到kodo，拿到url，存到mongo
            upload = userService.upload(file);
        } else {
            upload = "";
        }
        Future<Article> f1 = asyncExecutor.submit(() -> {
            // 保存到mongo
            return articleDao.save(Article.builder()
                    .uid(UserContext.getUserId())
                    .title(req.getTitle())
                    .content(req.getContent())
                    .articleKind(GenKind(upload))
                    .build());
        });
        Future<?> f2 = asyncExecutor.submit(() -> {
            // 保存到es
            esArticleService.addArticle(EsArticle.builder()
                    .author(userService.FindById(UserContext.getUserId()).getUserName())
                    .title(req.getTitle())
                    .content(req.getContent())
                    .createTime(LocalDateTime.now())
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


    public SearchHits<EsArticle> search(String keyword) {
        return esArticleService.searchArticle(keyword);
    }


    /**
     * 每次去查一条数据，根据Id循环去拿取
     *
     * @return
     */
    @Override
    public Article getVideoUrl() {

        List<String> ids = new ArrayList<>();
        Integer currentIndex = 0;

        // 如果所有 ID 都已使用过，重新查询数据库获取新的 ID 列表
        if (currentIndex >= ids.size()) {
            ids.clear();
            currentIndex = 0;

            // 查询数据库并获取 ID 列表
            // 这里是查询的视频文件，所以要做过滤
            Iterator<Article> iterator = articleDao.find(Query.query(Criteria.where(Article.Fields.articleKind).is(UserFileConstant.UserFileKind.VIDEO))).iterator();
            while (iterator.hasNext()) {
                Article article = iterator.next();
                ids.add(article.getId().toString());
            }
        }

        // 获取当前索引对应的 ID
        String currentId = ids.get(currentIndex++);

        // 根据 ID 查询并返回文章
        return articleDao.findOne(Query.query(Criteria.where(Article.ID).is(currentId)));
    }

    /**
     * 获取文章数据:分页查询, 每次查10条数据
     *
     * @return
     */
    @Override
    public List<Article> getArticleList() {
        // 定义分页参数
        PageVO pageVO = new PageVO();
        // 设置要跳过的文档数量
        pageVO.setSkip(Long.valueOf((pageVO.getPageNumber()-1) * pageVO.getPageSize()));
        // 执行分页查询
        Page<Article> articlePage = articleDao.page(pageVO, null, null, null);
        // 获取查询结果
        List<Article> articles = articlePage.getResult();
        return articles;
    }

}
