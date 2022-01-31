package com.rbkmoney.claimmanagementapi.security

import com.rbkmoney.claimmanagementapi.config.properties.BouncerProperties
import dev.vality.bouncer.base.Entity
import dev.vality.bouncer.context.v1.*
import dev.vality.bouncer.ctx.ContextFragmentType
import dev.vality.bouncer.decisions.Context
import mu.KotlinLogging
import org.apache.thrift.TSerializer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@EnableConfigurationProperties(BouncerProperties::class)
class BouncerContextFactory(
    private val bouncerProperties: BouncerProperties,
    private val keycloakService: KeycloakService,
    private val userAuthContextProvider: UserAuthContextProvider
) {

    private val log = KotlinLogging.logger {}

    fun buildContext(bouncerContext: BouncerContextDto): Context {
        val contextFragment = buildContextFragment(bouncerContext)
        val serializer = TSerializer()
        val fragment = dev.vality.bouncer.ctx.ContextFragment().apply {
            setType(ContextFragmentType.v1_thrift_binary)
            setContent(serializer.serialize(contextFragment))
        }
        val userFragment = userAuthContextProvider.getUserAuthContext(keycloakService.accessToken.subject)
        return Context().apply {
            putToFragments(bouncerProperties.contextFragmentId, fragment)
            putToFragments("user", userFragment)
        }
    }

    private fun buildContextFragment(bouncerContext: BouncerContextDto): ContextFragment {
        val env = buildEnvironment()
        val accessToken = keycloakService.accessToken
        val expiration = Instant.ofEpochSecond(accessToken.exp).toString()
        val auth = Auth()
            .setToken(Token().setId(accessToken.id))
            .setMethod(bouncerProperties.authMethod)
            .setExpiration(expiration)
        val contextClaimManagement = buildClaimManagementContext(bouncerContext)
        val contextFragment = ContextFragment()
            .setAuth(auth)
            .setEnv(env)
            .setClaimmgmt(contextClaimManagement)
        log.debug { "Context fragment to bouncer $contextFragment" }
        return contextFragment
    }

    private fun buildEnvironment(): Environment {
        val deployment = Deployment().apply {
            id = bouncerProperties.deploymentId
        }
        return Environment().apply {
            this.deployment = deployment
            this.now = Instant.now().toString()
        }
    }

    private fun buildClaimManagementContext(bouncerContext: BouncerContextDto) =
        ContextClaimManagement().apply {
            op = buildClaimManagementOperation(bouncerContext)
        }

    private fun buildClaimManagementOperation(bouncerContext: BouncerContextDto) =
        ClaimManagementOperation().apply {
            id = bouncerContext.operationId
            party = Entity().apply { id = bouncerContext.partyId }
        }
}
