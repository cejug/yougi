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
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.exception.BusinessLogicException;
import org.cejug.yougi.util.ResourceBundleHelper;
import org.cejug.yougi.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class ChangeEmailMBean {

    private static final Logger LOGGER = Logger.getLogger(ChangeEmailMBean.class.getSimpleName());

    @EJB
    private UserAccountBean userAccountBean;

    @ManagedProperty(value = "#{param.cc}")
    private String confirmationCode;

    @Inject
    private UserProfileMBean userProfileMBean;

    private UserAccount userAccount;
    private String currentEmail;
    private String newEmail;
    private String newEmailConfirmation;

    public ChangeEmailMBean() {
        userAccount = new UserAccount();
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public void setUserProfileMBean(UserProfileMBean userProfileMBean) {
        this.userProfileMBean = userProfileMBean;
    }

    /**
     * @return the userAccount that wants to change the password.
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return the currentEmail that will be changed.
     */
    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    /**
     * @return the newEmail that will replace the currentEmail.
     */
    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    /**
     * @return the newEmailConfirmation to minimize the risk of typos in the
     * address.
     */
    public String getNewEmailConfirmation() {
        return newEmailConfirmation;
    }

    public void setNewEmailConfirmation(String newEmailConfirmation) {
        this.newEmailConfirmation = newEmailConfirmation;
    }

    @PostConstruct
    public void load() {
        if (StringUtils.INSTANCE.isNullOrBlank(confirmationCode)) {
            this.userAccount = userProfileMBean.getUserAccount();
        }
    }

    /**
     * Compares the informed email with its respective confirmation.
     *
     * @return true if the email matches with its confirmation.
     */
    private boolean isEmailConfirmed() {
        return newEmail.equals(newEmailConfirmation);
    }

    /**
     * It changes the user's email.
     *
     * @return returns the next step in the navigation flow.
     */
    public String changeEmail() {

        // If password doesn't match its confirmation.
        if (!isEmailConfirmed()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The email confirmation does not match."));
            return "change_password";
        }

        try {
            userAccountBean.changeEmail(userAccount, this.newEmail);
        } catch (BusinessLogicException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ResourceBundleHelper.INSTANCE.getMessage(e.getMessage())));
            return "change_email";
        }
        return "profile?faces-redirect=true";
    }

    public String confirmEmailChange() {
        try {
            userAccountBean.confirmEmailChange(confirmationCode);
        } catch(BusinessLogicException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ResourceBundleHelper.INSTANCE.getMessage(e.getMessage())));
            return "change_email_confirmation";
        }
        return "index?faces-redirect=true";
    }
}