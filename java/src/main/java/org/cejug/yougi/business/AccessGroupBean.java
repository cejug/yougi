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

import com.itextpdf.tool.xml.exceptions.NotImplementedException;
import org.cejug.yougi.entity.*;
import org.cejug.yougi.exception.BusinessLogicException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class AccessGroupBean extends AbstractBean<AccessGroup> {

    private static final Logger LOGGER = Logger.getLogger(AccessGroupBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    @EJB
    private AuthenticationBean authenticationBean;

    @EJB
    private UserGroupBean userGroupBean;

    @EJB
    private MessengerBean messengerBean;

    @EJB
    private MessageTemplateBean messageTemplateBean;

    public static final String ADMIN_GROUP = "admins";
    public static final String DEFAULT_GROUP = "members";

    public AccessGroupBean() {
        super(AccessGroup.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccessGroup findAccessGroupByName(String name) {
        try {
            return em.createQuery("select ag from AccessGroup ag where ag.name = :name", AccessGroup.class)
                     .setParameter("name", name)
                     .getSingleResult();
        } catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);
            return null;
        }
    }

    public AccessGroup findDefaultAccessGroup() {
        AccessGroup defaultUserGroup;
        try {
            defaultUserGroup = (AccessGroup) em.createQuery("select ag from AccessGroup ag where ag.userDefault = :default")
                                        .setParameter("default", Boolean.TRUE)
                                        .getSingleResult();
        } catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);
            defaultUserGroup = new AccessGroup(DEFAULT_GROUP,"Default Members Group");
            defaultUserGroup.setId(EntitySupport.INSTANCE.generateEntityId());
            defaultUserGroup.setUserDefault(Boolean.TRUE);
            em.persist(defaultUserGroup);
        }
        return defaultUserGroup;
    }

    /** Returns the existing administrative group. If it doesn't find anyone
     *  then a new one is created and returned. */
    public AccessGroup findAdministrativeGroup() {
        AccessGroup group;
        try {
            group = em.createQuery("select ag from AccessGroup ag where ag.name = :name", AccessGroup.class)
                      .setParameter("name", ADMIN_GROUP)
                      .getSingleResult();
        } catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);
            group = new AccessGroup(ADMIN_GROUP,"Administrators Group");
            group.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(group);
        }
        return group;
    }

    public List<AccessGroup> findAccessGroups() {
        return em.createQuery("select ag from AccessGroup ag order by ag.name", AccessGroup.class).getResultList();
    }

    public void sendGroupAssignmentAlert(UserAccount userAccount, AccessGroup accessGroup) throws BusinessLogicException {
        MessageTemplate messageTemplate = messageTemplateBean.find("09JDIIE82O39IDIDOSJCHXUDJJXHCKP0");
        messageTemplate.setVariable("userAccount.firstName", userAccount.getFirstName());
        messageTemplate.setVariable("accessGroup.name", accessGroup.getName());
        EmailMessage emailMessage = messageTemplate.buildEmailMessage();
        emailMessage.setRecipient(userAccount);

        try {
            messengerBean.sendEmailMessage(emailMessage);
        } catch(MessagingException me) {
            LOGGER.log(Level.WARNING, "Error when sending the group assignment alert to "+ userAccount.getFullName(), me);
        }
    }

    @Override
    public AccessGroup save(AccessGroup entity) {
        throw new NotImplementedException("Please use the save method that accepts the list of members by parameter");
    }

    public void save(AccessGroup accessGroup, List<UserAccount> members) {
        if(accessGroup.getUserDefault()) {
            AccessGroup defaultGroup = findDefaultAccessGroup();
            defaultGroup.setUserDefault(false);
        }

        if(EntitySupport.INSTANCE.isIdValid(accessGroup.getId())) {
            try {
                AccessGroup group = findAccessGroupByName(accessGroup.getName());
                if(group != null) {
                    throw new PersistenceException("A group named '"+ accessGroup.getName() +"' already exists.");
                }
            } catch(NoResultException nre) {
                LOGGER.log(Level.INFO, nre.getMessage(), nre);
                accessGroup.setId(EntitySupport.INSTANCE.generateEntityId());
                em.persist(accessGroup);
            }
        } else {
            em.merge(accessGroup);
        }

        if(members != null) {
            Authentication auth;
            List<UserGroup> usersGroup = new ArrayList<>();
            for(UserAccount member: members) {
                auth = authenticationBean.findByUserId(member.getId());
                usersGroup.add(new UserGroup(accessGroup, auth));
            }
            userGroupBean.update(accessGroup, usersGroup);
        }
    }
}