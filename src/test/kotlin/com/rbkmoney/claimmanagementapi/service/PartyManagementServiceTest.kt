package com.rbkmoney.claimmanagementapi.service

import com.rbkmoney.claimmanagementapi.exception.client.ForbiddenException
import com.rbkmoney.claimmanagementapi.exception.server.DarkApi5xxException
import com.rbkmoney.claimmanagementapi.security.KeycloakService
import dev.vality.damsel.domain.Blocked
import dev.vality.damsel.domain.Blocking
import dev.vality.damsel.domain.PartyStatus
import dev.vality.damsel.domain.Unblocked
import dev.vality.damsel.payment_processing.PartyManagementSrv
import org.apache.thrift.TException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
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
            partyManagementClient.getStatus(any())
        ).thenReturn(partyStatus)
        partyManagementService.checkStatus()
        partyStatus.setBlocking(Blocking.blocked(Blocked()))
        whenever(
            partyManagementClient.getStatus(any())
        ).thenReturn(partyStatus)
        Assertions.assertThrows(ForbiddenException::class.java) { partyManagementService.checkStatus() }
        reset(partyManagementClient)
        whenever(
            partyManagementClient.getStatus(any())
        ).thenThrow(
            TException::class.java
        )
        Assertions.assertThrows(DarkApi5xxException::class.java) { partyManagementService.checkStatus() }
    }
}
