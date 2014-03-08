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
package org.cejug.yougi.event.business;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.event.entity.Attendee;
import org.cejug.yougi.event.entity.Certificate;
import org.cejug.yougi.event.entity.Event;

/**
 * Manages attendees of events organized by the user group.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class AttendeeBean extends AbstractBean<Attendee> {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private EventBean eventBean;

    public AttendeeBean() {
        super(Attendee.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Attendee find(Event event, UserAccount person) {
        Attendee attendee = null;
        List<Attendee> attendees = em.createQuery("select a from Attendee a where a.userAccount = :person and a.event = :event")
                                     .setParameter("person", person)
                                     .setParameter("event", event)
                                     .getResultList();
        if(!attendees.isEmpty()) {
            attendee = attendees.get(0);
        }

        return attendee;
    }

    public Long findNumberPeopleAttending(Event event) {
        return (Long) em.createQuery("select count(a) from Attendee a where a.event = :event").setParameter("event", event).getSingleResult();
    }

    public Long findNumberPeopleAttended(Event event) {
        return (Long) em.createQuery("select count(a) from Attendee a where a.event = :event and a.attended = :attended").setParameter("event", event).setParameter("attended", true).getSingleResult();
    }

    public Boolean isAttending(Event event, UserAccount person) {
        try {
            Attendee attendee = (Attendee) em.createQuery("select a from Attendee a where a.userAccount = :person and a.event = :event").setParameter("person", person).setParameter("event", event).getSingleResult();
            return attendee != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    /**
     * Find the attendees of a specific event only.
     * @param event the event on which attendees are registered.
     * @return a list of found attendees.
     */
    public List<Attendee> findAttendees(Event event) {
        return em.createQuery("select a from Attendee a where a.event = :event order by a.userAccount.firstName asc").setParameter("event", event).getResultList();
    }

    /**
     * Find the attendees from the informed event and from all its sub-events.
     * @param event the event on which attendees are registered. Also considered
     * as the parent of other events.
     * @return a list of found attendees.
     */
    public List<Attendee> findAllAttendees(Event event) {
        List<Attendee> attendees = findAttendees(event);
        List<Event> subEvents = eventBean.findEvents(event);
        if(subEvents != null && !subEvents.isEmpty()) {
            for(Event subEvent: subEvents) {
                attendees.addAll(findAllAttendees(subEvent));
            }
        }
        return attendees;
    }

    public List<Attendee> findAttendeesWhoAttended(Event event) {
        return em.createQuery("select a from Attendee a where a.event = :event and a.attended = :attended order by a.userAccount.firstName asc").setParameter("event", event).setParameter("attended", true).getResultList();
    }

    /**
     * Returns a list of events in which the presence of the user was confirmed.
     */
    public List<Event> findAttendeedEvents(UserAccount userAccount) {
        return em.createQuery("select a.event from Attendee a where a.userAccount = :userAccount and a.attended = :attended order by a.event.startDate desc")
                 .setParameter("userAccount", userAccount)
                 .setParameter("attended", true)
                 .getResultList();
    }

    /**
     * Confirm the attendance of a list of members in a event.
     */
    public void confirmMembersAttendance(Event event, Attendee[] confirmedAttendees) {
        /* Compares the existing list of attendees with the list of confirmed
         * attendees.*/
        List<Attendee> attendees = findAttendees(event);
        boolean confirmed;
        for (Attendee attendee : attendees) {
            // We initially assume that the member didn't attend.
            confirmed = false;

            /* Check whether the attendee is in the list of confirmed
             * attendees. If yes, then his(er) attendance is confirmed. */
            if(confirmedAttendees != null) {
                for (Attendee confirmedAttendee : confirmedAttendees) {
                    if (attendee.equals(confirmedAttendee)) {
                        attendee.setAttended(true);
                        attendee.generateCertificateData();
                        em.merge(attendee);
                        confirmed = true;
                        break;
                    }
                }
            }

            /* If the attendee is not in the list of confirmed attendees then
             * (s)he is set as not attending. */
            if (!confirmed) {
                attendee.setAttended(false);
                attendee.resetCertificateCode();
                em.merge(attendee);
            }
        }
    }

    /**
     * @return true if the data of the certificate match exactly the record of
     * the related attendee.
     */
    public Boolean verifyCertificateAuthenticity(Certificate certificate) {
        try {
            Attendee attendee = (Attendee) em.createQuery("select a from Attendee a where a.certificateCode = :certificateCode and a.certificateFullname = :certificateFullname and a.certificateEvent = :certificateEvent and a.certificateVenue = :certificateVenue")
                                            .setParameter("certificateCode", certificate.getCertificateCode())
                                            .setParameter("certificateFullname", certificate.getCertificateFullname())
                                            .setParameter("certificateEvent", certificate.getCertificateEvent())
                                            .setParameter("certificateVenue", certificate.getCertificateVenue())
                                            .getSingleResult();

            return attendee != null;
        }
        catch(NoResultException nre) {
            return false;
        }
    }
}