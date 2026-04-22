package org.dreambot.powerslayer.wrappers;

import org.dreambot.powerslayer.PowerSlayer;
import org.dreambot.powerslayer.data.SlayerItems.SlayerEquipment;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;

public class Finisher {
	//TODO: Add methods :D
	SlayerEquipment finisher;
	int amount = 1;

    public Finisher(SlayerEquipment equipment, int Amount) {
        finisher = equipment;
        amount = Amount;
    }

    public Finisher(SlayerEquipment equipment) {
        this (equipment, 1);
    }

	public boolean availableAtMaster() {
		return finisher.availableAtMaster();
	}

	public boolean canEquip() {
		return finisher.isEquipable();
	}

	public EquipmentSlot equipSlot() {
		return finisher.equipSlot();
	}

	public int getAmount() {
		return amount;
	}

	public int getCost() {
		return finisher.getCost();
	}

	public int[] getIDs() {
		return finisher.getIDs();
	}

	public String getName() {
		return finisher.getName();
	}

	public SlayerEquipment getSlayerEquipment() {
		return finisher;
	}

	public boolean isUsable() {
		return finisher.isUsable();
	}

	public static boolean use (NPC Monster) {
		String finisherName = PowerSlayer.currentTask.getRequirements().getFinisher().getName();
		if (Monster == null || Monster.isDead())
			return false;
		for (int i = 0; i < 28; i++) {
			Item currItem = Inventory.getItemInSlot(i);
			if (currItem == null)
				continue;
			if (currItem.getName().equals(finisherName)) {
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
