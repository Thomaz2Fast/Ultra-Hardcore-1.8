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
import java.util.List;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.JChat;
import com.thomaztwofast.uhc.custom.Perm;
import com.thomaztwofast.uhc.data.PlayerData;

public class CmdStart implements CommandExecutor, TabCompleter {
	private List<String> t = ImmutableList.of("uhc");
	private ArrayList<UUID> sOp = new ArrayList<>();
	private Main pl;

	public CmdStart(Main main) {
		pl = main;
	}

	/**
	 * Command              >   Start
	 * Enabled Console      >   No
	 * Default Permission   >   OP
	 * Args                 -   NAME                TAB             CONSOLE
	 *                          uhc             |   Show        |   No
	 *                          notify-player   |   Hidden      |   No
	 * 
	 * Description          >   Start the Ultra Hardcore game.
	 */
	@Override
	public boolean onCommand(CommandSender send, Command cmd, String lab, String[] arg) {
		if (send instanceof Player) {
			PlayerData p = pl.getRegPlayer(((Player) send).getUniqueId());
			if (pl.getPlConf().pl_Enabled()) {
				if (pl.getGame().getStatus().getStat().getLvl() <= 4) {
					if (arg.length == 0) {
						p.sendRawICMessage(getGameReport(p.cp.getUniqueId()));
						return true;
					} else {
						switch (arg[0].toLowerCase()) {
						case "notify-player":
							if (arg.length == 2 & pl.getPlConf().g_teamMode()) {
								try {
									if (UUID.fromString(arg[1]) != null) {
										if (pl.getRegPlayer(UUID.fromString(arg[1])) != null) {
											PlayerData np = pl.getRegPlayer(UUID.fromString(arg[1]));
											p.sendActionMessage("§7Send notification to §e" + np.cp.getName());
											np.sendMessage("Notification", "The game is about to start. please select a team if you want to join the UHC or else you will be spectating the game.");
											float[] f = { 0.8f, 0.8f };
											int i = 0;
											for (float fs : f) {
												pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
													@Override
													public void run() {
														np.playLocalSound(Sound.ORB_PICKUP, fs);
													}
												}, 5 * i);
												i++;
											}
										}
									}
								} catch (IllegalArgumentException e) {
									pl.getPlLog().warn("[Notification Player] invalid UUID '" + arg[1] + "'");
								}
							}
							return true;
						case "uhc":
							if (sOp.contains(p.cp.getUniqueId())) {
								if (canStartUhc() > 1) {
									sOp.remove(p.cp.getUniqueId());
									if (pl.getPlConf().server()) {
										pl.getGame().getServer().forceStart(p.cp.getName());
										return true;
									}
									pl.getGame().startUhcGame(canStartUhc());
									return true;
								}
							}
							p.sendRawICMessage(getGameReport(p.cp.getUniqueId()));
							return true;
						}
						return true;
					}
				}
			}
			JChat ic = new JChat();
			ic.add("Start> ", null, 9, null, null);
			if (pl.getPlFun().hasPermission(p.cp, Perm.UHC)) {
				ic.add("Disabled!", null, 7, "2|/uhc help page 0", "§6§lHelp Information\n§7Click here to find out how to\n§7enable this command?");
			} else {
				ic.add("Disabled!", null, 7, null, null);
			}
			p.sendRawICMessage(ic.a());
			return true;
		}
		pl.getPlLog().info("Only ingame player can use this command.");
		return true;
	}

	/**
	 * Start > Tab Complete
	 */
	@Override
	public List<String> onTabComplete(CommandSender send, Command cmd, String lab, String[] arg) {
		if (send instanceof Player) {
			if (pl.getPlConf().pl_Enabled()) {
				if (arg.length == 1) {
					return StringUtil.copyPartialMatches(arg[0], t, new ArrayList<>(t.size()));
				}
			}
		}
		return null;
	}

	// :: PRIVATE :: //

	/**
	 * Game Information
	 */
	private String getGameReport(UUID u) {
		ArrayList<String> data;
		JChat ic = new JChat();
		ic.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		ic.add("\n ULTRA HARDCORE - " + (pl.getPlConf().g_teamMode() ? "TEAM" : "SOLO") + " MODE\n \n", new int[] { 0 }, 15, null, null);
		if (pl.getPlConf().g_teamMode()) {
			data = getTeamsData();
			ic.add(" Total Teams: ", null, 10, null, null);
			ic.add(data.get(0) + "\n", null, 7, null, null);
			if (data.get(1).length() != 0) {
				ic.add(" \n Warning\n", null, 14, null, null);
				ic.add(" There " + (data.size() > 3 ? "are " + (data.size() - 2) + " players" : "is 1 player") + ", which is not in the team yet.\n", null, 7, null, null);
				ic.add(" Player" + (data.size() > 3 ? "s" : "") + ": ", null, 7, null, null);
				for (int i = 0; i < (data.size() - 2); i++) {
					if (i != 0) {
						ic.add(", ", null, 7, null, null);
					}
					String[] us = data.get((i + 2)).split("\\|");
					ic.add(us[1], null, 14, "2|/start notify-player " + us[0], "§7Send notification?");
				}
				ic.add(" \n \n For those who have not in the team yet, will be move to spectator mode.\n", new int[] { 1 }, 8, null, null);
			}
		} else {
			data = getSolosData();
			ic.add(" Total Players: ", null, 10, null, null);
			ic.add(data.get(0) + "\n", null, 7, null, null);
		}
		if (Integer.parseInt(data.get(0)) <= 1) {
			ic.add(" \n Error\n", null, 12, null, null);
			ic.add(" Not enough " + (pl.getPlConf().g_teamMode() ? "teams" : "players") + " to start the game.\n", null, 7, null, null);
			ic.add(" Required: 2 or more " + (pl.getPlConf().g_teamMode() ? "teams" : "players") + ".\n", null, 7, null, null);
			sOp.remove(u);
		} else {
			ic.add(" \n If everyone is ready to start?\n", null, 7, null, null);
			ic.add(" Type the command below to start the game.\n", null, 7, null, null);
			ic.add(" /start uhc\n", null, 8, "2|/start uhc", "§e§l>§r §a/start uhc");
			if (!sOp.contains(u)) {
				sOp.add(u);
			}
		}
		ic.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		return ic.a();
	}

	/**
	 * Get solo data.
	 */
	private ArrayList<String> getSolosData() {
		ArrayList<String> data = new ArrayList<>();
		data.add(pl.getRegPlayerData().size() + "");
		return data;
	}

	/**
	 * Get team data.
	 */
	private ArrayList<String> getTeamsData() {
		ArrayList<String> data = new ArrayList<>();
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		int acT = 0;
		for (Team t : sb.getTeams()) {
			if (t.getSize() != 0) {
				acT++;
			}
		}
		data.add(acT + "");
		data.add("");
		for (PlayerData p : pl.getRegPlayerData().values()) {
			if (sb.getEntryTeam(p.cp.getName()) == null) {
				data.add(p.cp.getUniqueId() + "|" + p.cp.getName());
			}
		}
		if (data.size() > 2) {
			data.set(1, "true");
		}
		return data;
	}

	/**
	 * Check if Ultra Hardcore game can start now
	 */
	private int canStartUhc() {
		if (pl.getPlConf().g_teamMode()) {
			return Integer.parseInt(getTeamsData().get(0));
		}
		return Integer.parseInt(getSolosData().get(0));
	}
}
