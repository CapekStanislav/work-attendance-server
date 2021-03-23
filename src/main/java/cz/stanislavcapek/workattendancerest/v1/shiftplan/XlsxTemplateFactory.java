/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.stanislavcapek.workattendancerest.v1.shiftplan;

import cz.stanislavcapek.workattendancerest.v1.employee.Employee;
import cz.stanislavcapek.workattendancerest.v1.model.Month;
import cz.stanislavcapek.workattendancerest.v1.model.WorkTimeFund;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída slouží k vytvoření šablony XLSX v přesně daném formátu (plán služeb).
 * Vytvoří jméno pracovního sešitu, s uvedeným rokem a hlavičkou tabulky. Další obsah se odvíjí od použité přetížené
 * metody {@link XlsxTemplateFactory#create()}.
 *
 * @author Stanislav Čapek
 * @version 1.0
 */

@Service
public class XlsxTemplateFactory {
    private static final List<Employee> EMPTY_LIST = new ArrayList<>();
    public static final String TITLE = "Měsíční rozpis služeb - Městská policie";

    /**
     * Vytvoří excelovou šablonu pro plánování služeb. Rozdělena na jednotlivé měsíce v roce. Automaticky generuje
     * dny dle <b>aktuálního roku</b> pro jednotlivé měsíce. Barevně rozliší soboty a neděle.<br>
     * Obsahuje pouze první prázdný řádek, bez vyplnění konkrétního zaměstnance.
     *
     * @return {@link XSSFWorkbook} šablona plánu služeb
     */
    public static XSSFWorkbook create() {
        if (EMPTY_LIST.size() == 0) {
            final Employee emptyEmployee = new Employee(0, "", "");
            EMPTY_LIST.add(emptyEmployee);
        }
        return create(EMPTY_LIST);
    }

    /**
     * Vytvoří excelovou šablonu pro plánování služeb. Rozdělena na jednotlivé měsíce v roce. Automaticky generuje
     * dny dle <b>aktuálního roku</b> pro jednotlivé měsíce. Barevně rozliší soboty a neděle.<br>
     * Vygeneruje řádky pro jednotlivé zaměstnance, předané v parametru.
     *
     * @param employeeList zaměstnanci ke generování
     * @return {@link XSSFWorkbook} šablona plánu služeb
     */
    public static XSSFWorkbook create(List<Employee> employeeList) {
        return create(employeeList, LocalDate.now().getYear());
    }

    /**
     * Vytvoří excelovou šablonu pro plánování služeb. Rozdělena na jednotlivé měsíce v roce. Automaticky generuje
     * dny dle <b>zadaného roku</b> pro jednotlivé měsíce. Barevně rozliší soboty a neděle.<br>
     * Vygeneruje řádky pro jednotlivé zaměstnance, předané v parametru.
     *
     * @param employeeList zaměstnanci ke generování
     * @param year         celé číslo roku
     * @return {@link XSSFWorkbook} šablona plánu služeb
     */
    public static XSSFWorkbook create(List<Employee> employeeList, int year) {
        return getWorkbook(employeeList, year);
    }

    private static XSSFWorkbook getWorkbook(List<Employee> employeeList, int year) {
        LocalDate date = LocalDate.of(year, 1, 1);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XlsxFormatter format = new XlsxFormatter(workbook);
        int toNextMonth = 0;
        final Month[] months = Month.values();
        for (int i = 0; i < months.length; i++) {
            String tempMonth = months[i].getName();

            XSSFSheet sheet = workbook.createSheet(tempMonth);

            // row 0 //
            XSSFRow row = sheet.createRow(0);
            XSSFCell cell;

            cell = row.createCell(0);
            cell.setCellValue(year);
            cell.setCellStyle(format.getCenterStyle());

            cell = row.createCell(3);
            cell.setCellValue(TITLE);
            cell.setCellStyle(format.getHeadingStyle());

            // row 1 //
            // info about employee //
            row = sheet.createRow(1);

            cell = row.createCell(0);
            cell.setCellValue(tempMonth);
            cell.setCellStyle(format.getHeaderStyle());

            cell = row.createCell(1);
            cell.setCellValue("");
            cell.setCellStyle(format.getHeaderStyle());

            cell = row.createCell(2);
            cell.setCellValue("Sl.č.");
            cell.setCellStyle(format.getHeaderStyle());

            // number of days in month //
            for (int j = 3; j < date.lengthOfMonth() + 3; j++) {
                cell = row.createCell(j);
                cell.setCellValue(j - 2);
                cell.setCellStyle(format.getHeaderStyle());
            }

            // info about hours //
            short lastCellNum = row.getLastCellNum();
            String[] info = {"", "má být", "plán", "rozdíl", "převod", "do dal. měsíce"};

            for (int j = 0; j < info.length; j++) {
                cell = row.createCell(lastCellNum + j);
                if (j == info.length - 1) {
                    if (i == 5 || i == 11) {
                        cell.setCellValue("Vyrov. období");
                    } else {
                        cell.setCellValue(info[j]);
                    }
                } else {
                    cell.setCellValue(info[j]);
                }
                cell.setCellStyle(format.getHeaderStyle());
            }

            // row 2 + j //
            for (int j = 0; j < employeeList.size(); j++) {
                Employee employee = employeeList.get(j);
                row = sheet.createRow(2 + j);

                cell = row.createCell(0);
                cell.setCellValue(employee.getFullName());
                cell.setCellStyle(format.getBorderNoColorStyle());

                cell = row.createCell(1);
                cell.setCellValue(employee.getAbbreviation());
                cell.setCellStyle(format.getBorderNoColorStyle());

                cell = row.createCell(2);
                cell.setCellValue(employee.getAssignedId());
                cell.setCellStyle(format.getCenterBorderNoColorStyle());

                LocalDate tempDate = date;
                for (int k = 3; k < date.lengthOfMonth() + 3; k++) {
                    cell = row.createCell(k);
                    if (tempDate.getDayOfWeek() == DayOfWeek.SATURDAY || tempDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        cell.setCellStyle(format.getCenterColorStyle());
                    } else {
                        cell.setCellStyle(format.getCenterBorderNoColorStyle());
                    }
                    tempDate = tempDate.plusDays(1);

                }

                cell = row.createCell(lastCellNum);
                cell.setCellValue(employee.getAbbreviation());
                cell.setCellStyle(format.getCenterBorderNoColorStyle());

                // ought be
                cell = row.createCell(lastCellNum + 1);
                cell.setCellStyle(format.getCenterBorderNoColorStyle());
                cell.setCellValue(WorkTimeFund.calculate(date));

                // plan
                cell = row.createCell(lastCellNum + 2);
                cell.setCellStyle(format.getCenterBorderNoColorStyle());

                int d = date.lengthOfMonth();
                int r = 2 + j;
                CellReference ref1 = new CellReference(r, 3);
                CellReference ref2 = new CellReference(r, d + 2);
                String oblast = ref1.formatAsString() + ":" + ref2.formatAsString();
                final String formula = String.format(
                        "(COUNTIF(%s,\"d\")*12)+(COUNTIF(%s,\"n\")*12)+(COUNTIF(%s,\"řd\")*12)+" +
                                "(COUNTIF(%s,\"pd\")*12)+(COUNTIF(%s,\"zv\")*12)+(COUNTIF(%s,\"pn\")*12)+SUM(%s)"
                        , oblast, oblast, oblast, oblast, oblast, oblast, oblast
                );
                cell.setCellFormula(formula);

                // difference
                cell = row.createCell(lastCellNum + 3);
                cell.setCellStyle(format.getCenterBorderNoColorStyle());
                ref1 = new CellReference(ref2.getRow(), ref2.getCol() + 2);
                ref2 = new CellReference(ref1.getRow(), ref1.getCol() + 1);
                cell.setCellFormula(ref2.formatAsString() + "-" + ref1.formatAsString());

                // from last month
                cell = row.createCell(lastCellNum + 4);
                cell.setCellStyle(format.getCenterBorderNoColorStyle());
                if (i == 0) {
                    cell.setCellValue(0);
                } else {
                    CellReference minulyMesic = new CellReference(workbook.getSheetName(i - 1), r, toNextMonth, false, false);
                    cell.setCellFormula(minulyMesic.formatAsString());
                }

                // to next month
                cell = row.createCell(lastCellNum + 5);
                cell.setCellStyle(format.getCenterBorderNoColorStyle());
                ref1 = new CellReference(ref2.getRow(), ref2.getCol() + 1);
                ref2 = new CellReference(ref1.getRow(), ref1.getCol() + 1);
                cell.setCellFormula(ref1.formatAsString() + "+" + ref2.formatAsString());
            }
            toNextMonth = cell.getColumnIndex();

            // legend last row + 2 //
            String[][] legenda = {{"Legenda", ""}, {"Denní", "d"}, {"Noční", "n"}, {"Řádná dovolená", "řd"},
                    {"Půlden dovolené", "pd"}, {"Zdravotní volno", "zv"}, {"Prac. neschopnost", "pn"}};
            int lastRow = sheet.getLastRowNum();
            for (int j = 0; j < legenda.length; j++) {
                row = sheet.createRow(lastRow + 2 + j);
                cell = row.createCell(0);
                cell.setCellValue(legenda[j][0]);
                if (j == 0) {
                    cell.setCellStyle(format.getBorderBottomStyle());
                }

                cell = row.createCell(1);
                cell.setCellValue(legenda[j][1]);

                if (j == 0) {
                    cell.setCellStyle(format.getBorderBottomStyle());
                }
            }

            // overtimes last row + 2 //
            String[][] overtimes = {{"Přesčasy", "", "", "", ""}, {"Důvod", "datum", "od hod.", "do hod.", "služ.číslo"}};
            lastRow = sheet.getLastRowNum();
            for (int j = 0; j < overtimes.length; j++) {
                row = sheet.createRow(lastRow + 2 + j);
                for (int k = 0; k < overtimes[j].length; k++) {
                    cell = row.createCell(k);
                    if (j == 0) {
                        cell.setCellStyle(format.getBorderBottomStyle());
                    }
                    cell.setCellValue(k);
                    cell.setCellValue(overtimes[j][k]);
                }
            }

            // setting the width of column for name //
            for (int j = 0; j < 3; j++) {
                sheet.autoSizeColumn(j);
            }

            // setting the width of columns for the days in week //
            for (int j = 3; j < date.lengthOfMonth() + 3; j++) {
                sheet.setColumnWidth(j, 1250);
            }

            sheet.setColumnWidth(3, 1750);
            // formatting merge //
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, date.lengthOfMonth()));

            // shift to the next month
            date = date.plusMonths(1);
        }
        return workbook;
    }

}
