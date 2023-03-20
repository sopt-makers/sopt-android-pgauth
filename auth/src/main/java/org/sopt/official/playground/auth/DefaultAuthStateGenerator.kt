package org.sopt.official.playground.auth

import java.util.UUID

internal class DefaultAuthStateGenerator : AuthStateGenerator {
    override fun generate() = UUID.randomUUID().toString()
}