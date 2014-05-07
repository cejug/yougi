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
package org.cejug.yougi.web.controller;

import org.cejug.yougi.entity.PublicContent;
import org.cejug.yougi.event.business.EventBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.knowledge.business.ArticleBean;
import org.cejug.yougi.knowledge.entity.Article;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@ViewScoped
public class FrontPageMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private EventBean eventBean;

    @EJB
    private ArticleBean articleBean;

    private final List<PublicContent> publicContents;
    private PublicContent mainPublicContent;

    public FrontPageMBean() {
        publicContents = new ArrayList<>();
    }

    public List<PublicContent> getPublicContents() {
        return publicContents;
    }

    public PublicContent getMainPublicContent() {
        return mainPublicContent;
    }

    @PostConstruct
    public void init() {
        List<Event> comingEvents = eventBean.findUpCommingEvents();
        List<Article> publishedArticles = articleBean.findPublishedArticles();
        publicContents.addAll(comingEvents);
        publicContents.addAll(publishedArticles);

        if(!publicContents.isEmpty()) {
            mainPublicContent = this.publicContents.remove(0);
        }
    }
}