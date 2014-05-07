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

import org.cejug.yougi.entity.City;
import org.cejug.yougi.entity.Country;
import org.cejug.yougi.entity.Province;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class CityBean extends AbstractBean<City> {

    @PersistenceContext
    private EntityManager em;

    public CityBean() {
        super(City.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<City> findAll() {
        return em.createQuery("select c from City c order by c.country.name, c.name asc",City.class)
                 .getResultList();
    }

    public List<City> findValidatedCities() {
        return em.createQuery("select c from City c where c.valid = :valid", City.class)
                 .setParameter("valid", true)
                 .getResultList();
    }

    public List<City> findByCountry(Country country, Boolean includingInvalids) {
        if(includingInvalids) {
            return em.createQuery("select c from City c where c.country = :country order by c.name asc", City.class)
                 .setParameter("country", country)
                 .getResultList();
        } else {
            return em.createQuery("select c from City c where c.country = :country and c.valid = :valid order by c.name asc", City.class)
                 .setParameter("country", country)
                 .setParameter("valid", Boolean.TRUE)
                 .getResultList();
        }
    }

    public List<City> findByProvince(Province province, Boolean includingInvalids) {
        if(includingInvalids) {
            return em.createQuery("select c from City c where c.province = :province order by c.name asc", City.class)
                 .setParameter("province", province)
                 .getResultList();
        } else {
            return em.createQuery("select c from City c where c.province = :province and c.valid = :valid order by c.name asc", City.class)
                 .setParameter("province", province)
                 .setParameter("valid", Boolean.TRUE)
                 .getResultList();
        }
    }

    public List<City> findStartingWith(String initials) {
        return em.createQuery("select c from City c where c.name like '"+ initials +"%' order by c.name", City.class).getResultList();
    }
}