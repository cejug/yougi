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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.cejug.yougi.business.AuthenticationBean;
import org.cejug.yougi.business.MessageHistoryBean;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.Authentication;
import org.cejug.yougi.entity.DeactivationType;
import org.cejug.yougi.entity.MessageHistory;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.event.business.AttendeeBean;
import org.cejug.yougi.event.entity.Event;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@SessionScoped
public class MemberMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBean userAccountBean;

    @EJB
    private AuthenticationBean authenticationBean;

    @EJB
    private MessageHistoryBean messageHistoryBean;

    @EJB
    private AttendeeBean attendeeBean;

    @ManagedProperty(value = "#{locationMBean}")
    private LocationMBean locationMBean;

    private List<UserAccount> userAccounts;

    private List<UserAccount> deactivatedUsers;

    private List<MessageHistory> historicMessages;

    private List<Event> attendedEvents;

    private String userId;

    private UserAccount userAccount;

    private Authentication authentication;

    private String emailCriteria;

    private String firstLetterCriteria;

    public MemberMBean() {
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
     * @return the user credentials
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    /**
     * @param authentication the user credentials to set
     */
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    public List<UserAccount> getUserAccounts() {
        return this.userAccounts;
    }

    public List<Event> getAttendedEvents() {
        return this.attendedEvents;
    }

    public List<UserAccount> getDeactivatedUserAccounts() {
        if(deactivatedUsers == null) {
            deactivatedUsers = userAccountBean.findAllDeactivatedUserAccounts();
        }
        return deactivatedUsers;
    }

    public String findUserAccountByEmail() {
        if (this.emailCriteria == null || this.emailCriteria.isEmpty()) {
            this.userAccounts = userAccountBean.findAllNotVerifiedAccounts();
        } else {
            List<UserAccount> uas = new ArrayList<>(1);
            UserAccount ua = userAccountBean.findByEmail(this.emailCriteria);
            if (ua != null) {
                uas.add(ua);
            }
            this.userAccounts = uas;
        }
        this.firstLetterCriteria = null;
        return "users?faces-redirect=true";
    }

    public String findUserAccountByFirstLetter(String firstLetterCriteria) {
        if (firstLetterCriteria == null || firstLetterCriteria.isEmpty()) {
            this.userAccounts = userAccountBean.findAllNotVerifiedAccounts();
        } else {
            this.firstLetterCriteria = firstLetterCriteria;
            this.userAccounts = userAccountBean.findAllStartingWith(this.firstLetterCriteria);
            this.emailCriteria = null;
        }

        return "users?faces-redirect=true";
    }

    /**
     * @return the messageHistoryItens
     */
    public List<MessageHistory> getHistoricMessages() {
        return historicMessages;
    }

    /**
     * @param historicMessages the messageHistoryItens to set
     */
    public void setHistoricMessages(List<MessageHistory> historicMessages) {
        this.historicMessages = historicMessages;
    }

    public String getEmailCriteria() {
        return emailCriteria;
    }

    public void setEmailCriteria(String emailCriteria) {
        this.emailCriteria = emailCriteria;
    }

    public String getFirstLetterCriteria() {
        return firstLetterCriteria;
    }

    public void setFirstLetterCriteria(String firstLetterCriteria) {
        this.firstLetterCriteria = firstLetterCriteria;
    }

    public boolean isConfirmed() {
        if (userAccount.getConfirmationCode() == null || userAccount.getConfirmationCode().isEmpty()) {
            return true;
        }
        return false;
    }

    public void validateUserId(FacesContext context, UIComponent toValidate, Object value) throws ValidatorException {
        String usrId = (String) value;
        if (-1 == usrId.indexOf('@')) {
            throw new ValidatorException(new FacesMessage("Invalid email address."));
        }
    }

    @PostConstruct
    public void load() {
        this.userAccounts = userAccountBean.findAllNotVerifiedAccounts();
    }

    public String load(String userId) {
        this.userId = userId;
        this.userAccount = userAccountBean.find(this.userId);
        this.authentication = authenticationBean.findByUserAccount(this.userAccount);
        this.historicMessages = messageHistoryBean.findByRecipient(this.userAccount);
        this.attendedEvents = attendeeBean.findAttendeedEvents(this.userAccount);

        locationMBean.initialize();

        if (this.userAccount.getCountry() != null) {
            locationMBean.setSelectedCountry(this.userAccount.getCountry().getAcronym());
        }

        if (this.userAccount.getProvince() != null) {
            locationMBean.setSelectedProvince(this.userAccount.getProvince().getId());
        }

        if (this.userAccount.getCity() != null) {
            locationMBean.setSelectedCity(this.userAccount.getCity().getId());
        }

        return "user?faces-redirect=true";
    }

    public String save() {
        save(null);
        return "users?faces-redirect=true";
    }

    /**
     * @param verified if true, the user account if saved with the status of verified.
     */
    private void save(Boolean verified) {
        UserAccount existingUserAccount = userAccountBean.find(userAccount.getId());

        existingUserAccount.setCountry(this.locationMBean.getCountry());
        existingUserAccount.setProvince(this.locationMBean.getProvince());
        existingUserAccount.setCity(this.locationMBean.getCity());
        existingUserAccount.setFirstName(userAccount.getFirstName());
        existingUserAccount.setLastName(userAccount.getLastName());
        existingUserAccount.setGender(userAccount.getGender());
        existingUserAccount.setWebsite(userAccount.getWebsite());
        existingUserAccount.setTwitter(userAccount.getTwitter());
        existingUserAccount.setPublicProfile(userAccount.getPublicProfile());
        existingUserAccount.setMailingList(userAccount.getMailingList());
        existingUserAccount.setNews(userAccount.getNews());
        existingUserAccount.setGeneralOffer(userAccount.getGeneralOffer());
        existingUserAccount.setJobOffer(userAccount.getJobOffer());
        existingUserAccount.setEvent(userAccount.getEvent());
        existingUserAccount.setSponsor(userAccount.getSponsor());
        existingUserAccount.setSpeaker(userAccount.getSpeaker());

        if (verified != null) {
            existingUserAccount.setVerified(verified);
        }

        userAccountBean.save(existingUserAccount);
    }

    public String deactivateMembership() {
        userAccountBean.deactivateMembership(userAccount, DeactivationType.ADMINISTRATIVE);
        return "users?faces-redirect=true";
    }

    public String confirm() {
        try {
            userAccountBean.confirmUser(userAccount.getConfirmationCode());
        } catch (IllegalArgumentException iae) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(iae.getMessage()));
            return "user";
        }
        removeSessionScoped();
        return "users?faces-redirect=true";
    }

    public String checkUserAsVerified() {
        save(Boolean.TRUE);
        removeSessionScoped();
        return "users?faces-redirect=true";
    }

    /**
     * Remove the current user permanently and navigate to the users view.
     * @return the next step in the navigation logic.
     */
    public String removeUserAccount() {
        userAccountBean.remove(this.userAccount.getId());
        removeSessionScoped();
        return "users?faces-redirect=true";
    }

    private void removeSessionScoped() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("memberBean");
    }
}