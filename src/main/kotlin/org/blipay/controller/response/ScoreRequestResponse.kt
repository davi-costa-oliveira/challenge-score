package org.blipay.controller.response

import org.blipay.model.ScoreRequest

data class ScoreRequestResponse(
    val cpf: String,
    val approved: Boolean,
    val score: Int
)

fun ScoreRequest.toResponse() = ScoreRequestResponse(
    this.cpf,
    this.approved,
    this.score
)