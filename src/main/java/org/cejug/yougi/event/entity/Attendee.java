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
package org.cejug.yougi.event.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.*;
import org.cejug.yougi.entity.Identified;
import org.cejug.yougi.entity.UserAccount;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "attendee")
public class Attendee implements Serializable, Identified {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "event")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "attendee")
    private UserAccount userAccount;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "registration_date")
    private Date registrationDate;

    private Boolean attended;

    @Column(name="certificate_fullname")
    private String certificateFullname;

    @Column(name="certificate_event")
    private String certificateEvent;

    @Column(name="certificate_venue")
    private String certificateVenue;

    @Temporal(TemporalType.DATE)
    @Column(name="certificate_date")
    private Date certificateDate;

    @Column(name="certificate_code")
    private String certificateCode;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFullName() {
        return this.userAccount.getFullName();
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

    /**
     * @return the full name of the member who attended the event. If the name
     * of the member changes in its records, this field is not updated
     * automatically. The intention is to avoid generating certificates of an
     * event with different data over time. This field is also used to verify
     * the autenticity of a certificate.
     */
    public String getCertificateFullname() {
        return certificateFullname;
    }

    /**
     * @return the name of the event. If the name of the member changes in its
     * records, this field is not updated automatically. The intention is to
     * avoid generating certificates of an event with different data over time.
     * This field is also used to verify the autenticity of a certificate.
     */
    public String getCertificateEvent() {
        return certificateEvent;
    }

    /**
     * @return the name of the venue where the event took place. If the name of
     * the venue changes in its records, this field is not updated automatically.
     * The intention is to avoid generating certificates of an event with
     * different data over time.
     */
    public String getCertificateVenue() {
        return certificateVenue;
    }

    /**
     * @return the date in which the event happened. If the date of the event
     * changes in its records, this field is not updated automatically. The
     * intention is to avoid generating certificates of an event with different
     * data over time.  This field is also used to verify the autenticity of a
     * certificate.
     */
    public Date getCertificateDate() {
        return certificateDate;
    }

    /**
     * @return the certificateCode is used to verify the authenticity of the
     * generated certificate by third parts.
     */
    public String getCertificateCode() {
        return certificateCode;
    }

    /**
     * It generates the certification data if they are not yet defined and the
     * member actually attended the event.
     */
    public void generateCertificateData() {
        if(this.certificateCode == null && attended) {
            this.certificateFullname = this.userAccount.getFullName();
            this.certificateEvent = this.event.getName();
            this.certificateDate = this.event.getStartDate();
            this.certificateCode = UUID.randomUUID().toString().toUpperCase();
            this.certificateVenue = this.event.getVenues().get(0).getName();
        }
    }

    /**
     * It sets the certificate code to its default value if the member did not
     * attended. The default value is not valid for certificate validation.
     */
    public void resetCertificateCode() {
        if(!attended) {
            this.certificateFullname = null;
            this.certificateEvent = null;
            this.certificateVenue = null;
            this.certificateDate = null;
            this.certificateCode = null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Attendee)) {
            return false;
        }
        Attendee other = (Attendee) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.userAccount.getFullName();
    }
}