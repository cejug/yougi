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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Felipe W. M. Martins - https://github.com/felipewmartins
 */
public class TextUtilsTest {
	Date date;
	SimpleDateFormat sdf, sdft;
	
	@Before
	public void setUp(){
		date = new Date();
		sdf = new SimpleDateFormat("HH:mm:ss");
		sdft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	}
	
    @Test
    public void testCapitalizeFirstCharWords() throws Exception {
    	Assert.assertEquals("First Char", TextUtils.INSTANCE.capitalizeFirstCharWords("first char"));

    }

    @Test
    public void testGetFormattedDate() throws Exception {
    	Assert.assertEquals(new SimpleDateFormat("dd-M-yyyy").format(date), TextUtils.INSTANCE.getFormattedDate(date, "dd-M-yyyy"));

    }

    @Test
    public void testGetFormattedTime() throws Exception {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	Assert.assertEquals(sdf.format(date), TextUtils.INSTANCE.getFormattedTime(date, "HH:mm:ss", "UTC"));

    }

    @Test
    public void testGetFormattedDateTime() throws Exception {
        sdft.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
    	Assert.assertEquals(sdft.format(date), TextUtils.INSTANCE.getFormattedTime(date, "yyyy/MM/dd HH:mm:ss", "GMT-8:00"));


    }
}
