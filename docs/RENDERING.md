# Renderer architecture

Axiomata has no custom in-world atlas textures or baked block geometry. Placed components use invisible block states with ordinary collision shapes, while block-entity renderers construct the visible machine at runtime.

## Filled procedural forms

`RenderSurfaces` emits double-sided translucent quads into Minecraft's lightning render layer. It builds discs, annuli, iris petals, cardinal ribbons, and faceted diamonds. `RenderPrimitives` remains available for a small number of fine halo and progress accents, but lines are no longer the dominant visual material.

`AstralAnchorRenderer` changes composition according to synchronized ritual state:

- an incomplete frame displays a compact dormant crystal;
- a complete but unpowered frame displays a closed iris;
- an operational network opens a breathing, seven-petal membrane with a solid white core;
- four short pedestal ribbons show that the cardinal ritual arms are energized;
- one faceted crystal orbits for every unlocked archive page;
- three solid-light matter flares appear briefly after access;
- a single fine occupancy arc remains as a restrained information accent.

`AstralNodeRenderer` gives the construction blocks separate silhouettes. Conduits use filled connection ribbons, pylons use levitating nested prisms, and collectors use a twelve-segment dish, crossed sky beam, and descending light droplet. Vanilla reverse-portal and end-rod particles add sparse motion without custom particle textures.

## Archive screen

`AstralStorageScreen` never samples a GUI texture. `DrawContext` primitives create cut-corner panels, deep slot wells, filled diamond page marks, and a synchronized resonance meter. The old star field and dotted constellation web were removed to protect item readability.

## Synchronization cost

Anchors scan once per second and only synchronize when ritual, power, capacity, or occupancy display state changes. Nodes receive a timestamped pulse; they synchronize when activation, color, or strength changes and expire locally after the heartbeat stops. Continuous rotation, breathing, orbiting, and particle positions are derived from world time on the client.
