package org.blipay.controller

import org.blipay.controller.response.ScoreRequestResponse
import org.blipay.controller.response.toResponse
import org.blipay.model.ScoreRequest
import org.blipay.service.ScoreService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/score")
class ScoreController(
    private val scoreService: ScoreService
) {

    @GetMapping
    fun consultCpf(
        @RequestParam name: String,
        @RequestParam age: Int,
        @RequestParam income: Double,
        @RequestParam city: String,
        @RequestParam cpf: String,
    ): ScoreRequestResponse {
        return scoreService.evaluateScore(name, cpf, city, age, income).toResponse()
    }

    @GetMapping("/history/{cpf}")
    fun consultByCpf(
        @PathVariable cpf: String,
    ): List<ScoreRequest> {
        return scoreService.getScoreHistoryByCpf(cpf)
    }
}