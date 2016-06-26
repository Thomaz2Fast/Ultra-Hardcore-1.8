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
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.GameStatus;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class UhcServer extends Function {
	private Main uA;
	public World uB;
	private Scoreboard uCa;
	private Objective uCb;
	private HashMap<Integer, String> uCc = new HashMap<>();
	private boolean uD[] = { false, false };
	private int[] uE = { 30, 15 };
	private BukkitTask uF;
	public ItemStack uG;

	public UhcServer(Main a) {
		uA = a;
	}

	public void load() {
		kickAllPlayers(uA.getServer().getOnlinePlayers(), false, false);
		uCa = uA.getServer().getScoreboardManager().getMainScoreboard();
		if (uA.mC.cGa) {
			uA.mE.gD.uCc = true;
		}
		if (uA.mC.cFv) {
			uA.getServer().getMessenger().registerOutgoingPluginChannel(uA, "BungeeCord");
			uG = nItem(Material.matchMaterial((Material.matchMaterial(uA.mC.cFx) != null ? uA.mC.cFx : "BARRIER")), 0, "\u00A7eReturn to " + uA.mC.cFw, "");
		}
		loadChunkloader();
		loadLobby();
		updateScoreboard();
	}

	public void unLoad() {
		kickAllPlayers(uA.getServer().getOnlinePlayers(), false, false);
		if (uCa.getObjective("uhc_w") != null) {
			uCb.unregister();
		}
		if (uA.getServer().getWorld(uB.getName()) != null) {
			uA.getServer().unloadWorld(uB.getName(), false);
		}
	}

	// ------:- PUBLIC | SERVER -:------------------------------------------------------------------

	public void canStart() {
		if (!uD[0]) {
			if (getEntity() >= (uA.mC.cGa ? uA.mC.cFe : uA.mC.cFd)) {
				svCountdown();
			}
		}
	}

	public void forceStart(String a) {
		if (!uD[0] || !uD[1]) {
			uD[1] = true;
			svCountdown();
			for (UHCPlayer b : uA.mB.getAllPlayers()) {
				b.sendCommandMessage("UHC", "\u00A7e\u00A7o" + a + "\u00A77\u00A7o has started the game.");
			}
		}
	}

	public void serverRestart() {
		if (uA.mC.cOa) {
			uE[1] = 60;
		}
		uA.getServer().getScheduler().runTaskLater(uA, new Runnable() {
			@Override
			public void run() {
				for (UHCPlayer a : uA.mB.getAllPlayers()) {
					a.sendCommandMessage("Server", "Server is about to restart in ca " + uE[1] + " secounds.");
				}
			}
		}, 100);
		uA.getServer().getScheduler().runTaskLater(uA, new Runnable() {
			@Override
			public void run() {
				kickAllPlayers(uA.getServer().getOnlinePlayers(), uA.mC.cFv, true);
			}
		}, 20 * uE[1]);
		uA.getServer().getScheduler().runTaskLater(uA, new Runnable() {
			@Override
			public void run() {
				uA.getServer().shutdown();
			}
		}, 20 * (uE[1] + 5));
	}

	// ------:- PUBLIC | PLAYER -:------------------------------------------------------------------

	public void newPlayer(UHCPlayer a) {
		a.uB.teleport(uB.getSpawnLocation().add(0.5, 0, 0.5));
		a.resetPlayer(true);
		a.hubItems();
		updateScoreboard();
		canStart();
	}

	public void removePlayer(UHCPlayer a) {
		if (uA.mC.cGa) {
			if (uCa.getEntryTeam(a.uB.getName()) != null) {
				uCa.getEntryTeam(a.uB.getName()).removeEntry(a.uB.getName());
				uA.mE.gD.updateInv();
			}
		}
		uA.getServer().getScheduler().runTaskLater(uA, new Runnable() {
			public void run() {
				updateScoreboard();
			}
		}, 0);
	}

	// ------:- PRIVATE | LOAD -:-------------------------------------------------------------------

	private void loadChunkloader() {
		ChunkLoader a = new ChunkLoader(uA);
		a.start(null);
	}

	private void loadLobby() {
		uB = uA.getServer().createWorld(WorldCreator.name("uhc_lobby"));
		uB.setDifficulty(Difficulty.PEACEFUL);
		uB.setPVP(false);
		uB.setGameRuleValue("doDaylightCycle", "false");
		uB.setGameRuleValue("keepInventory", "true");
		uB.setGameRuleValue("randomTickSpeed", "0");
		uB.setTime(6000);
		uB.setSpawnFlags(false, false);
		uB.setAutoSave(false);
	}

	// ------:- PRIVATE | GET | START | KICK -:-----------------------------------------------------

	private int getEntity() {
		int a = 0;
		if (uA.mC.cGa) {
			for (String b : uA.mE.gD.uCa) {
				if (uCa.getTeam(b).getSize() != 0) {
					a++;
				}
			}
		} else {
			for (UHCPlayer b : uA.mB.getAllPlayers()) {
				if (b.uB.getGameMode().equals(GameMode.ADVENTURE)) {
					a++;
				}
			}
		}
		return a;
	}

	private void svCountdown() {
		uD[0] = true;
		uA.mA = GameStatus.WAITING_STARTING;
		uE[0] = uA.mC.cFf;
		uF = uA.getServer().getScheduler().runTaskTimer(uA, new Runnable() {
			public void run() {
				if (getEntity() >= (uA.mC.cGa ? uA.mC.cFe : uA.mC.cFd) || (uD[1] && getEntity() > 1)) {
					if (uE[0] == 0) {
						uF.cancel();
						uCb.unregister();
						uA.mE.gmStart(getEntity());
					} else {
						if (uA.mC.cGa) {
							if (uE[0] % 10 == 0) {
								for (UHCPlayer b : uA.mB.getAllPlayers()) {
									if (uCa.getEntryTeam(b.uB.getName()) == null) {
										if (Math.random() < 0.55) {
											b.sendActionMessage("\u00A76\u00A7lRemember to select a team");
										} else {
											b.sendActionMessage("\u00A76\u00A7lYou are join as Spectator Mode");
										}
									}
								}
							}
						}
						uE[0]--;
					}
				} else {
					uF.cancel();
					uD[0] = false;
					uD[1] = false;
					uA.mA = GameStatus.WAITING;
				}
				updateScoreboard();
			}
		}, 20, 20);
	}

	private void kickAllPlayers(Collection<? extends Player> a, boolean b, boolean c) {
		for (Player d : a) {
			if (b) {
				UHCPlayer e = uA.mB.getPlayer(d.getName());
				e.tpFallbackServer();
			} else if (c) {
				d.kickPlayer(uA.mC.cFu);
			} else {
				d.kickPlayer(uA.mC.cFt);
			}
		}
	}

	// ------:- PRIVATE | SCOREBOARD -:-------------------------------------------------------------

	private void updateScoreboard() {
		if (uCb == null) {
			if (uCa.getObjective("uhc_w") != null) {
				uCa.getObjective("uhc_w").unregister();
			}
			uCb = uCa.registerNewObjective("uhc_w", "dummy");
			uCb.setDisplayName("\u00A7c\u00A7lUHC-" + uA.mC.cFc + "\u00A78\u00A7l :\u00A7e\u00A7l " + (uA.mC.cGa ? "Team" : "Solo") + " Mode");
			uCb.setDisplaySlot(DisplaySlot.SIDEBAR);
			setScore(6, " ");
			setScore(5, "\u00A76\u00A7lStatus");
			setScore(3, "");
			setScore(2, "\u00A76\u00A7lPlayers");
		}
		setScore(4, " " + (uD[0] ? "\u00A7a\u00A7oStarting in \u00A7e\u00A7o" + uE[0] + "\u00A7a\u00A7o secound" + (uE[0] != 1 ? "s" : "") : "\u00A77\u00A7oWaiting for players..."));
		setScore(1, " \u00A77" + uA.mB.getAllPlayers().size() + " / " + uA.getServer().getMaxPlayers());
	}

	private void setScore(int a, String b) {
		if (uCc.containsKey(a)) {
			if (uCc.get(a).equals(b)) {
				return;
			}
			uCa.resetScores(uCc.get(a));
		}
		uCb.getScore(b).setScore(a);
		uCc.put(a, b);
	}
}
