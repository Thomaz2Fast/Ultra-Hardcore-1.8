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

package com.thomaztwofast.uhc.events;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.Jc;
import com.thomaztwofast.uhc.data.Permission;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class EvDisabled implements Listener {
	private Main eA;

	public EvDisabled(Main a) {
		eA = a;
	}

	@EventHandler
	public void join(PlayerJoinEvent a) {
		UHCPlayer b = eA.mB.addPlayer(eA, a.getPlayer());
		if (b.uB.hasPermission(Permission.UHC.toString())) {
			Jc c = new Jc();
			c.add("> ", new int[] { 0, 1 }, 8, null, null);
			c.add("Ultra Hardcore 1.8 is disabled.", new int[] { 1 }, 7, "2|/uhc help page 0", "\u00A76\u00A7lHelp Information\n\u00A77Click here to find out how to\n\u00A77enable this plugin?");
			b.sendJsonMessageLater(c.o(), Sound.BLOCK_NOTE_HAT, 1.8f, 10);
		}
	}

	@EventHandler
	public void quit(PlayerQuitEvent a) {
		eA.mB.removePlayer(a.getPlayer());
	}
}
