"use client";

import { useState, FormEvent } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { API_URL } from "@/lib/config";

export default function LoginPage() {
    const router = useRouter();

    // Form e ja likha hoy, ta ekhane thake
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    async function handleLogin(e: FormEvent) {
        e.preventDefault(); // page reload howa bondho kori
        setError("");

        try {
            // Email ar password gateway hoye auth-service e pathai
            const res = await fetch(API_URL + "/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email: email, password: password }),
            });
            const data = await res.json();

            if (!res.ok) {
                setError(data.message || "কিছু একটা সমস্যা হয়েছে, আবার চেষ্টা করুন");
                return;
            }

            // Token save kori. Report page protita request e eta pathabe.
            localStorage.setItem("token", data.token);
            localStorage.setItem("fullName", data.fullName);
            router.push("/report");
        } catch {
            setError("সার্ভারের সাথে যোগাযোগ করা যাচ্ছে না");
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center p-4">
            <form onSubmit={handleLogin} className="bg-white p-8 rounded-lg shadow w-full max-w-sm">
                <h1 className="text-2xl font-bold text-center mb-6">লগইন</h1>

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
                    পাসওয়ার্ড
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
                    লগইন করুন
                </button>

                <p className="text-center mt-4">
                    অ্যাকাউন্ট নেই?{" "}
                    <Link href="/signup" className="text-green-700 underline">
                        সাইন আপ করুন
                    </Link>
                </p>
            </form>
        </div>
    );
}