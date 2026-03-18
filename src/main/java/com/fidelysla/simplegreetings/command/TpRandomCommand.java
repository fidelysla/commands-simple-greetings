package com.fidelysla.simplegreetings.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Random;

public class TpRandomCommand extends CommandBase {

    private static final int RADIUS = 50;
    private final Random random = new Random();

    public TpRandomCommand() {
        super("tprandom", "Teleports you to a random location within 50 blocks.");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {

        Ref<EntityStore> playerRef = ctx.senderAsPlayerRef();

        if (playerRef == null) {
            ctx.sendMessage(Message.raw("§cEste comando solo puede ser usado por jugadores."));
            return;
        }

        // Obtener el World ANTES de entrar al WorldThread (esto es thread-safe)
        Store<EntityStore> store = playerRef.getStore();
        World world = store.getExternalData().getWorld();

        // Calcular el destino aleatorio aquí, fuera del WorldThread
        double angle    = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * RADIUS;

        // Despachar al WorldThread para acceder al ECS de forma segura
        world.execute(() -> {

            TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());

            if (transform == null) return;

            Vector3d origin = transform.getPosition();
            Vector3f rotation = transform.getRotation();

            double targetX = origin.x + (distance * Math.cos(angle));
            double targetZ = origin.z + (distance * Math.sin(angle));
            double targetY = origin.y;

            Vector3d targetPos = new Vector3d(targetX, targetY, targetZ);

            Teleport teleport = Teleport.createForPlayer(targetPos, rotation);
            store.putComponent(playerRef, Teleport.getComponentType(), teleport);
        });

        ctx.sendMessage(Message.raw("¡Teletransportado a una ubicación aleatoria!"));
        ctx.sendMessage(Message.raw("Radio de " + RADIUS + " bloques desde tu posición original."));
    }
}