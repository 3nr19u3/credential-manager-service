package com.devpull.credentials.shared


import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus

fun Authentication.requireUserId(): Long {
    val jwt = this.principal as? Jwt ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT")
    return jwt.subject.toLong()
}