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

import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.Venue;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages venues.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class VenueBean extends AbstractBean<Venue> {

    @PersistenceContext
    private EntityManager em;

    public VenueBean() {
        super(Venue.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Venue> findVenues() {
    	return em.createQuery("select v from Venue v order by v.name asc", Venue.class)
                 .getResultList();
    }

    public List<Venue> findEventVenues(Event event) {
        if(event == null) {
            return new ArrayList<>();
        }

        List<Venue> venues = em.createQuery("select ev.venue from EventVenue ev where ev.event = :event", Venue.class)
                               .setParameter("event", event)
                               .getResultList();

        if((venues == null || venues.isEmpty()) && event.getParent() != null) {
            venues = findEventVenues(event.getParent());
        }

        return venues;
    }
}