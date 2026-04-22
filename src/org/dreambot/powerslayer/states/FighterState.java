package org.dreambot.powerslayer.states;

import org.dreambot.powerslayer.PowerSlayer;
import org.dreambot.powerslayer.abstracts.State;
import org.dreambot.powerslayer.common.MethodBase;
import org.dreambot.powerslayer.methods.SlayerInventory;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

//TODO: Zalgo2462 Touch up State
public class FighterState extends State {

    public FighterState(MethodBase methods) {
        super(methods);
    }

    private int badFoodCount = 0;
    private LoopAction[] loopActions = new LoopAction[]{new InCombatLoop(), new AttackLoop()};
    private boolean killCondition = false;
    @Override
    public int loop() {
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > random(60, 90)) {
            methods.parent.paint.Current = "Setting Run";
            for (int i = 0; i < 5 && !Walking.isRunEnabled(); i++)
            	Walking.toggleRun();
        }

	    //TODO: Zalgo implement explorers ring
        if (Dialogues.canContinue()) {
            methods.parent.paint.Current = "Clicking Continue";
            for (int i = 0; i < 5 && Dialogues.canContinue(); i++)
            	Dialogues.continueDialogue();
        }
        if (Tabs.getOpen() != Tab.INVENTORY) {
            methods.parent.paint.Current = "Opening Inventory";
            for (int i = 0; i < 5 && Tabs.getOpen() != Tab.INVENTORY; i++) {
            	Tabs.open(Tab.INVENTORY);
            }
        }
        if (methods.fighter.eat.needEat()) {
            if (methods.fighter.eat.haveFood()) {
                badFoodCount = 0;
                methods.parent.paint.Current = "Eating";
                methods.fighter.eat.eatFood();
            } else if (methods.fighter.eat.haveB2pTab() && methods.fighter.eat.haveBones()) {
                methods.parent.paint.Current = "Casting B2P";
                methods.fighter.eat.breakB2pTab();
                return random(2600, 3000);
            } else {
                badFoodCount++;
                if (badFoodCount > 5) {
                    log("You ran out of food! Stopping Fighter.");
                    killCondition = true;
                }
            }
            return random(1200, 1600);
        }

        methods.fighter.pot.usePotions();

		if (methods.fighter.pot.getPotions().get("PRAYER").length != 0 && !Prayers.isQuickPrayersActive() &&  methods.fighter.pot.setQuickPrayer) {
            Prayers.toggleQuickPrayer(true);
        }

	     if (methods.fighter.loot.onlyTakeLootFromKilled && methods.fighter.npcs.lastClickedNPC != null) {
				methods.fighter.npcs.sleepWhileNpcIsDying(5000);
		 }

        for (LoopAction a : loopActions)
            if (a != null && a.activate())
                return a.loop();
        return random(50, 200);
    }

    @Override
    public boolean activeCondition() {
    	return methods.fighter.npcs.getNPC() != null && !killCondition && PlayerSettings.getConfig(394) != 0 && checkItems();
    }

    public interface LoopAction {
        public int loop();
        public boolean activate();
    }




	private class InCombatLoop implements LoopAction {

			public int loop() {
				methods.parent.paint.Current = "Fighting";

				if (methods.fighter.npcs.getInteracting() != null) {
					if (methods.fighter.npcs.getInteracting().getHealthPercent() <= 10 &&
						PowerSlayer.currentTask.getMonster().getRequirements().getFinisher() != null) {
						if (!methods.fighter.npcs.useFinisher(methods.fighter.npcs.getInteracting())) {
							log("You ran out of finishers! Stopping Fighter.");
							killCondition = true;
						}
					}

					if (methods.fighter.npcs.useSpecial() && !Combat.isSpecialAttackActive() && !methods.fighter.npcs.getInteracting().isDying()) {
						sleep(random(500, 1000));
						Combat.toggleSpecialAttack();
					}

					if (methods.fighter.loot.onlyTakeLootFromKilled) {
						if (methods.fighter.npcs.getInteracting() != null){
							if (!methods.fighter.npcs.tilesFoughtOn.contains(methods.fighter.npcs.getInteracting().getTile())
									&& !methods.fighter.npcs.getInteracting().isMoving()) {
								methods.fighter.npcs.tilesFoughtOn.add(methods.fighter.npcs.getInteracting().getTile());
							}
						}
					}
				}
				methods.fighter.antiban();
				return random(50, 200);
			}

			public boolean activate() {
				return methods.fighter.npcs.isInCombat();
			}

		}

		private class AttackLoop implements LoopAction {

			public int loop() {
				NPC inter = methods.fighter.npcs.getInteracting();
				NPC n = inter != null ? inter : methods.fighter.npcs.getNPC();
				if (n != null) {
					int result;
					if (PowerSlayer.currentTask.getRequirements().getStarter() != null) {
						if (!methods.fighter.npcs.useStarter(n)) {
							log("You ran out of starters! Stopping Fighter.");
							killCondition = true;
						}
						result = 0;
					}
					else {
						methods.parent.paint.Current = "Attacking " + n.getName();
						result = methods.fighter.npcs.clickNPC(n, "Attack " + n.getName());
					}
					if (result == 0) {
						waitWhileMoving();
						return random(300, 500);
					} else if (result == 1) {
						waitWhileMoving();
						return random(0, 200);
					}
				} else {
					String[] currMonster = PowerSlayer.currentTask.getMonster().getNames();
					NPC nearest = NPCs.closest(currMonster);
					if (nearest != null) {
						Tile currTile = nearest.getTile();
						if (Players.getLocal().getTile().distance(currTile) > 10) {
							Walking.walk(currTile);
							waitWhileMoving();
						} else {
							methods.fighter.antiban();
						}
					} else {
						methods.fighter.antiban();
					}
				}
				return random(50, 200);
			}

			public boolean activate() {
				return !methods.fighter.npcs.isInCombat();
			}

		}


	    //TODO: Find a way to get a list of loots to feed into the loot loop
		@SuppressWarnings("unused")
		private class LootLoop implements LoopAction {

			private GroundItem loot = null;

			public int loop() {
				int origCount = Inventory.fullSlotCount();
				String name = loot.getName();
				int count = loot.getItem().getAmount();
				int result = methods.fighter.loot.takeItem(loot);
				if (result == 0) {
					waitWhileMoving();
					if (SlayerInventory.waitForInvChange(origCount, 2000)) {
						if (methods.fighter.loot.onlyTakeLootFromKilled && methods.fighter.npcs.tilesFoughtOn.contains(loot.getTile())) {
							methods.fighter.npcs.tilesFoughtOn.remove(loot.getTile());
						}
					}
				} else if (result == 1) {
					waitWhileMoving();
				}
				return random(50, 200);
			}

			public boolean activate() {
				return (loot = methods.fighter.loot.getLoot()) != null;
			}
		}


    /**
     * Waits until we are no longer moving.
     */
    public void waitWhileMoving() {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 1500 && !getMyPlayer().isMoving()) {
            sleep(random(50, 200));
        }
        while (getMyPlayer().isMoving()) {
            sleep(random(20, 50));
        }
    }

    public boolean checkItems() {
        return SlayerInventory.containsAllEquipment();
    }
}
