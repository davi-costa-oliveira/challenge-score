package org.blipay.service

import org.blipay.client.WeatherApiClient
import org.blipay.exception.ValidationException
import org.blipay.model.ScoreRequest
import org.blipay.repository.ScoreRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.math.roundToInt

class ScoreServiceTest {
    @Mock
    private lateinit var weatherApiClient: WeatherApiClient

    @Mock
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var scoreService: ScoreService

    private val ageFactor = 0.5
    private val tempFactor = 5.0
    private val incomeFactorMultiplier = 2.0
    private val incomeFactorDivider = 100.0
    private val minimumScore = 200

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        scoreService = ScoreService(
            ageFactor = ageFactor,
            tempFactor = tempFactor,
            incomeFactorMultiplier = incomeFactorMultiplier,
            incomeFactorDivider = incomeFactorDivider,
            weatherApiClient = weatherApiClient,
            scoreRepository = scoreRepository,
            minimumScore = minimumScore
        )
    }

    @Test
    fun `evaluateScore should calculate score correctly when all parameters are valid`() {
        val name = "João Test"
        val cpf = "12345678911"
        val city = "São Carlos"
        val age = 30
        val income = 3000.0
        val temperature = 25.0

        `when`(weatherApiClient.getTemperatureByCity(city)).thenReturn(temperature)

        val scoreRequest = scoreService.evaluateScore(name, cpf, city, age, income)

        val expectedScore =
            ((age * ageFactor) + ((income / incomeFactorDivider) * incomeFactorMultiplier) + (temperature * tempFactor)).roundToInt()

        assertEquals(expectedScore, scoreRequest.score)
        assertEquals(expectedScore >= minimumScore, scoreRequest.approved)
    }

    @Test
    fun `evaluateScore should save score request when calculation succeeds`() {
        val name = "João Test"
        val cpf = "12345678911"
        val city = "São Carlos"
        val age = 30
        val income = 3000.0
        val temperature = 25.0

        `when`(weatherApiClient.getTemperatureByCity(city)).thenReturn(temperature)

        val scoreRequest = scoreService.evaluateScore(name, cpf, city, age, income)

        verify(scoreRepository).save(scoreRequest)
    }

    @Test
    fun `calculateScore should floor the result to the nearest lower integer`() {
        val age = 33
        val income = 3000.0
        val temperature = 24.6
        val name = "João Test"
        val cpf = "12345678911"
        val city = "São Carlos"

        `when`(weatherApiClient.getTemperatureByCity(city)).thenReturn(temperature)

        val scoreRequest = scoreService.evaluateScore(name, cpf, city, age, income)

        val expectedScore =
            ((age * ageFactor) + ((income / incomeFactorDivider) * incomeFactorMultiplier) + (temperature * tempFactor))

        assertEquals(Math.floor(expectedScore).toInt(), scoreRequest.score)
    }

    @Test
    fun `evaluateScore should throw exception for underage request`() {
        assertThrows<ValidationException> {
            scoreService.evaluateScore("João Tes", "12345678911", "São Carlos", age = 17, income = 3000.0)
        }
    }

    @Test
    fun `evaluateScore should throw exception for empty name`() {
        assertThrows<ValidationException> {
            scoreService.evaluateScore("", "12345678911", "São Carlos", age = 30, income = 3000.0)
        }
    }

    @Test
    fun `evaluateScore should throw exception for empty cpf`() {
        assertThrows<ValidationException> {
            scoreService.evaluateScore("João Tes", "", "São Carlos", age = 30, income = 3000.0)
        }
    }

    @Test
    fun `evaluateScore should throw exception for empty city`() {
        assertThrows<ValidationException> {
            scoreService.evaluateScore("João Tes", "12345678911", "", age = 30, income = 3000.0)
        }
    }

    @Test
    fun `evaluateScore should throw exception for zero income`() {
        assertThrows<ValidationException> {
            scoreService.evaluateScore("João Tes", "12345678911", "São Carlos", age = 30, income = 0.0)
        }
    }

    @Test
    fun `getScoreHistoryByCpf should throw exception for empty cpf`() {
        assertThrows<ValidationException> {
            scoreService.getScoreHistoryByCpf("")
        }
    }

    @Test
    fun `getScoreHistoryByCpf should return score history when exists`() {
        val cpf = "12345678911"
        val expectedHistory = listOf(
            ScoreRequest("João Tes", cpf, "São Carlos", 3000.0, 30, 25.0, 150, true)
        )
        `when`(scoreRepository.getScoreHistoryByCpf(cpf)).thenReturn(expectedHistory)
        val history = scoreService.getScoreHistoryByCpf(cpf)
        assertEquals(expectedHistory, history)
    }

    @Test
    fun `getScoreHistoryByCpf should return empty list when history does not exists`() {
        val cpf = "12345678911"
        `when`(scoreRepository.getScoreHistoryByCpf(cpf)).thenReturn(emptyList<ScoreRequest>())
        val history = scoreService.getScoreHistoryByCpf(cpf)
        assertEquals(emptyList<ScoreRequest>(), history)
    }
}