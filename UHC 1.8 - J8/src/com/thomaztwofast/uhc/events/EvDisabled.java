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
import com.thomaztwofast.uhc.custom.JChat;
import com.thomaztwofast.uhc.custom.Perm;
import com.thomaztwofast.uhc.data.PlayerData;

public class EvDisabled implements Listener {
	private Main pl;

	public EvDisabled(Main main) {
		pl = main;
	}

	/**
	 * Event > Player Join
	 */
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		pl.regPlayer(e.getPlayer());
		PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
		if (pl.getPlFun().hasPermission(p.cp, Perm.UHC)) {
			pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
				@Override
				public void run() {
					if (p.cp.isOnline()) {
						JChat ic = new JChat();
						ic.add("UHC> ", null, 9, null, null);
						ic.add("Ultra Hardcore 1.8 is disabled.", null, 7, "2|/uhc help page 0", "§6§lHelp Information\n§7Click here to find out how to\n§7enable this command?");
						p.sendRawICMessage(ic.a());
						p.playLocalSound(Sound.NOTE_STICKS, 1.8f);
					}
				}
			}, 10);
		}
	}

	/**
	 * Event > Player Quit
	 */
	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		pl.removeRegPlayer(e.getPlayer());
	}
}