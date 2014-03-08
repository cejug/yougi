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
 * find it, write to the Fr0D6F96382D91454F8155A720F3326F1Bee Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.cejug.yougi.business;

import org.cejug.yougi.entity.ApplicationProperty;
import org.cejug.yougi.entity.EmailMessage;
import org.cejug.yougi.entity.MessageHistory;
import java.util.*;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import org.cejug.yougi.entity.Properties;

/**
 * Centralizes the posting of all email messages sent by the system and manage
 * the history of those messages. Each kind of message has a method, which can
 * be involked from different parts of the business logic.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class MessengerBean {

    @Resource(name = "java:/mail/yougi")
    private Session mailSession;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    @EJB
    private MessageHistoryBean messageHistoryBean;

    /**
     * If the application is configured to send emails, it sends the email message
     * based on the message template. The message is saved in the history and an
     * attempt to send the message is done. If successful the historical message
     * is set as sent, otherwise the message is set as not sent and new attempts
     * will be carried out later until the message is successfully sent.
     * @param emailMessage The message to be sent.
     */
    public void sendEmailMessage(EmailMessage emailMessage) throws MessagingException {
        ApplicationProperty appProp = applicationPropertyBean.findApplicationProperty(Properties.SEND_EMAILS);
        if(!appProp.sendEmailsEnabled()) {
            return;
        }

        MessagingException messagingException = null;

        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch (MessagingException me) {
            messagingException = me;
        }

        List<MessageHistory> messagesHistory = MessageHistory.createHistoricMessages(emailMessage);
        for(MessageHistory messageHistory: messagesHistory) {
            if(messagingException == null) {
                messageHistory.setMessageSent(Boolean.TRUE);
                messageHistory.setDateSent(Calendar.getInstance().getTime());
            }
            else {
                messageHistory.setMessageSent(Boolean.FALSE);
            }
            messageHistoryBean.save(messageHistory);
        }

        if(messagingException != null) {
            throw messagingException;
        }
    }
}