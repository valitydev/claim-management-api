package com.rbkmoney.claimmanagementapi.util

import com.rbkmoney.claimmanagementapi.exception.DeadlineException
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.math.ceil

@Component
class DeadlineChecker {

    fun checkDeadline(xRequestDeadline: String?, xRequestId: String?) {
        if (xRequestDeadline == null) return
        if (containsRelativeValues(xRequestDeadline, xRequestId)) return

        try {
            val instant = Instant.parse(xRequestDeadline)
            if (Instant.now().isAfter(instant)) {
                throw DeadlineException("Deadline is expired, xRequestId=$xRequestId")
            }
        } catch (ex: Exception) {
            throw DeadlineException("Deadline has invalid 'Instant' format, xRequestId=$xRequestId")
        }
    }

    fun containsRelativeValues(xRequestDeadline: String, xRequestId: String?): Boolean {
        return extractMinutes(xRequestDeadline, xRequestId) + extractSeconds(xRequestDeadline, xRequestId) +
                extractMilliseconds(xRequestDeadline, xRequestId) > 0
    }

    fun extractMinutes(xRequestDeadline: String, xRequestId: String?): Long {
        val part = "minutes"
        checkNegativeValue(xRequestDeadline, xRequestId, minutesNegativeRegex, part)
        val minutes = extractValue(xRequestDeadline, xRequestId, minutesRegex, part)
        return minutes?.let { it.times(60000).toLong() } ?: 0
    }

    fun extractSeconds(xRequestDeadline: String, xRequestId: String?): Long {
        val part = "seconds"
        checkNegativeValue(xRequestDeadline, xRequestId, secondsNegativeRegex, part)
        val seconds = extractValue(xRequestDeadline, xRequestId, secondsRegex, part)
        return seconds?.let { it.times(1000).toLong() } ?: 0
    }

    fun extractMilliseconds(xRequestDeadline: String, xRequestId: String?): Long {
        val part = "milliseconds"
        checkNegativeValue(xRequestDeadline, xRequestId, millisecondsNegativeRegex, part)
        val milliseconds = extractValue(xRequestDeadline, xRequestId, millisecondsRegex, part)
        if (milliseconds != null && ceil(milliseconds % 1) > 0) {
            throw DeadlineException("Deadline '$part' parameter can have only integer value, xRequestId=$xRequestId")
        }
        return milliseconds?.toLong() ?: 0
    }

    private fun checkNegativeValue(xRequestDeadline: String, xRequestId: String?, regex: Regex, part: String) {
        if (xRequestDeadline.matches(regex)) {
            throw DeadlineException("Deadline '$part' parameter has negative value, xRequestId=$xRequestId")
        }
    }

    private fun extractValue(
        xRequestDeadline: String,
        xRequestId: String?,
        regex: Regex,
        part: String
    ): Double? {
        val groups = regex.findAll(xRequestDeadline).map { it.groupValues }.flatten().toList()
        val result = groups
            .filter { it.matches(numberRegex) }
            .toList()

        if (result.size > 1) {
            throw DeadlineException("Deadline '$part' parameter has a few relative value, xRequestId=$xRequestId")
        }
        return result.firstOrNull()?.toDouble()
    }

    companion object {
        private val minutesRegex = "(([0-9]+([.][0-9]+)?)(?!ms)[m])".toRegex()
        private val minutesNegativeRegex = "([-][0-9]+([.][0-9]+)?(?!ms)[m])".toRegex()

        private val secondsRegex = "(([0-9]+([.][0-9]+)?)[s])".toRegex()
        private val secondsNegativeRegex = "([-][0-9]+([.][0-9]+)?[s])".toRegex()

        private val millisecondsRegex = "(([0-9]+([.][0-9]+)?)[m][s])".toRegex()
        private val millisecondsNegativeRegex = "([-][0-9]+([.][0-9]+)?[m][s])".toRegex()

        private val numberRegex = "([0-9]+([.][0-9]+)?)".toRegex()
    }
}
