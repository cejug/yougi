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
package org.cejug.yougi.knowledge.business;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.knowledge.entity.WebSource;
import org.cejug.yougi.entity.EntitySupport;

/**
 * Business logic dealing with articles from a web source.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class ArticleBean {

    @PersistenceContext
    private EntityManager em;

    public Article findArticle(String id) {
        return em.find(Article.class, id);
    }

    public List<Article> findPublishedArticles() {
            return em.createQuery("select a from Article a where a.published = :published order by a.publication desc")
                     .setParameter("published", Boolean.TRUE)
                     .getResultList();
    }

    public List<Article> findPublishedArticles(WebSource webSource) {
            return em.createQuery("select a from Article a where a.webSource = :webSource order by a.title asc")
                                 .setParameter("webSource", webSource)
                                 .getResultList();
    }

    public void publish(Article article) {
        article.setPublished(Boolean.TRUE);
        save(article);
    }

    public void unpublish(Article article) {
        remove(article.getId());
    }

    public void save(Article article) {
        if(EntitySupport.INSTANCE.isIdNotValid(article)) {
            article.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(article);
        }
        else {
            em.merge(article);
        }
    }

    public void remove(String id) {
        Article article = em.find(Article.class, id);
        if(article != null) {
            em.remove(article);
        }
    }
}