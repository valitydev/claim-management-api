package com.rbkmoney.claimmanagementapi.auth

import com.rbkmoney.claimmanagementapi.auth.utils.JwtTokenBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.ClassPathResource
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Properties

@Configuration
class JwtTokenTestConfiguration {

    @Bean
    fun JwtTokenBuilder(keyPair: KeyPair) = JwtTokenBuilder(keyPair.private)

    @Bean
    fun keyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }

    companion object {
        @Bean
        fun properties(keyPair: KeyPair): PropertySourcesPlaceholderConfigurer {
            val fact = KeyFactory.getInstance("RSA")
            val spec = fact.getKeySpec(keyPair.public, X509EncodedKeySpec::class.java)
            val publicKey = Base64.getEncoder().encodeToString(spec.encoded)
            val pspc = PropertySourcesPlaceholderConfigurer()
            val properties = Properties().apply {
                load(ClassPathResource("application.yml").inputStream)
                setProperty("keycloak.realm-public-key", publicKey)
            }
            pspc.setProperties(properties)
            pspc.setLocalOverride(true)
            return pspc
        }
    }
}
