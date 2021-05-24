package com.rbkmoney.claimmanagementapi.auth

import com.rbkmoney.claimmanagementapi.config.AbstractKeycloakOpenIdAsWiremockConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class JwtAuthTests : AbstractKeycloakOpenIdAsWiremockConfig() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun testCors() {
        mockMvc.perform(
            MockMvcRequestBuilders.options("/ping")
                .header("Origin", "*")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "authorization, content-type")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.header().string("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,DELETE")
            )
            .andExpect(
                MockMvcResultMatchers.header().string("Access-Control-Allow-Headers", "authorization, content-type")
            )
        mockMvc.perform(
            MockMvcRequestBuilders.options("/ping")
                .header("Origin", "*")
                .header("Access-Control-Request-Method", "PUT")
                .header("Access-Control-Request-Headers", "authorization, content-type")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.header().string("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,DELETE")
            )
            .andExpect(
                MockMvcResultMatchers.header().string("Access-Control-Allow-Headers", "authorization, content-type")
            )
        mockMvc.perform(
            MockMvcRequestBuilders.options("/ping")
                .header("Origin", "*")
                .header("Access-Control-Request-Method", "DELETE")
                .header("Access-Control-Request-Headers", "authorization, content-type")
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.header().string("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,DELETE")
            )
            .andExpect(
                MockMvcResultMatchers.header().string("Access-Control-Allow-Headers", "authorization, content-type")
            )
    }

    @Test
    fun testCorrectAuth() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/ping")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + generateRBKadminJwt())
        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(MockMvcResultMatchers.content().string("pong"))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/testAdmin")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + generateRBKadminJwt())
        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(MockMvcResultMatchers.content().string("testAdmin!"))
    }

    @Test
    fun testIncorrectAuth() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/testAdmin")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/testAdmin")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "WRONG JWT!")
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/testManager")
                .header("Authorization", "Bearer " + generateRBKadminJwt())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun testOldIat() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/ping")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(
                    "Authorization",
                    "Bearer " + generateJwt(1, 1, "RBKadmin")
                )
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }
}
