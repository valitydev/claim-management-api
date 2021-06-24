package com.rbkmoney.claimmanagementapi.security

import com.rbkmoney.bouncer.decisions.ArbiterSrv
import com.rbkmoney.claimmanagementapi.config.properties.BouncerProperties
import org.apache.thrift.TException
import org.springframework.stereotype.Service

@Service
class BouncerService(
    private val bouncerContextFactory: BouncerContextFactory,
    private val bouncerClient: ArbiterSrv.Iface,
    private val bouncerProperties: BouncerProperties
) {

    fun havePrivileges(bouncerContext: BouncerContextDto): Boolean {
        return try {
            val context = bouncerContextFactory.buildContext(bouncerContext)
            val judge = bouncerClient.judge(bouncerProperties.ruleSetId, context)
            val resolution = judge.getResolution()
            resolution.isSetAllowed
        } catch (e: TException) {
            throw BouncerException("Error while call bouncer", e)
        }
    }
}
