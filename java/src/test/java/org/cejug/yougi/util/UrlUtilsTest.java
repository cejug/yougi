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

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Felipe W. M. Martins - https://github.com/felipewmartins
 */
public class UrlUtilsTest {
    @Test
    public void testSetProtocol() throws Exception {
    	Assert.assertEquals("http://newurltest.com", UrlUtils.INSTANCE.setProtocol("newurltest.com"));

    }

    @Test
    public void testRemoveProtocol() throws Exception {
    	Assert.assertEquals("newurltest.com", UrlUtils.INSTANCE.removeProtocol("http://newurltest.com"));
    	Assert.assertEquals("newurltest.com", UrlUtils.INSTANCE.removeProtocol("https://newurltest.com"));

    }

    @Test
    public void testConcatUrlFragment() throws Exception {
    	Assert.assertEquals("http://newurltest.com/yougi", UrlUtils.INSTANCE.concatUrlFragment("http://newurltest.com/", "/yougi"));
    	Assert.assertEquals("http://newurltest.com/yougi", UrlUtils.INSTANCE.concatUrlFragment("http://newurltest.com/", "yougi"));
    	Assert.assertEquals("http://newurltest.com/yougi", UrlUtils.INSTANCE.concatUrlFragment("http://newurltest.com", "/yougi"));
    	Assert.assertEquals("http://newurltest.com/yougi", UrlUtils.INSTANCE.concatUrlFragment("http://newurltest.com", "yougi"));

    }

    @Test
    public void testIsRelative() throws Exception {
    	Assert.assertFalse(UrlUtils.INSTANCE.isRelative("http://newurltest.com"));
    	Assert.assertTrue(UrlUtils.INSTANCE.isRelative("newurltest.com"));

    }
}
