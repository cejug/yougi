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

import org.cejug.yougi.event.business.EventVenueBean;
import org.cejug.yougi.event.business.RoomBean;
import org.cejug.yougi.event.business.VenueBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.Room;
import org.cejug.yougi.event.entity.Venue;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * This class is used to manage the selection of the venue and room of an event
 * session. The selected venue updates the list of rooms related with that venue.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@ViewScoped
public class VenueSelectionMBean {

    @EJB
    private VenueBean venueBean;

    @EJB
    private EventVenueBean eventVenueBean;

    @EJB
    private RoomBean roomBean;

    private List<Venue> venues;

    private List<Room> rooms;

    private String selectedVenue;

    private String selectedRoom;

    private String selectedEvent;

    public void setSelectedEvent(String selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public List<Venue> getVenues() {
        Event event = new Event(this.selectedEvent);
        this.venues = eventVenueBean.findEventVenues(event);
        return this.venues;
    }

    public List<Room> getRooms() {
        if (this.selectedVenue != null) {
            Venue venue = new Venue(selectedVenue);
            this.rooms = roomBean.findRooms(venue);
        } else {
            this.rooms = null;
        }
        return this.rooms;
    }

    public Venue getVenue() {
        if (this.selectedVenue != null) {
            return venueBean.find(selectedVenue);
        } else {
            return null;
        }
    }

    public Room getRoom() {
        if (this.selectedRoom != null) {
            return roomBean.find(selectedRoom);
        } else {
            return null;
        }
    }

    public String getSelectedVenue() {
        return selectedVenue;
    }

    public void setSelectedVenue(String selectedVenue) {
        this.selectedVenue = selectedVenue;
        this.selectedRoom = null;
    }

    public String getSelectedRoom() {
        return selectedRoom;
    }

    public void setSelectedRoom(String selectedRoom) {
        this.selectedRoom = selectedRoom;
    }
}