package com.rbkmoney.claimmanagementapi.service

import com.rbkmoney.claimmanagementapi.exception.client.ForbiddenException
import com.rbkmoney.claimmanagementapi.exception.server.DarkApi5xxException
import com.rbkmoney.claimmanagementapi.service.security.KeycloakService
import com.rbkmoney.damsel.domain.Blocked
import com.rbkmoney.damsel.domain.Blocking
import com.rbkmoney.damsel.domain.PartyStatus
import com.rbkmoney.damsel.domain.Unblocked
import com.rbkmoney.damsel.payment_processing.PartyManagementSrv
import org.apache.thrift.TException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

@SpringBootTest
@ExtendWith(SpringExtension::class)
class PartyManagementServiceTest {

    @Autowired
    private lateinit var partyManagementService: PartyManagementService

    @MockBean
    private lateinit var partyManagementClient: PartyManagementSrv.Iface

    @MockBean
    private lateinit var keycloakService: KeycloakService

    @BeforeEach
    fun setUp() {
        whenever(keycloakService.partyId).thenReturn(UUID.randomUUID().toString())
    }

    @Test
    fun partyManagementServiceTest() {
        val partyStatus = PartyStatus()
        partyStatus.setBlocking(Blocking.unblocked(Unblocked()))
        reset(partyManagementClient)
        whenever(
            partyManagementClient.getStatus(
                ArgumentMatchers.any(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(partyStatus)
        partyManagementService.checkStatus()
        partyStatus.setBlocking(Blocking.blocked(Blocked()))
        whenever(
            partyManagementClient.getStatus(
                ArgumentMatchers.any(),
                ArgumentMatchers.anyString()
            )
        ).thenReturn(partyStatus)
        Assertions.assertThrows(ForbiddenException::class.java) { partyManagementService.checkStatus() }
        reset(partyManagementClient)
        whenever(
            partyManagementClient.getStatus(
                ArgumentMatchers.any(),
                ArgumentMatchers.anyString()
            )
        ).thenThrow(
            TException::class.java
        )
        Assertions.assertThrows(DarkApi5xxException::class.java) { partyManagementService.checkStatus() }
    }
}
