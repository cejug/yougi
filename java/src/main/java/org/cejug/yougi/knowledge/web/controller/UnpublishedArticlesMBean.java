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

import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.knowledge.entity.WebSource;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@SessionScoped
public class UnpublishedArticlesMBean {

    private WebSource webSource;
    private List<Article> unpublishedArticles;

    public List<Article> getUnpublishedArticles() {
        return unpublishedArticles;
    }

    public void setUnpublishedArticles(List<Article> unpublishedArticles) {
        this.unpublishedArticles = unpublishedArticles;
    }

    public WebSource getWebSource() {
        return webSource;
    }

    public void setWebSource(WebSource webSource) {
        this.unpublishedArticles = null;
        this.webSource = webSource;
    }

    public Article getArticle(String permanentLink) {
        if(unpublishedArticles == null || permanentLink == null) {
            return null;
        }

        Article article = new Article(null, permanentLink);
        for(Article art: unpublishedArticles) {
            if(art.equals(article)) {
                article = art;
                break;
            }
        }

        return article;
    }

    public void removeArticle(Article article) {
        if(article != null && this.unpublishedArticles != null) {
            this.unpublishedArticles.remove(article);
        }
    }

    public void addArticle(Article article) {
        article.setId(null);
        article.setPublished(Boolean.FALSE);
        if(this.unpublishedArticles == null) {
            this.unpublishedArticles = new ArrayList<>();
        }
        this.unpublishedArticles.add(article);
    }
}