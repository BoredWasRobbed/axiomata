# Renderer architecture

Axiomata has no custom in-world texture atlas entries and no baked world geometry. Both the placed anchor and its storage interface are composed at runtime.

## Astral Anchor

`RenderPrimitives` emits colored line pairs into Minecraft's line render layer. It provides segments, full and partial circles, wire boxes, octahedral storage cells, three-axis stars, and cubic Bézier paths.

`AstralAnchorRenderer` composes those primitives into several stateful layers:

- an octagonal physical base, radial braces, counter-rotating circles, and an occupancy arc;
- a vertical astral aperture with three breathing rings, twelve radial gates, and a separate pair of tilted armillary rings;
- one orbiting octahedral cell per unlocked 54-slot page;
- fourteen deterministic stars derived from the archive UUID, connected into a faint constellation canopy;
- six cubic transfer filaments and moving three-axis motes that appear after archive access.

Archive identity generates the palette and constellation seed. Capacity, occupied-slot ratio, and the last-access timestamp are synchronized display metrics, so visual changes describe actual storage state.

## Astral Archive screen

`AstralStorageScreen` extends `HandledScreen` but never samples a GUI texture. It draws panels, borders, 90 slot wells, stars, dotted constellation paths, page markers, labels, and shadows with `DrawContext` primitives.

The screen color comes from the same archive UUID as the anchor. The footer also states whether the access path is an anchor or portable key and prints the short constellation code.

## Network cost

The anchor updates occupancy metrics every ten server ticks and sends block-entity updates only when those metrics change. Last-access time is sent as a timestamp, letting the client animate locally without a countdown packet every tick. Screen contents use vanilla `ScreenHandler` slot synchronization.
