package org.dreambot.powerslayer.data;

import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;

public enum SlayerMaster {
    // TODO Add other slayer masters + locations
    TURAEL (new String[]{"Turael", "Spria"}, new Tile(0, 0, 0), 3),
    MAZCHNA (new String[]{"Mazchna", "Achtryn"}, new Tile(3510, 3509, 0), 20),
    VANNAKA (new String[]{"Vannaka"}, new Tile(3146, 9914, 0), 40),
    CHAELDAR (new String[]{"Chaeldar"}, new Tile(0, 0), 70),
    SUMONA (new String[]{"Sumona"}, new Tile(0, 0), 85, 35),
    DURADEL (new String[]{"Duradel", "Lapalok"}, new Tile(0, 0), 100, 50),
    KURADEL (new String[]{"Kuradel"}, new Tile(0, 0), 110, 75);

    private Tile location;
    private String[] name;
    private int combatLevel;
    private int slayerLevel;

    SlayerMaster(String[] Name, Tile Location, int CombatLevel) {
        this(Name, Location, CombatLevel, 0);
    }

    SlayerMaster(String[] Name, Tile Location, int CombatLevel,
                 int SlayerLevel) {
        name = Name;
        location = Location;
        combatLevel = CombatLevel;
        slayerLevel = SlayerLevel;
    }

    //FIXME: Add in combat level checker
    public boolean canUse() {
        return Skills.getRealLevel(Skill.SLAYER) >= slayerLevel;
    }

    public int getCombatLevel() {
        return this.combatLevel;
    }

    public Tile getLocation() {
        return this.location;
    }

    public String[] getNames() {
        return this.name;
    }

    public int getSlayerLevel() {
        return this.slayerLevel;
    }
}
