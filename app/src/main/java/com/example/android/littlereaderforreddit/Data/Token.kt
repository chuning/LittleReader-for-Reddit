package com.example.android.littlereaderforreddit.Data

data class Token(val access_token: String?,
                 val token_type: String?,
                 val expires_in: Long?,
                 val scope: String?,
                 val refresh_token: String?)