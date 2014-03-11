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
package org.cejug.yougi.business;

import javax.persistence.EntityManager;
import org.cejug.yougi.entity.EntitySupport;
import org.cejug.yougi.entity.Identified;

/**
 * Implements basic operations
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 * @param <T> Any entity class that implements Identified.
 */
public abstract class AbstractBean<T extends Identified> {

    private final Class<T> entityClass;

    public AbstractBean(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    /**
     * Save an entity instance on the database. The Id of the entity should
     * support a UUID string because this method will set the id as a UUID
     * string if the id is not defined yet.
     * @param entity Any entity class that implements Identified.
     */
    public T save(T entity) {
        if(EntitySupport.INSTANCE.isIdNotValid(entity)) {
            entity.setId(EntitySupport.INSTANCE.generateEntityId());
            getEntityManager().persist(entity);
        }
        else {
            entity = getEntityManager().merge(entity);
        }
        return entity;
    }

    public void remove(String id) {
        getEntityManager().remove(find(id));
    }

    public T find(String id) {
        return getEntityManager().find(this.entityClass, id);
    }
}
