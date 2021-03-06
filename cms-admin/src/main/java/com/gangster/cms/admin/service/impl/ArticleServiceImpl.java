package com.gangster.cms.admin.service.impl;

import com.gangster.cms.admin.base.impl.BaseServiceImpl;
import com.gangster.cms.admin.service.ArticleService;
import com.gangster.cms.admin.service.TagService;
import com.gangster.cms.admin.service.WebFileService;
import com.gangster.cms.admin.util.FileTool;
import com.gangster.cms.common.constant.CmsConst;
import com.gangster.cms.common.pojo.*;
import com.gangster.cms.dao.mapper.ArticleMapper;
import com.gangster.cms.dao.mapper.TagArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends BaseServiceImpl<ArticleMapper, Article, ArticleExample> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    TagService tagService;

    @Autowired
    TagArticleMapper tagArticleMapper;

    @Autowired
    private WebFileService webFileService;

    @Autowired
    private FileTool fileTool;

    private List<Integer> selectArticlesIdByTagName(String tag) {
        TagExample tagExample = new TagExample();
        tagExample.createCriteria().andTagNameEqualTo(tag);
        List<Tag> tags = tagService.selectByExample(tagExample);
        if (tags == null || tags.size() == 0) {
            return Collections.emptyList();
        }

        TagArticleExample tagArticleExample = new TagArticleExample();
        tagArticleExample.createCriteria().andTagIdEqualTo(tags.get(0).getTagId());
        tagArticleMapper.selectByExample(tagArticleExample);

        return tagArticleMapper.selectByExample(tagArticleExample)
                .stream()
                .map(TagArticle::getArticleId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> selectByTagName(String tag) {
        List<Integer> ids = selectArticlesIdByTagName(tag);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        ArticleExample articleExample = new ArticleExample();
        articleExample.createCriteria().andArticleIdIn(ids);
        return selectByExample(articleExample);
    }

    @Override
    public List<Article> selectByTagNameWithBLOBs(String tag) {
        List<Integer> ids = selectArticlesIdByTagName(tag);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        ArticleExample articleExample = new ArticleExample();
        articleExample.createCriteria().andArticleIdIn(ids);
        return selectByExampleWithBLOBs(articleExample);
    }

    @Override
    @Transactional
    public void insertWithTagAndFile(Article article, List<String> tagList, List<WebFile> fileList) {
        insertSelective(article);
        insertTagAndFile(article, tagList, fileList);
    }

    @Override
    public void updateSelectWithTagAndFile(Integer articleId, Article article, List<String> tagNameList, List<WebFile> files) {
        // 先删掉中间表,然后再添加中间表
        try {
            TagArticleExample tagArticleExample = new TagArticleExample();
            tagArticleExample.or().andArticleIdEqualTo(articleId);
            tagArticleMapper.deleteByExample(tagArticleExample);

            article.setArticleId(articleId);
            article.setArticleStatus(CmsConst.REVIEW);
            article.setArticleUpdateTime(new Date());
            insertTagAndFile(article, tagNameList, files);
            updateByPrimaryKeyWithBLOBs(article);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对给定的文章，插入对应的标签和文章
     */
    private void insertTagAndFile(Article article, List<String> tagList, List<WebFile> fileList) {
        TagExample tagExample = new TagExample();
        for (String tag : tagList) {
            tagExample.or().andTagNameEqualTo(tag);
            List<Tag> tags = tagService.selectByExample(tagExample);
            if (tags.size() != 0) {
                //tag already exist
                tagExample.clear();
                TagArticle tagArticle = new TagArticle();
                tagArticle.setArticleId(article.getArticleId());
                tagArticle.setTagId(tags.get(0).getTagId());
                tagArticleMapper.insert(tagArticle);
            } else {
                //tag not exist
                Tag newTag = new Tag();
                newTag.setTagCreateTime(new Date());
                newTag.setTagName(tag);
                tagService.insert(newTag);
                TagArticle tagArticle = new TagArticle(newTag.getTagId(), article.getArticleId());
                tagArticleMapper.insert(tagArticle);
            }
            tagExample.clear();
        }
        if (fileList != null) {
            fileList.stream().filter(e -> !e.getFileKey().isEmpty()).forEach(webFile -> {
                webFile.setFileArticleId(article.getArticleId());
                webFile.setFileSiteId(article.getArticleSiteId());
                webFileService.insert(webFile);
            });
        }
    }

    @Override
    public List<Article> selectArticleByCategoryId(Integer id) {
        ArticleExample articleExample = new ArticleExample();
        articleExample.or().andArticleCategoryIdEqualTo(id);
        return articleMapper.selectByExample(articleExample);
    }

    @Override
    public List<Article> selectArticleByCategoryId(Integer cid, String sort) {
        ArticleExample articleExample = new ArticleExample();
        articleExample.or().andArticleCategoryIdEqualTo(cid);
        articleExample.setOrderByClause(sort);
        return articleMapper.selectByExample(articleExample);
    }

    @Override
    public List<Article> selectArticleByReleaseStatus(Boolean releaseStatus) {
        ArticleExample articleExample = new ArticleExample();
        articleExample.or().andArticleReleaseStatusEqualTo(releaseStatus);
        return articleMapper.selectByExample(articleExample);
    }


    @Override
    public void deleteArticleWithTagAndFile(Integer articleId) {
        TagArticleExample tagArticleExample = new TagArticleExample();
        tagArticleExample.or().andArticleIdEqualTo(articleId);
        List<TagArticle> list = tagArticleMapper.selectByExample(tagArticleExample);   //得到要删除文章的所有标签
        tagArticleMapper.deleteByExample(tagArticleExample);   //删除中间表
        for (TagArticle tagArticle : list) {
            TagExample tagExample = new TagExample();
            tagExample.or().andTagIdEqualTo(tagArticle.getTagId());
            long count = tagService.countByExample(tagExample);
            if (count == 1) {
                tagService.deleteByPrimaryKey(tagArticle.getTagId());
            }
        }
        // 删除文章上传的文件
//        WebFileExample webFileExample = new WebFileExample();
//        webFileExample.or().andFileArticleIdEqualTo(articleId);
//        List<WebFile> files = webFileService.selectByExample(webFileExample);
//        if (files.size() > 0) {
//            fileTool.deleteFiles(files);
//        }
        // 删除文章
        articleMapper.deleteByPrimaryKey(articleId);
    }
}
