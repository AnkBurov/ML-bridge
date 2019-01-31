package io.ankburov.mlbridge

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.ankburov.mlbridge.model.Request
import io.ankburov.mlbridge.model.RequestBusinessData
import io.ankburov.mlbridge.model.RequestTechData
import io.ankburov.mlbridge.utils.badRequest
import io.ankburov.mlbridge.utils.bodyNotNull
import io.ankburov.mlbridge.utils.internalError
import io.ankburov.mlbridge.utils.ok
import org.apache.http.entity.ContentType.APPLICATION_JSON
import org.apache.http.entity.ContentType.TEXT_PLAIN
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.cloud.consul.enabled=false", "spring.cloud.bus.enabled=false",
        "spring.cloud.consul.discovery.enabled=false", "spring.cloud.consul.config.enabled=false"])
@AutoConfigureWireMock(port = 8057)
class ApplicationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun testExternalServicePost() {
        val request = Request(
            techData = RequestTechData(correlationId = "someCorrelationId"),
            businessData = RequestBusinessData(data = listOf(1234, 5))
        )

        stubFor(
            post(urlEqualTo("/mock-ok"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(request.businessData.data)))
                .willReturn(
                    aResponse()
                        .withBody("""{"headers":{"Hello":"World"}}""")
                        .withHeader("Content-Type", APPLICATION_JSON.mimeType)
                )
        )

        val expectedJson = """
        {"techData":{"correlationId":"someCorrelationId"},"businessData":{"data":{"headers":{"Hello":"World"}}}}
    """.trimIndent()

        val actual = restTemplate.postForEntity("/ml/mock-ok", request, String::class.java)
            .ok()
            .bodyNotNull()

        JSONAssert.assertEquals(expectedJson, actual, true)
    }

    @Test
    fun testExternalServiceGet() {
        val externalBody = "Hello world!"
        stubFor(
            get(urlEqualTo("/mock-ok-get"))
                .willReturn(
                    aResponse()
                        .withBody(externalBody)
                        .withHeader("Content-Type", TEXT_PLAIN.mimeType)
                )
        )

        val actual = restTemplate.getForEntity("/ml/mock-ok-get", String::class.java)
            .ok()
            .bodyNotNull()

        assertEquals(externalBody, actual)
    }

    @Test
    fun testBadRequestNullBody() {
        val expectedJson = """
            {"techData":{"correlationId":null,"errorCode":"BadRequest","errorDescription":"HTTP body cannot be empty"},"businessData":null}
        """.trimIndent()

        testBadRequest(null, expectedJson)
    }

    @Test
    fun testBadRequestEmptyCorrelationId() {
        val request = Request(
            techData = RequestTechData(correlationId = ""),
            businessData = RequestBusinessData(data = listOf(1234, 5))
        )

        val expectedJson = """
                {"techData":{"correlationId":null,"errorCode":"BadRequest","errorDescription":"CorrelationId must be present"},"businessData":null}
            """.trimIndent()

        testBadRequest(request, expectedJson)
    }

    @Test
    fun testBadRequestEmptyData() {
        val request = Request(
            techData = RequestTechData(correlationId = "Some"),
            businessData = RequestBusinessData(data = null)
        )

        val expectedJson = """
                {"techData":{"correlationId":Some,"errorCode":"BadRequest","errorDescription":"Data must be present"},"businessData":null}
            """.trimIndent()

        testBadRequest(request, expectedJson)
    }

    @Test
    fun testInternalErrorFromExternalServer() {
        val request = Request(
            techData = RequestTechData(correlationId = "someCorrelationId"),
            businessData = RequestBusinessData(data = listOf(1234, 5))
        )

        stubFor(
            post(urlEqualTo("/mock-error"))
                .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(request.businessData.data)))
                .willReturn(
                    aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                )
        )

        restTemplate.postForEntity("/ml/mock-error", request, String::class.java)
            .internalError()
    }

    @Test
    fun testZuulInternalError() {
        val request = Request(
            techData = RequestTechData(correlationId = "someCorrelationId"),
            businessData = RequestBusinessData(data = listOf(1234, 5))
        )

        val expectedJson = """
                {"techData":{"correlationId":"someCorrelationId","errorCode":"UnknownError","errorDescription":"Connection refused: connect"},"businessData":null}
            """.trimIndent()

        val actual = restTemplate.postForEntity("/ml/mock-nonexisting-server", request, String::class.java)
            .internalError()
            .bodyNotNull()

        JSONAssert.assertEquals(expectedJson, actual, true)
    }

    private fun testBadRequest(request: Request?, expectedJson: String) {
        val actual = restTemplate.postForEntity("/ml/mock-ok", request, String::class.java)
            .badRequest()
            .bodyNotNull()

        JSONAssert.assertEquals(expectedJson, actual, true)
    }
}