import type { Metadata } from "next";
import { Anek_Bangla } from "next/font/google";
import "./globals.css";

// Website er Bangla font. Next.js eta Google Fonts theke niye nijei rakhe.
const anekBangla = Anek_Bangla({
  subsets: ["bengali", "latin"],
});

export const metadata: Metadata = {
  title: "বাংলা রিপোর্ট সিস্টেম",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
      <html lang="bn">
      <body className={anekBangla.className}>{children}</body>
      </html>
  );
}