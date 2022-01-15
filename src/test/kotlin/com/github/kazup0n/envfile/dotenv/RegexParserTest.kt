package com.github.kazup0n.envfile.dotenv

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class RegexParserTest {
    private fun parse(
        s: String,
        entries: Map<Key, Value> = emptyMap(),
        errors: List<ParseError> = emptyList(),
        comments: List<Comment> = emptyList()
    ) {
        val result = RegexParser.parse(s.split("\n"))
        assertEquals(entries, result.entries)
        assertEquals(errors, result.errors)
        assertEquals(comments, result.comments)
    }

    @Test
    fun testParse() {
        parse("", emptyMap(), emptyList())
        parse("key=bar", mapOf("key" to "bar"), emptyList())
        // lines delimited by newline
        parse("key=bar\nfoo=bar", mapOf("key" to "bar", "foo" to "bar"))
        // duplicated key
        parse("key=v1\nkey=v2", mapOf("key" to "v2"), listOf(ParseError.DuplicatedKey("key", "v1")))
        // multiple newline
        parse("key=bar\n\n\nfoo=bar", mapOf("key" to "bar", "foo" to "bar"))
        // comment
        parse("key=bar\n# comment\n\nfoo=bar", mapOf("key" to "bar", "foo" to "bar"), comments = listOf("# comment"))
        // non ascii comment
        parse("key=bar\n# コメント\n\nfoo=bar", mapOf("key" to "bar", "foo" to "bar"), comments = listOf("# コメント"))
        // `in` in value
        parse("key=#=abc", mapOf("key" to "#=abc"), emptyList())
        // invalid line
        parse("invalidline", emptyMap(), listOf(ParseError.InvalidLine("invalidline")))
    }
}