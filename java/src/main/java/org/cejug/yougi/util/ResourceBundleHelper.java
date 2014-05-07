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
package org.cejug.yougi.util;

import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsulates the complexity of getting the service bundle from the context.
 * Actually, this is so complex that a better approach should be investigated,
 * or a solution presented to spec leaders.
 *
 * @author Daniel Cunha - danielsoro@gmail.com
 *         Hildeberto Mendonca - http://www.hildeberto.com
 */
public enum ResourceBundleHelper {
    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(ResourceBundleHelper.class.getSimpleName());
    private static final String BUNDLE_NAME = "org.cejug.yougi.web.bundles.Resources";

    private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

    public String getMessage(String key) {
        return getMessageFromResourceBundle(key);
    }

    public String getMessage(String key, Locale locale) {
        this.locale = locale;
        return getMessageFromResourceBundle(key);
    }

    public String getMessage(String key, Object ... params) {
        String message = getMessageFromResourceBundle(key);
        return MessageFormat.format(message, params);
    }

    public String getMessage(String key, Locale locale, Object ... params) {
        this.locale = locale;
        String message = getMessageFromResourceBundle(key);
        return MessageFormat.format(message, params);
    }

    private String getMessageFromResourceBundle(String key) {
        ResourceBundle bundle;
        String message = "";

        try {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, getCurrentLoader(BUNDLE_NAME));
        } catch (MissingResourceException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            return "?";
        }
        if (bundle == null) {
            return "?";
        }
        try {
            message = bundle.getString(key);
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
        }
        return message;
    }

    private ClassLoader getCurrentLoader(Object fallbackClass) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = fallbackClass.getClass().getClassLoader();
        }
        return loader;
    }
}