# Axiomata

Axiomata is a ritual-engineering storage mod for Fabric 1.20.1. Matter is archived in persistent vaults in the astral plane, but reaching those vaults requires a physical, powered network in the world.

Every placed machine is drawn at runtime from procedural geometry. There are no custom in-world textures or authored block models; the inventory icons alias vanilla item models and the PNG is only the mod-list icon.

## Requirements

- Minecraft 1.20.1
- Fabric Loader 0.15.11 or newer
- Fabric API 0.92.9+1.20.1
- Java 17 to play

## Ritual progression

1. Weave **Astral Thread** from string, amethyst, and an ender pearl. It is the common magical conductor for the rest of the network.
2. Craft an **Astral Anchor**, eight **Ley Conduits**, four **Resonance Pylons**, and a **Starlight Collector**.
3. Place the four pylons exactly three blocks north, east, south, and west of the anchor. Fill both intervening spaces on every arm with conduits.
4. Branch conduits from any ritual arm to a collector. The collector must have open sky directly above it.
5. Wait for the collector to charge the archive, then use the anchor. Right-click diagnostics identify the next missing ritual step.
6. Spend charged resonance on **Astral Cells** to unfold three additional 54-slot pages, and attune an **Astral Key** for remote and cross-dimensional access.

The required frame, viewed from above, is:

```text
      P
      C
      C
P C C A C C P
      C
      C
      P

A = Astral Anchor   C = Ley Conduit   P = Resonance Pylon
```

The collector may sit beside any connected conduit or at the end of a longer conduit branch. Conduit routing can travel vertically and up to 32 blocks from the anchor.

## Resonance and access

- One sky-facing collector produces 5 resonance per second by day and 24 at night. Rain and thunderstorms add bonuses.
- Opening at the anchor costs 5 resonance. Opening with a key costs 20.
- Any inventory action that moves matter into or out of the archive costs 1 resonance.
- Applying an Astral Cell costs 250 resonance.
- Base energy capacity is 1,200. Each extra archive page adds 600, reaching 3,000 at 216 slots.
- Breaking the frame, blocking every collector's sky, or unloading every valid ritual takes the network offline. Open screens close and remote keys stop working.

## Linking and persistence

- Use a key on a working anchor to copy that archive's constellation into the key.
- Sneak-use an attuned key on a working anchor to rebind the anchor to the key's archive.
- Use an attuned key in the air to open the archive remotely; sneak-use it to erase the attunement.
- Multiple completed anchors may point at the same archive. Any of them can keep it powered.
- Items and power are saved in Overworld persistent state, not in blocks. Breaking an anchor never drops or deletes archived contents.

## Visual language

Each network role has a distinct, state-driven silhouette:

- the anchor opens as a filled seven-petal astral iris with orbiting capacity crystals;
- conduits carry broad luminous ribbons and a traveling solid-light core;
- pylons levitate faceted prisms and orbiting resonance shards;
- collectors rotate a petaled sky dish and pull down falling star droplets;
- the archive interface uses clean cut-corner fields and a live resonance bar instead of a dense constellation overlay.

See [docs/RENDERING.md](docs/RENDERING.md) and [docs/STORAGE.md](docs/STORAGE.md) for implementation details.

## Development

The Loom build runs on Java 21 and emits Java 17-compatible bytecode:

```bash
./gradlew build
```

The distributable is generated at `build/libs/axiomata-0.3.0.jar`.

## License

CC0-1.0. See [LICENSE](LICENSE).
