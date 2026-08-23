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

## Firing and recovery

Every arrow this mod adds behaves like a vanilla arrow everywhere a vanilla arrow already works, rather than only on a bow:

| Source | Behaviour |
|---|---|
| Bow | Fires any mod arrow, including with Infinity |
| Crossbow | Fires any mod arrow, and Multishot fires three of them |
| Dispenser | Shoots any mod arrow as a projectile rather than dropping it as an item |

Recovery follows vanilla exactly. An arrow fired in survival is picked back up as the arrow it was fired as, an arrow fired in creative or off an Infinity bow is not recoverable, and an arrow shot by a dispenser is.

An arrow shot by a dispenser has no player behind it. Arrow effects account for that, so no effect misbehaves in a redstone contraption.

## Development

Gradle + Fabric Loom toolchain with Spotless formatting, JaCoCo coverage, Fabric GameTest, and CI/CD via GitHub Actions, sharing the workflow of [minecraft-dogs-unleashed](https://github.com/grabartley/minecraft-dogs-unleashed).

- `./gradlew check` runs formatting, unit tests, side-safety verification, and the check that no recipe viewer code is bundled into the jar
- `./gradlew runClient` launches the dev client
- `./gradlew runClient -Precipe_viewers=true` launches it with JEI and EMI loaded, which are off by default so their overlays stay out of screenshots
- `./gradlew runGametest` runs the in-game test suite

## Configuration

The mod keeps two separate stores, and which one a setting lives in decides who owns it:

| Store | Location | Owner | Reaches clients by |
|---|---|---|---|
| Server config | `<world>/more-arrows/server-config.json` | Server operator, per world | Sync on join and on change |
| Client state | `<config>/more-arrows/client-state.json` | The player, per installation | Never sent anywhere |

Server config decides gameplay and is authoritative. Client state holds interface preferences only, so editing it changes nothing another player can observe. Either file falls back to defaults if it is missing or malformed, keeping a copy of the broken file beside it rather than overwriting it.

## Commands

Every server config option is adjustable at runtime, so a server owner on a headless box never has to edit a file or restart. Changes are written to the world save and pushed to connected clients immediately.

| Command | Permission | Purpose |
|---|---|---|
| `/morearrows` | Anyone | Lists the commands the caller is allowed to run |
| `/morearrows status` | Anyone | Prints every setting and its current value |
| `/morearrows config reset` | Operator (level 2) | Restores every setting to its default |
| `/morearrows config <family> <option> <value>` | Operator (level 2) | Sets one option |

`<family>` is `explosive`, `grapple`, `utility`, or `physics`, mirroring how the config file nests its settings. `/morearrows status` prints setting names in the same `family.option` form the command tree uses, so a reported name maps directly onto the command that changes it.

Values are checked against the same bounds the config record enforces. A value outside them is rejected with an error naming the accepted range, rather than being silently clamped the way a hand-edited file is on load.

The gravity arrow block exclusion list is edited rather than replaced:

```
/morearrows config physics gravityblockexclusions add <block>
/morearrows config physics gravityblockexclusions remove <block>
/morearrows config physics gravityblockexclusions clear
```

## Documentation

- [`docs/standards.md`](docs/standards.md) covers the engineering standards shared across these mods
- [`docs/adr/`](docs/adr/) records the architecture decisions behind this mod and the reasoning for each
