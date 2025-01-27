package org.blipay.service

import org.blipay.client.WeatherApiClient
import org.blipay.exception.ValidationException
import org.blipay.model.ScoreRequest
import org.blipay.repository.ScoreRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.math.floor
import kotlin.math.roundToInt

@Service
class ScoreService(
    @Value("\${factors.age}") private val ageFactor: Double,
    @Value("\${factors.temp}") private val tempFactor: Double,
    @Value("\${factors.income.multiplier}") private val incomeFactorMultiplier: Double,
    @Value("\${factors.income.divider}") private val incomeFactorDivider: Double,
    private val weatherApiClient: WeatherApiClient,
    private val scoreRepository: ScoreRepository,
    @Value("\${minimum.score}") private val minimumScore: Int,

    ) {

    private fun calculateScore(
        age: Int,
        income: Double,
        temperature: Double
    ): Int {
        val ageComponent = age * ageFactor
        val incomeComponent = (income / incomeFactorDivider) * incomeFactorMultiplier
        val tempComponent = temperature * tempFactor

        val score = ageComponent + incomeComponent + tempComponent

        return floor(score).toInt()
    }

    private fun validateScoreRequest(name: String, cpf: String, age: Int, city: String, income: Double) {
        if (age < 18)
            throw ValidationException("Requester underage not allowed")

        if (name.trim().isEmpty())
            throw ValidationException("Name field can not be empty")

        if (cpf.trim().isEmpty())
            throw ValidationException("Cpf field can not be empty")

        if (city.trim().isEmpty())
            throw ValidationException("City field can not be empty")

        if (income <= 0.0)
            throw ValidationException("income field can not be zero or a negative number")
    }

    private fun validateHistoryRequest(cpf: String) {
        if (cpf.trim().isEmpty())
            throw ValidationException("Cpf field can not be empty")
    }

    fun evaluateScore(name: String, cpf: String, city: String, age: Int, income: Double): ScoreRequest {
        validateScoreRequest(name, cpf, age, city, income)
        val temperature = weatherApiClient.getTemperatureByCity(city)
        val score = calculateScore(age, income, temperature)

        val scoreRequest = ScoreRequest(
            name,
            cpf,
            city,
            income,
            age,
            temperature,
            score,
            score >= minimumScore
        )

        scoreRepository.save(scoreRequest)

        return scoreRequest
    }

    fun getScoreHistoryByCpf(cpf: String): List<ScoreRequest> {
        validateHistoryRequest(cpf)
        return scoreRepository.getScoreHistoryByCpf(cpf)
    }
}