package com.rbkmoney.claimmanagementapi.config

import com.rbkmoney.bouncer.decisions.ArbiterSrv
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
class BouncerConfig {

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
