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

import org.junit.Assert;
import org.junit.Test;

/**
 * @Author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class EntitySupportTest {
    @Test
    public void testGenerateEntityId() throws Exception {
        Assert.assertEquals("An id should have exactly 32 characters", EntitySupport.INSTANCE.generateEntityId().length(), 32);
        Assert.assertFalse("An id should not contain the character '-'", EntitySupport.INSTANCE.generateEntityId().contains("-"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsIdNotValid() throws Exception {
        EntitySupport.INSTANCE.isIdNotValid(null);
    }

    @Test
    public void testIsIdValid() throws Exception {
        Assert.assertFalse("Id is invalid", EntitySupport.INSTANCE.isIdValid("l"));
    }
}
