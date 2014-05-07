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

import org.cejug.yougi.business.JobSchedulerBean;
import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.JobFrequencyType;
import org.cejug.yougi.entity.JobScheduler;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.util.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@ViewScoped
public class JobScheduleMBean {

    @EJB
    private JobSchedulerBean jobSchedulerBean;

    @EJB
    private UserAccountBean userAccountBean;

    private JobScheduler jobScheduler;

    public JobScheduler getJobScheduler() {
        if(this.jobScheduler == null) {
            this.jobScheduler = jobSchedulerBean.getDefaultInstance();
        }
        return this.jobScheduler;
    }

    public void loadJobScheduler(String id) {
        this.jobScheduler = jobSchedulerBean.find(id);
    }

    public void changeJobFrequencyType(JobFrequencyType jobFrequencyType) {
        if(this.jobScheduler == null) {
            this.jobScheduler = jobSchedulerBean.getInstance(jobFrequencyType);
        } else if(!this.jobScheduler.getFrequencyType().equals(jobFrequencyType)) {
            this.jobScheduler = jobSchedulerBean.getInstance(jobFrequencyType, this.jobScheduler);
        }
    }

    public UserAccount getDefaultOwner() {
        if(this.jobScheduler != null && this.jobScheduler.getDefaultOwner() != null) {
            return this.jobScheduler.getDefaultOwner();
        } else {
            return null;
        }
    }

    public void setDefaultOwner(String selectedOwner) {
        if(!StringUtils.INSTANCE.isNullOrBlank(selectedOwner)) {
            UserAccount owner = userAccountBean.find(selectedOwner);
            this.jobScheduler.setDefaultOwner(owner);
        }
    }
}