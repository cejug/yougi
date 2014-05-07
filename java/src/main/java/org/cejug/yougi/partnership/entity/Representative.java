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
package org.cejug.yougi.partnership.entity;

import org.cejug.yougi.entity.Identified;
import org.cejug.yougi.entity.UserAccount;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "representative")
public class Representative implements Serializable, Identified {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "person")
    private UserAccount person;
    @ManyToOne
    @JoinColumn(name = "partner")
    private Partner partner;
    private String phone;
    private String position;

    public Representative() {
    }

    public Representative(Partner partner, UserAccount person) {
        this.partner = partner;
        this.person = person;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public UserAccount getPerson() {
        return person;
    }

    public void setPerson(UserAccount person) {
        this.person = person;
    }

    public Partner getPartner() {
        return this.partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((partner == null) ? 0 : partner.hashCode());
        result = prime * result + ((person == null) ? 0 : person.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Representative other = (Representative) obj;
        if (partner == null) {
            if (other.partner != null) {
                return false;
            }
        } else if (!partner.equals(other.partner)) {
            return false;
        }
        if (person == null) {
            if (other.person != null) {
                return false;
            }
        } else if (!person.equals(other.person)) {
            return false;
        }
        return true;
    }
}