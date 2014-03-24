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
package org.cejug.yougi.web.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.cejug.yougi.business.ApplicationPropertyBean;
import org.cejug.yougi.business.LanguageBean;
import org.cejug.yougi.business.TimezoneBean;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.ApplicationProperty;
import org.cejug.yougi.entity.Language;
import org.cejug.yougi.entity.Properties;
import org.cejug.yougi.entity.Timezone;
import org.cejug.yougi.entity.UserAccount;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@SessionScoped
public class UserProfileMBean implements Serializable {

    @EJB
    private LanguageBean languageBean;

    @EJB
    private TimezoneBean timezoneBean;

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    private Language language;
    private UserAccount userAccount;
    private String timezone;

    static final Logger LOGGER = Logger.getLogger(UserProfileMBean.class.getName());

    public UserProfileMBean() {
        this.language = new Language();
    }

    public String getLanguage() {
        if (this.language != null) {
            return this.language.getAcronym();
        } else {
            this.language = new Language();
            return this.language.getAcronym();
        }
    }

    public String changeLanguage(String acronym) {
        this.language = languageBean.findLanguage(acronym);
        FacesContext fc = FacesContext.getCurrentInstance();
        Locale locale = new Locale(language.getAcronym());
        fc.getViewRoot().setLocale(locale);
        return "index?faces-redirect=true";
    }

    /**
     * In the first invocation, it loads the user account in the session, using
     * the authenticated user to search for the corresponding user in the
     * database. Subsequent invocations return the user account in the session.
     */
    public UserAccount getUserAccount() {
    	if(userAccount == null) {
            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
            String username = request.getRemoteUser();
            this.userAccount = userAccountBean.findByUsername(username);
    	}
    	return userAccount;
    }

    /**
     * Returns the time zone of the authenticated user. If no user is
     * authenticated, then it returns the time zone defined in the application
     * properties. If the time zone was not defined in the application
     * properties yet, then it returns the default time zone where the system is
     * running.
     * @return The time zone of the authenticated user.
     */
    public String getTimeZone() {
        if(userAccount != null && userAccount.getTimeZone() != null && !userAccount.getTimeZone().isEmpty()) {
            if(timezone == null) {
                timezone = userAccount.getTimeZone();
                //LOGGER.log(Level.INFO, "User timezone: {0}",timezone);
            }
            return timezone;
        }
        else {
            ApplicationProperty appPropTimeZone = applicationPropertyBean.findApplicationProperty(Properties.TIMEZONE);
            if(appPropTimeZone.getPropertyValue() == null || appPropTimeZone.getPropertyValue().isEmpty()) {
                Timezone tz = timezoneBean.findDefaultTimezone();
                //LOGGER.log(Level.INFO, "Default timezone: {0}",tz.getId());
                return tz.getId();
            }
            else {
                //LOGGER.log(Level.INFO, "App timezone: {0}",appPropTimeZone.getPropertyValue());
                return appPropTimeZone.getPropertyValue();
            }
        }
    }

    public Date getWhatTimeIsIt() {
        return Calendar.getInstance().getTime();
    }
}