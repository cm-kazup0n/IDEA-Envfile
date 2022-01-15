package com.github.kazup0n.envfile.types

sealed class Either<out L, out R> {
    fun isRight(): Boolean {
        return when (this) {
            is Right -> true
            is Left -> false
        }
    }

    data class Right<out L, out R>(val get: R) : Either<L, R>()
    data class Left<out L, out R>(val get: L) : Either<L, R>()
}
