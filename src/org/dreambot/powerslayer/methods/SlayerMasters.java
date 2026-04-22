package org.dreambot.powerslayer.methods;

import org.dreambot.powerslayer.common.DMethodProvider;
import org.dreambot.powerslayer.common.MethodBase;
import org.dreambot.powerslayer.data.Monsters;
import org.dreambot.powerslayer.data.Monsters.Monster;
import org.dreambot.powerslayer.data.SlayerMaster;
import org.dreambot.powerslayer.wrappers.Task;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.ArrayList;

//TODO: Peer review code
public class SlayerMasters extends DMethodProvider {
	public SlayerMasters(MethodBase methods) {
		super(methods);
	}

	public SlayerMaster getBestSlayerMaster() {
		ArrayList<SlayerMaster> possibleMasters = new ArrayList<SlayerMaster>();
		for(SlayerMaster master : SlayerMaster.values()) {
			if(master.getSlayerLevel() < Skills.getRealLevel(Skill.SLAYER) &&
					master.getCombatLevel() < Players.getLocal().getLevel()) {
				possibleMasters.add(master);
			}
		}
		if(possibleMasters.size() < 1) {
			return null;
		}
		if(possibleMasters.size() == 1) {
			return possibleMasters.get(0);
		}
		if(possibleMasters.size() > 1) {
			SlayerMaster best = null;
			for(SlayerMaster master : possibleMasters) {
				if(best == null || master.getCombatLevel() > best.getCombatLevel()) {
					best = master;
				}
			}

			for(SlayerMaster master : possibleMasters) {
				if(best == null || master.getSlayerLevel() > best.getSlayerLevel()) {
					best = master;
				}
			}
			return best;
		}
		return null;
	}

	public Task getTaskFromMaster(SlayerMaster master) {
		NPC npc = getMasterNPC(master);
		if(npc != null) {
			if(npc.interact("Get-Task")) {
				long time = System.currentTimeMillis();
				while((getDialogueChild() == null ||
						!getDialogueText().equals("I need another assignment")) &&
						System.currentTimeMillis() - time < 10000) {
					sleep(random(50, 80));
				}

				if(Dialogues.canContinue()) {
					Dialogues.continueDialogue();
				}

				time = System.currentTimeMillis();
				while((getDialogueChild() == null ||
						!getDialogueText().contains("Your new task is to kill")) &&
						System.currentTimeMillis() - time < 10000) {
					sleep(random(50, 80));
				}
				//make sure we have the right page instead of referring to another master
				WidgetChild taskChild = getDialogueChild();
				if(taskChild != null &&
						taskChild.getText().contains("Your new task is to kill")) {
					int amount = 0;
					Monster monster = null;
					String string = taskChild.getText();
					String subString = string.substring(string.indexOf("kill") + 5);
					if(subString.length() != 0 && subString.contains(" ")) {
						String[] words = subString.split(" ");
						if(words.length != 0) {
							amount = Integer.parseInt(words[0]);
							for(Monster mon : Monster.values()) {
								for(String name : mon.getNames()) {
									if(words[1].equals(name)) {
										monster = mon;
										break;
									}
								}
								if(monster != null)
									break;
							}
							if(monster == null) {
								for(Monsters.MonsterGroup mg : Monsters.MonsterGroup.values()) {
									if(mg.toString().equals(words[1])) {
										monster = mg.getBestMonster();
									}
								}
							}
						}
					}
					return new Task(monster, amount, master);
				}
			}
		}
		return null;
	}

	public NPC getMasterNPC(SlayerMaster master) {
		for(String name : master.getNames()) {
			NPC found = NPCs.closest(name);
			if(found != null) {
				return found;
			}
		}
		return null;
	}

	private static WidgetChild getDialogueChild() {
		return Widgets.getChildWidget(64, 4);
	}

	private static String getDialogueText() {
		WidgetChild c = getDialogueChild();
		return c == null ? "" : c.getText();
	}
}
