# ADR 0007: Config travels to clients as JSON, not a hand-written packet codec

- **Status:** Accepted
- **Date:** 2026-08-22

## Context

Server configuration is persisted as JSON per world and also has to reach every connected client, on join and whenever an operator changes a value.

The conventional approach, and the one the reference mod in this family uses, is a hand-written packet codec: a `writeConfig` that pushes each field onto the buffer in order and a `readConfig` that pulls them back off in the same order. It is compact and it is what the networking API expects.

It also means every option in the mod is written down twice, in two files, in two formats that must agree. The config record already nests four family sub-records and will grow as arrow families land. Adding a field means editing the record, the JSON mapping, and the packet codec, and forgetting the third produces a fault that no compiler catches: fields silently shift by one position on the wire, and the client ends up with a config whose values are real numbers in the wrong slots.

## Decision

The sync payload carries the config as its JSON string, encoded with the same codec used to read and write the file on disk. There is one serialisation format for this mod's configuration and one place to get it wrong.

The encoded form is bounded, so the block exclusion list is capped at a fixed number of entries and the payload declares an explicit maximum string length rather than relying on the default.

## Consequences

Adding a config option means editing exactly one record. The wire format follows automatically and cannot drift from the file format, because they are the same bytes. The round-trip is already covered by the tests that cover the file format, and the gametest that pushes a payload through a real buffer is checking the transport rather than re-checking the mapping.

A config sync is larger than it would be as packed binary, roughly a kilobyte rather than a hundred bytes. It is sent on join and on change, not per tick, so the cost is paid rarely and is negligible next to the chunk data sent alongside it.

Accepted drawback: the payload is no longer self-describing at the packet level, and a malformed sync degrades to defaults on the client rather than failing loudly at the decode step. That is the right failure mode for a client receiving state it does not own, but it does mean a corrupt sync shows up as surprising client behaviour rather than an exception, so the server, which is authoritative, is where such a bug has to be diagnosed.
