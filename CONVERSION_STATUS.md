# Powerbot → DreamBot Conversion Status

Branch: `claude/powerbot-to-dreambot-conversion-t7sn8`

## Strategy

Mirror the Powerbot package tree at `src/org/dreambot/powerslayer/`. Leverage
DreamBot built-ins instead of porting our custom equivalents:

- Drop `data/Banks.java` → use `org.dreambot.api.methods.container.impl.bank.BankLocation`
- Drop `data/Quests.java` → use `org.dreambot.api.methods.quest.Quests` +
  `FreeQuest`/`PaidQuest` (both implement `org.dreambot.api.methods.quest.book.Quest`)
- Map `Skills` int constants → `org.dreambot.api.methods.skills.Skill` enum
  (`DEFENSE`→`DEFENCE`, `RANGE`→`RANGED`, `CONSTITUTION`→`HITPOINTS`)
- Map `Equipment.Slot` → `EquipmentSlot` (`HELMET`→`HAT`)
- OSRS has no Summoning skill → drop SUMMONING requirements
- `Filter<T>` is DreamBot-native (method `match`, not `accept`)

## API mapping cheatsheet

| Powerbot/RSBot | DreamBot |
|---|---|
| `org.rsbot.script.Script` | `org.dreambot.api.script.AbstractScript` |
| `org.rsbot.script.wrappers.Tile` | `org.dreambot.api.methods.map.Tile` |
| `org.rsbot.script.wrappers.Area` | `org.dreambot.api.methods.map.Area` |
| `org.rsbot.script.wrappers.NPC` | `org.dreambot.api.wrappers.interactive.NPC` |
| `org.rsbot.script.wrappers.Player` | `org.dreambot.api.wrappers.interactive.Player` |
| `org.rsbot.script.wrappers.GameObject` | `org.dreambot.api.wrappers.interactive.GameObject` |
| `org.rsbot.script.wrappers.GroundItem` | `org.dreambot.api.wrappers.items.GroundItem` |
| `org.rsbot.script.wrappers.Item` | `org.dreambot.api.wrappers.items.Item` |
| `org.rsbot.script.wrappers.Character` | `org.dreambot.api.wrappers.interactive.Character` |
| `org.rsbot.script.wrappers.GameModel` | `org.dreambot.api.wrappers.interactive.Model` |
| `org.rsbot.script.methods.Players` | `org.dreambot.api.methods.interactive.Players` |
| `org.rsbot.script.methods.NPCs` | `org.dreambot.api.methods.interactive.NPCs` |
| `org.rsbot.script.methods.Skills` | `org.dreambot.api.methods.skills.Skills` (+ `Skill` enum) |
| `org.rsbot.script.methods.Calculations` | `org.dreambot.api.methods.Calculations` (+ `Tile.distance`) |
| `org.rsbot.script.methods.Walking` | `org.dreambot.api.methods.walking.impl.Walking` |
| `org.rsbot.script.methods.GroundItems` | `org.dreambot.api.methods.item.GroundItems` |
| `org.rsbot.script.methods.Menu` | `org.dreambot.api.wrappers.widgets.Menu` |
| `org.rsbot.script.methods.Mouse` | `org.dreambot.api.input.Mouse` |
| `org.rsbot.script.methods.Game.Tabs` | `org.dreambot.api.methods.tabs.Tabs` + `Tab` enum |
| `org.rsbot.script.methods.Settings.get(i)` | `org.dreambot.api.methods.settings.PlayerSettings.getConfig(i)` |
| `org.rsbot.script.methods.ui.Camera` | `org.dreambot.api.methods.input.Camera` |
| `org.rsbot.script.methods.ui.Interfaces` | `org.dreambot.api.methods.widget.Widgets` (+ `Dialogues`) |
| `org.rsbot.script.methods.ui.Bank` | `org.dreambot.api.methods.container.impl.bank.Bank` |
| `org.rsbot.script.methods.tabs.Inventory` | `org.dreambot.api.methods.container.impl.Inventory` |
| `org.rsbot.script.methods.tabs.Equipment` | `org.dreambot.api.methods.container.impl.equipment.Equipment` |
| `org.rsbot.script.methods.tabs.Combat` | `org.dreambot.api.methods.combat.Combat` |
| `org.rsbot.script.methods.tabs.Prayer` | `org.dreambot.api.methods.prayer.Prayers` |
| `org.rsbot.script.util.Filter` (`accept`) | `org.dreambot.api.methods.filter.Filter` (`match`) |

Camera: use `Camera.rotateToYaw`, `Camera.rotateToTile`, `Camera.rotateToEntity`.
Run/walking: `Walking.isRunEnabled`, `Walking.toggleRun`, `Walking.getRunEnergy`,
`Walking.walk(Tile)`, `Walking.walkExact(Tile)`.
Continue dialogue: `Dialogues.canContinue`, `Dialogues.continueDialogue`.
Menu actions: `Menu.getActions`, `Menu.contains(action)`, `Menu.clickAction(action)`.
Logger: `org.dreambot.api.utilities.Logger.log(...)`.
Poison: `Combat.isPoisoned()`.
Bank distance: `Bank.getClosestBankLocation()` or iterate `BankLocation.values()` +
`loc.getCenter().distance(local)`.

## Conversion progress

### Done

- `abstracts/State.java`
- `abstracts/GoToState.java`
- `common/DMethodProvider.java` (uses `Logger.log`, DreamBot wrapper imports)
- `common/MethodBase.java`
- `data/SlayerMaster.java` (uses `Skill.SLAYER`, `Skills.getRealLevel`)
- `data/SlayerItems.java` (uses `EquipmentSlot.HAT`/`SHIELD`/etc., `Skill` enum,
  `PaidQuest.*` via the `Quest` interface; requirements stored as `Object[]`
  alternating `(Skill, Integer)` pairs; SUMMONING dropped)
- `data/Monsters.java`
- `wrappers/*.java` (MonsterInfo, MonsterLocation, LocationProfile,
  Requirements, Task, Starter, Finisher)
- `methods/CombatStyle.java`, `methods/Banking.java`, `methods/Traveling.java`,
  `methods/SlayerInventory.java`, `methods/SlayerEquip.java`,
  `methods/SlayerMasters.java`
- `states/*.java` (GoToMonsterState, GoToBankState, GoToMasterState,
  GetTaskState, BankingState, FighterState)
- `methods/UniversalFighter.java` — simplified `clickNPC` to use
  `NPC.interact(action)` (DreamBot handles walk + camera); simplified
  `takeItem` similarly. `Combat.getHealthPercent()` replaces the manual
  widget-based HP read; `Combat.isPoisoned()` replaces the varp check.
  Filters use DreamBot `match` signature.
- `PowerSlayer.java` — extends `AbstractScript`, `@ScriptManifest` with
  `Category.SLAYER`; implements `ChatListener` (`onGameMessage`) and
  `MouseListener` (`onClick`/etc.). The nested skill enum was renamed
  `PaintSkill` to avoid collision with DreamBot's `Skill` enum and now
  holds a `Skill` reference directly. Paint uses `Skills.getPercentageToLevel`
  and `Skills.getRealLevel`.

### Remaining (in order)

All of the files below have now been ported. Retained for reference.

1. `data/Monsters.java` — large enum; only Tile import needs to change. Rename
   `import org.rsbot.script.wrappers.Tile` → `org.dreambot.api.methods.map.Tile`,
   change `org.powerbot.powerslayer.*` package paths → `org.dreambot.powerslayer.*`.
   No real logic change needed.
2. `wrappers/MonsterInfo.java` — only update package paths to
   `org.dreambot.powerslayer.*`. No DreamBot API used directly.
3. `wrappers/MonsterLocation.java` — Tile import + Skills→Skill (the
   `int[] neededSkills` array stores skill IDs; convert to `Object[]` of
   `(Skill, int)` pairs OR change wrappers to take `Skill[]`).
4. `wrappers/LocationProfile.java` — `Calculations.distanceTo(tile)` →
   `Players.getLocal().getTile().distance(tile)` (or `Calculations.distance(a,b)`).
5. `wrappers/Requirements.java` — package + Quest type swap (use DreamBot
   `Quest` interface). Mostly just package rename.
6. `wrappers/Task.java` — package rename only.
7. `wrappers/Starter.java` — package rename, `Inventory.getItemAt(i)` →
   `Inventory.getItemInSlot(i)`, `currItem.click(true)` →
   `currItem.interact("Use")` (DreamBot Item has `interact(action)`),
   `Camera.turnTo(Monster)` → `Camera.rotateToEntity(Monster)`.
8. `wrappers/Finisher.java` — same pattern as Starter.
9. `methods/CombatStyle.java` — pure logic; package rename only.
10. `methods/Banking.java` — `Bank.getItems()` works; `Bank.withdraw(id, amt)`.
11. `methods/Traveling.java` — drop custom `Banks`; replace with
    `BankLocation.values()` + `loc.getCenter().distance(player)`. Use
    `Walking.walk(Tile)`.
12. `methods/SlayerInventory.java` — `Inventory.count`, `Inventory.contains`,
    `Inventory.all()`. `Item.interact(action)`.
13. `methods/SlayerEquip.java` — `EquipmentSlot`, `Equipment.getItemInSlot`,
    `Inventory.interact(id, "Wield")` to equip.
14. `methods/SlayerMasters.java` — `NPCs.closest(name)`, `Skills.getRealLevel`,
    `Players.getLocal().getCombatLevel()`, `Widgets.getChildWidget(64,4)`,
    `Dialogues.canContinue`/`continueDialogue`.
15. `methods/UniversalFighter.java` — biggest port. Replace all RSBot calls.
    `Filter<NPC>` with `match(NPC n)` (not `accept`). Mouse moves via
    `org.dreambot.api.input.Mouse`. `Combat.getSpecialPercentage`. Inventory
    iteration via `Inventory.all()`. `PlayerSettings.getConfig(102)` for poison
    (or `Combat.isPoisoned()` — prefer that). Widget poison icon at 748,4 still
    valid in OSRS but better to call `Combat.isPoisoned()`.
16. `states/GoToMonsterState.java`, `GoToBankState.java`, `GoToMasterState.java`,
    `GetTaskState.java`, `BankingState.java` — package renames; bodies are stubs.
17. `states/FighterState.java` — bigger one. `Walking.isRunEnabled`/`toggleRun`,
    `Dialogues.canContinue`/`continueDialogue`, `Tabs.getOpen()`/`Tabs.open(Tab.INVENTORY)`,
    `Prayers.isQuickPrayersActive`/`toggleQuickPrayer`, `PlayerSettings.getConfig(394)`
    (the slayer task remaining varp — verify in OSRS), `NPCs.closest(names)`.
18. `PowerSlayer.java` — extends `AbstractScript`. `@ScriptManifest(name=...,
    author=..., version=1.0, category=Category.SLAYER, description=...)`.
    `onStart` instead of `onRun`, `onLoop` instead of `loop`, `onPaint` for paint.
    For chat events implement `org.dreambot.api.script.listener.ChatListener`
    (`onGameMessage(Message)`). For mouse events implement
    `org.dreambot.api.script.listener.MouseListener` (or use `onPaint`-only).
    Skill enum inside the paint section: rename `CONSTITUTION`→`HITPOINTS`,
    `DEFENSE`→`DEFENCE`, `RANGE`→`RANGED`. Use `Skills.getPercentageToLevel(Skill)`
    and `Skills.getRealLevel(Skill)`.

## Notes for the implementer

- DreamBot `Inventory.get(idx)` returns the slot at index; some older snippets
  show `getItemAt`/`getItem` — current API is `get`.
- DreamBot `NPC.getModelTriangles()` may not exist — prefer
  `npc.getModel().getTriangles()` or just use `npc.interact(action)` and skip
  manual point-on-model logic. Recommend simplifying `clickNPC` in
  UniversalFighter to just `npc.interact("Attack " + npc.getName())` rather
  than porting the manual triangle-walking. Original code only did this for
  reliability tweaks; DreamBot's `interact` already handles camera + walking.
- Eating: `Inventory.all(item -> item.hasAction("Eat"))` is the cleaner port for
  the food finder.
- `getHPPercent` from interface 748 child 8 is RS3-style. In OSRS use
  `Combat.getHealthPercent()` directly.
- The `Prayer` import in original code refers to the prayer tab; in DreamBot
  it's `Prayers` (class) and `Prayer` (enum). Original `Prayer.getRemainingPoints()`
  → `Prayers.getPoints()`.
- For a clean compile, we may want a small `SkillReq` POJO instead of `Object[]`
  in SlayerItems. Left as `Object[]` for now to mirror the original layout.

## Resume instructions for next session

1. `git checkout claude/powerbot-to-dreambot-conversion-t7sn8`
2. Read this file end-to-end.
3. Read the already-converted files under `src/org/dreambot/powerslayer/` to
   confirm the patterns.
4. Work through the "Remaining" list in order. After each file, run a quick
   sanity grep for any leftover `org.rsbot` or `org.powerbot` imports inside
   `src/org/dreambot/`.
5. Once all files are written, the `src/org/powerbot/` tree can stay in place
   as a reference until the user confirms; do NOT delete it without asking.
6. Final sanity check: `grep -r "org.rsbot\|org.powerbot" src/org/dreambot/`
   should return nothing.
