package org.blipay.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.web.client.RestTemplate
import java.io.File

class WeatherApiClientTest {

    @Mock
    private lateinit var restTemplate: RestTemplate
    private val apiKey = "test-api-key"
    private val apiUrl = "http://api.openweathermap.org/data/2.5/weather"
    private val defaultTemperature = 20.0
    private lateinit var objectMapper: ObjectMapper

    private lateinit var weatherApiClient: WeatherApiClient

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        weatherApiClient = WeatherApiClient(restTemplate, apiKey, apiUrl, defaultTemperature)
        objectMapper = ObjectMapper().registerKotlinModule()

    }

    @Test
    fun `getTemperatureByCity should return temperature when response is valid`() {
        val city = "São Carlos"
        val expectedTemperature = 20.43
        val weatherResponse = getResourceText("client/WeatherResponse.json")
        val mockResponse: WeatherResponse = objectMapper.readValue(weatherResponse, WeatherResponse::class.java)

        `when`(
            restTemplate.getForObject("$apiUrl?q=$city&appid=$apiKey&units=metric", WeatherResponse::class.java)
        ).thenReturn(mockResponse)

        val temperature = weatherApiClient.getTemperatureByCity(city)
        assertEquals(expectedTemperature, temperature)
    }

    @Test
    fun `getTemperatureByCity should return default temperature when response is null`() {
        val city = "São Carlos"

        `when`(
            restTemplate.getForObject("$apiUrl?q=$city&appid=$apiKey&units=metric", WeatherResponse::class.java)
        ).thenReturn(null)

        val temperature = weatherApiClient.getTemperatureByCity(city)

        assertEquals(defaultTemperature, temperature)
    }

    @Test
    fun `getTemperatureByCity should return default temperature on exception`() {
        val city = "City With Error"

        `when`(restTemplate.getForObject("$apiUrl?q=$city&appid=$apiKey&units=metric", WeatherResponse::class.java))
            .thenThrow(RuntimeException::class.java)

        val temperature = weatherApiClient.getTemperatureByCity(city)
        assertEquals(defaultTemperature, temperature)
    }

    private fun getResourceText(path: String): String {
        return File(ClassLoader.getSystemResource(path).file).readText()
    }
}