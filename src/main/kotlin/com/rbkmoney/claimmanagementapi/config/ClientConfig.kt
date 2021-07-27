package com.rbkmoney.claimmanagementapi.config

import com.rbkmoney.damsel.claim_management.ClaimManagementSrv
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityEmailExtensionKit
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
}
