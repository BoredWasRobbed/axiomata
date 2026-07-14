# Renderer architecture

Axiomata deliberately separates simulation from presentation. Ritual matching, inventory consumption, timers, and effects live in the common source set. The client source set reads only synchronized block-entity state and turns it into geometry.

## Geometry pipeline

`RenderPrimitives` is the renderer vocabulary. It emits line pairs into Minecraft's line render layer and supports segments, circles, eight glyph alphabets, and wireframe boxes. There are no baked in-world models and no texture sampling in this path.

- `RitualNexusRenderer` composes a wireframe base, concentric proof circles, two independently rotating and tilted rings, eight orbiting glyphs, cardinal energy paths, and a two-strand rising helix. Ritual progress controls height and radius; the definition supplies color.
- `RuneMarkRenderer` draws a pair of floor circles and one procedurally defined symbol. Charged marks pulse while their nexus is resolving.
- `OfferingPlinthRenderer` draws three nested wireframe prisms and a hovering ring. Its only conventional rendering is the offered vanilla `ItemStack`, so players can identify what they placed.

The nexus renderer opts out of the normal one-block culling box because its cardinal paths reach the plinths three blocks away.

## Synchronization

`SyncedBlockEntity` centralizes update packets and initial chunk NBT. The nexus syncs active ritual ID, progress, duration, color, cooldown, and caster UUID. Plinths sync one inventory stack, while rune marks sync a three-bit rune value and charged flag.

The server sends ritual progress every five ticks. Client renderers interpolate with tick delta, keeping animation smooth without a packet every tick.

## Adding an axiom

Add one `RitualDefinition` in `RitualRegistry.bootstrap()` with:

1. A unique identifier and translation key.
2. Four rune integers in `NW, NE, SE, SW` order.
3. Exactly four item requirements.
4. A duration, RGB color, and server-side effect.

The matching engine treats offerings as an unordered multiset and handles duplicate requirements correctly. No client renderer changes are necessary; new definitions automatically drive nexus color and timing.
