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

import org.cejug.yougi.business.UserAccountBean;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.exception.BusinessLogicException;
import org.cejug.yougi.util.StringUtils;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class EmailConfirmationServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(EmailConfirmationServlet.class.getSimpleName());

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBean userAccountBean;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // Builds the header of message.
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>UG - Email Confirmation Failure</title>");
        sb.append("<link href=\"/ug/resources/css/default_theme.css\" rel=\"stylesheet\" type=\"text/css\"/>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h1>Email Confirmation Failure</h1>");

        // Common variables.
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        String confirmationCode = request.getParameter("code");

        if (StringUtils.INSTANCE.isNullOrBlank(confirmationCode)) {
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.println(sb.toString());
                out.println("<p>This email confirmation is invalid. Check if the address on the browser coincides with the address sent by email.</p>");
                out.println("<p><a href=\"" + scheme + "://" + serverName + (serverPort != 80 ? ":" + serverPort : "") + (contextPath.isEmpty() ? "" : contextPath) + "\">Go to Homepage</a>.");
                out.println("</body>");
                out.println("</html>");
            } finally {
                if(out != null) {
                    out.close();
                }
            }
        }
        try {
            UserAccount userAccount = userAccountBean.confirmUser(confirmationCode);
            if(userAccount != null) {
                response.sendRedirect("login.xhtml");
            } else {
                throw new BusinessLogicException("User account is null");
            }
        } catch (Exception e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.println(sb.toString());
                out.println("<p>This email confirmation '"+ confirmationCode +"' is invalid.</p>");
                out.println("<p class=\"alertMessage\">Cause: " + (e.getCause() != null ? e.getCause().getMessage(): e.getMessage()) + "</p>");
                out.println("<p>Check if the address on the browser coincides with the address sent my email.</p>");
                out.println("<p><a href=\"" + scheme + "://" + serverName + (serverPort != 80 ? ":" + serverPort : "") + (contextPath.isEmpty() ? "" : contextPath) + "\">Go to website</a>");
                out.println("</body>");
                out.println("</html>");
            } finally {
                if(out != null) {
                    out.close();
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}