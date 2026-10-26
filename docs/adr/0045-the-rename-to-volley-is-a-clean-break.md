# ADR 0045: The rename to Volley is a clean break

- **Status:** Accepted
- **Date:** 2026-10-26

## Context

The mod was renamed once before, from More Arrows to Not Enough Arrows, in [ADR 0030](0030-the-rename-is-a-clean-break.md). That record made a clean break and was explicit that its licence came from timing: nothing had been published, so no player held an item or a configuration file under the old identifiers. It also said the decision did not generalise, and that a later rename would have to answer the migration question rather than cite it.

That later rename is this one. Three pre-releases now exist on GitHub, v0.1.0, v0.1.1 and v0.2.0, and their jars have been downloaded a handful of times between them. Nothing has been published to a mod host, there is no stable release, and the project has no storefront listing under the old name. The downloads are development builds handed to testers, not a player base.

The name itself is also weaker than it looked. "Not Enough Arrows" reads as a joke about quantity, while the mod is about what an arrow does when it lands. "Volley" names the thing a player does with a bow, is short enough to type as a command root, and leaves the namespace `volley` free.

The surfaces are the same as the previous rename: the mod id the loader registers, the resource namespace every item, block, recipe, sound, data component and structure template resolves through, the Java package, the server and client configuration directories, the command root, the archives base name, the repository path and the mod metadata links.

## Decision

Rename every one of those surfaces to `volley`, and recognise no previous identifier. No legacy item, block, entity, recipe, sound, data component, structure template, configuration directory or command root is registered, aliased or read.

The break is a real cost this time, and it is accepted rather than denied. A compatibility layer would be permanent code carried for a few pre-release worlds whose owners can re-craft their arrows and re-enter their settings. The pre-release status is the whole of the justification, and it is a narrower one than ADR 0030 had.

The `/nea` alias is dropped rather than renamed. It existed because `notenougharrows` was long to type over SSH; `volley` is six characters, so an alias would add a second entry point for no gain. `/volley` is the only root, and the help output can no longer disagree with the root a player used.

## Consequences

A world created under a pre-release loses its mod items, placed mod blocks and both configuration files. Testers who want to keep a build should finish it before this lands. That is the expected cost of a clean break, and it falls on a small, known group.

The decision still does not generalise. It rests on the absence of a published release, not on a preference for clean breaks. Once a build is on a mod host, a later identifier change has to migrate item ids, entity ids and configuration directories properly, and must not cite this record or ADR 0030 as permission to skip that work.

Git history, tags and the artifacts attached to the old releases keep the old name. They are immutable records of what shipped, not live references, and the repository rename leaves redirects in place for links to them.
