package org.dreambot.powerslayer.methods;

import org.dreambot.powerslayer.data.SlayerItems.SlayerEquipment;
import org.dreambot.powerslayer.wrappers.Finisher;
import org.dreambot.powerslayer.wrappers.Starter;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.wrappers.items.Item;

public class Banking {

	public static boolean isInBank(SlayerEquipment equipment) {
		for (Item item : Bank.all()) {
			if (item != null && item.getName().equalsIgnoreCase(equipment.getName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInBank(Finisher fin) {
		return isInBank(fin.getSlayerEquipment());
	}

	public static boolean isInBank(Starter start) {
		return isInBank(start.getSlayerEquipment());
	}

	public static boolean withdraw (SlayerEquipment equipment) {
		for (Item currItem: Bank.all()) {
			if (currItem != null && currItem.getName().equals(equipment.getName()))
				return Bank.withdraw(currItem.getID(), equipment.getAmount());
		}
		return false;
	}

	public static boolean withdraw (Starter start) {
		return withdraw(start.getSlayerEquipment());
	}

	public static boolean withdraw (Finisher finish) {
		return withdraw(finish.getSlayerEquipment());
	}
}
