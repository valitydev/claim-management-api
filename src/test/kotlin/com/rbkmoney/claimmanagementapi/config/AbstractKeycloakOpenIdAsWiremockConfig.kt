package com.rbkmoney.claimmanagementapi.config

import com.rbkmoney.claimmanagementapi.ClaimManagementApiApplication
import com.rbkmoney.claimmanagementapi.auth.TestRestController
import com.rbkmoney.claimmanagementapi.auth.utils.KeycloakOpenIdStub
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ClaimManagementApiApplication::class, TestRestController::class],
    properties = [
        "wiremock.server.baseUrl=http://localhost:\${wiremock.server.port}",
        "keycloak.auth-server-url=\${wiremock.server.baseUrl}/auth"
    ]
)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@ExtendWith(SpringExtension::class)
abstract class AbstractKeycloakOpenIdAsWiremockConfig {

    @Autowired
    private lateinit var keycloakOpenIdStub: KeycloakOpenIdStub

    protected fun generateJwt(iat: Long, exp: Long, vararg roles: String): String {
        return keycloakOpenIdStub.generateJwt(iat, exp, *roles)
    }

    protected fun generateRBKadminJwt(): String {
        return keycloakOpenIdStub.generateJwt("RBKadmin")
    }

    protected fun generateReadJwt(): String {
        return keycloakOpenIdStub.generateJwt("party:read")
    }

    protected fun generateWriteJwt(): String {
        return keycloakOpenIdStub.generateJwt("party:write")
    }

    protected fun generateInvoicesReadJwt(): String {
        return keycloakOpenIdStub.generateJwt("invoices:read")
    }

    companion object {

        @BeforeAll
        fun setUp(@Autowired keycloakOpenIdStub: KeycloakOpenIdStub) {
            keycloakOpenIdStub.givenStub()
        }
    }
}
