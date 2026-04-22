package org.dreambot.powerslayer.wrappers;

import org.dreambot.powerslayer.PowerSlayer;
import org.dreambot.powerslayer.data.SlayerItems.SlayerEquipment;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;

public class Starter {
	//TODO: Add methods
	SlayerEquipment starter;
	int amount = 1;


    public Starter (SlayerEquipment equipment, int Amount) {
    	starter = equipment;
    	amount = Amount;
    }

    public Starter(SlayerEquipment equipment) {
        this (equipment, 1);
    }

	public boolean availableAtMaster() {
		return starter.availableAtMaster();
	}

	public boolean canEquip() {
		return starter.isEquipable();
	}

	public EquipmentSlot equipSlot() {
		return starter.equipSlot();
	}

    public int getAmount() {
		return amount;
	}

	public int getCost() {
		return starter.getCost();
	}

	public int[] getIDs() {
		return starter.getIDs();
	}

	public String getName() {
		return starter.getName();
	}

    public SlayerEquipment getSlayerEquipment() {
    	return starter;
    }

	public boolean isUsable() {
		return starter.isUsable();
	}

	public static boolean use (NPC Monster) {
		String starterName = PowerSlayer.currentTask.getRequirements().getStarter().getName();
		if (Monster == null || Monster.isDead())
			return false;
		for (int i = 0; i < 28; i++) {
			Item currItem = Inventory.getItemInSlot(i);
			if (currItem == null)
				continue;
			if (currItem.getName().equals(starterName)) {
				currItem.interact("Use");
				if (!Monster.isOnScreen())
					Camera.rotateToEntity(Monster);
				if (Monster.isOnScreen())
					return Monster.interact("Use");
				return false;
			}
		}
		return false;
	}
}
