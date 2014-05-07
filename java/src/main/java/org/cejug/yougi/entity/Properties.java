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
public enum Properties {
    CAPTCHA_ENABLED     ("captchaEnabled",     "false"                   ),
    CAPTCHA_PRIVATE_KEY ("captchaPrivateKey",  ""                        ),
    CAPTCHA_PUBLIC_KEY  ("captchaPublicKey",   ""                        ),
    DEFAULT_LANGUAGE    ("language",           "en"                      ),
    EMAIL_HOST          ("emailHost",          ""                        ),
    EMAIL_HOST_PORT     ("emailHostPort",      ""                        ),
    EMAIL_SERVER_TYPE   ("emailServerType",    "pop3"                    ),
    EMAIL_USER          ("emailUser",          ""                        ),
    EMAIL_USER_PASSWORD ("emailUserPassword",  ""                        ),
    FILE_REPOSITORY_PATH("fileRepositoryPath", ""                        ),
    GROUP_NAME          ("groupName",          "Yougi"                   ),
    RECEIVE_EMAILS      ("receiveEmails",      "false"                   ),
    SEND_EMAILS         ("sendEmails",         "false"                   ),
    TIMEZONE            ("timezone",           ""                        ),
    URL                 ("url",                "http://localhost:8080/ug");

    private String key;
    private String defaultValue;

    Properties(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}