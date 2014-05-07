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
package org.cejug.yougi.event.business;

import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.business.AccessGroupBean;
import org.cejug.yougi.business.AuthenticationBean;
import org.cejug.yougi.business.UserGroupBean;
import org.cejug.yougi.entity.AccessGroup;
import org.cejug.yougi.entity.Authentication;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.entity.UserGroup;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.SessionEvent;
import org.cejug.yougi.event.entity.Speaker;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class SpeakerBean extends AbstractBean<Speaker> {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private AccessGroupBean accessGroupBean;

    @EJB
    private AuthenticationBean authenticationBean;

    @EJB
    private UserGroupBean userGroupBean;

    public SpeakerBean() {
        super(Speaker.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Returns the list of users who are not speakers yet. If a user is passed
     * by parameter he/she is included in the list even if he/she is already a
     * speaker.
     */
    public List<UserAccount> findSpeakerCandidates(UserAccount except) {
        List<UserAccount> candidates;
        if(except != null) {
            candidates = em.createQuery("select ua from UserAccount ua where ua not in (select s.userAccount from Speaker s where s.userAccount <> :except) order by ua.firstName, ua.lastName asc", UserAccount.class)
                           .setParameter("except", except)
                           .getResultList();
        } else {
            candidates = em.createQuery("select ua from UserAccount ua where ua not in (select s.userAccount from Speaker s) order by ua.firstName, ua.lastName asc", UserAccount.class)
                           .getResultList();
        }
        return candidates;
    }

    /**
     * Returns the entire list of speakers from all registered events.
     */
    public List<Speaker> findSpeakers() {
        return em.createQuery("select distinct s from Speaker s order by s.userAccount.firstName asc", Speaker.class).getResultList();
    }

    /**
     * Returns the list of speakers from a specific event only.
     */
    public List<Speaker> findSpeakers(Event event) {
        return em.createQuery("select distinct ss.speaker from SpeakerSession ss where ss.sessionEvent.event.id = :event order by ss.speaker.userAccount.firstName asc", Speaker.class)
                                   .setParameter("event", event.getId())
                                   .getResultList();
    }

    /**
     * Returns the list of speakers from a specific session only.
     */
    public List<Speaker> findSpeakers(SessionEvent session) {
        return em.createQuery("select ss.speaker from SpeakerSession ss where ss.sessionEvent = :session order by ss.speaker.userAccount.firstName asc", Speaker.class)
                 .setParameter("session", session)
                 .getResultList();
    }

    @Override
    public Speaker save(Speaker speaker) {
        Speaker theSpeaker = super.save(speaker);

        Authentication authentication = authenticationBean.findByUserAccount(theSpeaker.getUserAccount());
        AccessGroup accessGroup = accessGroupBean.findAccessGroupByName("speakers");
        UserGroup userGroup = new UserGroup(accessGroup, authentication);
        userGroupBean.add(userGroup);

        return theSpeaker;
    }
}