package com.rbkmoney.claimmanagementapi.config

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.KeycloakDeploymentBuilder
import org.keycloak.representations.adapters.config.AdapterConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@Configuration
class KeycloakConfiguration {

    @Value("\${keycloak.realm}")
    private lateinit var keycloakRealmName: String

    @Value("\${keycloak.resource}")
    private lateinit var keycloakResourceName: String

    @Value("\${keycloak.realm-public-key}")
    private lateinit var keycloakRealmPublicKey: String

    @Value("\${keycloak.realm-public-key.file-path:}")
    private lateinit var keycloakRealmPublicKeyFile: String

    @Value("\${keycloak.auth-server-url}")
    private lateinit var keycloakAuthServerUrl: String

    @Value("\${keycloak.ssl-required}")
    private lateinit var keycloakSSLRequired: String

    @Value("\${keycloak.not-before}")
    private var keycloakTokenNotBefore: Int = 0

    @Bean
    fun keycloakConfigResolver(): KeycloakConfigResolver =
        KeycloakConfigResolver {
            KeycloakDeploymentBuilder.build(adapterConfig()).apply {
                notBefore = keycloakTokenNotBefore
            }
        }

    private fun adapterConfig(): AdapterConfig {
        if (keycloakRealmPublicKeyFile.isNotBlank()) {
            keycloakRealmPublicKey = readKeyFromFile(keycloakRealmPublicKeyFile)
        }
        return AdapterConfig().apply {
            realm = keycloakRealmName
            realmKey = keycloakRealmPublicKey
            resource = keycloakResourceName
            authServerUrl = keycloakAuthServerUrl
            isUseResourceRoleMappings = true
            isBearerOnly = true
            sslRequired = keycloakSSLRequired
        }
    }

    private fun readKeyFromFile(filePath: String): String {
        return try {
            Files.readAllLines(Paths.get(filePath))
                .apply { removeFirst() }
                .apply { removeLast() }
                .joinToString(separator = "") { it.trim() }
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }

}