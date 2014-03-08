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
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.Session;
import org.cejug.yougi.knowledge.business.TopicBean;
import org.cejug.yougi.event.entity.Room;
import org.cejug.yougi.event.entity.Speaker;
import org.cejug.yougi.event.entity.Track;
import org.cejug.yougi.event.entity.Venue;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class SessionBean extends AbstractBean<Session> {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private TopicBean topicBean;

    @EJB
    private SpeakerBean speakerBean;

    @EJB
    private RoomBean roomBean;

    public SessionBean() {
        super(Session.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Session findSession(String id) {
        Session session = null;
        if (id != null) {
            session = em.find(Session.class, id);
            if(session != null) {
                session.setSpeakers(speakerBean.findSpeakers(session));
            }
        }
        return session;
    }

    public List<Session> findSessions(Event event) {
        return em.createQuery("select s from Session s where s.event = :event order by s.startDate, s.startTime asc")
                 .setParameter("event", event)
                 .getResultList();
    }

    /**
     * Returns all sessions with their speakers, which are related to the event.
     * A session may contain more than one speaker.
     * @param event The event with which the sessions are related.
     */
    public List<Session> findSessionsWithSpeakers(Event event) {
        List<Session> sessions = em.createQuery("select s from Session s where s.event = :event order by s.startDate, s.startTime asc")
                                   .setParameter("event", event)
                                   .getResultList();

        return loadSpeakers(sessions);
    }

    private List<Session> loadSpeakers(List<Session> sessions) {
        if(sessions != null) {
            for(Session session: sessions) {
                session.setSpeakers(speakerBean.findSpeakers(session));
            }
        }
        return sessions;
    }

    public Session findPreviousSession(Session currentSession) {
        List<Session> foundSessions = em.createQuery("select s from Session s where s.event = :event and s.startDate <= :startDate and s.startTime < :startTime order by s.startDate, s.startTime desc")
                                        .setParameter("event", currentSession.getEvent())
                                        .setParameter("startDate", currentSession.getStartDate())
                                        .setParameter("startTime", currentSession.getStartTime())
                                        .getResultList();

        if(foundSessions != null && !foundSessions.isEmpty()) {
            return foundSessions.get(0);
        }

        return null;
    }

    public Session findNextSession(Session currentSession) {
        List<Session> foundSessions = em.createQuery("select s from Session s where s.event = :event and s.startDate >= :startDate and s.startTime > :startTime order by s.startDate, s.startTime asc")
                .setParameter("event", currentSession.getEvent())
                .setParameter("startDate", currentSession.getStartDate())
                .setParameter("startTime", currentSession.getStartTime())
                .getResultList();

        if(foundSessions != null && !foundSessions.isEmpty()) {
            return foundSessions.get(0);
        }

        return null;
    }

    public List<Session> findSessionsByTopic(String topic) {
        return em.createQuery("select s from Session s where s.topics like '%"+ topic +"%'").getResultList();
    }

    public List<Session> findSessionsInByTopic(String topic) {
        return em.createQuery("select s from Session s where s.topics like '%"+ topic +"%'").getResultList();
    }

    public List<Session> findSessionsByTrack(Track track) {
        return em.createQuery("select s from Session s where s.track = :track order by s.startDate asc")
                 .setParameter("track", track)
                 .getResultList();
    }

    public List<Session> findSessionsByRoom(Event event, Room room) {
        return em.createQuery("select s from Session s where s.event = :event and s.room = :room order by s.startDate asc")
                 .setParameter("event", event)
                 .setParameter("room", room)
                 .getResultList();
    }

    public List<Session> findSessionsByVenue(Event event, Venue venue) {
        List<Room> rooms = roomBean.findRooms(venue);
        List<Session> sessions = new ArrayList<>();
        for(Room room: rooms) {
            sessions.addAll(findSessionsByRoom(event, room));
        }
        return sessions;
    }

    public List<Session> findSessionsSpeaker(Speaker speaker) {
        return em.createQuery("select ss.session from SpeakerSession ss where ss.speaker = :speaker")
                 .setParameter("speaker", speaker)
                 .getResultList();
    }

    public List<Event> findEventsSpeaker(Speaker speaker) {
        return em.createQuery("select ss.session.event from SpeakerSession ss where ss.speaker = :speaker")
                 .setParameter("speaker", speaker)
                 .getResultList();
    }

    public List<Speaker> findSessionSpeakersByRoom(Event event, Room room) {
        List<Session> sessions = findSessionsByRoom(event, room);
        sessions = loadSpeakers(sessions);
        Set<Speaker> speakers = new HashSet<>();
        for(Session session: sessions) {
            speakers.addAll(session.getSpeakers());
        }
        return new ArrayList<>(speakers);
    }

    public List<Speaker> findSessionSpeakersByTrack(Track track) {
        List<Session> sessions = findSessionsByTrack(track);
        sessions = loadSpeakers(sessions);
        Set<Speaker> speakers = new HashSet<>();
        for(Session session: sessions) {
            speakers.addAll(session.getSpeakers());
        }
        return new ArrayList<>(speakers);
    }

    public List<Session> findSessionsInTheSameRoom(Session session) {
        return em.createQuery("select s from Session s where s <> :session and s.room = :room order by s.startDate asc")
                 .setParameter("session", session)
                 .setParameter("room", session.getRoom())
                 .getResultList();
    }

    public List<Session> findSessionsInParallel(Session session) {
        return em.createQuery("select s from Session s where s <> :except and s.startDate = :date and (s.startTime between :otherStartTime1 and :otherEndTime1 or s.endTime between :otherStartTime2 and :otherEndTime2)")
                 .setParameter("except", session)
                 .setParameter("date", session.getStartDate())
                 .setParameter("otherStartTime1", session.getStartTime())
                 .setParameter("otherEndTime1", session.getEndTime())
                 .setParameter("otherStartTime2", session.getStartTime())
                 .setParameter("otherEndTime2", session.getEndTime())
                 .getResultList();
    }

    public List<Session> findRelatedSessions(Session session) {
        String strTopics = session.getTopics();
        if(strTopics == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(strTopics, ",");
        Set<Session> relatedSessions = new HashSet<>();
        String topic;
        while(st.hasMoreTokens()) {
            topic = st.nextToken().trim();
            relatedSessions.addAll(findSessionsByTopic(topic));
        }
        relatedSessions.remove(session);
        return new ArrayList<>(relatedSessions);
    }
}