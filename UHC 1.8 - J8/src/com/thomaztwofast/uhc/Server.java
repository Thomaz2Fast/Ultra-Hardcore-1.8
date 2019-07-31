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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.thomaztwofast.uhc.data.Status;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;

public class Server {
	private Main pl;
	private Scoreboard scoreboard;
	private Objective objective;
	private HashMap<Integer, String> scores = new HashMap<>();
	private Chunkloader chunkloader;
	private boolean[] isActive = { false, false };
	private int[] countdowns = { 30, 15 };
	private BukkitTask task;
	public World lobby;
	public ItemStack itemStack;

	public Server(Main pl) {
		this.pl = pl;
	}

	public void forceStart(String name) {
		if (!isActive[0] || !isActive[1]) {
			isActive[1] = true;
			taskCountdown();
			pl.PLAYERS.values().forEach(e -> e.sendCmdMessage("UHC", "\u00A7E\u00A7O" + name + "\u00A77\u00A7O has started the game."));
		}
	}

	public String getChunkloaderProgress() {
		if (chunkloader != null)
			return " (" + chunkloader.getProgress() + ")";
		return "";
	}

	public void load() {
		kickPlayers();
		scoreboard = pl.getServer().getScoreboardManager().getMainScoreboard();
		if (pl.config.gameInTeam)
			pl.gameManager.teams.isSelectItem = true;
		if (pl.config.serverIsBungeeCord) {
			pl.getServer().getMessenger().registerOutgoingPluginChannel(pl, "BungeeCord");
			itemStack = F.item(Material.matchMaterial(Material.valueOf(pl.config.serverMaterial) != null ? pl.config.serverMaterial : "BARRIER"), "&eReturn to " + pl.config.serverHub);
		}
		startChunkloder();
		loadLobby();
		updateScoreboard();
	}

	public void join(UHCPlayer u) {
		u.player.teleport(lobby.getSpawnLocation().add(.5, 0, .5));
		u.resetPlayer(true);
		u.getHubItems();
		updateScoreboard();
		startCountdown();
	}

	public void quit(UHCPlayer u) {
		if (pl.config.gameInTeam && scoreboard.getEntryTeam(u.player.getName()) != null) {
			scoreboard.getEntryTeam(u.player.getName()).removeEntry(u.player.getName());
			pl.gameManager.teams.updateInv();
		}
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				updateScoreboard();
			}
		}, 0);
	}

	public void shutdown() {
		if (pl.config.dmgEnable)
			countdowns[1] = 60;
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				pl.PLAYERS.values().forEach(e -> e.sendCmdMessage("Server", "Server is about to restart in ca " + countdowns[1] + " seconds."));
			}
		}, 100);
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				kickPlayers();
			}
		}, 20 * countdowns[1]);
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				pl.getServer().shutdown();
			}
		}, 20 * (countdowns[1] + 5));
	}

	public void startCountdown() {
		if (!isActive[0] && getParticipants() >= (pl.config.gameInTeam ? pl.config.serverMinTeam : pl.config.serverMinSolo))
			taskCountdown();
	}

	public void unLoad() {
		kickPlayers();
		if (scoreboard.getObjective("uhc_w") != null)
			objective.unregister();
		if (lobby != null)
			pl.getServer().unloadWorld(lobby, false);
	}

	// ---------------------------------------------------------------------------

	private void kickPlayers() {
		List<UHCPlayer> list = new ArrayList<>(pl.PLAYERS.values());
		list.forEach(e -> {
			if (pl.isEnabled() && pl.config.serverIsBungeeCord)
				e.fallbackServer(itemStack.getType());
			else
				e.player.kickPlayer(pl.status.equals(Status.FINISHED) ? pl.config.serverKickShutdown : pl.config.serverKickReset);
		});
	}

	private void loadLobby() {
		lobby = pl.getServer().createWorld(WorldCreator.name("uhc_lobby"));
		lobby.setAutoSave(false);
		lobby.setDifficulty(Difficulty.PEACEFUL);
		lobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		lobby.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		lobby.setGameRule(GameRule.KEEP_INVENTORY, true);
		lobby.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
		lobby.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		lobby.setPVP(false);
		lobby.setSpawnFlags(false, false);
		lobby.setStorm(false);
		lobby.setThundering(false);
		lobby.setTime(6000);
	}

	private int getParticipants() {
		int i = 0;
		if (pl.config.gameInTeam) {
			for (String tm : pl.config.gameTeamNames)
				if (scoreboard.getTeam(tm.split("\\|")[0].replace(" ", "_")).getSize() != 0)
					i++;
			return i;
		}
		for (UHCPlayer b : pl.PLAYERS.values())
			if (b.player.getGameMode().equals(GameMode.ADVENTURE))
				i++;
		return i;
	}

	private void startChunkloder() {
		if (!pl.config.serverActiveChunkloader) {
			chunkloader = new Chunkloader(pl);
			chunkloader.start(null);
			return;
		}
		pl.status = Status.WAITING;
	}

	private void taskCountdown() {
		isActive[0] = true;
		pl.status = Status.WAITING_STARTING;
		countdowns[0] = pl.config.serverCountdown;
		task = pl.getServer().getScheduler().runTaskTimer(pl, new Runnable() {
			public void run() {
				int size = getParticipants();
				if (size >= (pl.config.gameInTeam ? pl.config.serverMinTeam : pl.config.serverMinSolo) || (isActive[0] && size > 1)) {
					if (countdowns[0] == 0) {
						task.cancel();
						objective.unregister();
						pl.gameManager.startGame(size);
						return;
					}
					if (countdowns[0] < 6)
						pl.PLAYERS.values().forEach(e -> e.playSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1f));
					if (pl.config.gameInTeam && countdowns[0] % 10 == 0) {
						pl.PLAYERS.values().forEach(a -> {
							if (scoreboard.getEntryTeam(a.player.getName()) == null)
								a.sendActionMessage("\u00A76\u00A7L" + (Math.random() < .55 ? "Remember to select a team" : "You are join as Spectator Mode"));
						});
					}
					countdowns[0]--;
				} else {
					task.cancel();
					isActive = new boolean[] { false, false };
					pl.status = Status.WAITING;
				}
				updateScoreboard();
			}
		}, 20, 20);
	}

	private void updateScore(int value, String input) {
		if (scores.containsKey(value)) {
			if (scores.get(value).equals(input))
				return;
			scoreboard.resetScores(scores.get(value));
		}
		objective.getScore(input).setScore(value);
		scores.put(value, input);
	}

	private void updateScoreboard() {
		if (objective == null) {
			if (scoreboard.getObjective("uhc_w") != null)
				scoreboard.getObjective("uhc_w").unregister();
			objective = scoreboard.registerNewObjective("uhc_w", "dummy", "\u00A7L UHC-" + pl.config.serverID + " | " + (pl.config.gameInTeam ? "TEAM" : "SOLO") + " MODE ");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			updateScore(6, "");
			updateScore(5, "\u00A76\u00A7L Status");
			updateScore(3, " ");
			updateScore(2, "\u00A76\u00A7L Players");
		}
		updateScore(4, isActive[0] ? "\u00A7A\u00A7O Starting in \u00A7E\u00A7O" + countdowns[0] + " \u00A7A\u00A7Osecond" + (countdowns[0] != 1 ? "s" : "") : "\u00A77\u00A7O Waiting for players...");
		updateScore(1, "\u00A77\u00A7O " + pl.PLAYERS.size() + "/" + pl.getServer().getMaxPlayers());
	}
}
