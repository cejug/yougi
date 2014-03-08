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
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.event.business.AttendeeBean;
import org.cejug.yougi.event.business.EventBean;
import org.cejug.yougi.event.entity.Attendee;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.web.controller.UserProfileMBean;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class AttendeeMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AttendeeBean attendeeBean;

    @EJB
    private EventBean eventBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    @ManagedProperty(value = "#{userProfileMBean}")
    private UserProfileMBean userProfileMBean;

    private Attendee attendee;

    private List<Event> attendedEvents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public UserProfileMBean getUserProfileMBean() {
        return userProfileMBean;
    }

    public void setUserProfileMBean(UserProfileMBean userProfileMBean) {
        this.userProfileMBean = userProfileMBean;
    }

    public Attendee getAttendee() {
        return this.attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }

    public Boolean getIsAttending() {
        if(this.attendee.getId() != null) {
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }

    public List<Event> getAttendedEvents() {
        if(this.attendedEvents == null && this.attendee != null) {
            this.attendedEvents = attendeeBean.findAttendeedEvents(this.attendee.getUserAccount());
        }
        return this.attendedEvents;
    }

    public String attendEvent() {
        this.attendee.setRegistrationDate(Calendar.getInstance().getTime());
        attendeeBean.save(this.attendee);
        return "attendee?id="+ this.attendee.getId();
    }

    public String cancelAttendance() {
        attendeeBean.remove(this.attendee.getId());
        this.attendee.setId(null);
        return "attendee";
    }

    @PostConstruct
    public void load() {
        if (this.id != null && !this.id.isEmpty()) {
            this.attendee = attendeeBean.find(id);
        }
        else if(eventId != null && !eventId.isEmpty()) {
            Event event = eventBean.find(eventId);
            UserAccount userAccount = userProfileMBean.getUserAccount();
            this.attendee = attendeeBean.find(event, userAccount);

            if(this.attendee == null) {
                this.attendee = new Attendee();
                this.attendee.setEvent(event);
                this.attendee.setUserAccount(userAccount);
            }
        }
    }
}