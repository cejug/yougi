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
package org.cejug.yougi.web.handler;

import org.cejug.yougi.util.ResourceBundleHelper;

import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.inject.Inject;
import java.util.Iterator;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private ExceptionHandler parent;

    @Inject
    private FacesContext context;

    public CustomExceptionHandler() {}

    public CustomExceptionHandler(ExceptionHandler parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return this.parent;
    }

    @Override
    public void handle() {
        ExceptionQueuedEvent event;
        ExceptionQueuedEventContext eventContext;
        Throwable t;
        for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
            event = i.next();
            eventContext = (ExceptionQueuedEventContext) event.getSource();
            t = eventContext.getException();
            if (t instanceof ViewExpiredException) {
                NavigationHandler nav = context.getApplication().getNavigationHandler();
                try {
                    context.getExternalContext().getFlash().put("currentViewId", ResourceBundleHelper.INSTANCE.getMessage("warnCode0004"));
                    nav.handleNavigation(context, null, "/login?faces-redirect=true");
                    context.renderResponse();
                } finally {
                    i.remove();
                }
            }
        }
        getWrapped().handle();
    }
}