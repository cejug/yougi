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
import org.cejug.yougi.event.business.EventVenueBean;
import org.cejug.yougi.event.business.VenueBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.EventVenue;
import org.cejug.yougi.event.entity.Venue;
import org.cejug.yougi.util.StringUtils;

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
public class EventVenueMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private EventBean eventBean;

    @EJB
    private VenueBean venueBean;

    @EJB
    private EventVenueBean eventVenueBean;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    @ManagedProperty(value = "#{param.venueId}")
    private String venueId;

    private List<Venue> venues;
    private List<Event> events;

    private String selectedVenue;
    private String selectedEvent;

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

    public String getSelectedVenue() {
        return selectedVenue;
    }

    public void setSelectedVenue(String selectedVenue) {
        this.selectedVenue = selectedVenue;
    }

    public String getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(String selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public List<Venue> getVenues() {
        if(this.venues == null) {
            if(StringUtils.INSTANCE.isNullOrBlank(this.selectedEvent)) {
                this.venues = venueBean.findVenues();
            } else {
                Event event = new Event(selectedEvent);
                this.venues = eventVenueBean.findVenues(event);
            }
        }
        return this.venues;
    }

    public List<Event> getEvents() {
        if(this.events == null) {
            if(StringUtils.INSTANCE.isNullOrBlank(this.selectedVenue)) {
                this.events = eventBean.findParentEvents();
            } else {
                Venue venue = new Venue(selectedVenue);
                this.events = eventVenueBean.findEvents(venue);
            }
        }
        return this.events;
    }

    @PostConstruct
    public void load() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            this.selectedEvent = eventId;
        }

        if (this.venueId != null && !this.venueId.isEmpty()) {
            this.selectedVenue = venueId;
        }
    }

    public String save() {
        EventVenue eventVenue = new EventVenue();
        eventVenue.setEvent(new Event(this.selectedEvent));
        eventVenue.setVenue(new Venue(this.selectedVenue));

        eventVenueBean.save(eventVenue);

        return getNextPage();
    }

    public String cancel() {
        return getNextPage();
    }

    private String getNextPage() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            return "event?faces-redirect=true&tab=5&id="+ this.selectedEvent;
        } else {
            return "venue?faces-redirect=true&id="+ this.selectedVenue;
        }
    }
}