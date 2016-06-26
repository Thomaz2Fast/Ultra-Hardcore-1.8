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

package com.thomaztwofast.uhc.custom;

import java.io.File;
import java.io.IOException;
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

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.GameStatus;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class ChunkLoader extends Function {
	private Main cA;
	private Scoreboard cBa;
	private Objective cBb;
	private HashMap<Integer, String> cBc = new HashMap<>();
	private List<World> cCa = new ArrayList<>();
	private List<int[]> cCb = new ArrayList<>();
	private List<Long> cCc = new ArrayList<>();
	private DecimalFormat cD = new DecimalFormat("#.##");
	private BukkitTask cE;
	private boolean cFa = false;
	private boolean cFb = false;
	private boolean cFc = false;
	private long cFd = 0;
	private long cFe = 0;
	private int cFf = 8;
	private int cFg = 20;
	private int cFh = 0;
	private int cFi = 0;
	private int cFj = 0;
	private int cFk = 0;
	private int cGa = 0;
	private int cGb = 0;
	private int cGc = 0;
	private String cHa;

	public ChunkLoader(Main a) {
		cA = a;
		cFf = cA.mC.cDb;
		cFg = cA.mC.cDc;
		cGa = 160 * cFg / cFf;
		cGb = cFg + (cFg / 10);
		cGc = (cFg * 2) / 4;
	}

	public boolean isRunning() {
		return cFa;
	}

	public String getPlayer() {
		return cHa;
	}

	public String getProgress() {
		return cD.format((float) ((float) (cFj + .0) / (cCb.size() * cCa.size()) + .0) * 100) + "%";
	}

	public String[] getChunkData() {
		String[] a = new String[3];
		String b = "";
		lD();
		for (World c : cCa) {
			if (b.length() == 0) {
				b = c.getName();
			} else {
				b += "|" + c.getName();
			}
		}
		a[0] = b;
		a[1] = "" + cCb.size();
		a[2] = getEta();
		resetSettings();
		return a;
	}

	public String getEta() {
		return asClock(Math.round((((cCb.size() * cCa.size()) - cFj) / ((20.0 / cFf) * cFg))));
	}

	public void start(Player a) {
		cFa = true;
		if (a != null) {
			cFb = true;
			cHa = a.getName();
		}
		cFd = System.currentTimeMillis() + 10000;
		cFe = cFd;
		cBa = cA.getServer().getScoreboardManager().getNewScoreboard();
		log("Preparing to start...", false);
		lD();
		log("Found " + cCa.size() + " world.", false);
		if (cFb) {
			updateScoreboard();
		}
		if (!cFb && cCa.size() == 0) {
			log("Done.", false);
			cA.mA = GameStatus.WAITING;
			return;
		} else {
			log("Will completed in " + getEta(), false);
			log("Starting to load chunks in world '" + (cFh + 1) + "'", false);
		}
		startChunkloader();
	}

	public void stop() {
		if (cFa) {
			cE.cancel();
			log("Stopped!", false);
			if (cFb) {
				if (cA.mB.getPlayer(cHa) != null) {
					cA.mB.getPlayer(cHa).uB.setScoreboard(cA.getServer().getScoreboardManager().getMainScoreboard());
				}
			}
			resetSettings();
		}
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private void lD() {
		for (World a : cA.getServer().getWorlds()) {
			if (!a.getEnvironment().equals(Environment.THE_END)) {
				if (!a.getName().equals("uhc_lobby")) {
					if (!new File(a.getWorldFolder(), "uhc.yml").exists()) {
						if (a.getEnvironment().equals(Environment.NORMAL) || (cA.mC.cDe && a.getEnvironment().equals(Environment.NETHER))) {
							cCa.add(a);
						}
					}
				}
			}
		}
		if (cCa.size() != 0) {
			int a = (cA.mC.cIb + cA.mC.cDa);
			Location b = new Location(cCa.get(0), -a, 64, -a);
			Location c = new Location(cCa.get(0), a, 64, a);
			for (int d = b.getChunk().getX(); d <= c.getChunk().getZ(); d++) {
				for (int e = b.getChunk().getX(); e <= c.getChunk().getZ(); e++) {
					cCb.add(new int[] { d, e });
				}
			}
		}
	}

	private void startChunkloader() {
		cE = cA.getServer().getScheduler().runTaskTimer(cA, new Runnable() {
			@Override
			public void run() {
				long a = System.currentTimeMillis();
				for (int b = 0; b <= cFg; b++) {
					if (cFi == cCb.size()) {
						if (cFb) {
							if (cA.mB.getPlayer(cHa) != null) {
								UHCPlayer c = cA.mB.getPlayer(cHa);
								c.sendActionMessage("\u00A7cChunkLoader>\u00A77 Task\u00A7e " + (cFh + 1) + "\u00A77 completed!");
								c.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.7f);
							}
						}
						cf(cCa.get(cFh));
						if ((cFh + 1) == cCa.size()) {
							cE.cancel();
							if (cFb) {
								if (cA.mB.getPlayer(cHa) != null) {
									UHCPlayer c = cA.mB.getPlayer(cHa);
									if (cA.mC.cDd) {
										c.uB.sendMessage(sh());
									} else {
										c.sendCommandMessage("ChunkLoader", "Done. (" + asClock((System.currentTimeMillis() - cFd) / 1000) + ")");
									}
									c.playLocalSound(Sound.ENTITY_PLAYER_LEVELUP, 1f);
								}
							}
							log("Completed!", false);
							cA.getServer().getScheduler().runTaskLater(cA, new Runnable() {
								@Override
								public void run() {
									if (cFb) {
										if (cA.mB.getPlayer(cHa) != null) {
											UHCPlayer c = cA.mB.getPlayer(cHa);
											c.uB.setScoreboard(cA.getServer().getScoreboardManager().getMainScoreboard());
										}
									} else {
										cA.mA = GameStatus.WAITING;
									}
									resetSettings();
								}
							}, 100);
							updateScoreboard();
							break;
						}
						cFh++;
						cFi = 0;
						updateScoreboard();
						break;
					}
					int[] d = cCb.get(cFi);
					cCa.get(cFh).loadChunk(d[0], d[1], true);
					cFi++;
					cFj++;
				}
				a = System.currentTimeMillis() - a;
				if (a > cGa) {
					if (cFg != cGc) {
						cFg--;
					}
					log("Hard to keep it up. Loaded " + (cFg + 1) + " chunks in " + a + "ms", true);
				} else if (a < 7) {
					if (cFg < cGb) {
						cFg++;
					}
				}
				if (cFk % 5 == 0) {
					updateScoreboard();
				}
				if (!cFb && cFk % 50 == 0) {
					log(getProgress(), false);
				}
				cFk++;
			}

			// Show hidden info.
			private String sh() {
				StringBuilder a = new StringBuilder();
				a.append("\u00A78\u00A7m--------------------------------------------\u00A7r\n");
				a.append("\u00A7l CHUNKLOADER\n \n");
				a.append("\u00A7a Chunkloader is now completed!\n \n");
				a.append("\u00A76 Information\n");
				for (int b = 0; b < cCa.size(); b++) {
					a.append("\u00A76  " + (b + 1) + ":\u00A77 World '\u00A7e" + cCa.get(b).getName() + "\u00A77', Completed in '\u00A7c" + asClock(cCc.get(b) / 1000) + "\u00A77'\n");
				}
				a.append("\u00A76  Total Time:\u00A7c " + asClock((System.currentTimeMillis() - cFd) / 1000) + "\n");
				a.append("\u00A78\u00A7m--------------------------------------------");
				return a.toString();
			}

			// Save world.
			private void cf(World a) {
				try {
					new File(a.getWorldFolder(), "uhc.yml").createNewFile();
				} catch (IOException e) {
					log("ERROR! " + e.getMessage(), true);
				}
				log("Task " + (cFh + 1) + " / " + cCa.size() + " completed!", false);
				cCc.add(System.currentTimeMillis() - cFe);
				cFe = System.currentTimeMillis();
				a.save();
			}
		}, 200, cFf);
	}

	private void resetSettings() {
		cBb = null;
		cBc.clear();
		cCa.clear();
		cCb.clear();
		cCc.clear();
		cE = null;
		cFa = false;
		cFb = false;
		cFc = false;
		cFd = 0;
		cFe = 0;
		cFf = cA.mC.cDb;
		cFg = cA.mC.cDc;
		cFh = 0;
		cFi = 0;
		cFj = 0;
		cFk = 0;
		cHa = null;
	}

	private void log(String a, boolean b) {
		cA.log((b ? 1 : 0), "[CHUNKLOADER] " + a);
	}

	private void updateScoreboard() {
		if (cFb) {
			if (cA.mB.getPlayer(cHa) != null) {
				UHCPlayer a = cA.mB.getPlayer(cHa);
				String b = "\u00A77" + (cFh + 1) + "/" + cCa.size();
				String c = "\u00A77" + getProgress();
				String d = "\u00A77" + getEta();
				if (cBb == null) {
					cBb = cBa.registerNewObjective("c", "dummy");
					cBb.setDisplayName("\u00A7lCHUNKLOADER\u00A7r");
					cBb.setDisplaySlot(DisplaySlot.SIDEBAR);
					setScore(9, " ");
					setScore(8, "\u00A76\u00A7lTask");
					setScore(7, b);
					setScore(6, "  ");
					setScore(5, "\u00A76\u00A7lProgress");
					setScore(4, c);
					a.uB.setScoreboard(cBa);
					return;
				}
				setScore(7, b);
				setScore(4, c);
				setScore(3, "   ");
				setScore(2, "\u00A76\u00A7lETA");
				setScore(1, d);
				if (cFc) {
					cFc = false;
					a.uB.setScoreboard(cBa);
				}
			}
			if (!cFc) {
				cFc = true;
			}
		}
	}

	private void setScore(int a, String b) {
		if (cBc.containsKey(a)) {
			if (cBc.get(a).equals(b)) {
				return;
			}
			cBb.getScoreboard().resetScores(cBc.get(a));
		}
		cBb.getScore(b).setScore(a);
		cBc.put(a, b);
	}
}
