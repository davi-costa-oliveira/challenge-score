package org.blipay.controller

import org.blipay.controller.response.toResponse
import org.blipay.model.ScoreRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScoreRequestResponseTest {

    @Test
    fun `toResponse should convert ScoreRequest to ScoreRequestResponse correctly`() {
        val scoreRequest = ScoreRequest(
            name = "John Doe",
            cpf = "12345678901",
            city = "Los Angeles",
            income = 5000.0,
            age = 30,
            temperature = 24.0,
            score = 150,
            approved = true
        )

        val response = scoreRequest.toResponse()

        assertEquals(scoreRequest.cpf, response.cpf)
        assertEquals(scoreRequest.approved, response.approved)
        assertEquals(scoreRequest.score, response.score)
    }
}