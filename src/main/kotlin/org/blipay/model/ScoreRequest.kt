package org.blipay.model

import java.time.LocalDateTime

data class ScoreRequest(
    val name: String,
    val cpf: String,
    val city: String,
    val income: Double,
    val age: Int,
    val temperature: Double,
    val score: Int,
    val approved: Boolean,
    val timestamp: LocalDateTime = LocalDateTime.now()
)