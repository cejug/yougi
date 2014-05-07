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

import org.cejug.yougi.entity.Authentication;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.exception.BusinessLogicException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class AuthenticationBean {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    /**
     * @param userAccount the user who has authentication credentials registered.
     * @return the user's authentication data.
     */
    public Authentication findByUserAccount(UserAccount userAccount) {
        try {
            return (Authentication) em.createQuery("select a from Authentication a where a.userAccount = :userAccount")
                                      .setParameter("userAccount", userAccount)
                                      .getSingleResult();
        } catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);
            return null;
        }
    }

    /**
     * @param userAccount the id of the user who has authentication credentials registered.
     * @return the user's authentication data.
     */
    public Authentication findByUserId(String userAccount) {
        try {
            return (Authentication) em.createQuery("select a from Authentication a where a.userAccount.id = :userAccount")
                                   .setParameter("userAccount", userAccount)
                                   .getSingleResult();
        } catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);
            return null;
        }
    }

    /**
     * Compares the informed password with the one stored in the database.
     * @param userAccount the user account that has authentication credentials.
     * @param passwordToCheck the password to be compared with the one in the database.
     * @return true if the password matches.
     */
    public Boolean passwordMatches(UserAccount userAccount, String passwordToCheck) {
        try {
            Authentication authentication = new Authentication();
            authentication = (Authentication) em.createQuery("select a from Authentication a where a.userAccount = :userAccount and a.password = :password")
                                                .setParameter("userAccount", userAccount)
                                                .setParameter("password", authentication.hashPassword(passwordToCheck))
                                                .getSingleResult();
            if(authentication != null) {
                return Boolean.TRUE;
            }
        } catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);
            return Boolean.FALSE;
        }

        return Boolean.FALSE;
    }

    /**
     * @param userAccount account of the user who wants to change his password.
     * @param newPassword the new password of the user.
     * @throws org.cejug.yougi.exception.BusinessLogicException
     */
    public void changePassword(UserAccount userAccount, String newPassword) throws BusinessLogicException {
        try {
            // Retrieve the user authentication where the password is saved.
            Authentication authentication = (Authentication) em.createQuery("select a from Authentication a where a.userAccount = :userAccount")
                                            .setParameter("userAccount", userAccount)
                                            .getSingleResult();
            if(authentication != null) {
                authentication.setPassword(newPassword);
            }
        } catch(NoResultException nre) {
            throw new BusinessLogicException("User account not found. It is not possible to change the password.", nre);
        }
    }

    public void save(Authentication authentication) {
        Authentication existingAuthentication = findByUserAccount(authentication.getUserAccount());
        if(existingAuthentication == null) {
            em.persist(authentication);
        } else {
            em.merge(authentication);
        }
    }

    public void remove(UserAccount userAccount) {
        em.createQuery("delete from Authentication a where a.userAccount = :userAccount")
                .setParameter("userAccount", userAccount)
                .executeUpdate();
    }

    public void changeUsername(UserAccount userAccount) {
        Authentication existingAuthentication = findByUserAccount(userAccount);

        remove(userAccount);
        em.detach(existingAuthentication);
        existingAuthentication.setUsername(userAccount.getEmail());
        em.persist(existingAuthentication);
    }
}