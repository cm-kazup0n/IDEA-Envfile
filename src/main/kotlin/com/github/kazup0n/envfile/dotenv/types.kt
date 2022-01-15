package com.github.kazup0n.envfile.dotenv

import com.github.kazup0n.envfile.types.Either

typealias Key = String
typealias Value = String
typealias Comment = String

sealed class ParseError {
    data class InvalidLine(val line: String) : ParseError()
    data class DuplicatedKey(val key: String, val prevValue: String) : ParseError()
}

class ParseResult(val entries: Map<Key, Value>, val comments: List<Comment>, val errors: List<ParseError>) {
    companion object {
        fun build(parsed: List<Either<ParseError, Entry>>): ParseResult {
            val entries = mutableMapOf<Key, Value>()
            val comments = mutableListOf<Comment>()
            val errors = mutableListOf<ParseError>()

            parsed.forEach {
                when (it) {
                    is Either.Right -> when (it.get) {
                        is Entry.CommentEntry -> comments.add(it.get.comment)
                        is Entry.EnvEntry -> {
                            val prev = entries.put(it.get.key, it.get.value)
                            if (prev != null) {
                                errors.add(ParseError.DuplicatedKey(it.get.key, prev))
                            }
                        }
                    }
                    is Either.Left -> errors.add(it.get)
                }
            }
            return ParseResult(entries, comments, errors)
        }
    }
}


sealed class Entry {
    data class EnvEntry(val key: Key, val value: Value) : Entry()

    data class CommentEntry(val comment: Comment) : Entry()
}

