package com.rbkmoney.claimmanagementapi.config

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.KeycloakDeploymentBuilder
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.keycloak.representations.adapters.config.AdapterConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@Configuration
@EnableWebSecurity
@ComponentScan(
    basePackageClasses = [KeycloakSecurityComponents::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ["org.keycloak.adapters.springsecurity.management.HttpSessionManager"]
        )
    ]
)
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@ConditionalOnProperty(value = ["auth.enabled"], havingValue = "true")
class SecurityConfig : KeycloakWebSecurityConfigurerAdapter() {

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
    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy = NullAuthenticatedSessionStrategy()

    override fun configure(http: HttpSecurity) {
        super.configure(http)
        http
            .cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers(HttpMethod.GET, "/**/health").permitAll()
            .anyRequest().authenticated()
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(keycloakAuthenticationProvider())
    }

    @Bean
    fun keycloakConfigResolver(): KeycloakConfigResolver =
        KeycloakConfigResolver {
            KeycloakDeploymentBuilder.build(adapterConfig()).apply {
                notBefore = keycloakTokenNotBefore
            }
        }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            applyPermitDefaultValues()
            addAllowedMethod(HttpMethod.PUT)
            addAllowedMethod(HttpMethod.DELETE)
        }
        return UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", configuration) }
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
