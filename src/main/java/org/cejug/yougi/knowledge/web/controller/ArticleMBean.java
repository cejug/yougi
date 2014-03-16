/* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.cejug.yougi.knowledge.web.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.cejug.yougi.knowledge.business.ArticleBean;
import org.cejug.yougi.knowledge.business.WebSourceBean;
import org.cejug.yougi.knowledge.entity.Article;

import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class ArticleMBean {

    @EJB
    private ArticleBean articleBean;

    @EJB
    private WebSourceBean webSourceBean;

    private Article article;

    private List<Article> articlesFromSameSource;
    private List<Article> otherPublishedArticles;

    @ManagedProperty(value="#{param.id}")
    private String id;

    @ManagedProperty(value="#{param.pl}")
    private String permanentLink;

    @ManagedProperty(value="#{unpublishedArticlesMBean}")
    private UnpublishedArticlesMBean unpublishedArticlesMBean;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPermanentLink(String permanentLink) {
        this.permanentLink = permanentLink;
    }

    public void setUnpublishedArticlesMBean(UnpublishedArticlesMBean unpublishedArticlesMBean) {
        this.unpublishedArticlesMBean = unpublishedArticlesMBean;
    }

    public Article getArticle() {
        return this.article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public List<Article> getArticlesFromSameSource() {
        if(this.articlesFromSameSource == null) {
            this.articlesFromSameSource = articleBean.findPublishedArticles(this.article);
        }
        return articlesFromSameSource;
    }

    public List<Article> getOtherPublishedArticles() {
        if(this.otherPublishedArticles == null) {
            otherPublishedArticles = articleBean.findOtherPublishedArticles(this.article.getWebSource());
        }
        return otherPublishedArticles;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.article = articleBean.find(id);
        }
        else if(permanentLink != null && !permanentLink.isEmpty()) {
            this.article = this.unpublishedArticlesMBean.getArticle(this.permanentLink);
        }
        else {
            this.article = new Article();
        }
    }

    public String publish() {
        this.unpublishedArticlesMBean.removeArticle(this.article);
        articleBean.publish(this.article);
        return "web_source?faces-redirect=true&id="+ this.article.getWebSource().getId();
    }

    public String unpublish() {
        articleBean.unpublish(this.article);
        this.unpublishedArticlesMBean.addArticle(this.article);
        return "web_source?faces-redirect=true&id="+ this.article.getWebSource().getId();
    }

    public String save() {
        articleBean.save(this.article);
        return "web_source?faces-redirect=true&id="+ this.article.getWebSource().getId();
    }

    public void reloadFromSource() {
        this.article = articleBean.find(this.article.getId());
        this.article = this.webSourceBean.loadOriginalArticle(this.article);
        this.article.setPublished(true);
    }
}