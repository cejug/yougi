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
package org.cejug.yougi.web.converter;

import org.cejug.yougi.event.entity.Speaker;
import org.cejug.yougi.util.ResourceBundleHelper;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@FacesConverter(value="SessionSpeakerConverter")
public class SessionSpeakerConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null || !(value instanceof List)) {
            return null;
        }

        List<Speaker> speakers = (List<Speaker>) value;

        if(speakers.isEmpty()) {
            return null;
        }

        StringBuilder strSpeakers = new StringBuilder();
        strSpeakers.append(ResourceBundleHelper.INSTANCE.getMessage("by"));
        strSpeakers.append(" ");
        String and = "";
        for(Speaker speaker: speakers) {
            strSpeakers.append(and);
            if("".equals(and)) {
                and = " " + ResourceBundleHelper.INSTANCE.getMessage("and") + " ";
            }
            strSpeakers.append("<a href=\"speaker.xhtml?id=");
            strSpeakers.append(speaker.getId());
            strSpeakers.append("\">");
            strSpeakers.append(speaker.getUserAccount().getFullName());
            strSpeakers.append("</a>");
        }
        return strSpeakers.toString();
    }
}