<h1 align="center">Not Enough Arrows</h1>

<p align="center"><b>Thirteen new arrows for Minecraft 1.21.1 on Fabric.</b><br>
Grapple up a cliff. Hang a rope into a ravine. Blow a hole in a mountain, then teleport into it.</p>

<p align="center">
<a href="https://modrinth.com/mod/not-enough-arrows"><img src="https://img.shields.io/modrinth/dt/not-enough-arrows?logo=modrinth&label=Modrinth%20downloads&color=00AF5C" alt="Modrinth downloads"></a>
<a href="https://github.com/grabartley/minecraft-not-enough-arrows/stargazers"><img src="https://img.shields.io/github/stars/grabartley/minecraft-not-enough-arrows?logo=github&label=Stars&color=4078c0" alt="GitHub stars"></a>
<a href="https://github.com/grabartley/minecraft-not-enough-arrows/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-MIT-yellow.svg" alt="License: MIT"></a>
<a href="https://ko-fi.com/grahambartley"><img src="https://img.shields.io/badge/Ko--fi-Support_Not_Enough_Arrows-009078?logo=ko-fi&logoColor=white" alt="Ko-fi"></a>
</p>

<p align="center">
<a title="Fabric API" href="https://modrinth.com/mod/fabric-api" target="_blank">
	<img src="https://i.imgur.com/Ol1Tcf8.png" width="180" height="60" alt="Fabric API requirement button">
</a>
</p>

## Your bow deserves better than one arrow

Vanilla gives you an arrow that does one thing: it hurts. Every bow you have ever drawn has been a
slightly slower sword.

**Not Enough Arrows** gives that bow thirteen more things to do. The cliff you were going to walk
around becomes a cliff you shoot a hook into and get pulled up. The ravine you were going to bridge
becomes a ravine you drop a rope into. The mountain in your way stops being in your way.

Every one of them is craftable, every one of them is configurable, and every one of them works from
a bow, a crossbow, or a dispenser.

> **Alpha:** Not Enough Arrows is in alpha and feedback is very welcome.
> [Open an issue](https://github.com/grabartley/minecraft-not-enough-arrows/issues/new) and tell me
> what you think.

## Get off the ground

Fire the **grapple arrow** at anything solid and it hooks in and reels you to it. Fall damage is
cancelled when you land, and the arrow comes back to you, so the same one carries you up a mountain
in stages.

The **rope arrow** is the slower, safer version: it hangs a climbable rope beneath whatever it hits,
up to 128 blocks of it. Shoot the lip of a cliff and the way back up is already built. Shoot into a
ravine and you have a way down that does not involve falling.

## Blow something up, eventually

Explosive arrows do not go off on impact. They stick, they beep, the beeping speeds up, and a ring
in the world counts down the time you have left. Plenty of time to admire the angle you fired at,
and just enough to regret it.

There are three tiers and each is built from the one below it, so the blast you get is the blast you
worked up to. The **incendiary arrow** is the odd one out: it never explodes, it just sets
everything nearby alight.

## Move the world instead

The **gravity arrow** cuts the ground out from under whatever you hit. A sphere of blocks stops
being attached to anything and falls, which is a fast way down through a ceiling and a fast way to
ruin someone's floor.

The **glow ink arrow** outlines what you hit through walls, for everyone on the server, so the
creeper behind the ridge is now a problem everybody can see.

## The full set

| | Arrow | What it does | Craft it from |
|---|---|---|---|
| 🪝 | **Grapple** | Hooks the block it hits and reels you to it | Tripwire hook |
| 🪢 | **Rope** | Hangs a climbable rope beneath the block it hits | Lead |
| ✨ | **Glow ink** | Outlines what you hit through walls, for everyone | Glow ink sac |
| 🔴 | **Redstone** | Powers the block it hits, then stops | Redstone dust |
| 💨 | **Wind** | Shoves everything around the impact. Not you | Wind charge |
| 💥 | **Gunpowder** | A blast on a fuse. The first explosive tier | Gunpowder |
| 🧨 | **TNT** | A bigger blast. Built from gunpowder arrows | TNT |
| 🔥 | **Fire charge** | The biggest blast, and it leaves fire. Built from TNT arrows | Fire charge |
| 🕯️ | **Incendiary** | Sets everything nearby alight. Never explodes | Fire charge |
| 🪨 | **Gravity** | Drops the block you hit, like sand | Slime ball |
| ⚙️ | **Ricochet** | Bounces off blocks instead of sticking | Iron nugget |
| 🟣 | **Ender pearl** | Teleports you to wherever it lands | Ender pearl |
| 🌀 | **Recall** | Brings whatever you hit back to you. Built from ender pearl arrows | Fermented spider eye |

Every recipe is the vanilla tipped-arrow shape: **eight arrows around one ingredient, for eight
arrows back.** The tiers stack, so eight gunpowder arrows around TNT gives you TNT arrows, and eight
of those around a fire charge gives you the top tier. Recall is built from ender pearl arrows the
same way.

## Cheaper arrows at the fletching table

Right-click any vanilla fletching table and it opens a crafting station that sells this mod's arrows
at a better rate than a crafting table does. Same ingredients, more arrows out.

Nothing is gated behind it: every arrow stays craftable at a crafting table forever, the station is
just the reward for finding one. The block is untouched vanilla, so uninstalling the mod leaves your
world exactly as it was, and your fletcher keeps their job.

## Quick start

1. Drop Not Enough Arrows and Fabric API into your `mods` folder
2. Load a world, craft eight arrows around a tripwire hook
3. Find the tallest thing nearby and shoot the top of it

No config to edit, no server setup, no extra steps.

## Settings

Everything is adjustable while the server is running, per world, and it sticks across restarts.
Change it in the Mod Menu screen or from the command tree:

```
/nea status                                 read every setting
/nea config <family> <option> <value>       change one
/nea config reset                           back to defaults
```

`/nea` and `/notenougharrows` are the same command. Reading is open to anyone; changing anything
needs operator level 2. The families are `explosive`, `grapple`, `utility`, `physics`, `ender`, and
`fletching`.

The defaults ship the fun version of the mod rather than the safe one, so on a shared server these
are the ones to turn **down**:

- **`explosive.damageTerrain` is on.** Explosive arrows break blocks out of the box. Turn it off and
blasts still throw entities around, they just leave the scenery alone.
- **`ender.recallAffectsPlayers` is on.** A recall arrow can drag another player, and their boat,
back to the shooter. Turn it off and only mobs move.
- **`physics.gravityImpactRadius` is 3**, so a gravity arrow drops a sphere rather than the single
block it hit. Set it to 0 for one block.

None of that gets past spawn protection or the world border. A gravity arrow asks the world for
permission block by block, fire patches are time-boxed and server-owned, and a teleport that would
land somewhere unsafe is refused rather than relocated. The defaults decide how loud the mod is, not
what it is allowed to touch.

<details>
<summary><b>Every setting, with its default and range</b></summary>

Values from a hand-edited file are clamped to their range. Values from a command or the settings
screen are rejected outright, with the accepted range in the error.

| Setting | Default | Range |
|---|---|---|
| `explosive.gunpowder.delayTicks` | 80 | 0 to 200 |
| `explosive.gunpowder.power` | 4.0 | 0.0 to 20.0 |
| `explosive.tnt.delayTicks` | 70 | 0 to 200 |
| `explosive.tnt.power` | 6.0 | 0.0 to 20.0 |
| `explosive.fireCharge.delayTicks` | 60 | 0 to 200 |
| `explosive.fireCharge.power` | 8.0 | 0.0 to 20.0 |
| `explosive.damageTerrain` | on | on or off |
| `explosive.damageEntities` | on | on or off |
| `explosive.firePatchRadius` | 2 | 0 to 8 |
| `explosive.firePatchDurationTicks` | 200 | 0 to 6000 |
| `explosive.beepVolume` | 1.0 | 0.0 to 2.0 |
| `explosive.incendiary.burnRadius` | 3 | 0 to 8 |
| `explosive.incendiary.igniteSeconds` | 5 | 0 to 60 |
| `explosive.incendiary.ignitesBlocks` | on | on or off |
| `grapple.maxRangeBlocks` | 128 | 4 to 128 |
| `grapple.pullSpeed` | 1.5 | 0.1 to 4.0 |
| `grapple.pullAcceleration` | 0.15 | 0.01 to 4.0 |
| `grapple.cancelFallDamageOnArrival` | on | on or off |
| `grapple.returnArrowOnArrival` | on | on or off |
| `grapple.ropeLengthBlocks` | 128 | 1 to 128 |
| `grapple.ropesDecay` | off | on or off |
| `utility.glowDurationTicks` | 200 | 0 to 6000 |
| `utility.redstoneSignalDurationTicks` | 40 | 0 to 1200 |
| `utility.redstoneSignalStrength` | 15 | 1 to 15 |
| `utility.windBurstRadius` | 3.0 | 0.5 to 16.0 |
| `utility.windPushStrength` | 1.0 | 0.0 to 8.0 |
| `physics.gravityImpactRadius` | 3 | 0 to 8 |
| `physics.gravityBlockExclusions` | empty | up to 256 block ids |
| `physics.ricochetBounceCount` | 3 | 0 to 16 |
| `physics.ricochetRetainsDamage` | on | on or off |
| `ender.pearlMaxRangeBlocks` | 128 | 4 to 128 |
| `ender.recallMaxRangeBlocks` | 128 | 4 to 128 |
| `ender.recallAffectsPlayers` | on | on or off |
| `fletching.stationEnabled` | on | on or off |

The gravity arrow's exclusion list is edited rather than replaced:

```
/nea config physics gravityblockexclusions add <block>
/nea config physics gravityblockexclusions remove <block>
/nea config physics gravityblockexclusions clear
```

These three are yours alone. They live on your machine, they are never sent anywhere, and changing
them changes nothing for anyone else:

| Setting | Default | Range |
|---|---|---|
| `client.showCountdownRing` | on | on or off |
| `client.playCountdownSound` | on | on or off |
| `client.countdownRingScale` | 1.0 | 0.5 to 2.0 |

</details>

## Compatibility

Minecraft `1.21.1` on Java `21`, singleplayer or dedicated server, fully multiplayer.

| Dependency | Version | Required | Reason |
|---|---|---|---|
| [Fabric Loader](https://fabricmc.net/use/installer/) | `>=0.16.5` | Yes | Mod loader |
| [Fabric API](https://modrinth.com/mod/fabric-api) | `>=0.107.0+1.21.1` | Yes | Fabric hooks and APIs |
| Mod Menu | any | No | Settings screen in the mods list |
| JEI | any | No | Recipe and info pages |
| EMI | any | No | Recipe and info pages |

**[Mod Menu](https://modrinth.com/mod/modmenu)** puts the settings screen in your mods list.
**[EMI](https://modrinth.com/mod/emi)** and **[JEI](https://modrinth.com/mod/jei)** put every arrow
and every fletching station rate in your recipe lookup. None of these are bundled or required, and
the mod runs identically without them.

## Building it yourself

```bash
./gradlew check        # formatting, unit tests, side-safety, jar checks
./gradlew runClient    # dev client
./gradlew runGametest  # the in-game test suite
```

Add `-Precipe_viewers=true` to `runClient` to launch with JEI and EMI loaded. They are off by
default so their overlays stay out of screenshots.

## Documentation

- [`docs/mechanics.md`](https://github.com/grabartley/minecraft-not-enough-arrows/blob/main/docs/mechanics.md) is the detailed behaviour of every arrow and every shared system
- [`docs/prd.md`](https://github.com/grabartley/minecraft-not-enough-arrows/blob/main/docs/prd.md) is what the mod is: use cases, numbered requirements, and what it deliberately leaves out
- [`docs/adr/`](https://github.com/grabartley/minecraft-not-enough-arrows/blob/main/docs/adr/) is why it is built the way it is
- [`docs/standards.md`](https://github.com/grabartley/minecraft-not-enough-arrows/blob/main/docs/standards.md) is the engineering standards shared across these mods

## Open source

Not Enough Arrows is MIT licensed. Every asset is original work.

If this mod made a mountain more fun to get up, a star on GitHub or a coffee on Ko-fi means a lot.

<a href="https://ko-fi.com/grahambartley" rel="noopener nofollow ugc" target="_blank">
<img src="https://i.imgur.com/FSNi7zk.png" alt="Support me on Ko-fi">
</a>
