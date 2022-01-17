package com.github.kazup0n.envfile.dotenv

import com.github.kazup0n.envfile.types.Either
import java.io.File

object RegexParser {

    private val entry = Regex(pattern = "([^=]*)\\s*=\\s*(.*)")

    fun parse(envFile: File): ParseResult {
        return parse(envFile.readLines())
    }

    internal fun parse(lines: List<String>): ParseResult {
        return ParseResult.build(lines.filter(String::isNotBlank).map(this::parseLine))
    }

    private fun parseLine(s: String): Either<ParseError, Entry> {
        if (s.startsWith("#")) {
            return Either.Right(Entry.CommentEntry(s))
        }
        val kv: MatchResult? = entry.matchEntire(s)
        return if (kv != null) {
            val (_, k, v) = kv.groupValues
            Either.Right(Entry.EnvEntry(k, v))
        } else {
            Either.Left(ParseError.InvalidLine(s))
        }
    }
}
