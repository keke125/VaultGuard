package com.keke125.vaultguard.service

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


class PasswordService {
    companion object {
        fun getPasswordEncoder(): PasswordEncoder {

            val encoders: MutableMap<String, PasswordEncoder> = HashMap()
            encoders["BCrypt"] = BCryptPasswordEncoder()
            encoders["argon2@SpringSecurity_v5_8"] =
                Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
            return DelegatingPasswordEncoder(
                "argon2@SpringSecurity_v5_8", encoders
            )
        }
    }

}