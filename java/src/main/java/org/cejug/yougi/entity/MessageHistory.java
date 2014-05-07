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

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "historical_message")
public class MessageHistory implements Serializable, Identified {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false)
    private String body;

    @ManyToOne
    @JoinColumn(name="recipient", nullable=false)
    private UserAccount recipient;

    @Column(name = "date_sent", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSent;

    @Column(name="message_sent")
    private Boolean messageSent;

    public MessageHistory () {}

    /**
     * A factory that creates a historical message based on an email message. In
     * this case the factory considers the email message has only one recipient.
     * All other recipients are ignored. If all recipients needs to be considered
     * then use the factory createHistoricMessages(EmailMessage emailMessage).
     * @return historical message from a single recipient.
     * @see org.cejug.entity.MessageHistory#createHistoricMessages(EmailMessage emailMessage)
     */
    public static MessageHistory createHistoricMessage(EmailMessage emailMessage) {
        MessageHistory messageHistory = new MessageHistory();
        messageHistory.setSubject(emailMessage.getSubject());
        messageHistory.setBody(emailMessage.getBody());
        messageHistory.setRecipient(emailMessage.getRecipient());
        return messageHistory;
    }

    /**
     * A factory that creates a historical message for each recipient of the
     * email message.
     * @return A list of historical messages, one for each recipient of the message.
     */
    public static List<MessageHistory> createHistoricMessages(EmailMessage emailMessage) {
        List<MessageHistory> messageHistories = new ArrayList<>();
        MessageHistory messageHistory;

        for(UserAccount userAccount: emailMessage.getRecipients()) {
            messageHistory = new MessageHistory();
            messageHistory.setRecipient(userAccount);
            messageHistory.setSubject(emailMessage.getSubject());
            messageHistory.setBody(emailMessage.getBody());
            messageHistories.add(messageHistory);
        }

        return messageHistories;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the recipient
     */
    public UserAccount getRecipient() {
        return recipient;
    }

    /**
     * @param recipient the user account for whom the message was sent.
     */
    public void setRecipient(UserAccount recipient) {
        this.recipient = recipient;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    /**
     * @return true if the message was sent to the user by email. False if it was
     * not possible to send the message.
     */
    public Boolean getMessageSent() {
        return messageSent;
    }

    /**
     * @param messageSent set true if it was possible to send the message, or
     * false if some exception ocurred.
     */
    public void setMessageSent(Boolean messageSent) {
        this.messageSent = messageSent;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MessageHistory)) {
            return false;
        }
        MessageHistory other = (MessageHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.subject;
    }
}