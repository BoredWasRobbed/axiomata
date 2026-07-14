# Astral storage architecture

## World-level persistence

`AstralStorageState` is a `PersistentState` owned by the server's Overworld. It maps random network UUIDs to a fixed 216-stack backing list plus an unlocked page count. NBT stores only occupied slots, so empty capacity has little disk cost.

An `AstralAnchorBlockEntity` stores a UUID link, cached occupancy metrics, and its last access time. It never owns the item inventory. Destroying an anchor therefore cannot drop, duplicate, or erase the archive contents.

## Capacity

Every archive begins with one 54-slot page. Applying an Astral Cell permanently increments the page count, up to four pages and 216 slots. Capacity is monotonic: it cannot shrink beneath stored items.

## Paged access

`AstralStorageScreenHandler` exposes 54 archive slots, 27 player inventory slots, and nine hotbar slots. A `PagedInventory` translates the visible archive indices into the current region of the 216-slot backing inventory.

Page buttons use vanilla screen-handler button packets. The client changes its view immediately; the server validates the requested page, moves its own view, and sends ordinary slot updates. Shift-click insertion and extraction therefore work without a custom item-transfer protocol.

## Linking

Astral Keys store the same UUID in item NBT. Normal use on an anchor copies the anchor link to the key. Sneak-use copies an attuned key's link to the anchor. The portable screen factory resolves that UUID against the current world's `AstralStorageState`, so a key works in any dimension while remaining world-local.

The system intentionally retains unreachable archives. A lost anchor can be recovered with any surviving attuned key, and server operators never face automatic deletion of unattended player storage.
