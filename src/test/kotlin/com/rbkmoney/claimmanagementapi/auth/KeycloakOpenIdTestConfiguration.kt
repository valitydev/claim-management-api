package com.rbkmoney.claimmanagementapi.auth

import com.rbkmoney.claimmanagementapi.auth.utils.JwtTokenBuilder
import com.rbkmoney.claimmanagementapi.auth.utils.KeycloakOpenIdStub
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakOpenIdTestConfiguration {

    @Bean
    fun keycloakOpenIdStub(
        @Value("\${keycloak.auth-server-url}") keycloakAuthServerUrl: String,
        @Value("\${keycloak.realm}") keycloakRealm: String,
        jwtTokenBuilder: JwtTokenBuilder
    ) = KeycloakOpenIdStub(keycloakAuthServerUrl, keycloakRealm, jwtTokenBuilder)
}
