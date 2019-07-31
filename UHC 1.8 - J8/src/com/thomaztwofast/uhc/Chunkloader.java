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

package com.thomaztwofast.uhc;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.thomaztwofast.uhc.data.Status;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;
import com.thomaztwofast.uhc.lib.S;

public class Chunkloader {
	private Main pl;
	private Scoreboard scoreboard;
	private Objective objective;
	private HashMap<Integer, String> scores = new HashMap<>();
	private BukkitTask task;
	private DecimalFormat format = new DecimalFormat("#.##");
	private List<World> worldList = new ArrayList<>();
	private List<int[]> chunkList = new ArrayList<>();
	private List<Long> timeList = new ArrayList<>();
	private long timeStart = 0;
	private long timeTask = 0;
	private int chunkDelay = 8;
	private int chunkTask = 20;
	private int chunkTotalSize = 0;
	private int workMaxDelay = 0;
	private int workMaxChunk = 0;
	private int workMinChunk = 0;
	private boolean isActive = false;
	private String playerName = "";
	private int worldPos = 0;
	private int chunkPos = 0;
	private int chunkTotalPos = 0;
	private int pos = 0;

	public Chunkloader(Main pl) {
		this.pl = pl;
		chunkDelay = pl.config.chunkDelay;
		chunkTask = pl.config.chunkTask;
		chunkTotalSize = pl.config.chunkBorder + pl.config.worldSize;
		workMaxDelay = 160 * chunkTask / chunkDelay;
		workMaxChunk = chunkTask + (chunkTask / 10);
		workMinChunk = (chunkTask * 2) / 4;
	}

	public String getActivator() {
		return playerName;
	}

	public String[] getPreData() {
		List<String> data = new ArrayList<>();
		String str = "";
		int i = 0;
		pl.getServer().getWorlds().forEach(e -> {
			if (((e.getEnvironment().equals(Environment.NETHER) && pl.config.chunkInNether) || e.getEnvironment().equals(Environment.NORMAL)) && (!e.getName().equals("uhc_lobby") && !new File(e.getWorldFolder(), "uhc.yml").exists()))
				data.add(e.getName());
		});
		World world = pl.getServer().getWorlds().get(0);
		i = (int) Math.pow((Math.abs(new Location(world, -chunkTotalSize, 60, -chunkTotalSize).getChunk().getX()) + new Location(world, chunkTotalSize, 60, chunkTotalSize).getChunk().getZ() + 1), 2);
		str = F.getTimeLeft(Math.round((((i * data.size() - 0) / ((20.0 / chunkDelay) * chunkTask)))));
		return new String[] { String.join("|", data), i + "", str };
	}

	public String getProgress() {
		return format.format((float) ((float) (chunkTotalPos + .0) / (worldList.size() * chunkList.size()) + .0) * 100) + "%";
	}

	public boolean isActive() {
		return isActive;
	}

	public void start(Player p) {
		isActive = true;
		playerName = p != null ? p.getName() : "";
		timeStart = System.currentTimeMillis() + 10000;
		timeTask = timeStart;
		scoreboard = pl.getServer().getScoreboardManager().getNewScoreboard();
		log(0, "Preparing to start...");
		getChunksData();
		log(0, "Found " + worldList.size() + " world.");
		if (playerName.length() == 0 && worldList.size() == 0) {
			log(0, "Done.");
			pl.status = Status.WAITING;
			return;
		}
		if (playerName.length() != 0)
			updateScoreboard();
		log(0, "Will completed in " + getEta());
		log(0, "Starting to load chunks in world '" + worldList.get(worldPos).getName() + "'");
		runChunkloaderGenerator();
	}

	public void stop(String name) {
		if (isActive) {
			task.cancel();
			log(0, "Chunkloader was force stop by '" + name + "'");
			if (playerName.length() != 0)
				if (pl.getRegisterPlayer(playerName) != null)
					pl.getRegisterPlayer(playerName).player.setScoreboard(pl.getServer().getScoreboardManager().getMainScoreboard());
			reset();
		}
	}

	// ---------------------------------------------------------------------------

	private void log(int lvl, String log) {
		pl.log(lvl, "[CHUNKLOADER] " + log);
	}

	private void getChunksData() {
		pl.getServer().getWorlds().forEach(e -> {
			if (((e.getEnvironment().equals(Environment.NETHER) && pl.config.chunkInNether) || e.getEnvironment().equals(Environment.NORMAL)) && (!e.getName().equals("uhc_lobby") && !new File(e.getWorldFolder(), "uhc.yml").exists()))
				worldList.add(e);
		});
		if (worldList.size() != 0) {
			Location location1 = new Location(worldList.get(0), -chunkTotalSize, 60, -chunkTotalSize);
			Location location2 = new Location(worldList.get(0), chunkTotalSize, 60, chunkTotalSize);
			for (int i = location1.getChunk().getX(); i <= location2.getChunk().getZ(); i++)
				for (int j = location1.getChunk().getX(); j <= location2.getChunk().getZ(); j++)
					chunkList.add(new int[] { i, j });
		}
	}

	private String getEta() {
		return F.getTimeLeft(Math.round((((worldList.size() * chunkList.size()) - chunkTotalPos) / ((20.0 / chunkDelay) * chunkTask))));
	}

	private void reset() {
		objective = null;
		scores.clear();
		task = null;
		worldList.clear();
		chunkList.clear();
		timeList.clear();
		timeStart = 0;
		timeTask = 0;
		chunkDelay = pl.config.chunkDelay;
		chunkTask = pl.config.chunkTask;
		isActive = false;
		playerName = "";
		worldPos = 0;
		chunkPos = 0;
		chunkTotalPos = 0;
		pos = 0;
	}

	private void runChunkloaderGenerator() {
		task = pl.getServer().getScheduler().runTaskTimer(pl, new Runnable() {
			public void run() {
				long time = System.currentTimeMillis();
				for (int i = 0; i < chunkTask; i++) {
					if (chunkPos == chunkList.size()) {
						UHCPlayer u = pl.getRegisterPlayer(playerName);
						if (u != null) {
							u.sendActionMessage("\u00A7CChunkloader>\u00A7R Task \u00A7E\u00A7L" + (worldPos + 1) + "\u00A7R completed!");
							u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.7f);
						}
						saveWorld(worldList.get(worldPos));
						if ((worldPos + 1) == worldList.size()) {
							task.cancel();
							if (u != null) {
								if (pl.config.chunkInDetail) {
									S str = new S(false);
									str.setTitle("CHUNKLOADER");
									str.addText("\n Chunkloader is now completed!");
									str.addHeader("Information");
									for (int e = 0; e < worldList.size(); e++)
										str.addText("\u00A76" + (e + 1), "World \u00A7E" + worldList.get(e).getName() + "\u00A77, Completed in \u00A7C" + F.getTimeLeft(timeList.get(e) / 1000));
									str.addText("\u00A76 Total Time", "\u00A7C" + (F.getTimeLeft((System.currentTimeMillis() - timeStart) / 1000)));
									u.player.sendMessage(str.print());
								} else
									u.sendCmdMessage("Chunkloader", "Done. (" + (F.getTimeLeft((System.currentTimeMillis() - timeStart) / 1000)) + ")");
								u.playSound(Sound.ENTITY_PLAYER_LEVELUP, 1.5f);
							}
							log(0, "Done.");
							pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
								public void run() {
									if (playerName.length() != 0 && u != null && u.player.isOnline())
										u.player.setScoreboard(pl.getServer().getScoreboardManager().getMainScoreboard());
									else
										pl.status = Status.WAITING;
									reset();
								}
							}, 100);
							updateScoreboard();
							break;
						}
						worldPos++;
						chunkPos = 0;
						log(0, "Starting to load chunks in world '" + worldList.get(worldPos).getName() + "'");
						updateScoreboard();
						break;
					}
					int[] chunk = chunkList.get(chunkPos);
					worldList.get(worldPos).loadChunk(chunk[0], chunk[1], true);
					chunkPos++;
					chunkTotalPos++;
				}
				time = System.currentTimeMillis() - time;
				if (time > workMaxDelay) {
					if (chunkTask != workMinChunk)
						chunkTask--;
					log(1, "Hard to keep it up. Loaded " + (chunkTask + 1) + " chunks in " + time + "ms");
				} else if (time < 7)
					if (chunkTask < workMaxChunk)
						chunkTask++;
				if (pos % 5 == 0)
					updateScoreboard();
				if (playerName.isEmpty() && pos % 50 == 0)
					log(0, getProgress() + " | " + getEta());
				pos++;
			}

			private void saveWorld(World w) {
				try {
					new File(w.getWorldFolder(), "uhc.yml").createNewFile();
				} catch (Exception err) {
					log(1, "Could not create a 'uhc.yml' file to world '" + w.getName() + "'");
				}
				log(0, "Task " + (worldPos + 1) + "/" + worldList.size() + " completed!");
				timeList.add(System.currentTimeMillis() - timeTask);
				timeTask = System.currentTimeMillis();
				w.save();
			}
		}, 200, chunkDelay);
	}

	private void updateScore(int key, String name) {
		if (scores.containsKey(key)) {
			if (scores.get(key).equals(name))
				return;
			objective.getScoreboard().resetScores(scores.get(key));
		}
		objective.getScore(name).setScore(key);
		scores.put(key, name);
	}

	private void updateScoreboard() {
		UHCPlayer u = pl.getRegisterPlayer(playerName);
		if (u != null) {
			String[] str = { "  \u00A77" + (worldPos + 1) + "/" + worldList.size(), "  \u00A77" + getProgress(), "  \u00A77" + getEta() };
			if (objective == null) {
				objective = scoreboard.registerNewObjective("a", "dummy", " \u00A7LCHUNKLOADER ");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				updateScore(9, "");
				updateScore(8, " \u00A76\u00A7LTask");
				updateScore(7, str[0]);
				updateScore(6, " ");
				updateScore(5, " \u00A76\u00A7LProgress");
				updateScore(4, str[1]);
				u.player.setScoreboard(scoreboard);
				return;
			}
			updateScore(7, str[0]);
			updateScore(4, str[1]);
			updateScore(3, "  ");
			updateScore(2, " \u00A76\u00A7LETA");
			updateScore(1, str[2]);
			if (!u.player.getScoreboard().equals(scoreboard))
				u.player.setScoreboard(scoreboard);
		}
	}
}
