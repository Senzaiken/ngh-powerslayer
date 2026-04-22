package org.dreambot.powerslayer.methods;

import org.dreambot.powerslayer.data.SlayerItems.SlayerEquipment;
import org.dreambot.powerslayer.wrappers.Requirements;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.wrappers.items.Item;

public class SlayerEquip {


	public static boolean equip(SlayerEquipment equip) {
		if (!equip.isEquipable())
			return false;
		for (Item item : Inventory.all()) {
			if (item != null && item.getName().equalsIgnoreCase(equip.getName())) {
				return item.interact("Wield") || item.interact("Wear") || item.interact("Equip");
			}
		}
		return false;
	}

	public static boolean isFullyEquipped(Requirements req) {
		for (SlayerEquipment currEquipment : req.getEquipment()) {
			if (!isEquipped(currEquipment)) {
				if (SlayerInventory.hasEnough(currEquipment)) {
					Item replacing = getReplacingItem(currEquipment);
					if (replacing != null) {
						for (SlayerEquipment r : req.getEquipment()) {
							if (replacing.getName().equals(r.getName())) {
								return false;
							}
						}
					}
					equip(currEquipment);
					if (!isEquipped(currEquipment)) {
						return false;
					}
				}
			}
		}
		return true;
	}


	public static boolean isEquipped(SlayerEquipment item) {
		Item equipped = Equipment.getItemInSlot(item.equipSlot());
		return equipped != null && equipped.getName().equals(item.getName());
	}

	public static Item getReplacingItem(SlayerEquipment item) {
		return Equipment.getItemInSlot(item.equipSlot());
	}


}
