# ADR 0005: Configuration and commands are split by arrow family

- **Status:** Accepted
- **Date:** 2026-08-22

## Context

Every option in every mod here must be adjustable at runtime by an operator through an OP-gated command tree, and persisted in a per-world configuration record.

With a dozen arrows each carrying several options, the direct implementation gives one configuration record holding a flat list of every option in the mod, and one command class registering every node. Both grow linearly with the arrow count, and both are on a path to exceeding the 700-line class limit these mods hold themselves to. A single class holding every option is also a merge conflict on every arrow ticket worked in parallel.

## Decision

The configuration record nests a sub-record per arrow family rather than holding a flat list. The command tree is split into per-family builder classes composed by a root command.

## Consequences

Adding an arrow family adds a sub-record and a builder class rather than growing two shared classes, so no class approaches the size limit as the mod grows. Arrow tickets worked in parallel touch different files.

Grouping options by family also produces a better command tree for the operator using it, since the structure of the commands mirrors the structure of the mod rather than being a flat list of every setting.

The pattern generalises. Any mod here with several feature families should nest its configuration the same way rather than discovering the limit later and refactoring under pressure.

Accepted drawback: slightly more ceremony for the first family, and the nesting is visible in the configuration file, so an operator editing it by hand navigates one level deeper. The command tree, which is the primary interface, hides that entirely.
