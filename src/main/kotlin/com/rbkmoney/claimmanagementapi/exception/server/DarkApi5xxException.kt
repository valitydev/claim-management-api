package com.rbkmoney.claimmanagementapi.exception.server

class DarkApi5xxException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)
