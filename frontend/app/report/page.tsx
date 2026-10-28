"use client";

import { useEffect, useState } from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { API_URL } from "@/lib/config";

// Backend theke je data ashe, tar shape. Naam gula SQL er column er naam,
// ar sob lekha PostgreSQL age thekei Bangla format kore pathay.
type Row = {
    id?: number;
    loan_date: string;
    loan_amount: string;
    principal_realized: string;
    service_charge: string;
    total_realized: string;
    realized_till: string;
    outstanding: string;
};

type Report = {
    printDate: string;
    endYear: string;
    paragraphs: string[];
    rows: Row[];
    total: Row;
};

export default function ReportPage() {
    const router = useRouter();

    const [report, setReport] = useState<Report | null>(null);
    const [fullName, setFullName] = useState("");
    const [error, setError] = useState("");

    // Page khulle report load kori
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            router.push("/login");
            return;
        }

        fetch(API_URL + "/api/reports", {
            headers: { Authorization: "Bearer " + token },
        })
            .then(async (res) => {
                if (res.status === 401) {
                    // Token bhul ba purono, tai abar login korte hobe
                    localStorage.clear();
                    router.push("/login");
                    return;
                }
                if (!res.ok) {
                    setError("রিপোর্ট লোড করা যায়নি। একটু পরে আবার চেষ্টা করুন।");
                    return;
                }
                setReport(await res.json());
                setFullName(localStorage.getItem("fullName") || "");
            })
            .catch(() => setError("সার্ভারের সাথে যোগাযোগ করা যাচ্ছে না"));
    }, [router]);

    // PDF ba Excel file download
    async function downloadFile(type: string, fileName: string) {
        const token = localStorage.getItem("token");

        const res = await fetch(API_URL + "/api/reports/" + type, {
            headers: { Authorization: "Bearer " + token },
        });
        if (!res.ok) {
            setError("ফাইল ডাউনলোড করা যায়নি");
            return;
        }

        // File ta ekta lukano link diye download kori
        const blob = await res.blob();
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = fileName;
        link.click();
    }

    function logout() {
        localStorage.clear();
        router.push("/login");
    }

    const cell = "border border-black px-2 py-1";

    return (
        <div className="min-h-screen">
            <div className="bg-white shadow px-6 py-3 flex justify-between items-center">
                <h1 className="font-bold text-lg">বাংলা রিপোর্ট সিস্টেম</h1>
                <div className="flex items-center gap-4">
                    <span>{fullName}</span>
                    <button onClick={logout} className="border border-gray-400 px-3 py-1 rounded hover:bg-gray-100">
                        লগআউট
                    </button>
                </div>
            </div>

            <div className="max-w-5xl mx-auto p-6">
                {error && <p className="text-red-600 mb-4">{error}</p>}
                {!report && !error && <p>লোড হচ্ছে...</p>}

                {report && (
                    <>
                        <div className="flex justify-end gap-3 mb-4">
                            <button
                                onClick={() => downloadFile("pdf", "pf-report.pdf")}
                                className="bg-red-700 text-white px-4 py-2 rounded hover:bg-red-800"
                            >
                                PDF ডাউনলোড
                            </button>
                            <button
                                onClick={() => downloadFile("excel", "pf-report.xlsx")}
                                className="bg-green-700 text-white px-4 py-2 rounded hover:bg-green-800"
                            >
                                Excel ডাউনলোড
                            </button>
                        </div>

                        {/* PDF er motoi dekhte ekta kagoj */}
                        <div className="bg-white px-10 py-8 shadow">
                            <Image src="/pksf-header.jpg" alt="পিকেএসএফ" width={2048} height={315} className="w-full h-auto mb-6" priority />

                            <p className="text-right mb-6">মুদ্রণের তারিখ: {report.printDate}</p>

                            {report.paragraphs.map((paragraph, index) => (
                                <p key={index} className="text-justify leading-8 mb-3">
                                    {paragraph}
                                </p>
                            ))}

                            <p className="text-right text-sm underline mt-6 mb-1">অঙ্কসমূহ টাকায়</p>

                            <div className="overflow-x-auto">
                                <table className="w-full border-collapse text-center text-sm">
                                    <thead>
                                    <tr>
                                        <th rowSpan={2} className={cell}>ঋণের তারিখ</th>
                                        <th rowSpan={2} className={cell}>ঋণের পরিমাণ (আসল)</th>
                                        <th colSpan={3} className={cell}>অর্থবছরে আদায়কৃত অর্থ</th>
                                        <th rowSpan={2} className={cell}>জুন {report.endYear} পর্যন্ত আদায়কৃত আসল</th>
                                        <th rowSpan={2} className={cell}>বকেয়া আসল</th>
                                    </tr>
                                    <tr>
                                        <th className={cell}>আসল</th>
                                        <th className={cell}>সার্ভিস চার্জ</th>
                                        <th className={cell}>মোট (আসল + সার্ভিস চার্জ)</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {report.rows.map((row) => (
                                        <tr key={row.id}>
                                            <td className={cell}>{row.loan_date}</td>
                                            <td className={cell}>{row.loan_amount}</td>
                                            <td className={cell}>{row.principal_realized}</td>
                                            <td className={cell}>{row.service_charge}</td>
                                            <td className={cell}>{row.total_realized}</td>
                                            <td className={cell}>{row.realized_till}</td>
                                            <td className={cell}>{row.outstanding}</td>
                                        </tr>
                                    ))}
                                    <tr className="font-semibold">
                                        <td className={cell}>{report.total.loan_date}</td>
                                        <td className={cell}>{report.total.loan_amount}</td>
                                        <td className={cell}>{report.total.principal_realized}</td>
                                        <td className={cell}>{report.total.service_charge}</td>
                                        <td className={cell}>{report.total.total_realized}</td>
                                        <td className={cell}>{report.total.realized_till}</td>
                                        <td className={cell}>{report.total.outstanding}</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>

                            <p className="text-center text-sm mt-10 mb-2">
                                এটি কম্পিউটারে প্রস্তুতকৃত প্রতিবেদন, এতে কোনো স্বাক্ষরের প্রয়োজন নেই।
                            </p>
                            <Image src="/pksf-footer.jpg" alt="ঠিকানা" width={2087} height={179} className="w-full h-auto" />
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}