# 🦈 SharkSkyblock

SharkSkyblock is a custom Skyblock progression plugin built for **Paper 1.21.1**, designed around structured gameplay systems including islands, economy, skills, quests, ocean events, and a rule-based assistant system.

The focus is on modular design, progression-based gameplay, and maintainable backend architecture using **SQLite persistence**.

The project is currently in active development. The **Island Generation System is ~75% complete**.

---

## 📌 Project Status

- 🏝️ Island Generation System: ~75% complete  
- 🧠 Shark Assistant (Rule-Based AI): ~75% complete  
- 💰 Economy System: In development  
- ⛏️ Skills System: In development  
- 📜 Quest System: In development  
- 🌊 Ocean Events: Planned  
- 🛡️ Island Protection System: In development  
- 💾 SQLite Database Layer: Implemented (base structure ready)  

---

## ✨ Core Features

### 🏝️ Island System
A core gameplay system where every player receives a personal progression island.

**Current implementation:**
- Automatic island generation on request
- One island per player restriction
- Island teleport system (`/island home`)
- Island creation and deletion logic
- Structured island placement system
- Base framework for future island upgrades

**Status:** ~75% complete (generation + core lifecycle implemented, expansion systems in progress)

---

### 🧠 Shark Assistant (Rule-Based AI System)
A built-in progression assistant that guides players without external APIs or AI services.

**Current features:**
- New player onboarding guidance
- Quest reminders and progression hints
- Skill progression suggestions
- Event notifications
- Survival warnings (low health / risk states)
- Rule-based contextual response system

**Design goals:**
- Fully offline system (no external AI dependency)
- Lightweight rule engine
- Easy expansion via configuration or code modules
- Integration with all major gameplay systems

---

### 💰 Economy System (In Development)
A server-wide currency system designed for progression and trading.

**Planned features:**
- Shark Coins currency
- Player balance tracking
- Secure player-to-player transfers
- Economy hooks for quests, skills, and events
- SQLite persistence support

---

### ⛏️ Skills System (In Development)
A progression system tied directly to gameplay activity.

**Planned skills:**
- Mining skill progression
- Fishing skill progression
- Experience-based leveling
- Unlockable rewards per milestone
- Integration with quests and economy systems

---

### 📜 Quest System (In Development)
A structured objective system for daily and long-term progression.

**Planned features:**
- Mining and fishing quests
- Dynamic progress tracking
- Coin and XP rewards
- Rotating daily quest pool
- Integration with skills system

---

### 🌊 Ocean Events (Planned)
Server-wide dynamic events designed to encourage activity and competition.

**Planned features:**
- Treasure chest spawn events
- Global server announcements
- Competitive loot rewards
- Rare item drops

---

### 🛡️ Island Protection System (In Development)
Prevents griefing and enforces structured island ownership rules.

**Planned features:**
- Block protection within island boundaries
- Interaction restrictions based on ownership
- Permission-based access control
- Expandable protection rule system

---

### 💾 Database System (SQLite)
Persistent backend system used for all player and island data.

**Current implementation:**
- Player data storage
- Island data persistence
- Base infrastructure for future systems
- Automatic save/load handling

---

## 🧱 Architecture Overview

SharkSkyblock is designed using a modular and scalable architecture:

- Independent feature modules (Island, Economy, Skills, Assistant)
- Shared service layer for player data access
- SQLite-backed persistence layer
- Event-driven system communication
- Expandable structure without core rewrites

This design allows new systems to be added without modifying existing core logic.

---

## 📦 Commands

### 🏝️ Island Commands
```text
/island create   - Create a new island
/island home     - Teleport to your island
/island delete   - Delete your island
