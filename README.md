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

## Fire Patches

Arrows that leave fire behind share one system rather than each placing blocks of their own, so a server owner has one set of rules to reason about and one switch to turn all of it off.

| Rule | Behaviour |
|---|---|
| Where fire may go | Only where vanilla itself would hold it. The position has to be air, and the fire state vanilla would use there has to report that it can be placed |
| Which fire is used | Whatever vanilla picks for that surface, so soul soil burns with soul fire |
| Uneven ground | Each column of the patch settles onto the nearest surface around the impact height rather than hanging in the air or burying itself in a block |
| Protection | A shot fired by a player is checked with the world's own permission check, which is what carries spawn protection and the world border. A dispensed shot has no player behind it, so it is checked against the world border alone |
| Lifetime | The server owns every patch it lights and puts it out once the configured duration elapses. A position that is no longer fire by then is left alone, so building over a burning block keeps your block |

`explosive.firePatchRadius` and `explosive.firePatchDurationTicks` set the size and the lifetime, and both are editable from the command tree and the settings screen like every other option. Setting either one to zero places no fire at all, which is the switch for a server that wants explosive arrows without the fire.

Expiry is tracked against the world clock in memory rather than written into the world save, so fire lit before a server restart is not put out by the mod afterwards and goes out the way vanilla fire does.

## Fuses and Countdowns

Explosive arrows do not detonate on impact. Once one comes to rest its fuse starts burning, beeping as it goes, and the blast follows when the countdown runs out. Every explosive arrow shares one fuse system rather than each running a timer of its own.

| Rule | Behaviour |
|---|---|
| What carries a fuse | Whatever the arrow came to rest in. An arrow embedded in a block carries its own fuse; an arrow that hit a mob hands the fuse to that mob, so the charge travels with it and goes off wherever it ends up |
| How long it burns | `explosive.<tier>.delayTicks`, read per tier, so gunpowder, TNT and fire charge arrows each keep their own timing |
| Detonating on contact | A tier delay of zero signals detonation the moment the arrow lands, with no countdown at all. Supported, and not the default for any tier |
| Beep cadence | Derived from how much of the delay is left. The gap between beeps only ever shortens, so the countdown reads as accelerating without any interface element. `explosive.beepVolume` sets how loud it is, and zero mutes it without stopping the fuse |
| Losing the carrier | A carrier that dies, and a player who disconnects, take their fuse with them. Nothing detonates |
| Unloaded chunks | A fuse whose carrier is not loaded holds where it is rather than burning down, and resumes when the carrier comes back. A carrier that never returns has its fuse dropped after a minute |

The fuse itself knows nothing about explosions. It signals that a countdown finished and hands over the world, the carrier, and the spent fuse, and each arrow tier decides what its blast looks like.

Because the blast position is read from the carrier at the moment the fuse expires, there is no path that detonates at a stale position. Fuse state is server-owned and lives in memory only, so a restart mid-countdown defuses what was burning rather than resuming it.

## Block Anchors

Arrows that attach themselves to the world share one anchoring system rather than each tracking their own hold, so the grapple arrow and the rope arrow agree on what counts as something worth holding onto and on when a hold is lost.

| Rule | Behaviour |
|---|---|
| What can be anchored to | Anything with a collision shape that is not air, not replaceable, and not a fluid. Full blocks, slabs, stairs, fences and glass all hold; grass, water, torches and open air do not |
| Who owns an anchor | Exactly one owner, identified by UUID, holds at most one anchor per world. Anchoring again replaces the previous hold rather than stacking a second one |
| Sharing a block | Any number of owners may anchor to the same block at once, and each hold is released on its own |
| Losing the block | The server checks every anchor each world tick and releases any whose block has been broken or replaced by a different block |
| Lifetime | Every anchor carries an expiry tick and is released once that tick passes, so no hold outlives its purpose |
| Leaving | Dying or disconnecting releases every anchor that player held in every world, and stopping the server clears all anchor state |

Anchor state is server-owned and lives in memory only. The client is never the authority on where an anchor is, and nothing is written into the world save, so no anchor survives a restart.

## Grapple Arrow

The grapple arrow hooks into the first block it hits and reels its shooter to it. The pull is a server-owned session, one per player, ticked alongside the anchor it holds, and [ADR 0004](docs/adr/0004-grapple-is-a-ticked-session.md) covers why it applies velocity rather than repositioning the player.

| Rule | Behaviour |
|---|---|
| What starts a pull | An arrow shot by a player landing in a block the anchoring system will hold onto. A dispensed arrow has no player behind it, so it embeds and pulls nobody |
| Reach | `grapple.maxRangeBlocks`, measured from the player to the centre of the block hit. An arrow that lands further away embeds without pulling |
| How the player moves | The server sets the player's velocity toward the anchor each tick and lets vanilla send the velocity update the client already knows how to apply. Nothing is ever repositioned, so the client's own movement prediction is never fought |
| Speed | `grapple.pullSpeed`, read fresh every tick, so an operator changing it mid-pull changes that pull. The pull carries the gravity the client is about to subtract, so the configured speed is the speed the player actually travels rather than an upper bound gravity quietly eats into |
| One at a time | A player is pulled by at most one grapple. Firing again replaces the first and takes the new anchor with it, rather than stacking a second pull |
| Arrival | The pull stops once the player is within reach of the anchor |
| Losing the block | The anchor is released the moment its block is broken or replaced, and the session ends with it |
| Running long | Every session carries a tick budget worked out from the distance it set out to cover, so a pull that cannot finish ends rather than stalling forever |
| Leaving | Dying or disconnecting ends the pull |

The server counts the consecutive ticks a player spends airborne without descending and disconnects anyone past its limit, which is the check that stops flight hacks. A pull is the mod deliberately holding a player in the air, so the mod clears that counter for as long as it is pulling, and [ADR 0015](docs/adr/0015-the-mod-owns-the-flight-check-while-it-moves-a-player.md) covers why. Without it, a slow pull across a long distance disconnects the very player it is carrying.

Session state is server-owned and lives in memory only, so a restart mid-pull drops the pull rather than resuming it.

Like every arrow in the mod, it is craftable at a crafting table from eight arrows around one tripwire hook, yielding eight, and [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why that route is never gated behind the fletching table station.

## Sounds

The mod's sound assets live under `assets/more-arrows/sounds/` and are declared in `assets/more-arrows/sounds.json`, keyed by the same path the `SoundEvent` is registered under in `ModSounds`.

| Sound | Used for |
|---|---|
| `more-arrows:countdown_beep` | The single beep every explosive arrow plays while its fuse burns |

The countdown communicates urgency through cadence rather than through different sounds: one short beep is replayed at a shortening interval as detonation approaches, so a player who hears the beeps speeding up knows to move. Keeping it to one asset is what makes that escalation smooth, because the interval is the only thing changing.

Every sound asset is mono. Minecraft only applies distance attenuation and stereo panning to mono sounds, so a stereo asset would play at full volume anywhere in the world and a player could not tell where the arrow counting down actually is.

## Textures

Texture assets live under `assets/more-arrows/textures/`, and each ships alongside a palette-mapped text source in `art/sprites/`. The text source is the thing that gets edited and reviewed: one character per pixel with the palette declared at the top, so a change to the art reads as a real diff rather than as a swapped binary.

| Texture | Source | Used for |
|---|---|---|
| `textures/item/grapple_arrow.png` | `art/sprites/grapple_arrow.sprite.txt` | The grapple arrow's item sprite |
| `textures/item/rope_arrow.png` | `art/sprites/rope_arrow.sprite.txt` | The rope arrow's item sprite |
| `textures/item/glow_ink_arrow.png` | `art/sprites/glow_ink_arrow.sprite.txt` | The glow ink arrow's item sprite |
| `textures/item/wind_arrow.png` | `art/sprites/wind_arrow.sprite.txt` | The wind arrow's item sprite |
| `textures/item/redstone_arrow.png` | `art/sprites/redstone_arrow.sprite.txt` | The redstone arrow's item sprite |
| `textures/item/gravity_arrow.png` | `art/sprites/gravity_arrow.sprite.txt` | The gravity arrow's item sprite |
| `textures/item/ricochet_arrow.png` | `art/sprites/ricochet_arrow.sprite.txt` | The ricochet arrow's item sprite |
| `textures/block/rope.png` | `art/sprites/rope.sprite.txt` | The climbable rope the rope arrow leaves behind |

The three utility arrows are the family that has to read as tools rather than as weapons, so none of them carries a blade. Each one instead takes the silhouette of the ingredient it is crafted from: a bulging sac for the glow ink arrow, an open vortex ring for the wind arrow, and a compact faceted crystal for the redstone arrow. That split matters more than colour does, because the redstone arrow and the TNT arrow are both red and the glow ink arrow and the wind arrow are both pale and cold. A player picking between them at hotbar size is reading the shape.

The two physics arrows have the same job of reading as terrain manipulation rather than as damage, and each solves it differently. The gravity arrow puts a slime cube on the tip and a second, smaller cube already falling away beneath it, because what the arrow does to a block is only sayable as a second shape: a head alone can be heavy, but it cannot be falling. The head keeps slime's darker inner cube so it is read as slime rather than as any green block, and it stays square where the glow ink sac is round, since those are the mod's two soft heads.

The ricochet arrow is the harder of the two, because the grapple arrow is also hook derived and the two must never be confused in a hotbar. They are separated by silhouette before colour: the grapple splays three prongs wide across the canvas, and the ricochet is a single narrow crook curling back over itself. Colour then reinforces it, warm iron against the grapple's cold blue steel, and dark enough that it does not drift toward the wind arrow's pale ring either.

The rope block is the one texture with a tiling contract, because a descent stacks it vertically and any mismatch across the tile boundary reads as a seam running the whole length of the drop. Its strand grooves step one column per row on a four row cycle, and sixteen divides by four, so row fifteen hands off to row zero mid-diagonal and the twist runs unbroken. Anything that changes the number of rows in that cycle to something other than a factor of sixteen puts a seam back. The single whipping band is what a ladder gets from its rungs, a repeat that tells a player the block is climbable, and it sits away from the tile boundary so it never reads as the seam it is not.

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

## Settings Screen

Every option is also editable in game through [Mod Menu](https://modrinth.com/mod/modmenu), so a player never has to type a command or edit a file. The screen lists the same settings the command tree exposes, in the same order, with each control constrained to the same range the config record enforces.

Server settings are edited on a draft and sent to the server when the screen closes, so the server stays the authority on what is actually stored. Client settings are written straight to the client state file.

| Session | Server settings | Client settings |
|---|---|---|
| Singleplayer | Editable | Editable |
| Multiplayer, operator | Editable | Editable |
| Multiplayer, not an operator | Read-only, with the reason shown under the title | Editable |
| Title screen, no world joined | Read-only, showing defaults | Editable |

The server checks operator permission again when the update arrives, so a client that ignores the read-only state changes nothing. A refused update is answered with a fresh sync, which puts the client's view back on the server's values.

## Commands

Every server config option is adjustable at runtime, so a server owner on a headless box never has to edit a file or restart. Changes are written to the world save and pushed to connected clients immediately.

| Command | Permission | Purpose |
|---|---|---|
| `/morearrows` | Anyone | Lists the commands the caller is allowed to run |
| `/morearrows status` | Anyone | Prints every setting and its current value |
| `/morearrows config reset` | Operator (level 2) | Restores every setting to its default |
| `/morearrows config <family> <option> <value>` | Operator (level 2) | Sets one option |

`<family>` is `explosive`, `grapple`, `utility`, or `physics`, mirroring how the config file nests its settings. `/morearrows status` prints setting names in the same `family.option` form the command tree uses, so a reported name maps directly onto the command that changes it. The settings screen and `/morearrows status` both read one shared option catalog, so a setting can never appear in one and be missing from the other.

Values are checked against the same bounds the config record enforces. A value outside them is rejected with an error naming the accepted range, rather than being silently clamped the way a hand-edited file is on load.

The gravity arrow block exclusion list is edited rather than replaced:

```
/morearrows config physics gravityblockexclusions add <block>
/morearrows config physics gravityblockexclusions remove <block>
/morearrows config physics gravityblockexclusions clear
```

## Fletching Recipes

The fletching table station has its own recipe type, `more-arrows:fletching`, so the station can offer this mod's arrows at a better exchange rate than a crafting table without ever replacing the crafting table route. Recipes are datapack driven, so a pack author changes the rates, or adds arrows of their own, without touching code.

A recipe is an unordered list of ingredients, each with the count it demands, and one result carrying its own count:

```json
{
"type": "more-arrows:fletching",
"ingredients": [
	{ "ingredient": { "item": "minecraft:arrow" }, "count": 4 },
	{ "ingredient": { "item": "minecraft:tnt" } }
],
"result": { "id": "minecraft:arrow", "count": 8 }
}
```

| Field | Meaning |
|---|---|
| `ingredients` | One to nine entries. Each `ingredient` is a vanilla ingredient, so `{ "item": ... }`, `{ "tag": ... }`, and a list of either all work |
| `count` | How many of that ingredient the station demands. Optional, one to sixty-four, defaults to one |
| `result` | A vanilla item stack, so `count` sets how many arrows come out |

| Rule | Behaviour |
|---|---|
| Slot order | Ignored. Each ingredient claims one slot, no two claim the same slot, and nothing is left over |
| Leftover items | An item the recipe did not ask for stops the match rather than being quietly ignored |
| Shared items | Two ingredients that accept the same item need two separate stacks, exactly as shapeless crafting already behaves |
| A bad recipe | Reported as a load error naming that one file, leaving the rest of the pack to load |

The station interface itself ships separately. The recipe type, the station's screen, and the recipes the mod ships are each their own piece of work, and no arrow is blocked on any of them: [ADR 0002](docs/adr/0002-crafting-table-always-works.md) explains why every arrow stays craftable at a crafting table regardless.

## Recipe Viewers

[EMI](https://modrinth.com/mod/emi) and [JEI](https://modrinth.com/mod/jei) both show an information page beside each of the mod's arrows, covering what the arrow does beyond what its recipe already says. Neither viewer holds content of its own. Both read one shared list, so the two can never disagree about what an arrow does.

| Piece | Holds |
|---|---|
| `InfoEntry` | The items an entry covers and the translation keys describing them |
| `RecipeViewerInfo` | The entry list, built from the arrows the mod registered |
| `en_us.json` | Every word a player reads |
| `MoreArrowsEmiPlugin` | The adapter that hands that list to EMI, holding no content of its own |

The list is derived from registration rather than hand-written, so an arrow cannot ship without an entry. Each entry is the arrow's own description followed by a shared line about firing and recovery, which is true of every arrow and stated once.

Neither viewer is bundled into the jar, and `./gradlew check` fails if either ever is.

## Documentation

- [`docs/standards.md`](docs/standards.md) covers the engineering standards shared across these mods
- [`docs/adr/`](docs/adr/) records the architecture decisions behind this mod and the reasoning for each
