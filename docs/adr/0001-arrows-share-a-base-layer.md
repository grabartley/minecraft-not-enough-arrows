# ADR 0001: Arrows share a base layer

- **Status:** Accepted
- **Date:** 2026-08-22

## Context

The mod ships around a dozen arrow types and expects to add more. Every one of them needs the same five things: an entity, an item that fires from bows and crossbows, a renderer, a recipe, and configuration.

Implemented per arrow, that is the same three hundred lines copied a dozen times, and every fix to firing, pickup, or dispenser behaviour has to be applied a dozen times. Bugs get fixed in some copies and not others, which is worse than not fixing them, because the inconsistency is invisible.

## Decision

A base arrow entity exposes hooks for block hits, entity hits, and per-tick behaviour. A base arrow item handles firing and dispenser behaviour. Registry helpers register an arrow's item, entity type, and renderer from one place.

An arrow type implements only what makes it different.

## Consequences

Adding an arrow is a small, self-contained ticket rather than a repetition of boilerplate, which is the difference between a dozen arrow issues that can be worked in parallel and a dozen that each need review of the same shared behaviour.

Shared concerns are correct once for everything: pickup rules that match vanilla, no pickup when fired in creative, dispenser behaviour, and creative tab placement.

The hooks define the mod's extension vocabulary, so the shape of that vocabulary matters. Two arrows in the physics family both override the block-hit path to replace embed-on-impact, which is a sign the hooks are cut in the right places. An arrow that cannot express itself through the hooks is a signal to extend the base layer rather than to work around it.

Accepted drawback: the base layer must land before any arrow can be built, so it is a hard dependency for most of the repository, and getting the hooks wrong is expensive to correct once a dozen arrows depend on them.
