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
package org.cejug.yougi.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Represents the allocation of users in groups.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "user_group")
public class UserGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserGroupId id;

    @ManyToOne
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name="group_id", insertable = false, updatable = false)
    private AccessGroup accessGroup;

    private String username;

    @Column(name = "group_name")
    private String groupName;

    public UserGroup() {
    }

    public UserGroup(AccessGroup accessGroup, Authentication authentication) {
        this.accessGroup = accessGroup;
        this.userAccount = authentication.getUserAccount();
        this.id = new UserGroupId(this.accessGroup.getId(), this.userAccount.getId());
        this.username = authentication.getUsername();
        this.groupName = this.accessGroup.getName();
    }

    public UserGroupId getId() {
        return this.id;
    }

    public void setId(UserGroupId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setAuthentication(Authentication authentication) {
        this.userAccount = authentication.getUserAccount();

        if(this.id == null) {
            this.id = new UserGroupId();
        }
        this.id.setUserId(this.userAccount.getId());
        this.username = authentication.getUsername();
    }

    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;

        if(this.id == null) {
            this.id = new UserGroupId();
        }
        this.id.setGroupId(accessGroup.getId());
        this.groupName = accessGroup.getName();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserGroup)) {
            return false;
        }
        UserGroup other = (UserGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return username;
    }
}