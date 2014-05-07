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

import org.cejug.yougi.event.business.EventBean;
import org.cejug.yougi.event.business.SessionBean;
import org.cejug.yougi.event.business.TrackBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.SessionEvent;
import org.cejug.yougi.event.entity.Speaker;
import org.cejug.yougi.event.entity.Track;

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
public class TrackMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private TrackBean trackBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private EventBean eventBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    private Track track;

    private List<Event> events;
    private List<SessionEvent> sessions;
    private List<Speaker> speakers;

    private String selectedEvent;

    public TrackMBean() {
        this.track = new Track();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(String selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public List<Event> getEvents() {
        if (this.events == null) {
            this.events = eventBean.findParentEvents();
        }
        return this.events;
    }

    public List<SessionEvent> getSessions() {
        if (this.sessions == null) {
            this.sessions = sessionBean.findSessionsByTrack(this.track);
        }
        return this.sessions;
    }

    public List<Speaker> getSpeakers() {
        if(this.speakers == null) {
            this.speakers = sessionBean.findSessionSpeakersByTrack(this.track);
        }
        return this.speakers;
    }

    @PostConstruct
    public void load() {
        if (this.id != null && !this.id.isEmpty()) {
            this.track = trackBean.find(id);
            this.selectedEvent = this.track.getEvent().getId();
        }

        if (this.eventId != null && !this.eventId.isEmpty()) {
            this.selectedEvent = eventId;
        }
    }

    public String save() {
        this.track.setEvent(new Event(this.selectedEvent));

        trackBean.save(this.track);

        return "event?faces-redirect=true&tab=1&id="+ this.selectedEvent;
    }
}