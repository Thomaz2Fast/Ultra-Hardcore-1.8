/*
F * Ultra Hardcore 1.8, a Minecraft survival game mode.
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

package com.thomaztwofast.uhc.custom;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.GameStatus.Stat;
import com.thomaztwofast.uhc.data.PlayerData;

/**
 * Chunkloader
 * @version 1.2.8
 */
public class ChunkLoader {
	private Main pl;
	private Scoreboard sb;
	private Objective sbObj;
	private HashMap<Integer, String> sbHis = new HashMap<>();
	private ArrayList<World> wl = new ArrayList<>();
	private ArrayList<int[]> cl = new ArrayList<>();
	private ArrayList<Long> comJob = new ArrayList<>();
	private DecimalFormat fm = new DecimalFormat("#.##");
	private BukkitTask clockID;
	private boolean run = false;
	private boolean asPlayer = false;
	private boolean offPlayer = false;
	private long sT1;
	private long sT2;
	private int tkRun = 0;
	private int tkInW = 0;
	private int tkCkInW = 0;
	private int ckPrg = 0;
	private int ticD;
	private int ticT;
	private int mxTk;
	private int mnTk;
	private int mxMs;
	private UUID uuid;
	private String ply;

	public ChunkLoader(Main main) {
		pl = main;
		ticD = main.getPlConf().c_DelayTick();
		ticT = main.getPlConf().c_Task();
		mxMs = 160 * ticT / ticD;
		mxTk = ticT + (ticT / 10);
		mnTk = (ticT * 2) / 4;
	}

	/**
	 * Is chunkloader running?
	 */
	public boolean isRunning() {
		return run;
	}

	/**
	 * Get the player how run the chunkloader.
	 */
	public String getPlayerName() {
		return ply;
	}

	/**
	 * Get the progress from the chunkloader.
	 */
	public String getProgress() {
		float a = (float) ((float) (ckPrg + .0) / (cl.size() * wl.size()) + .0) * 100;
		return fm.format(a);
	}

	/**
	 * Get the time how long the chunkloader will be completed.
	 * @return
	 */
	public String getEta() {
		return pl.getPlFun().asClockFormat(Math.round((((cl.size() * wl.size()) - ckPrg) / ((20.0 / ticD) * ticT))));
	}

	/**
	 * Start chunkloader from player
	 */
	public void start(CraftPlayer cp) {
		run = true;
		asPlayer = true;
		ply = cp.getName();
		uuid = cp.getUniqueId();
		sT1 = (System.currentTimeMillis() + 10000);
		sT2 = sT1;
		sb = pl.getServer().getScoreboardManager().getNewScoreboard();
		loadWorldAndChunks();
		updateScoreboard(0);
		startChunkLoader();
	}

	/**
	 * Start chunkloader from server
	 */
	public void startAsServer() {
		run = true;
		sT1 = (System.currentTimeMillis() + 10000);
		sT2 = sT1;
		pl.getPlLog().info("[CHUNKLOADER] Preparing to start...");
		loadWorldAndChunks();
		pl.getPlLog().info("[CHUNKLOADER] Found " + wl.size() + " world.");
		if (wl.size() != 0) {
			pl.getPlLog().info("[CHUNKLOADER] Will completed in " + getEta());
			pl.getPlLog().info("[CHUNKLOADER] Starting to load chunk in world '" + wl.get(tkInW).getName() + "'.");
			startChunkLoader();
		} else {
			pl.getPlLog().info("[CHUNKLOADER] Done");
			pl.getGame().getStatus().setStat(Stat.WAITING);
		}
	}

	// :: PRIVATE :: //

	/**
	 * Find all world and Chunk.
	 */
	private void loadWorldAndChunks() {
		for (World w : pl.getServer().getWorlds()) {
			if (!w.getEnvironment().equals(Environment.THE_END)) {
				if (!w.getName().equals("uhc_lobby")) {
					if (!new File(w.getWorldFolder(), "uhc.yml").exists()) {
						wl.add(w);
					}
				}
			}
		}
		if (wl.size() != 0) {
			int az = (pl.getPlConf().ws_ArenaSize() + pl.getPlConf().c_Border());
			Location l1 = new Location(wl.get(0), (0 - az), 64, (0 - az));
			Location l2 = new Location(wl.get(0), (0 + az), 64, (0 + az));
			for (int i = l1.getChunk().getX(); i <= l2.getChunk().getZ(); i++) {
				for (int o = l1.getChunk().getX(); o <= l2.getChunk().getZ(); o++) {
					cl.add(new int[] { i, o });
				}
			}
		}
	}

	/**
	 * Run chunkloader
	 */
	private void startChunkLoader() {
		clockID = pl.getServer().getScheduler().runTaskTimer(pl, new Runnable() {
			@Override
			public void run() {
				Long tt = System.currentTimeMillis();
				for (int i = 0; i <= ticT; i++) {
					if (tkCkInW == cl.size()) {
						if (asPlayer) {
							if (pl.getRegPlayer(uuid) != null) {
								PlayerData p = pl.getRegPlayer(uuid);
								p.sendActionMessage("§1ChunkLoader>§2 Job§6 §l" + (tkInW + 1) + "§2 Completed!");
								p.playLocalSound(Sound.ORB_PICKUP, 1.7f);
							}
						}
						createFile(wl.get(tkInW));
						pl.getPlLog().info("[CHUNKLOADER] Task " + (tkInW + 1) + "/" + wl.size() + " completed!");
						comJob.add(System.currentTimeMillis() - sT2);
						sT2 = System.currentTimeMillis();
						wl.get(tkInW).save();
						if ((tkInW + 1) == wl.size()) {
							pl.getServer().getScheduler().cancelTask(clockID.getTaskId());
							if (asPlayer) {
								if (pl.getRegPlayer(uuid) != null) {
									PlayerData p = pl.getRegPlayer(uuid);
									if (pl.getPlConf().c_ShowHidden()) {
										String s = "§8§m--------------------------------------------§r\n";
										s += " §lCHUNKLOADER§r\n \n";
										s += " §aChunkloader is now completed!§r\n \n";
										s += " §6Information§4\n";
										for (int ii = 0; ii < wl.size(); ii++) {
											s += "  §6" + (ii + 1) + ":§7 World '§e" + wl.get(ii).getName() + "§7', Completed in '§c" + pl.getPlFun().asClockFormat((comJob.get(ii) / 1000)) + "§7'§r\n";
										}
										s += "  §6Total Time:§c " + pl.getPlFun().asClockFormat(((System.currentTimeMillis() - sT1) / 1000)) + "§r\n";
										s += "§8§m--------------------------------------------§r";
										p.cp.sendMessage(s);
									} else {
										p.sendMessage("ChunkLoader", "Chunkloader is now completed!");
									}
									p.playLocalSound(Sound.LEVEL_UP, 1f);
								}
							}
							pl.getPlLog().info("[CHUNKLOADER] Completed!");
							pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
								@Override
								public void run() {
									if (asPlayer) {
										if (pl.getRegPlayer(uuid) != null) {
											PlayerData p = pl.getRegPlayer(uuid);
											p.cp.setScoreboard(pl.getServer().getScoreboardManager().getMainScoreboard());
										}
									}
									wl.clear();
									cl.clear();
									tkInW = 0;
									tkCkInW = 0;
									ckPrg = 0;
									ticT = pl.getPlConf().c_Task();
									tkRun = 0;
									comJob.clear();
									ply = null;
									uuid = null;
									run = false;
									sbObj = null;
									sbHis.clear();
									offPlayer = false;
									if (!asPlayer) {
										pl.getGame().getStatus().setStat(Stat.WAITING);
									}
									asPlayer = false;
								}
							}, (20 * 5));
							updateScoreboard(2);
							break;
						}
						tkInW++;
						tkCkInW = 0;
						updateScoreboard(2);
						break;
					}
					int[] cid = cl.get(tkCkInW);
					wl.get(tkInW).loadChunk(cid[0], cid[1], true);
					tkCkInW++;
					ckPrg++;
				}
				long ms = (System.currentTimeMillis() - tt);
				if (ms > mxMs) {
					if (ticT != mnTk) {
						pl.getPlLog().warn("[CHUNKLOADER] Hard to keep it up. Loaded " + ticT + " chunks in " + ms + "ms");
						ticT--;
					}
				} else if (ms < 7) {
					if (ticT < mxTk) {
						ticT++;
					}
				}
				if (tkRun % 5 == 0) {
					updateScoreboard(1);
				}
				if (!asPlayer) {
					if (tkRun % 50 == 0) {
						pl.getPlLog().info("[CHUNKLOADER] " + getProgress() + "%");
					}
				}
				tkRun++;
			}
		}, (20 * 10), ticD);
	}

	/**
	 * Creating a chunkloader file in the world folder.
	 */
	private void createFile(World world) {
		try {
			File f = new File(world.getWorldFolder(), "uhc.yml");
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update scoreboard.
	 */
	private void updateScoreboard(int i) {
		if (asPlayer) {
			if (pl.getRegPlayer(uuid) != null) {
				PlayerData p = pl.getRegPlayer(uuid);
				String tk = "§7" + (tkInW + 1) + "/" + wl.size();
				String prg = "§7" + getProgress() + "%";
				String eta = "§7" + getEta();
				if (i == 0) {
					if (sbObj == null) {
						sbObj = sb.registerNewObjective("c", "dummy");
						sbObj.setDisplayName("§lCHUNKLOADER§r");
						sbObj.setDisplaySlot(DisplaySlot.SIDEBAR);
						Score s = sbObj.getScore(" ");
						s.setScore(9);
						s = sbObj.getScore("§6§lTask");
						s.setScore(8);
						s = sbObj.getScore(tk);
						s.setScore(7);
						sbHis.put(7, tk);
						s = sbObj.getScore("  ");
						s.setScore(6);
						s = sbObj.getScore("§6§lProgress");
						s.setScore(5);
						s = sbObj.getScore(prg);
						s.setScore(4);
						sbHis.put(4, prg);
						p.cp.setScoreboard(sb);
					}
				} else {
					sb.resetScores(sbHis.get(4));
					Score s = sbObj.getScore(prg);
					s.setScore(4);
					sbHis.put(4, prg);
					if (sbHis.get(1) != null) {
						sb.resetScores(sbHis.get(1));
					} else {
						s = sbObj.getScore("   ");
						s.setScore(3);
						s = sbObj.getScore("§6§lETA");
						s.setScore(2);
					}
					s = sbObj.getScore(eta);
					s.setScore(1);
					sbHis.put(1, eta);
					if (offPlayer) {
						offPlayer = false;
						i = 2;
						p.cp.setScoreboard(sb);
					}
					if (i == 2) {
						sb.resetScores(sbHis.get(7));
						s = sbObj.getScore(tk);
						s.setScore(7);
						sbHis.put(7, tk);
					}
				}
				return;
			}
			if (!offPlayer) {
				offPlayer = true;
			}
		}
	}
}