package com.fidelysla.simplegreetings;

import com.fidelysla.simplegreetings.command.TpRandomCommand;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

import com.fidelysla.simplegreetings.listener.PlayerJoinListener;
import com.fidelysla.simplegreetings.command.SaludoCommand;

import javax.annotation.Nonnull;

public class SimpleGreetings extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public SimpleGreetings(@Nonnull JavaPluginInit init) {
        super(init);

        LOGGER.atInfo().log(
                "Loaded " + this.getName() +
                        " v" + this.getManifest().getVersion().toString()
        );
    }

    @Override
    protected void setup() {

        LOGGER.atInfo().log("Setting up SimpleGreetings...");

        // Registrar comando
        this.getCommandRegistry().registerCommand(
                new SaludoCommand(
                        this.getName(),
                        this.getManifest().getVersion().toString()
                )
        );

        this.getCommandRegistry().registerCommand(new TpRandomCommand());

        // Crear instancia del listener
        PlayerJoinListener listener = new PlayerJoinListener();

        // Suscribirse al evento correctamente
        //this.getEventRegistry().subscribe(PlayerReadyEvent.class, listener::onPlayerReady);
        this.getEventRegistry().registerGlobal(
                PlayerReadyEvent.class,
                listener::onPlayerReady
        );

        LOGGER.atInfo().log("SimpleGreetings setup complete!");
    }
}
