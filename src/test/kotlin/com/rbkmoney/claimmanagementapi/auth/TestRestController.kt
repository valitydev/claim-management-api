package com.rbkmoney.claimmanagementapi.auth

import io.swagger.annotations.ApiOperation
import mu.KotlinLogging
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

/**
 * @since 17.11.16
 */
@RestController
class TestRestController {

    private val log = KotlinLogging.logger { }

    @ApiOperation("Операция закрытая просто валидированным JWT")
    @GetMapping("/ping")
    fun ping(@RequestHeader(value = "Authorization") auth: String?) = "pong"

    @ApiOperation(value = "Операция закрытая ролью", notes = "Требует роль 'RBKadmin' ")
    @PreAuthorize("hasAuthority('RBKadmin')")
    @GetMapping("/testAdmin")
    fun testAdmin(@RequestHeader(value = "Authorization") auth: String?): String {
        val authentication = SecurityContextHolder.getContext().authentication
        log.info(authentication.authorities.toString())
        return "testAdmin!"
    }

    @ApiOperation(value = "Операция закрытая ролью менеджер", notes = "требует роль 'RBKmanager' ")
    @PreAuthorize("hasAuthority('RBKmanager')")
    @GetMapping("/testManager")
    fun testManager(@RequestHeader(value = "Authorization") auth: String?): String {
        val authentication = SecurityContextHolder.getContext().authentication
        log.info(authentication.authorities.toString())
        return "testManager!"
    }
}
