package com.rbkmoney.claimmanagementapi.auth.utils

import com.github.tomakehurst.wiremock.client.WireMock

class KeycloakOpenIdStub(
    keycloakAuthServerUrl: String,
    private val keycloakRealm: String,
    private val jwtTokenBuilder: JwtTokenBuilder
) {
    private val issuer = "$keycloakAuthServerUrl/realms/$keycloakRealm"
    private val openidConfig = """{
      "issuer": "$issuer",
      "authorization_endpoint": "$keycloakAuthServerUrl/realms/$keycloakRealm/protocol/openid-connect/auth",
      "token_endpoint": "$keycloakAuthServerUrl/realms/$keycloakRealm/protocol/openid-connect/token",
      "token_introspection_endpoint": "$keycloakAuthServerUrl/realms/$keycloakRealm/protocol/openid-connect/token/introspect",
      "userinfo_endpoint": "$keycloakAuthServerUrl/realms/$keycloakRealm/protocol/openid-connect/userinfo",
      "end_session_endpoint": "$keycloakAuthServerUrl/realms/$keycloakRealm/protocol/openid-connect/logout",
      "jwks_uri": "$keycloakAuthServerUrl/realms/$keycloakRealm/protocol/openid-connect/certs",
      "check_session_iframe": "$keycloakAuthServerUrl/realms/$keycloakRealm/protocol/openid-connect/login-status-iframe.html",
      "registration_endpoint": "$keycloakAuthServerUrl/realms/$keycloakRealm/clients-registrations/openid-connect",
      "introspection_endpoint": "$keycloakAuthServerUrl/realms/$keycloakRealm/protocol/openid-connect/token/introspect"
    }"""

    fun givenStub() {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlEqualTo("/auth/realms/$keycloakRealm/.well-known/openid-configuration"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(openidConfig)
                )
        )
    }

    fun generateJwt(vararg roles: String): String {
        return jwtTokenBuilder.generateJwtWithRoles(issuer, *roles)
    }

    fun generateJwt(iat: Long, exp: Long, vararg roles: String): String {
        return jwtTokenBuilder.generateJwtWithRoles(iat, exp, issuer, *roles)
    }
}
