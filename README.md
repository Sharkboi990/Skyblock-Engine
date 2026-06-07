# SharkSkyblock

- **Current Version:** 0.1.8
- **Development Progress:** ~30% Complete

SharkSkyblock is a custom Skyblock plugin for Paper 1.21.1 focused on progression, island management, player assistance, economy systems, skills, quests, and server management tools.

The project is currently under active development and is not yet ready for public release.

## Project Overview

SharkSkyblock is designed around structured progression rather than traditional Skyblock gameplay. The goal is to provide players with meaningful progression systems, intuitive interfaces, and long-term gameplay objectives through a modular and expandable architecture.

Current development is focused on establishing core systems before expanding into advanced progression and content features.

---

## Current Development Status

### Implemented

* Island Generation System (~75% Complete)
* Island NPC System
* Player Menu System
* Hub System
* Shark Assistant System (~75% Complete)
* Admin GUI
* Anti-Cheat Framework
* Anti-Cheat Score System
* SQLite Database Layer

### In Development

* Economy System
* Skills System
* Quest System
* Island Protection System

### Planned

* Ocean Events
* Additional Progression Systems
* Expanded Gameplay Content

---

## Features

### Island System

The Island System serves as the foundation of SharkSkyblock.

Current Features:

* Automatic island generation
* One island per player
* Island creation
* Island deletion
* Island teleportation
* Island ownership management
* Structured island placement system

Current Status:

* Core functionality implemented
* Approximately 75% complete
* Future expansion systems are still in development

---

### Island NPC System

Every island includes a dedicated NPC that acts as a central access point for island-related systems.

Features:

* Island management access
* Quick navigation menus
* GUI-based interactions
* Centralized island controls

The NPC is intended to provide a simple and accessible user experience without requiring players to memorize commands.

---

### Player Menu System

Players receive a dedicated hotbar item that provides access to major gameplay systems.

Current Menus:

* Skills
* Statistics
* Recipes
* Teleportation
* Island Management
* Progression Interfaces

The menu system serves as the primary interaction hub for players.

---

### Shark Assistant

Shark Assistant is a built-in rule-based assistant designed to guide players through progression systems.

Features:

* New player guidance
* Progression recommendations
* Quest reminders
* Event notifications
* Survival warnings
* Context-aware assistance

The assistant operates entirely offline and does not rely on external AI services or APIs.

---

### Hub System

A custom hub schematic acts as the central player spawn and navigation area.

Features:

* Centralized spawn location
* Navigation point for future systems
* Future NPC integration support
* Expandable framework

---

### Economy System (In Development)

The economy system is built around Shark Coins.

Planned Features:

* Player balances
* Coin transfers
* Reward systems
* Progression integration
* SQLite persistence

---

### Skills System (In Development)

A progression framework designed around player activity.

Planned Skill Categories:

* Mining
* Fishing

Planned Features:

* Experience progression
* Skill levels
* Milestone rewards
* Economy integration
* Quest integration

---

### Quest System (In Development)

A structured objective system designed to provide progression goals.

Planned Features:

* Daily quests
* Mining quests
* Fishing quests
* Progress tracking
* Coin rewards
* Experience rewards

---

### Island Protection System (In Development)

A protection framework designed to enforce ownership rules and prevent griefing.

Planned Features:

* Block protection
* Interaction restrictions
* Ownership validation
* Permission-based access

---

### Ocean Events (Planned)

Server-wide activities designed to increase player engagement.

Planned Features:

* Treasure chest events
* Global announcements
* Competitive rewards
* Rare loot opportunities

---

### Anti-Cheat System

A built-in anti-cheat framework designed to maintain fair gameplay.

Features:

* Violation detection
* Suspicious activity monitoring
* Administrative review tools
* Expandable detection framework
* Progressive punishment support

#### Anti-Cheat Score System

The anti-cheat score system tracks player violations over time.

Features:

* Violation accumulation
* Progressive flagging
* Suspicion tracking
* Reduced impact from isolated false positives
* Administrative monitoring support

---

### Admin GUI

A centralized administrative interface designed to simplify server management.

Features:

* GUI-based administration
* Monitoring tools
* Quick management actions
* Administrative controls
* System integration

---

### Database System

SharkSkyblock uses SQLite for persistent storage.

Current Storage:

* Player data
* Island data
* Progression data

Features:

* Automatic saving
* Automatic loading
* Persistent progression
* Lightweight deployment

---

## Architecture

SharkSkyblock follows a modular architecture.

Core Principles:

* Independent gameplay modules
* Event-driven communication
* SQLite-backed persistence
* Shared service layer
* Expandable framework
* Maintainable code structure

This architecture allows new systems to be integrated without major modifications to existing systems.

---

## Commands

### Player Commands

```text
/island create
/island home
/island delete
/menu
```

### Administration Commands

```text
/admin
```

### Anti-Cheat Commands

The anti-cheat framework includes multiple administrative commands used for:

* Violation management
* Player monitoring
* Anti-cheat score management
* Flag inspection
* Debugging
* Configuration
* Administrative moderation

---

## Design Goals

SharkSkyblock is designed around:

* Structured player progression
* Long-term gameplay objectives
* Modular architecture
* GUI-driven interaction
* Lightweight performance impact
* Expandable gameplay systems
* Reduced command dependency

---

## Notes

SharkSkyblock is currently in active development and is approximately 30% complete overall.

Version 0.1.8 represents an early development milestone focused on building the foundation of the project. Features, balancing, architecture, and gameplay systems may change significantly as development continues. Source code file will be uploaded in a few days for view only.
