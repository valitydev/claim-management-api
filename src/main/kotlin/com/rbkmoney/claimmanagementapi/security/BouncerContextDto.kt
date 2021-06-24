package com.rbkmoney.claimmanagementapi.security

data class BouncerContextDto(
    val operationId: String,
    val partyId: String? = null
)
