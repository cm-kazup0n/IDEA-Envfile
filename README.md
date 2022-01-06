# IDEA Envfile plugin

<!-- Plugin description -->
With this plugin you can run your java code with environment variables from `.env` file in project root. 
<!-- Plugin description end -->

## Predefined Run/Debug configurations

Within the default project structure, there is a `.run` directory provided containing three predefined *Run/Debug configurations* that expose corresponding Gradle tasks:

![Run/Debug configurations][file:run-debug-configurations.png]

| Configuration name | Description                                                                                                                                                            |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Run Plugin         | Runs [`:runIde`][gh:gradle-intellij-plugin-running-dsl] Gradle IntelliJ Plugin task. Use the *Debug* icon for plugin debugging.                                        |
| Run Tests          | Runs [`:check`][gradle-lifecycle-tasks] Gradle task that invokes `:test` and `detekt`/`ktlint` code inspections.                                                       |
| Run Verifications  | Runs [`:runPluginVerifier`][gh:gradle-intellij-plugin-verifier-dsl] Gradle IntelliJ Plugin task to check the plugin compatibility against the specified IntelliJ IDEs. |


> **TIP:** You can find the logs from the running task in the `idea.log` tab.
> 
> ![Run/Debug configuration logs][file:run-logs.png]


## Continuous integration

Continuous integration depends on [GitHub Actions][gh:actions], a set of workflows that make it possible to automate your testing and release process. Thanks to such automation, you can delegate the testing and verification phases to the CI and instead focus on development (and writing more tests).

In the `.github/workflows` directory, you can find definitions for the following GitHub Actions workflows:

- [Build](.github/workflows/build.yml)
    - Triggered on `push` and `pull_request` events.
    - Runs the *Gradle Wrapper Validation Action* to verify the wrapper's checksum.
    - Runs the `verifyPlugin` and `test` Gradle tasks.
    - Builds the plugin with the `buildPlugin` Gradle task and provides the artifact for the next jobs in the workflow.
    - Verifies the plugin using the *IntelliJ Plugin Verifier* tool.
    - Prepares a draft release of the GitHub Releases page for manual verification.
- [Release](.github/workflows/release.yml)
    - Triggered on `released` event.
    - Publishes the plugin to the Marketplace using the provided `PUBLISH_TOKEN`.
    - Sets publish channel depending on the plugin version, i.e. `1.0.0-beta` -> `beta` channel.
    - Patches the Changelog and commits.
- [Template Cleanup](.github/workflows/template-cleanup.yml) 
    - Triggered once on the `push` event when a new template-based repository has been created.
    - Overrides the scaffold with files from the `.github/template-cleanup` directory.
    - Overrides JetBrains-specific sentences or package names with ones specific to the target repository.
    - Removes redundant files.

All the workflow files have accurate documentation, so it's a good idea to take a look through their sources.

### Dependencies management

This project depends on Gradle plugins and external libraries – and during the development, you will add more of them.
Keeping the project in good shape and having all the dependencies up-to-date requires time and effort, but it is possible to automate that process using [dependabot][gh:dependabot].
Dependabot is a bot provided by GitHub for checking the build configuration files and reviewing any outdated or insecure dependencies of yours – in case if any update is available, it creates a new pull request providing [the proper change][gh:dependabot-pr].

> **Note:** Dependabot doesn't yet support checking of the Gradle Wrapper. Check the [Gradle Releases][gradle-releases] page and update it with:
> ```bash
> ./gradlew wrapper --gradle-version 6.8
> ```

### Changelog maintenance

When releasing an update, it is important to let your users know what the new version offers. The best way to do this is to provide release notes.

The changelog is a curated list that contains information about any new features, fixes, and deprecations. When they are provided, these lists are available in a few different places: the [CHANGELOG.md](./CHANGELOG.md) file, the [Releases page][gh:releases], the *What's new* section of the Marketplace Plugin page, and inside of the Plugin Manager's item details.

There are many methods for handling the project's changelog. The one used in the current template project is the [Keep a Changelog][keep-a-changelog] approach.

### Release flow

The release process depends on the workflows already described above. When your main branch receives a new pull request or a regular push, the [Build](.github/workflows/build.yml) workflow runs multiple tests on your plugin and prepares a draft release.

![Release draft][file:draft-release.png]

The draft release is a working copy of a release, which you can review before publishing. It includes a predefined title and git tag, which is the current version of the plugin, for example, `v0.0.1`. The changelog is provided automatically using the [gradle-changelog-plugin][gh:gradle-changelog-plugin]. An artifact file is also built with the plugin attached. Every new Build overrides the previous draft to keep your *Releases* page clean.

When you edit the draft and use the <kbd>Publish release</kbd> button, GitHub will tag your repository with the given version and add a new entry to the Releases tab. Next, it will notify users that are *watching* the repository, and it will trigger the final [Release](.github/workflows/release.yml) workflow.

### Publishing the plugin

Releasing a plugin to the Marketplace is a straightforward operation that uses the `publishPlugin` Gradle task provided by the [gradle-intellij-plugin][gh:gradle-intellij-plugin]. The [Release](.github/workflows/release.yml) workflow automates this process by running the task when a new release appears in the GitHub Releases section.

> **TIP**: Set a suffix to the plugin version to publish it in the custom repository channel, i.e. `v1.0.0-beta` will push your plugin to the `beta` [release channel][docs:release-channel].

The authorization process relies on the `PUBLISH_TOKEN` secret environment variable, which has to be acquired through the Secrets section of the repository Settings.

![Settings > Secrets][file:settings-secrets.png]

You can get that token in the [My Tokens][jb:my-tokens] tab within your Marketplace profile dashboard.

> **Important:**
> Before using the automated deployment process, it is necessary to manually create a new plugin in the Marketplace to specify options like the license, repository URL, etc.
> Please follow the [Publishing a Plugin][docs:publishing] instructions.


## FAQ

### How to use Java in my project?

Java language is supported by default along with Kotlin.
Initially, there's `/src/main/kotlin` directory available with some minimal examples.
You can still replace it or add next to it the `/src/main/java` to start working with Java language instead.

### How to disable tests or build job using the `[skip ci]` commit message?

Since the February 2021, GitHub Actions [support the skip CI feature][github-actions-skip-ci].
If the message contains one of the following strings: `[skip ci]`, `[ci skip]`, `[no ci]`, `[skip actions]`, or `[actions skip]` – workflows will not be triggered.


## Useful links

- [IntelliJ Platform SDK DevGuide][docs]
- [IntelliJ Platform Explorer][jb:ipe]
- [Marketplace Quality Guidelines][jb:quality-guidelines]
- [IntelliJ Platform UI Guidelines][jb:ui-guidelines]
- [Marketplace Paid Plugins][jb:paid-plugins]
- [Kotlin UI DSL][docs:kotlin-ui-dsl]
- [IntelliJ SDK Code Samples][gh:code-samples]
- [JetBrains Platform Slack][jb:slack]
- [JetBrains Platform Twitter][jb:twitter]
- [IntelliJ IDEA Open API and Plugin Development Forum][jb:forum]
- [Keep a Changelog][keep-a-changelog]
- [GitHub Actions][gh:actions]

[docs]: https://plugins.jetbrains.com/docs/intellij?from=IJPluginTemplate
[docs:intro]: https://plugins.jetbrains.com/docs/intellij/intellij-platform.html?from=IJPluginTemplate
[docs:kotlin-ui-dsl]: https://plugins.jetbrains.com/docs/intellij/kotlin-ui-dsl.html?from=IJPluginTemplate
[docs:plugin.xml]: https://plugins.jetbrains.com/docs/intellij/plugin-configuration-file.html?from=IJPluginTemplate
[docs:publishing]: https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate
[docs:release-channel]: https://plugins.jetbrains.com/docs/intellij/deployment.html?from=IJPluginTemplate#specifying-a-release-channel
[docs:using-gradle]: https://plugins.jetbrains.com/docs/intellij/gradle-build-system.html?from=IJPluginTemplate

[file:use-this-template.png]: .github/readme/use-this-template.png
[file:draft-release.png]: .github/readme/draft-release.png
[file:gradle.properties]: ./gradle.properties
[file:run-logs.png]: .github/readme/run-logs.png
[file:plugin.xml]: ./src/main/resources/META-INF/plugin.xml
[file:run-debug-configurations.png]: .github/readme/run-debug-configurations.png
[file:settings-secrets.png]: .github/readme/settings-secrets.png
[file:template_cleanup.yml]: ./.github/workflows/template-cleanup.yml

[gh:actions]: https://help.github.com/en/actions
[gh:dependabot]: https://docs.github.com/en/free-pro-team@latest/github/administering-a-repository/keeping-your-dependencies-updated-automatically
[gh:code-samples]: https://github.com/JetBrains/intellij-sdk-code-samples
[gh:gradle-changelog-plugin]: https://github.com/JetBrains/gradle-changelog-plugin
[gh:gradle-intellij-plugin]: https://github.com/JetBrains/gradle-intellij-plugin
[gh:gradle-intellij-plugin-running-dsl]: https://github.com/JetBrains/gradle-intellij-plugin#running-dsl
[gh:gradle-intellij-plugin-verifier-dsl]: https://github.com/JetBrains/gradle-intellij-plugin#plugin-verifier-dsl
[gh:releases]: https://github.com/JetBrains/intellij-platform-plugin-template/releases
[gh:build]: https://github.com/JetBrains/intellij-platform-plugin-template/actions?query=workflow%3ABuild
[gh:dependabot-pr]: https://github.com/JetBrains/intellij-platform-plugin-template/pull/73

[jb:confluence-on-gh]: https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub
[jb:download-ij]: https://www.jetbrains.com/idea/download
[jb:forum]: https://intellij-support.jetbrains.com/hc/en-us/community/topics/200366979-IntelliJ-IDEA-Open-API-and-Plugin-Development
[jb:ipe]: https://plugins.jetbrains.com/intellij-platform-explorer
[jb:my-tokens]: https://plugins.jetbrains.com/author/me/tokens
[jb:paid-plugins]: https://plugins.jetbrains.com/docs/marketplace/paid-plugins-marketplace.html
[jb:quality-guidelines]: https://plugins.jetbrains.com/docs/marketplace/quality-guidelines.html
[jb:slack]: https://plugins.jetbrains.com/slack
[jb:twitter]: https://twitter.com/JBPlatform
[jb:ui-guidelines]: https://jetbrains.github.io/ui

[keep-a-changelog]: https://keepachangelog.com
[detekt]: https://detekt.github.io/detekt
[github-actions-skip-ci]: https://github.blog/changelog/2021-02-08-github-actions-skip-pull-request-and-push-workflows-with-skip-ci/
[gradle]: https://gradle.org
[gradle-releases]: https://gradle.org/releases
[gradle-kotlin-dsl]: https://docs.gradle.org/current/userguide/kotlin_dsl.html
[gradle-lifecycle-tasks]: https://docs.gradle.org/current/userguide/java_plugin.html#lifecycle_tasks
[kotlin-for-plugin-developers]: https://plugins.jetbrains.com/docs/intellij/kotlin.html#adding-kotlin-support
