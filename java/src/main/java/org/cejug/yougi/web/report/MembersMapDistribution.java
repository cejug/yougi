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
package org.cejug.yougi.web.report;

import org.cejug.yougi.business.CityBean;
import org.cejug.yougi.entity.City;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class MembersMapDistribution implements Serializable {

    private static final long serialVersionUID = 1L;
    private final MapModel simpleModel;

    @EJB
    private CityBean cityBean;

    public MembersMapDistribution() {
        simpleModel = new DefaultMapModel();
    }

    @PostConstruct
    public void load() {
        List<City> cities = cityBean.findValidatedCities();
        LatLng coord;
        Double latitude = null;
        Double longitude = null;
        for (City city : cities) {
            if (city.getLatitude() != null && !city.getLatitude().isEmpty()) {
                latitude = Double.valueOf(city.getLatitude());
            }

            if (city.getLongitude() != null && !city.getLongitude().isEmpty()) {
                longitude = Double.valueOf(city.getLongitude());
            }

            if (latitude != null && longitude != null) {
                coord = new LatLng(latitude, longitude);
                simpleModel.addOverlay(new Marker(coord, city.getName()));
            }
        }
    }

    public MapModel getSimpleModel() {
        return simpleModel;
    }
}