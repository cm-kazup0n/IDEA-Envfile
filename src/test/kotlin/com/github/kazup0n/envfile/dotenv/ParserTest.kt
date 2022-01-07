package com.github.kazup0n.envfile.dotenv

import com.copperleaf.kudzu.parser.ParserContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ParserTest {
    private fun parse(s: String): Map<String, String> {
        return Parser.parse(ParserContext.fromString(s))
    }

    @Test
    fun testParse() {
        assertEquals(parse("key=bar"), mapOf("key" to "bar"))
        // lines delimited by newline
        assertEquals(parse("key=bar\nfoo=bar"), mapOf("key" to "bar", "foo" to "bar"))
        // duplicated key
        assertEquals(parse("key=v1\nkey=v2"), mapOf("key" to "v2"))
        // multiple newline
        assertEquals(parse("key=bar\n\n\nfoo=bar"), mapOf("key" to "bar", "foo" to "bar"))
        // comment
        assertEquals(parse("key=bar\n# comment\n\nfoo=bar"), mapOf("key" to "bar", "foo" to "bar"))
        // non ascii comment
        assertEquals(parse("key=bar\n# コメント\n\nfoo=bar"), mapOf("key" to "bar", "foo" to "bar"))
        // `in` in value
        assertEquals(parse("key=#=abc"), mapOf("key" to "#=abc"))
    }
}
