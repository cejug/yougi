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
import org.cejug.yougi.entity.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class JobSchedulerMBean {

    @EJB
    private JobSchedulerBean jobSchedulerBean;

    @EJB
    private UserAccountBean userAccountBean;

    private List<JobScheduler> jobSchedulers;
    private List<String> jobNames;
    private List<UserAccount> userAccounts;

    private Boolean workingDaysOnly;

    @ManagedProperty(value="#{param.id}")
    private String id;

    @ManagedProperty(value = "#{jobScheduleMBean}")
    private JobScheduleMBean jobScheduleMBean;

    public void setId(String userId) {
        this.id = userId;
    }

    public void setJobScheduleMBean(JobScheduleMBean jobScheduleMBean) {
        this.jobScheduleMBean = jobScheduleMBean;
    }

    public JobScheduler getJobScheduler() {
        return this.jobScheduleMBean.getJobScheduler();
    }

    public String getSelectedOwner() {
        UserAccount userAccount = jobScheduleMBean.getDefaultOwner();
        if(userAccount != null) {
            return userAccount.getId();
        } else {
            return null;
        }
    }

    public void setSelectedOwner(String selectedOwner) {
        jobScheduleMBean.setDefaultOwner(selectedOwner);
    }

    public JobFrequencyType getFrequencyType() {
        return jobScheduleMBean.getJobScheduler().getFrequencyType();
    }

    public void setFrequencyType(JobFrequencyType frequencyType) {
        this.jobScheduleMBean.changeJobFrequencyType(frequencyType);
    }

    public Boolean getWorkingDaysOnly() {
        return workingDaysOnly;
    }

    public void setWorkingDaysOnly(Boolean workingDaysOnly) {
        this.workingDaysOnly = workingDaysOnly;
    }

    public List<JobScheduler> getJobSchedulers() {
        if(this.jobSchedulers == null) {
            this.jobSchedulers = jobSchedulerBean.findAllActive();
        }
        return this.jobSchedulers;
    }

    public List<String> getJobNames() {
        if(this.jobNames == null) {
            this.jobNames = jobSchedulerBean.findJobNames();
        }
        return this.jobNames;
    }

    public List<UserAccount> getUserAccounts() {
        if(this.userAccounts == null) {
            this.userAccounts = userAccountBean.findAllActiveAccounts();
        }
        return this.userAccounts;
    }

    @PostConstruct
    public void load() {
        if(EntitySupport.INSTANCE.isIdValid(this.id)) {
            this.jobScheduleMBean.loadJobScheduler(this.id);
        }
    }

    public String save() {
        if(this.jobScheduleMBean.getJobScheduler().getFrequencyType() == JobFrequencyType.DAILY) {
            JobDailyScheduler jobDailyScheduler = (JobDailyScheduler) jobScheduleMBean.getJobScheduler();
            jobDailyScheduler.setWorkingDaysOnly(this.workingDaysOnly);
        }
        jobSchedulerBean.save(jobScheduleMBean.getJobScheduler());

        return "job_schedulers";
    }

    public String remove() {
        jobSchedulerBean.remove(this.jobScheduleMBean.getJobScheduler().getId());
        return "job_schedulers";
    }
}