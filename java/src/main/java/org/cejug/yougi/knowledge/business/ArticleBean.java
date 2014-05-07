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

import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.knowledge.entity.WebSource;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Business logic dealing with articles from a web source.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class ArticleBean extends AbstractBean<Article> {

    @PersistenceContext
    private EntityManager em;

    public ArticleBean() {
        super(Article.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Article> findPublishedArticles() {
            return em.createQuery("select a from Article a where a.published = :published order by a.publication desc", Article.class)
                     .setParameter("published", Boolean.TRUE)
                     .getResultList();
    }

    public List<Article> findPublishedArticles(WebSource webSource) {
            return em.createQuery("select a from Article a where a.webSource = :webSource order by a.publication desc", Article.class)
                                 .setParameter("webSource", webSource)
                                 .getResultList();
    }

    public List<Article> findPublishedArticles(Article except) {
        return em.createQuery("select a from Article a where a.webSource.id = :webSource and a.id <> :except order by a.publication desc", Article.class)
                .setParameter("webSource", except.getWebSource() != null ? except.getWebSource().getId() : null)
                .setParameter("except", except.getId())
                .getResultList();
    }

    public List<Article> findOtherPublishedArticles(WebSource except) {
        return em.createQuery("select a from Article a where a.webSource <> :except order by a.publication desc", Article.class)
                .setParameter("except", except)
                .getResultList();
    }

    public void publish(Article article) {
        article.setPublished(Boolean.TRUE);
        save(article);
    }

    public void unpublish(Article article) {
        remove(article.getId());
    }
}
