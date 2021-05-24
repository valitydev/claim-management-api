package com.rbkmoney.claimmanagementapi.exception.client

class ForbiddenException(override val message: String) : DarkApi4xxException(message)
