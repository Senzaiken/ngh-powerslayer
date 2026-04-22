package org.dreambot.powerslayer.methods;

import org.dreambot.powerslayer.common.DMethodProvider;
import org.dreambot.powerslayer.common.MethodBase;
import org.dreambot.powerslayer.data.SlayerMaster;
import org.dreambot.powerslayer.wrappers.Task;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Player;

//TODO: Convert to QS Once it comes out
public class Traveling extends DMethodProvider {
    public Traveling(MethodBase methods) {
        super(methods);
    }

    public boolean travelToMaster(SlayerMaster master) {
        return travelTo(master.getLocation());
    }

    public boolean travelToSlayerLocation(Task task) {
	    return travelTo(task.getMonster().getLocationProfile().getBestLocation().getSlayerLocation().getTile());
    }

    // The default will be the closest bank to the player
    public boolean travelToBank() {
        return travelToBank(getNearestBank());
    }

    public boolean travelToBank(BankLocation bank) {
        return travelTo(bank.getCenter());
    }

    public boolean travelTo(Tile t) {
        if (t == null)
            return false;
	    return Walking.walk(t);
    }

    public BankLocation getNearestBank() {
        /*
       * TODO Add a method to remove all banks that a player can not reach.
       */
        Player local = Players.getLocal();
        if (local == null)
            return null;
        Tile playerTile = local.getTile();
        BankLocation best = null;
        double bDist = Double.MAX_VALUE;
        for (BankLocation b : BankLocation.values()) {
            Tile center = b.getCenter();
            if (center == null)
                continue;
            double dist = center.distance(playerTile);
            if (dist < bDist) {
                best = b;
                bDist = dist;
            }
        }
        return best;
    }
}
