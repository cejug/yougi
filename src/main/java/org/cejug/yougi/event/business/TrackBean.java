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

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.Track;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class TrackBean extends AbstractBean<Track> {

    @PersistenceContext
    private EntityManager em;

    public TrackBean() {
        super(Track.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * It finds all tracks available in a event, including tracks from
     * its parent event. If you don't want higher level tracks, just set the
     * parent event as null.
     * @param event event instance.
     * @return a list of found tracks.
     */
    public List<Track> findTracks(Event event) {
        List<Track> tracks = new ArrayList<>();
        if(event != null) {
            tracks = em.createQuery("select t from Track t where t.event = :event order by t.name asc")
                               .setParameter("event", event)
                               .getResultList();
            tracks.addAll(findTracks(event.getParent()));
        }
        return tracks;
    }
}