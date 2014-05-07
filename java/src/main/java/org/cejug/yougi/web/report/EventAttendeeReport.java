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
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.cejug.yougi.event.entity.Attendee;

import java.util.List;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
public class EventAttendeeReport {

    private Document document;

    public EventAttendeeReport(Document document) throws DocumentException {
        this.document = document;

        if (!this.document.isOpen()) {
            this.document.open();
        }

        init();
    }

    private void init() throws DocumentException {
    }

    public void printReport(List<Attendee> attendees) throws DocumentException {
        float[] columnSizes = {20, 220, 220, 60};
        PdfPTable table = new PdfPTable(columnSizes.length);
        table.setLockedWidth(true);
        table.setTotalWidth(columnSizes);

        PdfPCell headerCell = new PdfPCell(new Phrase("Yougi"));
        headerCell.setColspan(4);
        headerCell.setBackgroundColor(BaseColor.ORANGE);
        headerCell.setPadding(3);
        table.addCell(headerCell);

        table.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);

        PdfPCell checkCell = new PdfPCell(new Phrase(" "));
        checkCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(checkCell);

        PdfPCell productCell = new PdfPCell(new Phrase("Nome"));
        productCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        productCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(productCell);

        PdfPCell currentPurchaseCell = new PdfPCell(new Phrase("Email"));
        currentPurchaseCell.setPadding(3);
        currentPurchaseCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        currentPurchaseCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(currentPurchaseCell);

        PdfPCell previousPurchaseCell = new PdfPCell(new Phrase("Presente"));
        previousPurchaseCell.setPadding(3);
        previousPurchaseCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        previousPurchaseCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(previousPurchaseCell);

        table.getDefaultCell().setBackgroundColor(null);
        table.setHeaderRows(2);

        Font font = new Font(Font.FontFamily.HELVETICA, 9);
        int seq = 1;
        for (Attendee attendee : attendees) {
            table.addCell(new Phrase(String.valueOf(seq++), font));

            table.addCell(new Phrase(attendee.getUserAccount().getFullName(), font));

            table.addCell(new Phrase(attendee.getUserAccount().getEmail(), font));

            table.addCell(" ");
        }

        document.add(table);
    }
}