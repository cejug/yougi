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

import org.cejug.yougi.exception.BusinessLogicException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Inheritance
@Table(name = "job_scheduler")
@DiscriminatorColumn(name = "frequency_type")
public abstract class JobScheduler implements Serializable, Identified {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "default_owner")
    private UserAccount defaultOwner;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.TIME)
    @Column(name = "start_time")
    private Date startTime;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    private Integer frequency;

    private String description;

    private Boolean active;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * User responsible for the execution of the job. This user is considered the owner of this job,
     * thus it has the right to run it. In addition to the owner, the administrator also has the right
     * to run this job.
     * */
    public UserAccount getDefaultOwner() {
        return defaultOwner;
    }

    public void setDefaultOwner(UserAccount defaultOwner) {
        this.defaultOwner = defaultOwner;
    }

    /**
     * The date from which the job can be started. Before this date it is not possible to start the job.
     * */
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * The time the scheduler will try to start the job automatically. It is possible to start this job
     * before this time, but only manually and the current date should be equal or after the start date.
     * */
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * The date from which the job cannot be executed anymore. If null, the job will be executed according
     * to the frequency until the scheduler is deactivated or removed.
     * */
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * How many times the frequency is repeated before the next execution. For instance, if the frequency
     * is 2 and the type is weekly, then the job is executed every two weeks.
     * */
    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public JobExecution getNextJobExecution() throws BusinessLogicException {
        return getNextJobExecution(this.defaultOwner);
    }

    public abstract JobExecution getNextJobExecution(UserAccount owner) throws BusinessLogicException;

    public abstract JobFrequencyType getFrequencyType();

    /**
     * Checks whether the scheduler interval is valid.
     * @return true the start date is greater than or equal to now and start date is less than or equal to end date.
     * false if otherwise.
     * */
    protected boolean checkInterval() {
        boolean valid = true;

        Calendar today = Calendar.getInstance();

        if(today.getTime().compareTo(startDate) > 0) {
            valid = false;
        } else if(endDate != null && (startDate.compareTo(endDate) > 0)) {
            valid = false;
        }

        return valid;
    }

    /**
     * @return the original start time of the job execution. The date for the next job execution should be calculated
     * from this date.
     * */
    protected Calendar getJobExecutionStartTime() {
    	Calendar jobExecutionStartTime = Calendar.getInstance();
        jobExecutionStartTime.setTime(startDate);
        if(this.startTime != null) {
            Calendar jobStartTime = Calendar.getInstance();
            jobStartTime.setTime(this.startTime);
            jobExecutionStartTime.set(Calendar.HOUR_OF_DAY, jobStartTime.get(Calendar.HOUR_OF_DAY));
            jobExecutionStartTime.set(Calendar.MINUTE, jobStartTime.get(Calendar.MINUTE));
        } else {
            jobExecutionStartTime.set(Calendar.HOUR_OF_DAY, 0);
            jobExecutionStartTime.set(Calendar.MINUTE, 0);
        }
        return jobExecutionStartTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobScheduler)) {
            return false;
        }

        JobScheduler that = (JobScheduler) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}