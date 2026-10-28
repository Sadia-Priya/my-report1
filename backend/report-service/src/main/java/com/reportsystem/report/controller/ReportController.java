package com.reportsystem.report.controller;

import com.reportsystem.report.service.ExcelService;
import com.reportsystem.report.service.PdfService;
import com.reportsystem.report.service.PfReportService;
import com.reportsystem.report.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private PfReportService pfReportService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private JwtUtil jwtUtil;

    // GET /api/reports  -> website er jonno PF report er data (JSON)
    @GetMapping
    public ResponseEntity<?> getReport(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isLoggedIn(authHeader)) {
            return ResponseEntity.status(401).body(Map.of("message", "আগে লগইন করুন"));
        }
        return ResponseEntity.ok(pfReportService.buildReport());
    }

    // GET /api/reports/pdf  -> PF report (PDF), templates/pf-report.html theke
    @GetMapping("/pdf")
    public ResponseEntity<?> downloadPdf(@RequestHeader(value = "Authorization", required = false) String authHeader) throws Exception {
        if (!isLoggedIn(authHeader)) {
            return ResponseEntity.status(401).body(Map.of("message", "আগে লগইন করুন"));
        }
        byte[] file = pdfService.createPdf("pf-report", pfReportService.buildReport());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=pf-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    // GET /api/reports/excel  -> PF report (Excel)
    @GetMapping("/excel")
    public ResponseEntity<?> downloadExcel(@RequestHeader(value = "Authorization", required = false) String authHeader) throws Exception {
        if (!isLoggedIn(authHeader)) {
            return ResponseEntity.status(401).body(Map.of("message", "আগে লগইন করুন"));
        }
        byte[] file = excelService.createExcel(pfReportService.buildReport());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=pf-report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    // Frontend token pathay ei bhabe:  Authorization: Bearer <token>
    private boolean isLoggedIn(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        return jwtUtil.isTokenValid(token);
    }
}