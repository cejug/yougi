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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.cejug.yougi.entity.Country;
import org.cejug.yougi.entity.Province;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class ProvinceBean extends AbstractBean<Province> {

    @PersistenceContext
    private EntityManager em;

    public ProvinceBean() {
        super(Province.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Province> findAll() {
        return em.createQuery("select p from Province p order by p.country.name, p.name asc")
                 .getResultList();
    }

    public List<Province> findByCountry(Country country) {
        return em.createQuery("select p from Province p where p.country = :country order by p.name asc")
                 .setParameter("country", country)
                 .getResultList();
    }
}
