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
package org.cejug.yougi.web.report;

import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.util.ResourceBundleHelper;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.util.List;

/**
 * This class feeds a column chart that shows members' preferences in terms of
 * privacy.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class CommunicationPrivacyRange implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBean userAccountBean;

    private CartesianChartModel communicationPrivacyModel;

    public CommunicationPrivacyRange() {
    }

    public CartesianChartModel getCommunicationPrivacyModel() {
        return this.communicationPrivacyModel;
    }

    @PostConstruct
    public void load() {
        communicationPrivacyModel = new CartesianChartModel();

        Integer totalPublicProfile = 0, totalMailingList = 0, totalNews = 0,
                totalGeneralOffer = 0, totalJobOffer = 0, totalEvent = 0,
                totalSponsor = 0, totalSpeaker = 0;

        List<UserAccount> userAccounts = userAccountBean.findAllActiveAccounts();
        for (UserAccount userAccount : userAccounts) {
            if (userAccount.getPublicProfile()) {
                totalPublicProfile++;
            }

            if (userAccount.getMailingList()) {
                totalMailingList++;
            }

            if (userAccount.getNews()) {
                totalNews++;
            }

            if (userAccount.getGeneralOffer()) {
                totalGeneralOffer++;
            }

            if (userAccount.getJobOffer()) {
                totalJobOffer++;
            }

            if (userAccount.getEvent()) {
                totalEvent++;
            }

            if (userAccount.getSponsor()) {
                totalSponsor++;
            }

            if (userAccount.getSpeaker()) {
                totalSpeaker++;
            }
        }

        ChartSeries communicarionPrivacyActive = new ChartSeries();
        communicarionPrivacyActive.setLabel(ResourceBundleHelper.INSTANCE.getMessage("active"));
        communicarionPrivacyActive.set(ResourceBundleHelper.INSTANCE.getMessage("publicProfile"), totalPublicProfile);
        communicarionPrivacyActive.set(ResourceBundleHelper.INSTANCE.getMessage("mailingList"), totalMailingList);
        communicarionPrivacyActive.set(ResourceBundleHelper.INSTANCE.getMessage("news"), totalNews);
        communicarionPrivacyActive.set(ResourceBundleHelper.INSTANCE.getMessage("generalOffer"), totalGeneralOffer);
        communicarionPrivacyActive.set(ResourceBundleHelper.INSTANCE.getMessage("jobOffer"), totalJobOffer);
        communicarionPrivacyActive.set(ResourceBundleHelper.INSTANCE.getMessage("event"), totalEvent);
        communicarionPrivacyActive.set(ResourceBundleHelper.INSTANCE.getMessage("sponsor"), totalSponsor);
        communicarionPrivacyActive.set(ResourceBundleHelper.INSTANCE.getMessage("speaker"), totalSpeaker);
        this.communicationPrivacyModel.addSeries(communicarionPrivacyActive);

        Integer ttl = userAccounts.size();
        ChartSeries communicarionPrivacyInactive = new ChartSeries();
        communicarionPrivacyInactive.setLabel(ResourceBundleHelper.INSTANCE.getMessage("inactive"));
        communicarionPrivacyInactive.set(ResourceBundleHelper.INSTANCE.getMessage("publicProfile"), ttl - totalPublicProfile);
        communicarionPrivacyInactive.set(ResourceBundleHelper.INSTANCE.getMessage("mailingList"), ttl - totalMailingList);
        communicarionPrivacyInactive.set(ResourceBundleHelper.INSTANCE.getMessage("news"), ttl - totalNews);
        communicarionPrivacyInactive.set(ResourceBundleHelper.INSTANCE.getMessage("generalOffer"), ttl - totalGeneralOffer);
        communicarionPrivacyInactive.set(ResourceBundleHelper.INSTANCE.getMessage("jobOffer"), ttl - totalJobOffer);
        communicarionPrivacyInactive.set(ResourceBundleHelper.INSTANCE.getMessage("event"), ttl - totalEvent);
        communicarionPrivacyInactive.set(ResourceBundleHelper.INSTANCE.getMessage("sponsor"), ttl - totalSponsor);
        communicarionPrivacyInactive.set(ResourceBundleHelper.INSTANCE.getMessage("speaker"), ttl - totalSpeaker);
        this.communicationPrivacyModel.addSeries(communicarionPrivacyInactive);
    }
}