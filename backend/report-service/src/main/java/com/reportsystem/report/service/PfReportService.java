package com.reportsystem.report.service;

import com.reportsystem.report.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

// PF report er sob data ek jaygay kore. Website, PDF ar Excel tin jaygay-i ei data jay.
// Kono hisab ekhane nai, sob SQL e (ReportRepository).
@Service
public class PfReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Map<String, Object> buildReport() {
        Map<String, Object> dates = reportRepository.findReportDates();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("printDate", dates.get("print_date"));
        report.put("endYear", dates.get("end_year"));
        report.put("paragraphs", reportRepository.findParagraphs("pf-report"));
        report.put("rows", reportRepository.findLoanRows());
        report.put("total", reportRepository.findTotalRow());
        return report;
    }
}