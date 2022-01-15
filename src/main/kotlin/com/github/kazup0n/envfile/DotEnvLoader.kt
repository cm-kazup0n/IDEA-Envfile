package com.github.kazup0n.envfile

import com.github.kazup0n.envfile.dotenv.ParseResult
import com.github.kazup0n.envfile.dotenv.RegexParser
import java.nio.file.Path

object DotEnvLoader {
    fun tryLoad(path: Path): ParseResult {
        return RegexParser.parse(path.toFile())
    }
}
