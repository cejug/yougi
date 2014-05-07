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

import org.cejug.yougi.exception.EnvironmentResourceException;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Adapts a mime email message to the application domain, considering a
 * UserAccount as a usual recipient.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class EmailMessage {

    private UserAccount[] recipients;
    private String subject;
    private String body;

    EmailMessage() {}

    public UserAccount[] getRecipients() {
        return recipients;
    }

    public UserAccount getRecipient() {
        if(recipients != null) {
            return recipients[0];
        }
        return null;
    }

    public void setRecipients(List<UserAccount> recipients) {
        if(recipients == null) {
            return;
        }

        this.recipients = new UserAccount[recipients.size()];
        int i = 0;
        for(UserAccount recipient: recipients) {
            this.recipients[i++] = recipient;
        }
    }

    public void setRecipient(UserAccount recipientTo) {
        if(recipients == null) {
            recipients = new UserAccount[1];
        }
        recipients[0] = recipientTo;
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

    public MimeMessage createMimeMessage(Session mailSession) {
        try {
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setSubject(this.getSubject(), "UTF-8");
            Address[] jRecipients;

            if(recipients != null) {
                jRecipients = new Address[recipients.length];
                for(int i = 0;i < recipients.length;i++) {
                    jRecipients[i] = new InternetAddress(recipients[i].getPostingEmail(), recipients[i].getFullName());
                }
                msg.setRecipients(RecipientType.TO, jRecipients);
            }

            msg.setText(getBody(), "UTF-8");
            msg.setHeader("Content-Type", "text/html;charset=UTF-8");

            return msg;
        } catch (MessagingException | UnsupportedEncodingException me) {
            throw new EnvironmentResourceException("Error when sending the mail confirmation.", me);
        }
    }
}