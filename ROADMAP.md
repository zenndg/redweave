# Roadmap

Everything planned for Redweave, phase by phase. A checked box is merged into `develop`. Each item points to its GitHub issue. The [issues](https://github.com/zenndg/redweave/issues) and the GitHub Projects board are the source of truth, and this page is updated whenever a pull request is merged.

## Versions

Redweave follows [Semantic Versioning](https://semver.org/). While in `0.x`, a breaking change bumps the minor version. Every release is playable, tested in an instance with only the mod and in the [Iron Meridian](https://github.com/marcelofabianov/iron-meridian) modpack.

Decided on 2026-11-12: Phases 1 and 2 ship together in v0.1.0, as a single phase with one work order (question 303). The Phase 2 release (#35) is closed, and the later versions move down by one, with Phase 3 as v0.2.0.

| Version | When | Release issue |
| --- | --- | --- |
| **v0.1.0** | End of Phase 1 · Wireless and Phase 2 · Logic | #27 |
| v0.2.0 | End of Phase 3 · Chips | #43 |
| v1.0.0 | End of Phase 4 · Expansion | #51 |
| — | Phase 5 · Bridge, after 1.0 | to be planned |

### When v0.1.0 is born

v0.1.0 is the first public version. It is cut only when all of these are true:

- [ ] Every item of Phases 1 and 2 below is merged, in the order of [Toward v0.1.0](#toward-v010).
- [x] The pending Phase 1 decisions (questions 173 to 179) are answered.
- [ ] Each new module is fully refined, with every question answered, before its work starts (question 307).
- [ ] Block, item, data component, NBT and config IDs are frozen, with every module inside (#26). From here on, they only change with a migration.
- [ ] The 1,000-node load test, with modules and gates, shows no visible MSPT impact (#25).
- [ ] The manual test script passes in an instance with only the mod and in Iron Meridian.
- [ ] The release goes through `develop` → `homolog` → `release/0.1.0` → `main`, with the annotated tag `v0.1.0` (#27).

## Phase 1 · Wireless (MVP)

Signal anywhere: networks, nodes, channels and the tools to configure them.

### Foundation

- [x] NeoForge 1.21.1 project with ModDevGradle and Java 21 (#1)
- [x] GitHub Actions CI: build and tests on every push (#2)
- [x] Makefile, git hooks and gates: Spotless, Checkstyle and 85% JaCoCo coverage on the domain and the application (#3)
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
- [x] Configurator modes `Open`, `Copy` and `Wrench`, each with a color (#86)
- [x] A new node joins no network and its owner picks one; node names; switching networks from the Node Screen (#87)
- [x] `Offline` and `Unplaced` network states, and the per-player network limit (#88)
- [x] Controller Screen: node list with name, signal and position, rename and access (#20)
- [x] Signal Node with 16 fixed slots, one per channel color, each enabled or disabled (#20) — slot names removed by #101
- [x] Configurator `Toggle` mode: enable or disable every configured channel of a node (#20)
- [x] Signal change log on the Controller Screen (#90)
- [x] Network Terminal: list, rename, turn off and on, clear nodes and delete unplaced networks (#89)
- [x] Configurator mode display and floating labels over nodes (#80)
- [x] Two-line node labels, and slot names removed (#101)

### Phase 1 decisions answered after the audit

- [x] 173 · ID and values of the Configurator mode component
- [x] 174 · New save keys: node name, `Offline` state and last network used
- [x] 175 · Where the server keeps each player's Network Terminal
- [x] 176 · Rename and clear nodes on an `Unplaced` network
- [x] 177 · How nodes and controllers of an `Offline` network look
- [x] 178 · State of an `Offline` network whose controller is broken and placed again
- [x] 179 · Columns of the node list on the Controller Screen

## Toward v0.1.0

Phases 1 and 2 as one phase. The order follows dependencies first and then difficulty, easiest first (questions 303 and 304). The memory module comes before the Signal Keypad (question 858). Each line is one issue and one pull request.

### Signal Modules

All signal logic lives in **Signal Modules**: plates that attach to any face of any block, like the Signal Node, with a shared base. Their inputs and output are the edges of the plate or network channels, and each module implements only its own rule (questions 297, 300, 308, 309, 313 and 314). The Signal Node only reads and emits. The English names and IDs are decided for the Signal Timer, the Signal Inverter, the Weather Detector, the Signal Randomizer (questions 457 and 475), the Signal Lever, the Signal Button, the Signal Pressure Plate (question 550), the Signal Item Sensor (question 691) and the Signal Lock, Signal Lamp, Signal Alarm, Signal Keypad, Signal Card and Signal Gate (questions 633 to 687); the other modules below still have them open. The lever, the button and the pressure plate form the Signal Emitters family, the modules where the signal starts by a trigger (questions 627 and 632). Every item, with its ID and state, is in the [item catalog](_docs/itens.md).

### Work order

1. [x] Record decisions 279 to 320, audit every Markdown file, reorganize the roadmap, create the new issues and close #29 and #35 (#104)
2. [x] AND, OR, XOR and NAND gates and the NOT rule in the domain, with tested truth tables (#28)
3. [x] Remove the `Logic` row from the Node Screen (#106 · question 308)
4. [x] Status and type columns in the node lists (#107 · question 311)
5. [x] Node Screen in two columns, with the signal meter in the channel panel (#122 · questions 346 to 350)
6. [x] Signal Remote: a tablet linked to a node input slot (#22)
7. [x] Signal Timer and the shared base of the Signal Modules (#103)
8. [x] Signal Inverter, the NOT of the mod (#105 · questions 296 and 310)
9. [x] Weather detector module (#108)
10. [x] Random module (#109)
11. [x] Multipart for the whole mod and the Signal Cable: the Signal Node and the modules become parts that share a block space, and a physical redstone cable in 16 colors (#44 · questions 494 to 575; #45 closed as a duplicate of #44)
12. [x] Signal Lever: a 3D lever with an indicator light that turns from red to green, used by its owner by hand or with the Configurator, and a screen to set its strength from 1 to 15 and send its signal wireless, on a network channel, or by redstone (#130 · questions 548 to 558 and 576 to 587)
13. [x] Signal Button: the same idea as the lever, pressed by its owner by hand or with the Configurator, that turns off by itself after an on time set on its screen with its strength from 1 to 15 (#131 · questions 548 to 558 and 588 to 600)
14. [x] Configurator `Rotate` mode: turns the Signal Modules clockwise, or back with Shift, in place of Shift + click in Open (#136 · questions 610 and 612 to 620)
15. [x] Signal Pressure Plate: the same idea as the lever, on the floor only, on while something of its mode stands on it (players, passive mobs, hostile mobs or items), with the mode and its strength from 1 to 15 on its screen (#132 · questions 548 to 558, 602 to 611 and 621 to 626)
16. [x] Signal Emitters: the family of the modules where the signal starts, with one shared contract and one code base for the lever, the button and the pressure plate, with no change in behavior (#139 · questions 627 to 631)
17. [x] Signal Item Sensor: a plate on a container that emits its strength when the container holds exactly the chosen amount of the chosen item; it only reads, never stores or moves items (#148 · questions 689 to 698)
18. [x] Signal Lock: a lock that only opens with the exact strength of its key, owner-only removal (#141 · questions 633 to 642)
19. [x] Signal Lamp: a multipart LED bar in the color of its channel, with the light level of the strength it gets, an optional control channel (`Hold` or `Pulse`) and the strength it emits when lit set on its screen (#143 · questions 650 to 658)
20. [x] Configurator copy for every item: `Copy` copies the settings of the node and of every Signal Module and pastes them only on an item of the same type and owner, carrying its network and channels; the lock and the keypad are never copied (#154 · questions 722 to 736)
21. [x] Signal Alarm: a double note block sound, a text and a pulsing red light when it gets a signal, up to 10 rings, for a time and for whom its owner chooses (#142 · questions 643 to 649 and 737 to 744)
22. [x] Recipes of the delivered items: vanilla materials with at least one redstone each, one shared shape for the Signal Modules, and the Signal Cable dyes taking only white cables (#157 · questions 745 to 764), reviewed for the Signal Node, the modules, the Controller and the Remote (#159 · questions 765 to 769)
23. [x] Labels, log and slot status: a floating label over the Network Controller, the square of the first enabled slot on the node label, channels shown as their number beside their color square with no color name, the log with the time only, and `On`/`Off` for the slot status (#161 · questions 770 to 777)
24. [x] Signal Remote with the emitters: Shift + right-click links the tablet to a Signal Lever, Button or Pressure Plate of its owner, and whoever holds it fires the emitter from afar, within `remote.emitter_range` and only in loaded chunks (#163 · questions 778 to 793)
25. [x] Network components: the Signal Modules registered in their network, saved with it, so the screens count and list nodes and modules with the chunks unloaded; the `Components` tab and `Clear components` (#165 · questions 801 to 817), with the Controller tabs and the Network Terminal widened to fit them (#175 · questions 856 and 857)
26. [x] Signal Lamp new look: a copper base with a band in the color of the channel, a frosted glass dome and a white LED core, 5 px high, with the glass in the translucent layer (#167 · questions 798 to 800 and 820 to 828), one pixel lower on top of the dome (#169 · question 829)
27. [x] Output delay on the Signal Inverter: 0 to 15 s in whole seconds, when it turns on and off, saved with its pending changes, and a strength from 1 to 15; the shared delay base of the modules (#171 · questions 830 to 841)
28. [x] Output delay on the other Signal Modules with an output: the Signal Lever, Button and Pressure Plate, the Weather Detector, the Signal Randomizer, the Signal Item Sensor and the Signal Lamp, with the strength kept in the line, a cap on the waiting changes and module screens one row taller; not on the Signal Timer nor the Signal Lock (#173 · questions 842 and 845 to 855)
29. [ ] Memory module, a T flip-flop (#110 · question 858)
30. [ ] Signal Keypad: a numeric password, kept only on the server, that lets the signal through (#144 · questions 659 to 667)
31. [ ] Signal Card: an access card its owner links to locks, keypads and gates (#145 · questions 668 to 671)
32. [ ] Block protection: the blocks of a door, chosen by the owner, protected by its lock or keypad (#146 · questions 672 to 676)
33. [ ] Signal Gate: a gate whose field only lets through what its rules allow, with no teleport (#147 · questions 677 to 687)
34. [ ] Latch module, an SR latch (#111)
35. [ ] Gate module: AND, OR, XOR and NAND (#31 · question 309)
36. [ ] Shift + scroll changes a gate's mode (#32)
37. [ ] Tooltip with each gate's truth table (#34)
38. [ ] Schedule module, by the time of day in the game (#112)
39. [ ] Pulse extender module (#113)
40. [ ] Edge detector module (#114)
41. [ ] Threshold module, with hysteresis (#115)
42. [ ] Long delay module (#116)
43. [ ] Player detector module (#117)
44. [ ] Counter module (#30 · question 308)
45. [ ] Placement preview of the modules before the block exists (#33)
46. [ ] Optional Jade and The One Probe integration, covering every block (#23 · question 306)
47. [ ] Emissive textures, the Signal Node lens and the recipes of the items still to come (#24)
48. [ ] Load test with spark: a 1,000-node network with modules and gates (#25)
49. [ ] Freeze block, item, data component and config IDs (#26)
50. [ ] Release v0.1.0 and test in Iron Meridian (#27)

### Dropped from Phase 2

- Per-channel logic on the node slots (#29): replaced by the Signal Modules (question 308).
- Gate blocks as a family of their own: the gate is a Signal Module, a plate like the others (questions 309 and 313).
- The NOT gate: the Signal Inverter covers it (question 310).
- Phase 2 release v0.2.0 (#35): Phase 2 ships in v0.1.0 (question 303).
- The 16-channel bundled cable (#46): 16 Signal Cables of different colors and the network channels already cover it (question 519).
- A teleport portal: moving entities between two places is not signal (rule 2). The idea became the Signal Gate, a selective barrier (#147 · question 677).
- Storing, filtering and moving items in an item receiver: that is item logistics (rule 2). The idea became the Signal Item Sensor, which only reads a container (#148 · question 689).

### Maybe

- Conveyor belts: kept as a maybe, with no plan (question 688).

## Phase 3 · Chips

Program your redstone.

- [ ] `Circuit` model compiled in topological order (#36)
- [ ] Loop and oscillation detection that points to the spot (#37)
- [ ] Chip as an item: name, icon and circuit in data components (#38)
- [ ] Circuit Workbench (#39)
- [ ] Drag-and-drop editor with animated wires (#40)
- [ ] Test mode: pause, step one tick and undo (#41)
- [ ] Guide book with ready-to-copy examples (#42)
- [ ] Release v0.2.0 (#43)

## Phase 4 · Expansion

Integrations and the port to 26.x. The multipart and the physical cable moved to v0.1.0 (question 516).

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
