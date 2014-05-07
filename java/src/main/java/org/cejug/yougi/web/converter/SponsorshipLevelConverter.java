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

import org.cejug.yougi.event.entity.SponsorshipLevel;
import org.cejug.yougi.util.ResourceBundleHelper;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@FacesConverter(value = "SponsorshipLevelConverter")
public class SponsorshipLevelConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        SponsorshipLevel sponsorshipLevel = (SponsorshipLevel) value;
        switch (sponsorshipLevel) {
            case DIAMOND:
                return ResourceBundleHelper.INSTANCE.getMessage("diamond");
            case TITANIUM:
                return ResourceBundleHelper.INSTANCE.getMessage("titanium");
            case PLATINUM:
                return ResourceBundleHelper.INSTANCE.getMessage("platinum");
            case GOLD:
                return ResourceBundleHelper.INSTANCE.getMessage("gold");
            case SILVER:
                return ResourceBundleHelper.INSTANCE.getMessage("silver");
            case BRONZE:
                return ResourceBundleHelper.INSTANCE.getMessage("bronze");
            default:
                return null;
        }
    }

}
