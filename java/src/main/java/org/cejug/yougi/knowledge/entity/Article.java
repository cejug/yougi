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
package org.cejug.yougi.knowledge.entity;

import org.cejug.yougi.entity.Identified;
import org.cejug.yougi.entity.PublicContent;
import org.cejug.yougi.util.StringUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity class representing an extracted article from a web source, making it
 * permanently available for publication.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "article")
@XmlRootElement
public class Article implements Serializable, Identified, PublicContent {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String title;
    private String author;

    @ManyToOne
    @JoinColumn(name="web_source")
    private WebSource webSource;

    private String content;
    private String summary;

    @Column(name="perm_link")
    private String permanentLink;

    private String topics;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date publication;

    private Boolean published;

    public Article() {}

    public Article(String id) {
        this.id = id;
    }

    public Article(String id, String permanentLink) {
        this.id = id;
        this.permanentLink = permanentLink;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public WebSource getWebSource() {
        return webSource;
    }

    public void setWebSource(WebSource webSource) {
        this.webSource = webSource;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String getUrl() {
        return "/knowledge/article";
    }

    /**
     * @return Returns the content if it exists or the summary if the content
     * doesn't exist.
     */
    public String getText() {
        if(StringUtils.INSTANCE.isNullOrBlank(this.content)) {
            return this.summary;
        } else {
            return this.content;
        }
    }

    public String getPermanentLink() {
        return permanentLink;
    }

    public void setPermanentLink(String permanentLink) {
        this.permanentLink = permanentLink;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public Date getPublication() {
        return publication;
    }

    public void setPublication(Date publication) {
        this.publication = publication;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Article)) {
            return false;
        }
        boolean equals = false;
        Article other = (Article) object;

        if(this.id != null && other.id != null) {
            equals = this.id.equals(other.id);
        } else if(this.permanentLink != null) {
            equals = this.permanentLink.equals(other.permanentLink);
        }

        return equals;
    }

    @Override
    public String toString() {
        return this.title;
    }
}