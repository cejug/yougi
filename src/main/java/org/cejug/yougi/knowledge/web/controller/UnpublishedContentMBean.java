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

import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.cejug.yougi.knowledge.business.WebSourceBean;
import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.knowledge.entity.WebSource;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@SessionScoped
public class UnpublishedContentMBean {

    private WebSource webSource;

    @EJB
    private WebSourceBean webSourceBean;

    public WebSource getWebSource() {
        return webSource;
    }

    public void setWebSource(WebSource webSource) {
        if(this.webSource == null || !this.webSource.equals(webSource)) {
            reset();
        }
        this.webSource = webSource;
    }

    public void reset() {
        this.webSource  = null;
    }

    public List<Article> getArticles() {
        return this.webSource.getArticles();
    }

    public Article getArticle(String permanentLink) {
        List<Article> articles = this.webSource.getArticles();
        if(articles == null || permanentLink == null) {
            return null;
        }

        Article article = new Article(null, permanentLink);
        for(Article art: articles) {
            if(art.equals(article)) {
                article = art;
                break;
            }
        }

        return article;
    }

    public void loadWebSource() {
        this.webSource = webSourceBean.loadWebSource(this.webSource);
    }

    public void addArticle(Article article) {
        article.setId(null);
        article.setPublished(Boolean.FALSE);
        this.webSource.addArticle(article);
    }

    public void removeArticle(Article article) {
        this.webSource.removeArticle(article);
    }
}