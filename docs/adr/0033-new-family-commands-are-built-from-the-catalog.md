# ADR 0033: New family command trees are built from the option catalog

- **Status:** Accepted
- **Date:** 2026-09-27

## Context

[ADR 0010](0010-one-option-catalog-feeds-every-surface.md) made the option catalog the single source for the status output and the settings screen, and left the command tree on hand-written builders. Those builders repeat each setting's bounds and writer a second time, so the only thing keeping the command tree in step with the catalog is a test that walks the status output and tries to set each entry.

Six more families (traversal, terrain, agriculture, discovery, chaos, social) arrive with around sixty settings between them. Writing each one a second time in a hand-built command class is the drift ADR 0010 was written to stop, at twice the size.

The reason ADR 0010 gave for keeping hand-written builders did not hold up. Every node the mod has is one of four shapes: an integer argument, a float argument, a boolean argument, or an identifier list edited with add, remove, and clear. The catalog already knows which shape each setting is and what its bounds are.

## Decision

`OptionCommandNodes` builds a family's command subtree straight from its `ConfigSection`. Each option becomes a node of its own kind, with the option's own bounds as the Brigadier argument range and the option's own writer as the change it applies. An id with more than two segments, such as `traversal.zipline.maxSpanBlocks`, gets one literal per segment, so an arrow's settings share one node.

Each new family still has its own builder class, as CONFIG-10 requires, but the class is a single call into `OptionCommandNodes` with its section. Identifier lists go through `IdentifierListNodes`, which the gravity exclusion list now uses too, so all three lists answer add, remove, and clear with the same checks and the same messages.

The eight families that already existed keep their hand-written builders. Moving them over is a mechanical follow-up with no behaviour change, and it did not belong in the change that added the new families.

## Consequences

For a new family, a setting's bounds are stated once, on the record, and every surface reads them from there. The command tree, the status output, and the screen can only disagree for a new family if the catalog disagrees with itself.

Adding a setting to a new family means a record field, a catalog entry, and English for the screen. The command node follows with no further edit.

Accepted drawback: for a while, two ways of building command nodes sit side by side. A reader looking at `PhysicsCommandNodes` sees the old way and one looking at `TraversalCommandNodes` sees the new one, until the older builders are moved across.
