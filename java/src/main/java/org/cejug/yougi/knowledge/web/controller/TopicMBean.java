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

import org.cejug.yougi.knowledge.business.TopicBean;
import org.cejug.yougi.knowledge.entity.Topic;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class TopicMBean {

    @EJB
    private TopicBean topicBean;

    private Topic topic;

    private List<Topic> topics;

    @ManagedProperty(value="#{param.topic}")
    private String topicName;

    private Boolean topicExistent = Boolean.FALSE;

    public TopicMBean() {
        this.topic = new Topic();
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public List<Topic> getTopics() {
        if(this.topics == null) {
            this.topics = topicBean.findTopics();
        }
        return this.topics;
    }

    public Boolean getExistent() {
        return this.topicExistent;
    }

    @PostConstruct
    public void load() {
        if(this.topicName != null && !this.topicName.isEmpty()) {
            this.topic = topicBean.findTopic(topicName);
            if(this.topic != null) {
                this.topicExistent = true;
            }
        }
    }

    public String save() {
        topicBean.save(this.topic);
        return "topics?faces-redirect=true";
    }

    public String remove() {
        topicBean.remove(this.topic.getName());
        return "topics?faces-redirect=true";
    }
}