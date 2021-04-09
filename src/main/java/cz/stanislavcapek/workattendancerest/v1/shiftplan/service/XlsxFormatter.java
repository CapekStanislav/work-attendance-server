package cz.stanislavcapek.workattendancerest.v1.shiftplan.service;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Pomocná třída zodpovědná za formátování souboru XLSX.
 *
 * @author Stanislav Čapek
 */
public class XlsxFormatter {
    private final XSSFWorkbook workbook;

    public XlsxFormatter(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public XSSFCellStyle getHeadingStyle() {
        XSSFCellStyle heading = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        final short heightInPoints = 15;

        font.setFontHeightInPoints(heightInPoints);
        font.setBold(true);
        heading.setFont(font);
        heading.setAlignment(HorizontalAlignment.CENTER);
        return heading;
    }

    /**
     * Vytvoří styl hlavičky. Ohraničení H,S,L,P, zalamování textu, umístění textu na střed
     *
     * @return styl hlavičky tabulky
     */
    public XSSFCellStyle getHeaderStyle() {
        XSSFCellStyle header = workbook.createCellStyle();
        XSSFFont fontHeader = workbook.createFont();
        final short heightInPoints = 10;

        fontHeader.setFontHeightInPoints(heightInPoints);
        fontHeader.setBold(true);
        header.setFont(fontHeader);
        header.setBorderTop(BorderStyle.THICK);
        header.setBorderBottom(BorderStyle.THICK);
        header.setBorderLeft(BorderStyle.THIN);
        header.setBorderRight(BorderStyle.THIN);
        header.setWrapText(true);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        return header;
    }

    /**
     * Vytvoří styl zarovnání textu na střed.
     *
     * @return zarovnání textu na střed
     */
    public XSSFCellStyle getCenterStyle() {
        XSSFCellStyle center = workbook.createCellStyle();
        center.setAlignment(HorizontalAlignment.CENTER);
        center.setVerticalAlignment(VerticalAlignment.CENTER);
        return center;
    }

    /**
     * Vytvoří styl obarvené buňky s textem na středu
     *
     * @return barevné buňky s textem na středu
     */
    public XSSFCellStyle getCenterColorStyle() {
        XSSFCellStyle color = workbook.createCellStyle();
//        color.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
        color.setFillPattern(FillPatternType.LESS_DOTS);
        color.setBorderTop(BorderStyle.THIN);
        color.setBorderBottom(BorderStyle.THIN);
        color.setBorderLeft(BorderStyle.THIN);
        color.setBorderRight(BorderStyle.THIN);
        color.setAlignment(HorizontalAlignment.CENTER);
        color.setVerticalAlignment(VerticalAlignment.BOTTOM);

        return color;
    }

    /**
     * Vytvoří styl ohraničené buňky bez barvy, zarovnání textu na střed
     *
     * @return bezbarvá buňka s ohraničením, zarovnání textu na střed
     */
    public XSSFCellStyle getCenterBorderNoColorStyle() {
        XSSFCellStyle centerNoColor = workbook.createCellStyle();
        centerNoColor.setBorderTop(BorderStyle.THIN);
        centerNoColor.setBorderBottom(BorderStyle.THIN);
        centerNoColor.setBorderLeft(BorderStyle.THIN);
        centerNoColor.setBorderRight(BorderStyle.THIN);
        centerNoColor.setAlignment(HorizontalAlignment.CENTER);
        centerNoColor.setVerticalAlignment(VerticalAlignment.BOTTOM);

        return centerNoColor;
    }

    /**
     * Vytvoří styl bezbarvé buňky s ohraničením, zarovnání doleva.
     *
     * @return bezbarvá buňka s ohraničením, zarovnání textu doleva
     */
    public XSSFCellStyle getBorderNoColorStyle() {
        XSSFCellStyle borderNoColor = workbook.createCellStyle();
        borderNoColor.setBorderTop(BorderStyle.THIN);
        borderNoColor.setBorderBottom(BorderStyle.THIN);
        borderNoColor.setBorderLeft(BorderStyle.THIN);
        borderNoColor.setBorderRight(BorderStyle.THIN);
        return borderNoColor;
    }

    /**
     * Vytvoří styl bezbarvé buňky s dolním ohraničením.
     *
     * @return bezbarvá buňka s dolním ohraničením
     */
    public XSSFCellStyle getBorderBottomStyle() {
        XSSFCellStyle borderBottom = workbook.createCellStyle();
        borderBottom.setBorderBottom(BorderStyle.THIN);
        return borderBottom;
    }
}
