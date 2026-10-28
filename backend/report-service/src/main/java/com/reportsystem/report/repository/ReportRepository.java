package com.reportsystem.report.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

// Report er sob SQL ekhane. Hisab ar Bangla format PostgreSQL nijei kore
// (bn_taka, bn_date, bn_digits function gula pgAdmin e banano).
@Repository
public class ReportRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Table er protita row, tarikh er order e
    public List<Map<String, Object>> findLoanRows() {
        String sql = """
                SELECT p.id,
                       bn_date(p.loan_date)                              AS loan_date,
                       bn_taka(p.loan_amount)                            AS loan_amount,
                       bn_taka(p.principal_realized)                     AS principal_realized,
                       bn_taka(p.service_charge)                         AS service_charge,
                       bn_taka(p.principal_realized + p.service_charge)  AS total_realized,
                       bn_taka(p.realized_till)                          AS realized_till,
                       bn_taka(p.outstanding)                            AS outstanding
                FROM pf_loans p
                ORDER BY p.loan_date, p.id
                """;
        return jdbcTemplate.queryForList(sql);
    }

    // Sesh er "মোট" row: protita column er jogfol
    public Map<String, Object> findTotalRow() {
        String sql = """
                SELECT 'মোট (টাকা):'                                         AS loan_date,
                       bn_taka(SUM(loan_amount))                             AS loan_amount,
                       bn_taka(SUM(principal_realized))                      AS principal_realized,
                       bn_taka(SUM(service_charge))                          AS service_charge,
                       bn_taka(SUM(principal_realized + service_charge))     AS total_realized,
                       bn_taka(SUM(realized_till))                           AS realized_till,
                       bn_taka(SUM(outstanding))                             AS outstanding
                FROM pf_loans
                """;
        return jdbcTemplate.queryForMap(sql);
    }

    // Report er upore lekha, paragraph er order e
    public List<String> findParagraphs(String reportName) {
        String sql = """
                SELECT content
                FROM report_texts
                WHERE report_name = ?
                ORDER BY position
                """;
        return jdbcTemplate.queryForList(sql, String.class, reportName);
    }

    // Aj er tarikh ar sesh hoye jawa orthobochor (July-June) er bochor
    public Map<String, Object> findReportDates() {
        String sql = """
                SELECT bn_date(CURRENT_DATE) AS print_date,
                       bn_digits(CASE WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 7
                                      THEN EXTRACT(YEAR FROM CURRENT_DATE)
                                      ELSE EXTRACT(YEAR FROM CURRENT_DATE) - 1
                                 END::INT::TEXT) AS end_year
                """;
        return jdbcTemplate.queryForMap(sql);
    }
}