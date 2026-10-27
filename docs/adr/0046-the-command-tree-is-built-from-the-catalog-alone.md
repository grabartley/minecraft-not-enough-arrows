# ADR 0046: The command tree is built from the option catalog alone

- **Status:** Accepted
- **Date:** 2026-10-27
- **Revises:** [ADR 0005](0005-config-split-by-family.md) and [ADR 0033](0033-new-family-commands-are-built-from-the-catalog.md)

## Context

ADR 0005 split the command tree into one builder class per family, so no command class would grow past the size limit. ADR 0033 then built every family subtree from the option catalog, and kept each family class as a single call into `OptionCommandNodes` with its section.

After that, the fifteen family classes held no decisions. Each one repeated the catalog's list of sections, and `VolleyCommand` repeated it a third time in the same order. Adding a family meant three edits to keep in step.

ADR 0033 also named the per-family `*CommandNodesTest` classes as the independent check on the command tree, because they state each bound as a literal rather than reading it from the catalog.

## Decision

`VolleyCommand` mounts one subtree per section of `ServerConfigOptions.sections()`. The family builder classes are gone. Int, float and boolean options share one value node that takes the option itself.

The independent check stays independent. Each family's literal cases live in a CSV file under `src/test/resources/command-bounds/`, and `VolleyCommandTest` runs every row against the real dispatcher. No row is derived from the catalog.

## Consequences

Adding a family means a record, a catalog section, and a CSV file of literal cases. The command tree follows with no further edit.

The size concern in ADR 0005 still holds: the tree is built in a loop, so no command class grows with the number of families.

Accepted drawback: the literal cases repeat each bound a second time, on purpose. A change to a bound fails the command test until its CSV row is updated, which is the point of an independent check.
