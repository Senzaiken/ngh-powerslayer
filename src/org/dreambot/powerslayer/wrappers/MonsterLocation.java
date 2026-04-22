package org.dreambot.powerslayer.wrappers;

import org.dreambot.powerslayer.data.SlayerItems.SlayerEquipment;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;

public class MonsterLocation {

	SlayerLocation monsterLocation;
	Tile monsterTile;

	public MonsterLocation (SlayerLocation MonsterLocation, Tile MonsterTile) {
		monsterLocation = MonsterLocation;
		monsterTile = MonsterTile;
	}

	public enum SlayerLocation {

		Example (new Tile(0,0), new SlayerEquipment[] {}, new Object[] {Skill.ATTACK, 0}, 0),
		Fremmenik_Slayer_Dungeon (new Tile (0, 0, 0), null, new Object[] {Skill.SLAYER, 0}, 0),
		//Need to finish Priest in Peril
		Canifis_Slayer_Tower (new Tile(0, 0, 0), null, null, 0),
		Lumbridge_Swamp_Caves (new Tile(0, 0, 0),
			new SlayerEquipment[] {SlayerEquipment.Bullseye_Lantern, SlayerEquipment.Rope, SlayerEquipment.Tinderbox}, null, 0),
		God_Wars_Dungeon (new Tile(0, 0, 0), null, null, 0),
		Desert_Slayer_Dungeon (new Tile(0, 0, 0), null, null, 0),
		Chaos_Tunnels (new Tile(0, 0, 0), null, null, 0),
		DorgeshKaan_South_Dungeon (new Tile(0, 0, 0), null, null, 0),
		Kuradels_Slayer_Dungeon (new Tile(0, 0, 0), null, null, 0);


		Tile tile = null;
		SlayerEquipment[] equipment;
		Object[] neededSkills = null;
		int neededCbLevel = 0;


		SlayerLocation(Tile Tile, SlayerEquipment[] neededEquipment, Object[] neededSkills, int neededCbLevel) {
			tile = Tile;
			equipment = neededEquipment;
			if(neededSkills != null) {
				if(neededSkills.length > 0) {
					if(neededSkills.length % 2 == 0) {
						this.neededSkills = neededSkills;
					} else {
						this.neededSkills = null;
					}
				}
			}
			this.neededCbLevel = neededCbLevel;
		}

		SlayerLocation(Tile tile, SlayerEquipment[] neededEquipment, Object[] neededSkills) {
			this(tile, neededEquipment, neededSkills, 0);
		}

		SlayerLocation (Tile tile, SlayerEquipment[] neededEquipment) {
			this(tile, neededEquipment, null);
		}

		SlayerLocation(Tile tile) {
			this(tile, null);
		}

		public Tile getTile() {
			return tile;
		}

		public SlayerEquipment[] getEquipment() {
			return equipment;
		}

		public Object[] getNeededSkills() {
			return neededSkills;
		}

		public int getNeededCbLevel() {
			return neededCbLevel;
		}
	}

	public Tile getTile() {
		return monsterTile;
	}

	public SlayerLocation getSlayerLocation() {
		return monsterLocation;
	}
}
