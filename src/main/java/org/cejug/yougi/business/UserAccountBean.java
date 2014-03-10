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
package org.cejug.yougi.business;

import org.cejug.yougi.entity.City;
import org.cejug.yougi.entity.ApplicationProperty;
import org.cejug.yougi.entity.AccessGroup;
import org.cejug.yougi.entity.UserGroup;
import org.cejug.yougi.entity.Authentication;
import org.cejug.yougi.entity.Properties;
import org.cejug.yougi.entity.DeactivationType;
import org.cejug.yougi.entity.UserAccount;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.*;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.cejug.yougi.entity.EmailMessage;
import org.cejug.yougi.entity.MessageTemplate;
import org.cejug.yougi.exception.BusinessLogicException;
import org.cejug.yougi.entity.EntitySupport;
import org.cejug.yougi.util.UrlUtils;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class UserAccountBean extends AbstractBean<UserAccount> {

    @EJB
    private AccessGroupBean accessGroupBean;

    @EJB
    private UserGroupBean userGroupBean;

    @EJB
    private MessengerBean messengerBean;

    @EJB
    private MessageTemplateBean messageTemplateBean;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    @EJB
    private AuthenticationBean authenticationBean;

    @EJB
    private CityBean cityBean;

    @PersistenceContext
    private EntityManager em;

    static final Logger LOGGER = Logger.getLogger(UserAccountBean.class.getName());

    public UserAccountBean() {
        super(UserAccount.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Checks whether an user account exists.
     * @param username the username that uniquely identify users.
     * @return true if the account already exists.
     */
    public boolean existingAccount(String username) {
        UserAccount existing = findByUsername(username);
        return existing != null;
    }

    /**
     * @return true if there is no account registered in the database.
     * */
    public boolean thereIsNoAccount() {
        Long totalUserAccounts = (Long)em.createQuery("select count(u) from UserAccount u").getSingleResult();
        return totalUserAccounts == 0;
    }

    /**
     * Check if the username has authentication data related to it. If there is
     * no authentication data, then the user is considered as non-existing, even
     * if an user account exists.
     */
    public UserAccount findByUsername(String username) {
        try {
            return (UserAccount) em.createQuery("select a.userAccount from Authentication a where a.username = :username")
                                   .setParameter("username", username)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public UserAccount findByEmail(String email) {
        try {
            return (UserAccount) em.createQuery("select ua from UserAccount ua where ua.email = :email")
                                   .setParameter("email", email)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public UserAccount findByWebsite(String website) {
        try {
            website = UrlUtils.INSTANCE.removeProtocol(website);
            return (UserAccount) em.createQuery("select ua from UserAccount ua where ua.website like :website")
                    .setParameter("website", "%" + website + "%")
                    .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public UserAccount findByConfirmationCode(String confirmationCode) {
        try {
            return (UserAccount) em.createQuery("select ua from UserAccount ua where ua.confirmationCode = :confirmationCode")
                                   .setParameter("confirmationCode", confirmationCode)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    /**
     * @return All activated user accounts ordered by name.
     */
    public List<UserAccount> findAllActiveAccounts() {
        return em.createQuery("select ua from UserAccount ua where ua.deactivated = :deactivated and ua.confirmationCode is null order by ua.firstName", UserAccount.class)
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    /**
     * Returns user accounts ordered by registration date and in which the
     * registration date is between the informed period of time.
     */
    public List<UserAccount> findConfirmedAccounts(Date from, Date to) {
        return em.createQuery("select ua from UserAccount ua where ua.confirmationCode is null and ua.registrationDate >= :from and ua.registrationDate <= :to order by ua.registrationDate asc", UserAccount.class)
                 .setParameter("from", from)
                 .setParameter("to", to)
                 .getResultList();
    }

    public List<UserAccount> findAllNotVerifiedAccounts() {
        return em.createQuery("select ua from UserAccount ua where ua.verified = :verified and ua.deactivated = :deactivated order by ua.registrationDate desc", UserAccount.class)
                 .setParameter("verified", Boolean.FALSE)
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    public List<UserAccount> findAllStartingWith(String firstLetter) {
        return em.createQuery("select ua from UserAccount ua where ua.firstName like '"+ firstLetter +"%' and ua.deactivated = :deactivated order by ua.firstName", UserAccount.class)
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    /**
     * @return the list of deactivated user accounts that were deactivated by
     * their own will or administratively.
     */
    public List<UserAccount> findAllDeactivatedUserAccounts() {
        return em.createQuery("select ua from UserAccount ua where ua.deactivated = :deactivated and ua.deactivationType <> :type order by ua.deactivationDate desc", UserAccount.class)
                 .setParameter("deactivated", Boolean.TRUE)
                 .setParameter("type", DeactivationType.UNREGISTERED)
                 .getResultList();
    }

    /**
     * Find a user account that was previously deactivated or not activated yet.
     * @param email The email address of the user.
     * @return the user account of an unregistered user.
     */
    public UserAccount findDeactivatedUserAccount(String email) {
        try {
            return (UserAccount) em.createQuery("select ua from UserAccount ua where ua.email = :email and ua.deactivationType is not null")
                                   .setParameter("email", email)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    /**
     * Returns all users related to the informed city, independent of their
     * confirmation, validation or deactivation status.
     */
    public List<UserAccount> findInhabitantsFrom(City city) {
        if(EntitySupport.INSTANCE.isIdNotValid(city)) {
            return null;
        }

        City existingCity = cityBean.find(city.getId());
        return em.createQuery("select u from UserAccount u where u.city = :city order by u.firstName", UserAccount.class)
                .setParameter("city", existingCity)
                .getResultList();
    }

    /** <p>Register new user accounts. For the moment, this is the only way an
     * user account can be created.  This contact record is related to the new user
     * and it is set as his/her main contact. The server address is
     * informed just because it can only be detected automatically on the web
     * container.</p>
     * <p>When there is no user, the first registration creates a super user
     * with administrative rights.</p> */
    public void register(UserAccount newUserAccount, Authentication authentication) throws BusinessLogicException {

        // true if there is no account registered so far.
        boolean noAccount = thereIsNoAccount();

        /* In case there is at least one account, it checks if the current
         * registration has a corresponding account that was deactivated before.
         * If there is then the current registration updates the existing
         * account. Otherwise, a new account is created. */
        UserAccount userAccount = null;
        boolean existingAccount = false;
        if(!noAccount) {
            userAccount = findDeactivatedUserAccount(newUserAccount.getUnverifiedEmail());
            if(userAccount != null) {
                existingAccount = true;
                userAccount.setUnverifiedEmail(newUserAccount.getUnverifiedEmail());
                userAccount.setFirstName(newUserAccount.getFirstName());
                userAccount.setLastName(newUserAccount.getLastName());
                userAccount.setGender(null);
                userAccount.setDeactivated(false);
                userAccount.setDeactivationDate(null);
                userAccount.setDeactivationReason(null);
                userAccount.setDeactivationType(null);
                userAccount.setVerified(false);
            }
        }

        /* If the account does not exist yet then the informed account is taken
           into consideration. */
        if(userAccount == null) {
            userAccount = newUserAccount;
        }

        if(!noAccount) {
            ApplicationProperty timezone = applicationPropertyBean.findApplicationProperty(Properties.TIMEZONE);

            userAccount.setTimeZone(timezone.getPropertyValue());
            userAccount.defineNewConfirmationCode();
        }

        userAccount.setRegistrationDate(Calendar.getInstance().getTime());

        if(!existingAccount) {
            userAccount.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(userAccount);
        }

        authentication.setUserAccount(userAccount);
        authenticationBean.save(authentication);

        /* In case there is no account, the user is added to the administrative
         * group straight away. There is no need to send a confirmation email.*/
        if(noAccount) {
            userAccount.setEmailAsVerified();
            AccessGroup adminGroup = accessGroupBean.findAdministrativeGroup();
            UserGroup userGroup = new UserGroup(adminGroup, authentication);
            userGroupBean.add(userGroup);
        }
        else {
            /* A confirmation email is sent to all other new users. */
            ApplicationProperty appProp = applicationPropertyBean.findApplicationProperty(Properties.SEND_EMAILS);
            if(appProp.sendEmailsEnabled()) {
                ApplicationProperty url = applicationPropertyBean.findApplicationProperty(Properties.URL);
                sendEmailConfirmationRequest(userAccount, url.getPropertyValue());
            }
        }
    }

    public void sendEmailConfirmationRequest(UserAccount userAccount, String serverAddress) throws BusinessLogicException {
        MessageTemplate messageTemplate = messageTemplateBean.find("E3F122DCC87D42248872878412B34CEE");
        messageTemplate.setVariable("serverAddress", serverAddress);
        messageTemplate.setVariable("userAccount.firstName", userAccount.getFirstName());
        messageTemplate.setVariable("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipient(userAccount);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            LOGGER.log(Level.WARNING, "Error when sending the mail confirmation. The registration was not finalized.", me);
        }
    }

    /**
     * Finds the user account using the confirmation code, adds this user
     * account in the default group, sends a welcome message to the user and a
     * notification message to the administrators. The user has access to the
     * application when he/she is added to the default group.
     * @return The confirmed user account.
     * */
    public UserAccount confirmUser(String confirmationCode) {
    	if(confirmationCode == null || confirmationCode.isEmpty()) {
            return null;
        }

        try {
            UserAccount userAccount = (UserAccount)em.createQuery("select ua from UserAccount ua where ua.confirmationCode = :code", UserAccount.class)
                                                     .setParameter("code", confirmationCode)
                                                     .getSingleResult();
            if(userAccount != null) {
            	userAccount.resetConfirmationCode();
                userAccount.setEmailAsVerified();
            	userAccount.setRegistrationDate(Calendar.getInstance().getTime());

                // This step effectively allows the user to access the application.
                AccessGroup defaultGroup = accessGroupBean.findDefaultAccessGroup();
                Authentication authentication = authenticationBean.findByUserAccount(userAccount);
                UserGroup userGroup = new UserGroup(defaultGroup, authentication);
                userGroupBean.add(userGroup);

                ApplicationProperty appProp = applicationPropertyBean.findApplicationProperty(Properties.SEND_EMAILS);
                if(appProp.sendEmailsEnabled()) {
                    sendWelcomeMessage(userAccount);

                    AccessGroup administrativeGroup = accessGroupBean.findAdministrativeGroup();
                    List<UserAccount> admins = userGroupBean.findUsersGroup(administrativeGroup);
                    sendNewMemberAlertMessage(userAccount, admins);
                }
            }

            return userAccount;
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public void sendWelcomeMessage(UserAccount userAccount) {
        MessageTemplate messageTemplate = messageTemplateBean.find("47DEE5C2E0E14F8BA4605F3126FBFAF4");
        messageTemplate.setVariable("userAccount.firstName", userAccount.getFirstName());
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipient(userAccount);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            LOGGER.log(Level.WARNING, "Error when sending the deactivation reason to user "+ userAccount.getPostingEmail(), me);
        }
    }

    public void sendNewMemberAlertMessage(UserAccount userAccount, List<UserAccount> admins) {
        MessageTemplate messageTemplate = messageTemplateBean.find("0D6F96382D91454F8155A720F3326F1B");
        messageTemplate.setVariable("userAccount.fullName", userAccount.getFullName());
        messageTemplate.setVariable("userAccount.registrationDate", userAccount.getRegistrationDate());
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipients(admins);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            LOGGER.log(Level.WARNING, "Error when sending alert to administrators about the registration of "+ userAccount.getPostingEmail(), me);
        }
    }

    public UserAccount save(UserAccount userAccount) {
        userAccount.setLastUpdate(Calendar.getInstance().getTime());
        return super.save(userAccount);
    }

    public void deactivateMembership(UserAccount userAccount, DeactivationType deactivationType) {
        UserAccount existingUserAccount = find(userAccount.getId());

        existingUserAccount.setDeactivated(Boolean.TRUE);
        existingUserAccount.setDeactivationDate(Calendar.getInstance().getTime());
        existingUserAccount.setDeactivationReason(userAccount.getDeactivationReason());
        existingUserAccount.setDeactivationType(deactivationType);

        save(existingUserAccount);

        userGroupBean.removeUserFromAllGroups(existingUserAccount);

        authenticationBean.remove(existingUserAccount);

        ApplicationProperty appProp = applicationPropertyBean.findApplicationProperty(Properties.SEND_EMAILS);

        if(!existingUserAccount.getDeactivationReason().trim().isEmpty() && appProp.sendEmailsEnabled()) {
            sendDeactivationReason(existingUserAccount);
        }

        AccessGroup administrativeGroup = accessGroupBean.findAdministrativeGroup();
        List<UserAccount> admins = userGroupBean.findUsersGroup(administrativeGroup);

        if(appProp.sendEmailsEnabled()) {
            sendDeactivationAlertMessage(existingUserAccount, admins);
        }
    }

    public void sendDeactivationReason(UserAccount userAccount) {
        MessageTemplate messageTemplate;
        if(userAccount.getDeactivationType() == DeactivationType.ADMINISTRATIVE) {
            messageTemplate = messageTemplateBean.find("03BD6F3ACE4C48BD8660411FC8673DB4");
        }
        else {
            messageTemplate = messageTemplateBean.find("IKWMAJSNDOE3F122DCC87D4224887287");
        }
        messageTemplate.setVariable("userAccount.firstName", userAccount.getFirstName());
        messageTemplate.setVariable("userAccount.deactivationReason", userAccount.getDeactivationReason());
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipient(userAccount);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            LOGGER.log(Level.WARNING, "Error when sending the deactivation reason to user "+ userAccount.getPostingEmail(), me);
        }
    }

    public void sendDeactivationAlertMessage(UserAccount userAccount, List<UserAccount> admins) {
        MessageTemplate messageTemplate = messageTemplateBean.find("0D6F96382IKEJSUIWOK5A720F3326F1B");
        messageTemplate.setVariable("userAccount.fullName", userAccount.getFullName());
        messageTemplate.setVariable("userAccount.deactivationReason", userAccount.getDeactivationReason());
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipients(admins);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            LOGGER.log(Level.WARNING, "Error when sending the deactivation reason from "+ userAccount.getPostingEmail() +" to administrators.", me);
        }
    }

    public void requestConfirmationPasswordChange(String username, String serverAddress) {
        UserAccount userAccount = findByUsername(username);

        if(userAccount != null) {
            userAccount.defineNewConfirmationCode();
            sendConfirmationCode(userAccount, serverAddress);
        }
        else {
            throw new PersistenceException("Usuário inexistente: "+ username);
        }
    }

    public void sendConfirmationCode(UserAccount userAccount, String serverAddress) {
        MessageTemplate messageTemplate = messageTemplateBean.find("67BE6BEBE45945D29109A8D6CD878344");
        messageTemplate.setVariable("serverAddress", serverAddress);
        messageTemplate.setVariable("userAccount.firstName", userAccount.getFirstName());
        messageTemplate.setVariable("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipient(userAccount);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            LOGGER.log(Level.WARNING, "Error when sending the mail confirmation. The registration was not finalized.", me);
        }
    }

    /**
     * Changes the email address of the user without having to repeat the
     * registration process.
     * @param userAccount the user account that intends to change its email address.
     * @param newEmail the new email address of the user account.
     * @exception BusinessLogicException in case the newEmail is already registered.
     */
    public void changeEmail(UserAccount userAccount, String newEmail) throws BusinessLogicException {
        // Check if the new email already exists in the UserAccounts
        UserAccount existingUserAccount = findByEmail(newEmail);

        if(existingUserAccount != null) {
            throw new BusinessLogicException("errorCode0001");
        }

        existingUserAccount = find(userAccount.getId());

        existingUserAccount.defineNewConfirmationCode();

        // Change the email address in the UserAccount
        existingUserAccount.setUnverifiedEmail(newEmail);

        // Send an email to the user to confirm the new email address
        ApplicationProperty url = applicationPropertyBean.findApplicationProperty(Properties.URL);
        sendEmailVerificationRequest(existingUserAccount, url.getPropertyValue());
    }

    /**
     * Sends a email to the user that requested to change his/her email address,
     * asking him/her to confirm the request by clicking on the informed link. If
     * the user successfully click on the link it means that his/her email address
     * is valid since he/she could receive the email message successfully.
     * @param userAccount the user who wants to change his/her email address.
     * @param serverAddress the URL of the server where the application is deployed.
     * it will be used to build the URL that the user will click to validate his/her
     * email address.
     */
    public void sendEmailVerificationRequest(UserAccount userAccount, String serverAddress) throws BusinessLogicException {
        MessageTemplate messageTemplate = messageTemplateBean.find("KJZISKQBE45945D29109A8D6C92IZJ89");
        messageTemplate.setVariable("serverAddress", serverAddress);
        messageTemplate.setVariable("userAccount.firstName", userAccount.getFirstName());
        messageTemplate.setVariable("userAccount.email", userAccount.getEmail());
        messageTemplate.setVariable("userAccount.unverifiedEmail", userAccount.getUnverifiedEmail());
        messageTemplate.setVariable("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipient(userAccount);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            LOGGER.log(Level.WARNING, "Error when sending the mail confirmation. The registration was not finalized.", me);
        }
    }

    public void confirmEmailChange(String confirmationCode) throws BusinessLogicException {
        UserAccount userAccount = findByConfirmationCode(confirmationCode);
        if(userAccount.getUnverifiedEmail() == null || userAccount.getUnverifiedEmail().isEmpty()) {
            throw new BusinessLogicException("errorCode0002");
        }

        userAccount.resetConfirmationCode();
        userAccount.setEmailAsVerified();

        /* Since the email address is also the username, change the username in
         * the Authentication and in the UserGroup */
        userGroupBean.changeUsername(userAccount);

        authenticationBean.changeUsername(userAccount);
    }

    public void changePassword(UserAccount userAccount, String password) throws BusinessLogicException {
        authenticationBean.changePassword(userAccount, password);
        userAccount.resetConfirmationCode();
        save(userAccount);
    }

    /**
     * Remove all users whose registration date is older than two days ago, the
     * confirmation code is not null and the user was not verified by an
     * administrator. This method is scheduled to execute every 12 hours.
     */
    @Schedules({ @Schedule(hour="*/12") })
    public void removeNonConfirmedAccounts(Timer timer) {
        LOGGER.log(Level.INFO, "Timer to remove non confirmed accounts started.");

        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.add(Calendar.DAY_OF_YEAR, -2);

        Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        LOGGER.log(Level.INFO, "Accounts that were now confirmed after {0} will be removed.", formatter.format(twoDaysAgo.getTime()));

        int i = em.createQuery("delete from UserAccount ua where ua.registrationDate <= :twoDaysAgo and ua.confirmationCode is not null and ua.verified = false")
                  .setParameter("twoDaysAgo", twoDaysAgo.getTime())
                  .executeUpdate();

        LOGGER.log(Level.INFO, "Number of removed accounts: {0}", i);
    }

    /**
     * Update the time zone of all users that inhabit the informed city.
     */
    public void updateTimeZoneInhabitants(City city) {
        if(city.getTimeZone() != null && !city.getTimeZone().isEmpty()) {
            List<UserAccount> userAccounts = findInhabitantsFrom(city);
            for(UserAccount user: userAccounts) {
                user.setTimeZone(city.getTimeZone());
            }
        }
    }
}