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
package org.cejug.yougi.business;

import org.cejug.yougi.entity.JobExecution;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class JobExecutionBean extends AbstractBean<JobExecution> {

    @PersistenceContext
    private EntityManager em;

    @Resource
    TimerService timerService;

    public JobExecutionBean() {
        super(JobExecution.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
	}

    @Override
    public JobExecution save(JobExecution jobExecution) {
        JobExecution persistentJobExecution = super.save(jobExecution);

        timerService.createTimer(persistentJobExecution.getStartTime(), persistentJobExecution.getId());

        return persistentJobExecution;
    }

    @Timeout
    public void startJob(Timer timer) {
        String jobExecutionId = (String) timer.getInfo();
        JobExecution jobExecution = find(jobExecutionId);
        jobExecution.startJob();
    }
}