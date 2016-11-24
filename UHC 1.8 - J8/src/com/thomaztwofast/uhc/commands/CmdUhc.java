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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import com.google.common.collect.ImmutableList;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.Function;
import com.thomaztwofast.uhc.custom.Jc;
import com.thomaztwofast.uhc.custom.Sc;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class CmdUhc extends Function implements CommandExecutor, TabCompleter {
	private Main cA;
	private YamlConfiguration cBa;
	private int cBb = 1;
	private boolean cBc = false;

	public CmdUhc(Main a) {
		cA = a;
		loadHelpCenter();
	}

	/**
	 * Command - - - - - - - - - - > - Uhc
	 * Enabled Console - - - - - - > - true
	 * Default Permission  - - - - > - OP
	 * Args  - - - - - - - - - - - > -
	 *       ID               NAME         TAB         CONSOLE
	 *       3198785          help         false       false
	 *       3347807          menu         true        false
	 *       1434631203       settings     true        true
	 *      -892481550        status       true        true
	 *      -838846263        update       false       false
	 */
	@Override
	public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
		if (!(a instanceof Player)) {
			if (d.length != 0) {
				switch (d[0].toLowerCase().hashCode()) {
				case 1434631203:
					a.sendMessage(getSettings(true, (d.length == 2 ? d[1] : "1")));
					return true;
				case -892481550:
					a.sendMessage(getStatus(true));
					return true;
				case 3198785:
				case 3347807:
				case -838846263:
					cA.log(0, "Command '/uhc " + d[0].toLowerCase() + "' can only execute from ingame player.");
					return true;
				}
			}
			a.sendMessage(getConsolePluginInfo());
			return true;
		}
		UHCPlayer e = cA.mB.getPlayer(a.getName());
		if (d.length != 0) {
			switch (d[0].toLowerCase().hashCode()) {
			case 3198785:
				if (!cBc) {
					e.sendJsonMessage(getHelpCenter(d));
					return true;
				}
				e.sendCommandMessage("Help Center", "Disabled!");
				return true;
			case 3347807:
				if (cA.mC.cCa && cA.mA.i() <= 5) {
					cA.mE.gB.openMenu(e);
					if (!e.uB.getInventory().contains(cA.mE.gB.uD)) {
						e.uB.getInventory().setItem(4, cA.mE.gB.uD);
					}
					return true;
				}
				Jc f = new Jc();
				f.add("Menu> ", new int[] { 1 }, 8, null, null);
				f.add("Disabled!", new int[] { 1 }, 7, "2|/uhc help page 0", "\u00A76\u00A7lHelp Information\n\u00A77Click here to find out how to\n\u00A77enable this command?");
				e.sendJsonMessage(f.o());
				return true;
			case 1434631203:
				e.uB.sendMessage(getSettings(false, (d.length == 2 ? d[1] : "1")));
				return true;
			case -892481550:
				e.uB.sendMessage(getStatus(false));
				return true;
			case -838846263:
				if (cA.mC.cCb && cA.mD.pBb) {
					e.sendJsonMessage(cA.mD.pBd.o());
				}
				return true;
			}
		}
		e.sendJsonMessage(getPluginInfo());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender a, Command b, String c, String[] d) {
		if (d.length == 1) {
			if (a instanceof Player) {
				return StringUtil.copyPartialMatches(d[0], ImmutableList.of("menu", "settings", "status"), new ArrayList<>(3));
			}
			return StringUtil.copyPartialMatches(d[0], ImmutableList.of("settings", "status"), new ArrayList<>(2));
		}
		return null;
	}

	// ------:- PRIVATE | COMMAND ARGS -:-----------------------------------------------------------

	private String getHelpCenter(String[] a) {
		if (a.length < 3) {
			return getHelpCenterMessage(new String[] { "help", "index", "1" });
		}
		return getHelpCenterMessage(a);
	}

	private String getPluginInfo() {
		Jc a = new Jc();
		a.add("--------------------------------------------\n", new int[] { 3 }, 8, null, null);
		a.add(" ULTRA HARDCORE 1.8\n \n", new int[] { 0 }, 15, null, null);
		a.add(" Commands\n", null, 6, null, null);
		a.add(" /UHC Help: ", null, 10, "2|/uhc help", "\u00A7e\u00A7l>\u00A7r\u00A7a /UHC Help");
		a.add("UHC help center. ", null, 7, null, null);
		a.add("(Beta)\n", null, 8, null, null);
		if (cA.mC.cCa) {
			if (cA.mC.cGa && !cA.mC.cFa) {
				a.add(" /AutoTeam: ", null, 10, "2|/autoteam", "\u00A7e\u00A7l>\u00A7r\u00A7a /AutoTeam");
				a.add(cA.getCommand("autoteam").getDescription() + "\n", null, 7, null, null);
				a.add(" /SelectTeam: ", null, 10, "2|/selectteam", "\u00A7e\u00A7l>\u00A7r\u00A7a /SelectTeam");
				a.add(cA.getCommand("selectteam").getDescription() + "\n", null, 7, null, null);
			}
			a.add(" /Start: ", null, 10, "2|/start", "\u00A7e\u00A7l>\u00A7r\u00A7a /Start");
			a.add(cA.getCommand("start").getDescription() + "\n", null, 7, null, null);
		} else {
			a.add(" /ChunkLoader: ", null, 10, "2|/chunkloader", "\u00A7e\u00A7l>\u00A7r\u00A7a /ChunkLoader");
			a.add(cA.getCommand("chunkloader").getDescription().substring(0, 37) + "...\n", null, 7, null, null);
		}
		a.add(" \n Plugin\n", null, 6, null, null);
		a.add(" Version: ", null, 10, null, null);
		a.add(cA.getDescription().getVersion(), null, 14, null, null);
		if (cA.mC.cCb && cA.mD.pBb) {
			a.add(" | ", null, 8, null, null);
			a.add("New update ", null, 10, "2|/uhc update", "\u00a76\u00a7lNew Update available.\u00a7r\n\u00a77Click here for more info.");
			a.add("[", null, 8, null, null);
			a.add(cA.mD.pBa, null, 14, null, null);
			a.add("]", null, 8, null, null);
		}
		a.add("\n Author: ", null, 10, null, null);
		a.add(cA.getDescription().getAuthors().get(0) + "\n", null, 14, null, "\u00A78\u00A7oPN7913.P6WP9M");
		a.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		return a.o();
	}

	private String getConsolePluginInfo() {
		Sc a = new Sc();
		a.setConsole();
		a.setTitle("ULTRA HARDCORE 1.8");
		a.addHeader("Commands");
		a.addListLn("UHC Settings", "Show game settings.");
		a.addListLn("UHC Status", "Plugin status / Game Status.");
		a.addHeader("Plugin");
		a.addListLn("Version", "\u00a7e" + cA.getDescription().getVersion() + (cA.mC.cCb && cA.mD.pBb ? "\u00a78|\u00a7a New Version \u00a78[\u00a7e" + cA.mD.pBa + "\u00a78]" : ""));
		a.addListLn("Author", "\u00a7e" + cA.getDescription().getAuthors().get(0));
		return a.o();
	}

	private String getSettings(boolean a, String b) {
		Sc c = new Sc();
		if (a) {
			c.setConsole();
		}
		switch (b.hashCode()) {
		case 50:
			c.setTitle("ULTRA HARDCORE 1.8 - SETTINGS \u00a78\u00a7l[\u00a7a\u00a7l2 - 5\u00a78\u00a7l]");
			c.addHeader("Game");
			c.addListLn("Mode", (cA.mC.cGa ? "Team" : "Solo"));
			if (cA.mC.cGa) {
				c.addListLn("Max Team Player", "" + cA.mC.cGb);
				c.addListLn("Friendly Fire", stat(cA.mC.cGd));
				c.addListLn("See Friendly Invisibles", stat(cA.mC.cGd));
				c.addListLn("Name Tag Visibility", teamTag(cA.mC.cGf));
				c.addListLn("Player Collision", teamCol(cA.mC.cGg));
			}
			c.addHeader("Server");
			if (cA.mC.cFa) {
				c.addListLn("Server ID", "" + cA.mC.cFc);
				if (cA.mC.cGa) {
					c.addListLn("Minimum Teams To Start", "" + cA.mC.cFe);
				} else {
					c.addListLn("Minimum Players To Start", "" + cA.mC.cFd);
				}
				c.addListLn("Countdown", "" + cA.mC.cFf);
				c.addListLn("BungeeCord", stat(cA.mC.cFv));
				if (cA.mC.cFv) {
					c.addListLn("Fallback Server", cA.mC.cFw);
				}
				break;
			}
			c.addListLn("Status", "Off");
			break;
		case 51:
			c.setTitle("ULTRA HARDCORE 1.8 - SETTINGS \u00a78\u00a7l[\u00a7a\u00a7l3 - 5\u00a78\u00a7l]");
			c.addHeader("Marker");
			if (cA.mC.cNa.length() != 0 || cA.mC.cNb != 0) {
				c.addListLn("Message", cA.mC.cNa);
				c.addListLn("Delay", asClock(cA.mC.cNb * 60));
			} else {
				c.addListLn("Status", "Off");
			}
			c.addHeader("Freezing Starting Players");
			c.addListLn("Status", stat(cA.mC.cMa));
			if (cA.mC.cMa) {
				c.addListLn("Size", "" + cA.mC.cMb);
			}
			c.addHeader("Disconnected Ingame Players");
			c.addListLn("Max Timeout", asClock(cA.mC.cPa * 60));
			c.addListLn("Message", cA.mC.cPb);
			break;
		case 52:
			c.setTitle("ULTRA HARDCORE 1.8 - SETTINGS \u00a78\u00a7l[\u00a7a\u00a7l4 - 5\u00a78\u00a7l]");
			c.addHeader("Golden Head");
			if (cA.mC.cLa) {
				c.addListLn("Default Apple", stat(cA.mC.cLb));
				c.addListLn("Golden Head Apple", stat(cA.mC.cLc));
			} else {
				c.addListLn("Status", "Off");
			}
			c.addHeader("Global Chat");
			if (cA.mC.cQa) {
				c.addListLn("Default Chat", stat(cA.mC.cQb.length() != 0));
				if (cA.mC.cGa) {
					c.addListLn("Team Chat", stat(cA.mC.cQd.length() != 0));
					c.addListLn("Private Team Chat", stat(cA.mC.cQe.length() != 0));
				}
				c.addListLn("Spectator Chat", stat(cA.mC.cQc.length() != 0));
			} else {
				c.addListLn("Status", "Off");
			}
			c.addHeader("Other");
			c.addListLn("Damage Logger", stat(cA.mC.cOa));
			c.addListLn("UHC Book", stat(cA.mC.cKa));
			break;
		case 53:
			c.setTitle("ULTRA HARDCORE 1.8 - SETTINGS \u00a78\u00a7l[\u00a7a\u00a7l5 - 5\u00a78\u00a7l]");
			c.addHeader("Minecraft Gamerules");
			c.addListLn("Daylight Cycle", stat(cA.mC.cEa));
			c.addListLn("Entity Drops", stat(cA.mC.cEb));
			c.addListLn("Fire Tick", stat(cA.mC.cEc));
			c.addListLn("Mob Loot", stat(cA.mC.cEd));
			c.addListLn("Mob Spawning", stat(cA.mC.cEe));
			c.addListLn("Tile Dropse", stat(cA.mC.cEf));
			c.addListLn("Max Entity Cramming", "" + cA.mC.cEh);
			c.addListLn("Mob Griefing", stat(cA.mC.cEi));
			c.addListLn("Random Tick Speed", "" + cA.mC.cEj);
			c.addListLn("Reduced Debug Info", stat(cA.mC.cEk));
			c.addListLn("Spectators Generate Chunks", stat(cA.mC.cEl));
			c.addListLn("Spawn Radius", "" + cA.mC.cEm);
			c.addListLn("Weather Cycle", "" + cA.mC.cEg);
			break;
		default:
			c.setTitle("ULTRA HARDCORE 1.8 - SETTINGS \u00a78\u00a7l[\u00a7a\u00a7l1 - 5\u00a78\u00a7l]");
			c.addHeader("World");
			c.addListLn("Sun Time", asRealClock(cA.mC.cIc));
			c.addListLn("Difficulty", wDif(cA.mC.cIa));
			c.addListLn("Arena Radius Size", "" + cA.mC.cIb);
			c.addHeader("WorldBorder");
			if (cA.mC.cJd != 0) {
				if (cA.mC.cJa != 0) {
					c.addListLn("Start Delay", "" + cA.mC.cJa);
				}
				c.addListLn("Start Position", "" + cA.mC.cJb);
				c.addListLn("Stop Position", "" + cA.mC.cJc);
				c.addListLn("Shrinks Time", asClock(cA.mC.cJd));
				break;
			}
			c.addListLn("Start Position", "" + cA.mC.cJb);
			break;
		}
		return c.o();
	}

	private String getStatus(boolean a) {
		Sc b = new Sc();
		if (a) {
			b.setConsole();
		}
		b.setTitle("ULTRA HARDCORE 1.8 - STATUS");
		b.addHeader("Plugin");
		b.addListLn("Status", stat(cA.mC.cCa));
		b.addListLn("Update Notifies", stat(cA.mC.cCb));
		if (cA.mC.cCa) {
			if (cA.mC.cFa) {
				b.addHeader("Server");
				b.addListLn("Server ID", "" + cA.mC.cFc);
				b.addListLn("BungeeCord", stat(cA.mC.cFv));
				if (cA.mC.cFv) {
					b.addListLn("Fallback Server", cA.mC.cFw);
				}
			}
			b.addHeader("Game");
			b.addListLn("Mode", (cA.mC.cGa ? "Team" : "Solo"));
			b.addListLn("Status", cA.mA.toString());
			if (cA.mA.i() > 5 && cA.mA.i() < 8) {
				b.addHeader("InGame");
				if (cA.mC.cGa) {
					b.addListLn("Alive Teams", cA.mE.getIngameSize() + "");
				}
				b.addListLn("Alive Players", cA.mE.getIngamePlayers().size() + "");
				if (cA.mE.getOfflineSize() != 0) {
					b.addListLn("Offline Player" + (cA.mE.getOfflineSize() > 1 ? "s" : ""), cA.mE.getOfflineSize() + "");
				}
			}
		}
		return b.o();
	}

	// ------:- PRIVATE | HELP CENTER -:------------------------------------------------------------

	private void loadHelpCenter() {
		try {
			cBa = YamlConfiguration.loadConfiguration(new StringReader(IOUtils.toString(cA.getResource("help.yml"))));
			cBb = cBa.getConfigurationSection("I.P").getKeys(false).size();
		} catch (IOException e) {
			cA.log(1, "Error, Could not load 'Help Center' information.");
			cBc = true;
		}
	}

	private String getHelpCenterMessage(String[] a) {
		Jc b = new Jc();
		b.add("--------------------------------------------\n", new int[] { 3 }, 8, null, null);
		switch (a[1].toLowerCase().hashCode()) {
		case 3433103:
			if (cBa.isConfigurationSection("H." + a[2])) {
				b.add(" " + cBa.getString("H." + a[2] + ".T") + "\n \n", new int[] { 0 }, 15, null, null);
				for (int c = 0; c < cBa.getStringList("H." + a[2] + ".D").size(); c++) {
					b.add(" " + (c + 1) + ": ", null, 6, null, null);
					String d = cBa.getStringList("H." + a[2] + ".D").get(c);
					for (int e = 0; e < StringUtils.countMatches(d, "++") / 2; e++) {
						d = d.replaceFirst("\\++", "\u00a7e").replaceFirst("\\++", "\u00a77");
					}
					b.add(d + "\n", null, 7, null, null);
				}
				b.add(" \n <<\n", new int[] { 0 }, 14, "2|/uhc help", "\u00a7aBack to help center");
				break;
			}
		case 100346066:
			if (cBa.isList("I.P." + a[2])) {
				b.add(" " + cBa.getString("I.T") + " ", new int[] { 0 }, 15, null, null);
				b.add("[", new int[] { 0 }, 8, null, null);
				b.add(a[2] + " - " + cBb, new int[] { 0 }, 10, null, null);
				b.add("] \n \n", new int[] { 0 }, 8, null, null);
				for (int c = 0; c < cBa.getStringList("I.P." + a[2]).size(); c++) {
					String[] d = cBa.getStringList("I.P." + a[2]).get(c).split("\\|");
					b.add(" " + d[1] + "\n", null, 10, "2|/uhc help page " + d[0], "\u00A7eOpen?");
				}
				int e = Integer.valueOf(a[2]);
				if (e != 1) {
					b.add(" \n <<", null, 14, "2|/uhc help " + (e - 1), "Previous page");
				}
				if (e != cBb) {
					b.add(" \n >>", null, 14, "2|/uhc help " + (e + 1), "Next page");
				}
				break;
			}
		default:
			b.add(" UHC - Help Center\n \n", new int[] { 0 }, 15, null, null);
			b.add(" Error\n", null, 12, null, null);
			b.add(" Oh no, something went wrong here.\n The help page you are looking for do not exist.\n \n", null, 7, null, null);
			b.add(" Use '", null, 7, null, null);
			b.add("/UHC Help", null, 10, "2|/uhc help", "\u00A7e\u00A7l>\u00A7r\u00A7a /UHC Help");
			b.add("' for help.", null, 7, null, null);
			break;
		}
		b.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		return b.o();
	}
}
