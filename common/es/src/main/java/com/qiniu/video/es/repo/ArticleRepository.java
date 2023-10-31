package com.qiniu.video.es.repo;

import com.qiniu.video.es.entity.EsArticle;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface  ArticleRepository extends ElasticsearchRepository<EsArticle, String> {

    /**
     *
     * @param title 支持文章模糊查询
     * @param author 支持作者模糊查询
     * @return 返回匹配得文章
     */
    List<EsArticle> findByTitleOrAuthorOrContent(String title, String author, String content);

    /**
     * 通过搜索标题和内容高亮匹配文章
     * @param keyword 用来匹配标题和内容
     * @return 查询的文章
     */
    @Highlight(fields = {
            @HighlightField(name = "title"),
            @HighlightField(name = "author"),
            @HighlightField(name = "content")
    })
    @Query("{\"bool\":{\"should\":[{\"match\":{\"title\":\"?0\"}},{\"match\":{\"content\":\"?0\"}}]}}")
    SearchHits<EsArticle> find(String keyword);
}
