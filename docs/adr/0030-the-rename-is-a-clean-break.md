# ADR 0030: The rename is a clean break

- **Status:** Accepted
- **Date:** 2026-09-11

## Context

The mod changed its name from More Arrows to Not Enough Arrows. A Minecraft mod's name is not only a label: it is the mod id the loader registers, the namespace every item, block, recipe, sound and structure template resolves through, the directory the server configuration is written into inside a world save, and the root of the command tree an operator types. A rename touches all of them at once.

That raises the usual compatibility question. A namespace change orphans every item already in a player's inventory, every block already placed, and every configuration file already written, because the identifiers those were saved under no longer resolve. The standard answer is a migration layer: register the old ids as aliases, move the old configuration directory on first load, and keep both names alive for a release or two.

The second question is smaller but has the same shape. `notenougharrows` is a long command root to type repeatedly over SSH, which is where an operator configures this mod. A short alias is worth having, and there are two ways to provide one: build a second command tree under the short name, or point the short name at the tree that already exists.

## Decision

The rename is a clean break. No old identifier is aliased, no configuration directory is migrated, and nothing recognises the previous namespace.

This is only defensible because of when it happened. The mod has never been released. No player holds an item under the old namespace, no server has written a configuration file under the old directory name, and no world contains a block that would fail to resolve. A migration layer here would be code with no user, carried forward and maintained on the strength of a hypothetical that the release history says cannot exist. The cost of the break falls entirely on development worlds, which are rebuilt routinely anyway.

The short command root is an alias in the strict sense: `nea` is registered as a Brigadier redirect onto the node `notenougharrows` already registered, not as a tree of its own. The redirect is taken after the alias literal is matched, so every subcommand, every argument type, and every operator gate below the root is the same object reached by both paths.

## Consequences

A development world created before the rename loses its mod items and its settings. That is accepted, and it is the whole of the cost.

The decision does not generalise. It is sound because nothing was published, and a later rename would not have that licence. Any future identifier change has to answer the migration question properly rather than citing this record.

The alias cannot drift from the canonical root, because there is nothing to drift: adding a subcommand to one adds it to both, and there is no second registration to forget to update. The ordering inside the registration is load-bearing in exchange, since the redirect needs the node it points at to exist, which the code enforces by holding that node in a local before the alias is built.

The help output lists the canonical root rather than whichever root the player typed. Someone who finds the mod through `/nea` is shown the name the mod actually goes by, which is the more useful of the two answers.
