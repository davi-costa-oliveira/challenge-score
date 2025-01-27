package org.blipay.repository

import org.blipay.model.ScoreRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScoreRepositoryTest {

    private lateinit var scoreStorage: MutableList<ScoreRequest>

    private lateinit var scoreRepository: ScoreRepository

    @BeforeEach
    fun setUp() {
        scoreStorage = mutableListOf()
        scoreRepository = ScoreRepository(scoreStorage)
    }

    @Test
    fun `save should store scoreRequest in the repository`() {
        val scoreRequest = ScoreRequest(
            name = "João Test",
            cpf = "12345678901",
            city = "São Carlos",
            income = 5000.0,
            age = 30,
            temperature = 24.0,
            score = 150,
            approved = true
        )

        scoreRepository.save(scoreRequest)

        assertTrue(scoreStorage.contains(scoreRequest))
    }

    @Test
    fun `getScoreHistoryByCpf should return all entries with the given cpf`() {
        val scoreRequest1 = ScoreRequest("João Test", "12345678901", "São Carlos", 5000.0, 30, 24.0, 150, true)
        val scoreRequest2 = ScoreRequest("João Test", "12345678901", "São Carlos", 4500.0, 25, 22.0, 140, false)
        val scoreRequest3 = ScoreRequest("Maria Test", "98765432100", "Campinas", 6000.0, 35, 26.0, 160, true)
        scoreStorage.addAll(listOf(scoreRequest1, scoreRequest2, scoreRequest3))

        val result = scoreRepository.getScoreHistoryByCpf("12345678901")

        assertEquals(2, result.size)
        assertTrue(result.contains(scoreRequest1))
        assertTrue(result.contains(scoreRequest2))
        assertTrue(!result.contains(scoreRequest3))
    }
}