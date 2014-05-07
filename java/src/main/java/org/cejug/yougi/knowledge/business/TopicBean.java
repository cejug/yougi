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

import org.cejug.yougi.exception.BusinessLogicException;
import org.cejug.yougi.knowledge.entity.Topic;
import org.cejug.yougi.util.StringUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class TopicBean {

    @PersistenceContext
    private EntityManager em;

    public Topic findTopic(String name) {
        return em.find(Topic.class, name);
    }

    public List<Topic> findTopics() {
        return em.createQuery("select t from Topic t order by t.name asc", Topic.class).getResultList();
    }

    public List<Topic> findTopics(String query) {
        return em.createQuery("select t from Topic t where t.name like '"+ query +"%' order by t.name asc", Topic.class).getResultList();
    }

    public void save(Topic topic) {
        Topic existing = em.find(Topic.class, topic.getName());
        if(existing == null) {
            em.persist(topic);
        } else {
            em.merge(topic);
        }
    }

    /**
     * Receive a list of topics separated by comma and verify if they already
     * exist. Topics are created with default values if they don't exist yet.
     */
    public void consolidateTopics(String topics) throws BusinessLogicException {
        if(StringUtils.INSTANCE.isNullOrBlank(topics)) {
            return;
        }

        StringTokenizer st = new StringTokenizer(topics, ",");
        String topicName;
        Topic topic;
        while(st.hasMoreTokens()) {
            topicName = st.nextToken().trim();
            topic = findTopic(topicName.toUpperCase());
            if(topic == null) {
                topic = new Topic();
                topic.setName(topicName);
                topic.setLabel(topicName);
                topic.setDescription(topicName);
                topic.setValid(Boolean.TRUE);
                em.persist(topic);
            }
        }
    }

    public void remove(String name) {
        if(StringUtils.INSTANCE.isNullOrBlank(name)) {
            return;
        }

        Topic topic = em.find(Topic.class, name);
        if(topic != null) {
            em.remove(topic);
        }
    }
}