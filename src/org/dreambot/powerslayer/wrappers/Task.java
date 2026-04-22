package org.dreambot.powerslayer.wrappers;

import org.dreambot.powerslayer.data.Monsters.Monster;
import org.dreambot.powerslayer.data.SlayerMaster;

public class Task {
    private Monster monster;
	private int amount;
	private SlayerMaster master;

    public Task(Monster monster, int amount, SlayerMaster master) {
        this.monster = monster;
	    this.amount = amount;
	    this.master = master;
    }

    public int getAmount() {
    	return amount;
    }

    public SlayerMaster getMaster() {
    	return master;
    }

    public Monster getMonster() {
        return monster;
    }

    public Requirements getRequirements() {
	    //TODO: Convert over to both locationProfile and monster requirements
        return monster.getRequirements();
    }
}
