# Final Fantasy Character Manager

A Java application to manage **Final Fantasy characters** with support for **CLI and GUI interfaces**.  
This project allows users to **add, view, update, and delete characters** and persists all data in an **SQLite database** (`ffgame.db`).

---

## Features

- **Add characters manually** (name, job, level, HP, active status)
- **Import characters from CSV files**
- **Level up characters** by ID
- **Remove characters** by ID
- **View all characters**
- **SQLite database** to store characters persistently
- **Dual interface**: CLI or GUI

---

## Technologies Used

- Java 17+
- SQLite (via `sqlite-jdbc`)
- Swing (for GUI)
- CLI with `Scanner` input

---

## Database

- The database file: `ffgame.db`
- Table schema (`characters`):

```sql
CREATE TABLE IF NOT EXISTS characters (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    job TEXT NOT NULL,
    level INTEGER NOT NULL,
    hp REAL NOT NULL,
    isActive INTEGER NOT NULL
);

```

## Jobs

Warrior, Thief, Monk, Red Mage, White Mage, Black Mage,
Knight, Ninja, Master, Red Wizard, White Wizard, Black Wizard,
Archer, Bard, Beastmaster, Berserker, Blue Mage, Chemist,
Dancer, Dark Knight, Dragoon, Machinist, Geomancer, Samurai,
Summoner, Scholar, Astrologian, Time Mage.

