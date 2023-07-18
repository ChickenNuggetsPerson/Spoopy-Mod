package com.hayden.spoop;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;



public class FallBlockCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("fallingBlocks").then(
            CommandManager.argument("radius", IntegerArgumentType.integer(0, 50)).executes(context -> 
            execute(context.getSource(), IntegerArgumentType.getInteger(context, "radius"))
        )));
    }

    private static int execute(ServerCommandSource source, int radius) {
        // Get the world and the ChunkRegenerator
        ServerWorld world = source.getWorld();
        BlockPos position = source.getPlayer().getBlockPos();

        List<BlockPos> blocks = TNTExplodeListen.getBlocksInSphere(position, radius);
        blocks.forEach((block) -> {            
            BlockState blockState = world.getBlockState(block);
            if (!blockState.isAir() && (blockState.getHardness(world, position) >= 0)) {
                FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, position, blockState);
                fallingBlockEntity.updatePosition(block.getX(), block.getY(), block.getZ());
                world.spawnEntity(fallingBlockEntity);
                world.removeBlock(block, false);
            }

        });
        return 1;
    }
}
