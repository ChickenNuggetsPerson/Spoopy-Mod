package com.hayden.spoop;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;


public final class TNTCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("tnt").then(
            CommandManager.argument("ammount", IntegerArgumentType.integer(1)).then(
            CommandManager.argument("velocity", IntegerArgumentType.integer(0)).then(
            CommandManager.argument("spread", IntegerArgumentType.integer(0)).executes(context -> 
            execute(context.getSource(), IntegerArgumentType.getInteger(context, "ammount"), IntegerArgumentType.getInteger(context, "spread"), IntegerArgumentType.getInteger(context, "velocity"))
        )))));
    }

    private static int execute(ServerCommandSource source, int ammount, int spread, int velocity) {
    

        int scale = spread;

        for (int i = 0; i < ammount; i++) {
            
            LivingEntity player = source.getPlayer();
            Vec3d playerPos = player.getPos();

            // Get the player's rotation angles
            float yaw = player.getYaw() + (float)((0.5 - Math.random())*scale);
            float pitch = player.getPitch() + (float)((0.5 - Math.random())*scale);

            // Calculate the direction vector from the rotation angles
            double x = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
            double y = -Math.sin(Math.toRadians(pitch));
            double z = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));

            // Normalize the direction vector
            Vec3d direction = new Vec3d(x, y, z).normalize().multiply(1 + velocity);

            direction.rotateX((float)((0.5 - Math.random())*scale));
            direction.rotateY((float)((0.5 - Math.random())*scale));
            direction.rotateZ((float)((0.5 - Math.random())*scale));


            TntEntity tntEntity = EntityType.TNT.create(source.getWorld());
            tntEntity.setFuse(80);
            tntEntity.setVelocity(direction);
            tntEntity.updatePosition(playerPos.x, playerPos.y + 1, playerPos.z);
            tntEntity.setGlowing(true);
            tntEntity.setFrozenTicks(40);
            source.getWorld().spawnEntity(tntEntity);
            
        }
        


        return 1;
    }
}