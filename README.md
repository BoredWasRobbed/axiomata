# Axiomata

Axiomata is a renderer-first storage mod for Fabric 1.20.1. It treats storage as a place rather than a container: every archive is a persistent constellation in the astral plane, and physical anchors are only doors into it.

There are no custom in-world textures or authored block models. The anchor, its aperture, and the archive interface are assembled at runtime from procedural geometry and UI primitives. Inventory icons reuse vanilla item models; the PNG is only the mod-list icon.

## Requirements

- Minecraft 1.20.1
- Fabric Loader 0.15.11 or newer
- Fabric API 0.92.9+1.20.1
- Java 17 to play

## The storage loop

1. Craft and place an **Astral Anchor**. It creates a new archive with 54 slots.
2. Right-click the anchor to open its textureless, paged interface. Shift-clicking works in both directions.
3. Use an **Astral Cell** on the anchor to permanently unfold one more 54-slot page. Each network supports four pages, or 216 slots total.
4. Use an **Astral Key** on the anchor to attune it. Right-click the key anywhere—even in another dimension—to open the same archive.
5. Break, move, or replace the anchor freely. Its contents remain in world-level astral storage rather than dropping or disappearing.

## Linking anchors and keys

- Use a key normally on an anchor to copy that anchor's constellation into the key.
- Sneak-use an attuned key on an anchor to rebind the anchor to the key's archive.
- Right-click with an attuned key in the air to open its archive remotely.
- Sneak-right-click with the key in the air to erase its attunement.

Multiple anchors and keys can point at the same archive. Each archive has a short constellation code in the interface and key tooltip, making it possible to verify links without exposing the full UUID.

## Visual language

The anchor communicates real state instead of playing a fixed animation:

- archive identity determines its color and deterministic constellation;
- the bright base arc shows occupied-slot ratio;
- one orbiting crystal appears for every unlocked page;
- access wakes six transfer filaments and moving matter motes;
- the aperture breathes, counter-rotates, and gains intensity while active;
- a textureless inventory screen uses the same network color, constellation, and page indicators.

See [docs/RENDERING.md](docs/RENDERING.md) for the renderer breakdown and [docs/STORAGE.md](docs/STORAGE.md) for persistence and linking internals.

## Persistence guarantees

- Storage is saved once per world in `axiomata_astral_storage` persistent state.
- The Overworld owns the data, so access remains consistent across dimensions.
- Block entities save only their network link and display metrics—not item contents.
- Removing the last known anchor does not garbage-collect an archive. An attuned key can always recover it.
- Capacity only increases. Cells cannot strand items by shrinking a vault.

## Development

The current Loom plugin uses Java 21 as its build runtime while producing Java 17-compatible mod bytecode:

```bash
./gradlew build
```

The distributable is generated at `build/libs/axiomata-0.2.0.jar`.

## License

CC0-1.0. See [LICENSE](LICENSE).
