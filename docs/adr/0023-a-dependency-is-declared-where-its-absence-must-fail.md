# ADR 0023: A dependency is declared where its absence must fail

- **Status:** Accepted
- **Date:** 2026-09-09

## Context

This mod was scaffolded from a shared template and inherited the template's dependency block rather than one describing what it uses. Nothing since had checked that each declared dependency is real.

A dependency is declared in four places that drift apart independently, and none of them is what a player reads:

| Location | What it controls |
|---|---|
| `fabric.mod.json` `depends` | A hard requirement. The loader refuses to start without it |
| `fabric.mod.json` `suggests` | Optional, surfaced to the player as a recommendation |
| `build.gradle` dependencies | Whether the library is compiled against, present at runtime, or bundled |
| `build.gradle` Modrinth block | What a launcher installs alongside the mod |

The inherited block carried GeckoLib as a hard `depends`, as a `modImplementation`, as its own Maven repository, and as a `required.project` on Modrinth. The mod imports nothing from it and ships no `.geo.json` or `.animation.json`. Every player, and every dedicated server, was installing a rendering library the mod never touches.

The trap underneath that is worth stating, because it is what makes the wrong answer tempting. **`depends` is not environment-scoped.** A library used only by client code, declared as a hard dependency, still blocks a dedicated server from booting until the operator installs a rendering library on a machine with no renderer. Meanwhile a library the mod genuinely cannot start without, demoted to `suggests` to dodge that, turns a clear "you need X" loader message into a class-loading crash.

## Decision

A dependency is declared according to what its absence must do.

**If the mod cannot function without it, it is a hard `depends`, and it must be genuinely needed by both environments.** Only Fabric Loader, Minecraft, Java, and Fabric API qualify. Fabric API earns it: the main source set uses its command, entity event, lifecycle, gametest, item group, and networking modules, and the client source set adds rendering and block render layer on top of the lifecycle and networking modules it already shares with the main set.

**If the mod works fine without it, it is never a hard dependency, whatever the build needs to compile.** Mod Menu, JEI, and EMI are integrations: each is `modCompileOnly` so the compat classes build, and each sits in `suggests` so a player is told it exists. Each also carries a development runtime entry, Mod Menu unconditionally and the two viewers behind `-Precipe_viewers=true`, so a developer can launch with them without any of that reaching a player. None is bundled, and the build fails if any ever is.

**A library nothing imports is removed outright**, from all four locations at once, rather than left because removing it feels riskier than keeping it. Keeping it is the risk: it is a hard requirement doing nothing.

The Modrinth block mirrors `fabric.mod.json` rather than being maintained separately. What is hard there is `required.project`; what is suggested there is `optional.project`.

## Consequences

The environment question answers itself once the rule is applied. Every hard dependency is needed by both sides, so no hard dependency is environment-scoped and the trap has nothing to catch. Every client-only library is optional, where being client-only costs a dedicated server nothing, because the server never loads the classes that reference it.

Mod Menu now appears in `suggests`, which it did not while its integration shipped. A player running this mod had no way to learn from the metadata that it has a settings screen they could reach.

Removing GeckoLib removes a Maven repository, a runtime dependency, a loader requirement, and a launcher install. The mod's own jar is unchanged, because nothing in it ever referenced the library.

Accepted drawback: the four locations still have to be edited together and nothing enforces that they agree. The rule above is what a reviewer checks against, and the smallest signal that they have drifted is a `depends` entry whose package never appears in `src/`. Enforcing it mechanically would mean a build step reading the loader metadata and grepping the source, which is worth doing if this ever drifts again rather than in anticipation.

Accepted drawback: an optional integration that a player does have installed is still compiled against a pinned API version, so a sufficiently old or new JEI can fail at runtime where the loader would have caught a version mismatch on a hard dependency. That is the price of the integration being optional, and it is the right trade for a library most players will not have.
