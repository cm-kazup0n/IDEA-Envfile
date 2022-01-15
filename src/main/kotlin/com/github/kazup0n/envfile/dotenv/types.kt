package com.github.kazup0n.envfile.dotenv

import com.github.kazup0n.envfile.types.Either

typealias Key = String
typealias Value = String
typealias Comment = String

sealed class ParseError {
    abstract fun show(): String
    data class InvalidLine(val line: String) : ParseError() {
        override fun show(): String {
            return "InvalidLine(line=$line)"
        }
    }

    data class DuplicatedKey(val key: String, val prevValue: String) : ParseError() {
        override fun show(): String {
            return "DuplicatedKey(key=$key,prevValue=$prevValue"
        }
    }
}

fun <A> Either<ParseError, Entry>.fold(
    onEntry: (e: Entry.EnvEntry) -> A,
    onComment: (c: Entry.CommentEntry) -> A,
    onError: (e: ParseError) -> A
): A {
    return when (this) {
        is Either.Right -> when (this.get) {
            is Entry.CommentEntry -> onComment(this.get)
            is Entry.EnvEntry -> onEntry(this.get)
        }
        is Either.Left -> onError(this.get)
    }
}

class ParseResult(val entries: Map<Key, Value>, val comments: List<Comment>, val errors: List<ParseError>) {
    companion object {
        fun build(parsed: List<Either<ParseError, Entry>>): ParseResult {
            val entries = mutableMapOf<Key, Value>()
            val comments = mutableListOf<Comment>()
            val errors = mutableListOf<ParseError>()
            parsed.forEach {
                it.fold(
                    onEntry = { e ->
                        val prev = entries.put(e.key, e.value)
                        if (prev != null) {
                            errors.add(ParseError.DuplicatedKey(e.key, prev))
                        }
                    },
                    onComment = { c -> comments.add(c.comment) },
                    onError = { err -> errors.add(err) }
                )
            }
            return ParseResult(entries, comments, errors)
        }
    }
}

sealed class Entry {
    data class EnvEntry(val key: Key, val value: Value) : Entry()

    data class CommentEntry(val comment: Comment) : Entry()
}
