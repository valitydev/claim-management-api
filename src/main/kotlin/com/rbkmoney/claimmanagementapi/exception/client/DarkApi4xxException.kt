package com.rbkmoney.claimmanagementapi.exception.client

open class DarkApi4xxException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)
