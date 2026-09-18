package uk.co.fredjames.jsonplaceholder.core.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AppConfigTest {
    @Test
    fun `returns the API URL`() {
        assertEquals(AppConfig(apiUrl = "http://localhost:3000"), AppConfig.parse("http://localhost:3000"))
    }

    @Test
    fun `strips trailing slashes so paths can be appended safely`() {
        assertEquals("https://api.example.com", AppConfig.parse("https://api.example.com//").apiUrl)
    }

    @Test
    fun `throws when the API URL is missing`() {
        for (value in listOf(null, "", "  ")) {
            val error = assertThrows(IllegalArgumentException::class.java) { AppConfig.parse(value) }
            assertTrue(error.message.orEmpty().contains("jsonplaceholder.apiUrl is not set"))
        }
    }

    @Test
    fun `throws when the API URL is not http or https`() {
        for (value in listOf("not a url", "ftp://example.com", "javascript:alert(1)", "http://")) {
            val error = assertThrows(IllegalArgumentException::class.java) { AppConfig.parse(value) }
            assertTrue(value, error.message.orEmpty().contains("must be an http or https URL"))
        }
    }
}
