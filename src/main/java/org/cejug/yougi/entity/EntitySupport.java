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

import java.util.UUID;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public enum EntitySupport {

    INSTANCE;

    /**
     * @return Returns a 32 characteres string to be used as id of entities that
     * implements the interface org.cejug.persistence.Identified.
     */
    public final String generateEntityId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "").toUpperCase();
    }

    /**
     * Verifies whether the id of an identified entity is not valid to persist
     * in the database.
     * @param identified entity class that implements the interface
     * org.cejug.persistence.Identified.
     * @return true if the id is not valid.
     */
    public final boolean isIdNotValid(Identified identified) {
        if(identified == null) {
            throw new IllegalArgumentException("Identified entity is null");
        }
        // TODO: lançar uma excessão se o parâmetro for nulo.
        if(identified.getId() == null || identified.getId().isEmpty()) {
            return true;
        }
        return false;
    }
}