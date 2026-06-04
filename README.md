# SharkSkyblock

SharkSkyblock is a custom Skyblock plugin for Paper 1.21.1 focused on progression, economy, island management, quests, ocean events, and player assistance systems.

The project is currently under active development. Features, balancing, performance improvements, and new content will be added over time.

A public release will be published once the plugin reaches a stable state.

## Features

### Island System

* Create a personal island
* Teleport to your island
* Delete your island
* One island per player
* Automatic island placement

### Economy

* Shark Coins currency
* Player balances
* Coin transfers between players
* SQLite data storage

### Skills

* Mining
* Fishing
* Level progression
* Experience rewards
* Skill-based advancement

### Daily Quests

* Mining quests
* Fishing quests
* Progress tracking
* Coin rewards

### Ocean Events

* Automatic treasure chest events
* Server-wide announcements
* Reward system for winners

### Island Protection

* Protected islands
* Build restrictions
* Interaction protection

### Shark Assistant

A built-in assistant that helps players progress through the server.

Functions include:

* New player guidance
* Progress recommendations
* Quest reminders
* Near-death warnings
* Event notifications
* Skill progression suggestions

The assistant is rule-based and does not rely on external APIs or AI services.

### Database

* SQLite support
* Persistent player data
* Automatic saving and loading

## Commands

### Island

```text
/island create
/island home
/island delete
```

### Economy

```text
/balance
/pay <player> <amount>
```

### Skills

```text
/skills
```

### Quests

```text
/quests
```

### Assistant

```text
/assistant
/assistant help
/assistant next
/assistant stats
```

### Administration

```text
/sharksb reload
/sharksb event
```

## Permissions

```text
sharksb.admin
```

Provides access to administrative commands.

## Requirements

* Java 21
* Paper 1.21.1+

## Roadmap

Planned improvements include:

* Additional skills
* More quest types
* Expanded ocean events
* Island upgrades
* Island leaderboards
* Trading system
* PlaceholderAPI support
* GUI menus
* Additional progression systems
* More assistant functionality

## Development Status

This project is actively maintained.

Future updates may include:

* New gameplay features
* Performance improvements
* Bug fixes
* Balance changes
* Quality of life improvements

## Release

The plugin is not yet considered complete.

Once development reaches a stable stage and the core systems are fully tested, SharkSkyblock will be released publicly.
