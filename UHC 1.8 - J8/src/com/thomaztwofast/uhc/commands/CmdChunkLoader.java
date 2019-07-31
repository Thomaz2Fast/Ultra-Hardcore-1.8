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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.thomaztwofast.uhc.Chunkloader;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Node;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;
import com.thomaztwofast.uhc.lib.J;

public class CmdChunkLoader implements CommandExecutor, TabCompleter {
	private Main pl;
	private List<String> names = new ArrayList<>();
	private Chunkloader chunkloader;

	public CmdChunkLoader(Main pl) {
		this.pl = pl;
		chunkloader = new Chunkloader(pl);
	}

	/*
	 * Command - - - - - - - - - - > - Chunkloader
	 * Access  - - - - - - - - - - > - Disable Mode
	 * Who can use it  - - - - - - > - Players
	 * Arguments - - - - - - - - - > - 
	 *    ID       NAME        TAB COMPLETE   
	 *             Start       True
	 *             Stop        True     
	 * 
	 * Default Permission  - - - - > - OP
	 * Permission Node - - - - - - > - com.thomaztwofast.uhc.commands.chunkloader [ See plugin.yml ]
	 * 
	 * ----------------------------
	 * 
	 * Chunkloader will load / generated all the chunks in the arena by specific size that
	 * can be set on the config file.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			UHCPlayer u = pl.getRegisterPlayer(sender.getName());
			if (pl.config.pluginEnable) {
				J str = new J("Chunkloader> ", "8", "bi");
				if (u.hasNode(Node.UHC))
					str.addTextWithCmd("Disabled!", "7", "i", "/uhc help page 1", F.chatTitle("Help Information", "Click here to find out how to enable this command?"));
				else
					str.addText("Disabled!", "7", "i");
				u.sendJsonMessage(str.print());
				return true;
			}
			if (chunkloader.isActive()) {
				if (args.length == 1 && args[0].equals("stop")) {
					chunkloader.stop(sender.getName());
					u.sendCmdMessage("Chunkloader", "Stopped!");
					return true;
				}
				u.sendCmdMessage("Chunkloader", "In progress from \u00A7E\u00A7O" + chunkloader.getActivator() + "\u00A77\u00A7O.");
				u.sendCmdMessage("Chunkloader", chunkloader.getProgress() + " Completed.");
				return true;
			} else if (args.length == 1 && args[0].equals("start") && names.contains(sender.getName())) {
				chunkloader.start(u.player);
				u.sendCmdMessage("Chunkloader", "Starting in a few Seconds.");
				names.clear();
				return true;
			}
			u.sendActionMessage("Loading...");
			new Thread(new Runnable() {
				public void run() {
					u.sendJsonMessage(getDisplayInfo());
					u.sendActionMessage("");
				}

				private String getDisplayInfo() {
					String[] data = chunkloader.getPreData();
					String[] arr = data[0].split("\\|");
					J str = new J("                                                                   \n", "8", "s");
					str.addText(" CHUNKLOADER\n\n", "f", "b");
					if (arr[0].length() != 0) {
						str.addText(" Estimate Time: ", "a", "");
						str.addText(data[2] + "\n\n", "c", "");
						str.addText(" Size Info\n", "6", "");
						str.addText("  Arena Size: ", "a", "");
						str.addText(pl.config.worldSize + "", "c", "");
						str.addText(" Blocks\n", "e", "");
						if (pl.config.chunkBorder != 0) {
							str.addText("  Border Size: ", "a", "");
							str.addText(pl.config.chunkBorder + " ", "c", "");
							str.addText(" Blocks\n", "e", "");
						}
						str.addText("  Total Radius: ", "a", "");
						str.addText((pl.config.worldSize + pl.config.chunkBorder) + "", "c", "");
						str.addText(" Blocks from 0,0\n\n", "e", "");
						if (pl.config.chunkInDetail) {
							str.addText(" Chunk Info\n", "6", "");
							for (int i = 0; i < arr.length; i++) {
								str.addText("  " + (i + 1) + ": ", "6", "");
								str.addText("World '", "7", "");
								str.addText(arr[i], "e", "");
								str.addText("', Chunks '", "7", "");
								str.addText(data[1], "e", "");
								str.addText("'\n", "7", "");
							}
							str.addText("  Total Chunks: ", "6", "");
							str.addText((arr.length * Integer.parseInt(data[1])) + "\n \n", "c", "");
						}
						str.addText(" If everything is okay?\n Type the command below to start.\n", "7", "");
						str.addTextWithCmd(" /chunkloader start\n", "8", "", "/chunkloader start", F.chatCmd("chunkloader start"));
						if (!names.contains(sender.getName()))
							names.add(sender.getName());
					} else
						str.addText(" Every world has been loaded / generated.\n", "7", "");
					str.addText("                                                                   ", "8", "s");
					return str.print();
				}
			}).start();
			return true;
		}
		pl.log(0, "Command 'Chunkloader' can only execute from player.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && !pl.config.pluginEnable && args.length == 1)
			return StringUtil.copyPartialMatches(args[0], Arrays.asList("start", "stop"), new ArrayList<>(2));
		return null;
	}
}
