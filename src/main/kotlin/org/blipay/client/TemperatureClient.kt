package org.blipay.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }
}

@Service
class WeatherApiClient(
    private val restTemplate: RestTemplate,
    @Value("\${weather.api.key}") private val apiKey: String,
    @Value("\${weather.api.url}") private val apiUrl: String,
    @Value("\${weather.default.temperature}") private val defaultTemperature: Double
) {
    val logger: Logger = LoggerFactory.getLogger(WeatherApiClient::class.java)

    fun getTemperatureByCity(city: String): Double {
        try {
            val url = "$apiUrl?q=$city&appid=$apiKey&units=metric"
            val response = restTemplate.getForObject(url, WeatherResponse::class.java)
            return response!!.main.temp
        } catch (e: Exception) {
            logger.error("Using default temperature due to error while requesting temperature to weather API", e)
            return defaultTemperature
        }

    }
}