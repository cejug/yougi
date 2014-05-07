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

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public enum ContentType {

    TEXT_HTML      ("text/html",       "html"),
    TEXT_PLAIN     ("text/plain",      "txt");

    private final String contentType;
    private final String extension;

    ContentType(String contenuType, String extension) {
        this.contentType = contenuType;
        this.extension = extension;
    }

    @Override
    public String toString() {
        return this.contentType;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isKnownExtension(String extension) {
        boolean exist = false;

        if(TEXT_HTML.getExtension().equals(extension) || TEXT_PLAIN.getExtension().equals(extension)) {
            exist = true;
        }

        return exist;
    }
}