package org.cejug.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.ApplicationProperty;

@Stateless
@LocalBean
public class ApplicationPropertyBsn {

    @PersistenceContext
    EntityManager em;

    @SuppressWarnings("unchecked")
	public Map<String, String> findApplicationProperties() {
        Map<String, String> propertiesMap = new HashMap<String, String>();
        List<ApplicationProperty> properties = em.createQuery("select ap from ApplicationProperty ap").getResultList();
        for(ApplicationProperty property: properties) {
            propertiesMap.put(property.getPropertyKey(), property.getPropertyValue());
        }
        return propertiesMap;
    }

    @SuppressWarnings("unchecked")
    public void save(Map<String, String> properties) {
        List<ApplicationProperty> existingProperties = em.createQuery("select ap from ApplicationProperty ap").getResultList();
        for(ApplicationProperty property: existingProperties) {
            property.setPropertyValue(properties.get(property.getPropertyKey()));
            em.merge(property);
        }
    }
}