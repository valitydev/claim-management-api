package com.rbkmoney.claimmanagementapi.util

import com.rbkmoney.claimmanagementapi.exception.DeadlineException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class DeadlineCheckerTest {

    private val deadlineChecker = DeadlineChecker()

    @Test
    fun checkDeadlineTest() {
        deadlineChecker.checkDeadline(null, null)
        deadlineChecker.checkDeadline("12m", null)
        deadlineChecker.checkDeadline("1.2m", null)
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("-1.2m", null) }
        deadlineChecker.checkDeadline("12s", null)
        deadlineChecker.checkDeadline("1.2s", null)
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("-1.2s", null) }
        deadlineChecker.checkDeadline("12ms", null)
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("1.2ms", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("-12ms", null) }
        deadlineChecker.checkDeadline("12m12s12ms", null)
        deadlineChecker.checkDeadline("1.2m1.2s12ms", null)
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("1.2m1.2s1.2ms", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("12s12s", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("12m12m", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("12ms12ms", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("12s12ms12ms", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("12s12s12ms", null) }
        deadlineChecker.checkDeadline(Instant.now().plus(1, ChronoUnit.DAYS).toString(), null)
        assertThrows(DeadlineException::class.java) {
            deadlineChecker.checkDeadline(
                Instant.now().minus(1, ChronoUnit.DAYS).toString(), null
            )
        }
        assertThrows(DeadlineException::class.java) { deadlineChecker.checkDeadline("undefined", null) }
    }

    @Test
    fun extractMillisecondsTest() {
        assertEquals(12, deadlineChecker.extractMilliseconds("12ms", null) as Long)
        assertEquals(12, deadlineChecker.extractMilliseconds("1.2m1.2s12ms", null) as Long)
        assertThrows(DeadlineException::class.java) { deadlineChecker.extractMilliseconds("1.2ms", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.extractMilliseconds("-12ms", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.extractMilliseconds("12ms12ms", null) }
    }

    @Test
    fun extractSecondsTest() {
        assertEquals(12000, deadlineChecker.extractSeconds("12s", null) as Long)
        assertEquals(1200, deadlineChecker.extractSeconds("1.2s", null) as Long)
        assertEquals(1200, deadlineChecker.extractSeconds("1.2m1.2s12ms", null) as Long)
        assertThrows(DeadlineException::class.java) { deadlineChecker.extractSeconds("-1.2s", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.extractSeconds("12s12s", null) }
    }

    @Test
    fun extractMinutesTest() {
        assertEquals(720000, deadlineChecker.extractMinutes("12m", null) as Long)
        assertEquals(72000, deadlineChecker.extractMinutes("1.2m", null) as Long)
        assertEquals(72000, deadlineChecker.extractMinutes("1.2m1.2s12ms", null) as Long)
        assertThrows(DeadlineException::class.java) { deadlineChecker.extractMinutes("-1.2m", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.extractMinutes("12m12m", null) }
    }

    @Test
    fun containsRelativeValuesTest() {
        assertTrue(deadlineChecker.containsRelativeValues("12m", null))
        assertTrue(deadlineChecker.containsRelativeValues("12s", null))
        assertTrue(deadlineChecker.containsRelativeValues("12ms", null))
        assertTrue(deadlineChecker.containsRelativeValues("1.2m1.2s12ms", null))
        assertThrows(DeadlineException::class.java) { deadlineChecker.containsRelativeValues("-1.2s", null) }
        assertThrows(DeadlineException::class.java) { deadlineChecker.containsRelativeValues("12s12s", null) }
        assertFalse(deadlineChecker.containsRelativeValues(Instant.now().toString(), null))
        assertFalse(deadlineChecker.containsRelativeValues("asd", null))
    }
}
