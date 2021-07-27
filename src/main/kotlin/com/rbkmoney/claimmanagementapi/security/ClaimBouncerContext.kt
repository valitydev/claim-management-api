package com.rbkmoney.claimmanagementapi.security

import com.rbkmoney.bouncer.starter.api.BouncerContext

data class ClaimBouncerContext(
    override val tokenExpiration: Long,
    override val tokenId: String,
    override val userId: String,
    val operationId: String,
    val partyId: String?
) : BouncerContext()
