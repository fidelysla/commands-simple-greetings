# Documentacion de Simple Greetings Plugin

## Archivo `settings.gradle`

```groovy
rootProject.name = 'SimpleGreetings'
```

## Archivo `gladle.properties`

```properties
version=1.0.0

maven_group=com.fidelysla

java_version=25

includes_pack=false

# The release channel your plugin should be built and ran against. This is
# usually release or pre-release. You can verify your settings in the
# official launcher.
patchline=release

# Determines if the development server should also load mods from the user's
# standard mods folder. This lets you test mods by installing them where a
# normal player would, instead of adding them as dependencies or adding them
# to the development server manually.
load_user_mods=false
```

## Archivo `src/main/resources/manifest.json`

```json
{
    "Group": "com.fidelysla",
    "Name": "SimpleGreetings",
    "Version": "1.0.0",
    "Description": "A simple plugin that greets players and provides greeting commands.",
    "Authors": [
        {
            "Name": "Fidel Ysla"
        }
    ],
    "Website": "https://github.com/fidelysla",
    "ServerVersion": "*",
    "Dependencies": {
        
    },
    "OptionalDependencies": {
        
    },
    "DisabledByDefault": false,
    "Main": "com.fidelysla.simplegreetings.SimpleGreetings",
    "IncludesAssetPack": true
}
```

## Estructura del Proyecto

```txt
SimpleGreetings/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── fidelysla/
│       │           └── simplegreetings/
│       │               ├── SimpleGreetings.java
│       │               ├── listener/
│       │               │    └── PlayerJoinListener.java
│       │               └── command/
│       │                    └── SaludoCommand.java
│       │
│       └── resources/
│           └── manifest.json
│
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## Clase Principal `SimpleGreetings.java`

```java
package com.fidelysla.simplegreetings;

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
```

## Evento: Bienvenida al Jugador `listener/PlayerJoinListener.java`

```java
package com.fidelysla.simplegreetings.listener;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

public class PlayerJoinListener {

    public void onPlayerReady(PlayerReadyEvent event) {

        Player player = event.getPlayer();

        player.sendMessage(
                Message.raw("§aBienvenido " + player.getDisplayName() + "!")
        );

        player.sendMessage(
                Message.raw("§7Escribe §e/saludo §7para recibir un mensaje especial.")
        );
    }
}
```

## Comando /saludo `command/SaludoCommand.java`

```java
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
```

## Comando /tprandom `command/TpRandomCommand.java`

```java
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

        ctx.sendMessage(Message.raw("§a¡Teletransportado a una ubicación aleatoria!"));
        ctx.sendMessage(Message.raw("§7Radio de §e" + RADIUS + " §7bloques desde tu posición original."));
    }
}
```