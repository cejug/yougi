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

import org.cejug.yougi.event.business.*;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.Room;
import org.cejug.yougi.event.entity.SessionEvent;
import org.cejug.yougi.event.entity.Venue;
import org.cejug.yougi.web.controller.LocationMBean;

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
public class VenueMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private VenueBean venueBean;

    @EJB
    private RoomBean roomBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private EventBean eventBean;

    @EJB
    private EventVenueBean eventVenueBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    @ManagedProperty(value="#{locationMBean}")
    private LocationMBean locationMBean;

    private Event event;

    private Venue venue;

    private List<Room> rooms;
    private List<Event> events;
    private List<Venue> venues;

    public VenueMBean() {
        this.venue = new Venue();
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

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    public List<SessionEvent> getSessions(Room room) {
        return sessionBean.findSessionsByRoom(this.event, room);
    }

    public List<Room> getRooms() {
        if(this.rooms == null) {
            this.rooms = roomBean.findRooms(this.venue);
        }
        return this.rooms;
    }

    public List<Venue> getVenues() {
        if(this.venues == null) {
            this.venues = venueBean.findVenues();
        }
        return this.venues;
    }

    public List<Event> getEvents() {
        if(this.events == null) {
            this.events = eventVenueBean.findEventsVenue(venue);
        }
        return this.events;
    }

    @PostConstruct
    public void load() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            this.event = eventBean.find(eventId);
        }

        if (this.id != null && !this.id.isEmpty()) {
            this.venue = venueBean.find(id);

            locationMBean.initialize();

            if (this.venue.getCountry() != null) {
                locationMBean.setSelectedCountry(this.venue.getCountry().getAcronym());
            }

            if (this.venue.getProvince() != null) {
                locationMBean.setSelectedProvince(this.venue.getProvince().getId());
            }

            if (this.venue.getCity() != null) {
                locationMBean.setSelectedCity(this.venue.getCity().getId());
            }
        }
    }

    public String save() {
        this.venue.setCountry(this.locationMBean.getCountry());
        this.venue.setProvince(this.locationMBean.getProvince());
        this.venue.setCity(this.locationMBean.getCity());

        venueBean.save(this.venue);

        return "venues?faces-redirect=true";
    }
}