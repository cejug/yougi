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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message template with variables to be fulfilled with object attributes.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Entity
@Table(name = "message_template")
public class MessageTemplate implements Serializable, Identified {

    private static final String VAR_PATTERN = "\\#\\{([a-z][a-zA-Z_0-9]*(\\.)?)+\\}";

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Transient
    private Map<String, Object> variablesValues;

    public MessageTemplate() {
        this.variablesValues = new HashMap<>();
    }

    public MessageTemplate(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTruncatedBody() {
        if (this.body.length() < 200) {
            return this.body;
        } else {
            return this.body.substring(0, 200);
        }
    }

    public void setVariable(String variable, Object value) {
        this.variablesValues.put(variable, value);
    }

    public EmailMessage buildEmailMessage() {
        EmailMessage emailMessage = new EmailMessage();
        String subject = this.title;
        String message = this.body;

        Pattern pattern = Pattern.compile(VAR_PATTERN);

        List<String> variables = findVariables(pattern, this.getTitle());
        Object value;
        for(String variable: variables) {
            variable = variable.substring(2, variable.length() - 1);
            value = this.variablesValues.get(variable);
            if(value != null) {
                subject = subject.replace("#{" + variable + "}", this.variablesValues.get(variable).toString());
            }
        }
        emailMessage.setSubject(subject);

        variables = findVariables(pattern, this.getBody());
        for(String variable: variables) {
            variable = variable.substring(2, variable.length() - 1);
            value = this.variablesValues.get(variable);
            if(value != null) {
                message = message.replace("#{" + variable + "}", this.variablesValues.get(variable).toString());
            }
        }
        emailMessage.setBody(message);

        return emailMessage;
    }

    private List<String> findVariables(Pattern pattern, CharSequence charSequence) {
        Matcher m = pattern.matcher(charSequence);
        List<String> matches = new ArrayList<>();
        while (m.find()) {
            matches.add(m.group());
        }
        return matches;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MessageTemplate other = (MessageTemplate) obj;
        if (this.id == null ? other.id != null : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.title;
    }
}