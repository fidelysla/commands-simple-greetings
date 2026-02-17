package com.fidelysla.simplegreetings.command;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;

import javax.annotation.Nonnull;

public class SaludoCommand extends CommandBase {

    private final String pluginName;
    private final String pluginVersion;

    public SaludoCommand(String pluginName, String pluginVersion) {

        super("saludo", "Sends a friendly greeting.");
        this.setPermissionGroup(GameMode.Adventure);

        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {

        Player player = ctx.senderAs(Player.class);
        String playerName = player.getDisplayName();

        ctx.sendMessage(Message.raw("§aHola " + playerName + "!"));
        ctx.sendMessage(Message.raw("§7Bienvenido al servidor."));
        ctx.sendMessage(Message.raw("§bPlugin: " + pluginName + " v" + pluginVersion));
    }
}
