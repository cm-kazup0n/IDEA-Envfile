package com.github.kazup0n.envfile

import com.github.kazup0n.envfile.dotenv.Parser
import java.nio.file.Path

object DotEnvLoader {
    fun asMap(path: Path): Map<String, String> {
        return Parser.parse(path.toFile())
    }
}
