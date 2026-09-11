# ADR 0008: Client state is a separate store from server configuration

- **Status:** Accepted
- **Date:** 2026-08-22

## Context

The mod already persists a server configuration record per world, syncs it to clients, and gates changes behind operator permission. It also needs settings that are not the server's business at all: whether this player wants to see the explosive countdown display, whether they want to hear its sound, how large they want it drawn.

The cheap option is to add those fields to the existing configuration record. It is one file, one codec, one screen to build. The problem is what that implies about ownership. The configuration record is written to disk on the client during a sync, or it is not written at all and every interface preference becomes a per-world value a player cannot change without operator rights. Neither is right, and the first one is worse: once a client writes any part of the configuration file, the boundary that keeps a modified client from choosing its own blast radius is a naming convention rather than a structure.

## Decision

Client state is a second store, with its own record, codec, file, holder, and service, living in the client source set under `com.grahambartley.notenougharrows.client.state`. It is stored in the client config directory rather than in a world save, and it is never sent anywhere.

The separation is enforced by the build rather than by discipline. The classes live in the client source set, so the dedicated server cannot load them, and `verifyMainSourceSideSafety` fails the build if anything under `src/main/java` so much as names the package.

The two stores share the parts that are genuinely the same problem. Reading a JSON document, falling back to defaults when it is missing, empty, or malformed, preserving a broken file rather than overwriting it, and writing through a temporary file so a crash mid-save cannot truncate the real one, all live in `JsonDocumentFile` and are used by both.

## Consequences

A value's location answers the question of who owns it. Anything in the server configuration is authoritative and reaches the client only by sync. Anything in client state is a preference, and no amount of editing it changes what another player sees. The Mod Menu screen edits both, and routes the two kinds differently for reasons that are visible in the type it is holding rather than remembered.

Adding a preference means adding a field to one record. Adding an option an operator controls means adding it to the other. Choosing between them is the design decision, and it is made once, at the point where it is obvious.

Accepted drawback: two parallel stacks of small classes that look alike, and a reader who finds one may not realise the other exists. The shared file layer keeps the duplication to the mapping itself rather than the mechanics, but the symmetry is real and it is the price of the boundary being structural.
