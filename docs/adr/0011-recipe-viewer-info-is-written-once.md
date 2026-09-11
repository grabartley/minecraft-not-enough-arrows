# ADR 0011: Recipe viewer info is written once and adapted per viewer

- **Status:** Accepted
- **Date:** 2026-08-23

## Context

The mod supports two recipe viewers, EMI and JEI. Both can show an information page beside an item: a stack, and a few lines of prose explaining what the item does beyond what its recipe already says. Both APIs accept the same shape, a list of stacks and a list of text lines, behind entirely different plugin interfaces.

The obvious implementation gives each plugin its own list of items and its own strings. That is two copies of the same prose, maintained by hand, in two files that no test compares. The failure is the one ADR 0010 already describes for settings, in a new place: an arrow gains a description in EMI, nobody remembers JEI, and the two viewers disagree about what the mod does. Nothing fails to compile and no test goes red.

It is worse here than for settings, because [ADR 0002](0002-crafting-table-always-works.md) makes recipe viewer discoverability load-bearing. The fletching station's better rates are hidden knowledge unless a viewer surfaces them.

## Decision

Info content lives in one place, `com.grahambartley.notenougharrows.compat.info`, and neither viewer plugin holds content of its own.

An `InfoEntry` names the items it covers and the translation keys that describe them. It holds keys rather than strings, so every player-facing word resolves through the language file and no prose is compiled into a class. `RecipeViewerInfo` builds the entry list, one entry per item, from the arrows the mod actually registered rather than from a hand-written list, so an arrow cannot ship without an entry.

Each entry carries the item's own description key followed by a shared key covering firing and recovery. That second line is true of every arrow the mod adds and is the sort of thing a recipe cannot express, so it is stated once and appended rather than repeated per arrow.

The package sits in the client source set. Recipe viewers are client mods, and nothing on a dedicated server has a reason to build this list.

The entries name items by `Identifier` and not by `Item`. That keeps the whole layer testable without a running game, which is what lets the ordering, the key derivation, and the duplicate rejection be covered by unit tests rather than by looking at a screen.

## Consequences

The viewer plugins shrink to adapters. Each one walks `RecipeViewerInfo.arrowEntries()`, resolves the identifiers against the item registry, and hands the result to its own API. Adding a viewer is a new adapter and no new content.

Adding an arrow means adding one `info.not-enough-arrows.<path>` key to the language file. The entry itself appears with no further edit, because it is derived from registration.

Accepted drawback: forgetting that language key is quiet. The viewer shows the raw key rather than falling back to something readable, and no build step catches it, because language files are client resources and the gametest suite runs on a dedicated server where they are never loaded. The guard is the same one that covers item names and settings labels, which is that a missing key is obvious the first time the item is looked at in game.
