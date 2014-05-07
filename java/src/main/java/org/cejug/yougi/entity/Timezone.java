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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.TimeZone;

/**
 * Due to the user-unfriendly Java TimeZone implementation, this class was
 * created to represent friendly time zones to end-users. User friendly time zones
 * are stored in the database for immediate changes in case of public time zone
 * changes.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "timezone")
public class Timezone implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Integer INTERVAL_RAW_OFFSET = 3600000;
    private static final Integer MINUTE_SCALE = 60000;

    @Id
    private String id;

    @Column(name = "raw_offset")
    private Integer rawOffset;

    private String label;

    @Column(name = "default_tz")
    private Boolean defaultTz;

    public Timezone() {
        this.rawOffset = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOffset() {
        StringBuilder sb = new StringBuilder();
        Integer absRawOffset = Math.abs(this.rawOffset);

        if(this.rawOffset >= 0) {
            sb.append("+");
        } else {
            sb.append("-");
        }

        sb.append(String.format("%02d", absRawOffset / INTERVAL_RAW_OFFSET));

        sb.append(":");

        if((this.rawOffset % INTERVAL_RAW_OFFSET) != 0) {
            sb.append(String.format("%02d", (absRawOffset - ((absRawOffset / INTERVAL_RAW_OFFSET) * INTERVAL_RAW_OFFSET)) / MINUTE_SCALE));
        } else {
            sb.append("00");
        }

        return sb.toString();
    }

    public String getSign() {
        String sign;
        if(this.rawOffset >= 0) {
            sign = "+";
        } else {
            sign = "-";
        }
        return sign;
    }

    public void setSign(String sign) {
        if ("-".equals(sign)) {
            if (this.rawOffset < 0) {
                this.rawOffset *= -1;
            }
        } else {
            if(this.rawOffset >= 0) {
                this.rawOffset *= +1;
            }
        }
    }

    public Integer getOffsetHour() {
        Integer absRawOffset = Math.abs(this.rawOffset);
        return absRawOffset / INTERVAL_RAW_OFFSET;
    }

    public void setOffsetHour(Integer offsetHour) {
        Integer rest = 0;
        if((this.rawOffset % INTERVAL_RAW_OFFSET) != 0) {
            Integer absRawOffset = Math.abs(this.rawOffset);
            rest = absRawOffset - ((absRawOffset / INTERVAL_RAW_OFFSET) * INTERVAL_RAW_OFFSET);
        }

        Integer newRawOffset = (offsetHour * INTERVAL_RAW_OFFSET) + rest;

        if(this.rawOffset >= 0) {
            this.rawOffset = newRawOffset;
        } else {
            this.rawOffset = newRawOffset * -1;
        }
    }

    public Integer getOffsetMinute() {
        Integer absRawOffset = Math.abs(this.rawOffset);
        Integer minutes = 0;
        if((this.rawOffset % INTERVAL_RAW_OFFSET) != 0) {
            minutes = (absRawOffset - ((absRawOffset / INTERVAL_RAW_OFFSET) * INTERVAL_RAW_OFFSET)) / MINUTE_SCALE;
        }
        return minutes;
    }

    public void setOffsetMinute(Integer offsetMinute) {
        Integer offsetHour = getOffsetHour();
        if(this.rawOffset >= 0) {
            this.rawOffset = (offsetHour * INTERVAL_RAW_OFFSET) + (offsetMinute * MINUTE_SCALE);
        } else {
            this.rawOffset = ((offsetHour * INTERVAL_RAW_OFFSET) + (offsetMinute * MINUTE_SCALE)) * -1;
        }
    }

    public String getLabel() {
        return label;
    }

    public String getOffsetLabel() {
        return "("+ getOffset() +") "+ this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getDefaultTz() {
        return this.defaultTz;
    }

    public void setDefaultTz(Boolean defaultTz) {
        this.defaultTz = defaultTz;
    }

    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(this.id);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Timezone)) {
            return false;
        }
        Timezone other = (Timezone) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "("+ getOffset() +") "+ this.label;
    }
}