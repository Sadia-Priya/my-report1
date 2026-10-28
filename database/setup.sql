-- 1. Rin er table
CREATE TABLE pf_loans (
    id                 SERIAL PRIMARY KEY,
    loan_date          DATE           NOT NULL,             -- riner tarikh
    loan_amount        NUMERIC(14, 2) NOT NULL,             -- riner poriman (asol)
    principal_realized NUMERIC(14, 2) NOT NULL DEFAULT 0,   -- orthobochore aday: asol
    service_charge     NUMERIC(14, 2) NOT NULL DEFAULT 0,   -- orthobochore aday: service charge
    realized_till      NUMERIC(14, 2) NOT NULL DEFAULT 0,   -- June porjonto mot aday (asol)
    outstanding        NUMERIC(14, 2) NOT NULL DEFAULT 0    -- bokeya asol
);

-- 2. Report er lekha (protita paragraph ekta row)
CREATE TABLE report_texts (
    id          SERIAL PRIMARY KEY,
    report_name VARCHAR(50) NOT NULL,   -- kon report, jemon 'pf-report'
    position    INT         NOT NULL,   -- kototomo paragraph
    content     TEXT        NOT NULL    -- paragraph er lekha
);

-- 3. Bangla format er function
-- '2026' -> '২০২৬'
CREATE FUNCTION bn_digits(value TEXT) RETURNS TEXT AS $$
    SELECT translate(value, '0123456789', '০১২৩৪৫৬৭৮৯');
$$ LANGUAGE sql IMMUTABLE;

-- 200000 -> '২,০০,০০০.০০' (lakh er moto comma)
CREATE FUNCTION bn_taka(amount NUMERIC) RETURNS TEXT AS $$
    SELECT bn_digits(to_char(COALESCE(amount, 0), 'FM99,99,99,99,990.00'));
$$ LANGUAGE sql IMMUTABLE;

-- 2025-11-16 -> '১৬/১১/২০২৫'
CREATE FUNCTION bn_date(day DATE) RETURNS TEXT AS $$
    SELECT bn_digits(to_char(day, 'DD/MM/YYYY'));
$$ LANGUAGE sql IMMUTABLE;

-- 4. Sample data (PF_Report.pdf er row)
INSERT INTO pf_loans (loan_date, loan_amount, principal_realized, service_charge, realized_till, outstanding)
VALUES ('2025-11-16', 200000, 116669, 0, 116669, 83331);

INSERT INTO report_texts (report_name, position, content)
VALUES ('pf-report', 1, 'পিকেএসএফ এবং এর সহযোগী সংস্থাসমূহের মাঠপর্যায়ের ঋণ কার্যক্রম আরও সুসংগঠিত ও তথ্য-উপাত্ত নির্ভর করার লক্ষ্যে পিকেএসএফ কর্তৃক ‘Inclusive Finance Insights’ (সংক্ষেপে ‘IFI’) নামে একটি সফটওয়্যার মডিউল (Version 1) প্রস্তুত করা হয়েছে। উক্ত মডিউল ব্যবহার করে সহযোগী সংস্থাসমূহের কাভারেজ ম্যাপ স্বয়ংক্রিয়ভাবে তৈরি; GIS ভিত্তিক রিসোর্স ম্যাপিং করা, যে সকল ইউনিয়ন/উপজেলায় সহযোগী সংস্থাসমূহের উপস্থিতি বা মাইক্রোক্রেডিট কাভারেজ কম সেগুলো সহজে চিহ্নিত করা, যার আলোকে ঋণ কাভারেজ বৃদ্ধির সুনির্দিষ্ট কৌশল নির্ধারণ ইত্যাদি সহজতর হবে। পাশাপাশি, কোনো একক ঋণগ্রহীতা একই সময়ে একাধিক সহযোগী সংস্থা হতে ঋণ গ্রহণ করে থাকলে তা চিহ্নিতকরণ, মৌসুমভিত্তিক ঋণের চাহিদা নিরূপণ এবং সংশ্লিষ্ট ঋণগ্রহীতার অনুকূলে চাহিদামাফিক অতিরিক্ত ঋণ প্রদানের উপযুক্ততা যাচাই করা সম্ভব হবে।');




SELECT bn_date(loan_date) AS loan_date, bn_taka(loan_amount) AS loan_amount, bn_taka(outstanding) AS outstanding
FROM pf_loans;