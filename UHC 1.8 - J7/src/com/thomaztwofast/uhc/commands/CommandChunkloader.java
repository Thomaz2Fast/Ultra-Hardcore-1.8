/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2015> Thomaz2Fast
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.thomaztwofast.uhc.Function;
import com.thomaztwofast.uhc.Main;

public class CommandChunkloader implements CommandExecutor, TabCompleter {
	private List<String> tab = ImmutableList.of("start");
	private Main pl;
	private boolean clMode = false;
	private DecimalFormat clDF = new DecimalFormat("#.##");
	private ArrayList<UUID> taskPs = new ArrayList<UUID>();
	private ArrayList<World> ws = new ArrayList<World>();
	private ArrayList<String> cs = new ArrayList<String>();
	private ArrayList<Long> tj = new ArrayList<Long>();
	private long sysTime;
	private long sysTimeJob;
	private int cl1 = 1;
	private int cl2 = 0;
	private int clTP = 0;
	private int clWP = 0;
	private int clSysId;
	private UUID uuid = null;
	private Scoreboard sb;

	public CommandChunkloader(Main main) {
		this.pl = main;
	}

	/**
	 * <b>~ Command: /chunkloader ~</b>
	 * <p>
	 * When the player trigger this command, it will generate all chunk in the Ultra Hardcore
	 * arena by spesific arena size. This chunkloader will not only generate all chunk in 1
	 * world, but in all overworld and nether world.
	 * </p>
	 * 
	 * @param sender = Get the player how trigger the command.
	 * @param cmd = Get the command that player type in.
	 * @param label = Get the command in string format.
	 * @param args = Get all argument that player type in the command.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player == false) {
			pl.getLogger().info("Only ingame player can use this command.");
			return true;
		} else {
			if (pl.plMode) {
				CraftPlayer cp = (CraftPlayer) sender;
				IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("[{text: '§9ChunkLoader>'},{text: '§7 Disabled!', hoverEvent: {action: 'show_text', value: {text: '', extra: [{text: '§9§lHelp?\n\n§7How to enable this command?\n§7Open §econfig.yml§7 file to this plugin\n§7and change the \"Plugin Mode\" => \"false\"'}]}}}]");
				cp.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(icbc));
				return true;
			} else {
				Player p = (Player) sender;
				if (clMode) {
					float a = (float) ((float) (clTP + .0) / (cs.size() * ws.size()) + .0) * 100;
					p.sendMessage("§9ChunkLoader>§7 In progress from§e " + pl.getServer().getOfflinePlayer(uuid).getName() + "§7.");
					p.sendMessage("§9ChunkLoader>§7 " + clDF.format(a) + "% Completed.");
					return true;
				} else {
					if (args.length == 0) {
						Function.sendActionBarPlayerMessages(p, "§7Loading ChunkLoader...");
						p.sendMessage(displayInfo());
						Function.sendActionBarPlayerMessages(p, "");
						taskPs.add(p.getUniqueId());
						return true;
					} else {
						if (taskPs.contains(p.getUniqueId()) && args[0].equalsIgnoreCase("start")) {
							clMode = true;
							uuid = p.getUniqueId();
							p.sendMessage("§9ChunkLoader>§7 Starting in few seconds.");
							chunkloaderPrePering();
							return true;
						} else {
							Function.sendActionBarPlayerMessages(p, "§7Loading ChunkLoader...");
							p.sendMessage(displayInfo());
							Function.sendActionBarPlayerMessages(p, "");
							if (!taskPs.contains(p.getUniqueId())) {
								taskPs.add(p.getUniqueId());
							}
							return true;
						}
					}
				}
			}
		}
	}

	/**
	 * <b>~ Chunkloader Auto Tab Completer ~</b>
	 * <p>
	 * Show all matches tab args for specific command args.
	 * </p>
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!pl.plMode) {
			if (sender instanceof Player) {
				if (args.length == 1) {
					return StringUtil.copyPartialMatches(args[0], tab, new ArrayList<String>(tab.size()));
				}
			}
		}
		return null;
	}

	/**
	 * <b>~ Chunkloader > Display Info ~</b>
	 * <p>
	 * Display how long this job will take and how big the arena will be.<br>
	 * If '<i>ChunkLoader.ShowHiddenDetail</i>' enabled, it will show what world will generate
	 * chunk and how many chunk it will generated.
	 * </p>
	 * 
	 * @return Text
	 */
	private String displayInfo() {
		long t = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append("§8§m--------------------------------------------§r");
		sb.append("\n §lCHUNKLOADER§r\n \n");
		sb.append(" §aJob time: §c" + jobTime() + "§r\n");
		sb.append(" §aArena size: §c" + (pl.woArenaSize + pl.clBorder) + "§e block from 0,0.§r\n \n");
		if (pl.clShd) {
			sb.append(" §6Chunk Info:§r\n");
			int i = 1;
			for (World w : ws) {
				sb.append("  §6" + i + ":§7 World '§e" + w.getName() + "§7' Chunk '§e" + cs.size() + "§7'§r\n");
				i++;
			}
			sb.append("  §6Total Chunk: §c" + (cs.size() * ws.size()) + "§7.§r\n \n");
		}
		sb.append(" §7If everything is okay?\n Type the command below to start.§r\n");
		sb.append(" §8/chunkloader start§r\n");
		if ((System.currentTimeMillis() - t) >= 100) {
			sb.append("\n \n §eWarning§r\n§7 ChunkLoader will use a lot computer resurs and time to do this task.§r\n");
		}
		sb.append("§8§m--------------------------------------------§r");
		return sb.toString();
	}

	/**
	 * <b>~ Get ChunkLoader Job Time ~</b>
	 * 
	 * @return String
	 */
	private String jobTime() {
		ws.clear();
		cs.clear();
		int tws = 0;
		int tcs = 0;
		int az = pl.woArenaSize + pl.clBorder;
		for (World w : pl.getServer().getWorlds()) {
			if (!w.getEnvironment().equals(Environment.THE_END)) {
				ws.add(w);
				tws++;
			}
		}
		Location l1 = new Location(pl.getServer().getWorlds().get(0), (0 - az), 64, (0 - az));
		Location l2 = new Location(pl.getServer().getWorlds().get(0), (0 + az), 64, (0 + az));
		for (int i = l1.getChunk().getX(); i <= l2.getChunk().getZ(); i++) {
			for (int o = l1.getChunk().getX(); o <= l2.getChunk().getZ(); o++) {
				cs.add(tcs + "");
				tcs++;
			}
		}
		return Function.getTimeFormat((int) ((tws * tcs) / ((20.0 / pl.clTick) * pl.clTask)) + "000");
	}

	/**
	 * <b>~ Preload ChunkLoader Task | ChunkLoader Run~</b>
	 * <p>
	 * Find all world and chunk in the Ultra Hardcore Game arena and saved on memory.<br>
	 * After that it will take a break and start generated chunk in selected world.
	 * </p>
	 */
	private void chunkloaderPrePering() {
		sysTime = (System.currentTimeMillis() + 10000);
		sysTimeJob = (System.currentTimeMillis() + 10000);
		sb = pl.getServer().getScoreboardManager().getNewScoreboard();
		Player p = pl.getServer().getPlayer(uuid);
		ws.clear();
		cs.clear();
		for (World w : pl.getServer().getWorlds()) {
			if (!w.getEnvironment().equals(Environment.THE_END)) {
				ws.add(w);
				cl2++;
			}
		}
		int az = pl.woArenaSize + pl.clBorder;
		Location l1 = new Location(pl.getServer().getWorlds().get(0), (0 - az), 64, (0 - az));
		Location l2 = new Location(pl.getServer().getWorlds().get(0), (0 + az), 64, (0 + az));
		for (int i = l1.getChunk().getX(); i <= l2.getChunk().getZ(); i++) {
			for (int o = l1.getChunk().getX(); o <= l2.getChunk().getZ(); o++) {
				cs.add(i + "|" + o);
			}
		}
		chunkloaderBoard(p);
		clSysId = pl.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			@Override
			public void run() {
				final Player t = pl.getServer().getPlayer(uuid);
				for (int i = 0; i <= pl.clTask; i++) {
					if (clWP >= cs.size()) {
						if (t != null) {
							if (t.isOnline()) {
								tj.add(System.currentTimeMillis() - sysTimeJob);
								sysTimeJob = System.currentTimeMillis();
								Function.sendActionBarPlayerMessages(t, "§1ChunkLoader>§2 Job§6 §l" + cl1 + "§2 Completed!");
								t.playSound(t.getLocation(), Sound.ORB_PICKUP, 10f, 1.7f);
								chunkloaderBoard(t);
							}
						}
						ws.get(cl1 - 1).save();
						if (cl1 == cl2) {
							pl.getServer().getScheduler().cancelTask(clSysId);
							if (t != null) {
								if (t.isOnline()) {
									if (pl.clShd) {
										StringBuilder sb = new StringBuilder();
										sb.append("§8§m--------------------------------------------§r");
										sb.append("\n §lCHUNKLOADER§r\n \n");
										sb.append(" §aChunkloader is now completed!§r\n \n");
										sb.append(" §6Task:§4\n");
										int j = 1;
										for (long ti : tj) {
											sb.append(" §e" + j + ":§7 World '§e" + ws.get(j - 1).getName() + "§7' Completed in '§c" + Function.getTimeFormat(ti + "") + "§7'§r\n");
											j++;
										}
										sb.append(" §6Total Time: §c" + Function.getTimeFormat((System.currentTimeMillis() - sysTime) + "") + "§r\n \n");
										sb.append("§8§m--------------------------------------------§r");
										t.sendMessage(sb.toString());
									} else {
										t.sendMessage("§9ChunkLoader>§7 Chunkloader is now completed!");
									}
									t.playSound(t.getLocation(), Sound.LEVEL_UP, 5, 1f);
									pl.getLogger().info("ChunkLoader> Completed!");
								}
							}
							pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
								@Override
								public void run() {
									if (t != null) {
										if (t.isOnline()) {
											t.setScoreboard(pl.getServer().getScoreboardManager().getMainScoreboard());
										}
									}
									ws.clear();
									cs.clear();
									cl1 = 1;
									cl2 = 0;
									clTP = 0;
									clWP = 0;
									taskPs.clear();
									tj.clear();
									uuid = null;
									clMode = false;
								}
							}, (20 * 5));
							break;
						} else {
							cl1++;
							clWP = 0;
							break;
						}
					}
					String[] clID = cs.get(clWP).split("\\|");
					ws.get(cl1 - 1).loadChunk(Integer.parseInt(clID[0]), Integer.parseInt(clID[1]), true);
					clWP++;
					clTP++;
				}
				if (clTP % 250 == 0) {
					ws.get(cl1 - 1).save();
				}
				if (clTP % 5 == 0) {
					if (t != null) {
						if (t.isOnline()) {
							chunkloaderBoard(t);
						}
					}
				}
			}
		}, (20 * 10), pl.clTick);
	}

	/**
	 * <b>~ ChunkLoader Progress Scoreboard ~</b>
	 * <p>
	 * Update ChunkLoader scoreboard progress and display to selected player.
	 * </p>
	 * 
	 * @param p = Selected player
	 */
	private void chunkloaderBoard(Player p) {
		if (sb.getObjective("c") != null) {
			sb.getObjective("c").unregister();
		}
		Objective obj = sb.registerNewObjective("c", "dummy");
		obj.setDisplayName("§lCHUNKLOADER§r");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		float a = (float) ((float) (clTP + .0) / (cs.size() * ws.size()) + .0) * 100;
		Score s = obj.getScore(" ");
		s.setScore(6);
		s = obj.getScore("§6§lTask");
		s.setScore(5);
		s = obj.getScore("§7" + cl1 + "/" + cl2);
		s.setScore(4);
		s = obj.getScore("  ");
		s.setScore(3);
		s = obj.getScore("§6§lProgress");
		s.setScore(2);
		s = obj.getScore("§7" + clDF.format(a) + "%");
		s.setScore(1);
		p.setScoreboard(sb);
	}
}