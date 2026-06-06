# SharkSkyblock

SharkSkyblock is a custom Skyblock plugin developed for Paper 1.21.1.  
It focuses on structured progression systems including islands, economy, skills, quests, ocean events, and a rule-based assistant system.

The project is currently in active development.  
The Island Generation System is approximately 75% complete, along with the Shark Assistant system at a similar stage.

---

## Project Overview

SharkSkyblock is designed to move away from traditional unstructured Skyblock gameplay and instead provide a progression-driven experience with modular systems that interact with each other.

Each system is designed to be independent but expandable, allowing future features to integrate without major rewrites.

---

## Development Status

- Island Generation System: ~75% complete
- Shark Assistant (Rule-Based AI): ~75% complete
- Economy System: In development
- Skills System: In development
- Quest System: In development
- Ocean Events: Planned
- Island Protection System: In development
- SQLite Database Layer: Implemented (base structure complete)

---

## Island System

The Island System is the core gameplay foundation of SharkSkyblock.

### Current Features
- Automatic island generation for players
- One island per player enforcement
- Island teleport system (/island home)
- Island creation system
- Island deletion system
- Structured island placement logic

### Status
The system is functional at a core level, with island creation, placement, and teleportation implemented. Expansion features such as upgrades and progression layers are planned.

---

## Shark Assistant (Rule-Based AI System)

The Shark Assistant is a built-in guidance system designed to help players progress through the server.

It does not use external APIs or AI services and is fully rule-based.

### Current Features
- New player onboarding guidance
- Quest reminders and progression hints
- Skill progression suggestions
- Event notifications
- Survival warnings (low health or danger states)

### Design Approach
- Fully offline system
- Rule-based decision logic
- Lightweight performance impact
- Integrated with gameplay systems
- Expandable behavior rules

---

## Economy System (In Development)

A global currency system built around Shark Coins.

### Planned Features
- Player balance system
- Coin earning from gameplay activities
- Player-to-player transfers
- Integration with quests and skills
- SQLite-based persistence

---

## Skills System (In Development)

A progression system based on player activity.

### Planned Skills
- Mining skill progression
- Fishing skill progression
- Experience-based leveling system
- Rewards per skill milestone
- Integration with quests and economy

---

## Quest System (In Development)

A structured objective system designed to guide player progression.

### Planned Features
- Daily mining and fishing quests
- Progress tracking system
- Coin and XP rewards
- Rotating quest pool system
- Integration with skills and economy

---

## Ocean Events (Planned)

Server-wide dynamic events designed to increase player activity.

### Planned Features
- Treasure chest spawning events
- Global announcements
- Competitive reward system
- Rare loot drops and bonuses

---

## Island Protection System (In Development)

A protection layer to prevent griefing and enforce island ownership rules.

### Planned Features
- Block protection within island boundaries
- Interaction restrictions based on ownership
- Permission-based access control
- Expandable protection rule system

---

## SQLite Database System

The plugin uses SQLite for persistent data storage.

### Current Implementation
- Player data storage
- Island data persistence
- Base structure for future systems
- Automatic save and load handling

---

## Architecture

SharkSkyblock is built using a modular architecture.

### Key Principles
- Each system is independent (Island, Economy, Skills, Assistant)
- Shared service layer for data access
- SQLite-based persistence layer
- Event-driven system communication
- Expandable without core rewrites

This architecture allows new features to be added without breaking existing systems.

---

## Commands

### Island Commands
/island create   - Create a new island
/island home     - Teleport to your island
/island delete   - Delete your island

---

## Design Philosophy

SharkSkyblock is designed around structured progression instead of open-ended sandbox gameplay.

The core design goals are:
- Clear progression systems for players
- Modular and maintainable architecture
- Lightweight but meaningful economy interaction
- Expandable systems without rewriting core logic
- Long-term scalability for future features
  
---

## Notes

This project is actively evolving.  
Features and systems may change as development continues, balancing improves, and architecture is refined.
