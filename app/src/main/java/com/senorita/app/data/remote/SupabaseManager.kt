package com.senorita.app.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

object SupabaseManager {
    const val SUPABASE_URL = "https://obnuvjizbksosoerbkbl.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_publishable_u1NB2qXTpPvWV-mskWQmwQ_XR-EOgtm"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
        }
    }

    val postgrest: Postgrest
        get() = client.postgrest
}
