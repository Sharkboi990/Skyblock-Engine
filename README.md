# SharkSkyblock

Version: 0.1.8

Development Progress: ~30% Complete

SharkSkyblock is a custom Skyblock plugin developed for Paper 1.21.1 focused on progression-based gameplay through islands, skills, collections, quests, economy systems, anti-cheat systems, player assistance tools, and GUI-driven interaction.

The project is currently under active development and is not yet ready for public release.

---

## Repository Notice

This repository is not updated in real time.

Development is performed primarily on private development branches and local builds. Public repository updates are intentionally delayed by approximately three months.

This allows major systems to be developed, tested, optimized, and refactored before being published publicly.

As a result, the repository may not always reflect the latest internal development progress.

Repository releases should be considered stable development snapshots rather than real-time development builds.

---

## Project Overview

SharkSkyblock aims to provide a progression-focused Skyblock experience built around long-term player goals rather than traditional survival gameplay.

The project is designed using a modular architecture that allows new systems to be added without major rewrites, making future expansion significantly easier.

Current development is focused on building core infrastructure before expanding into advanced gameplay systems.

---

## Current Development Status

### Implemented

* Island Generation System (~75% Complete)
* Island Management System
* Player Menu System
* Hub System
* Shark Assistant Framework
* Admin GUI
* Anti-Cheat Framework
* Anti-Cheat Score System
* SQLite Persistence Layer

### Under Development

* Economy System
* Skills System
* Collections System
* Recipe Unlock System
* Shop System
* Quest System
* Achievement System
* Profile System
* Statistics Expansion
* Skill Rewards
* Collection Rewards
* Advanced Teleportation System
* Progression Menu Redesign
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
* Structured island placement

Current Status:

* Core functionality implemented
* Generation system approximately 75% complete

---

### Island Management

Players can manage their islands through GUI-driven interfaces.

Features:

* Island controls
* Island information
* Quick-access menus
* Future upgrade support

The system is designed to reduce command dependency and improve accessibility.

---

### Player Menu System

Players receive a dedicated hotbar item that acts as a centralized access point for gameplay systems.

Current Menus:

* Island Management
* Statistics
* Teleportation
* Progression Interfaces

Menus Under Development:

* Skills
* Collections
* Recipes
* Shops
* Quests
* Achievements
* Profile
* Settings

The menu system serves as the primary interaction hub for players.

---

### Shark Assistant

Shark Assistant is a built-in rule-based assistant designed to guide players throughout their progression.

Features:

* New player guidance
* Progression recommendations
* Quest reminders
* Event notifications
* Survival warnings
* Context-aware assistance

The assistant operates entirely offline and does not rely on external AI services.

---

### Hub System

A custom hub schematic acts as the central player spawn and navigation area.

Features:

* Centralized spawn location
* Access point for gameplay systems
* GUI integration
* Future expansion support

---

### Economy System

Currently under development.

Planned Features:

* Shark Coins currency
* Player balances
* Coin transfers
* Reward systems
* Progression integration
* SQLite persistence

---

### Skills System

Currently under development.

Planned Skills:

* Mining
* Fishing
* Farming
* Combat
* Foraging

Planned Features:

* Experience progression
* Skill levels
* Milestone rewards
* Collection integration
* Quest integration

---

### Collections System

Currently under development.

Planned Features:

* Resource tracking
* Collection progression
* Unlockable rewards
* Recipe unlock integration
* Progress milestones

---

### Recipe System

Currently under development.

Planned Features:

* Unlockable recipes
* Collection requirements
* Progression-based crafting
* Recipe tracking interface

---

### Shop System

Currently under development.

Planned Features:

* GUI-based shops
* Buy and sell support
* Economy integration
* Flexible configuration

---

### Quest System

Currently under development.

Planned Features:

* Daily quests
* Mining quests
* Fishing quests
* Collection quests
* Progress tracking
* Coin rewards
* Experience rewards

---

### Achievement System

Currently under development.

Planned Features:

* Progress milestones
* Achievement rewards
* Long-term objectives
* Progress tracking

---

### Anti-Cheat System

A built-in anti-cheat framework designed to maintain fair gameplay.

Current Features:

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
* Administrative monitoring
* Reduced impact from isolated false positives

---

### Admin GUI

A centralized administrative interface designed to simplify server management.

Features:

* GUI-based administration
* Monitoring tools
* Quick management actions
* Anti-cheat integration
* Administrative controls

---

### Database System

SharkSkyblock uses SQLite for persistent storage.

Stored Data:

* Player data
* Island data
* Statistics
* Progression data

Features:

* Automatic saving
* Automatic loading
* Persistent progression
* Lightweight deployment

---

## Commands

### Player Commands

/island create

/island home

/island delete

/menu

### Administration Commands

/admin

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

## Architecture

SharkSkyblock follows a modular architecture.

Core Principles:

* Independent gameplay modules
* Event-driven communication
* SQLite-backed persistence
* Shared service layer
* Expandable framework
* Maintainable code structure

This architecture allows future systems to be integrated without major modifications to existing functionality.

---

## Development Timeline

SharkSkyblock is a long-term project currently in its early development stages.

The project is approximately 30% complete overall and remains focused on building core infrastructure before moving toward a public release candidate.

Based on the current scope and planned systems, development is expected to continue for approximately 1-2 years before reaching a feature-complete release phase.

Development is currently paused for approximately three months before active development resumes.

The goal is to prioritize quality, maintainability, and long-term scalability rather than rushing development.

---

## Changelog

### Version 0.1.8

Implemented:

* Island Generation System foundation
* Island Management System
* Player Menu System
* Hub System
* Shark Assistant framework
* Admin GUI
* Anti-Cheat framework
* Anti-Cheat Score System
* SQLite persistence layer

Current Focus:

* Economy System
* Skills System
* Collections System
* Recipe Unlock System
* Shop System
* Quest System
* Achievement System
* Progression Menu Redesign

Development logs will continue to be updated as major milestones are completed.

---

## Design Goals

SharkSkyblock is designed around:

* Structured player progression
* Long-term gameplay objectives
* GUI-driven interaction
* Modular architecture
* Lightweight performance impact
* Expandable gameplay systems
* Reduced command dependency

---

## Notes

SharkSkyblock remains an early-stage project and is far from feature complete.

Version 0.1.8 represents a foundational development milestone focused on core systems and infrastructure. Features, balancing, architecture, and gameplay systems may change significantly throughout development.
