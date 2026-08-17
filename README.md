# More Arrows

A Fabric mod for Minecraft 1.21.1.

Expands the arrow types available in Minecraft with new craftable arrows that carry unique effects.

## Planned features

- TNT arrow
- Ender pearl arrow
- Lead (rope) arrow
- Glow ink arrow
- Slime arrow
- Fire charge arrow
- Wind arrow
- Redstone arrow

## Development

Gradle + Fabric Loom toolchain with Spotless formatting, JaCoCo coverage, Fabric GameTest, and CI/CD via GitHub Actions, sharing the workflow of [minecraft-dogs-unleashed](https://github.com/grabartley/minecraft-dogs-unleashed).

- `./gradlew check` runs formatting, unit tests, and side-safety verification
- `./gradlew runClient` launches the dev client
- `./gradlew runGametest` runs the in-game test suite
