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
package org.cejug.yougi.knowledge.web.controller;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.knowledge.business.MailingListBean;
import org.cejug.yougi.knowledge.entity.MailingList;
import org.cejug.yougi.knowledge.entity.MailingListSubscription;
import org.cejug.yougi.util.ResourceBundleHelper;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class SubscriptionMBean {

    static final Logger LOGGER = Logger.getLogger("org.cejug.knowledge.web.controller.SubscriptionBean");

    @EJB
    private MailingListBean mailingListBean;

    @EJB
    private org.cejug.yougi.knowledge.business.SubscriptionBean subscriptionBean;

    @EJB
    private UserAccountBean userAccountBean;

    @ManagedProperty(value="#{param.id}")
    private String id;

    @ManagedProperty(value="#{param.list}")
    private String mailingListId;

    private MailingList mailingList;
    private MailingListSubscription subscription;

    private List<MailingListSubscription> subscriptions;
    private List<MailingList> mailingLists;
    private List<UserAccount> usersAccount;

    private String emailCriteria;
    private String selectedMailingList;
    private String selectedUserAccount;
    private Date subscriptionDate;
    private Date unsubscriptionDate;

    private Boolean isSubscribed;

    public SubscriptionMBean() {
        this.subscription = new MailingListSubscription();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMailingListId() {
        return mailingListId;
    }

    public void setMailingListId(String mailingListId) {
        this.mailingListId = mailingListId;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    public MailingListSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(MailingListSubscription subscription) {
        this.subscription = subscription;
    }

    public String getEmailCriteria() {
        return emailCriteria;
    }

    public void setEmailCriteria(String emailCriteria) {
        this.emailCriteria = emailCriteria;
    }

    public String getSelectedMailingList() {
        return selectedMailingList;
    }

    public void setSelectedMailingList(String selectedMailingList) {
        this.selectedMailingList = selectedMailingList;
    }

    public String getSelectedUserAccount() {
        return selectedUserAccount;
    }

    public void setSelectedUserAccount(String selectedUserAccount) {
        this.selectedUserAccount = selectedUserAccount;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getUnsubscriptionDate() {
        return unsubscriptionDate;
    }

    public void setUnsubscriptionDate(Date unsubscriptionDate) {
        this.unsubscriptionDate = unsubscriptionDate;
    }

    public List<MailingListSubscription> getSubscriptions() {
        return this.subscriptions;
    }

    public List<MailingList> getMailingLists() {
        if(this.mailingLists == null) {
            this.mailingLists = mailingListBean.findMailingLists();
        }
        return this.mailingLists;
    }

    public List<UserAccount> getUsersAccount() {
        if (this.usersAccount == null) {
            this.usersAccount = userAccountBean.findAllActiveAccounts();
        }
        return this.usersAccount;
    }

    public Boolean getIsSubscribed() {
        if(isSubscribed == null) {
            isSubscribed = subscriptionBean.isSubscribed(this.subscription.getEmailAddress());
        }
        return isSubscribed;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.subscription = subscriptionBean.findMailingListSubscription(id);
            this.selectedMailingList = this.subscription.getMailingList().getId();
            if(this.subscription.getUserAccount() != null) {
                this.selectedUserAccount = this.subscription.getUserAccount().getId();
            }
            else {
                UserAccount possibleUserAccount = userAccountBean.findByEmail(this.subscription.getEmailAddress());
                if(possibleUserAccount != null) {
                    this.selectedUserAccount = possibleUserAccount.getId();
                }
            }
        }

        if(mailingListId != null && !mailingListId.isEmpty()) {
            this.mailingList = mailingListBean.find(mailingListId);
        }
    }

    public String searchByEmail() {
        if(this.emailCriteria != null) {
            this.subscriptions = subscriptionBean.findMailingListSubscriptions(this.getMailingList(), this.emailCriteria);
        }
        return "subscriptions";
    }

    public String save() {
        this.subscription.setMailingList(mailingListBean.find(this.selectedMailingList));

        if(this.selectedUserAccount != null && !this.selectedUserAccount.isEmpty()) {
            this.subscription.setUserAccount(userAccountBean.find(this.selectedUserAccount));
        }

        subscriptionBean.save(this.subscription);
        return "mailing_lists?faces-redirect=true";
    }

    public String subscribe() {
        FacesContext context = FacesContext.getCurrentInstance();

        if(this.subscriptionDate == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceBundleHelper.INSTANCE.getMessage("errorCode0008"),""));
            context.validationFailed();
        }
        else {
            this.subscription.setSubscriptionDate(this.subscriptionDate);
        }

        if(context.isValidationFailed()) {
            return "subscription";
        }

        this.subscription.setMailingList(mailingListBean.find(this.selectedMailingList));
        if(this.selectedUserAccount != null && !this.selectedUserAccount.isEmpty()) {
            this.subscription.setUserAccount(userAccountBean.find(this.selectedUserAccount));
        }

        subscriptionBean.subscribe(this.subscription.getMailingList(),
                                  this.subscription.getUserAccount(),
                                  this.subscription.getSubscriptionDate());

        return "mailing_list_view?faces-redirect=true&id="+ this.subscription.getMailingList().getId();
    }

    public String unsubscribe() {
        FacesContext context = FacesContext.getCurrentInstance();

        if(this.unsubscriptionDate == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ResourceBundleHelper.INSTANCE.getMessage("errorCode0009"),""));
            context.validationFailed();
        }
        else {
            this.subscription.setUnsubscriptionDate(this.unsubscriptionDate);
        }

        if(context.isValidationFailed()) {
            return "subscription";
        }

        this.subscription.setMailingList(mailingListBean.find(this.selectedMailingList));
        if(this.selectedUserAccount != null && !this.selectedUserAccount.isEmpty()) {
            this.subscription.setUserAccount(userAccountBean.find(this.selectedUserAccount));
        }
        subscriptionBean.unsubscribe(this.subscription);
        return "mailing_list_view?faces-redirect=true&id="+ this.subscription.getMailingList().getId();
    }
}