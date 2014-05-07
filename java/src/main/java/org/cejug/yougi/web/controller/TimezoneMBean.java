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

import org.cejug.yougi.business.TimezoneBean;
import org.cejug.yougi.entity.Timezone;

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
public class TimezoneMBean {

    @EJB
    private TimezoneBean timezoneBean;

    private Timezone timezone;

    private List<Timezone> timezones;

    @ManagedProperty(value="#{param.timezone}")
    private String timezoneId;

    private Boolean timezoneExistent = Boolean.FALSE;

    public TimezoneMBean() {
        this.timezone = new Timezone();
    }

    public String getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(String id) {
        this.timezoneId = id;
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
    }

    public List<Timezone> getTimezones() {
        if(this.timezones == null) {
            this.timezones = timezoneBean.findTimezones();
        }
        return this.timezones;
    }

    public Boolean getExistent() {
        return this.timezoneExistent;
    }

    @PostConstruct
    public void load() {
        if(this.timezoneId != null && !this.timezoneId.isEmpty()) {
            this.timezone = timezoneBean.findTimezone(this.timezoneId);
            if(this.timezone != null) {
                this.timezoneExistent = true;
            }
        }
    }

    public String save() {
        timezoneBean.save(this.timezone);
        return "timezones?faces-redirect=true";
    }

    public String remove() {
        timezoneBean.remove(this.timezone.getId());
        return "timezones?faces-redirect=true";
    }
}