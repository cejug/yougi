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


import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.qualifier.UserName;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Daniel Cunha - danielsoro@gmail.com
 */
public class ProducesUtil {

    @Inject
    private UserAccountBean userAccountBean;

    @Inject
    private FacesContext facesContext;

    @Inject
    private HttpServletResponse httpServletResponse;

    @Inject
    private HttpServletRequest httpServletRequest;

    @Produces @RequestScoped
    public FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    @Produces @RequestScoped
    public HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse) facesContext.getExternalContext().getResponse();
    }

    @Produces @Named @UserName
    public String getUserName() {
        return httpServletRequest.getRemoteUser();
    }

    @Produces @Named
    public String getFirstName() {
        String username = getUserName();
        UserAccount userAccount = userAccountBean.findByUsername(username);
        return userAccount == null ? "" : userAccount.getFirstName();
    }
}
