package com.rbkmoney.claimmanagementapi.security

import com.rbkmoney.claimmanagementapi.config.properties.BouncerProperties
import dev.vality.bouncer.decisions.ArbiterSrv
import mu.KotlinLogging
import org.apache.thrift.TException
import org.springframework.stereotype.Service

@Service
class BouncerService(
    private val bouncerContextFactory: BouncerContextFactory,
    private val bouncerClient: ArbiterSrv.Iface,
    private val bouncerProperties: BouncerProperties
) {

    private val log = KotlinLogging.logger { }

    fun havePrivileges(bouncerContext: BouncerContextDto): Boolean {
        log.debug { "Check access with bouncer context: $bouncerContext" }
        return try {
            val context = bouncerContextFactory.buildContext(bouncerContext)
            log.debug { "Built thrift context: $context" }
            val judge = bouncerClient.judge(bouncerProperties.ruleSetId, context)
            log.debug { "Have judge: $judge" }
            val resolution = judge.getResolution()
            log.debug { "Resolution: $resolution" }
            resolution.isSetAllowed
        } catch (e: TException) {
            throw BouncerException("Error while call bouncer", e)
        }
    }
}
