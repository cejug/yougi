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

import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.Role;
import org.cejug.yougi.util.ResourceBundleHelper;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class SecurityBackingMBean {

    private static final Logger LOGGER = Logger.getLogger(SecurityBackingMBean.class.getSimpleName());

    @EJB
    private UserAccountBean userAccountBean;

    private String username;
    private String password;

    @ManagedProperty(value="#{sessionScope}")
    private Map<String, Object> sessionMap;

    @Inject
    private FacesContext context;

    @Inject
    private HttpServletRequest request;

    public boolean isUserSignedIn() {
        return sessionMap.containsKey("signedUser");
    }

    public String login() {
        if(userAccountBean.thereIsNoAccount()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.INSTANCE.getMessage("infoFirstUser"), ""));
            return "/registration";
        } else {
            return "/login?faces-redirect=true";
        }
    }

    public String authenticate() {
        try {
            if(!isUserSignedIn()) {
                request.login(this.username, this.password);
            }
            return "/index";
        } catch (ServletException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.INSTANCE.getMessage("errorCode0013"), null));
        }
        return "/login";
    }

    public String register() {
        if(userAccountBean.thereIsNoAccount()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.INSTANCE.getMessage("infoFirstUser"), ""));
            return "/registration";
        } else {
            return "/registration?faces-redirect=true";
        }
    }

    /**
     * Perform the logout of the user by removing the user from the session and
     * destroying the session.
     * @return The next step in the navigation flow.
     */
    public String logout() {
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        try {
            request.logout();
            session.invalidate();
        } catch(ServletException se) {
            LOGGER.log(Level.INFO, se.getMessage(), se);
            return "/index?faces-redirect=true";
        }
        return "/index?faces-redirect=true";
    }

    public Boolean getIsUserAdministrator() {
        return request.isUserInRole(Role.ADMIN.toString());
    }

    public Boolean getIsUserLeader() {
        return request.isUserInRole(Role.LEADER.toString());
    }

    public Boolean getIsUserHelper() {
        return request.isUserInRole(Role.HELPER.toString());
    }

    public Boolean getIsUserPartner() {
        return request.isUserInRole(Role.PARTNER.toString());
    }

    public Boolean getIsUserSpeaker() {
        return request.isUserInRole(Role.SPEAKER.toString());
    }

    public Map<String, Object> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}