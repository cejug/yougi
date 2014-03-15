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
package org.cejug.yougi.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import org.cejug.yougi.business.CityBean;
import org.cejug.yougi.business.CountryBean;
import org.cejug.yougi.business.ProvinceBean;
import org.cejug.yougi.business.TimezoneBean;
import org.cejug.yougi.entity.City;
import org.cejug.yougi.entity.Country;
import org.cejug.yougi.entity.Province;
import org.cejug.yougi.entity.Timezone;

/**
 * This class is used to manage the update of the fields country, province and
 * city, based on the selection of the user. When the user selects the country,
 * its provinces and cities are listed in the respective fields. When the user
 * selects a province, its cities are listed in the respective field. This class
 * should be used every time at least 2 of the location fields are presented to
 * the user.
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 *
 */
@ManagedBean
@SessionScoped
public class LocationMBean implements Serializable {

    static final Logger LOGGER = Logger.getLogger(LocationMBean.class.getName());

    @EJB
    private CountryBean countryBean;

    @EJB
    private ProvinceBean provinceBean;

    @EJB
    private CityBean cityBean;

    @EJB
    private TimezoneBean timezoneBean;

    private List<Country> countries;

    private List<Province> provinces;

    private List<City> cities;

    private List<Timezone> timezones;

    private String selectedCountry;

    private String selectedProvince;

    private String selectedCity;

    private String selectedTimeZone;

    private String cityNotListed;

    private boolean initialized;

    public List<Country> getCountries() {
        if(this.countries == null) {
            this.countries = countryBean.findCountries();
        }
        return this.countries;
    }

    public List<Province> getProvinces() {
        if (this.selectedCountry != null) {
            Country country = new Country(selectedCountry);
            this.provinces = provinceBean.findByCountry(country);
            return this.provinces;
        } else {
            return null;
        }
    }

    public List<City> getCities() {
        if (selectedCountry != null && selectedProvince == null) {
            Country country = new Country(selectedCountry);
            this.cities = cityBean.findByCountry(country, false);
        } else if (selectedProvince != null) {
            Province province = new Province(selectedProvince);
            this.cities = cityBean.findByProvince(province, false);
        }
        return this.cities;
    }

    public List<Timezone> getTimezones() {
        if(this.timezones == null) {
            this.timezones = timezoneBean.findTimezones();
        }
        return this.timezones;
    }

    public List<String> findCitiesStartingWith(String initials) {
        List<City> cits = cityBean.findStartingWith(initials);
        List<String> citiesStartingWith = new ArrayList<>();
        for (City city : cits) {
            citiesStartingWith.add(city.getName());
        }
        return citiesStartingWith;
    }

    public String getCityNotListed() {
        return cityNotListed;
    }

    /**
     * @return an instance of City not registered yet, according to the
     * parameters informed by the user.
     */
    public City getNotListedCity() {
        City newCity = null;
        if (this.cityNotListed != null && !this.cityNotListed.isEmpty()) {
            newCity = new City(null, this.cityNotListed);
            newCity.setCountry(getCountry());
            newCity.setProvince(getProvince());
            newCity.setValid(false);
        }
        return newCity;
    }

    public void setCityNotListed(String cityNotListed) {
        this.cityNotListed = cityNotListed;
    }

    public Country getCountry() {
        if (this.selectedCountry != null) {
            return countryBean.findCountry(this.selectedCountry);
        } else {
            return null;
        }
    }

    public Province getProvince() {
        if (this.selectedProvince != null && !this.selectedProvince.isEmpty()) {
            return provinceBean.find(this.selectedProvince);
        } else {
            return null;
        }
    }

    public City getCity() {
        if (this.selectedCity != null && !this.selectedCity.isEmpty()) {
            return cityBean.find(this.selectedCity);
        } else {
            return null;
        }
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
        this.selectedProvince = null;
        this.selectedCity = null;
    }

    public String getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(String selectedProvince) {
        this.selectedProvince = selectedProvince;
        this.selectedCity = null;
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getSelectedTimeZone() {
        return selectedTimeZone;
    }

    public void setSelectedTimeZone(String selectedTimeZone) {
        this.selectedTimeZone = selectedTimeZone;
    }

    public void initialize() {
        this.countries = null;
        this.provinces = null;
        this.cities = null;

        this.selectedCountry = null;
        this.selectedProvince = null;
        this.selectedCity = null;

        this.initialized = true;

        LOGGER.info("LocationBean initialized for a new use.");
    }

    public boolean isInitialized() {
        return this.initialized;
    }
}