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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.event.entity.*;
import org.cejug.yougi.event.entity.SessionEvent;
import org.cejug.yougi.knowledge.business.TopicBean;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class SessionBean extends AbstractBean<SessionEvent> {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private TopicBean topicBean;

    @EJB
    private SpeakerBean speakerBean;

    @EJB
    private RoomBean roomBean;

    public SessionBean() {
        super(SessionEvent.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<SessionEvent> findSessions(Event event) {
        return em.createQuery("select s from SessionEvent s where s.event = :event order by s.startDate, s.startTime asc", SessionEvent.class)
                 .setParameter("event", event)
                 .getResultList();
    }

    /**
     * Returns all sessions with their speakers, which are related to the event.
     * A session may contain more than one speaker.
     * @param event The event with which the sessions are related.
     */
    public List<SessionEvent> findSessionsWithSpeakers(Event event) {
        List<SessionEvent> sessions = em.createQuery("select s from SessionEvent s where s.event.id = :event order by s.startDate, s.startTime asc", SessionEvent.class)
                                   .setParameter("event", event.getId())
                                   .getResultList();

        return loadSpeakers(sessions);
    }

    private List<SessionEvent> loadSpeakers(List<SessionEvent> sessions) {
        if(sessions != null) {
            for(SessionEvent session: sessions) {
                session.setSpeakers(speakerBean.findSpeakers(session));
            }
        }
        return sessions;
    }

    public SessionEvent findPreviousSession(SessionEvent currentSession) {
        List<SessionEvent> foundSessions = em.createQuery("select s from SessionEvent s where s.event = :event and s.startDate <= :startDate and s.startTime < :startTime order by s.startDate, s.startTime desc", SessionEvent.class)
                                        .setParameter("event", currentSession.getEvent())
                                        .setParameter("startDate", currentSession.getStartDate())
                                        .setParameter("startTime", currentSession.getStartTime())
                                        .getResultList();

        if(foundSessions != null && !foundSessions.isEmpty()) {
            return foundSessions.get(0);
        }

        return null;
    }

    public SessionEvent findNextSession(SessionEvent currentSession) {
        List<SessionEvent> foundSessions = em.createQuery("select s from SessionEvent s where s.event = :event and s.startDate >= :startDate and s.startTime > :startTime order by s.startDate, s.startTime asc", SessionEvent.class)
                .setParameter("event", currentSession.getEvent())
                .setParameter("startDate", currentSession.getStartDate())
                .setParameter("startTime", currentSession.getStartTime())
                .getResultList();

        if(foundSessions != null && !foundSessions.isEmpty()) {
            return foundSessions.get(0);
        }

        return null;
    }

    public List<SessionEvent> findSessionsByTopic(String topic) {
        return em.createQuery("select s from SessionEvent s where s.topics like '%"+ topic +"%'", SessionEvent.class).getResultList();
    }

    public List<SessionEvent> findSessionsInByTopic(String topic) {
        return em.createQuery("select s from SessionEvent s where s.topics like '%"+ topic +"%'", SessionEvent.class).getResultList();
    }

    public List<SessionEvent> findSessionsByTrack(Track track) {
        return em.createQuery("select s from SessionEvent s where s.track = :track order by s.startDate asc", SessionEvent.class)
                 .setParameter("track", track)
                 .getResultList();
    }

    public List<SessionEvent> findSessionsByRoom(Event event, Room room) {
        return em.createQuery("select s from SessionEvent s where s.event = :event and s.room = :room order by s.startDate asc", SessionEvent.class)
                 .setParameter("event", event)
                 .setParameter("room", room)
                 .getResultList();
    }

    public List<SessionEvent> findSessionsByVenue(Event event, Venue venue) {
        List<Room> rooms = roomBean.findRooms(venue);
        List<SessionEvent> sessions = new ArrayList<>();
        for(Room room: rooms) {
            sessions.addAll(findSessionsByRoom(event, room));
        }
        return sessions;
    }

    public List<SessionEvent> findSessionsSpeaker(Speaker speaker) {
        return em.createQuery("select ss.sessionEvent from SpeakerSession ss where ss.speaker = :speaker", SessionEvent.class)
                 .setParameter("speaker", speaker)
                 .getResultList();
    }

    public List<Event> findEventsSpeaker(Speaker speaker) {
        return em.createQuery("select ss.sessionEvent.event from SpeakerSession ss where ss.speaker = :speaker", Event.class)
                 .setParameter("speaker", speaker)
                 .getResultList();
    }

    public List<Speaker> findSessionSpeakersByRoom(Event event, Room room) {
        List<SessionEvent> sessions = findSessionsByRoom(event, room);
        sessions = loadSpeakers(sessions);
        Set<Speaker> speakers = new HashSet<>();
        for(SessionEvent session: sessions) {
            speakers.addAll(session.getSpeakers());
        }
        return new ArrayList<>(speakers);
    }

    public List<Speaker> findSessionSpeakersByTrack(Track track) {
        List<SessionEvent> sessions = findSessionsByTrack(track);
        sessions = loadSpeakers(sessions);
        Set<Speaker> speakers = new HashSet<>();
        for(SessionEvent session: sessions) {
            speakers.addAll(session.getSpeakers());
        }
        return new ArrayList<>(speakers);
    }

    public List<SessionEvent> findSessionsInTheSameRoom(SessionEvent session) {
        return em.createQuery("select s from SessionEvent s where s <> :session and s.room = :room order by s.startDate asc", SessionEvent.class)
                 .setParameter("session", session)
                 .setParameter("room", session.getRoom())
                 .getResultList();
    }

    public List<SessionEvent> findSessionsInParallel(SessionEvent session) {
        return em.createQuery("select s from SessionEvent s where s <> :except and s.startDate = :date and (s.startTime between :otherStartTime1 and :otherEndTime1 or s.endTime between :otherStartTime2 and :otherEndTime2)", SessionEvent.class)
                 .setParameter("except", session)
                 .setParameter("date", session.getStartDate())
                 .setParameter("otherStartTime1", session.getStartTime())
                 .setParameter("otherEndTime1", session.getEndTime())
                 .setParameter("otherStartTime2", session.getStartTime())
                 .setParameter("otherEndTime2", session.getEndTime())
                 .getResultList();
    }

    public List<SessionEvent> findRelatedSessions(SessionEvent session) {
        String strTopics = session.getTopics();
        if(strTopics == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(strTopics, ",");
        Set<SessionEvent> relatedSessions = new HashSet<>();
        String topic;
        while(st.hasMoreTokens()) {
            topic = st.nextToken().trim();
            relatedSessions.addAll(findSessionsByTopic(topic));
        }
        relatedSessions.remove(session);
        return new ArrayList<>(relatedSessions);
    }
}