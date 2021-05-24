package com.rbkmoney.claimmanagementapi.exception.client

class NotFoundException(
    override val message: String,
    override val cause: Throwable? = null,
    val response: Any
) : DarkApi4xxException(message, cause)
