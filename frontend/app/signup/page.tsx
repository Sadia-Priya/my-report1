"use client";

import { useState, FormEvent } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { API_URL } from "@/lib/config";

export default function SignupPage() {
    const router = useRouter();

    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    async function handleSignup(e: FormEvent) {
        e.preventDefault();
        setError("");

        try {
            // Notun user er tottho auth-service e pathai
            const res = await fetch(API_URL + "/api/auth/signup", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ fullName: fullName, email: email, password: password }),
            });
            const data = await res.json();

            if (!res.ok) {
                setError(data.message || "কিছু একটা সমস্যা হয়েছে, আবার চেষ্টা করুন");
                return;
            }

            // Signup hole sathe sathe login o hoye jay
            localStorage.setItem("token", data.token);
            localStorage.setItem("fullName", data.fullName);
            router.push("/report");
        } catch {
            setError("সার্ভারের সাথে যোগাযোগ করা যাচ্ছে না");
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4">
            <form onSubmit={handleSignup} className="bg-white p-8 rounded-lg shadow w-full max-w-sm">
                <h1 className="text-2xl font-bold text-center mb-6">সাইন আপ</h1>

                <label htmlFor="fullName" className="block mb-1">
                    পূর্ণ নাম
                </label>
                <input
                    id="fullName"
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    className="w-full border border-gray-400 rounded px-3 py-2 mb-4"
                    required
                />

                <label htmlFor="email" className="block mb-1">
                    ইমেইল
                </label>
                <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full border border-gray-400 rounded px-3 py-2 mb-4"
                    required
                />

                <label htmlFor="password" className="block mb-1">
                    পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর)
                </label>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full border border-gray-400 rounded px-3 py-2 mb-4"
                    required
                />

                {error && <p className="text-red-600 mb-4">{error}</p>}

                <button type="submit" className="w-full bg-green-700 text-white py-2 rounded hover:bg-green-800">
                    অ্যাকাউন্ট খুলুন
                </button>

                <p className="text-center mt-4">
                    আগেই অ্যাকাউন্ট আছে?{" "}
                    <Link href="/login" className="text-green-700 underline">
                        লগইন করুন
                    </Link>
                </p>
            </form>
        </div>
    );
}