package com.hayden.spoop;

import java.util.ArrayList;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NbtCompound;

public class TNTExplodeListen {
     public static void register() {
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof TntEntity) {
                TntEntity tntEntity = (TntEntity) entity;
                if (tntEntity.getFuse() == 0) {
                    explode(tntEntity, world);
                }
            }
        });
    }

    private static void explode(TntEntity tntEntity, ServerWorld world) {
        // Your custom logic to run when a TNT explodes
        BlockPos position = tntEntity.getBlockPos();
    
        List<BlockPos> blocks = getBlocksInSphere(position, 5);
        blocks.forEach((block) -> {
            //world.breakBlock(block, true);
            
            BlockState blockState = world.getBlockState(block);
            if (!blockState.isAir() && (blockState.getHardness(world, position) >= 0)) {
                // Create the FallingBlockEntity using the static create method
            
                FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, position, blockState);
                fallingBlockEntity.updatePosition(position.getX() + (1-Math.random()), position.getY() + (1-Math.random()), position.getZ() + (1-Math.random()));

                world.spawnEntity(fallingBlockEntity);
                
                // Remove the original block
                world.removeBlock(block, false);
            }

        });
    }


    public static List<BlockPos> getBlocksInSphere(BlockPos centerPos, int radius) {
        List<BlockPos> blockList = new ArrayList<>();

        // Loop through all positions within the bounding box of the sphere
        for (int x = centerPos.getX() - radius; x <= centerPos.getX() + radius; x++) {
            for (int y = centerPos.getY() - radius; y <= centerPos.getY() + radius; y++) {
                for (int z = centerPos.getZ() - radius; z <= centerPos.getZ() + radius; z++) {
                    BlockPos currentPos = new BlockPos(x, y, z);

                    // Check if the current position is within the sphere using the distance formula
                    if (isWithinSphere(centerPos, currentPos, radius)) {
                        blockList.add(currentPos);
                    }
                }
            }
        }

        return blockList;
    }

    private static boolean isWithinSphere(BlockPos centerPos, BlockPos currentPos, int radius) {
        double distanceSq = centerPos.getSquaredDistance(currentPos);

        // The distance squared should be less than or equal to the radius squared
        return distanceSq <= radius * radius;
    }
}
