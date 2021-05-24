package com.rbkmoney.claimmanagementapi.exception

import java.lang.RuntimeException

class DeadlineException(override val message: String) : RuntimeException(message)
