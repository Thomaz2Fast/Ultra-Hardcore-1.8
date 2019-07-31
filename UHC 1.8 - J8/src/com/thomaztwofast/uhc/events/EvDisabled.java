/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2019> Thomaz2Fast
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
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitTask;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Node;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;
import com.thomaztwofast.uhc.lib.J;

public class EvDisabled implements Listener {
	private final String[] TITLE = { "Disable Mode", "Type '/uhc' to begin", "Type '/uhc help' to help", "Edit Mode" };
	private Main pl;
	private BossBar boss;
	private BukkitTask task;
	private World world;
	private int pos = 0;

	public EvDisabled(Main pl) {
		this.pl = pl;
		world = pl.getServer().getWorlds().get(0);
		boss = pl.getServer().createBossBar("", BarColor.RED, BarStyle.SEGMENTED_20);
		pl.PLAYERS.values().forEach(e -> join(e));
	}

	@EventHandler
	public void cmdPlayer(PlayerCommandPreprocessEvent e) {
		if (isMatch(e.getMessage(), "/rl|/reload") && e.getPlayer().hasPermission(Node.RL))
			boss.removeAll();
	}

	@EventHandler
	public void cmdServer(ServerCommandEvent e) {
		if (isMatch(e.getCommand(), "rl|reload"))
			boss.removeAll();
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		join(pl.registerPlayer(e.getPlayer()));
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		pl.unRegisterPlayer(e.getPlayer());
		if (boss.getPlayers().contains(e.getPlayer())) {
			boss.removePlayer(e.getPlayer());
			if (boss.getPlayers().size() == 0)
				task.cancel();
		}
	}

	// ---------------------------------------------------------------------------

	private boolean isMatch(String str, String regex) {
		return str.split(" ", 2)[0].toLowerCase().matches(regex);
	}

	private void join(UHCPlayer u) {
		if (u.hasNode(Node.UHC)) {
			J str = new J("> ", "8", "bi");
			str.addTextWithCmd("Ultra Hardcore 1.8 is disabled.", "7", "i", "/uhc help page 0", F.chatTitle("Help Information", "Click here to find out how to enable this plugin?"));
			u.sendJsonMessageLater(str.print(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.8f, 15);
			boss.addPlayer(u.player);
			if (task == null || task.isCancelled())
				run();
		}
	}

	private void run() {
		task = pl.getServer().getScheduler().runTaskTimer(pl, new Runnable() {
			@Override
			public void run() {
				boss.setTitle("\u00A7" + (world.getTime() > 12500 ? "C" : "F") + TITLE[pos]);
				pos = pos == TITLE.length - 1 ? 0 : pos + 1;
			}
		}, 0, 300);
	}
}
