package org.blipay.controller

import org.blipay.exception.ValidationException
import org.blipay.model.ScoreRequest
import org.blipay.service.ScoreService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ExtendWith(MockitoExtension::class)
@WebMvcTest(ScoreController::class)
class ScoreControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var scoreService: ScoreService

    @Test
    fun `consultCpf should return score response when given valid request`() {
        val name = "José Test"
        val age = 18
        val income = 3000.0
        val city = "São Carlos"
        val cpf = "12345678901"

        val scoreRequest = ScoreRequest("José Test", cpf, "City", 3000.0, 30, 15.0, 150, true)

        Mockito.`when`(scoreService.evaluateScore(name, cpf, city, age, income))
            .thenReturn(scoreRequest)

        mockMvc.get("/score") {
            param("name", name)
            param("age", age.toString())
            param("income", income.toString())
            param("city", city)
            param("cpf", cpf)
        }
            .andExpect {
                status { isOk() }
                jsonPath("cpf") { value(cpf) }
                jsonPath("approved") { value(true) }
                jsonPath("score") { value(150) }
            }
    }

    @Test
    fun `consultCpf should return bad request and error message when ValidationException is thrown`() {
        val name = "José Test"
        val age = 17
        val income = 3000.0
        val city = "São Carlos"
        val cpf = "12345678901"
        val errorMessage = "Requester underage not allowed"

        Mockito.`when`(scoreService.evaluateScore(name, cpf, city, age, income))
            .thenThrow(ValidationException(errorMessage, httpStatus = HttpStatus.BAD_REQUEST))

        mockMvc.get("/score") {
            param("name", name)
            param("age", age.toString())
            param("income", income.toString())
            param("city", city)
            param("cpf", cpf)
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message") { value(errorMessage) }
            }
    }

    @Test
    fun `consultByCpf should return bad request for empty cpf`() {
        val cpf = "   "
        val scoreRequests = listOf(
            ScoreRequest("José Test", cpf, "City", 3000.0, 30, 15.0, 150, true)
        )
        val errorMessage = "Requester underage not allowed"

        Mockito.`when`(scoreService.getScoreHistoryByCpf(cpf))
            .thenThrow(ValidationException(errorMessage, httpStatus = HttpStatus.BAD_REQUEST))

        mockMvc.get("/score/history/$cpf")
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message") { value(errorMessage) }
            }
    }

    @Test
    fun `consultByCpf should return score history for given cpf`() {
        val cpf = "12345678901"
        val scoreRequests = listOf(
            ScoreRequest("José Test", cpf, "City", 3000.0, 30, 15.0, 150, true)
        )

        Mockito.`when`(scoreService.getScoreHistoryByCpf(cpf))
            .thenReturn(scoreRequests)

        mockMvc.get("/score/history/$cpf")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].cpf") { value(cpf) }
            }
    }
}