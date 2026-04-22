package org.dreambot.powerslayer.methods;

import org.dreambot.powerslayer.PowerSlayer;
import org.dreambot.powerslayer.data.SlayerItems.SlayerEquipment;
import org.dreambot.powerslayer.wrappers.Finisher;
import org.dreambot.powerslayer.wrappers.Requirements;
import org.dreambot.powerslayer.wrappers.Starter;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

public class SlayerInventory {

	public static boolean containsAllEquipment() {
        for (SlayerEquipment i : PowerSlayer.currentTask.getRequirements().getEquipment()) {
            if (!hasEnough(i)) {
                return false;
            }
        }
        return true;
    }

	public static boolean contains(SlayerEquipment equipment) {
		for (Item currItem: Inventory.all()) {
			if (currItem != null && currItem.getName().equalsIgnoreCase(equipment.getName()))
			   return true;
		}
		return false;
	}

	public static boolean contains(Finisher finisher) {
		return contains(finisher.getSlayerEquipment());
	}

	public static boolean contains(Starter starter) {
		return contains(starter.getSlayerEquipment());
	}

	public static boolean hasEnough(SlayerEquipment items) {
		int total = 0;
		for (int id : items.getIDs()) {
			total += Inventory.count(id);
		}
		return total >= items.getAmount();
	}

	public static boolean hasEnough(Finisher fin) {
		return hasEnough(fin.getSlayerEquipment());
	}

	public static boolean hasEnough(Starter start) {
		return hasEnough(start.getSlayerEquipment());
	}

	public static boolean inventReady(Requirements req) {
		for (SlayerEquipment i : req.getEquipment()) {
			if (!contains(i)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	private static int inventSpace() {
		return 28 - Inventory.fullSlotCount();
	}

	public static boolean performAction(SlayerEquipment items, String action) {
		for (Item item : Inventory.all()) {
			if (item != null && item.getName().equalsIgnoreCase(items.getName())) {
				return item.interact(action);
			}
		}
		return false;
	}

	public static boolean waitForInvChange(int threshold) {
		int origCount = Inventory.fullSlotCount();
		for (int i = 0; i < ((threshold/50) + 1); i++) {
			if (origCount != Inventory.fullSlotCount())
				return true;
			Sleep.sleep(50);
		}
		return false;
    }

	public static boolean waitForInvChange(int origCount, int threshold) {
		for (int i = 0; i < ((threshold/50) + 1); i++) {
			if (origCount != Inventory.fullSlotCount())
				return true;
			Sleep.sleep(50);
		}
		return false;
    }
}
