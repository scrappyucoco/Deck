<div align="center">

# 🃏 Deck — A Balatro-Inspired Deckbuilder

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Course](https://img.shields.io/badge/CIT_240-Object_Oriented_Programming-purple?style=for-the-badge)](https://www.wvsu.edu.ph/)
[![Status](https://img.shields.io/badge/Status-Work--In--Progress-yellow?style=for-the-badge)]()
<!-- [![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)]() -->

*A single-player roguelike deckbuilder combining poker hand evaluation algebra, a java engine, and an interactive gameplay.*

</div>

---

## Overview

**Deck** is a tactical card battler inspired by modern roguelikes like *Balatro*. It blends traditional poker hand evaluation with deep object-oriented system design, challenging players to survive **8 Antes** of escalating score targets ranging from Small Blinds up to the final Showdown Boss Trial along with some unique twists.

### Core Gameplay Pillars
1. **4-Stage Scoring Engine:** Calculates points via $\text{Score} = \text{Chips} \times \text{Mult}$. Evaluates played cards, held card passives (Steel/Gold), and left-to-right Joker multipliers in strict sequential order.
2. **Socket Equipment Engine:** Equip up to 3 Jokers, each featuring **2 Item Sockets** (6 total sockets). Socketable items dynamically retrigger, double, or amplify parent Joker effects.
3. **Monologue Narrative Trials:** Post-round dialogue minigames where players debate existential prompts using sentence fragment blocks. Correct logical alignment grants high-tier rewards; failure incurs penalties.
4. **Custom CSS & Pixel Art UI:** Polished JavaFX interface powered by external CSS stylesheets (`assets/ui/`) and crisp 8x pixel art assets.

---

## Project Architecture & OOP Design

The codebase strictly adheres to core Object-Oriented Programming principles to maintain modularity and clean separation of concerns:

- **Encapsulation:** Domain models (`Card`, `GameState`, `ScoringContext`) guard internal state behind strict mutator validations.
- **Inheritance:** Base superclasses (`Joker`, `Item`, `Occurrence`) establish shared properties while concrete subclasses override unique behavior.
- **Polymorphism:** The 4-stage evaluation engine calls `evaluate(ScoringContext ctx)` on `Scorable` interfaces without needing to know concrete runtime types.
- **Decorator & Strategy Patterns:** Socket items wrap around parent Jokers to modify execution count and multipliers dynamically; poker hand parsers apply runtime evaluation strategies.

---

## 📁 Repository & Package Structure

```text
Deck/
├── src/
│   ├── module-info.java
│   └── com/game/deck/
│       ├── AppLauncher.java         # JavaFX UI Entry Point
│       ├── ConsoleTestRunner.java    # Headless Logic Debugger
│       ├── model/                   # Card, Deck, GameState, ScoringContext, PokerHand
│       ├── equipment/               # Joker, Item, 11 Concrete Socket Items
│       ├── engine/                  # ScoringEngine, PokerEvaluator, GameLoop
│       ├── occurrences/             # Occurrence, MonologueTrial, RandomEvents
│       ├── shop/                    # ShopManager, TarotCard, TomeOfMastery
│       └── views/                   # CardNode, GameScene, UI Controllers
└── assets/
    ├── cards/                       # 52 Playing Card Textures (e.g., spades_ace.png)
    ├── enhancements/                # Card Frame Overlays (bonus_shield.png, etc.)
    ├── jokers/                      # Joker Artwork Tiles
    ├── items/                       # Socket Item Badges (11 Items)
    └── ui/                          # External CSS Stylesheets (theme.css, cards.css)

```

> **Note on `com.game.deck.equipment`:** Both `Joker` and `Item` live in this package to model their tight composition/decorator relationship and prevent circular package imports.

---

## Setup & Installation Instructions for Collaborators

### 1. Prerequisite: Download JDK 21 with JavaFX Included

To avoid configuring complex `--module-path` or `--add-modules` VM options, **all developers and reviewers MUST use a JDK that pre-bundles JavaFX**:

* **Option A (Recommended):** [BellSoft Liberica JDK 21 Full](https://bell-sw.com/pages/downloads/). *(Select the **"Full JDK"** download variant)*.
* **Option B:** [Azul Zulu JDK 21 with JavaFX](https://www.azul.com/downloads/).

> ⚠️ **Important:** Standard Oracle OpenJDK does **NOT** include JavaFX out of the box. Installing Liberica JDK 21 Full or Zulu FX ensures JavaFX runs natively without manual module configurations.

---

### 2. Setting Up in Eclipse IDE (I Recommend you all use Eclipse IDE)

1. **Clone the Repository**:
```bash
cd C:\Users\**YourUsername**\Documents
git clone https://github.com/scrappyucoco/Deck.git

```


2. **Import into Eclipse**:
* Open Eclipse $\rightarrow$ **File** $\rightarrow$ **Import...**
* Select **Existing Projects into Workspace** $\rightarrow$ browse to the cloned `Deck` folder(Documents) $\rightarrow$ **Finish**.


3. **Configure JRE in Eclipse**:
* Go to Window $\rightarrow$ Preferences (or Eclipse $\rightarrow$ Settings on macOS).
* Navigate to Java $\rightarrow$ Installed JREs.
* Click Add... $\rightarrow$ select Standard VM $\rightarrow$ Next.
* Click Directory... and locate your installed Liberica JDK 21 Full or Zulu JDK 21 FX folder $\rightarrow$ click Finish and check it as default.


4. **Set Project JDK**:
* Right-click the project in Package Explorer $\rightarrow$ **Properties** $\rightarrow$ **Java Build Path**.
* Under the **Libraries** tab, select **JRE System Library** $\rightarrow$ click **Edit...**.
* Choose **Alternate JRE** and point it to your installed **Liberica JDK 21 Full** (or Zulu JDK 21 FX).


5. **Mark `assets` as a Resource Folder** *(Crucial for CSS and Image loading!)*:
* Still in **Java Build Path**, go to the **Source** tab.
* Click **Add Folder...** on the right $\rightarrow$ check the box for `assets` $\rightarrow$ click **OK**.
* Click **Apply and Close**.



---

### 3. Running the Project

* **To Run the GUI App**:
* Right-click `src/com/game/deck/AppLauncher.java` $\rightarrow$ **Run As** $\rightarrow$ **Java Application**.


* **To Run the Headless Engine Test Harness**:
* Right-click `src/com/game/deck/ConsoleTestRunner.java` $\rightarrow$ **Run As** $\rightarrow$ **Java Application**.



---

### 4. Building the Executable JAR (`Deck.jar`)

#### Via Eclipse:

1. Go to **File** $\rightarrow$ **Export...** $\rightarrow$ **Java** $\rightarrow$ **Runnable JAR file**.
2. Select Launch Configuration: `AppLauncher - Deck`.
3. Set export destination (e.g., your Desktop).
4. Select **Extract required libraries into generated JAR** $\rightarrow$ Click **Finish**.

#### Via Terminal:

```bash
# Compile source files into bin/
javac -d bin --module-source-path src $(find src -name "*.java")

# Copy assets to bin root so they package inside the JAR
cp -r assets bin/

# Create runnable JAR
jar --create --file Deck.jar --main-class com.game.deck.AppLauncher -C bin .

# Test run
java -jar Deck.jar

```

#### Packaging `Deck.jar` as a Windows `.exe`

To package the runnable `Deck.jar` as a Windows installer, install **Liberica JDK 21 Full** or **Liberica JDK 21 FX**. These distributions include JavaFX, and the JDK includes the `jpackage` tool used below. Run the command from the project directory:

```powershell
# Create an input directory containing the runnable JAR
New-Item -ItemType Directory -Force dist | Out-Null
Copy-Item Deck.jar dist/

# Build the Windows installer and place it in output/
jpackage --type exe `
  --name DeckGame `
  --input dist `
  --main-jar Deck.jar `
  --main-class com.game.deck.AppLauncher `
  --dest output
```

The generated installer will be placed in `output/`. If JavaFX is not included in the JAR, add the JavaFX modules and the `lib` directory from the Liberica JDK installation:

```powershell
jpackage --type exe `
  --name DeckGame `
  --input dist `
  --main-jar Deck.jar `
  --main-class com.game.deck.AppLauncher `
  --module-path "$env:JAVA_HOME\lib" `
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.media `
  --dest output
```

Use the first command when the JAR already contains the application's dependencies. Use the second command when JavaFX is available as separate modules in the Liberica JDK installation.

---

## Asset Naming Conventions

When contributing new graphics or custom styles to `assets/`, please follow these exact naming patterns:

* **Cards (`assets/cards/`)**: `{suit}_{rank}.png` (e.g., `spades_ace.png`, `hearts_king.png`, `diamonds_10.png`, `clubs_2.png`).
* **Items (`assets/items/`)**: `{tier}_{name}.png` (e.g., `gem_polished.png`, `mirror_shattered.png`, `relic_ego.png`).
* **UI Styles (`assets/ui/`)**: Modify `theme.css` for table backgrounds and buttons; modify `cards.css` for card frames and selection glows.

---

## 🤝 Contribution Guidelines

1. Pull the latest changes from `main` before starting new feature work (`git pull origin main`).
2. Do **not** commit compiled `.class` files or the `bin/` folder (already handled by `.gitignore`).
3. Keep backend logic strictly isolated in `engine/` or `equipment/`, separated from JavaFX UI code in `views/`.
4. Refer to the project documentation and workflow guidelines via the [Collaboration & Workflow Wiki/Docs](https://docs.google.com/document/d/1JbXFVtgIU0BfoM6Q8dCaDsQ9rexq2SIlM1eUp8O-u2c/edit?usp=sharing).
