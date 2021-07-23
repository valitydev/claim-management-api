package com.rbkmoney.claimmanagementapi.config

import com.rbkmoney.bouncer.decisions.ArbiterSrv
import com.rbkmoney.damsel.claim_management.ClaimManagementSrv
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv
import com.rbkmoney.orgmanagement.AuthContextProviderSrv
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityEmailExtensionKit
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityIdExtensionKit
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityRealmExtensionKit
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityUsernameExtensionKit
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.io.Resource
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.temporal.ChronoUnit

@Configuration
class ClientConfig {

    @Value("\${http.timeout.read}")
    private var readTimeoutSec: Long = 0

    @Value("\${http.timeout.connect}")
    private var connectTimeoutSec: Long = 0

    @Bean
    @Primary
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate =
        builder
            .setConnectTimeout(Duration.of(connectTimeoutSec, ChronoUnit.SECONDS))
            .setReadTimeout(Duration.of(readTimeoutSec, ChronoUnit.SECONDS))
            .build()

    @Bean
    fun claimManagementClient(
        @Value("\${claimmanagement.client.adapter.url}") resource: Resource,
        @Value("\${claimmanagement.client.adapter.networkTimeout}") timeout: Int
    ): ClaimManagementSrv.Iface =
        THSpawnClientBuilder()
            .withMetaExtensions(
                listOf(
                    UserIdentityEmailExtensionKit.INSTANCE
                )
            )
            .withAddress(resource.uri)
            .withNetworkTimeout(timeout)
            .build(ClaimManagementSrv.Iface::class.java)

    @Bean
    fun partyManagementClient(
        @Value("\${partyManagement.url}") resource: Resource,
        @Value("\${partyManagement.timeout}") timeout: Int
    ): PartyManagementSrv.Iface {
        return THSpawnClientBuilder()
            .withAddress(resource.uri)
            .withNetworkTimeout(timeout)
            .build(PartyManagementSrv.Iface::class.java)
    }

    @Bean
    fun orgManagerClient(
        @Value("\${orgManagement.url}") resource: Resource,
        @Value("\${orgManagement.networkTimeout}") networkTimeout: Int
    ): AuthContextProviderSrv.Iface =
        THSpawnClientBuilder()
            .withMetaExtensions(
                listOf(
                    UserIdentityIdExtensionKit.INSTANCE,
                    UserIdentityEmailExtensionKit.INSTANCE,
                    UserIdentityUsernameExtensionKit.INSTANCE,
                    UserIdentityRealmExtensionKit.INSTANCE
                )
            )
            .withNetworkTimeout(networkTimeout)
            .withAddress(resource.uri)
            .build(AuthContextProviderSrv.Iface::class.java)

    @Bean
    fun bouncerClient(
        @Value("\${bouncer.url}") resource: Resource,
        @Value("\${bouncer.networkTimeout}") networkTimeout: Int
    ): ArbiterSrv.Iface =
        THSpawnClientBuilder()
            .withNetworkTimeout(networkTimeout)
            .withAddress(resource.uri)
            .build(ArbiterSrv.Iface::class.java)
}
