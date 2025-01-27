package org.blipay.repository

import org.blipay.model.ScoreRequest
import org.springframework.stereotype.Component

@Component
class ScoreRepository(
    private val scoreStorage: MutableList<ScoreRequest>
) {

    fun save(scoreRequest: ScoreRequest) {
        scoreStorage.add(scoreRequest)
    }

    fun getScoreHistoryByCpf(cpf: String): List<ScoreRequest> {
        return scoreStorage.filter { it.cpf == cpf }
    }
}