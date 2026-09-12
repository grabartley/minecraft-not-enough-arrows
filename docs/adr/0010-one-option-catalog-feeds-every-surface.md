# ADR 0010: One option catalog feeds every surface that reads configuration

- **Status:** Accepted
- **Date:** 2026-08-23

## Context

The server configuration record holds twenty-six settings across four arrow families. Three separate places need to walk that list: the command tree, which builds a node per setting; `/notenougharrows status`, which prints each setting and its current value; and now the Mod Menu screen, which draws a control per setting.

Each of those had, or would have had, its own hand-written table naming every setting, reading it off the record, and formatting it. Three tables that must agree, with nothing enforcing that they do. The failure is quiet and specific: a setting added to the command tree but forgotten in the screen is invisible to any player who does not use commands, and nothing fails to compile, no test goes red, and the omission surfaces as a bug report months later.

## Decision

Settings are described once, as a catalog of `ConfigOption` values under `com.grahambartley.notenougharrows.config.option`. An option carries its identifier, its bounds, a reader that pulls its value off a subject, and a writer that returns a new subject with that value changed. `ServerConfigOptions` groups them into the four family sections in the order the status output uses.

`ConfigStatusLines` builds its output from the catalog rather than from a table of its own. The settings screen builds its controls from the same catalog. A setting exists in both surfaces or in neither.

The catalog is generic in its subject, so client state uses the same option types with `ClientState` in place of `NotEnoughArrowsConfig`. The screen therefore draws both stores through one widget factory while the type it is holding still says which store a control belongs to.

The command tree keeps its own builders. Brigadier nodes need argument types, suggestion providers, and per-setting feedback that an option descriptor would have to grow fields to express, and the identifiers those builders use come from the same `ConfigSettings` constants the catalog does, so the two cannot drift on naming.

## Consequences

Adding a setting means adding a field to the record and an entry to its family catalog. The status output and the settings screen both pick it up with no further edit.

Because writers return a new configuration rather than mutating one, an option is testable without a game: write a value, read it back, and confirm every other setting is untouched. That last check is what catches the copy-paste error a nested `with` chain invites, and it runs for every option in the catalog rather than for the ones someone thought to test.

Accepted drawback: the catalog is a layer of indirection over what could be plain field access, and a reader chasing one setting now reads a lambda pair instead of a line. The trade is that the list of settings has one home, which is the property that was actually failing to hold.
