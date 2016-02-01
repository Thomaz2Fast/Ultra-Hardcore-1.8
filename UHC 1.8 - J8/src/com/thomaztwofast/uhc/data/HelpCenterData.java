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

import java.io.InputStream;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.JChat;

public class HelpCenterData {
	private FileConfiguration fc;
	private int mxPg = 1;

	/**
	 * Load UHC Help data
	 */
	@SuppressWarnings("deprecation")
	public HelpCenterData(Main main) {
		InputStream is = main.getResource("help.yml");
		fc = YamlConfiguration.loadConfiguration(is);
		mxPg = fc.getConfigurationSection("Index.Pages").getKeys(false).size();
	}

	/**
	 * UHC > HELP Center
	 */
	public String onCommand(String[] arg) {
		if (arg.length <= 2) {
			return getHelpMessage(new String[] { "help", "index", "1" });
		}
		return getHelpMessage(arg);
	}

	// :: PRIVATE :: //

	/**
	 * Get help message from YML file.
	 */
	private String getHelpMessage(String[] arg) {
		JChat ic = new JChat();
		ic.add("--------------------------------------------\n", new int[] { 3 }, 8, null, null);
		switch (arg[1]) {
		case "page":
			if (fc.isConfigurationSection("HelpInfo." + arg[2])) {
				ic.add(" " + fc.getString("HelpInfo." + arg[2] + ".Title") + "\n \n", new int[] { 0 }, 15, null, null);
				ic.add(fc.getString("HelpInfo." + arg[2] + ".Description") + "\n", null, 7, null, null);
				ic.add(" \n <<", new int[] { 0 }, 14, "2|/uhc help", "§aBack to UHC Help");
				break;
			}

		case "index":
			if (arg[1].equals("index")) {
				if (fc.isList("Index.Pages." + arg[2])) {
					ic.add(fc.getString("Index.Title").replace("%1", arg[2]).replace("%2", mxPg + ""), null, 15, null, null);
					for (String list : fc.getStringList("Index.Pages." + arg[2])) {
						String[] hlObj = list.split("\\|");
						ic.add(" " + hlObj[0] + "\n", null, 10, "2|/uhc help page " + hlObj[1], "§6§lOpen§r\n§a" + hlObj[0]);
					}
					int np = Integer.parseInt(arg[2]);
					if (mxPg != 1) {
						ic.add(" \n", null, 0, null, null);
					}
					if (np != 1) {
						ic.add("<<", new int[] { 0 }, 14, "2|/uhc help index " + (np - 1), "§aPrevious page");
					}
					if (np < mxPg) {
						ic.add(">>", new int[] { 0 }, 14, "2|/uhc help index " + (np + 1), "§aNext page");
					}
					break;
				}
			}

		default:
			ic.add(" UHC - Help Center\n \n", new int[] { 0 }, 15, null, null);
			ic.add(" Error\n", null, 12, null, null);
			ic.add(" Oh no, something went wrong here.\n The help page you are looking for doesn't exist.\n \n", null, 7, null, null);
			ic.add(" Use '", null, 7, null, null);
			ic.add("/UHC Help", null, 10, "2|/uhc help", "§e§l>§r §a/UHC Help");
			ic.add("' for help", null, 7, null, null);
			break;
		}
		ic.add("\n--------------------------------------------", new int[] { 3 }, 8, null, null);
		return ic.a();
	}
}