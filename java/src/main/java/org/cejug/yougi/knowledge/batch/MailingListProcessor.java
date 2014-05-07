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
package org.cejug.yougi.knowledge.batch;

import org.cejug.yougi.knowledge.entity.MailingListMessage;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.mail.Message;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Named
@Dependent
public class MailingListProcessor implements ItemProcessor {

    @Override
    public MailingListMessage processItem(Object msg) throws Exception {
        Message message = (Message) msg;

        MailingListMessage mailingListMessage = new MailingListMessage();
        mailingListMessage.setSubject(message.getSubject());
        mailingListMessage.setContentType(message.getContentType());

        return mailingListMessage;
    }
}