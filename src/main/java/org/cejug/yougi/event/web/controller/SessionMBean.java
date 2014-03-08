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

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.yougi.event.business.EventBean;
import org.cejug.yougi.event.business.SessionBean;
import org.cejug.yougi.event.business.TrackBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.Session;
import org.cejug.yougi.event.entity.Track;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class SessionMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private EventBean eventBean;

    @EJB
    private TrackBean trackBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    @ManagedProperty(value = "#{venueSelectionMBean}")
    private VenueSelectionMBean venueSelectionMBean;

    private Session session;

    private List<Event> events;
    private List<Session> sessions;
    private List<Session> relatedSessions;
    private List<Session> sessionsInTheSameRoom;
    private List<Session> sessionsInParallel;
    private List<Track> tracks;

    private String selectedEvent;
    private String selectedTrack;

    public SessionMBean() {
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

    public void setVenueSelectionMBean(VenueSelectionMBean venueSelectionMBean) {
        this.venueSelectionMBean = venueSelectionMBean;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<Session> getSessions() {
        if (this.sessions == null) {
            Event event = new Event(selectedEvent);
            this.sessions = sessionBean.findSessionsWithSpeakers(event);
        }
        return this.sessions;
    }

    public List<Session> getRelatedSessions() {
        if (this.relatedSessions == null) {
            this.relatedSessions = sessionBean.findRelatedSessions(this.session);
        }
        return this.relatedSessions;
    }

    public List<Session> getSessionsInTheSameRoom() {
        if (this.sessionsInTheSameRoom == null) {
            this.sessionsInTheSameRoom = sessionBean.findSessionsInTheSameRoom(this.session);
        }
        return this.sessionsInTheSameRoom;
    }

    public List<Session> getSessionsInParallel() {
        if(this.sessionsInParallel == null) {
            this.sessionsInParallel = sessionBean.findSessionsInParallel(this.session);
        }
        return sessionsInParallel;
    }

    public List<Track> getTracks() {
        if(this.tracks == null) {
            Event event = new Event(selectedEvent);
            this.tracks = trackBean.findTracks(event);
        }
        return this.tracks;
    }

    public String getSelectedEvent() {
        return this.selectedEvent;
    }

    public void setSelectedEvent(String selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public String getSelectedTrack() {
        return this.selectedTrack;
    }

    public void setSelectedTrack(String selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    public List<Event> getEvents() {
        if (this.events == null) {
            this.events = eventBean.findParentEvents();
        }
        return this.events;
    }

    public Session getPreviousSession() {
        return sessionBean.findPreviousSession(this.session);
    }

    public Session getNextSession() {
        return sessionBean.findNextSession(this.session);
    }

    @PostConstruct
    public void load() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            Event event = eventBean.find(eventId);
            this.session = new Session();
            this.session.setEvent(event);
            this.selectedEvent = event.getId();
            this.venueSelectionMBean.setSelectedEvent(this.selectedEvent);
        }

        if (this.id != null && !this.id.isEmpty()) {
            this.session = sessionBean.findSession(id);
            Event event = this.session.getEvent();
            this.selectedEvent = event.getId();
            if(this.session.getTrack() != null) {
                this.selectedTrack = this.session.getTrack().getId();
            }
            this.venueSelectionMBean.setSelectedEvent(this.selectedEvent);
            if(this.session.getRoom() != null) {
                this.venueSelectionMBean.setSelectedVenue(this.session.getRoom().getVenue().getId());
                this.venueSelectionMBean.setSelectedRoom(this.session.getRoom().getId());
            }
        }

        if(this.session == null) {
            this.session = new Session();
        }
    }

    public String save() {
        Event evt = eventBean.find(selectedEvent);
        this.session.setEvent(evt);

        this.session.setRoom(this.venueSelectionMBean.getRoom());

        if(this.selectedTrack != null && !this.selectedTrack.isEmpty()) {
            Track track = new Track(this.selectedTrack);
            this.session.setTrack(track);
        }

        sessionBean.save(this.session);
        return "event?faces-redirect=true&tab=2&id=" + this.eventId;
    }

    public String remove() {
        sessionBean.remove(this.session.getId());
        return "event?faces-redirect=true&tab=2&id=" + this.eventId;
    }
}