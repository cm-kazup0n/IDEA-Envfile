package com.github.kazup0n.envfile.dotenv

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class RegexParserTest {
    private fun parse(s: String, rs: Map<Key, Value>, es: List<ParseError>) {
        val result = RegexParser.parse(s.split("\n"))
        assertEquals(rs, result.entries)
        assertEquals(es, result.errors)
    }

    @Test
    fun testParse() {
        parse("", emptyMap(), emptyList())
        parse("key=bar", mapOf("key" to "bar"), emptyList())
        // lines delimited by newline
        parse("key=bar\nfoo=bar", mapOf("key" to "bar", "foo" to "bar"), emptyList())
        // duplicated key
        parse("key=v1\nkey=v2", mapOf("key" to "v2"), listOf(ParseError.DuplicatedKey("key", "v1")))
        // multiple newline
        parse("key=bar\n\n\nfoo=bar", mapOf("key" to "bar", "foo" to "bar"), emptyList())
        // comment
        parse("key=bar\n# comment\n\nfoo=bar", mapOf("key" to "bar", "foo" to "bar"), emptyList())
        // non ascii comment
        parse("key=bar\n# コメント\n\nfoo=bar", mapOf("key" to "bar", "foo" to "bar"), emptyList())
        // `in` in value
        parse("key=#=abc", mapOf("key" to "#=abc"), emptyList())
    }
}