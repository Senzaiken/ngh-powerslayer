package org.dreambot.powerslayer.wrappers;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.Player;

public class LocationProfile {

	private MonsterLocation[] locations;


	/*	TODO Make an algorithm that returns a MonsterLocation based on:
	 * 		1. Current Location
	 * 		2. Combat level
	 * 		3. Equipment needed to go in
	 */
	public LocationProfile(MonsterLocation... locations) {
		this.locations = locations;
	}

	public MonsterLocation getBestLocation() {
		return null;
	}

	public Tile getNearestLocationFromTile(Tile tile) {
		Player local = Players.getLocal();
		Tile playerTile = local != null ? local.getTile() : tile;
		Tile closest = null;
		for (MonsterLocation location : locations) {
			Tile locTile = location.getTile();
			if (locTile == null)
				continue;
			if (closest == null || playerTile.distance(locTile) < playerTile.distance(closest))
				closest = locTile;
		}
		return closest;
	}

	public MonsterLocation[] getMonsterLocations() {
		return locations;
	}

}
