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
import org.cejug.yougi.event.business.*;
import org.cejug.yougi.event.entity.*;
import org.cejug.yougi.qualifier.UserName;
import org.cejug.yougi.util.WebTextUtils;
import org.cejug.yougi.web.controller.UserProfileMBean;
import org.primefaces.model.chart.PieChartModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class EventMBean {

    @EJB
    private EventBean eventBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private SpeakerBean speakerBean;

    @EJB
    private TrackBean trackBean;

    @EJB
    private AttendeeBean attendeeBean;

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private EventVenueBean eventVenueBean;

    @EJB
    private SponsorshipEventBean sponsorshipEventBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @Inject
    private UserProfileMBean userProfileMBean;

    @Inject
    @UserName
    private String username;

    private Event event;
    private Attendee attendee;
    private String selectedParent;

    private List<Event> events;
    private List<Event> subEvents;
    private List<Event> parentEvents;
    private List<Venue> venues;
    private List<SessionEvent> sessions;
    private List<Track> tracks;
    private List<Speaker> speakers;
    private List<Attendee> attendees;
    private List<SponsorshipEvent> sponsors;

    private Long numberPeopleAttending;

    private Long numberPeopleAttended;

    public EventMBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getSelectedParent() {
        return this.selectedParent;
    }

    public void setSelectedParent(String selectedParent) {
        this.selectedParent = selectedParent;
    }

    /**
     * @return true if the event ocurred on the day before today.
     */
    public Boolean getHappened() {
        TimeZone tz = TimeZone.getTimeZone(userProfileMBean.getTimeZone());
        Calendar today = Calendar.getInstance(tz);

        if(this.event.getStartDate().before(today.getTime())) {
            return true;
        }

        return false;
    }

    /**
     * @return true if the member has the intention to attend the event. It does
     * not mean that s(he) actually attended it.
     */
    public Boolean getIsAttending() {
        return this.attendee != null && this.attendee.getId() != null;
    }

    /**
     * @return true if the member actually attended the event.
     */
    public Boolean getAttended() {
        if(attendee != null) {
            return attendee.getAttended();
        }
        return Boolean.FALSE;
    }

    public List<Event> getEvents() {
        if (events == null) {
            events = eventBean.findParentEvents();
        }
        return events;
    }

    public List<Event> getParentEvents() {
        if (parentEvents == null) {
            parentEvents = eventBean.findParentEvents();
        }
        return parentEvents;
    }

    public List<Venue> getVenues() {
        if(venues == null) {
            venues = eventVenueBean.findEventVenues(event);
        }
        return venues;
    }

    public List<Event> getSubEvents() {
        if (subEvents == null) {
            subEvents = eventBean.findEvents(this.event);
        }
        return subEvents;
    }

    public List<SessionEvent> getSessions() {
        if (sessions == null) {
            sessions = sessionBean.findSessionsWithSpeakers(this.event);
        }
        return sessions;
    }

    public List<SessionEvent> getSessions(Event event) {
        return sessionBean.findSessionsWithSpeakers(event);
    }

    public List<Track> getTracks() {
        if (tracks == null) {
            tracks = trackBean.findTracks(this.event);
        }
        return tracks;
    }

    public List<Track> getTracks(Event event) {
        return trackBean.findTracks(event);
    }

    public List<Speaker> getSpeakers() {
        if (speakers == null) {
            speakers = speakerBean.findSpeakers(this.event);
        }
        return speakers;
    }

    public List<Speaker> getSpeakers(Event event) {
        return speakerBean.findSpeakers(event);
    }

    public List<Attendee> getAttendees() {
        if (attendees == null) {
            attendees = attendeeBean.findAllAttendees(this.event);
        }
        return attendees;
    }

    public List<SponsorshipEvent> getSponsors() {
        if(sponsors == null) {
            sponsors = sponsorshipEventBean.findSponsorshipsEvent(this.event);
        }
        return sponsors;
    }

    public Long getNumberPeopleAttending() {
        return numberPeopleAttending;
    }

    public void setNumberPeopleAttending(Long numberPeopleAttending) {
        this.numberPeopleAttending = numberPeopleAttending;
    }

    public void setNumberPeopleAttended(Long numberPeopleAttended) {
        this.numberPeopleAttended = numberPeopleAttended;
    }

    public Long getNumberPeopleAttended() {
        return numberPeopleAttended;
    }

    public PieChartModel getAttendanceRateChartModel() {
        PieChartModel pieChartModel = new PieChartModel();
        pieChartModel.set("Registered", numberPeopleAttending);
        pieChartModel.set("Attended", numberPeopleAttended);
        return pieChartModel;
    }

    public String getFormattedEventDescription() {
        return WebTextUtils.INSTANCE.convertLineBreakToHTMLParagraph(event.getDescription());
    }

    public String getFormattedRegistrationDate() {
        if (this.attendee == null) {
            return "";
        }
        return WebTextUtils.INSTANCE.getFormattedDate(this.attendee.getRegistrationDate());
    }

    @PostConstruct
    public void load() {
        if (id != null && !id.isEmpty()) {
            this.event = eventBean.find(id);

            if(this.event.getParent() != null) {
                this.selectedParent = this.event.getParent().getId();
            }

            UserAccount person = userAccountBean.findByUsername(username);

            this.attendee = attendeeBean.find(this.event, person);

            this.numberPeopleAttending = attendeeBean.findNumberPeopleAttending(this.event);
            this.numberPeopleAttended = attendeeBean.findNumberPeopleAttended(this.event);
        } else {
            this.event = new Event();
        }
    }

    public String save() {
        if(selectedParent != null && !selectedParent.isEmpty()) {
            this.event.setParent(new Event(selectedParent));
        }
        eventBean.save(this.event);

        return "events?faces-redirect=true";
    }

    public String remove() {
        eventBean.remove(this.event.getId());
        return "events?faces-redirect=true";
    }

    public UserProfileMBean getUserProfileMBean() {
        return userProfileMBean;
    }

    public void setUserProfileMBean(UserProfileMBean userProfileMBean) {
        this.userProfileMBean = userProfileMBean;
    }
}