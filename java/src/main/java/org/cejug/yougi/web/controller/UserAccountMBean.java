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
import org.cejug.yougi.entity.Authentication;
import org.cejug.yougi.entity.DeactivationType;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.qualifier.UserName;
import org.cejug.yougi.util.ResourceBundleHelper;
import org.cejug.yougi.util.StringUtils;

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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class UserAccountMBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserAccountMBean.class.getSimpleName());

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBean userAccountBean;

    @ManagedProperty(value="#{locationMBean}")
    private LocationMBean locationMBean;

    @Inject
    private FacesContext context;

    @Inject
    private HttpServletRequest request;

    @Inject
    @UserName
    private String username;

    private String userId;
    private UserAccount userAccount;

    private String password;
    private String passwordConfirmation;
    private String validationEmail;

    private Boolean validationPrivacy = false;

    public UserAccountMBean() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * That is not a entity property and it will not be saved in the database.
     * It is used only to check if the user properly typed his password.
     */
    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    // Beginning of mail validation
    public void validateEmail(FacesContext context, UIComponent component, Object value) {
        this.validationEmail = (String) value;

        if(userAccountBean.existingAccount(this.validationEmail)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceBundleHelper.INSTANCE.getMessage("errorCode0004"), null));
        }
    }

    public void validateEmailConfirmation(FacesContext context, UIComponent component, Object value) {
        String validationEmailConfirmation = (String) value;
        if(!validationEmailConfirmation.equals(this.validationEmail)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.INSTANCE.getMessage("errorCode0003"), null));
        }
    }
    // End of email validation

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

    // Beginning of privacy composite validation
    public void validatePrivacyOption(FacesContext context, UIComponent component, Object value) {
        if(!this.validationPrivacy) {
            this.validationPrivacy = (Boolean) value;
        }
    }

    public void validatePrivacy(FacesContext context, UIComponent component, Object value) {
        if(!this.validationPrivacy) {
            this.validationPrivacy = (Boolean) value;
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, ResourceBundleHelper.INSTANCE.getMessage("errorCode0007"), null));
        }
    }
    // End of privacy composite validation

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    public Boolean getNoAccount() {
        return userAccountBean.thereIsNoAccount();
    }

    public boolean isConfirmed() {
        if(StringUtils.INSTANCE.isNullOrBlank(userAccount.getConfirmationCode())) {
            return true;
        }
        return false;
    }

    public void validateUserId(FacesContext context, UIComponent toValidate, Object value) {
        String usrId = (String) value;
        if(-1 == usrId.indexOf('@')) {
            throw new ValidatorException(new FacesMessage("Invalid email address."));
        }
    }

    @PostConstruct
    public void load() {
        if(username != null) {
            this.userAccount = userAccountBean.findByUsername(username);

            if(this.userAccount.getCountry() != null) {
                locationMBean.setSelectedCountry(this.userAccount.getCountry().getAcronym());
            } else {
                locationMBean.setSelectedCountry(null);
            }

            if(this.userAccount.getProvince() != null) {
                locationMBean.setSelectedProvince(this.userAccount.getProvince().getId());
            } else {
                locationMBean.setSelectedProvince(null);
            }

            if(this.userAccount.getCity() != null) {
                locationMBean.setSelectedCity(this.userAccount.getCity().getId());
            } else {
                locationMBean.setSelectedCity(null);
            }

            if(this.userAccount.getTimeZone() != null) {
                locationMBean.setSelectedTimeZone(this.userAccount.getTimeZone());
            }
        } else {
            this.userAccount = new UserAccount();
        }
    }

    public String register() {
        boolean isFirstUser = userAccountBean.thereIsNoAccount();

        if(context.isValidationFailed()) {
            return "registration";
        }

        Authentication authentication = new Authentication();
        try {
            authentication.setUserAccount(this.userAccount);
            authentication.setUsername(userAccount.getUnverifiedEmail());
            authentication.setPassword(this.password);
            userAccountBean.register(userAccount, authentication);
        } catch(Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            context.addMessage(userId, new FacesMessage(e.getCause().getMessage()));
            return "registration";
        }

        if(isFirstUser) {
            context.addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.INSTANCE.getMessage("infoSuccessfulRegistration"), ""));
            return "login";
        } else {
            context.addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, ResourceBundleHelper.INSTANCE.getMessage("infoRegistrationConfirmationRequest"), ""));
            return "registration_confirmation";
        }
    }

    public String savePersonalData() {
        if(userAccount != null) {
            UserAccount existingUserAccount = userAccountBean.find(userAccount.getId());

            existingUserAccount.setCountry(this.locationMBean.getCountry());
            existingUserAccount.setProvince(this.locationMBean.getProvince());
            existingUserAccount.setCity(this.locationMBean.getCity());
            existingUserAccount.setTimeZone(this.locationMBean.getSelectedTimeZone());
            existingUserAccount.setFirstName(userAccount.getFirstName());
            existingUserAccount.setLastName(userAccount.getLastName());
            existingUserAccount.setGender(userAccount.getGender());
            existingUserAccount.setWebsite(userAccount.getWebsite());
            existingUserAccount.setTwitter(userAccount.getTwitter());
            existingUserAccount.setPublicProfile(userAccount.getPublicProfile());
            userAccountBean.save(existingUserAccount);

            context.getExternalContext().getSessionMap().remove("locationBean");
        }
        return "profile?faces-redirect=true";
    }

    public String savePrivacy() {
        if(userAccount != null) {
            UserAccount existingUserAccount = userAccountBean.find(userAccount.getId());
            existingUserAccount.setPublicProfile(userAccount.getPublicProfile());
            existingUserAccount.setMailingList(userAccount.getMailingList());
            existingUserAccount.setNews(userAccount.getNews());
            existingUserAccount.setGeneralOffer(userAccount.getGeneralOffer());
            existingUserAccount.setJobOffer(userAccount.getJobOffer());
            existingUserAccount.setEvent(userAccount.getEvent());
            existingUserAccount.setSponsor(userAccount.getSponsor());
            existingUserAccount.setSpeaker(userAccount.getSpeaker());

            if(!isPrivacyValid(existingUserAccount)) {
                context.addMessage(null, new FacesMessage("Selecione pelo menos uma das opções de privacidade."));
                return "privacy";
            }

            userAccountBean.save(existingUserAccount);
        }
        return "profile?faces-redirect=true";
    }

    public String deactivateMembership() {
        userAccountBean.deactivateMembership(userAccount, DeactivationType.OWNWILL);
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        try {
            request.logout();
            session.invalidate();
        } catch(ServletException se) {
            LOGGER.log(Level.INFO, se.getMessage(), se);
        }

        return "/index?faces-redirect=true";
    }

    /** Check whether at least one of the privacy options was checked. */
    private boolean isPrivacyValid(UserAccount userAccount) {
        if(userAccount.getPublicProfile() ||
             userAccount.getMailingList() ||
             userAccount.getEvent() ||
             userAccount.getNews() ||
             userAccount.getGeneralOffer() ||
             userAccount.getJobOffer() ||
             userAccount.getSponsor() ||
             userAccount.getSpeaker()) { return true; }
        return false;
    }
}