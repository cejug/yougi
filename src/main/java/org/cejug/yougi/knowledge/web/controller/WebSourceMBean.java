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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.knowledge.business.ArticleBean;
import org.cejug.yougi.knowledge.business.WebSourceBean;
import org.cejug.yougi.knowledge.entity.Article;
import org.cejug.yougi.knowledge.entity.WebSource;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class WebSourceMBean {

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private WebSourceBean webSourceBean;

    @EJB
    private ArticleBean articleBean;

    private UserAccount provider;
    private WebSource webSource;

    private List<UserAccount> usersWithWebsite;
    private List<Article> publishedArticles;

    @ManagedProperty(value="#{param.user}")
    private String userId;

    @ManagedProperty(value="#{unpublishedContentMBean}")
    private UnpublishedContentMBean unpublishedContentMBean;

    public UserAccount getProvider() {
        return this.provider;
    }

    public WebSource getWebSource() {
        return this.webSource;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<UserAccount> getUsersWithWebsite() {
        if(this.usersWithWebsite == null) {
            this.usersWithWebsite = userAccountBean.findUserAccountsWithWebsite();
        }
        return this.usersWithWebsite;
    }

    public UnpublishedContentMBean getUnpublishedContentMBean() {
        return unpublishedContentMBean;
    }

    public void setUnpublishedContentMBean(UnpublishedContentMBean unpublishedContentMBean) {
        this.unpublishedContentMBean = unpublishedContentMBean;
    }

    public List<Article> getUnpublishedArticles() {
        return this.unpublishedContentMBean.getArticles();
    }

    public List<Article> getPublishedArticles() {
        if(publishedArticles == null) {
            this.publishedArticles = articleBean.findPublishedArticles(this.webSource);
        }
        return this.publishedArticles;
    }

    @PostConstruct
    public void load() {
        if(this.userId != null && !this.userId.isEmpty()) {
            this.provider = userAccountBean.findUserAccount(this.userId);
            this.webSource = webSourceBean.findWebSourceByProvider(this.provider);
            if(this.webSource == null) {
                this.webSource = new WebSource();
                this.webSource.setProvider(this.provider);
            }
            this.webSource = webSourceBean.loadWebSource(this.webSource);
            this.unpublishedContentMBean.setWebSource(this.webSource);
        }
    }

    public String reference() {
        webSourceBean.save(this.webSource);
        return "website";
    }

    public String undoReference() {
        webSourceBean.remove(this.webSource.getId());
        this.webSource.setId(null);
        this.unpublishedContentMBean.reset();
        return "website";
    }

    public String refreshUnpublishedContent() {
        this.unpublishedContentMBean.loadWebSource();
        return "website";
    }
}