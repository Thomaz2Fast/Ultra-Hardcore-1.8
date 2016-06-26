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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.ChunkLoader;
import com.thomaztwofast.uhc.custom.Jc;
import com.thomaztwofast.uhc.data.Permission;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class CmdChunkLoader implements CommandExecutor, TabCompleter {
	private Main cA;
	private ChunkLoader cB;
	private List<UUID> cC = new ArrayList<>();

	public CmdChunkLoader(Main a) {
		cA = a;
		cB = new ChunkLoader(a);
	}

	/**
	 * Command - - - - - - - - - - > - ChunkLoader
	 * Enabled Console - - - - - - > - false
	 * Default Permission  - - - - > - OP
	 */
	@Override
	public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
		if (a instanceof Player) {
			UHCPlayer e = cA.mB.getPlayer(a.getName());
			if (cA.mC.cCa) {
				Jc f = new Jc();
				f.add("ChunkLoader> ", new int[] { 1 }, 8, null, null);
				if (e.uB.hasPermission(Permission.UHC.toString())) {
					f.add("Disabled!", new int[] { 1 }, 7, "2|/uhc help page 1", "\u00A76\u00A7lHelp Information\n\u00A77Click here to find out how to\n\u00A77enable this command?");
				} else {
					f.add("Disabled!", new int[] { 1 }, 7, null, null);
				}
				e.sendJsonMessage(f.o());
				return true;
			}
			if (cB.isRunning()) {
				if (d.length == 1 && d[0].equals("stop")) {
					cB.stop();
					e.sendCommandMessage("ChunkLoader", "Stopped!");
					return true;
				}
				e.sendCommandMessage("ChunkLoader", "In progress from \u00A7e\u00A7o" + cB.getPlayer() + "\u00A77\u00A7o.");
				e.sendCommandMessage("ChunkLoader", cB.getProgress() + " Completed.");
				return true;
			} else if (d.length == 1 && cC.contains(e.uB.getUniqueId()) && d[0].equals("start")) {
				cB.start(e.uB);
				cC.clear();
				e.sendCommandMessage("ChunkLoader", "Starting in a few seconds.");
				return true;
			}
			e.sendActionMessage("\u00A77Loading ChunkLoader...");
			e.sendJsonMessage(getChunkInfo(e.uB.getUniqueId()));
			e.sendActionMessage("");
			return true;
		}
		cA.log(0, "Command '/ChunkLoader' can only execute from ingame player.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender a, Command b, String c, String[] d) {
		if (a instanceof Player) {
			if (!cA.mC.cCa && d.length == 1) {
				return StringUtil.copyPartialMatches(d[0], ImmutableList.of("start", "stop"), new ArrayList<>(2));
			}
		}
		return null;
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private String getChunkInfo(UUID a) {
		String[] b = cB.getChunkData();
		String[] c = b[0].split("\\|");
		Jc d = new Jc();
		d.add("--------------------------------------------\n", new int[] { 3 }, 8, null, null);
		d.add(" CHUNKLOADER\n \n", new int[] { 0 }, 15, null, null);
		d.add("", null, 0, null, null);
		if (b[0].length() != 0) {
			d.add(" Estimate Time: ", null, 10, null, null);
			d.add(b[2] + "\n \n", null, 12, null, null);
			d.add(" Size Info\n", null, 6, null, null);
			d.add("  Arena Size: ", null, 10, null, null);
			d.add("" + cA.mC.cIb, null, 12, null, null);
			d.add(" blocks\n", null, 14, null, null);
			if (cA.mC.cDa != 0) {
				d.add("  Border Size: ", null, 10, null, null);
				d.add("" + cA.mC.cDa, null, 12, null, null);
				d.add(" blocks\n", null, 14, null, null);
			}
			d.add("  Total Radius: ", null, 10, null, null);
			d.add("" + (cA.mC.cIb + cA.mC.cDa), null, 12, null, null);
			d.add(" blocks from 0,0\n \n", null, 14, null, null);
			if (cA.mC.cDd) {
				d.add(" Chunk Info\n", null, 6, null, null);
				for (int e = 0; e < c.length; e++) {
					d.add("  " + (e + 1) + ": ", null, 6, null, null);
					d.add("World '", null, 7, null, null);
					d.add(c[e], null, 14, null, null);
					d.add("', Chunks '", null, 7, null, null);
					d.add(b[1], null, 14, null, null);
					d.add("'\n", null, 7, null, null);
				}
				d.add("  Total Chunks: ", null, 6, null, null);
				d.add((c.length * Integer.parseInt(b[1])) + "\n \n", null, 12, null, null);
			}
			d.add(" If everything is okay?\n Type the command below to start.\n", null, 7, null, null);
			d.add(" /chunkloader start\n", null, 8, "2|/chunkloader start", "\u00A7e\u00A7l>\u00A7r\u00A7a /chunkloader start");
			if (!cC.contains(a)) {
				cC.add(a);
			}
		} else {
			d.add(" Every world has been loaded / generated.\n", null, 7, null, null);
		}
		d.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		return d.o();
	}
}
