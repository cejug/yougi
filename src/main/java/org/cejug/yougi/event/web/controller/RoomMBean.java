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
import org.cejug.yougi.event.business.RoomBean;
import org.cejug.yougi.event.business.SessionBean;
import org.cejug.yougi.event.business.VenueBean;
import org.cejug.yougi.event.entity.*;
import org.cejug.yougi.event.entity.SessionEvent;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class RoomMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private RoomBean roomBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private EventBean eventBean;

    @EJB
    private VenueBean venueBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    @ManagedProperty(value = "#{param.venueId}")
    private String venueId;

    private Event event;
    private Room room;

    private List<SessionEvent> sessions;
    private List<Speaker> speakers;
    private List<Venue> venues;

    private String selectedVenue;

    public RoomMBean() {
        this.room = new Room();
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

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getSelectedVenue() {
        return selectedVenue;
    }

    public void setSelectedVenue(String selectedVenue) {
        this.selectedVenue = selectedVenue;
    }

    public List<SessionEvent> getSessions() {
        if (this.sessions == null) {
            this.sessions = sessionBean.findSessionsByRoom(this.event, this.room);
        }
        return this.sessions;
    }

    public List<Speaker> getSpeakers() {
        if(this.speakers == null) {
            this.speakers = sessionBean.findSessionSpeakersByRoom(this.event, this.room);
        }
        return this.speakers;
    }

    public List<Venue> getVenues() {
        if(this.venues == null) {
            this.venues = venueBean.findVenues();
        }
        return this.venues;
    }

    @PostConstruct
    public void load() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            this.event = eventBean.find(eventId);
        }

        if (this.venueId != null && !this.venueId.isEmpty()) {
            this.selectedVenue = venueId;
        }

        if (this.id != null && !this.id.isEmpty()) {
            this.room = roomBean.find(id);
            this.selectedVenue = this.room.getVenue().getId();
        }
    }

    public String save() {
        this.room.setVenue(new Venue(this.selectedVenue));

        roomBean.save(this.room);

        return "venue?faces-redirect=true&tab=1&id="+ this.selectedVenue;
    }
}