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

package com.thomaztwofast.uhc.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;
import com.thomaztwofast.uhc.lib.J;
import com.thomaztwofast.uhc.lib.S;

public class CmdUhc implements CommandExecutor, TabCompleter {
	private Main pl;
	private String[] tabs = { "menu", "settings", "status" };
	private YamlConfiguration helpYaml;
	private int helpMaxPage = 1;

	public CmdUhc(Main pl) {
		this.pl = pl;
		helpYaml = YamlConfiguration.loadConfiguration(new InputStreamReader(pl.getResource("help.yml")));
		helpMaxPage = helpYaml.getConfigurationSection("i.p").getKeys(false).size();
	}

	/*
	 * Command - - - - - - - - - - > - UHC
	 * Access  - - - - - - - - - - > - Global
	 * Who can use it  - - - - - - > - Console / Players
	 * Arguments - - - - - - - - - > - 
	 *    ID               NAME           TAB COMPLETE       CONSOLE
	 *    3198785          help           false              false
	 *    3347807          menu           true               false
	 *    1434631203       settings       true               true
	 *   -892481550        status         true               true
	 * 
	 * Default Permission  - - - - > - OP
	 * Permission Node - - - - - - > - com.thomaztwofast.uhc.commands.uhc       [ See plugin.yml ]
	 * 
	 * ----------------------------
	 * 
	 * Ultra Hardcore 1.8 command center / plugin information / Help information.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length != 0) {
				switch (args[0].toLowerCase().hashCode()) {
				case 1434631203:
					sender.sendMessage(settings(true, (args.length == 2 ? args[1] : "1")));
					return true;
				case -892481550:
					sender.sendMessage(status(true));
					return true;
				case 3198785:
				case 3347807:
					pl.log(0, "Command 'UHC " + args[0].toLowerCase() + "' can only execute from player.");
					return true;
				}
			}
			sender.sendMessage(plugin(true));
			return true;
		}
		UHCPlayer u = pl.getRegisterPlayer(sender.getName());
		if (args.length != 0) {
			switch (args[0].toLowerCase().hashCode()) {
			case 3198785:
				if (helpYaml != null) {
					u.sendJsonMessage(help(args));
					return true;
				}
				u.sendCmdMessage("Help Center", "Disabled!");
				return true;
			case 3347807:
				if (pl.config.pluginEnable && pl.status.ordinal() <= 5) {
					pl.gameManager.menu.openInv(u);
					if (!u.player.getInventory().contains(pl.gameManager.menu.itemStack))
						u.player.getInventory().setItem(4, pl.gameManager.menu.itemStack);
					return true;
				}
				J str = new J("Menu> ", "8", "bi");
				str.addText("Disabled!", "7", "i");
				u.sendJsonMessage(str.print());
				return true;
			case 1434631203:
				u.player.sendMessage(settings(false, (args.length == 2 ? args[1] : "1")));
				return true;
			case -892481550:
				u.player.sendMessage(status(false));
				return true;
			}
		}
		u.sendJsonMessage(plugin(false));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			if (sender instanceof Player)
				return StringUtil.copyPartialMatches(args[0], Arrays.asList(tabs), new ArrayList<>(3));
			return StringUtil.copyPartialMatches(args[0], Arrays.asList(tabs[1], tabs[2]), new ArrayList<>(2));
		}
		return null;
	}

	// ---------------------------------------------------------------------------

	private String help(String[] args) {
		if (args.length < 3)
			args = new String[] { "help", "index", "1" };
		J str = new J("                                                                   \n", "8", "s");
		switch (args[1].toLowerCase().hashCode()) {
		case 3433103:
			if (helpYaml.isConfigurationSection("h." + args[2])) {
				str.addText(" " + helpYaml.getString("h." + args[2] + ".t") + "\n\n", "f", "b");
				helpYaml.getStringList("h." + args[2] + ".d").forEach(e -> {
					str.addText(" - ", "6", "");
					str.addText(strReplaceMatch(e, "++") + "\n", "7", "");
				});
				str.addText("\n ", "7", "");
				str.addTextWithCmd("<<\n", "e", "b", "/uhc help", "\u00A7eBack to help center");
				break;
			}
		case 100346066:
			if (helpYaml.isList("i.p." + args[2])) {
				str.addText(" " + helpYaml.getString("i.t"), "f", "b");
				str.addText(" [", "8", "b");
				str.addText(args[2] + " - " + helpMaxPage, "a", "b");
				str.addText("]\n\n", "8", "b");
				helpYaml.getStringList("i.p." + args[2]).forEach(b -> str.addTextWithCmd(" " + helpYaml.getString("h." + b + ".t") + "\n", "a", "", "/uhc help page " + b, "\u00A7eOpen?"));
				int page = Integer.valueOf(args[2]);
				if (page != 1) {
					str.addText("\n", "7", "");
					str.addTextWithCmd(" <<", "e", "b", "/uhc help index " + (page - 1), "\u00A7ePrevious page");
				}
				if (page != helpMaxPage) {
					str.addText(page != 1 ? " | " : "\n ", "8", "");
					str.addTextWithCmd(">> ", "e", "b", "/uhc help index " + (page + 1), "\u00A7eNext page");
					str.addText("\n", "7", "");
				}
				break;
			}
		default:
			str.addText(" " + helpYaml.getString("i.t"), "f", "b");
			str.addText(" \n\n Error\n", "c", "");
			str.addText(" Oh no, something went wrong here.\n The page you are looking for do not exist.\n\n", "7", "");
			str.addText(" Use '", "7", "");
			str.addTextWithCmd("/uhc Help", "a", "", "/uhc help", F.chatCmd("uhc help"));
			str.addText("' for help.", "7", "");
			break;
		}
		str.addText("                                                                   ", "8", "s");
		return str.print();
	}

	private String strReplaceMatch(String str, String match) {
		for (int i = 0; i < StringUtils.countMatches(str, match) / 2; i++)
			str = str.replaceFirst("\\" + match, "\u00a7E").replaceFirst("\\" + match, "\u00a77");
		return str;
	}

	private String plugin(boolean bool) {
		if (bool) {
			S str = new S(true);
			str.setTitle("ULTRA HARDCORE 1.8");
			str.addHeader("Commands");
			str.addText("UHC Settings", "Show game settings.");
			str.addText("UHC Status", "Plugin status / Game Status.");
			str.addHeader("Plugin");
			str.addText("Version", pl.getDescription().getVersion() + (pl.config.pluingUpdate && pl.update.isNew() ? "\u00a78|\u00A7A New Version \u00A78[\u00A7E" + pl.update.getInformation()[0] + "\u00A78]" : ""));
			str.addText("Author", pl.getDescription().getAuthors().get(0));
			return str.print();
		}
		J str = new J("                                                                   \n", "8", "s");
		str.addText(" ULTRA HARDCORE 1.8\n\n", "f", "b");
		str.addText(" Commands\n", "6", "");
		str.addTextWithCmd(" /UHC Help:", "a", "", "/uhc help", F.chatCmd("uhc help"));
		str.addText(" UHC help center.", "7", "");
		str.addText(" (Beta)\n", "8", "");
		if (pl.config.pluginEnable) {
			if (pl.config.gameInTeam && !pl.config.serverEnable) {
				str.addTextWithCmd(" /AutoTeam:", "a", "", "/autoteam", F.chatCmd("autoteam"));
				str.addText(" " + pl.getCommand("autoteam").getDescription() + "\n", "7", "");
				str.addTextWithCmd(" /Selectteam:", "a", "", "/selectteam", F.chatCmd("selectteam"));
				str.addText(" " + pl.getCommand("selectteam").getDescription() + "\n", "7", "");
			}
			str.addTextWithCmd(" /Start:", "a", "", "/start", F.chatCmd("start"));
			str.addText(" " + pl.getCommand("start").getDescription() + "\n", "7", "");
		} else {
			str.addTextWithCmd(" /Chunkloader:", "a", "", "/chunkloader", F.chatCmd("chunkloader"));
			str.addText(" " + pl.getCommand("chunkloader").getDescription().substring(0, 37) + "...\n", "7", "");
		}
		str.addText("\n Plugin\n", "6", "");
		str.addText(" Version: ", "a", "");
		str.addTextWithHoverText(pl.getDescription().getVersion(), "e", "", "\u00A78\u00A7OMC V1.13.2");
		if (pl.config.pluingUpdate && pl.update.isNew()) {
			String[] data = pl.update.getInformation();
			str.addText(" | ", "8", "");
			str.addTextWithURL("New update ", "a", "", data[1], F.chatTitle("New Update available", "Click here to go to the download page."));
			str.addText("[", "8", "");
			str.addText(data[0], "e", "");
			str.addText("]", "8", "");
		}
		str.addText("\n Author: ", "a", "");
		str.addTextWithHoverText("Thomaz2Fast\n", "e", "", "\u00A78\u00A7OPN7913.P6WP9M");
		str.addText("                                                                   ", "8", "s");
		return str.print();
	}

	private String settings(boolean bool, String page) {
		S str = new S(bool);
		String title = "ULTRA HARDCORE 1.8 - SETTINGS \u00A78\u00A7L[\u00A7A\u00A7L%0 - 6\u00a78\u00A7l]";
		switch (page.hashCode()) {
		case 50:
			str.setTitle(title.replace("%0", "2"));
			str.addHeader("Game");
			str.addText("Mode", pl.config.gameInTeam ? "Team" : "Solo");
			str.addText("Old Combat Mode", F.isOn(pl.config.gameOldCombat, false));
			if (pl.config.gameInTeam) {
				str.addText("Max Team Player", pl.config.gameMaxTeam + "");
				str.addText("Player Collision", F.isTeamsOption(pl.config.gameCollision, true, false));
				str.addText("Name Tag Visibility", F.isTeamsOption(pl.config.gameNameTag, false, false));
				str.addText("Friendly Fire", F.isOn(pl.config.gameIsFriendly, false));
				str.addText("See Friendly Invisibles", F.isOn(pl.config.gameSeeFriendly, false));
			}
			str.addHeader("Server");
			if (pl.config.serverEnable) {
				str.addText("Server ID", pl.config.serverID + "");
				str.addText("Minimum " + (pl.config.gameInTeam ? "Teams" : "Players") + " To Start", (pl.config.gameInTeam ? pl.config.serverMinTeam : pl.config.serverMinSolo) + "");
				str.addText("Countdown", pl.config.serverCountdown + "");
				str.addText("Disabled Chunkloader", F.isOn(pl.config.serverActiveChunkloader, false));
				str.addText("BungeeCord", F.isOn(pl.config.serverIsBungeeCord, false));
				if (pl.config.serverIsBungeeCord)
					str.addText("Fallback Server", pl.config.serverHub);
				break;
			}
			str.addText("Status", "Off");
			break;
		case 51:
			str.setTitle(title.replace("%0", "3"));
			str.addHeader("Marker");
			if (!pl.config.markerMsg.isEmpty() && pl.config.markerDelay != 0) {
				str.addText("Delay", F.getTimeLeft(pl.config.markerDelay * 60));
				str.addText("Message", pl.config.markerMsg);
			} else
				str.addText("Status", "Off");
			str.addHeader("Freezing Starting Players");
			str.addText("Status", F.isOn(pl.config.freezeEnable, false));
			if (pl.config.freezeEnable)
				str.addText("Size", pl.config.freezeSize + "");
			str.addHeader("Disconnected Ingame Players");
			str.addText("Max Timeout", F.getTimeLeft(pl.config.kickerDelay * 60));
			str.addText("Message", pl.config.kickerMsg);
			break;
		case 52:
			str.setTitle(title.replace("%0", "4"));
			str.addHeader("Golden Head");
			if (pl.config.headEnable) {
				str.addText("Default Apple", F.isOn(pl.config.headDefault, false));
				str.addText("Golden Head Apple", F.isOn(pl.config.headGolden, false));
			} else
				str.addText("Status", "Off");
			str.addHeader("Global Chat");
			if (pl.config.chatEnable) {
				str.addText("Default Chat", F.isOn(pl.config.chatDefault.isEmpty(), false));
				if (pl.config.gameInTeam) {
					str.addText("Team Chat", F.isOn(pl.config.chatTeam.isEmpty(), false));
					str.addText("Private Team Chat", F.isOn(pl.config.chatTeamMsg.isEmpty(), false));
				}
				str.addText("Spectator Chat", F.isOn(pl.config.chatSpectator.isEmpty(), false));
			} else
				str.addText("Status", "Off");
			str.addHeader("Other");
			str.addText("UHC Book", F.isOn(pl.config.bookEnable, false));
			str.addText("Damage Logger", F.isOn(pl.config.dmgEnable, false));
			break;
		case 53:
			str.setTitle(title.replace("%0", "5"));
			str.addHeader("Minecraft Gamerules 1/2");
			str.addText("Disable Elytra Movement Check", F.isOn(pl.config.grDisabledElytra, false));
			str.addText("Daylight Cycle", F.isOn(pl.config.grDaylight, false));
			str.addText("Entity Drops", F.isOn(pl.config.grEntityDrops, false));
			str.addText("Fire Tick", F.isOn(pl.config.grFireTick, false));
			str.addText("Limited Crafting", F.isOn(pl.config.grLimitedCrafting, false));
			str.addText("Mob Loot", F.isOn(pl.config.grMobLoot, false));
			str.addText("Mob Spawning", F.isOn(pl.config.grMobSpawning, false));
			str.addText("Tile Drops", F.isOn(pl.config.grTileDrops, false));
			str.addText("Weather Cycle", F.isOn(pl.config.grWeather, false));
			str.addText("Max Entity Cramming", pl.config.grMaxCramming + "");
			break;
		case 54:
			str.setTitle(title.replace("%0", "6"));
			str.addHeader("Minecraft Gamerules 2/2");
			str.addText("Mob Griefing", F.isOn(pl.config.grMobGriefing, false));
			str.addText("Random TickSpeed", pl.config.grRandom + "");
			str.addText("Reduced Debug Info", F.isOn(pl.config.grDebugInfo, false));
			str.addText("Spawn Radius", pl.config.grSpawnRadius + "");
			str.addText("Spectators Generate Chunks", F.isOn(pl.config.grSpectators, false));
			break;
		default:
			str.setTitle(title.replace("%0", "1"));
			str.addHeader("World");
			str.addText("Sun Time", F.asRealClock(pl.config.worldTime));
			str.addText("Difficulty", F.isDifficulty(pl.config.worldDifficulty, false));
			str.addText("Arena Radius Size", pl.config.worldSize + "");
			str.addHeader("WorldBorder");
			if (pl.config.borderTime != 0) {
				if (pl.config.borderDelay != 0)
					str.addText("Start Delay", pl.config.borderDelay + "");
				str.addText("Start Position", pl.config.borderStartSize + "");
				str.addText("Stop Position", pl.config.borderEndSize + "");
				str.addText("Shrinks Time", F.getTimeLeft(pl.config.borderTime));
				break;
			}
			str.addText("Start Position", pl.config.borderStartSize + "");
			break;
		}
		return str.print();
	}

	private String status(boolean bool) {
		S str = new S(bool);
		str.setTitle("ULTRA HARDCORE 1.8 - STATUS");
		str.addHeader("Plugin");
		str.addText("Status", F.isOn(pl.config.pluginEnable, false));
		if (pl.lastError.length() != 0)
			str.addText("Error", "\u00A7C" + pl.lastError);
		str.addText("Update Notifies", F.isOn(pl.config.pluingUpdate, false));
		if (pl.config.pluginEnable) {
			if (pl.config.serverEnable) {
				str.addHeader("Server");
				str.addText("Server ID", pl.config.serverID + "");
				str.addText("BungeeCord", F.isOn(pl.config.serverIsBungeeCord, false));
				if (pl.config.serverIsBungeeCord)
					str.addText("Fallback Server", pl.config.serverHub);
			}
			str.addHeader("Game");
			str.addText("Mode", pl.config.gameInTeam ? "Team" : "Solo");
			str.addText("Status", pl.status + "");
			if (pl.status.ordinal() > 5 && pl.status.ordinal() < 8) {
				str.addHeader("InGame");
				if (pl.config.gameInTeam)
					str.addText("Alive Teams", pl.gameManager.locations.size() + "");
				str.addText("Alive Players", pl.gameManager.inGamePlayers.size() + "");
				if (pl.gameManager.offlineTasks.size() != 0)
					str.addText("Offline Players", pl.gameManager.offlineTasks.size() + "");
			}
		}
		return str.print();
	}
}
