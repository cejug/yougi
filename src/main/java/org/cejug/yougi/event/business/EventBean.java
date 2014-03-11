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

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.business.MessageTemplateBean;
import org.cejug.yougi.business.MessengerBean;
import org.cejug.yougi.entity.EmailMessage;
import org.cejug.yougi.entity.MessageTemplate;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.util.TextUtils;

/**
 * Manages events organized by the user group.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class EventBean extends AbstractBean<Event> {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private VenueBean venueBean;

    @EJB
    private MessengerBean messengerBean;

    @EJB
    private MessageTemplateBean messageTemplateBean;

    static final Logger LOGGER = Logger.getLogger(EventBean.class.getName());

    public EventBean() {
        super(Event.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Event find(String id) {
        Event event = em.find(Event.class, id);
        event.setVenues(venueBean.findEventVenues(event));
        return event;
    }

    public List<Event> findParentEvents() {
    	List<Event> events =  em.createQuery("select e from Event e where e.parent is null order by e.endDate desc", Event.class)
        		        .getResultList();

        return loadVenues(events);
    }

    public List<Event> findEvents(Event parent) {
        List<Event> events = em.createQuery("select e from Event e where e.parent.id = :parent order by e.startDate asc", Event.class)
                               .setParameter("parent", parent.getId())
                               .getResultList();
        return loadVenues(events);
    }

    public List<Event> findUpCommingEvents() {
    	Calendar today = Calendar.getInstance();
        List<Event> events = em.createQuery("select e from Event e where e.endDate >= :today and e.parent is null order by e.startDate asc", Event.class)
        		       .setParameter("today", today.getTime())
                               .getResultList();
        return loadVenues(events);
    }

    private List<Event> loadVenues(List<Event> events) {
        if(events != null) {
            for(Event event: events) {
                event.setVenues(venueBean.findEventVenues(event));
            }
        }
        return events;
    }

    public void sendConfirmationEventAttendance(UserAccount userAccount, Event event, String dateFormat, String timeFormat, String timezone) {
        MessageTemplate messageTemplate = messageTemplateBean.find("KJDIEJKHFHSDJDUWJHAJSNFNFJHDJSLE");
        messageTemplate.setVariable("userAccount.firstName", userAccount.getFirstName());
        messageTemplate.setVariable("event.name", event.getName());
        messageTemplate.setVariable("event.venue", "");
        messageTemplate.setVariable("event.startDate", TextUtils.INSTANCE.getFormattedDate(event.getStartDate(), dateFormat));
        messageTemplate.setVariable("event.startTime", TextUtils.INSTANCE.getFormattedTime(event.getStartTime(), timeFormat, timezone));
        messageTemplate.setVariable("event.endTime", TextUtils.INSTANCE.getFormattedTime(event.getEndTime(), timeFormat, timezone));
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipient(userAccount);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        }
        catch(MessagingException e) {
            LOGGER.log(Level.WARNING, "Error when sending the confirmation of event attendance to user "+ userAccount.getPostingEmail(), e);
        }
    }
}