package com.fidelysla.simplegreetings.listener;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

public class PlayerJoinListener {

    public void onPlayerReady(PlayerReadyEvent event) {

        Player player = event.getPlayer();

        player.sendMessage(
                Message.raw("Bienvenido " + player.getDisplayName() + "!")
        );

        player.sendMessage(
                Message.raw("Escribe /saludo para recibir un mensaje especial.")
        );
    }
}
