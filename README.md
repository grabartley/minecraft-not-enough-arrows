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

- `./gradlew check` runs formatting, unit tests, side-safety verification, and the check that no recipe viewer code is bundled into the jar
- `./gradlew runClient` launches the dev client
- `./gradlew runClient -Precipe_viewers=true` launches it with JEI and EMI loaded, which are off by default so their overlays stay out of screenshots
- `./gradlew runGametest` runs the in-game test suite

## Documentation

- [`docs/standards.md`](docs/standards.md) covers the engineering standards shared across these mods
- [`docs/adr/`](docs/adr/) records the architecture decisions behind this mod and the reasoning for each
