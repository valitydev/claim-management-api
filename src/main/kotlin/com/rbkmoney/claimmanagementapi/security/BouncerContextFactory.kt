package com.rbkmoney.claimmanagementapi.security

import com.rbkmoney.bouncer.context.v1.ClaimManagementOperation
import com.rbkmoney.bouncer.context.v1.ContextClaimManagement
import com.rbkmoney.bouncer.context.v1.ContextFragment
import com.rbkmoney.bouncer.context.v1.Entity
import com.rbkmoney.bouncer.starter.AbstractBouncerContextFactory
import com.rbkmoney.bouncer.starter.UserAuthContextProvider
import com.rbkmoney.bouncer.starter.api.BouncerContext
import com.rbkmoney.bouncer.starter.config.properties.BouncerProperties
import org.springframework.stereotype.Component

@Component
class BouncerContextFactory(
    bouncerProperties: BouncerProperties,
    userAuthContextProvider: UserAuthContextProvider
) : AbstractBouncerContextFactory(bouncerProperties, userAuthContextProvider) {

    override fun customizeContext(contextFragment: ContextFragment, bouncerContext: BouncerContext) {
        contextFragment.setClaimmgmt(buildClaimManagementContext(bouncerContext as ClaimBouncerContext))
    }

    private fun buildClaimManagementContext(bouncerContext: ClaimBouncerContext) =
        ContextClaimManagement().apply {
            op = buildClaimManagementOperation(bouncerContext)
        }

    private fun buildClaimManagementOperation(bouncerContext: ClaimBouncerContext) =
        ClaimManagementOperation().apply {
            id = bouncerContext.operationId
            party = Entity().apply { id = bouncerContext.partyId }
        }
}
