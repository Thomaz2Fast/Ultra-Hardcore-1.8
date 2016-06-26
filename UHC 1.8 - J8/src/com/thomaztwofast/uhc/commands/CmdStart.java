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

package com.thomaztwofast.uhc.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.StringUtil;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.Jc;
import com.thomaztwofast.uhc.data.Permission;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class CmdStart implements CommandExecutor, TabCompleter {
	private Main cA;
	private List<UUID> cB = new ArrayList<>();

	public CmdStart(Main a) {
		cA = a;
	}

	/**
	 * Command - - - - - - - - - - > - Start
	 * Enabled Console - - - - - - > - false
	 * Default Permission  - - - - > - OP
	 * Args  - - - - - - - - - - - > -
	 *       ID               NAME         		TAB         CONSOLE
	 *       115760           uhc         		true       false
	 *      -1166190107       notify-player 	false       false
	 */
	@Override
	public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
		if (a instanceof Player) {
			UHCPlayer e = cA.mB.getPlayer(a.getName());
			if (cA.mC.cCa && cA.mA.i() == 4) {
				if (d.length != 0) {
					switch (d[0].toLowerCase().hashCode()) {
					case -1166190107:
						if (cA.mC.cGa && d.length == 2) {
							if (UUID.fromString(d[1]) != null && cA.mB.getPlayer(UUID.fromString(d[1])) != null) {
								UHCPlayer f = cA.mB.getPlayer(UUID.fromString(d[1]));
								e.sendActionMessage("\u00A77Send notification to \u00A7e" + f.uB.getName());
								f.sendCommandMessage("Notification", "This game is about to start. please select a team if you want to join the UHC or else you will be spectating the game.");
								for (int g = 0; g < 2; g++) {
									cA.getServer().getScheduler().runTaskLater(cA, new Runnable() {
										@Override
										public void run() {
											f.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f);
										}
									}, 5 * g);
								}
							}
						}
						return true;
					case 115760:
						if (cB.contains(e.uB.getUniqueId()) && ig() > 1) {
							cB.clear();
							if (cA.mC.cFa) {
								cA.mE.gC.forceStart(e.uB.getName());
								return true;
							}
							cA.mE.gmStart(ig());
							return true;
						}
					}
				}
				e.sendJsonMessage(r(e.uB.getUniqueId()));
				return true;
			}
			Jc f = new Jc();
			f.add("Start> ", new int[] { 1 }, 8, null, null);
			if (e.uB.hasPermission(Permission.UHC.toString())) {
				f.add("Disabled!", new int[] { 1 }, 7, "2|/uhc help page 0", "\u00A76\u00A7lHelp Information\n\u00A77Click here to find out how to\n\u00A77enable this command?");
			} else {
				f.add("Disabled!", new int[] { 1 }, 7, null, null);
			}
			e.sendJsonMessage(f.o());
			return true;
		}
		cA.log(0, "Command '/Start' can only execute from ingame player.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender a, Command b, String c, String[] d) {
		if (a instanceof Player && cA.mC.cCa && d.length == 1) {
			return StringUtil.copyPartialMatches(d[0], Arrays.asList("uhc"), new ArrayList<>(1));
		}
		return null;
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private String r(UUID a) {
		List<String> b = (cA.mC.cGa ? gTd() : gSd());
		Jc c = new Jc();
		c.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		c.add("\n ULTRA HARDCORE - " + (cA.mC.cGa ? "TEAM" : "SOLO") + " MODE\n \n", new int[] { 0 }, 15, null, null);
		c.add(" Total " + (cA.mC.cGa ? "Teams" : "Players") + ": ", null, 10, null, null);
		c.add(b.get(0) + "\n", null, 7, null, null);
		if (cA.mC.cGa) {
			if (b.size() > 1) {
				c.add(" \n Warning\n", null, 14, null, null);
				c.add(" There " + ((b.size() - 1) > 1 ? "are " + (b.size() - 1) + " players" : "is 1 player") + " which is not in the team yet.\n", null, 7, null, null);
				c.add(" Player" + ((b.size() - 1) > 1 ? "s" : "") + ": ", null, 7, null, null);
				for (int d = 1; d < b.size(); d++) {
					if (d > 1) {
						c.add(", ", null, 7, null, null);
					}
					String[] e = b.get(d).split("\\|");
					c.add(e[0], null, 14, "2|/start notify-player " + e[1], "\u00A77Send notification?");
				}
				c.add(" \n \n For those who have not in the team yet, will be move to spectator mode.\n", new int[] { 1 }, 8, null, null);
			}
		}
		if (Integer.valueOf(b.get(0)) < 2) {
			c.add(" \n Error \n", null, 12, null, null);
			c.add(" Not enough " + (cA.mC.cGa ? "teams" : "players") + " to start the game.\n", null, 7, null, null);
			c.add(" Required: 2 or more " + (cA.mC.cGa ? "teams" : "players") + ".\n", null, 7, null, null);
			cB.clear();
		} else {
			c.add(" \n If everyone is ready to start?\n", null, 7, null, null);
			c.add(" Type the command below to start the game.\n", null, 7, null, null);
			c.add(" /start uhc", null, 8, "2|/start uhc", "\u00A7e\u00A7l>\u00A7r\u00A7a /start uhc");
			if (!cB.contains(a)) {
				cB.add(a);
			}
		}
		c.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		return c.o();
	}

	private List<String> gSd() {
		List<String> a = new ArrayList<>();
		int b = 0;
		for (UHCPlayer c : cA.mB.getAllPlayers()) {
			if (c.uB.getGameMode().equals(GameMode.ADVENTURE)) {
				b++;
			}
		}
		a.add(b + "");
		return a;
	}

	private List<String> gTd() {
		List<String> a = new ArrayList<>();
		Scoreboard b = cA.getServer().getScoreboardManager().getMainScoreboard();
		int c = 0;
		for (String d : cA.mE.gD.uCa) {
			if (b.getTeam(d) != null && b.getTeam(d).getSize() != 0) {
				c++;
			}
		}
		a.add(c + "");
		for (UHCPlayer d : cA.mB.getAllPlayers()) {
			if (b.getEntryTeam(d.uB.getName()) == null && d.uB.getGameMode().equals(GameMode.ADVENTURE)) {
				a.add(d.uB.getName() + "|" + d.uB.getUniqueId());
			}
		}
		return a;
	}

	private int ig() {
		return Integer.valueOf((cA.mC.cGa ? gTd().get(0) : gSd().get(0)));
	}
}
