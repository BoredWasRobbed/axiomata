# Axiomata

Axiomata is a renderer-first ritual magic mod for Fabric 1.20.1. Instead of painted blocks or authored models, its ritual apparatus is drawn at runtime from animated lines: rotating geometric proofs, orbiting runes, energy paths, wireframe plinths, and a double helix that grows as an axiom resolves.

The mod ships no custom in-world textures. Block-entity renderers create every placed structure procedurally; inventory icons are small JSON aliases to vanilla item models. The existing PNG is used only as the mod-list icon.

## Requirements

- Minecraft 1.20.1
- Fabric Loader 0.15.11 or newer
- Fabric API 0.92.9+1.20.1
- Java 17 to play

## Building a ritual

Place the blocks on one level in this exact footprint. North is at the top:

```text
              P north

         R NW         R NE

P west          N           P east

         R SW         R SE

              P south
```

`N` is the Ritual Nexus, each `P` is an Offering Plinth three blocks from it, and each `R` is a Rune Mark one block diagonally from it.

1. Put one offering on each plinth by right-clicking it. Use an empty hand to take the offering back.
2. Right-click a rune with the Resonance Tuner to advance its value from 0 through 7. Sneak-right-click to go backward.
3. Right-click the nexus with an empty hand, or sneak-right-click with the tuner, to inspect the sequence and offerings.
4. Right-click the nexus with the tuner to resolve the matching axiom.

Rune order is `NW → NE → SE → SW`. Plinth order is `north → east → south → west`; offerings themselves match in any order.

## Known axioms

| Axiom | Runes | Four offerings | Result |
| --- | --- | --- | --- |
| Material Concordance | `0, 2, 4, 6` | iron ingot, copper ingot, redstone, quartz | Transmutes the set into four gold ingots. |
| Verdant Recursion | `1, 3, 5, 7` | wheat seeds, bone meal, glow berries, emerald | Grows fertilizable blocks in a 15×6×15 region. |
| Aegis Equation | `7, 0, 7, 0` | shield, iron ingot, amethyst shard, ghast tear | Grants nearby players Resistance II, Absorption III, and Fire Resistance for 150 seconds. |
| Tempest Postulate | `3, 6, 1, 4` | copper ingot, feather, redstone, prismarine shard | Begins a thunderstorm and calls lightning onto up to six nearby hostiles. |
| Astral Translation | `6, 6, 2, 2` | ender pearl, chorus fruit, compass, amethyst shard | Moves the caster 48 blocks forward to the surface. |

Every activation is server-authoritative. The complete structure and all four offerings are checked before anything is consumed; active rituals persist across saves and enter a four-second stabilization cooldown when they finish.

## Crafting

All four mod items have ordinary crafting recipes and appear in the Functional Blocks creative tab. Recipe viewers can discover them normally.

## Development

The current Loom build runtime requires Java 21, while compilation deliberately targets Java 17 for Minecraft 1.20.1 compatibility:

```bash
./gradlew build
```

The distributable is generated at `build/libs/axiomata-0.1.0.jar`. Renderer architecture and extension notes are in [docs/RENDERING.md](docs/RENDERING.md).

## License

CC0-1.0. See [LICENSE](LICENSE).
