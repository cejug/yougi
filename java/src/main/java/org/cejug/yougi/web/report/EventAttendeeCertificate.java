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
package org.cejug.yougi.web.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.cejug.yougi.event.entity.Attendee;
import org.cejug.yougi.util.TextUtils;

import java.io.IOException;

/**
 *
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class EventAttendeeCertificate extends PdfPageEventHelper {

    private Document document;
    private PdfImportedPage page;
    private PdfContentByte canvas;

    public EventAttendeeCertificate(Document document) throws DocumentException {
        this.document = document;
        if (!this.document.isOpen()) {
            this.document.open();
        }
    }

    public void generateCertificate(Attendee attendee) throws DocumentException {

        Font helvetica = new Font(Font.FontFamily.HELVETICA, 12);
        BaseFont baseFont = helvetica.getCalculatedBaseFont(false);
        canvas.saveState();
        canvas.beginText();
        canvas.setFontAndSize(baseFont, 12);
        canvas.showTextAligned(Element.ALIGN_LEFT, "Validation code: " + attendee.getCertificateCode() + " ( http://www.cejug.org/certificate_validation.xhtml) ", 30, 30, 0);
        canvas.endText();
        canvas.restoreState();

        String[] contentLine = new String[8];
        contentLine[0] = "Certificamos que";
        contentLine[1] = attendee.getCertificateFullname();
        contentLine[2] = "participou do evento";
        contentLine[3] = attendee.getCertificateEvent();
        contentLine[4] = "realizado na";
        contentLine[5] = attendee.getCertificateVenue();
        contentLine[6] = "no dia " + TextUtils.INSTANCE.getFormattedDate(attendee.getCertificateDate(), "dd.MM.yyyy");

        Font normalFont = new Font(Font.FontFamily.HELVETICA, 24);
        Font largeFont = new Font(Font.FontFamily.HELVETICA, 28, Font.FontStyle.BOLD.ordinal());
        Paragraph p;

        for (int i = 0; i < 5; i++) {
            p = new Paragraph(" ", normalFont);
            this.document.add(p);
        }

        Font currentFont = normalFont;
        for (int i = 0; i < contentLine.length; i++) {
            p = new Paragraph(contentLine[i], currentFont);
            p.setAlignment(Element.ALIGN_CENTER);
            this.document.add(p);

            currentFont = currentFont.equals(normalFont) ? largeFont : normalFont;
        }
    }

    public void setCertificateTemplate(PdfWriter writer, String urlTemplate) throws IOException {
        writer.setPageEvent(this);

        PdfReader reader = new PdfReader(urlTemplate);
        page = writer.getImportedPage(reader, 1);
        canvas = writer.getDirectContent();
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        writer.getDirectContentUnder().addTemplate(page, 0, 0);
    }
}