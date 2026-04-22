package org.dreambot.powerslayer.common;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.methods.map.Tile;

import java.awt.Color;

public abstract class DMethodProvider {
    public MethodBase methods;

    public DMethodProvider(MethodBase methods) {
        this.methods = methods;
    }

    public Player getMyPlayer() {
        return Players.getLocal();
    }

    /**
     * Returns a random integer with min as the inclusive lower bound and max as
     * the exclusive upper bound.
     */
    public int random(int min, int max) {
        int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : methods.random.nextInt(n));
    }

    /**
     * Returns a random double with min as the inclusive lower bound and max as
     * the exclusive upper bound.
     */
    public double random(double min, double max) {
        return Math.min(min, max) + methods.random.nextDouble()
                * Math.abs(max - min);
    }

    public boolean verify(NPC npc) {
        return npc != null;
    }

    public boolean verify(GameObject o) {
        return o != null;
    }

    public boolean verify(Tile t) {
        return t != null;
    }

    public boolean verify(GroundItem i) {
        return i != null;
    }

    /**
     * Pauses execution for a random amount of time between two values.
     */
    public void sleep(int minSleep, int maxSleep) {
        sleep(random(minSleep, maxSleep));
    }

    /**
     * Pauses execution for a given number of milliseconds.
     */
    public void sleep(int toSleep) {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(toSleep);

            // Guarantee minimum sleep
            long now;
            while (start + toSleep > (now = System.currentTimeMillis())) {
                Thread.sleep(start + toSleep - now);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void log(Object message) {
        Logger.log(message);
    }

    public void log(Color color, Object message) {
        Logger.log(String.valueOf(message), color);
    }
}
