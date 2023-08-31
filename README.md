<!-- shields -->
[![](https://img.shields.io/github/issues/mojira/r-isa)](https://github.com/mojira/r-isa/issues)
[![](https://img.shields.io/github/stars/mojira/r-isa)](https://github.com/mojira/r-isa/stargazers)
[![](https://img.shields.io/github/license/mojira/r-isa)](https://github.com/mojira/r-isa/blob/master/LICENSE.md)

# r/isa

<!-- PROJECT LOGO -->
<br/>
<p align="center">
  <a href="https://bugs.mojang.com/">
    <img src="mojira-logo.png" alt="Mojira logo" width="80" height="80">
  </a>

  <h3 align="center">r/isa</h3>

  <p align="center">
    A Reddit bot for generating the snapshot reports posted to <a href="https://reddit.com/r/mojira">the subreddit</a> based on Mojang's bug tracker <a href="https://bugs.mojang.com/">Mojira</a>.
    <br/>
  </p>
</p>

## Installation

If you want to tinker around with the project on your local PC, you can simply go ahead, clone the project and build it with Gradle.

```
git clone https://github.com/mojira/r-isa.git
```

```
./gradlew clean build
```

To run the bot, you need to run the following command and it will use the default configuration:
```
./gradlew build installDist
./build/install/r-isa/bin/r-isa
```

## Built with

This project depends on the following projects, thanks to every developer who makes their code open-source! :heart:

- [Kotlin](https://kotlinlang.org/)
- [Arrow](https://arrow-kt.io/)
- [jira-client](https://github.com/rcarz/jira-client) by [rcarz](https://github.com/rcarz)
- [JRAW](https://github.com/mattbdean/JRAW) by [mattbdean](https://github.com/mattbdean)
- [logback-discord-appender](https://github.com/napstr/logback-discord-appender) by [napstr](https://github.com/napstr)

## Contributing

You're very welcome to contribute to this project! Please note that this project uses [ktlint](https://github.com/pinterest/ktlint) to ensure consistent code.
It runs with `./gradlew clean build`, but you can also run it independently using `./gradlew ktlintCheck`.

## Found a bug in Minecraft?

Please head over to [bugs.mojang.com](https://bugs.mojang.com/), search whether your bug is already reported and if not, create an account and click the grey "Create" button on the top of the page.

## License

Distributed under the GNU General Public License v3.0. See `LICENSE.md` for more information.
