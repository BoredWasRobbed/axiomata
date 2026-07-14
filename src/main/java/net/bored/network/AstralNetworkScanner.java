package net.bored.network;

import net.bored.content.ModBlocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AstralNetworkScanner {
    public static final int REQUIRED_PYLONS = 4;
    public static final int REQUIRED_FRAME_CONDUITS = 8;
    private static final int MAX_CONDUITS = 160;
    private static final int MAX_DISTANCE_SQUARED = 32 * 32;
    private static final Direction[] HORIZONTAL = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    private AstralNetworkScanner() {
    }

    public static ScanResult scan(ServerWorld world, BlockPos anchorPos) {
        int pylons = 0;
        int frameConduits = 0;
        Set<BlockPos> ritualNodes = new HashSet<>();
        for (Direction direction : HORIZONTAL) {
            BlockPos pylonPos = anchorPos.offset(direction, 3);
            if (world.getBlockState(pylonPos).isOf(ModBlocks.RESONANCE_PYLON)) {
                pylons++;
                ritualNodes.add(pylonPos.toImmutable());
            }
            for (int distance = 1; distance <= 2; distance++) {
                BlockPos conduitPos = anchorPos.offset(direction, distance);
                if (world.getBlockState(conduitPos).isOf(ModBlocks.LEY_CONDUIT)) {
                    frameConduits++;
                }
            }
        }

        Set<BlockPos> conduits = traceConduits(world, anchorPos);
        ritualNodes.addAll(conduits);
        Set<BlockPos> collectors = new HashSet<>();
        for (BlockPos conduit : conduits) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = conduit.offset(direction);
                if (world.getBlockState(neighbor).isOf(ModBlocks.STARLIGHT_COLLECTOR)) {
                    collectors.add(neighbor.toImmutable());
                }
            }
        }
        ritualNodes.addAll(collectors);

        List<BlockPos> poweredCollectors = new ArrayList<>();
        for (BlockPos collector : collectors) {
            if (world.isSkyVisible(collector.up())) {
                poweredCollectors.add(collector);
            }
        }
        boolean frameComplete = pylons == REQUIRED_PYLONS && frameConduits == REQUIRED_FRAME_CONDUITS;
        int generation = frameComplete ? generation(world, poweredCollectors.size()) : 0;
        return new ScanResult(frameComplete, pylons, frameConduits, collectors.size(),
                poweredCollectors.size(), generation, List.copyOf(ritualNodes));
    }

    private static Set<BlockPos> traceConduits(ServerWorld world, BlockPos anchorPos) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        for (Direction direction : HORIZONTAL) {
            BlockPos adjacent = anchorPos.offset(direction);
            if (world.getBlockState(adjacent).isOf(ModBlocks.LEY_CONDUIT)) {
                queue.add(adjacent.toImmutable());
            }
        }
        while (!queue.isEmpty() && visited.size() < MAX_CONDUITS) {
            BlockPos current = queue.removeFirst();
            if (visited.contains(current) || current.getSquaredDistance(anchorPos) > MAX_DISTANCE_SQUARED) {
                continue;
            }
            visited.add(current);
            for (Direction direction : Direction.values()) {
                BlockPos next = current.offset(direction);
                if (!visited.contains(next) && world.getBlockState(next).isOf(ModBlocks.LEY_CONDUIT)) {
                    queue.addLast(next.toImmutable());
                }
            }
        }
        return visited;
    }

    private static int generation(ServerWorld world, int collectors) {
        if (collectors == 0) {
            return 0;
        }
        long dayTime = Math.floorMod(world.getTimeOfDay(), 24000L);
        boolean night = dayTime >= 12500L && dayTime <= 23500L;
        int perCollector = night ? 24 : 5;
        if (world.isRaining()) {
            perCollector += 4;
        }
        if (world.isThundering()) {
            perCollector += 8;
        }
        return collectors * perCollector;
    }

    public record ScanResult(boolean frameComplete, int pylons, int frameConduits, int collectors,
                             int poweredCollectors, int generation, List<BlockPos> ritualNodes) {
        public boolean operational() {
            return frameComplete && poweredCollectors > 0;
        }
    }
}
