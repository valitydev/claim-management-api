package com.rbkmoney.claimmanagementapi.controller

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorData(
    @field:JsonProperty("code")
    val code: Code? = null,
    @field:JsonProperty("message")
    val message: String? = null
) {
    enum class Code(private val value: String) {
        INVALIDCLAIMSTATUS("invalidClaimStatus"),
        CHANGESETCONFLICT("changesetConflict"),
        INVALIDCHANGESET("invalidChangeset"),
        INVALIDCLAIMREVISION("invalidClaimRevision"),
        LIMITEXCEEDED("limitExceeded"),
        BADCONTINUATIONTOKEN("badContinuationToken"),
        INVALIDDEADLINE("invalidDeadline");

        @JsonValue
        override fun toString() = value
    }
}
