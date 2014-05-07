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

import javax.batch.operations.*;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "job_execution")
public class JobExecution implements Serializable, Identified {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(JobExecution.class.getSimpleName());

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "job_scheduler")
    private JobScheduler jobScheduler;

    @ManyToOne
    @JoinColumn(name = "owner")
    private UserAccount owner;

    @Column(name = "instance_id")
    private Long instanceId;

    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private Date endTime;

    public JobExecution() {}

    public JobExecution(JobScheduler jobScheduler, UserAccount owner, Date startTime) {
        this.jobScheduler = jobScheduler;
        this.owner = owner;
        this.startTime = startTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public JobScheduler getJobScheduler() {
        return jobScheduler;
    }

    public UserAccount getOwner() {
        return owner;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void startJob() {
        try {
            JobOperator jo = BatchRuntime.getJobOperator();
            this.instanceId = jo.start(jobScheduler.getName(), new java.util.Properties());
            LOGGER.log(Level.INFO, "Started job: {0}", this.instanceId);
        } catch (JobStartException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void stopJob() {
        try {
            JobOperator jo = BatchRuntime.getJobOperator();
            jo.stop(this.instanceId);
            LOGGER.log(Level.INFO, "Stopped job: {0}", this.instanceId);
        } catch (JobExecutionNotRunningException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    public void restartJob() {
        try {
            JobOperator jo = BatchRuntime.getJobOperator();
            this.instanceId = jo.restart(this.instanceId, new java.util.Properties());
            LOGGER.log(Level.INFO, "Restarted job: {0}", this.instanceId);
        } catch (NoSuchJobExecutionException | JobRestartException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    public void abandonJob() {
        JobOperator jo = BatchRuntime.getJobOperator();
        jo.abandon(this.instanceId);
        LOGGER.log(Level.INFO, "Abandoned job: {0}", this.instanceId);
    }

    public String toString() {
        return this.jobScheduler.getFrequencyType() + " to start at " + this.startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobExecution)) {
            return false;
        }

        JobExecution that = (JobExecution) o;

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