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

import org.cejug.yougi.entity.ApplicationProperty;
import org.cejug.yougi.entity.Properties;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@Stateless
public class ApplicationPropertyBean {

    private static final Logger LOGGER = Logger.getLogger(ApplicationPropertyBean.class.getSimpleName());

    @PersistenceContext
    private EntityManager em;

    public Map<String, String> findApplicationProperties() {
        Map<String, String> propertiesMap = new HashMap<>();
        List<ApplicationProperty> properties = em.createQuery("select ap from ApplicationProperty ap", ApplicationProperty.class).getResultList();
        for(ApplicationProperty property: properties) {
            propertiesMap.put(property.getPropertyKey(), property.getPropertyValue());
        }

        // If there is no property in the database, it creates all properties according to the enumeration Properties.
        if(propertiesMap.isEmpty()) {
            Properties[] props = Properties.values();
            for(int i = 0;i < props.length;i++) {
                propertiesMap.put(props[i].getKey(), props[i].getDefaultValue());
            }
            create(propertiesMap);
        } else if(Properties.values().length > propertiesMap.size()) {
            // If there is more properties in the enumeration than in the database, then additional enumerations are persisted.
            Properties[] props = Properties.values();
            for(int i = 0;i < props.length;i++) {
                if(!propertiesMap.containsKey(props[i].getKey())) {
                    propertiesMap.put(props[i].getKey(), props[i].getDefaultValue());
                    create(props[i].getKey(), props[i].getDefaultValue());
                }
            }
        } else if(Properties.values().length < propertiesMap.size()) {
            // If there is more persisted properties than in the enumeration, then exceding properties are removed.
            // entries from database
            Set<Map.Entry<String, String>> propEntries = propertiesMap.entrySet();

            Iterator<Map.Entry<String, String>> iProps = propEntries.iterator();
            Map.Entry<String, String> entry;
            Properties[] props = Properties.values();
            while(iProps.hasNext()) {
                entry = iProps.next();
                for(int i = 0; i < props.length; i++) {
                    if(!entry.getKey().equals(props[i].getKey())) {
                        remove(entry.getKey());
                    }
                }
            }
        }
        return propertiesMap;
    }

    /**
     * Returns the ApplicationProperty that corresponds to the informed enum
     * property. If the ApplicationProperty does not exist, then it creates one
     * with the default value.
     */
    public ApplicationProperty findApplicationProperty(Properties properties) {
        ApplicationProperty applicationProperty;
        try {
            applicationProperty = (ApplicationProperty)em.createQuery("select ap from ApplicationProperty ap where ap.propertyKey = :key")
                                                                         .setParameter("key", properties.getKey())
                                                                         .getSingleResult();
        } catch(NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);
            Map<String, String> applicationProperties = findApplicationProperties();
            String key = properties.getKey();
            applicationProperty = new ApplicationProperty(key, applicationProperties.get(key));
        }
        return applicationProperty;
    }

    public String getPropertyValue(Properties properties) {
        ApplicationProperty applicationProperty = findApplicationProperty(properties);
        return applicationProperty.getPropertyValue();
    }

    public void save(Map<String, String> properties) {
        List<ApplicationProperty> existingProperties = em.createQuery("select ap from ApplicationProperty ap", ApplicationProperty.class).getResultList();
        String value;
        for(ApplicationProperty property: existingProperties) {
            value = properties.get(property.getPropertyKey());
            property.setPropertyValue(value);
            em.merge(property);
        }
    }

    private void create(Map<String, String> properties) {
        Set<Map.Entry<String, String>> props = properties.entrySet();
        Iterator<Map.Entry<String, String>> iProps = props.iterator();
        ApplicationProperty appProp;
        Map.Entry<String, String> entry;
        while(iProps.hasNext()) {
            entry = iProps.next();
            appProp = new ApplicationProperty(entry.getKey(), entry.getValue());
            em.persist(appProp);
        }
    }

    private void create(String key, String value) {
        ApplicationProperty appProp = new ApplicationProperty(key, value);
        em.persist(appProp);
    }

    private void remove(String key) {
        ApplicationProperty applicationProperty = em.find(ApplicationProperty.class, key);
        em.remove(applicationProperty);
    }
}