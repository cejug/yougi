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
package org.cejug.yougi.knowledge.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.knowledge.entity.MailingList;
import org.cejug.yougi.knowledge.entity.MailingListSubscription;
import org.cejug.yougi.entity.EntitySupport;

/**
 * Implements the business logic related to the management of mailing lists.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class SubscriptionBean {

    @PersistenceContext
    private EntityManager em;

    static final Logger LOGGER = Logger.getLogger("org.cejug.knowledge.business.SubscriptionBean");

    public MailingListSubscription findMailingListSubscription(String id) {
        return em.find(MailingListSubscription.class, id);
    }

    /** Check if a subscription with the informed email is subscribed. */
    public boolean isSubscribed(String email) {
        try {
            em.createQuery("select mls from MailingListSubscription mls where mls.emailAddress = :email and mls.unsubscriptionDate is null")
                    .setParameter("email", email)
                    .getSingleResult();
            return true;
        }
        catch(NoResultException nre) {
            return false;
        }
        catch(NonUniqueResultException nure) {
            return true;
        }
    }

    /**
     * @param mailingList the mailing list where the subscriber might be found.
     * @param email the email address of the possible subscriber.
     * @return The found mailing list subscription or null if the email address
     * is not subscribed in the list.
     */
    public MailingListSubscription findMailingListSubscription(MailingList mailingList, String email) {
        try {
            return (MailingListSubscription) em.createQuery("select mls from MailingListSubscription mls where mls.mailingList = :mailingList and mls.emailAddress = :email and mls.unsubscriptionDate is null")
                                   .setParameter("mailingList", mailingList)
                                   .setParameter("email", email)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    /**
     * Returns a list of subscriptions in different mailing lists in which the
     * informed user is subscribed.
     */
    public List<MailingListSubscription> findMailingListSubscriptions(UserAccount userAccount) {
        return em.createQuery("select mls from MailingListSubscription mls where mls.emailAddress = :email and mls.unsubscriptionDate is null")
                 .setParameter("email", userAccount.getEmail())
                 .getResultList();
    }

    /**
     * Returns a list of subscriptions associated with the informed mailing list.
     */
    public List<MailingListSubscription> findMailingListSubscriptions(MailingList mailingList) {
        return em.createQuery("select mls from MailingListSubscription mls where mls.mailingList = :mailingList order by mls.subscriptionDate desc")
                 .setParameter("mailingList", mailingList)
                 .getResultList();
    }

    /**
     * Returns a list of mailing lists associated with the user account.
     * @param userAccount the user account that might be subscribed in a mailing list.
     * @return a list of mailing lists that the user is registered.
     */
    public List<MailingList> findSubscribedMailingLists(UserAccount userAccount) {
        return em.createQuery("select mls.mailingList from MailingListSubscription mls where mls.userAccount = :userAccount")
                 .setParameter("userAccount", userAccount)
                 .getResultList();
    }

    /**
     * Returns a subscription with the informed email and associated with the
     * informed mailing list.
     */
    public List<MailingListSubscription> findMailingListSubscriptions(MailingList mailingList, String email) {
        return em.createQuery("select mls from MailingListSubscription mls where mls.mailingList = :mailingList and mls.emailAddress = :email order by mls.subscriptionDate desc")
                 .setParameter("mailingList", mailingList)
                 .setParameter("email", email.trim())
                 .getResultList();
    }

    /** Subscribes the user in several mailing lists. */
    public void subscribe(List<MailingList> mailingLists, UserAccount userAccount) {
        // Nothing to do if the user is not informed.
        if(userAccount == null) {
            return;
        }

        // If the user account is informed and the mailing list is empty, this is
        // the case in which the user must not be associated with a mailing list,
        // thus unsubscribed from all existing lists.
        if(mailingLists.isEmpty()) {
            unsubscribeAll(userAccount);
            userAccount.setMailingList(Boolean.FALSE);
            return;
        }

        userAccount.setMailingList(Boolean.TRUE);

        // Check if the user is already registered in the informed mailing lists.
        List<MailingListSubscription> mailingListSubscriptions = findMailingListSubscriptions(userAccount);
        boolean found;
        for(MailingListSubscription mailingListSubscription: mailingListSubscriptions) {
            found = false;
            for(MailingList mailingList: mailingLists) {
                // If true, the user is already registered. No action needed.
                if(mailingListSubscription.getMailingList().equals(mailingList)) {
                    mailingLists.remove(mailingList);
                    found = true;
                    break;
                }
            }
            // If one of the existing registrations was not found in the informed mailing lists,
            // then the user is unsubscribed.
            if(!found) {
                unsubscribe(mailingListSubscription.getMailingList(), userAccount);
            }
        }

        // If there is any remaining mailing lists in the list, the user is registered to them.
        Calendar today = Calendar.getInstance();
        for(MailingList mailingList: mailingLists) {
            subscribe(mailingList, userAccount, today.getTime());
        }
    }

    /** Subscribes the user in the informed mailing list.
     * @param mailingList the mailing list where the user will be subscribed.
     * @param userAccount the user who will be subscribed.
     * @param when the date when the user is subscribed.
     */
    public void subscribe(MailingList mailingList, UserAccount userAccount, Date when) {
        if(mailingList == null || userAccount == null) {
            return;
        }

        MailingListSubscription mailingListSubscription = new MailingListSubscription();
        mailingListSubscription.setId(EntitySupport.INSTANCE.generateEntityId());
        mailingListSubscription.setMailingList(mailingList);
        mailingListSubscription.setUserAccount(userAccount);
        mailingListSubscription.setEmailAddress(userAccount.getEmail());
        mailingListSubscription.setSubscriptionDate(when);
        em.persist(mailingListSubscription);
    }

    /** Unsubscribes the user from the informed mailing list. */
    public void unsubscribe(MailingList mailingList, UserAccount userAccount) {
        MailingListSubscription mailingListSubscription = findMailingListSubscription(mailingList, userAccount.getEmail());
        if(mailingListSubscription != null) {
            Calendar today = Calendar.getInstance();
            mailingListSubscription.setUnsubscriptionDate(today.getTime());
        }
    }

    /**
     * Unsubscribe the user from a limited list of mailingLists.
     */
    public void unsubscribe(List<MailingList> mailingLists, UserAccount userAccount) {
        for(MailingList mailingList: mailingLists) {
            unsubscribe(mailingList, userAccount);
        }
    }

    /** Unsubscribes the user from all mailing lists. */
    public void unsubscribeAll(UserAccount userAccount) {
        List<MailingListSubscription> mailingListSubscriptions = findMailingListSubscriptions(userAccount);
        if(mailingListSubscriptions != null) {
            for(MailingListSubscription mailingListSubscription: mailingListSubscriptions) {
                Calendar today = Calendar.getInstance();
                mailingListSubscription.setUnsubscriptionDate(today.getTime());
                em.merge(mailingListSubscription);
            }
        }
    }

    /**
     * When the subscription is not associated to a user, but it must be
     * unsubscribed from the mailing list.
     */
    public void unsubscribe(MailingListSubscription subscription) {
        MailingListSubscription mailingListSubscription = findMailingListSubscription(subscription.getId());
        if(mailingListSubscription == null) {
            return;
        }

        mailingListSubscription.setUnsubscriptionDate(subscription.getUnsubscriptionDate());
    }

    /**
     * Unsubscribes the user account from his/er current mailing lists and
     * subscribes again, updating the email addresses.
     */
    public void changeEmailAddress(UserAccount userAccount) {
        List<MailingList> subscribedMailingLists = findSubscribedMailingLists(userAccount);

        // Close the subscription of the previous email address
        unsubscribe(subscribedMailingLists, userAccount);

        // Subscribe the new email address linked to the same user account.
        subscribe(subscribedMailingLists, userAccount);
    }

    /**
     * Save the mailing list subscription in the database.
     */
    public void save(MailingListSubscription subscription) {
        if(EntitySupport.INSTANCE.isIdNotValid(subscription)) {
            subscription.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(subscription);
        }
        else {
            em.merge(subscription);
        }
    }
}