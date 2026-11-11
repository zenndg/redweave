# Roadmap

Everything planned for Redweave, phase by phase. A checked box is merged into `develop`. Each item points to its GitHub issue. The [issues](https://github.com/zenndg/redweave/issues) and the GitHub Projects board are the source of truth, and this page is updated whenever a pull request is merged.

## Versions

Redweave follows [Semantic Versioning](https://semver.org/). While in `0.x`, a breaking change bumps the minor version. Every phase ends with a playable release, tested in an instance with only the mod and in the [Iron Meridian](https://github.com/marcelofabianov/iron-meridian) modpack.

| Version | When | Release issue |
| --- | --- | --- |
| **v0.1.0** | End of Phase 1 · Wireless | #27 |
| v0.2.0 | End of Phase 2 · Logic | #35 |
| v0.3.0 | End of Phase 3 · Chips | #43 |
| v1.0.0 | End of Phase 4 · Expansion | #51 |
| — | Phase 5 · Bridge, after 1.0 | to be planned |

### When v0.1.0 is born

v0.1.0 is the first public version. It is cut only when all of these are true:

- [ ] Every Phase 1 item below is merged, including the new network model (#84 to #90).
- [x] The pending Phase 1 decisions (questions 173 to 179) are answered.
- [ ] Block, item, data component, NBT and config IDs are frozen (#26). From here on, they only change with a migration.
- [ ] The 1,000-node load test shows no visible MSPT impact (#25).
- [ ] The manual test script passes in an instance with only the mod and in Iron Meridian.
- [ ] The release goes through `develop` → `homolog` → `release/0.1.0` → `main`, with the annotated tag `v0.1.0` (#27).

## Phase 1 · Wireless (MVP)

Signal anywhere: networks, nodes, channels and the tools to configure them.

### Foundation

- [x] NeoForge 1.21.1 project with ModDevGradle and Java 21 (#1)
- [x] GitHub Actions CI: build and tests on every push (#2)
- [x] Makefile, git hooks and gates: Spotless, Checkstyle and 85% JaCoCo coverage on the domain (#3)
- [x] ArchUnit test: domain and application without Minecraft imports (#4)
- [x] GameTests on a dedicated server, including one without Jade and The One Probe (#5)
- [x] Translation key parity between `en_us` and `pt_br` (#6)
- [x] Network and channel behavior documented, with the domain README (#7)
- [x] Unloaded chunk behavior decided (#8)
- [x] Visual concept of the first interaction and the identity (#59)

### Domain

- [x] Value objects: `Signal` (0–15), `ChannelColor` (16), `NetworkId`, `NodeId` (#9)
- [x] `Network` aggregate: nodes, channels, owner and permissions (#10)
- [x] Channel rule: with many inputs, the strongest signal wins (#11)
- [x] Simulator: each network evaluated once per tick, only when a channel changed (#12)
- [x] Use cases: create a network, link a node, configure a channel (#13)

### Blocks, items and screens

- [x] Network Controller block and block entity (#14)
- [x] Signal Node: attaches to any face, reads and emits redstone (#15)
- [x] Networks saved with Codecs and SavedData (#16)
- [x] Configurator: pair, copy and paste (#17) — redesigned by #86
- [x] A new node starts on a free channel (#18) — the network choice comes with #87
- [x] Node Screen: channels, 3D block face and live meter (#19)
- [x] Signal Remote name decided (#21)
- [x] Label, to reuse a node configuration (#81) — removed by #85
- [x] Node lens decisions and the manual test script (#78)

### New network model

Decided on 2026-11-11: each Network Controller is one network, a Network Terminal manages all of a player's networks, and the Configurator works in modes.

- [x] Record the decisions and audit the documentation (#84)
- [x] Remove the Label (#85)
- [ ] Configurator modes `Open`, `Copy` and `Wrench`, each with a color (#86)
- [ ] A new node joins no network and its owner picks one; node names; switching networks from the Node Screen (#87)
- [ ] `Offline` and `Unplaced` network states, and the per-player network limit (#88)
- [ ] Controller Screen: node list with name, signal and position, rename and access (#20)
- [ ] Signal change log on the Controller Screen (#90)
- [ ] Network Terminal: list, rename, turn off and on, clear nodes and delete unplaced networks (#89)
- [ ] Configurator mode display and floating labels over nodes (#80)

### Phase 1 decisions answered after the audit

- [x] 173 · ID and values of the Configurator mode component
- [x] 174 · New save keys: node name, `Offline` state and last network used
- [x] 175 · Where the server keeps each player's Network Terminal
- [x] 176 · Rename and clear nodes on an `Unplaced` network
- [x] 177 · How nodes and controllers of an `Offline` network look
- [x] 178 · State of an `Offline` network whose controller is broken and placed again
- [x] 179 · Columns of the node list on the Controller Screen

### Toward v0.1.0

- [ ] Signal Remote: lever or button on a channel (#22)
- [ ] Optional Jade and The One Probe integration (#23)
- [ ] Vanilla recipes and emissive textures (#24)
- [ ] Load test with spark: a 1,000-node network (#25)
- [ ] Freeze block, item, data component and config IDs (#26)
- [ ] Release v0.1.0 and test in Iron Meridian (#27)

## Phase 2 · Logic

Gates you can see.

- [ ] AND, OR, XOR, NOT and NAND gates with tested truth tables (#28)
- [ ] Per-channel logic: invert, pulse only, delay and threshold (#29)
- [ ] Timer and Counter in the domain (#30)
- [ ] Plate-shaped gate blocks: floor, wall and ceiling (#31)
- [ ] Shift + scroll changes a gate's mode (#32)
- [ ] Placement preview before the block exists (#33)
- [ ] Tooltip with each gate's truth table (#34)
- [ ] Release v0.2.0 (#35)

## Phase 3 · Chips

Program your redstone.

- [ ] `Circuit` model compiled in topological order (#36)
- [ ] Loop and oscillation detection that points to the spot (#37)
- [ ] Chip as an item: name, icon and circuit in data components (#38)
- [ ] Circuit Workbench (#39)
- [ ] Drag-and-drop editor with animated wires (#40)
- [ ] Test mode: pause, step one tick and undo (#41)
- [ ] Guide book with ready-to-copy examples (#42)
- [ ] Release v0.3.0 (#43)

## Phase 4 · Expansion

Physical wiring and integrations.

- [ ] Multipart decision: CB Multipart or our own system (#44)
- [ ] Physical redstone wire (#45)
- [ ] 16-channel bundled cable (#46)
- [ ] Integrated Dynamics integration (#47)
- [ ] CC: Tweaked integration (#48)
- [ ] Optional Cubit integration through a tag or an interface (#49)
- [ ] Port to 26.x touching only the adapters (#50)
- [ ] Release v1.0.0 (#51)

## Phase 5 · Bridge (future)

Beyond the game, through MQTT. Disabled by default and server-side only.

- [ ] `EventPublisher` and `CommandReceiver` ports (#52)
- [ ] Asynchronous MQTT adapter, off the server thread (#53)
- [ ] Bridge Node and server config, disabled by default (#54)
- [ ] Authentication, TLS and per-channel message limits (#55)
- [ ] ESP32 prototype: a relay following a channel (#56)
- [ ] Toy car with an ESP32, an H-bridge and a failsafe (#57)
- [ ] Example web dashboard subscribed to the channels (#58)
