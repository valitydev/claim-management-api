package com.rbkmoney.claimmanagementapi.controller

import com.rbkmoney.claimmanagementapi.config.AbstractKeycloakOpenIdAsWiremockConfig
import com.rbkmoney.claimmanagementapi.service.ClaimManagementService
import com.rbkmoney.claimmanagementapi.service.PartyManagementService
import com.rbkmoney.claimmanagementapi.service.security.KeycloakService
import com.rbkmoney.swag.claim_management.model.Claim
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class ClaimManagementControllerTest : AbstractKeycloakOpenIdAsWiremockConfig() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var partyManagementService: PartyManagementService

    @MockBean
    private lateinit var keycloakService: KeycloakService

    @MockBean
    private lateinit var claimManagementService: ClaimManagementService

    @BeforeEach
    fun setUp() {
        doNothing().whenever(partyManagementService).checkStatus(ArgumentMatchers.anyString())
        doNothing().whenever(partyManagementService).checkStatus()
        whenever(keycloakService.partyId).thenReturn(randomUUID())
    }

    @Test
    fun testClaimShopLocationUrlAreConvertedRight() {
        whenever(claimManagementService.createClaim(ArgumentMatchers.anyString(), ArgumentMatchers.anyList()))
            .thenReturn(Claim().id(1L))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/processing/claims")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Authorization", "Bearer " + generateReadJwt())
                .header("X-Request-ID", randomUUID())
                .header("X-Request-Deadline", Instant.now().plus(1, ChronoUnit.DAYS).toString())
                .content(
                    """[
                      {
                        "modificationType": "PartyModification",
                        "partyModificationType": {
                          "partyModificationType": "ContractorModificationUnit",
                          "id": "c454ea42-8598-483a-840a-141b9ab0f3e2",
                          "modification": {
                            "contractorModificationType": "Contractor",
                            "contractorType": {
                              "contractorType": "LegalEntity",
                              "legalEntityType": {
                                "legalEntityType": "RussianLegalEntity",
                                "actualAddress": "обл Московская, г Ивантеевка, ул Новая Слобода, дом 9",
                                "russianBankAccount": {
                                  "payoutToolModificationType": "RussianBankAccount",
                                  "payoutToolType": "RussianBankAccount",
                                  "account": "99999999999999999999",
                                  "bankBik": "044525974",
                                  "bankName": "АО «Тинькофф Банк»",
                                  "bankPostAccount": "30101810145250000974"
                                },
                                "inn": "5016003824",
                                "postAddress": "обл Московская, г Ивантеевка, ул Новая Слобода, дом 9",
                                "registeredName": "ООО \"Батя\"",
                                "registeredNumber": "",
                                "representativeDocument": "1111 111111",
                                "representativeFullName": "Тархов Юрий Львович",
                                "representativePosition": "Директор"
                              }
                            }
                          }
                        }
                      },
                      {
                        "modificationType": "PartyModification",
                        "partyModificationType": {
                          "partyModificationType": "ContractModificationUnit",
                          "id": "3dc3dc36-32ba-4e15-859a-a5c4434c5d92",
                          "modification": {
                            "contractModificationType": "ContractCreationModification",
                            "contractorID": "c454ea42-8598-483a-840a-141b9ab0f3e2"
                          }
                        }
                      },
                      {
                        "modificationType": "PartyModification",
                        "partyModificationType": {
                          "partyModificationType": "ContractModificationUnit",
                          "id": "3dc3dc36-32ba-4e15-859a-a5c4434c5d92",
                          "modification": {
                            "contractModificationType": "ContractPayoutToolModificationUnit",
                            "payoutToolID": "a5648077-a4c7-4e35-95ae-380d171fbc0f",
                            "modification": {
                              "payoutToolModificationType": "ContractPayoutToolCreationModification",
                              "currency": {
                                "symbolicCode": "RUB"
                              },
                              "toolInfo": {
                                "payoutToolType": "RussianBankAccount",
                                "account": "000000000000000000",
                                "bankName": "ФИЛИАЛ \"РОСТОВСКИЙ\" АО \"АЛЬФА-БАНК\"",
                                "bankPostAccount": "30101810500000000207",
                                "bankBik": "046015207"
                              }
                            }
                          }
                        }
                      },
                      {
                        "modificationType": "PartyModification",
                        "partyModificationType": {
                          "partyModificationType": "ShopModificationUnit",
                          "id": "80e5b7bd-f57e-482b-a1f0-4065fc7eb584",
                          "modification": {
                            "shopModificationType": "ShopCreationModification",
                            "category": {
                              "id": 1
                            },
                            "location": {
                              "locationType": "ShopLocationUrl",
                              "url": "oaofoaofaofoaof"
                            },
                            "details": {
                              "name": "kkfkakfkkafkkafkakfakkfa"
                            },
                            "contractID": "3dc3dc36-32ba-4e15-859a-a5c4434c5d92",
                            "payoutToolID": "a5648077-a4c7-4e35-95ae-380d171fbc0f"
                          }
                        }
                      }
                    ]"""
                )
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    private fun randomUUID() = UUID.randomUUID().toString()
}
