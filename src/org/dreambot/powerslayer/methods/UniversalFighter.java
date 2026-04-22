package org.dreambot.powerslayer.methods;

import java.util.ArrayList;
import java.util.HashMap;

import org.dreambot.powerslayer.PowerSlayer;
import org.dreambot.powerslayer.common.DMethodProvider;
import org.dreambot.powerslayer.common.MethodBase;
import org.dreambot.powerslayer.wrappers.Finisher;
import org.dreambot.powerslayer.wrappers.Starter;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;

//TODO: Zalgo2462 Touch up
public class UniversalFighter extends DMethodProvider {
	public UniversalFighter(MethodBase methods) {
		super(methods);
	}


	private long nextAntiban = 0;
	public SlayerNPCs npcs = new SlayerNPCs();
	public Eating eat = new Eating();
	public Potion pot = new Potion();
	public Loot loot = new Loot();
	public Tiles tiles = new Tiles();

	/**
	 * Performs a random action, always.
	 * Actions: move mouse, move mouse off screen, move camera.
	 */
	public void antiban() {
		if (System.currentTimeMillis() > nextAntiban) {
			nextAntiban = System.currentTimeMillis() + random(2000, 30000);
		} else {
			return;
		}
		Thread mouseThread = new Thread() {
			public void run() {
				switch (random(0, 5)) {
				case 0:
					Mouse.moveOffScreen();
					break;
				case 1:
					Mouse.moveOnScreen();
					break;
				case 2:
					Mouse.moveOnScreen();
					break;
				}
			}
		};
		Thread keyThread = new Thread() {
			public void run() {
				switch (random(0, 4)) {
				case 0:
					Camera.rotateToYaw(Camera.getYaw() + random(-100, 100));
					break;
				case 1:
					Camera.rotateToYaw(Camera.getYaw() + random(-100, 100));
					break;
				case 2:
					Camera.rotateToYaw(Camera.getYaw() + random(-100, 100));
					break;
				}
			}
		};
		if (random(0, 2) == 0) {
			keyThread.start();
			sleep(random(0, 600));
			mouseThread.start();
		} else {
			mouseThread.start();
			sleep(random(0, 600));
			keyThread.start();
		}
		while (keyThread.isAlive() || mouseThread.isAlive())
			sleep(random(30, 100));
	}

	public class SlayerNPCs {

		private String[] npcNames = PowerSlayer.currentTask.getMonster().getNames();

		public NPC lastClickedNPC = null;
		public boolean npcWasClickedLast = false;

		private String weapon = "";
		private boolean hasSpecialWeapon = false;


		public ArrayList<Tile> tilesFoughtOn = new ArrayList<Tile>();

		/**
		 * Checks if we are in combat.
		 *
		 * @return True if we are in combat.
		 */
		public boolean isInCombat() {
			Character inter = Players.getLocal().getInteractingCharacter();
			return inter instanceof NPC;
		}

		public boolean useSpecial() {
			if (hasSpecialWeapon) {
				int[] amountUsage = {10, 25, 33, 35, 45, 50, 55, 60, 80, 85, 100};
				String[][] weapons = {
						{"Rune thrownaxe", "Rod of ivandis"},
						{"Dragon Dagger", "Dragon dagger (p)", "Dragon dagger (p+)",
							"Dragon dagger (p++)", "Dragon Mace", "Dragon Spear",
							"Dragon longsword", "Rune claws"},
							{"Dragon Halberd"},
							{"Magic Longbow"},
							{"Magic Composite Bow"},
							{"Dragon Claws", "Abyssal Whip", "Granite Maul", "Darklight",
								"Barrelchest Anchor", "Armadyl Godsword"},
								{"Magic Shortbow"},
								{"Dragon Scimitar", "Dragon 2H Sword", "Zamorak Godsword",
								"Korasi's sword"},
								{"Dorgeshuun Crossbow", "Bone Dagger", "Bone Dagger (p+)",
								"Bone Dagger (p++)"},
								{"Brine Sabre"},
								{"Bandos Godsword", "Dragon Battleaxe", "Dragon Hatchet",
									"Seercull Bow", "Excalibur", "Enhanced excalibur",
									"Ancient Mace", "Saradomin sword"}};

				for (int i = 0; i < weapons.length; i++) {
					for (int j = 0; j < weapons[i].length; j++) {
						if (weapons[i][j].equalsIgnoreCase(weapon)) {
							return Combat.getSpecialPercentage() >= amountUsage[i];
						}
					}
				}
			}
			return false;
		}

		/**
		 * Clicks an NPC using DreamBot's built-in interaction, which handles
		 * walking and camera rotation automatically.
		 *
		 * @param npc    The NPC to click.
		 * @param action The action to perform.
		 * @return 0 if the NPC was interacted with, -1 if nothing happened.
		 */
		public int clickNPC(NPC npc, String action) {
			if (npc == null)
				return -1;
			if (npc.interact(action)) {
				loot.itemWasClickedLast = false;
				npcWasClickedLast = true;
				lastClickedNPC = npc;
				return 0;
			}
			return -1;
		}

		/**
		 * Returns the nearest NPC.
		 *
		 * @return The nearest NPC that matches the filter.
		 */
		public NPC getNPC() {
			NPC onScreen = NPCs.closest(npcOnScreenFilter);
			if (onScreen != null)
				return onScreen;
			return NPCs.closest(npcFilter);
		}

		/**
		 * Returns the interacting NPC that matches our description, if any.
		 *
		 * @return The closest interacting NPC that matches the filter.
		 */
		public NPC getInteracting() {
			NPC npc = null;
			double dist = 20;
			Player me = Players.getLocal();
			for (NPC n : NPCs.all()) {
				if (n == null || !isOurNPC(n))
					continue;
				Character inter = n.getInteractingCharacter();
				if (inter instanceof Player && inter.equals(me) && me.getTile().distance(n.getTile()) < dist) {
					dist = me.getTile().distance(n.getTile());
					npc = n;
				}
			}
			return npc;
		}

		private boolean isOurNPC(NPC t) {
			String name = t.getName();
			if (name == null)
				return false;
			for (String s : npcNames) {
				if (name.equalsIgnoreCase(s))
					return true;
			}
			return false;
		}

		public boolean useStarter(NPC monster) {
			return Starter.use(monster);
		}

		public boolean useFinisher(NPC monster) {
			return Finisher.use(monster);
		}

		/**
		 * Waits until the last clicked NPC dies or time runs out... whichever comes first
		 * @param threshold sleep threshold
		 * @return true if last clicked NPC died within threshold, else false
		 */
		public boolean sleepWhileNpcIsDying(int threshold) {
			final NPC currNPC = npcs.lastClickedNPC;
			if (currNPC == null || currNPC.isDead())
				return false;
			final Tile npcTile = currNPC.getTile();
			Filter<NPC> monsterFilter = new Filter<NPC>() {
				public boolean match(NPC n) {
					return n != null && n.getTile().equals(npcTile);
				}
			};
			for (int i = 0; i < ((threshold/50) + 1); i++) {
				java.util.List<NPC> NPCList = NPCs.all(monsterFilter);
				if (NPCList.isEmpty())
					break;
				if (i == threshold/50)
					return false;
				sleep (50);
			}
			return true;
		}

		/**
		 * The filter we use!
		 */
		private final Filter<NPC> npcFilter = new Filter<NPC>() {
			public boolean match(NPC t) {
				return (t != null && isOurNPC(t) && t.exists() && !t.isInCombat() && t.getInteractingCharacter() == null &&
						t.getHealthPercent() != 0 && !tiles.NPCisOnBadTile(t));
			}
		};

		/**
		 * Will only return an on screen NPC. Based on npcFilter.
		 */
		private final Filter<NPC> npcOnScreenFilter = new Filter<NPC>() {
			public boolean match(NPC n) {
				return npcFilter.match(n) && n.isOnScreen();
			}
		};
	}

	public class Eating {

		private final int B2P_ID = 8015;
		private final int[] BONES_ID = new int[]{526, 532, 530, 528, 3183, 2859};

		/**
		 * Returns a random integer of when to eat.
		 *
		 * @return A random integer of the percent to eat at.
		 */
		private int getRandomEatPercent() {
			return random(45, 60);
		}

		/**
		 * Checks if we have at least one B2P tab.
		 *
		 * @return True if we have a tab.
		 */
		public boolean haveB2pTab() {
			return Inventory.count(B2P_ID) > 0;
		}

		/**
		 * Breaks a B2P tab.
		 */
		public void breakB2pTab() {
			Item i = Inventory.get(B2P_ID);
			if (i != null)
				i.interact();
		}

		/**
		 * Checks if the inventory contains bones, for B2P.
		 *
		 * @return True if we have bones.
		 */
		public boolean haveBones() {
			for (int id : BONES_ID) {
				if (Inventory.count(id) > 0)
					return true;
			}
			return false;
		}

		/**
		 * Checks if we have food.
		 *
		 * @return True if we have food.
		 */
		public boolean haveFood() {
			return getFood() != null;
		}

		/**
		 * Finds food based on inventory actions.
		 *
		 * @return The food, or null if none was found.
		 */
		private Item getFood() {
			for (Item i : Inventory.all()) {
				if (i == null || i.getID() == -1)
					continue;
				if (i.hasAction("Eat"))
					return i;
			}
			return null;
		}

		/**
		 * Attempts to eat food.
		 *
		 * @return True if we ate.
		 */
		public boolean eatFood() {
			Item i = getFood();
			if (i == null)
				return false;
			for (int j = 0; j < 3; j++) {
				if (i.interact("Eat"))
					return true;
			}
			return false;
		}

		/**
		 * Checks whether you need to eat or not.
		 *
		 * @return True if we need to eat.
		 */
		public boolean needEat() {
			return getHPPercent() <= getRandomEatPercent();
		}

		/**
		 * Returns an integer representing the current health percentage.
		 *
		 * @return The current health percentage.
		 */
		public int getHPPercent() {
			return Combat.getHealthPercent();
		}
	}

	public class Potion {

		private final int[] MAGIC_POTIONS = new int[] {3040, 3042, 3044, 3046, 11513, 11515, 13520, 13521, 13522, 13523};

		private final int[] PRAYER_POTIONS = new int[] {2434, 139, 141, 143, 11465, 11467};

		private final int[] RANGE_POTIONS = new int[] {2444, 169, 171, 173, 11509, 11511, 13524, 13525, 15326, 15327};

		private final int[] ENERGY_POTIONS = new int[] {3008, 3010, 3012, 3014, 3016, 3018, 3020, 3022, 11453, 11455,
				11481, 11483};

		private final int[] COMBAT_POTIONS = new int[] {9739, 9741, 9743, 9745, 11445, 11447};

		private final int[] ATTACK_POTIONS = new int[] {2428, 121, 123, 125, 2436, 145, 147, 149, 11429, 11431,
				11429, 11431, 11429, 11431, 11469, 11471, 15308	, 15309, 15310, 15311};

		private final int[] STRENGTH_POTIONS = new int[] {113, 115, 117, 119, 2440, 157, 159, 161, 11443, 11441,
				11485, 11487, 15312, 15313, 15314, 15315};

		private final int[] DEFENSE_POTIONS = new int[] {2432, 133, 135, 137, 2442, 163, 165, 167, 11457, 11459,
				11497, 11499, 15316, 15317, 15318, 15319};

		private final int[] ANTIPOISON = new int[] {2446, 175, 177, 179, 2448, 181, 183, 185, 5952, 5954,
				5956, 5958, 5943, 5945, 5947, 5949, 11433, 11435, 11501, 11503};

		private final int[] ZAMORAK_POTIONS = new int[] {2450, 189, 191, 193, 11521, 11523};

		private final int[] SARADOMIN_POTIONS = new int[] {6685, 6687, 6689, 6691};

		private final int[] OVERLOAD_POTIONS = new int[] {15332, 15333, 15334, 15335};

		private final int[] VIAL = new int[] {229};

		public boolean setQuickPrayer = true;

		public HashMap<String, Item[]> getPotions() {
			HashMap<String, Item[]> potions = new HashMap<String, Item[]>();

			potions.put("MAGIC", getRealItems(MAGIC_POTIONS));

			potions.put("PRAYER", getRealItems(PRAYER_POTIONS));

			potions.put("RANGE", getRealItems(RANGE_POTIONS));

			potions.put("ENERGY", getRealItems(ENERGY_POTIONS));

			potions.put("COMBAT", getRealItems(COMBAT_POTIONS));

			potions.put("ATTACK", getRealItems(ATTACK_POTIONS));

			potions.put("STRENGTH", getRealItems(STRENGTH_POTIONS));

			potions.put("DEFENSE", getRealItems(DEFENSE_POTIONS));

			potions.put("ANTIPOISON", getRealItems(ANTIPOISON));

			potions.put("ZAMORAK", getRealItems(ZAMORAK_POTIONS));

			potions.put("SARADOMIN", getRealItems(SARADOMIN_POTIONS));

			potions.put("OVERLOAD", getRealItems(OVERLOAD_POTIONS));

			return potions;
		}

		public void usePotions() {
			HashMap<String, Item[]> potions = getPotions();

			Item[] vials = getRealItems(VIAL);
			if (vials.length != 0) {
				for (Item i : vials) {
					int n = Inventory.fullSlotCount();
					i.interact("Drop Vial");
					SlayerInventory.waitForInvChange(n, 2000);
				}
			}

			if (!(statIsBoosted(Skill.MAGIC)) && (potions.get("MAGIC").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("MAGIC").length != 0) {
					potions.get("MAGIC")[0].interact();
				}
				else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].interact();
				}
			}

			if (shouldUsePrayerPot() && potions.get("PRAYER").length != 0 && setQuickPrayer) {
				int current = Prayers.getPoints();
				if (potions.get("PRAYER")[0].interact()) {
					long time = System.currentTimeMillis();
					while(Prayers.getPoints() == current && System.currentTimeMillis() - time < 10000) {
						sleep(random(200, 500));
					}
				}
			}

			if (!(statIsBoosted(Skill.RANGED)) && (potions.get("RANGE").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("RANGE").length != 0) {
					potions.get("RANGE")[0].interact();
				}
				else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].interact();
				}
			}

			if (Walking.getRunEnergy() < random(40, 70) && potions.get("ENERGY").length != 0) {
				potions.get("ENERGY")[0].interact();
			}

			if (!(statIsBoosted(Skill.STRENGTH)) && (potions.get("STRENGTH").length != 0 || potions.get("COMBAT").length != 0 || potions.get("ZAMORAK").length != 0  || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("COMBAT").length != 0) {
					potions.get("COMBAT")[0].interact();
				}
				else if (potions.get("STRENGTH").length != 0) {
					potions.get("STRENGTH")[0].interact();
				}
				else if (potions.get("ZAMORAK").length != 0) {
					potions.get("ZAMORAK")[0].interact();
				}
				else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].interact();
				}
			}

			if (!(statIsBoosted(Skill.DEFENCE)) && (potions.get("DEFENSE").length != 0 || potions.get("SARADOMIN").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("DEFENSE").length != 0) {
					potions.get("DEFENSE")[0].interact();
				}
				else if (potions.get("SARADOMIN").length != 0) {
					potions.get("SARADOMIN")[0].interact();
				}
				else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].interact();
				}
			}

			if (!(statIsBoosted(Skill.ATTACK)) && (potions.get("ATTACK").length != 0 || potions.get("COMBAT").length != 0 || potions.get("ZAMORAK").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("COMBAT").length != 0) {
					potions.get("COMBAT")[0].interact();
				}
				else if (potions.get("ATTACK").length != 0) {
					potions.get("ATTACK")[0].interact();
				}
				else if (potions.get("ZAMORAK").length != 0) {
					potions.get("ZAMORAK")[0].interact();
				}
				else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].interact();
				}
			}

			if (isPoisoned() && potions.get("ANTIPOISON").length != 0) {
				potions.get("ANTIPOISON")[0].interact();
			}
		}

		private Item[] getRealItems(int[] ids) {
			ArrayList<Item> refined = new ArrayList<Item>();
			for (Item item : Inventory.all()) {
				if (item == null || item.getID() == -1)
					continue;
				for (int id : ids) {
					if (item.getID() == id) {
						refined.add(item);
						break;
					}
				}
			}
			return refined.toArray(new Item[refined.size()]);
		}

		private boolean shouldUsePrayerPot() {
			return (Skills.getRealLevel(Skill.PRAYER) - Prayers.getPoints()) > (7+Math.floor(Skills.getRealLevel(Skill.PRAYER)/4));
		}

		private boolean statIsBoosted(Skill s) {
			return Skills.getBoostedLevel(s) != Skills.getRealLevel(s);
		}
	}

	public class Loot {

		private String[] lootNames = new String[0];

		public GroundItem lastClickedItem = null;
		public boolean itemWasClickedLast = false;

		public boolean onlyTakeLootFromKilled = false;

		/**
		 * Gets the nearest loot, based on the filter
		 *
		 * @return The nearest item to loot, or null if none.
		 */
		public GroundItem getLoot() {
			return GroundItems.closest(lootFilter);
		}

		/**
		 * Attempts to take an item.
		 *
		 * @param item The item to take.
		 * @return -1 if error, 0 if taken, 1 if walked
		 */
		public int takeItem(GroundItem item) {
			if (item == null)
				return -1;
			String action = "Take " + item.getName();
			if (item.isOnScreen()) {
				if (item.interact(action)) {
					itemWasClickedLast = true;
					npcs.npcWasClickedLast = false;
					lastClickedItem = item;
					return 0;
				}
				return -1;
			} else {
				Walking.walk(item.getTile());
				sleep(random(1500, 2000));
				if (!Players.getLocal().isMoving()) {
					tiles.addBadTile(item.getTile());
					return -1;
				}
				return 1;
			}
		}

		private final Filter<GroundItem> lootFilter = new Filter<GroundItem>() {
			public boolean match(GroundItem t) {
				if (t == null)
					return false;
				//Skip if we can't hold it
				Item i;
				if (Inventory.isFull() && ((i = Inventory.get(t.getID())) == null || i.getAmount() <= 1)) {
					return false;
				}
				//Skip if its out of radius or far away
				if (Players.getLocal().getTile().distance(t.getTile()) > 25) {
					return false;
				}
				//Check ID/getName
				boolean good = false;
				String name = t.getName();
				for (String s : lootNames) {
					if (name != null && name.toLowerCase().contains(s.toLowerCase()))
						good = true;
				}

				if (good) {
					for (Tile badTile : tiles.badTiles) {
						if (t.getTile().getX() == badTile.getX() && t.getTile().getY() == badTile.getY() ) {
							good = false;
							break;
						}
					}
				}
				if (good && onlyTakeLootFromKilled) {
					if (!npcs.tilesFoughtOn.isEmpty()) {
						for (Tile tileFoughtOn : npcs.tilesFoughtOn) {
							if (t.getTile().getX() == tileFoughtOn.getX() && t.getTile().getY() == tileFoughtOn.getY()) {
								return true;
							} else {
								good = false;
							}
						}
					} else {
						good = false;
					}
				}
				return good;
			}
		};

	}

	public class Tiles {
		ArrayList<Tile> badTiles = new ArrayList<Tile>();
		int threshold = 5;

		public Tile getNearestTile(Tile[] tiles) {
			Tile closest = null;
			Tile me = Players.getLocal().getTile();
			for (Tile t : tiles) {
				if (closest == null || me.distance(t) < me.distance(closest))
					closest = t;
			}
			return closest;
		}

		public void addBadTile(Tile tile) {
			addBadTile(tile, threshold);
		}

		public void addBadTile(Tile tile, int thres) {
			if (thres > -1) {
				if (badTiles.size() > 0) {

					ArrayList<Tile> tilesWithinRadius = new ArrayList<Tile>();

					for (Tile badTile : badTiles) {
						if (badTile.distance(tile) < thres) {
							tilesWithinRadius.add(badTile);
						}
					}

					if (tilesWithinRadius.size() > 1) {
						tilesWithinRadius.add(tile);
						Area temp = new Area(tilesWithinRadius.toArray(new Tile[tilesWithinRadius.size()]));
						Tile[] areaTiles = temp.getAllTiles();
						for (Tile tileToAdd : areaTiles) {
							if (!badTiles.contains(tileToAdd)) {
								badTiles.add(tileToAdd);
							}
						}
						if (!badTiles.contains(tile)) {
							badTiles.add(tile);
						}

					} else {
						if (!badTiles.contains(tile)) {
							badTiles.add(tile);
						}
					}
				} else {
					badTiles.add(tile);
				}
			}
		}

		private boolean NPCisOnBadTile(NPC t) {
			for (Tile badTile: badTiles) {
				if (t.getTile().getX() == badTile.getX() &&
						t.getTile().getY() == badTile.getY() ) {
					return true;
				}
			}
			return false;
		}


	}

	private boolean isPoisoned() {
		return Combat.isPoisoned();
	}
}
