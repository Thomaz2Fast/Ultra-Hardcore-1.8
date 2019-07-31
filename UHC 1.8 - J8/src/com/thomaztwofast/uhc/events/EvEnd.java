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

import java.util.Arrays;
import java.util.List;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Node;
import com.thomaztwofast.uhc.lib.F;

public class EvEnd implements Listener {
	private Main pl;
	private BossBar boss;
	private List<String> title = Arrays.asList("15|Thanks for playing", "", "5|UHC 1.8 Coded by Thomaz2Fast");
	private int titlePos;
	private Scoreboard scoreboard;
	private String prefix = "\u00A76\u00A7lWINNER\u00A7r ";
	private Team winnerTeam;

	public EvEnd(Main pl, String winner) {
		this.pl = pl;
		scoreboard = pl.getServer().getScoreboardManager().getMainScoreboard();
		title.set(1, "30|UHC Winner: " + winner);
		pl.getServer().getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
			public void run() {
				if (pl.config.gameInTeam) {
					Team team = scoreboard.getTeam(F.mcColorRm(winner).substring(5).replace(" ", "_").replace(" ", "_"));
					team.setPrefix(prefix);
				} else {
					if (scoreboard.getTeam("uhc_winner") != null)
						scoreboard.getTeam("uhc_winner").unregister();
					winnerTeam = scoreboard.registerNewTeam("uhc_winner");
					winnerTeam.setPrefix(prefix);
					winnerTeam.addEntry(F.mcColorRm(winner));
					pl.config.gameInTeam = true;
				}
				boss = pl.getServer().createBossBar("", BarColor.RED, BarStyle.SEGMENTED_20);
				pl.getServer().getOnlinePlayers().forEach(e -> boss.addPlayer(e));
				updateTitle(title.get(titlePos));
			}

			private void updateTitle(String input) {
				String[] arr = input.split("\\|");
				boss.setTitle(F.mcColor(arr[1]));
				pl.getServer().getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
					public void run() {
						titlePos = titlePos == (title.size() - 1) ? 0 : titlePos + 1;
						updateTitle(title.get(titlePos));
					}
				}, Integer.parseInt(arr[0]) * 20);
			}
		}, 20 * 10);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void command(PlayerCommandPreprocessEvent e) {
		if ((isMatch(e.getMessage(), "/rl|/reload") && e.getPlayer().hasPermission(Node.RL)) || (isMatch(e.getMessage(), "/stop") && e.getPlayer().hasPermission(Node.CL)))
			unload();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void command(ServerCommandEvent e) {
		if (isMatch(e.getCommand(), "rl|reload|stop"))
			unload();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void join(PlayerJoinEvent e) {
		if (boss != null)
			boss.addPlayer(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void quit(PlayerQuitEvent e) {
		if (boss != null)
			boss.removePlayer(e.getPlayer());
	}

	// ---------------------------------------------------------------------------

	private boolean isMatch(String input, String regex) {
		return input.split(" ")[0].toLowerCase().matches(regex);
	}

	private void unload() {
		boss.removeAll();
		if (winnerTeam != null) {
			winnerTeam.unregister();
			pl.config.gameInTeam = false;
		}
	}
}
