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
package org.cejug.yougi.event.web.controller;

import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.event.business.SessionBean;
import org.cejug.yougi.event.business.SpeakerBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.SessionEvent;
import org.cejug.yougi.event.entity.Speaker;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class SpeakerMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private SpeakerBean speakerBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private UserAccountBean userAccountBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    private Event event;

    private Speaker speaker;

    private List<Event> events;

    private List<SessionEvent> sessions;

    private List<UserAccount> userAccounts;

    private List<Speaker> speakers;

    private String selectedUserAccount;

    public SpeakerMBean() {
        this.speaker = new Speaker();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    public List<Speaker> getSpeakers() {
        if (this.speakers == null) {
            if(this.event != null) {
                this.speakers = speakerBean.findSpeakers(this.event);
            } else {
                this.speakers = speakerBean.findSpeakers();
            }
        }
        return this.speakers;
    }

    /**
     * @return the selectedUserAccount
     */
    public String getSelectedUserAccount() {
        return selectedUserAccount;
    }

    /**
     * @param selectedUserAccount the selectedUserAccount to set
     */
    public void setSelectedUserAccount(String selectedUserAccount) {
        this.selectedUserAccount = selectedUserAccount;
    }

    public List<Event> getEvents() {
        if(this.events == null && this.speaker != null) {
            this.events = sessionBean.findEventsSpeaker(this.speaker);
        }
        return this.events;
    }

    /**
     * @return the sessions
     */
    public List<SessionEvent> getSessions() {
        if(this.sessions == null) {
            if(this.event != null) {
                this.sessions = sessionBean.findSessions(this.event);
            } else if (this.speaker != null) {
                this.sessions = sessionBean.findSessionsSpeaker(this.speaker);
            }
        }
        return sessions;
    }

    /**
     * @return the userAccounts
     */
    public List<UserAccount> getUserAccounts() {
        if(this.userAccounts == null) {
            this.userAccounts = speakerBean.findSpeakerCandidates(this.speaker.getUserAccount());
        }
        return userAccounts;
    }

    /**
     * @param userAccounts the userAccounts to set
     */
    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }

    /**
     * @param sessions the sessions to set
     */
    public void setSessions(List<SessionEvent> sessions) {
        this.sessions = sessions;
    }

    @PostConstruct
    public void load() {
        if (this.id != null && !this.id.isEmpty()) {
            this.speaker = speakerBean.find(id);
            this.selectedUserAccount = this.speaker.getUserAccount().getId();
        }
    }

    public String save() {
        UserAccount userAccount = userAccountBean.find(selectedUserAccount);
        this.speaker.setUserAccount(userAccount);

        speakerBean.save(this.speaker);
        return getNextPage();
    }

    public String remove() {
        speakerBean.remove(this.speaker.getId());
        return getNextPage();
    }

    private String getNextPage() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            return "event?faces-redirect=true&tab=3&id="+ this.eventId;
        } else {
            return "speakers?faces-redirect=true&eventId="+ this.eventId;
        }
    }
}