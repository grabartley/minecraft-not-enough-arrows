# Architecture Decision Records

These records capture **why** More Arrows is built the way it is. They are not specifications: what the mod does lives in this repository's issues and epics, which the build workflow reads directly.

A record is historical. When a decision changes, add a new record superseding the old one rather than editing it, so the reasoning behind the original choice is not lost.

Shared engineering standards across all the mods in this family live in [`../standards.md`](../standards.md).

| Record | Decision |
|---|---|
| [0001](0001-arrows-share-a-base-layer.md) | Arrows share a base layer |
| [0002](0002-crafting-table-always-works.md) | Every arrow stays craftable at a crafting table |
| [0003](0003-explosive-arrows-telegraph.md) | Explosive arrows telegraph before detonating |
| [0004](0004-grapple-is-a-ticked-session.md) | Grappling is a server-owned ticked session |
| [0005](0005-config-split-by-family.md) | Configuration and commands are split by arrow family |
| [0006](0006-arrow-identity-is-shared-by-item-and-entity.md) | An arrow's item and entity share one identifier |
| [0007](0007-config-travels-as-json-on-the-wire.md) | Config travels to clients as JSON, not a hand-written packet codec |

## Writing A New Record

Cover the context, the decision, and the consequences, including the drawbacks accepted. A record that lists only benefits is not a decision record, it is an advertisement.

Record reasoning that is not obvious from reading the code. If the next person would arrive at the same choice anyway, it does not need a record.
