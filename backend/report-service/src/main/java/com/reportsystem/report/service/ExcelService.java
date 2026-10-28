package com.reportsystem.report.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

// PF report er Excel. Data ashe PfReportService theke (PDF ar website er eki data).
@Service
public class ExcelService {

    // Table er 7 ta column: SQL er column er naam, table er order e
    private static final String[] COLUMNS = {
            "loan_date", "loan_amount", "principal_realized", "service_charge",
            "total_realized", "realized_till", "outstanding"
    };
    private static final int LAST_COLUMN = COLUMNS.length - 1;

    @SuppressWarnings("unchecked")
    public byte[] createExcel(Map<String, Object> report) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("পিএফ রিপোর্ট");

        // Font: "Nirmala UI" Windows er font, eta Bangla dekhate pare
        Font normalFont = makeFont(workbook, false, false);
        Font boldFont = makeFont(workbook, true, false);
        Font smallUnderlineFont = makeFont(workbook, false, true);

        CellStyle rightStyle = makeStyle(workbook, normalFont, HorizontalAlignment.RIGHT, false);
        CellStyle textStyle = makeStyle(workbook, normalFont, HorizontalAlignment.JUSTIFY, false);
        textStyle.setWrapText(true);
        textStyle.setVerticalAlignment(VerticalAlignment.TOP);
        CellStyle unitStyle = makeStyle(workbook, smallUnderlineFont, HorizontalAlignment.RIGHT, false);
        CellStyle headerStyle = makeStyle(workbook, boldFont, HorizontalAlignment.CENTER, true);
        headerStyle.setWrapText(true);
        CellStyle cellStyle = makeStyle(workbook, normalFont, HorizontalAlignment.CENTER, true);
        CellStyle totalStyle = makeStyle(workbook, boldFont, HorizontalAlignment.CENTER, true);
        CellStyle noteStyle = makeStyle(workbook, normalFont, HorizontalAlignment.CENTER, false);

        int rowIndex = 0;

        // Mudroner tarikh
        addMergedRow(sheet, rowIndex, "মুদ্রণের তারিখ: " + report.get("printDate"), rightStyle);
        rowIndex += 2;

        // Database er lekha (protita paragraph ekta merged row)
        for (String paragraph : (List<String>) report.get("paragraphs")) {
            Row row = addMergedRow(sheet, rowIndex, paragraph, textStyle);
            row.setHeightInPoints(17 * (paragraph.length() / 130 + 1));   // lekha onujayi row er ucchota
            rowIndex++;
        }
        rowIndex++;

        addMergedRow(sheet, rowIndex, "অঙ্কসমূহ টাকায়", unitStyle);
        rowIndex++;

        // Table er duita header row
        String endYear = (String) report.get("endYear");
        String[] header1 = {"ঋণের তারিখ", "ঋণের পরিমাণ (আসল)", "অর্থবছরে আদায়কৃত অর্থ", "", "",
                "জুন " + endYear + " পর্যন্ত আদায়কৃত আসল", "বকেয়া আসল"};
        String[] header2 = {"", "", "আসল", "সার্ভিস চার্জ", "মোট (আসল + সার্ভিস চার্জ)", "", ""};
        int top = rowIndex;
        writeRow(sheet.createRow(top), header1, headerStyle).setHeightInPoints(36);
        writeRow(sheet.createRow(top + 1), header2, headerStyle).setHeightInPoints(36);
        for (int col : new int[] {0, 1, 5, 6}) {
            sheet.addMergedRegion(new CellRangeAddress(top, top + 1, col, col));     // upor-nicher ghor jora
        }
        sheet.addMergedRegion(new CellRangeAddress(top, top, 2, 4));                 // 3 column jora
        rowIndex += 2;

        // Database er protita rin er jonno ekta row
        for (Map<String, Object> data : (List<Map<String, Object>>) report.get("rows")) {
            writeRow(sheet.createRow(rowIndex), values(data), cellStyle);
            rowIndex++;
        }
        writeRow(sheet.createRow(rowIndex), values((Map<String, Object>) report.get("total")), totalStyle);
        rowIndex += 2;

        addMergedRow(sheet, rowIndex, "এটি কম্পিউটারে প্রস্তুতকৃত প্রতিবেদন, এতে কোনো স্বাক্ষরের প্রয়োজন নেই।", noteStyle);

        // Column er choura
        int[] widths = {14, 16, 15, 12, 18, 20, 16};
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i] * 256);
        }

        // Print korle A4, sob column ek page er choura te
        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    // Ekta row er Map theke 7 ta column er value, order moto
    private String[] values(Map<String, Object> data) {
        String[] values = new String[COLUMNS.length];
        for (int i = 0; i < COLUMNS.length; i++) {
            values[i] = String.valueOf(data.get(COLUMNS[i]));
        }
        return values;
    }

    private Row writeRow(Row row, String[] values, CellStyle style) {
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            cell.setCellStyle(style);
        }
        return row;
    }

    // Ekta row jar ekta ghor puro table er choura jure thake
    private Row addMergedRow(Sheet sheet, int rowIndex, String text, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        Cell cell = row.createCell(0);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, LAST_COLUMN));
        return row;
    }

    private Font makeFont(Workbook workbook, boolean bold, boolean smallUnderline) {
        Font font = workbook.createFont();
        font.setFontName("Nirmala UI");
        font.setBold(bold);
        if (smallUnderline) {
            font.setFontHeightInPoints((short) 9);
            font.setUnderline(Font.U_SINGLE);
        }
        return font;
    }

    private CellStyle makeStyle(Workbook workbook, Font font, HorizontalAlignment align, boolean border) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(align);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (border) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }
        return style;
    }
}