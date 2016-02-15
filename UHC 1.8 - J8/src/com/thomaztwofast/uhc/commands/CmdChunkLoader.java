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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.ChunkLoader;
import com.thomaztwofast.uhc.custom.JChat;
import com.thomaztwofast.uhc.custom.Perm;
import com.thomaztwofast.uhc.data.PlayerData;

public class CmdChunkLoader implements CommandExecutor, TabCompleter {
	private List<String> tb = ImmutableList.of("start");
	private Main pl;
	private ArrayList<UUID> uid = new ArrayList<>();
	private ChunkLoader cl;

	public CmdChunkLoader(Main main) {
		pl = main;
		cl = new ChunkLoader(pl);
	}

	/**
	 * Command              >   ChunkLoader
	 * Enabled Console      >   No
	 * Default Permission   >   OP
	 * 
	 * Description          >   Load/Generate chunk in the UHC arena by specific size.
	 */
	@Override
	public boolean onCommand(CommandSender send, Command cmd, String lab, String[] arg) {
		if (send instanceof Player) {
			PlayerData p = pl.getRegPlayer(((Player) send).getUniqueId());
			if (pl.getPlConf().pl_Enabled()) {
				JChat ic = new JChat();
				ic.add("ChunkLoader> ", null, 9, null, null);
				if (pl.getPlFun().hasPermission(p.cp, Perm.UHC)) {
					ic.add("Disabled!", null, 7, "2|/uhc help page 1", "§6§lHelp Information\n§7Click here to find out how to\n§7enable this command?");
				} else {
					ic.add("Disabled!", null, 7, null, null);
				}
				p.sendRawICMessage(ic.a());
				return true;
			} else if (cl.isRunning()) {
				p.sendMessage("ChunkLoader", "In progress from§e " + cl.getPlayerName() + "§7.");
				p.sendMessage("ChunkLoader", cl.getProgress() + "% Completed.");
				return true;
			} else if (arg.length == 0) {
				p.sendActionMessage("§7Loading...");
				p.sendRawICMessage(getChunkLoaderDisplay(p.cp.getUniqueId()));
				p.sendActionMessage("");
				return true;
			} else if (uid.contains(p.cp.getUniqueId()) && arg[0].equals("start")) {
				p.sendMessage("ChunkLoader", "Starting in a few seconds.");
				cl.start(p.cp);
				uid.clear();
				return true;
			}
			p.sendActionMessage("§7Loading ChunkLoader...");
			p.sendRawICMessage(getChunkLoaderDisplay(p.cp.getUniqueId()));
			p.sendActionMessage("");
			return true;
		}
		pl.getPlLog().info("Only ingame player can use this command.");
		return true;
	}

	/**
	 * ChunkLoader > Tab Complete
	 */
	@Override
	public List<String> onTabComplete(CommandSender send, Command cmd, String lab, String[] arg) {
		if (send instanceof Player) {
			if (!pl.getPlConf().pl_Enabled()) {
				if (arg.length == 1) {
					return StringUtil.copyPartialMatches(arg[0], tb, new ArrayList<>(tb.size()));
				}
			}
		}
		return null;
	}

	/**
	 * Get ChunkLoader Display Informations
	 */
	private String getChunkLoaderDisplay(UUID u) {
		ArrayList<World> wl = new ArrayList<>();
		ArrayList<Integer> cl = new ArrayList<>();
		int a = pl.getPlConf().ws_ArenaSize();
		int cb = pl.getPlConf().c_Border();
		int az = (a + cb);
		for (World w : pl.getServer().getWorlds()) {
			if (!w.getEnvironment().equals(Environment.THE_END)) {
				if (!new File(w.getWorldFolder(), "uhc.yml").exists()) {
					wl.add(w);
				}
			}
		}
		if (wl.size() != 0) {
			Location l1 = new Location(wl.get(0), (0 - az), 64, (0 - az));
			Location l2 = new Location(wl.get(0), (0 + az), 64, (0 + az));
			for (int i = l1.getChunk().getX(); i <= l2.getChunk().getZ(); i++) {
				for (int ii = l1.getChunk().getX(); ii <= l2.getChunk().getZ(); ii++) {
					cl.add(i);
				}
			}
		}
		JChat ic = new JChat();
		ic.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		ic.add("\n CHUNKLOADER\n \n", new int[] { 0 }, 15, null, null);
		if (wl.size() != 0) {
			ic.add(" Estimate Time: ", null, 10, null, null);
			ic.add(pl.getPlFun().asClockFormat(Math.round(((cl.size() * wl.size()) / ((20.0 / pl.getPlConf().c_DelayTick()) * pl.getPlConf().c_Task())))) + "\n", null, 12, null, null);
			ic.add(" \n Size Info\n", null, 6, null, null);
			ic.add("  Arena Size: ", null, 10, null, null);
			ic.add(a + "", null, 12, null, null);
			ic.add(" blocks\n", null, 14, null, null);
			if (cb != 0) {
				ic.add("  Border Size: ", null, 10, null, null);
				ic.add(cb + "", null, 12, null, null);
				ic.add(" blocks\n", null, 14, null, null);
			}
			ic.add("  Total Radius: ", null, 10, null, null);
			ic.add(az + "", null, 12, null, null);
			ic.add(" blocks from 0,0\n \n", null, 14, null, null);
			if (pl.getPlConf().c_ShowHidden()) {
				ic.add(" Chunk Info\n", null, 6, null, null);
				int i = 1;
				for (World w : wl) {
					if (!w.getEnvironment().equals(Environment.THE_END)) {
						ic.add("  " + i + ": ", null, 6, null, null);
						ic.add("World '", null, 7, null, null);
						ic.add(w.getName(), null, 14, null, null);
						ic.add("', Chunks '", null, 7, null, null);
						ic.add(cl.size() + "", null, 14, null, null);
						ic.add("'\n", null, 7, null, null);
						i++;
					}
				}
				ic.add("  Total Chunks: ", null, 6, null, null);
				ic.add((cl.size() * wl.size()) + "\n \n", null, 12, null, null);
			}

			ic.add(" If everything is okay?\n Type the command below to start.\n", null, 7, null, null);
			ic.add(" /ChunkLoader Start\n", null, 8, "2|/chunkloader start", "§e§l>§r §a/ChunkLoader Start§r");
			if (!uid.contains(u)) {
				uid.add(u);
			}
		} else {
			ic.add(" Every world has been loaded/generated.", null, 7, null, null);
		}
		ic.add("--------------------------------------------", new int[] { 3 }, 8, null, null);
		wl.clear();
		cl.clear();
		return ic.a();
	}
}
