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

import org.cejug.yougi.entity.*;
import org.cejug.yougi.exception.BusinessLogicException;
import org.cejug.yougi.exception.EnvironmentResourceException;
import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class JobSchedulerBean extends AbstractBean<JobScheduler> {

    private static final Logger LOGGER = Logger.getLogger(JobSchedulerBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    @EJB
    private JobExecutionBean jobExecutionBean;

    public JobSchedulerBean() {
        super(JobScheduler.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
	}

    public List<JobScheduler> findAllActive() {
        return em.createQuery("select js from JobScheduler js where js.active = :active order by js.name asc", JobScheduler.class)
                 .setParameter("active", Boolean.TRUE)
                 .getResultList();
    }

    /**
     * @return list of job names that are not scheduled at the moment.
     * */
    public List<String> findJobNames() {
        Set<String> jobNames = getJobXmlNames();
        return new ArrayList<>(jobNames);
    }

    /**
     * This method is necessary because the JSR 352 specification doesn't offer a method to retrieve the list of
     * available jobs. We need this list to enable the scheduler to start jobs without changing the code for every new
     * job definition.
     * @return a set of job xml file names that are located in the /META-INF/batch-job directory.
     * */
    private Set<String> getJobXmlNames() {
        final ClassLoader loader = JobSchedulerBean.class.getClassLoader();
        URL url = loader.getResource("/META-INF/batch-jobs");

        if (url == null) {
            return Collections.emptySet();
        }

        VirtualFile virtualFile;
        Closeable handle = null;
        String protocol = url.getProtocol();
        Set<String> names = new HashSet<>();
        try {
            if ("vfs".equals(protocol)) {
                URLConnection conn = url.openConnection();
                virtualFile = (VirtualFile) conn.getContent();
            } else if ("file".equals(protocol)) {
                virtualFile = VFS.getChild(url.toURI());
                File archiveFile = virtualFile.getPhysicalFile();
                TempFileProvider provider = TempFileProvider.create("tmp", Executors.newScheduledThreadPool(2));
                handle = VFS.mountZip(archiveFile, virtualFile, provider);
            } else {
                throw new UnsupportedOperationException("Protocol " + protocol + " is not supported");
            }

            List<VirtualFile> files = virtualFile.getChildren();
            for(VirtualFile ccFile : files) {
                if (ccFile.getName().endsWith(".xml")) {
                    names.add(ccFile.getName().substring(0, ccFile.getName().length() - 4));
                }
            }

            if(handle != null) {
                handle.close();
            }
        } catch (IOException | URISyntaxException ioe) {
            throw new EnvironmentResourceException(ioe.getMessage(), ioe);
        }

        return names;
    }

    public JobScheduler getDefaultInstance() {
        return getInstance(JobFrequencyType.DAILY);
    }

    public JobScheduler getInstance(JobFrequencyType jobFrequencyType, JobScheduler toMerge) {
        JobScheduler jobScheduler = getInstance(jobFrequencyType);
        jobScheduler = merge(toMerge, jobScheduler);
        return jobScheduler;
    }

    public <T extends JobScheduler> T getInstance(JobFrequencyType jobFrequencyType, Class<T> jobSchedulerClass, JobScheduler toMerge) {
        JobScheduler jobScheduler = getInstance(jobFrequencyType);
        jobScheduler = merge(toMerge, jobScheduler);
        return jobSchedulerClass.cast(jobScheduler);
    }

    private JobScheduler merge(JobScheduler origin, JobScheduler destine) {
        destine.setId(origin.getId());
        destine.setName(origin.getName());
        if(destine.getFrequencyType() != JobFrequencyType.INSTANT) {
            destine.setStartDate(origin.getStartDate());
        }
        destine.setEndDate(origin.getEndDate());
        destine.setStartTime(origin.getStartTime());
        destine.setDescription(origin.getDescription());
        destine.setDefaultOwner(origin.getDefaultOwner());
        destine.setFrequency(origin.getFrequency());
        destine.setActive(origin.getActive());
        return destine;
    }

    public JobScheduler getInstance(JobFrequencyType jobFrequencyType) {
        JobScheduler jobScheduler;
        switch (jobFrequencyType) {
            case INSTANT:
                jobScheduler = new JobInstantScheduler();
                break;
            case ONCE:
                jobScheduler = new JobOnceScheduler();
                break;
            case DAILY:
                jobScheduler = new JobDailyScheduler();
                jobScheduler.setFrequency(1);
                break;
            case WEEKLY:
                jobScheduler = new JobWeeklyScheduler();
                jobScheduler.setFrequency(1);
                break;
            case MONTHLY:
                jobScheduler = new JobMonthlyScheduler();
                jobScheduler.setFrequency(1);
                break;
            case YEARLY:
                jobScheduler = new JobYearlyScheduler();
                jobScheduler.setFrequency(1);
                break;
            default: return null;
        }
        jobScheduler.setStartDate(Calendar.getInstance().getTime());
        jobScheduler.setActive(true);
        return jobScheduler;
    }

    @Override
    public JobScheduler save(JobScheduler jobScheduler) {
        JobScheduler persistentJobScheduler = super.save(jobScheduler);

        try {
            JobExecution jobExecution = persistentJobScheduler.getNextJobExecution();
            LOGGER.log(Level.INFO, "Job execution: {0}", jobExecution);
            jobExecutionBean.save(jobExecution);
        } catch (BusinessLogicException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return persistentJobScheduler;
    }
}