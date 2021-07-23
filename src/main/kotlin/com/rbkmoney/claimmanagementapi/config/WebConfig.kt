package com.rbkmoney.claimmanagementapi.config

import com.rbkmoney.claimmanagementapi.util.DeadlineChecker
import com.rbkmoney.woody.api.flow.WFlow
import com.rbkmoney.woody.api.trace.ContextUtils
import com.rbkmoney.woody.api.trace.ContextUtils.setCustomMetadataValue
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityEmailExtensionKit
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityIdExtensionKit
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityRealmExtensionKit
import com.rbkmoney.woody.api.trace.context.metadata.user.UserIdentityUsernameExtensionKit
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.security.Principal
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class WebConfig {

    @Bean
    fun woodyFilter(): FilterRegistrationBean<*> {
        val wFlow = WFlow()
        val filter = object : OncePerRequestFilter() {
            override fun doFilterInternal(
                request: HttpServletRequest,
                response: HttpServletResponse,
                filterChain: FilterChain
            ) {
                wFlow.createServiceFork(
                    Runnable {
                        try {
                            request.userPrincipal?.let { addWoodyContext(request.userPrincipal) }
                            setWoodyDeadline(request)
                            filterChain.doFilter(request, response)
                        } catch (e: IOException) {
                            sneakyThrow<RuntimeException, Any>(e)
                        } catch (e: ServletException) {
                            sneakyThrow<RuntimeException, Any>(e)
                        }
                    }
                ).run()
            }

            private fun <E : Throwable, T> sneakyThrow(t: Throwable): T {
                throw t as E
            }
        }
        return FilterRegistrationBean<Filter>()
            .apply {
                this.filter = filter
                order = -50
                setName("woodyFilter")
                addUrlPatterns("*")
            }
    }

    private fun addWoodyContext(principal: Principal) {
        val keycloakSecurityContext = (principal as KeycloakAuthenticationToken).account.keycloakSecurityContext
        val accessToken = keycloakSecurityContext.token
        setCustomMetadataValue(UserIdentityIdExtensionKit.KEY, accessToken.subject)
        setCustomMetadataValue(UserIdentityUsernameExtensionKit.KEY, accessToken.preferredUsername)
        setCustomMetadataValue(UserIdentityEmailExtensionKit.KEY, accessToken.email)
        setCustomMetadataValue(UserIdentityRealmExtensionKit.KEY, keycloakSecurityContext.realm)
    }

    private fun setWoodyDeadline(request: HttpServletRequest) {
        val xRequestDeadline = request.getHeader("X-Request-Deadline")
        val xRequestId = request.getHeader("X-Request-ID")
        xRequestDeadline?.let {
            ContextUtils.setDeadline(getInstant(xRequestDeadline, xRequestId))
        }
    }

    private fun getInstant(xRequestDeadline: String, xRequestId: String): Instant {
        val deadlineChecker = DeadlineChecker()
        return if (deadlineChecker.containsRelativeValues(xRequestDeadline, xRequestId)) {
            Instant.now()
                .plus(deadlineChecker.extractMilliseconds(xRequestDeadline, xRequestId), ChronoUnit.MILLIS)
                .plus(deadlineChecker.extractSeconds(xRequestDeadline, xRequestId), ChronoUnit.MILLIS)
                .plus(deadlineChecker.extractMinutes(xRequestDeadline, xRequestId), ChronoUnit.MILLIS)
        } else {
            Instant.parse(xRequestDeadline)
        }
    }
}
