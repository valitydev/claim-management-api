package com.rbkmoney.claimmanagementapi.controller

import com.rbkmoney.claimmanagementapi.exception.client.BadRequestException
import com.rbkmoney.claimmanagementapi.exception.client.DarkApi4xxException
import com.rbkmoney.claimmanagementapi.exception.client.ForbiddenException
import com.rbkmoney.claimmanagementapi.exception.client.NotFoundException
import com.rbkmoney.claimmanagementapi.exception.server.DarkApi5xxException
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.context.request.WebRequest
import java.net.http.HttpTimeoutException

@RestControllerAdvice
class ErrorControllerAdvice {

    private val log = KotlinLogging.logger { }

    // ----------------- 4xx -----------------------------------------------------
    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(e: BadRequestException): Any {
        log.warn(e) { "<- Res [400]: Not valid" }
        return e.response
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException) {
        log.warn(e) { "<- Res [400]: MethodArgument not valid" }
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException) {
        log.warn(e) { "<- Res [400]: Missing ServletRequestParameter" }
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(e: AccessDeniedException) {
        log.warn(e) { "<- Res [403]: Request denied access" }
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbiddenException(e: ForbiddenException) {
        log.warn(e) { "<- Res [403]: Request denied access" }
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleFileNotFoundException(e: NotFoundException): Any {
        log.warn(e) { "<- Res [404]: Not found" }
        return e.response
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException::class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    fun handleHttpMediaTypeNotAcceptable(e: HttpMediaTypeNotAcceptableException) {
        log.warn(e) { "<- Res [406]: MediaType not acceptable" }
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupported(
        e: HttpMediaTypeNotSupportedException,
        request: WebRequest
    ): ResponseEntity<*> {
        log.warn(e) { "<- Res [415]: MediaType not supported" }
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .headers(httpHeaders(e))
            .build<Any>()
    }

    @ExceptionHandler(DarkApi4xxException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleDarkApiClientException(e: DarkApi4xxException) {
        log.warn(e) { "<- Res [400]: Unrecognized Error by controller" }
    }

    // ----------------- 5xx -----------------------------------------------------
    @ExceptionHandler(HttpClientErrorException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleHttpClientErrorException(e: HttpClientErrorException) {
        log.error(e) {
            "<- Res [500]: Error with using inner http client, code=${e.statusCode}, body=${e.responseBodyAsString}"
        }
    }

    @ExceptionHandler(HttpTimeoutException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleHttpTimeoutException(e: HttpTimeoutException) {
        log.error(e) { "<- Res [500]: Timeout with using inner http client" }
    }

    @ExceptionHandler(DarkApi5xxException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleHttpTimeoutException(e: DarkApi5xxException) {
        log.error(e) { "<- Res [500]: Unrecognized inner error" }
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception) {
        log.error(e) { "<- Res [500]: Unrecognized inner error" }
    }

    private fun httpHeaders(e: HttpMediaTypeNotSupportedException): HttpHeaders {
        val headers = HttpHeaders()
        val mediaTypes = e.supportedMediaTypes
        if (mediaTypes.isNotEmpty()) {
            headers.accept = mediaTypes
        }
        return headers
    }
}
