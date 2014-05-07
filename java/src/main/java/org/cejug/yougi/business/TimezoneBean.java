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
package org.cejug.yougi.business;

import org.cejug.yougi.entity.Timezone;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Manages user-friendly time zones.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class TimezoneBean {

    @PersistenceContext
    private EntityManager em;

    public Timezone findTimezone(String id) {
        if(id != null) {
            return em.find(Timezone.class, id);
        } else {
            return null;
        }
    }

    public Timezone findDefaultTimezone() {
        return em.createQuery("select tz from Timezone tz where tz.defaultTz = true", Timezone.class).getSingleResult();
    }

    public List<Timezone> findTimezones() {
        return em.createQuery("select tz from Timezone tz order by tz.rawOffset asc", Timezone.class)
                 .getResultList();
    }

    public void save(Timezone timezone) {
        if(timezone.getDefaultTz()) {
            Timezone defaultTimezone = findDefaultTimezone();
            if(!timezone.equals(defaultTimezone)) {
                defaultTimezone.setDefaultTz(Boolean.FALSE);
            }
        }

        Timezone existingTimezone = em.find(Timezone.class, timezone.getId());
        if(existingTimezone == null) {
            em.persist(timezone);
        } else {
            em.merge(timezone);
        }
    }

    public void remove(String id) {
        Timezone timezone = em.find(Timezone.class, id);
        if(timezone != null) {
            em.remove(timezone);
        }
    }
}