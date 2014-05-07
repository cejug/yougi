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

import org.cejug.yougi.business.ApplicationPropertyBean;
import org.cejug.yougi.business.LanguageBean;
import org.cejug.yougi.business.TimezoneBean;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.*;
import org.cejug.yougi.qualifier.UserName;
import org.cejug.yougi.util.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@SessionScoped
public class UserProfileMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private LanguageBean languageBean;

    @EJB
    private TimezoneBean timezoneBean;

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    @Inject
    private FacesContext context;

    @Inject
    @UserName
    private String username;

    private Language language;
    private UserAccount userAccount;
    private String timezone;

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
        Locale locale = new Locale(language.getAcronym());
        context.getViewRoot().setLocale(locale);
        return "index?faces-redirect=true";
    }

    /**
     * In the first invocation, it loads the user account in the session, using
     * the authenticated user to search for the corresponding user in the
     * database. Subsequent invocations return the user account in the session.
     */
    public UserAccount getUserAccount() {
    	if(userAccount == null) {
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
            }
            return timezone;
        } else {
            ApplicationProperty appPropTimeZone = applicationPropertyBean.findApplicationProperty(Properties.TIMEZONE);
            if(StringUtils.INSTANCE.isNullOrBlank(appPropTimeZone.getPropertyValue())) {
                Timezone tz = timezoneBean.findDefaultTimezone();
                return tz.getId();
            } else {
                return appPropTimeZone.getPropertyValue();
            }
        }
    }

    public Date getWhatTimeIsIt() {
        return Calendar.getInstance().getTime();
    }
}