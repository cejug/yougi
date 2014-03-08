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
package org.cejug.yougi.knowledge.business;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.business.AbstractBean;
import org.cejug.yougi.business.ApplicationPropertyBean;
import org.cejug.yougi.entity.ApplicationProperty;
import org.cejug.yougi.entity.Properties;
import org.cejug.yougi.knowledge.entity.MailingList;
import org.cejug.yougi.knowledge.entity.MailingListMessage;
import org.cejug.yougi.knowledge.entity.MailingListSubscription;
import org.cejug.yougi.entity.EntitySupport;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class MailingListMessageBean extends AbstractBean<MailingListMessage> {

    @Resource(name = "java:/mail/yougi")
    private Session mailSession;

    @EJB
    private MailingListBean mailingListBean;

    @EJB
    private SubscriptionBean subscriptionBean;

    @EJB
    private ApplicationPropertyBean applicationPropertyBean;

    static final Logger LOGGER = Logger.getLogger(MailingListMessageBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    public MailingListMessageBean() {
        super(MailingListMessage.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    //@Schedule(hour="*/1",persistent=false) // Production
    //@Schedule(hour="*",minute="*/2",persistent=false) // Development
    public void retrieveMailingListMessages() {

        try {
            LOGGER.log(Level.INFO, "Start retrieving of emails...");
            ApplicationProperty appProp = applicationPropertyBean.findApplicationProperty(Properties.EMAIL_SERVER_TYPE);
            Store store = mailSession.getStore(appProp.getPropertyValue());
            store.connect();
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] message = folder.getMessages();
            MailingListMessage mailingListMessage;
            String from;
            List<MailingList> mailingLists;

            for (int i = 0, n = message.length; i < n; i++) {
                /* Stores in the database only those messages that were sent to
                 * a registered mailing list. */
                mailingLists = figureOutMailingLists(message[i].getAllRecipients());
                if(mailingLists == null || mailingLists.isEmpty()) {
                    continue;
                }

                /* For each mailing list in the recipient of the message a new
                   message is created and saved. */
                for(MailingList mailingList: mailingLists) {
                    mailingListMessage = new MailingListMessage();
                    mailingListMessage.setId(EntitySupport.INSTANCE.generateEntityId());
                    mailingListMessage.setMailingList(mailingList);
                    mailingListMessage.setSubject(message[i].getSubject());

                    /* Get the email address of the 'from' field and set the sender. */
                    from = message[i].getFrom()[0].toString();
                    if(from.contains("<")) {
                        from = from.substring(from.indexOf('<') + 1, from.indexOf('>'));
                    }
                    from = from.toLowerCase();
                    MailingListSubscription mailingListSubscription = subscriptionBean.findMailingListSubscription(mailingList, from);
                    mailingListMessage.setSender(mailingListSubscription);

                    /* This part tries to get the full content of the message to
                    * store in the database. For that, a simple OutputStream
                    * implementation writes the whole content of the message in a
                    * string, which defines the attribute body of the
                    * mailingListMessage object. */
                    OutputStream output = new OutputStream() {
                        private StringBuilder string = new StringBuilder();

                        @Override
                        public void write(int b) throws IOException {
                            this.string.append((char) b );
                        }

                        @Override
                        public String toString() {
                            return this.string.toString();
                        }
                    };
                    message[i].writeTo(output);
                    mailingListMessage.setBody(output.toString());

                    mailingListMessage.setDateReceived(message[i].getReceivedDate());

                    em.persist(mailingListMessage);

                    LOGGER.log(Level.INFO, "Message -{0}- sent by -{1}- saved.", new Object[]{mailingListMessage.getSubject(),mailingListMessage.getSender()});
                }

                // Once persisted, the message is flagged to be deleted from the server.
                message[i].setFlag(Flags.Flag.DELETED, true);
            }
            // All messages flagged to be deleted will actually be deleted.
            folder.close(true);
            store.close();
            LOGGER.log(Level.INFO, "Email retrieval ended.");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /** Check the recipients to detect which mailing lists were in the recipient
     * of the message. */
    private List<MailingList> figureOutMailingLists(Address[] extendedListAddresses) {
        String listAddress;
        List<MailingList> mailingLists = new ArrayList<>();
        MailingList mailingList;
        for(int i = 0;i < extendedListAddresses.length;i++) {
            listAddress = extendedListAddresses[i].toString();
            if(listAddress.contains("<")) {
                listAddress = listAddress.substring(listAddress.indexOf('<') + 1, listAddress.indexOf('>'));
            }
            listAddress = listAddress.toLowerCase();
            mailingList = mailingListBean.findMailingListByEmail(listAddress);
            if(mailingList != null) {
                mailingLists.add(mailingList);
            }
        }
        return mailingLists;
    }
}