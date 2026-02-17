# ESTRUCTURA RESUMIDA - hytaleServer

```txt
com/hypixel/hytale/
│
├── assetstore/              # Almacén de assets (codec, event, iterator, map)
│
├── builtin/                 # Módulos del juego
│   ├── adventure/           # Quests, NPCs, reputación, tiendas, teletransporte
│   │   ├── camera/          # Efectos de cámara
│   │   ├── farming/         # Cultivos y estados de crecimiento
│   │   ├── memories/        # Memoria de NPCs
│   │   ├── npcobjectives/   # Objetivos de NPCs
│   │   ├── npcreputation/   # Reputación con NPCs
│   │   ├── npcshop/         # Tiendas de NPCs
│   │   ├── objectives/      # Sistema de objetivos/misiones
│   │   ├── reputation/      # Sistema de reputación general
│   │   ├── shop/            # Tiendas (barter)
│   │   ├── stash/           # Almacenamiento
│   │   └── teleporter/      # Teletransportadores
│   │
│   ├── ambience/            # Ambiente (commands, components, resources, systems)
│   ├── asseteditor/         # Editor de assets
│   ├── beds/                # Camas y sistema de respawn/sleep
│   ├── blockphysics/        # Física de bloques
│   ├── blockspawner/        # Generador de bloques
│   ├── blocktick/           # Ticks de bloques
│   ├── buildertools/        # Herramientas de construcción
│   │   ├── prefabeditor/    # Editor de prefabs
│   │   └── scriptedbrushes/ # Pinceles con scripts
│   ├── commandmacro/        # Macros de comandos
│   ├── crafting/            # Sistema de crafteo
│   ├── creativehub/         # Hub creativo
│   ├── crouchslide/         # Mecánica de deslizamiento
│   ├── deployables/         # Objetos desplegables
│   ├── fluid/               # Fluidos
│   │
│   ├── hytalegenerator/     # Generador de mundo principal ⭐
│   │   ├── assets/          # Assets de generación (biomes, blocksets, curves, density...)
│   │   ├── biome/           # Lógica de biomas
│   │   ├── chunkgenerator/  # Generación de chunks
│   │   ├── datastructures/  # Estructuras de datos (compression, voxelspace)
│   │   ├── density/         # Generación por densidad
│   │   ├── fields/          # Campos de ruido/puntos
│   │   ├── framework/       # Framework interno
│   │   ├── material/        # Materiales
│   │   ├── props/           # Props del mundo
│   │   └── worldstructure/  # Estructuras del mundo
│   │
│   ├── instances/           # Instancias de mundo
│   ├── landiscovery/        # Descubrimiento de tierra
│   ├── mantling/            # Mecánica de trepar
│   ├── model/               # Sistema de modelos
│   ├── mounts/              # Monturas
│   ├── npc/                 # Sistema de NPCs ⭐
│   │   ├── asset/           # Assets de NPC
│   │   ├── blackboard/      # Pizarra de decisiones (combat, attitude, interaction...)
│   │   ├── corecomponents/  # Componentes core (combat, movement, statemachine...)
│   │   ├── decisionmaker/   # Motor de decisiones
│   │   ├── movement/        # Movimiento y steering
│   │   └── navigation/      # Navegación
│   │
│   ├── npccombatactionevaluator/  # Evaluador de acciones de combate
│   ├── npceditor/           # Editor de NPCs
│   ├── parkour/             # Mecánica de parkour
│   ├── path/                # Sistema de rutas/waypoints
│   ├── portals/             # Portales y void events
│   ├── randomtick/          # Ticks aleatorios
│   ├── safetyroll/          # Seguridad de colisión
│   ├── sprintforce/         # Fuerza de sprint
│   ├── tagset/              # Conjuntos de tags
│   ├── teleport/            # Teletransporte/warps
│   ├── weather/             # Sistema de clima
│   │
│   ├── spawning/            # Sistema de spawning ⭐
│   │   ├── assets/          # Assets (spawnmarkers, spawns, spawnsuppression)
│   │   ├── controllers/     # Controladores de spawn
│   │   ├── managers/        # Gestores
│   │   ├── suppression/     # Supresión de spawns
│   │   └── world/           # Spawn por mundo
│   │
│   ├── flock/               # Comportamiento en grupo (IA flocking)
│   ├── migrations/          # Migraciones de datos
│   └── worldgen/            # Generación de mundo
│       ├── biome/           # Biomas
│       ├── cave/            # Cuevas
│       ├── climate/         # Clima
│       ├── loader/          # Cargador (biome, cave, climate, prefab, zone)
│       ├── prefab/          # Prefabs
│       └── zone/            # Zonas
│
├── codec/                   # Codecs y serialización
│   ├── codecs/              # (array, map, set, simple)
│   ├── schema/              # Esquemas de datos
│   └── validation/          # Validación
│
├── common/                  # Utilidades comunes (collections, thread, tuple, util)
│
├── component/               # Sistema de componentes ECS
│   ├── data/                # Datos de componentes
│   ├── event/               # Eventos
│   ├── query/               # Consultas
│   └── system/              # Sistemas ECS
│
├── event/                   # Sistema de eventos global
├── function/                # Funciones utilitarias (consumer, predicate, supplier)
├── logger/                  # Logging (backend, sentry)
│
├── math/                    # Utilidades matemáticas
│   ├── hitdetection/        # Detección de impactos
│   ├── random/              # Aleatoriedad
│   ├── raycast/             # Raycasting
│   ├── shape/               # Formas geométricas
│   └── vector/              # Vectores
│
├── metrics/                 # Métricas de rendimiento
├── plugin/                  # Sistema de plugins
├── procedurallib/           # Librería procedural (condition, logic, random)
│
├── protocol/                # Protocolo de red
│   ├── io/netty/            # Netty networking
│   └── packets/             # Paquetes (auth, entities, inventory, player, world...)
│
├── registry/                # Registro global
│
├── server/core/             # Núcleo del servidor ⭐
│   ├── asset/               # Sistema de assets del servidor
│   │   └── type/            # Tipos de assets (audio, blocks, items, particles, weather...)
│   ├── auth/                # Autenticación
│   ├── blocktype/           # Tipos de bloques
│   ├── command/             # Sistema de comandos
│   │   └── commands/        # (debug, player, server, world, utility)
│   ├── config/              # Configuración
│   ├── entity/              # Entidades
│   │   └── entities/player/ # Entidad jugador (data, hud, movement, pages)
│   ├── event/               # Eventos del servidor
│   ├── inventory/           # Inventario
│   ├── io/                  # I/O de red (handlers, netty, transport)
│   ├── modules/             # Módulos del servidor
│   │   ├── accesscontrol/   # Control de acceso y bans
│   │   ├── block/           # Sistema de bloques
│   │   ├── camera/          # Cámara
│   │   ├── collision/       # Colisiones
│   │   ├── entity/          # (damage, hitbox, player, stamina, teleport...)
│   │   ├── entitystats/     # Estadísticas de entidades
│   │   ├── i18n/            # Internacionalización
│   │   ├── interaction/     # Interacciones
│   │   ├── item/            # Items
│   │   ├── physics/         # Física
│   │   ├── projectile/      # Proyectiles
│   │   └── time/            # Tiempo del juego
│   ├── permissions/         # Permisos
│   ├── plugin/              # Plugins del servidor
│   ├── prefab/              # Prefabs
│   ├── receiver/            # Receptor de paquetes
│   ├── task/                # Tareas del servidor
│   └── universe/            # Universo/mundo
│       └── world/           # Lógica de mundo
│           ├── chunk/       # Chunks (palette, section, state)
│           ├── commands/    # Comandos de mundo
│           ├── lighting/    # Iluminación
│           ├── npc/         # NPCs en mundo
│           ├── spawn/       # Spawns
│           ├── storage/     # Almacenamiento
│           └── worldmap/    # Mapa del mundo
│
├── sneakythrow/             # Utilidad de excepciones (consumer, function, supplier)
├── storage/                 # Almacenamiento general
└── unsafe/                  # Operaciones inseguras (low-level)

====================================
RESUMEN: 12 módulos raíz principales
- builtin/  → ~35 submódulos de gameplay
- server/   → núcleo del servidor con modules/ y universe/
- codec/    → serialización y validación
- component/→ arquitectura ECS
- math/     → utilidades matemáticas
- protocol/ → red y paquetes
- common, event, function, logger, metrics, registry, plugin, procedurallib, storage, unsafe
====================================
```