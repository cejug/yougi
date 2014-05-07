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
package org.cejug.yougi.event.entity;

import org.cejug.yougi.entity.Identified;
import org.cejug.yougi.entity.UserAccount;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Person with knowledge and experience to give a speech in an event, respecting
 * the scope of subjects in the domain explored by the user group event.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "speaker")
public class Speaker implements Serializable, Identified {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_account", nullable=false)
    private UserAccount userAccount;

    @Column(name = "short_cv")
    private String shortCv;

    private String experience;

    private String organization;

    public Speaker() {
    }

    public Speaker(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        if(this.userAccount != null) {
            return this.userAccount.getFullName();
        }
        return null;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getShortCv() {
        return shortCv;
    }

    public void setShortCv(String shortCv) {
        this.shortCv = shortCv;
    }

    /**
     * @return The person previous experience on speeches in other events.
     */
    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    /**
     * @return The name of the company or institution where the speaker works or
     * school or university where the user studies.
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization The name of the company or institution where the
     * speaker works or school or university where the user studies.
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Speaker)) {
            return false;
        }
        Speaker other = (Speaker) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getFullName();
    }
}