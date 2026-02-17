# Documentación: Comando `/tprandom`

## Resumen

Comando que teletransporta al jugador a una ubicación aleatoria dentro de un radio de 50 bloques desde su posición actual. Desarrollado para Hytale Server utilizando la arquitectura ECS (Entity Component System) del motor.

---

## Clases y Paquetes Investigados

### `com.hypixel.hytale.server.core.entity.entities.Player`

Clase que representa al jugador en el servidor. No expone métodos directos de posición ni teleport — toda la información está en los componentes ECS adjuntos a la entidad. Los métodos relevantes encontrados fueron:

- `getDisplayName()` — nombre visible del jugador
- `sendMessage(Message)` — enviar mensaje al jugador

### `com.hypixel.hytale.server.core.command.system.CommandContext`

Contexto del comando ejecutado. Método clave para el comando:

- `senderAsPlayerRef()` → devuelve `Ref<EntityStore>`, el identificador ECS del jugador que ejecutó el comando. Devuelve `null` si el ejecutor no es un jugador.

### `com.hypixel.hytale.component.Ref<ECS_TYPE>`

Referencia ligera a una entidad dentro del ECS. Solo contiene el `Store` y el índice de la entidad. **No tiene métodos de acceso a componentes directamente.** Para leer/escribir componentes hay que pasar por el `Store`.

- `getStore()` → devuelve el `Store<ECS_TYPE>` asociado

### `com.hypixel.hytale.component.Store<ECS_TYPE>`

Implementa `ComponentAccessor<ECS_TYPE>`. Es el punto de entrada principal al ECS. Métodos usados:

- `getComponent(ref, ComponentType)` → lee un componente de la entidad
- `putComponent(ref, ComponentType, component)` → escribe/reemplaza un componente en la entidad
- `getExternalData()` → devuelve el `EntityStore` (datos externos del store)

> **Importante:** `Store` verifica internamente mediante `assertThread()` que las operaciones se ejecuten en el `WorldThread`. Intentar acceder desde otro hilo lanza `IllegalStateException`.

### `com.hypixel.hytale.component.ComponentAccessor<ECS_TYPE>` (interfaz)

Interfaz que define las operaciones ECS disponibles. `Store` la implementa. Métodos relevantes:

- `getComponent(ref, componentType)`
- `putComponent(ref, componentType, t)`
- `addComponent(ref, componentType, t)`
- `removeComponent(ref, componentType)`

### `com.hypixel.hytale.server.core.modules.entity.component.TransformComponent`

Componente ECS que almacena la posición y rotación de una entidad. Métodos usados:

- `getComponentType()` → referencia estática al tipo de componente en el ECS
- `getPosition()` → devuelve `Vector3d` con la posición actual
- `getRotation()` → devuelve `Vector3f` con la rotación actual

### `com.hypixel.hytale.server.core.modules.entity.teleport.Teleport`

Componente ECS que, al ser agregado a una entidad, dispara el sistema de teleport en el siguiente tick. No se llama como método — se instancia y se agrega al store. Factory methods disponibles:

- `Teleport.createForPlayer(Vector3d position, Vector3f rotation)` — crea un teleport para jugador sin world change
- `Teleport.createForPlayer(World world, Vector3d position, Vector3f rotation)` — con cambio de mundo
- `Teleport.createExact(...)` — teleport exacto con rotación de cabeza separada

### `com.hypixel.hytale.math.vector.Vector3d`

Vector de doble precisión para posiciones 3D. Campos públicos `x`, `y`, `z`. Constructor usado: `new Vector3d(double x, double y, double z)`.

### `com.hypixel.hytale.server.core.universe.world.storage.EntityStore`

Datos externos del `Store<EntityStore>`. Contiene la referencia al mundo. Método usado:

- `getWorld()` → devuelve el `World` al que pertenece el store

### `com.hypixel.hytale.server.core.universe.world.World`

Clase del mundo. Extiende `TickingThread` e implementa `Executor`. Método clave:

- `execute(Runnable)` → encola una tarea en el **WorldThread**, que es el único hilo donde el ECS puede ser accedido de forma segura.

---

## Problema: Thread Safety del ECS

### Error encontrado

```
java.lang.IllegalStateException: Assert not in thread!
Thread[#80,WorldThread - default,...] but was in
Thread[#70,ForkJoinPool.commonPool-worker-9 -- Running: tprandom,...]
    at com.hypixel.hytale.component.Store.assertThread(Store.java:2306)
    at com.hypixel.hytale.component.Store.getComponent(Store.java:1215)
    at com.fidelysla.simplegreetings.command.TpRandomCommand.executeSync(...)
```

### Causa

`CommandBase.executeSync()` se ejecuta en un hilo del `ForkJoinPool`, no en el `WorldThread`. El `Store` del ECS tiene una verificación estricta (`assertThread()`) que impide el acceso desde cualquier hilo que no sea su `WorldThread` dedicado.

### Solución

Obtener la referencia al `World` antes de entrar al hilo correcto, luego envolver todo acceso al ECS dentro de `world.execute(() -> { ... })`:

```java
Store<EntityStore> store = playerRef.getStore();
World world = store.getExternalData().getWorld();

world.execute(() -> {
    // Acceso seguro al ECS aquí
    TransformComponent transform = store.getComponent(playerRef, TransformComponent.getComponentType());
    // ...
    store.putComponent(playerRef, Teleport.getComponentType(), teleport);
});
```

---

## Problema Pendiente: Teletransporte bajo tierra

### Descripción

Cuando el jugador ejecuta `/tprandom` en una zona con terreno elevado respecto al destino, puede aparecer **dentro de la tierra** porque el comando mantiene la coordenada Y original del jugador, que puede estar por encima del suelo en su posición de origen pero por debajo en el destino.

### Causa raíz

```java
double targetY = origin.y; // ← siempre se usa la Y actual del jugador
```

Si el origen está en una montaña (Y=120) y el destino está en un valle (suelo en Y=60), el jugador aparece en Y=120 dentro de la tierra del valle.

### Solución propuesta

Obtener la altura del bloque más alto (superficie) en las coordenadas X/Z del destino antes de teleportar. Hytale expone acceso a chunks via `World`, por lo que la solución requiere:

1. Obtener el chunk del destino con `world.getChunkIfLoaded(index)` o `world.getChunkAsync(index)`
2. Iterar desde Y máximo hacia abajo para encontrar el primer bloque sólido
3. Usar esa Y + 1 como coordenada final del teleport

```java
// Pseudocódigo de la solución
double targetY = world.getHighestSolidY(targetX, targetZ) + 1.0;
```

> Esto requiere investigar la API de `WorldChunk` para confirmar si existe un método directo de surface height o si hay que iterar manualmente los bloques.

---

## Código Final del Comando

### `command/TpRandomCommand.java`

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

        // Obtener el World ANTES de entrar al WorldThread (thread-safe)
        Store<EntityStore> store = playerRef.getStore();
        World world = store.getExternalData().getWorld();

        // Calcular el destino aleatorio aquí, fuera del WorldThread
        double angle    = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * RADIUS;

        // Despachar al WorldThread para acceder al ECS de forma segura
        world.execute(() -> {

            TransformComponent transform = store.getComponent(
                playerRef,
                TransformComponent.getComponentType()
            );

            if (transform == null) return;

            Vector3d origin   = transform.getPosition();
            Vector3f rotation = transform.getRotation();

            double targetX = origin.x + (distance * Math.cos(angle));
            double targetZ = origin.z + (distance * Math.sin(angle));
            double targetY = origin.y; // ⚠️ Ver problema pendiente arriba

            Vector3d targetPos = new Vector3d(targetX, targetY, targetZ);

            Teleport teleport = Teleport.createForPlayer(targetPos, rotation);
            store.putComponent(playerRef, Teleport.getComponentType(), teleport);
        });

        ctx.sendMessage(Message.raw("§a¡Teletransportado a una ubicación aleatoria!"));
        ctx.sendMessage(Message.raw(
            "§7Radio de §e" + RADIUS + " §7bloques desde tu posición original."
        ));
    }
}
```

### Registro en `SimpleGreetings.java`

```java
this.getCommandRegistry().registerCommand(new TpRandomCommand());
```

---

## Flujo de Ejecución Resumido

```
/tprandom
    └─ CommandBase.executeSync() [ForkJoinPool thread]
        ├─ ctx.senderAsPlayerRef()        → Ref<EntityStore>
        ├─ ref.getStore()                 → Store<EntityStore>
        ├─ store.getExternalData()        → EntityStore
        ├─ entityStore.getWorld()         → World
        ├─ Calcular ángulo y distancia    (sin acceso al ECS)
        └─ world.execute(Runnable)        [encola en WorldThread]
            ├─ store.getComponent(ref, TransformComponent) → posición actual
            ├─ Calcular targetX, targetY, targetZ
            ├─ Teleport.createForPlayer(pos, rot)
            └─ store.putComponent(ref, Teleport) → dispara el teleport en el siguiente tick
```
