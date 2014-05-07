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
import org.cejug.yougi.business.AuthenticationBean;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.ApplicationProperty;
import org.cejug.yougi.entity.Authentication;
import org.cejug.yougi.entity.Properties;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.exception.BusinessLogicException;
import org.cejug.yougi.qualifier.UserName;
import org.cejug.yougi.util.ResourceBundleHelper;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class ChangePasswordMBean {

    private static final Logger LOGGER = Logger.getLogger(ChangePasswordMBean.class.getSimpleName());

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private AuthenticationBean authenticationBean;

    @ManagedProperty(value="#{param.cc}")
    private String confirmationCode;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    @Inject
    private FacesContext context;

    @Inject
    @UserName
    private String username;

    private String currentPassword;
    private String password;
    private String passwordConfirmation;

    private Boolean invalid = Boolean.FALSE;

    public ChangePasswordMBean() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public Boolean getInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

    // Beginning of password validation
    public void validatePassword(FacesContext context, UIComponent component, Object value) {
        this.password = (String) value;
    }

    public void validatePasswordConfirmation(FacesContext context, UIComponent component, Object value) {
        this.passwordConfirmation = (String) value;
        if(!this.passwordConfirmation.equals(this.password)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.INSTANCE.getMessage("errorCode0005"), null));
        }
    }
    // End of password validation

    @PostConstruct
    public void load() {
        if(confirmationCode != null && !confirmationCode.isEmpty()) {
            UserAccount userAccount = userAccountBean.findByConfirmationCode(confirmationCode);
            Authentication authentication = authenticationBean.findByUserAccount(userAccount);
            if(userAccount != null) {
                this.username = authentication.getUsername();
            } else {
                invalid = true;
            }
        }
    }

    /**
     * @return returns the next step in the navigation flow.
     */
    public String requestPasswordChange() {
        try {
            ApplicationProperty url = applicationPropertyBean.findApplicationProperty(Properties.URL);
            String serverAddress = url.getPropertyValue();
            userAccountBean.requestConfirmationPasswordChange(username, serverAddress);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.INSTANCE.getMessage("infoCode0003", username), null));
            return "change_password";
        } catch(BusinessLogicException ble) {
            LOGGER.log(Level.INFO, ble.getMessage(), ble);
            context.addMessage(null, new FacesMessage(ble.getMessage()));
            return "request_password_change";
        }
    }

    /**
     * It changes the password in case the user has forgotten it. It checks whether
     * the confirmation code sent to the user's email is valid before proceeding
     * with the password change.
     * @return returns the next step in the navigation flow.
     */
    public String changeForgottenPassword() {
        UserAccount userAccount = userAccountBean.findByConfirmationCode(confirmationCode.trim().toUpperCase());

        if(userAccount == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.INSTANCE.getMessage("errorCode0012"), null));
            return "change_password";
        }

        try {
            userAccountBean.changePassword(userAccount, this.password);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.INSTANCE.getMessage("infoCode0004"), null));
        } catch (BusinessLogicException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            context.addMessage(null, new FacesMessage(e.getMessage()));
            return "change_password";
        }
        return "login";
    }

    /**
     * It changes the password in case the user still knows his(er) own password.
     * @return returns the next step in the navigation flow.
     */
    public String changePassword() {
        UserAccount userAccount = userAccountBean.findByUsername(username);
        if(!authenticationBean.passwordMatches(userAccount, currentPassword)) {
            context.addMessage(null, new FacesMessage("The current password does not match."));
            return "change_password";
        }

        try {
            userAccountBean.changePassword(userAccount, this.password);
        } catch (BusinessLogicException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            context.addMessage(null, new FacesMessage(e.getMessage()));
            return "change_password";
        }
        return "profile?faces-redirect=true";
    }
}