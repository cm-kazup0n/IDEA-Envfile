package com.github.kazup0n.envfile

import com.intellij.execution.RunConfigurationExtension
import com.intellij.execution.configurations.JavaParameters
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration
import com.intellij.util.io.exists
import java.nio.file.Paths

class AddDotEnvVariablesConfigurationExtension : RunConfigurationExtension() {
    override fun isApplicableFor(configuration: RunConfigurationBase<*>): Boolean =
        configuration !is ExternalSystemRunConfiguration

    override fun <T : RunConfigurationBase<*>?> updateJavaParameters(
        configuration: T,
        params: JavaParameters,
        runnerSettings: RunnerSettings?
    ) {
        val rootDir = Paths.get(configuration?.project?.basePath)
        val envFile = rootDir.resolve(".env")

        if (envFile.exists()) {
            val newEnv = HashMap(params.env)
            val dotenv = DotEnvLoader.asMap(envFile)
            for (entry in dotenv) {
                newEnv[entry.key] = entry.value
            }
            params.env = newEnv
        }
    }
}
