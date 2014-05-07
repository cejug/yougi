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
import org.cejug.yougi.entity.City;
import org.cejug.yougi.entity.Province;
import org.cejug.yougi.entity.UserAccount;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Efraim Gentil - https://github.com/efraimgentil
 */
@Named
@RequestScoped
public class MembersMBean {

    private List<List<UserAccount>> membersRows;

    @EJB
    private UserAccountBean userAccountBean;

    @PostConstruct
    public void init() {
        membersRows = new ArrayList<>();
        List<UserAccount> userAccounts = new ArrayList<>();
        for (UserAccount account : userAccountBean.findActiveWithPublicProfile()) {
            if (membersRows.isEmpty()) {
                membersRows.add(userAccounts);
            }

            if (userAccounts.size() < 3) {
                userAccounts.add(account);
            } else {
                userAccounts = new ArrayList<>();
                membersRows.add(userAccounts);
                userAccounts.add(account);
            }
        }
    }

    public String formatedAddress(UserAccount member) {
        StringBuilder sb = new StringBuilder();
        City city = member.getCity();
        if (city != null) {
            sb.append(city.getName());
        }
        Province province = member.getProvince();
        if (province != null) {
            if (city != null) {
                sb.append(", ");
            }
            sb.append(province.getName());
        }
        return sb.toString();
    }

    public boolean showAddress(UserAccount member) {
        return member.getCity() != null || member.getCountry() != null || member.getProvince() != null;
    }

    public boolean showWebsite(UserAccount member) {
        return member.getWebsite() != null;
    }

    public boolean showTwitter(UserAccount member) {
        return member.getTwitter() != null;
    }

    public List<List<UserAccount>> getMembersRows() {
        return membersRows;
    }

}
