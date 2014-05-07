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

import org.cejug.yougi.event.business.AttendeeBean;
import org.cejug.yougi.event.entity.Attendee;
import org.cejug.yougi.event.entity.Event;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.Serializable;
import java.util.List;

/**
 * @author Efraim Gentil - https://github.com/efraimgentil
 */
@ManagedBean
@ViewScoped
public class RaffleMBean implements Serializable {

    private static final long serialVersionUID = 1L;

	@EJB
    private AttendeeBean attendeeBean;

	private String eventId;

	public void loadAttendees( ){
		RequestContext context = RequestContext.getCurrentInstance();
		List<Attendee> attendees = attendeeBean.findAttendees( new Event(eventId) );
		context.addCallbackParam("attendees",  toJsonString(attendees) );
	}

	protected String toJsonString( List<Attendee> attendees ){
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (Attendee attendee : attendees) {
			builder.add("name", attendee.getFullName() );
			arrayBuilder.add( builder.build() );
		}
		return arrayBuilder.build().toString();
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
}