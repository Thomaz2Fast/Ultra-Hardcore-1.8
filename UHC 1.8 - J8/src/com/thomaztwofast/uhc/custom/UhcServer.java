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

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Difficulty;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import com.thomaztwofast.uhc.GameManager;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.PlayerData;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.GameStatus.Stat;

public class UhcServer {
	private Main pl;
	private Server s;
	private HashMap<Integer, String> objHis = new HashMap<>();
	private Config c;
	private GameManager gm;
	private Scoreboard sb;
	private Objective obj;
	private World lobby;
	private boolean isStarting = false;
	private boolean isStartingOp = false;
	private BukkitTask sTimer;
	private int countdown = 30;
	private int reset = 15;

	public UhcServer(Main main) {
		pl = main;
		s = main.getServer();
		c = main.getPlConf();
		gm = main.getGame();
		sb = main.getServer().getScoreboardManager().getMainScoreboard();
		onStartChunkLoader();
		onStartLobby();
		updateWaitingScoreboard(new int[] { 1 });
	}

	/**
	 * Get > Lobby world
	 */
	public World getLobby() {
		return lobby;
	}

	/**
	 * UHC Server > Unload
	 */
	public void unLoad() {
		kickAllPlayers(pl.getServer().getOnlinePlayers(), false, false);
		if (sb.getObjective("uhc_wlog") != null) {
			obj.unregister();
		}
		if (s.getWorld("uhc_lobby") != null) {
			s.unloadWorld(lobby.getName(), false);
		}
	}

	/**
	 * UHC Server > Force start a game from command
	 */
	public void forceStart(String n) {
		if (!isStarting | !isStartingOp) {
			isStartingOp = true;
			startCountdown();
			gm.broadcastMessage("§9Game>§e " + n + "§7 has started the game.");
		}
	}

	/**
	 * UHC Server > New Player
	 */
	public void newPlayer(PlayerData p) {
		p.cp.teleport(lobby.getSpawnLocation().add(0.5, 0, 0.5));
		p.clearPlayer(true);
		gm.givePlayerHubItems(p);
		updateWaitingScoreboard(new int[] { 2 });
		if (!isStarting) {
			if (enoughToStart()) {
				startCountdown();
			}
		}
	}

	/**
	 * UHC Server > Remove Player
	 */
	public void removePlayer(PlayerData p) {
		if (c.g_teamMode()) {
			if (sb.getEntryTeam(p.cp.getName()) != null) {
				sb.getEntryTeam(p.cp.getName()).removeEntry(p.cp.getName());
				gm.getTeam().updateInventory();
			}
		}
		updateWaitingScoreboard(new int[] { 2 });
	}

	/**
	 * Check if there is enough players / teams
	 */
	public void canStart() {
		if (!isStarting) {
			if (enoughToStart()) {
				startCountdown();
			}
		}
	}

	/**
	 * UHC Server > Server Restart
	 */
	public void serverRestart() {
		if (c.damageLog()) {
			reset = 60;
		}
		s.getScheduler().runTaskLater(pl, new Runnable() {

			@Override
			public void run() {
				gm.broadcastMessage("§9Server>§7 Server is about to restart in ca " + reset + " seconds.");
			}
		}, 20 * 5);
		s.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				kickAllPlayers(pl.getServer().getOnlinePlayers(), c.server_BungeeCord(), true);
			}
		}, 20 * reset);
		s.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				s.shutdown();
			}
		}, 20 * (reset + 5));
	}

	// :: PRIVATE :: //

	/**
	 * Chunkloader > Start
	 */
	private void onStartChunkLoader() {
		ChunkLoader cl = new ChunkLoader(pl);
		cl.startAsServer();
	}

	/**
	 * UHC Server > Load lobby world
	 */
	private void onStartLobby() {
		lobby = s.createWorld(WorldCreator.name("uhc_lobby"));
		lobby.setDifficulty(Difficulty.PEACEFUL);
		lobby.setPVP(false);
		lobby.setGameRuleValue("doDaylightCycle", "false");
		lobby.setGameRuleValue("keepInventory", "false");
		lobby.setGameRuleValue("randomTickSpeed", "0");
		lobby.setTime(6000);
		lobby.setSpawnFlags(false, false);
		lobby.setAutoSave(false);
	}

	/**
	 * UHC Server > Update scoreboard
	 * @param id
	 */
	private void updateWaitingScoreboard(int[] id) {
		Score sc;
		if (obj == null) {
			if (sb.getObjective("uhc_wlog") != null) {
				sb.getObjective("uhc_wlog").unregister();
			}
			obj = sb.registerNewObjective("uhc_wlog", "dummy");
			obj.setDisplayName("§c§lUHC-" + c.server_ID() + "§8§l : §e§l" + (c.g_teamMode() ? "Team" : "Solo") + " Mode");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			sc = obj.getScore(" ");
			sc.setScore(6);
			sc = obj.getScore("§6§lStatus");
			sc.setScore(5);
			sc = obj.getScore("  ");
			sc.setScore(3);
			sc = obj.getScore("§6§lPlayers");
			sc.setScore(2);
		}
		if (id != null) {
			for (int i : id) {
				if (objHis.containsKey(i)) {
					sb.resetScores(objHis.get(i));
				}
				switch (i) {
				case 1:
					boolean b = false;
					if (gm.getStatus().getStat().equals(Stat.WAITING_STARTING)) {
						b = true;
					}
					sc = obj.getScore((b ? "§a§oStarting in §6§o" + countdown + "§a§o second" + (countdown != 1 ? "s" : "") : "§7§oWaiting for players..."));
					sc.setScore(4);
					objHis.put(1, sc.getEntry());
					break;
				case 2:
					sc = obj.getScore("§7" + pl.getRegPlayerData().size() + " / " + s.getMaxPlayers());
					sc.setScore(1);
					objHis.put(2, sc.getEntry());
					break;
				}
			}
		}
	}

	/**
	 * Enough to start?
	 * @return
	 */
	private boolean enoughToStart() {
		if (c.g_teamMode()) {
			int i = 0;
			for (Team t : sb.getTeams()) {
				if (t.getSize() != 0) {
					i++;
				}
			}
			if (i >= c.server_MinTeamToStart()) {
				return true;
			} else if (isStartingOp & i > 1) {
				return true;
			}
			return false;
		}
		if (pl.getRegPlayerData().size() >= c.server_MinPlayerToStart()) {
			return true;
		} else if (isStartingOp & pl.getRegPlayerData().size() > 1) {
			return true;
		}
		return false;
	}

	/**
	 * UHC Server > Countdown
	 */
	private void startCountdown() {
		isStarting = true;
		gm.getStatus().setStat(Stat.WAITING_STARTING);
		countdown = c.server_Countdown();
		sTimer = s.getScheduler().runTaskTimer(pl, new Runnable() {
			@Override
			public void run() {
				if (enoughToStart()) {
					if (countdown == 0) {
						s.getScheduler().cancelTask(sTimer.getTaskId());
						obj.unregister();
						gm.startUhcGame(getEntityNumber());
					} else {
						if (c.g_teamMode()) {
							if (countdown % 10 == 0) {
								double d = Math.random();
								for (PlayerData p : pl.getRegPlayerData().values()) {
									if (p.getPlayerTeam() == null) {
										if (d < 0.55) {
											p.sendActionMessage("§6§lRemember to select a team");
										} else {
											p.sendActionMessage("§6§lYou are join as Spectator Mode");
										}
									}
								}
							}
						}
						updateWaitingScoreboard(new int[] { 1 });
						countdown--;
					}
				} else {
					s.getScheduler().cancelTask(sTimer.getTaskId());
					isStarting = false;
					isStartingOp = false;
					gm.getStatus().setStat(Stat.WAITING);
					updateWaitingScoreboard(new int[] { 1, 2 });
				}
			}
		}, 20, 20);
	}

	/**
	 * Get how many players / team there are in the game
	 */
	private int getEntityNumber() {
		int i = 0;
		if (c.g_teamMode()) {
			for (Team t : sb.getTeams()) {
				if (t.getSize() != 0) {
					i++;
				}
			}
			return i;
		}
		return pl.getRegPlayerData().size();
	}

	/**
	 * UHC Server > Kick all players
	 */
	private void kickAllPlayers(Collection<? extends Player> collection, boolean b, boolean m) {
		if (collection.size() != 0) {
			for (Player p : collection) {
				if (b) {
					pl.getRegPlayer(p.getUniqueId()).TpFallBackServer();
				} else {
					if (m) {
						p.kickPlayer(c.server_KickMessage_PlayerKickGameEnd());
					} else {
						p.kickPlayer(c.server_KickMessage_PlayerKickRestart());
					}
				}
			}
		}
	}
}