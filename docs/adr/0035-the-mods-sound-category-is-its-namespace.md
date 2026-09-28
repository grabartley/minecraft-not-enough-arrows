# ADR 0035: The mod's sound category is its namespace

- **Status:** Accepted
- **Date:** 2026-09-28

## Context

IDENT-10 asks for every sound the mod plays to sit in a category that a player and a server can turn down on their own, without touching the game's other sound. Most of the fifty arrows still to come will carry an impact sound under IDENT-7, so this has to be settled before they arrive.

Minecraft's `SoundCategory` is a closed enum. Each value has a slider in the vanilla options screen and a place in the options file. Adding a value means extending an enum at runtime, which Fabric on 1.21.1 offers no API for, and the vanilla screen would still not draw a slider for it. Reusing one vanilla category, such as `NEUTRAL`, would mean the only way to quieten the mod is to quieten every animal in the game too.

Several arrows already play vanilla sounds whose identifiers belong to Minecraft, so a client cannot tell them apart from the same sound played by vanilla.

## Decision

The mod's category is its namespace. Every sound the mod plays is declared in `ModSounds` under a `not-enough-arrows:` identifier. Where the sound is a vanilla one, its `sounds.json` entry is an alias that plays the vanilla event and keeps the vanilla subtitle.

Two settings scale those sounds and nothing else. `sound.volume` is a server setting an operator controls, and it reaches every client with the rest of the synced server config. `client.modSoundVolume` is each player's own. Each client multiplies a mod sound's loudness by both, in `SoundSystem`, after vanilla has clamped the sound's loudness to its 0 to 1 range.

Scaling happens on the client, after the clamp, because a sound's volume on the wire is also its range. An explosion is sent at volume 4, which vanilla reads as full loudness out to 64 blocks. Scaling that 4 down on the server would shorten how far the explosion is heard and leave it as loud as ever for everyone inside the new range. Scaling after the clamp turns the loudness down and leaves the range alone. The server still decides whether a sound exists: at a `sound.volume` of 0, `ModSoundPlayer` sends nothing.

Sounds keep whichever vanilla category they already used, so the vanilla sliders still apply on top. The fletching station's menu click is played on the client for the clicking player alone.

## Consequences

A player or an operator can quieten the mod with one setting each, and nothing else changes volume.

Every sound needs a mod identifier even when it plays a vanilla event, so reusing a vanilla sound costs a `ModSounds` line and a `sounds.json` alias rather than a `SoundEvents` reference. That friction is deliberate: it puts every borrowed sound on the reviewed list IDENT-8 asks for.

A sound played by calling `World.playSound` or `Entity.playSound` directly, or an explosion's own built-in sound, would carry a vanilla identifier and escape both settings. Explosions go through `ModExplosion`, which creates them with vanilla's empty sound and plays the mod's alias at vanilla's loudness and pitch spread. `ModSoundPlayerTest` fails if any mod source other than `ModSoundPlayer` and `ModExplosion` plays a sound, creates an explosion, or names a vanilla `SoundEvents` entry.

A modified client can ignore `sound.volume` above 0, as it can ignore any volume. The setting controls what players hear, not what reaches them.

The hit and pickup sounds vanilla plays for every arrow, modded or not, are the bow's sounds rather than the mod's, so they stay under vanilla's own sliders.

The shock arrow's thunder escapes both settings, because the vanilla lightning bolt it spawns plays its own sound on each client and that bolt is not marked as the mod's there.

A change to either setting applies to sounds that start after it. A long sound already playing keeps its level until it ends.

The mod setting is not a slider in the vanilla sound screen. It lives with the mod's other client settings.
