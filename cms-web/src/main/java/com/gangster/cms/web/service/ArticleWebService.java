package com.gangster.cms.web.service;

import com.gangster.cms.common.pojo.*;
import com.gangster.cms.dao.mapper.*;
import com.gangster.cms.web.cache.CmsCache;
import com.gangster.cms.web.cache.impl.HashMapCache;
import com.gangster.cms.web.cache.impl.LRUCache;
import com.gangster.cms.web.dto.ModelResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleWebService {
    private final Logger logger = LoggerFactory.getLogger(ArticleWebService.class);

    private final
    SiteMapper siteMapper;

    private final
    ArticleMapper articleMapper;

    private final
    CategoryMapper categoryMapper;

    private final TagArticleMapper tagArticleMapper;

    private final TagMapper tagMapper;

    private final CategoryWebService categoryWebService;

    private CmsCache<Integer, ModelResult> articleModelCache = new LRUCache<>(128);

    public ArticleWebService(SiteMapper siteMapper, ArticleMapper articleMapper, CategoryMapper categoryMapper, TagArticleMapper tagArticleMapper, TagMapper tagMapper, CategoryWebService categoryWebService) {
        this.siteMapper = siteMapper;
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagArticleMapper = tagArticleMapper;
        this.tagMapper = tagMapper;
        this.categoryWebService = categoryWebService;
    }

    public ModelResult getArticleModel(Integer id) {
        if (articleModelCache.containsKey(id)) {
            ModelResult r = articleModelCache.get(id);
            Article a = (Article) r.get("article");
            addArticleHit(a);
            logger.info("get ArticleModel:{} from cache", id);
            return r;
        }

        Article article = articleMapper.selectByPrimaryKey(id);
        ModelResult result = new ModelResult();

        if (article == null) {
            return null;
        }

        //Get article's site
        Site site = siteMapper.selectByPrimaryKey(article.getArticleSiteId());

        //Get article's category
        Category category = categoryMapper.selectByPrimaryKey(article.getArticleCategoryId());

        //Get article's tags
        TagArticleExample tagArticleExample = new TagArticleExample();
        tagArticleExample.or().andArticleIdEqualTo(id);
        List<Integer> tagIdList = tagArticleMapper.selectByExample(tagArticleExample).stream().map(TagArticle::getTagId).collect(Collectors.toList());
        List tagList;
        if (tagIdList.isEmpty()) {
            tagList = Collections.EMPTY_LIST;
        } else {
            TagExample tagExample = new TagExample();
            tagExample.or().andTagIdIn(tagIdList);
            tagList = tagMapper.selectByExample(tagExample);
        }
        //Get 0 level categorise in this site (displayed above the homepage of the website)
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.or().andCategorySiteIdEqualTo(article.getArticleSiteId()).andCategoryLevelEqualTo(0);
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        //Each level 0 category into category tree
        List<CategoryTree> categoryTreeList = categoryList.stream().map(categoryWebService::toTree).collect(Collectors.toList());

        result.put("category", category)
                .put("article", article)
                .put("tagList", tagList)
                .put("categoryTreeList", categoryTreeList)
                .put("site", site);

        addArticleHit(article);

        articleModelCache.put(id, result);
        return result;
    }

    private void addArticleHit(Article article) {
        //hit add
        if (article.getArticleHit() == null) {
            article.setArticleHit(0);
        }

        article.setArticleHit(article.getArticleHit() + 1);
        articleMapper.updateByPrimaryKeySelective(article);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void flushCache() {
        articleModelCache.clear();
        logger.info("refresh article cache");
    }
}
