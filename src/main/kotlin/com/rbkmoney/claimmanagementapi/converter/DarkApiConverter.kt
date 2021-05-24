package com.rbkmoney.claimmanagementapi.converter

interface DarkApiConverter<R, T> {
    fun convertToThrift(value: T): R
    fun convertToSwag(value: R): T
}
