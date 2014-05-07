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
package org.cejug.yougi.partnership.web.controller;

import org.cejug.yougi.business.AccessGroupBean;
import org.cejug.yougi.business.UserGroupBean;
import org.cejug.yougi.entity.*;
import org.cejug.yougi.partnership.business.PartnerBean;
import org.cejug.yougi.partnership.business.RepresentativeBean;
import org.cejug.yougi.partnership.entity.Partner;
import org.cejug.yougi.partnership.entity.Representative;
import org.cejug.yougi.web.controller.LocationMBean;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class PartnerMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private PartnerBean partnerBean;

    @EJB
    private AccessGroupBean accessGroupBean;

    @EJB
    private UserGroupBean userGroupBean;

    @EJB
    private RepresentativeBean representativeBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{locationMBean}")
    private LocationMBean locationMBean;

    private Partner partner;
    private List<Partner> partners;
    private List<Representative> representatives;

    // List of users from the group of partners, which are candidates for
    // representative.
    private DualListModel<UserAccount> candidates;

    public PartnerMBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public List<Partner> getPartners() {
        if (partners == null) {
            this.partners = partnerBean.findPartners();
        }
        return partners;
    }

    public List<Representative> getRepresentatives() {
        return representatives;
    }

    public LocationMBean getLocationMBean() {
        return locationMBean;
    }

    public void setLocationMBean(LocationMBean locationMBean) {
        this.locationMBean = locationMBean;
    }

    public DualListModel<UserAccount> getCandidates() {
        return candidates;
    }

    public void setCandidates(DualListModel<UserAccount> candidates) {
        this.candidates = candidates;
    }

    @PostConstruct
    public void load() {

        if (this.id != null && !this.id.isEmpty()) {
            this.partner = partnerBean.find(id);

            locationMBean.initialize();

            if (this.partner.getCountry() != null) {
                locationMBean.setSelectedCountry(this.partner.getCountry().getAcronym());
            }

            if (this.partner.getProvince() != null) {
                locationMBean.setSelectedProvince(this.partner.getProvince().getId());
            }

            if (this.partner.getCity() != null) {
                locationMBean.setSelectedCity(this.partner.getCity().getId());
            }

            AccessGroup accessGroup = accessGroupBean.findAccessGroupByName("partners");
            List<UserAccount> usersGroup = userGroupBean.findUsersGroup(accessGroup);
            List<UserAccount> reps = new ArrayList<>();
            reps.addAll(representativeBean.findRepresentativePersons(this.partner));
            usersGroup.removeAll(reps);
            this.candidates = new DualListModel<>(usersGroup, reps);
        } else {
            this.partner = new Partner();

            AccessGroup accessGroup = accessGroupBean.findAccessGroupByName("partners");
            List<UserAccount> usersGroup = userGroupBean.findUsersGroup(accessGroup);
            List<UserAccount> reps = new ArrayList<>();
            this.candidates = new DualListModel<>(usersGroup, reps);
        }
    }

    public String save() {
        Country country = this.locationMBean.getCountry();
        if (country != null) {
            this.partner.setCountry(country);
        }

        Province province = this.locationMBean.getProvince();
        if (province != null) {
            this.partner.setProvince(province);
        }

        City city = this.locationMBean.getCity();
        if (city != null) {
            this.partner.setCity(city);
        }

        List<UserAccount> reps = new ArrayList<>();
        List<UserAccount> selectedCandidates = this.candidates.getTarget();
        UserAccount userAccount;
        for(int i = 0;i < selectedCandidates.size();i++) {
            userAccount = new UserAccount(((UserAccount)selectedCandidates.get(i)).getId());
            reps.add(userAccount);
        }

        representativeBean.save(this.partner, reps);

        return "partners?faces-redirect=true";
    }

    public String remove() {
        partnerBean.remove(this.partner.getId());
        return "partners?faces-redirect=true";
    }
}