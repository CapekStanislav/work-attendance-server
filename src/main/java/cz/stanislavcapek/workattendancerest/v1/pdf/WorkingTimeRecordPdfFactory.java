package cz.stanislavcapek.workattendancerest.v1.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Instance třídy {@code WorkingTimeRecordPdfFactory}
 *
 * @author Stanislav Čapek
 */
public class WorkingTimeRecordPdfFactory {
    private static final float PADDING = 50f;
    private static final PDRectangle A4 = PDRectangle.A4;
    private static final float PRINTABLE_AREA = A4.getWidth() - 2 * PADDING;
    private static final int FONT_SIZE_SMALL = 10;
    private static final int FONT_SIZE_NORMAL = 12;
    private static final int FONT_SIZE_LARGE = 16;
    private static final String TITLE = "Evidence pracovní doby";
    private static final String FONTS_CALIBRI_TTF = "fonts/calibri.ttf";
    private static final String FONTS_CALIBRIB_TTF = "fonts/calibrib.ttf";

    private static LocalDate period;
    private static PDFont normalFont;
    private static PDFont boldFont;

    public static PDDocument createRecordPDDocument(RecordDocument model) throws IOException {
        return createRecordPDDocument(model, TITLE);
    }

    public static PDDocument createRecordPDDocument(
            RecordDocument model, String title) throws IOException {
        period = model.getDate(0);
        final float daySize = 40;
        final float timeSize = 40;
        final float typeSize = 76;
        final float numSize = 49;

        // setting up document
        final PDDocument document = new PDDocument();
        PDPage page = new PDPage(A4);
        document.addPage(page);

        // load font
        if (normalFont == null || boldFont == null) {
            final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

            try (final InputStream inputStream = classLoader.getResourceAsStream(FONTS_CALIBRI_TTF)) {
                normalFont = PDType0Font.load(document, inputStream, true);
            }

            try (InputStream inputStream = classLoader.getResourceAsStream(FONTS_CALIBRIB_TTF)) {
                boldFont = PDType0Font.load(document, inputStream, true);
            }
        }

        final Table.TableBuilder builder = Table.builder();

        // setting up the first 4 columns
        final float[] columns = new float[model.getColumnCount()];
        columns[0] = daySize;
        columns[1] = timeSize;
        columns[2] = timeSize;
        columns[3] = typeSize;
        // setting up the remaining
        for (int i = 4; i < model.getColumnCount(); i++) {
            columns[i] = numSize;
        }

        // setting up the table
        builder.addColumnsOfWidth(columns)
                .fontSize(FONT_SIZE_SMALL)
                .font(normalFont)
                .borderColor(Color.BLACK);

        // creation of the table header
        Row.RowBuilder rowBuilder;
        builder.addRow(createTableHeader(model));

        // rows of shifts
        createTableContent(model, builder);

        // creation of the summarization
        builder.addRow(createTableSum(model));

        // repetition of the table footer
//        builder.addRow(createTableHeader(model));

        final List<Table> tablesToDraw = new ArrayList<>();
        tablesToDraw.add(createHeader(model, title));
        tablesToDraw.add(builder.build());
        tablesToDraw.add(createFooter(model, normalFont));
        tablesToDraw.add(createSigning(normalFont));

        float starty = page.getMediaBox().getHeight() - PADDING;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            for (Table table : tablesToDraw) {
                TableDrawer.builder()
                        .page(page)
                        .contentStream(contentStream)
                        .table(table)
                        .startX(PADDING)
                        .startY(starty)
                        .endY(PADDING)
                        .build()
                        .draw();
                starty -= table.getHeight() + PADDING / 2;
            }
        }

        return document;
    }

    private static Row createTableSum(RecordDocument model) {
        Row.RowBuilder rowBuilder = Row.builder();
        rowBuilder
                .font(boldFont)
                .fontSize(12)
                .backgroundColor(Color.LIGHT_GRAY);
        final String total = "Celkem";
        rowBuilder.add(TextCell.builder().text(total).colSpan(4).borderWidth(1).build());

        for (int i = 4; i < model.getColumnCount(); i++) {
            double count = 0;
            for (int r = 0; r < model.getRowCount(); r++) {
                final Object value = model.getValueAt(r, i);
                try {
                    count += Double.parseDouble(value.toString());
                } catch (NumberFormatException ignore) {
                }
            }
            rowBuilder.add(TextCell.builder().text(String.valueOf(count)).borderWidth(1).build());
        }
        return rowBuilder.build();
    }

    private static void createTableContent(RecordDocument model, Table.TableBuilder builder) {
        Row.RowBuilder rowBuilder;
        for (int i = 0; i < model.getRowCount(); i++) {
            rowBuilder = Row.builder();

            final DayOfWeek day = period.withDayOfMonth(i + 1).getDayOfWeek();
            final Color color = day == DayOfWeek.SATURDAY ||
                    day == DayOfWeek.SUNDAY ? Color.lightGray : Color.white;
            rowBuilder.backgroundColor(color);

            for (int n = 0; n < model.getColumnCount(); n++) {
                rowBuilder.add(
                        TextCell.builder()
                                .text(model.getValueAt(i, n).toString())
                                .borderWidth(1)
                                .build()
                );

            }
            builder.addRow(rowBuilder.build());
        }
    }

    private static Row createTableHeader(RecordDocument model) {
        Row.RowBuilder rowBuilder = Row.builder();
        for (int i = 0; i < model.getColumnCount(); i++) {
            rowBuilder.add(
                    TextCell.builder()
                            .text(model.getColumnName(i))
                            .borderWidth(1)
                            .backgroundColor(Color.LIGHT_GRAY)
                            .build()
            );
        }
        return rowBuilder.build();
    }

    private static Table createHeader(RecordDocument model, String title) {
        final Table.TableBuilder builder = Table.builder();

        builder
                .font(normalFont)
                .fontSize(FONT_SIZE_NORMAL)
                .addColumnsOfWidth(PRINTABLE_AREA);

        Row.RowBuilder rowBuilder = Row.builder();

        rowBuilder.add(
                TextCell.builder()
                        .text(title)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .font(boldFont)
                        .fontSize(FONT_SIZE_LARGE)
                        .build()
        );
        builder.addRow(rowBuilder.build());


        rowBuilder = Row.builder();
        final String name = String.format("Jmeno: %s", model.getName());
        rowBuilder.add(TextCell.builder().text(name).build());
        builder.addRow(rowBuilder.build());

        rowBuilder = Row.builder();
        final String period = String.format("Období: %s %d", model.getMonth(), model.getYear());
        rowBuilder.add(TextCell.builder().text(period).build());
        builder.addRow(rowBuilder.build());

        rowBuilder = Row.builder();
        final String fund = String.format("Fond pracovní doby: %.2f", model.getWorkTimeFund());
        rowBuilder.add(TextCell.builder().text(fund).build());
        builder.addRow(rowBuilder.build());

        return builder.build();

    }

    private static Table createFooter(RecordDocument model, PDFont normal) {
        final float cellSize = PRINTABLE_AREA / 3;
        final Table.TableBuilder builder = Table.builder();
        builder
                .font(normal)
                .fontSize(FONT_SIZE_NORMAL)
                .addColumnsOfWidth(cellSize, cellSize, cellSize);

        final double workTimeFund = model.getWorkTimeFund();
        final double lastMonthHours = model.getLastMonthHours();
        final double nextMonthHours = model.getNextMonthHours();

        final String fund = String.format("Fond pracovní doby: %.2f", workTimeFund);
        final String last = String.format("převod z minulého měsíce: %.2f", lastMonthHours);
        final String next = String.format("převod do dalšího měsíce: %.2f", nextMonthHours);

        builder.addRow(Row.builder()
                .add(TextCell.builder().text(fund).build())
                .add(TextCell.builder().text(last).build())
                .add(TextCell.builder().text(next).build())
                .build()
        );

        return builder.build();
    }

    private static Table createSigning(PDFont normal) {
        final float cellSize = A4.getWidth() / 4;

        final Table.TableBuilder builder = Table.builder();

        builder
                .fontSize(FONT_SIZE_NORMAL)
                .font(normal)
                .addColumnsOfWidth(cellSize, cellSize, cellSize, cellSize);

        builder.addRow(Row.builder()
                .add(TextCell.builder().text("Vystavil:").colSpan(2).build())
                .add(TextCell.builder().text("Zaměstnanec:").colSpan(2).build())
                .build()
        );

        builder.addRow(Row.builder()
                .add(TextCell.builder().text("").colSpan(4).build())
                .build()
        );

        builder.addRow(Row.builder()
                .add(TextCell.builder().text("Vydáno stravenek: ").colSpan(4).build())
                .build()
        );

        return builder.build();
    }
}
