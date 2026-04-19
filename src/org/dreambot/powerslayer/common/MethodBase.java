package org.dreambot.powerslayer.common;

import org.dreambot.powerslayer.PowerSlayer;
import org.dreambot.powerslayer.methods.SlayerMasters;
import org.dreambot.powerslayer.methods.Traveling;
import org.dreambot.powerslayer.methods.UniversalFighter;

import java.util.Random;

public class MethodBase {
    public PowerSlayer parent = null;

    public SlayerMasters masters = new SlayerMasters(this);
    public Traveling travel = new Traveling(this);
    public UniversalFighter fighter = new UniversalFighter(this);

    public Random random = new Random();

    public MethodBase(PowerSlayer parent) {
        this.parent = parent;
    }
}
