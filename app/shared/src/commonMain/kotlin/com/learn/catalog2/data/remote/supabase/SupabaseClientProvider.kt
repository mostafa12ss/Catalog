package com.learn.catalog2.app.shared.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClientProvider {

    private const val SUPABASE_URL = "https://fcooedesypbzoosryfxg.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_Q_HckIqAIcYmtpn-E7nNWw_Uwef3N6G"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth) {
            host = "login-callback"       // ده أي اسم تختاره، مش لازم يبقى الباكيدج نيم
            scheme = "com.learn.catalog2" // ده الـ scheme اللي هتسجله في الأندرويد مانيفست
        }
        install(Postgrest)
        install(Realtime)
    }
}