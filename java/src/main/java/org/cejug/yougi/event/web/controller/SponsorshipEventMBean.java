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

import org.cejug.yougi.event.business.EventBean;
import org.cejug.yougi.event.business.SponsorshipEventBean;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.event.entity.SponsorshipEvent;
import org.cejug.yougi.partnership.business.PartnerBean;
import org.cejug.yougi.partnership.entity.Partner;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class SponsorshipEventMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private SponsorshipEventBean sponsorshipEventBean;

    @EJB
    private EventBean eventBean;

    @EJB
    private PartnerBean partnerBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    private Event event;

    private SponsorshipEvent sponsorshipEvent;

    private List<SponsorshipEvent> sponsorshipsEvent;
    private List<Event> events;
    private List<Partner> partners;

    private String selectedEvent;
    private String selectedPartner;

    public SponsorshipEventMBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public SponsorshipEvent getSponsorshipEvent() {
        return sponsorshipEvent;
    }

    public void setSponsorshipEvent(SponsorshipEvent sponsorshipEvent) {
        this.sponsorshipEvent = sponsorshipEvent;
    }

    public List<SponsorshipEvent> getSponsorshipsEvent() {
        if (sponsorshipsEvent == null) {
            this.sponsorshipsEvent = sponsorshipEventBean.findSponsorshipsEvent(this.event);
        }
        return this.sponsorshipsEvent;
    }

    public BigDecimal getSumAmounts() {
        BigDecimal sum = BigDecimal.ZERO;
        List<SponsorshipEvent> es = getSponsorshipsEvent();
        for(SponsorshipEvent sponsor: es) {
            sum = sum.add(sponsor.getAmount());
        }
        return sum;
    }

    public String getSelectedEvent() {
        return this.selectedEvent;
    }

    public void setSelectedEvent(String selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public List<Event> getEvents() {
        if (this.events == null) {
            this.events = eventBean.findParentEvents();
        }
        return this.events;
    }

    public String getSelectedPartner() {
        return this.selectedPartner;
    }

    public void setSelectedPartner(String selectedPartner) {
        this.selectedPartner = selectedPartner;
    }

    public List<Partner> getPartners() {
        if (this.partners == null) {
            this.partners = partnerBean.findPartners();
        }
        return this.partners;
    }

    @PostConstruct
    public void load() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            this.event = eventBean.find(eventId);
            this.selectedEvent = this.event.getId();
        }

        if (this.id != null && !this.id.isEmpty()) {
            this.sponsorshipEvent = sponsorshipEventBean.find(id);
            this.selectedEvent = this.sponsorshipEvent.getEvent().getId();
            this.selectedPartner = this.sponsorshipEvent.getPartner().getId();
        } else {
            this.sponsorshipEvent = new SponsorshipEvent();
        }
    }

    public String save() {
        Event evt = eventBean.find(selectedEvent);
        this.sponsorshipEvent.setEvent(evt);

        Partner spon = partnerBean.find(selectedPartner);
        this.sponsorshipEvent.setPartner(spon);

        sponsorshipEventBean.save(this.sponsorshipEvent);
        return "event?faces-redirect=true&tab=6&id=" + evt.getId();
    }

    public String remove() {
        sponsorshipEventBean.remove(this.sponsorshipEvent.getId());
        return "event?faces-redirect=true&tab=6&id=" + this.event.getId();
    }
}