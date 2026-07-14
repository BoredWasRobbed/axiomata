# Astral network architecture

## Ritual validation

`AstralNetworkScanner` validates four pylons at cardinal radius three and two conduits on each arm. It then performs a bounded breadth-first search over up to 160 connected conduits within 32 blocks of the anchor. Adjacent Starlight Collectors are deduplicated and only generate when the block above has sky visibility.

Every valid anchor sends a heartbeat into its archive's world-level record. Multiple anchors may share one UUID; any loaded valid ritual can therefore keep the shared archive online and add resonance. If no heartbeat arrives for 60 ticks, remote access and open archive handlers stop working.

## World-level persistence

`AstralStorageState` belongs to the server's Overworld and maps network UUIDs to a fixed 216-stack backing list, unlocked page count, stored resonance, and last-online timestamp. NBT writes only occupied stacks plus the small network metadata.

An anchor stores its UUID link and cached rendering metrics, never the item inventory. Removing a block cannot drop, duplicate, or erase archived contents. Unreachable archives are intentionally retained so a surviving attuned key can recover them.

## Power accounting

Collectors add resonance once per second. The persistent state caps energy according to the unlocked page count and performs all access debits server-side. Anchor and key opening, archive slot actions, and cell upgrades fail atomically when the network is offline or lacks energy.

The screen handler synchronizes energy, capacity, and online state through vanilla integer properties. It checks the heartbeat in `canUse`, so an invalidated ritual closes both local and portable screens rather than allowing stale access.

## Paged inventory

The archive begins with 54 slots and can grow to four pages or 216 slots. `PagedInventory` translates the 54 visible storage indices into the selected region of the persistent backing list. Page buttons use vanilla handler button packets, and insertion/extraction uses ordinary slot synchronization.
