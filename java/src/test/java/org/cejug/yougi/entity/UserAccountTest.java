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
package org.cejug.yougi.entity;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Felipe W. M. Martins - https://github.com/felipewmartins
 */
public class UserAccountTest {

	private UserAccount user = new UserAccount();

    @Test
    public void testSetFirstName() throws Exception {
    	user.setFirstName("testname");
    	Assert.assertEquals("Testname", user.getFirstName());

    }

    @Test
    public void testGetFullName() throws Exception {
    	user.setFirstName("darth");
    	user.setLastName("vader");
    	Assert.assertEquals("Darth Vader", user.getFullName());

    }

    @Test
    public void testSetUnverifiedEmail() throws Exception {
    	user.setUnverifiedEmail("");
    	Assert.assertNotNull(user.getUnverifiedEmail());
    	user.setUnverifiedEmail("DARTHVADER@TEST.COM");
    	Assert.assertEquals("darthvader@test.com", user.getUnverifiedEmail());

    }

    @Test
    public void testSetEmailAsVerified() throws Exception {
    	user.setUnverifiedEmail(null);
    	user.setEmailAsVerified();
    	Assert.assertEquals(user.getEmail(), user.getUnverifiedEmail());
    }

    @Test
    public void testGetPostingEmail() throws Exception {
    	user.setUnverifiedEmail("DARTHVADER@TEST.COM");
    	Assert.assertEquals("darthvader@test.com", user.getPostingEmail());
    	user.setUnverifiedEmail(null);
    	Assert.assertNull(user.getPostingEmail());


    }

    @Test
    public void testSetWebsite() throws Exception {
    	user.setWebsite("http://teste.org");
    	Assert.assertEquals("teste.org", user.getWebsite());
    	user.setWebsite("https://teste.org");
    	Assert.assertEquals("teste.org", user.getWebsite());
    	user.setWebsite("teste.org");
    	Assert.assertEquals("teste.org", user.getWebsite());
    }

    @Test
    public void testSetTwitter() throws Exception {
    	user.setTwitter(null);
    	Assert.assertNull(user.getTwitter());
    	user.setTwitter("");
    	Assert.assertNull(user.getTwitter());
    	user.setTwitter("@TestTwitter");
    	Assert.assertEquals("TestTwitter", user.getTwitter());

    }

    @Test
    public void testDefineNewConfirmationCode() throws Exception {
    	user.defineNewConfirmationCode();
    	Assert.assertNotNull("This is a confirmation code", user.getConfirmationCode());

    }

    @Test
    public void testResetConfirmationCode() throws Exception {
    	user.defineNewConfirmationCode();
    	user.resetConfirmationCode();
    	Assert.assertNull(user.getConfirmationCode());

    }
}
