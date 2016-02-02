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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.JChat;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.HelpCenterData;
import com.thomaztwofast.uhc.data.PlayerData;

public class CmdUhc implements CommandExecutor, TabCompleter {
	private List<String> tb = ImmutableList.of("menu", "settings", "status");
	private Main pl;
	private Config c;
	private HelpCenterData hc;

	public CmdUhc(Main main) {
		pl = main;
		c = main.getPlConf();
		hc = new HelpCenterData(main);
	}

	/**
	 * Command			>	UHC
	 * Enabled Console		>	Yes
	 * Default Permission		>	OP
	 * Args				-	NAME			TAB			CONSOLE
	 * 					help		|	Hidden		|	No
	 * 					menu		|	Show		|	No
	 * 					settings	|	Show		|	Yes
	 * 					status		|	Show		|	Yes
	 * 					update		|	Hidden		|	No
	 * 
	 * Description			>	UHC Command info / Plugin info / Game info / Help center.
	 */
	@Override
	public boolean onCommand(CommandSender send, Command cmd, String lab, String[] arg) {
		if (send instanceof Player) {
			PlayerData p = pl.getRegPlayer(((Player) send).getUniqueId());
			if (arg.length == 0) {
				p.sendRawICMessage(uhcCommandInfo(false));
				return true;
			}
			switch (arg[0]) {
			case "help":
				p.sendRawICMessage(hc.onCommand(arg));
				return true;
			case "menu":
				if (c.pl_Enabled() & pl.getGame().getStatus().getStat().getLvl() <= 5) {
					if (!p.cp.getInventory().contains(pl.getGame().getMenu().getItem())) {
						p.cp.getInventory().setItem(4, pl.getGame().getMenu().getItem());
					}
					p.cp.openInventory(pl.getGame().getMenu().openMenu(p.cp.getUniqueId()));
					p.setInventoryLock(true, "MENU");
					return true;
				}
				JChat ic = new JChat();
				ic.add("Menu> ", null, 9, null, null);
				ic.add("Disabled!", null, 7, "2|/uhc help page 0", "§6§lHelp Information\n§7Click here to find out how to\n§7enable this command?");
				p.sendRawICMessage(ic.a());
				return true;
			case "settings":
				if (arg.length == 2) {
					p.cp.sendMessage(uhcGameSettings(false, arg[1]));
					return true;
				}
				p.cp.sendMessage(uhcGameSettings(false, "1"));
				return true;
			case "status":
				p.cp.sendMessage(uhcGameStatus(false));
				return true;
			case "update":
				if (c.pl_updateNotification()) {
					if (pl.getPlUpdate().isNewUpdate()) {
						p.sendRawICMessage(pl.getPlUpdate().getMessage());
					}
				}
				return true;
			default:
				p.sendRawICMessage(uhcCommandInfo(false));
				return true;
			}
		}
		if (arg.length == 0) {
			send.sendMessage(uhcCommandInfo(true));
			return true;
		}
		switch (arg[0]) {
		case "settings":
			if (arg.length == 2) {
				send.sendMessage(uhcGameSettings(true, arg[1]));
				return true;
			}
			send.sendMessage(uhcGameSettings(true, "1"));
			return true;
		case "status":
			send.sendMessage(uhcGameStatus(true));
			return true;
		case "help":
		case "menu":
		case "update":
			pl.getPlLog().info("Only ingame player can use this command.");
			return true;
		default:
			send.sendMessage(uhcCommandInfo(true));
			return true;
		}
	}

	/**
	 * UHC > Tab Completer
	 */
	@Override
	public List<String> onTabComplete(CommandSender send, Command cmd, String lab, String[] arg) {
		if (arg.length == 1) {
			return StringUtil.copyPartialMatches(arg[0], tb, new ArrayList<String>(tb.size()));
		}
		return null;
	}

	/**
	 * UHC > Command Info
	 */
	private String uhcCommandInfo(boolean b) {
		if (b) {
			String s = "\n§8§m--------------------------------------------§r";
			s += "\n §lULTRA HARDCORE 1.8§r\n \n §6Commands§r\n";
			s += " §aUHC Help:§7 Plugin help center. §8(Beta)§r\n";
			if (c.pl_Enabled()) {
				if (c.g_teamMode() & !c.server()) {
					s += " §aAutoTeam:§7 " + pl.getCommand("autoteam").getDescription() + "\n";
					s += " §aSelectTeam:§7 " + pl.getCommand("selectteam").getDescription() + "\n";
				}
				s += " §aStart:§7 " + pl.getCommand("start").getDescription() + "\n \n";
			} else {
				s += " §aChunkLoader:§7 " + pl.getCommand("chunkloader").getDescription() + "§r\n \n";
			}
			s += " §6Plugin§r\n §aVersion: §e" + pl.getDescription().getVersion() + "§r";
			if (c.pl_updateNotification()) {
				if (pl.getPlUpdate().isNewUpdate()) {
					s += "§8 | §aNew update §8[§e" + pl.getPlUpdate().getVersion() + "§8]§r";
				}
			}
			s += "\n §aAuthor: §e" + pl.getDescription().getAuthors().get(0) + "§r\n";
			if (pl.getDescription().getAuthors().size() > 1) {
				s += " §aHelper: " + getPluginHelper() + "§r\n";
			}
			s += "§8§m--------------------------------------------§r";
			return s;
		}
		JChat ic = new JChat();
		ic.add("--------------------------------------------\n", new int[] { 3 }, 8, null, null);
		ic.add(" ULTRA HARDCORE 1.8\n \n", new int[] { 0 }, 15, null, null);
		ic.add(" Commands\n", null, 6, null, null);
		ic.add(" /UHC Help: ", null, 10, "2|/uhc help", "§e§l>§r §a/UHC Help§r");
		ic.add("Plugin help center. ", null, 7, null, null);
		ic.add("(Beta) \n", null, 8, null, null);
		if (c.pl_Enabled()) {
			if (c.g_teamMode() && !c.server()) {
				ic.add(" /AutoTeam: ", null, 10, "2|/autoteam", "§e§l>§r §a/AutoTeam§r");
				ic.add(pl.getCommand("autoteam").getDescription() + "\n", null, 7, null, null);
				ic.add(" /SelectTeam: ", null, 10, "2|/selectteam", "§e§l>§r §a/SelectTeam§r");
				ic.add(pl.getCommand("selectteam").getDescription() + "\n", null, 7, null, null);
			}
			ic.add(" /Start: ", null, 10, "2|/start", "§e§l>§r §a/Start§r");
			ic.add(pl.getCommand("start").getDescription() + "\n \n", null, 7, null, null);
		} else {
			ic.add(" /ChunkLoader: ", null, 10, "2|/chunkloader", "§e§l>§r §a/ChunkLoader§r");
			ic.add(pl.getCommand("chunkloader").getDescription() + "\n \n", null, 7, null, null);
		}
		ic.add(" Plugin\n", null, 6, null, null);
		ic.add(" Version: ", null, 10, null, null);
		ic.add(pl.getDescription().getVersion(), null, 14, null, null);
		if (c.pl_updateNotification()) {
			if (pl.getPlUpdate().isNewUpdate()) {
				ic.add(" | ", null, 8, null, null);
				ic.add("New update", null, 10, "2|/uhc update", "§7New Update available.\n§8Click here for more info.");
				ic.add(" [", null, 8, null, null);
				ic.add(pl.getPlUpdate().getVersion(), null, 14, null, null);
				ic.add("]", null, 8, null, null);
			}
		}
		ic.add("\n Author: ", null, 10, null, null);
		ic.add(pl.getDescription().getAuthors().get(0) + "\n", null, 14, null, "§8§oPN7913.P6WP9M");
		if (pl.getDescription().getAuthors().size() > 1) {
			ic.add(" Helper: ", null, 10, null, null);
			ic.add(getPluginHelper() + "\n", null, 14, null, null);
		}
		ic.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		return ic.a();
	}

	// :: PRIVATE :: //

	/**
	 * UHC > Game Settings
	 */
	private String uhcGameSettings(boolean b, String pg) {
		String s = (b ? "\n" : "") + "§8§m--------------------------------------------§r\n";
		s += " §lULTRA HARDCORE 1.8 - SETTINGS ";
		switch (pg) {
		case "2":
			s += "§8§l[§a§l2 - 5§8§l]§r\n \n";
			s += " §6Game§r\n";
			s += " §aGame Mode:§7 " + (c.g_teamMode() ? "Team" : "Solo") + "§r\n";
			if (c.g_teamMode()) {
				s += " §aMax Team Player:§7 " + c.g_maxTeamPlayer() + "§r\n";
				s += " §aFriendly Fire:§7 " + (c.go_Friendly() ? "On" : "Off") + "§r\n";
				s += " §aSee Friendly Invisibles:§7 " + (c.go_SeeFriendly() ? "On" : "Off") + "§r\n";
				int nt = c.go_NameTagVisibility();
				s += " §aName Tag Visibility:§7 " + (nt == 0 ? "On" : (nt == 1 ? "Hide Other Team" : (nt == 2 ? "Hide Own Team" : "Off"))) + "§r\n";
			}
			s += " \n §6Server Mode§r\n";
			s += " §aStatus:§7 " + (c.server() ? "On" : "Off") + "§r\n";
			s += " §aServer ID:§7 " + c.server_ID() + "§r\n";
			if (c.g_teamMode()) {
				s += " §aMinimum Teams To Start:§7 " + c.server_MinTeamToStart() + "§r\n";
			} else {
				s += " §aMinimum Players To Start:§7 " + c.server_MinPlayerToStart() + "§r\n";
			}
			s += " §aCountdown:§7 " + pl.getPlFun().asClockFormat(c.server_Countdown()) + "§r\n";
			s += " §aBungeeCord Support:§7 " + (c.server_BungeeCord() ? "On" : "Off") + "§r\n";
			if (c.server_BungeeCord()) {
				s += " §aBungeeCord Lobby:§7 " + c.server_BungeeCordHub() + "§r\n";
			}
			break;
		case "3":
			s += "§8§l[§a§l3 - 5§8§l]§r\n \n";
			s += " §6Marker§r\n";
			if (c.mk_Delay() != 0) {
				if (c.mk_Message().length() != 0) {
					s += " §aMessage:§7 " + c.mk_Message() + "§r\n";
					s += " §aDelay:§7 " + pl.getPlFun().asClockFormat((c.mk_Delay() * 60)) + "§r\n";
				} else {
					s += " §aDelay:§7 " + pl.getPlFun().asClockFormat((c.mk_Delay() * 60)) + "§r\n";
				}
			} else {
				s += " §aStatus:§7 Disabled§r\n";
			}
			s += " \n §6Freezing Starting Players§r\n";
			s += " §aStatus:§7 " + (c.fp_Enabled() ? "On" : "Off") + "§r\n";
			s += " §aRadius Size:§7 " + c.fp_Size() + "§r\n";
			s += " \n §6Disconnected Ingame Players§r\n";
			s += " §aMax Timeout:§7 " + pl.getPlFun().asClockFormat((c.of_Timeout() * 60)) + "§r\n";
			s += " §aMessage:§7 " + c.of_Message() + "§r\n";
			break;
		case "4":
			s += "§8§l[§a§l4 - 5§8§l]§r\n \n";
			s += " §6Golden Head§r\n";
			s += " §aStatus:§7 " + (c.gh_Enalbed() ? "On" : "Off") + "§r\n";
			if (c.gh_Enalbed()) {
				s += " §aDefault Apple:§7 " + (c.gh_DefaultApple() ? "On" : "Off") + "§r\n";
				s += " §aGolden Head Apple:§7 " + (c.gh_GoldenApple() ? "On" : "Off") + "§r\n";
			}
			s += " \n §6Global Chat§r\n";
			if (c.g_teamMode()) {
				s += " §aNone Team Player Chat:§7 " + (c.gc_TeamDefault().length() != 0 ? "On" : "Off") + "§r\n";
				s += " §aTeam Chat:§7 " + (c.gc_TeamTeam().length() != 0 ? "On" : "Off") + "§r\n";
				s += " §aPrivate Team Chat:§7 " + (c.gc_TeamPrivate().length() != 0 ? "On" : "Off") + "§r\n";
			} else {
				s += " §aDefault Chat:§7 " + (c.gc_Default().length() != 0 ? "On" : "Off") + "§r\n";
			}
			s += " §aSpectator Chat:§7 " + (c.gc_Spectator().length() != 0 ? "On" : "Off") + "§r\n";
			s += " \n §6Other§r\n";
			s += " §aDamage Logger:§7 " + (c.damageLog() ? "On" : "Off") + "§r\n";
			s += " §aUHC Book:§7 " + (c.book_Enabled() ? "On" : "Off") + "§r\n";
			break;
		case "5":
			s += "§8§l[§a§l5 - 5§8§l]§r\n \n";
			s += " §6Minecraft Gamerules§r\n";
			s += " §aDaylight Cycle:§7 " + (c.gr_DaylightCycle() ? "On" : "Off") + "§r\n";
			s += " §aEntity Drops:§7 " + (c.gr_EntityDrops() ? "On" : "Off") + "§r\n";
			s += " §aFire Tick:§7 " + (c.gr_FireTick() ? "On" : "Off") + "§r\n";
			s += " §aMob Loot:§7 " + (c.gr_MobLoot() ? "On" : "Off") + "§r\n";
			s += " §aMob Spawning:§7 " + (c.gr_MobSpawning() ? "On" : "Off") + "§r\n";
			s += " §aTile Dropse:§7 " + (c.gr_TileDrops() ? "On" : "Off") + "§r\n";
			s += " §aMob Griefing:§7 " + (c.gr_MobGriefing() ? "On" : "Off") + "§r\n";
			s += " §aRandom Tick Speed:§7 " + c.gr_RandomTick() + "§r\n";
			s += " §aReduced Debug Info:§7 " + (c.gr_ReducedDebugInfo() ? "On" : "Off") + "§r\n";
			s += " §aSpectators Generate Chunks:§7 " + (c.gr_SpectatorsGenerateChunks() ? "On" : "Off") + "§r\n";
			break;
		default:
			s += "§8§l[§a§l1 - 5§8§l]§r\n \n";
			s += " §6World§r\n";
			s += " §aSun Time:§7 " + pl.getPlFun().tickFormat(c.ws_SunTime()) + "§r\n";
			s += " §aDifficulty:§7 " + (c.ws_Difficulty() == 1 ? "Easy" : (c.ws_Difficulty() == 2 ? "Normal" : "Hard")) + "§r\n";
			s += " §aArena Radius Size:§7 " + c.ws_ArenaSize() + " blocks from 0,0§r\n \n";
			s += " §6World Border§r\n";
			if (c.wb_Time() != 0) {
				if (c.wb_StartDelay() != 0) {
					s += " §aStart Delay:§7 " + pl.getPlFun().asClockFormat(c.wb_StartDelay()) + "§r\n";
				}
				s += " §aStart Position:§7 " + c.wb_StartPos() + " blocks from 0,0§r\n";
				s += " §aStop Position:§7 " + c.wb_EndPos() + " blocks from 0,0§r\n";
				s += " §aShrinks Time:§7 " + pl.getPlFun().asClockFormat(c.wb_Time()) + "§r\n";
			} else {
				s += " §aStart Position:§7 " + c.wb_StartPos() + "§r\n";
			}
			break;
		}
		s += "§8§m--------------------------------------------§r";
		return s;
	}

	/**
	 * UHC > Game Status
	 */
	private String uhcGameStatus(boolean b) {
		String s = (b ? " \n" : "") + "§8§m--------------------------------------------§r";
		s += "\n §lULTRA HARDCORE 1.8 - STATUS§r\n \n";
		s += " §6Plugin§r\n §aStatus:§7 " + (c.pl_Enabled() ? "Enabled" : "Disabled") + "§r\n";
		s += " §aUpdate Notifies:§7 " + (c.pl_updateNotification() ? "Enabled" : "Disabled") + "§r\n";
		if (c.pl_Enabled()) {
			if (c.server()) {
				s += " \n §6Server Mode§r\n";
				s += " §aID:§7 " + c.server_ID() + "§r\n";
				s += " §aBungeeCord:§7 " + (c.server_BungeeCord() ? "Enabled" : "Disabled") + "§r\n";
				if (c.server_BungeeCord()) {
					s += " §aHub Server:§7 " + c.server_BungeeCordHub() + "§r\n";
				}
			}
			s += " \n §6Game§r\n";
			s += " §aMode:§7 " + (c.g_teamMode() ? "Team" : "Solo") + "§r\n";
			s += " §aStatus:§7 " + pl.getGame().getStatus().getStat().toString() + "§r\n";
			if (!pl.getGame().getStatus().getStat().isActive()) {
				s += " \n §6Players§r\n";
				if (c.g_teamMode()) {
					s += " §aAlive Teams:§7 " + pl.getGame().getAliveTeams() + "\n";
				}
				s += " §aAlive Players:§7 " + pl.getGame().getAlivePlayers() + "\n";
				if (pl.getGame().getAliveOfflinePlayers() != 0) {
					s += " §aOffline Player:§7 " + pl.getGame().getAliveOfflinePlayers() + "\n";
				}
			}
		}
		s += "§8§m--------------------------------------------§r";
		return s;
	}

	/**
	 * Get the other authors of this plugin.
	 */
	private String getPluginHelper() {
		String s = "";
		int sz = pl.getDescription().getAuthors().size();
		for (int i = 1; i < sz; i++) {
			s += pl.getDescription().getAuthors().get(i);
			if (i != (sz - 1)) {
				s += "§8, ";
			}
		}
		return s;
	}
}
