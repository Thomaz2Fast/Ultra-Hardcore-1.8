/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2016> Thomaz2Fast
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.thomaztwofast.uhc.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.thomaztwofast.uhc.Main;

public class PlayerList {
	private HashMap<UUID, String> pA = new HashMap<>();
	private HashMap<String, UHCPlayer> pB = new HashMap<>();

	public UHCPlayer addPlayer(Main a, Player b) {
		pA.put(b.getUniqueId(), b.getName());
		pB.put(b.getName(), new UHCPlayer(a, b));
		return pB.get(b.getName());
	}

	public void removePlayer(Player a) {
		pB.remove(a.getName());
		pA.remove(a.getUniqueId());
	}

	public UHCPlayer getPlayer(String a) {
		return (pB.containsKey(a) ? pB.get(a) : null);
	}

	public UHCPlayer getPlayer(UUID a) {
		return (pA.containsKey(a) ? pB.get(pA.get(a)) : null);
	}

	public Collection<UHCPlayer> getAllPlayers() {
		return pB.values();
	}
}
