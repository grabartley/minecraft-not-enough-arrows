# ADR 0035: The mod's sound category is its namespace

- **Status:** Accepted
- **Date:** 2026-09-28

## Context

IDENT-10 asks for every sound the mod plays to sit in a category that a player and a server can turn down on their own, without touching the game's other sound. Most of the fifty arrows still to come will carry an impact sound under IDENT-7, so this has to be settled before they arrive.

Minecraft's `SoundCategory` is a closed enum. Each value has a slider in the vanilla options screen and a place in the options file. Adding a value means extending an enum at runtime, which Fabric on 1.21.1 offers no API for, and the vanilla screen would still not draw a slider for it. Reusing one vanilla category, such as `NEUTRAL`, would mean the only way to quieten the mod is to quieten every animal in the game too.

Several arrows already play vanilla sounds whose identifiers belong to Minecraft, so a client cannot tell them apart from the same sound played by vanilla.

## Decision

The mod's category is its namespace. Every sound the mod plays is declared in `ModSounds` under a `not-enough-arrows:` identifier. Where the sound is a vanilla one, its `sounds.json` entry is an alias that plays the vanilla event and keeps the vanilla subtitle.

Two settings scale those sounds and nothing else. `sound.volume` is a server setting, applied in `ModSoundPlayer` before the sound is sent, so it reaches every player and an operator controls it. `client.modSoundVolume` is a client setting, applied in `SoundSystem` to any sound in the mod's namespace. They multiply.

Sounds keep whichever vanilla category they already used, so the vanilla sliders still apply on top. The fletching station's menu click is played on the client for the clicking player alone, so only the client setting reaches it.

## Consequences

A player or an operator can quieten the mod with one setting each, and nothing else changes volume.

Every sound needs a mod identifier even when it plays a vanilla event, so reusing a vanilla sound costs a `ModSounds` line and a `sounds.json` alias rather than a `SoundEvents` reference. That friction is deliberate: it puts every borrowed sound on the reviewed list IDENT-8 asks for.

A sound played by calling `World.playSound` or `Entity.playSound` directly would skip `sound.volume`. `ModSoundPlayerTest` fails if any mod source outside `ModSoundPlayer` calls either or names a vanilla `SoundEvents` entry. An explosion's own sound would skip it too, so explosions are created with an empty sound and `ModSoundPlayer` plays the mod's alias at vanilla's loudness.

The hit and pickup sounds vanilla plays for every arrow, modded or not, are the bow's sounds rather than the mod's, so they stay under vanilla's own sliders.

The mod setting is not a slider in the vanilla sound screen. It lives with the mod's other client settings.
