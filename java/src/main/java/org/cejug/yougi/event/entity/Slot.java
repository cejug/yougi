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
package org.cejug.yougi.event.entity;

import org.cejug.yougi.entity.Identified;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Enforce the size of sessions within a event. If an event has slots it means
 * that the event should have sessions exactly at the size of one of its slots.
 * If an event doesn't have slots, it means that its sessions can have any sort
 * of time allocation. Slots heavily contribute to simplify the conference
 * planning.
 *
 * A slot should respect the time constraints imposed by its event. Therefore,
 * the date, start time and end time should be within the event interval.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name="slot")
public class Slot implements Serializable, Identified {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "event")
    private Event event;

    @Column(name = "date_slot")
    @Temporal(TemporalType.DATE)
    private Date when;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIME)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIME)
    private Date endTime;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Date getWhen() {
        return when;
    }

    /**
     * @param date If date is provided, then sessions allocated in this slot
     * should happen at any time within that date only. If a date is not
     * informed and the duration of the event is more than one day, then the
     * slot will be valid for all the days of the event, as long as its start
     * and end times do not conflict with the event interval or with another
     * dated slot.
     */
    public void setWhen(Date date) {
        this.when = date;
    }

    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime The time the slot starts. It should be smaller than
     * endTime. If a date is not informed and the duration of the event is more
     * than one day, then this startTime is valid for all the days of the event,
     * as long as it does not conflict with the event interval or with another
     * dated slot.
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime The time the slot ends. It should be bigger than startTime.
     * If a date is not informed and the duration of the event is more
     * than one day, then this endTime is valid for all the days of the event,
     * as long as it does not conflict with the event interval or with another
     * dated slot.
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Slot)) {
            return false;
        }
        Slot other = (Slot) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}