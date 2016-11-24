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

package com.thomaztwofast.uhc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.thomaztwofast.uhc.custom.Function;
import com.thomaztwofast.uhc.custom.Sc;
import com.thomaztwofast.uhc.custom.UhcBook;
import com.thomaztwofast.uhc.custom.UhcDmg;
import com.thomaztwofast.uhc.custom.UhcMenu;
import com.thomaztwofast.uhc.custom.UhcServer;
import com.thomaztwofast.uhc.custom.UhcTeams;
import com.thomaztwofast.uhc.data.GameStatus;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.events.EvGame;

public class GameManager extends Function {
	private Main gA;
	public UhcMenu gB;
	public UhcServer gC;
	public UhcTeams gD;
	public UhcBook gE;
	public UhcDmg gF;
	private List<ShapedRecipe> gG = new ArrayList<>();
	private HashMap<String, Location> gHa = new HashMap<>();
	private List<String> gHb = new ArrayList<>();
	private HashMap<String, BukkitTask> gHc = new HashMap<>();
	private List<String> gHd = new ArrayList<>();
	private Random gI = new Random();
	private int[] gJ = { 120, 30, 0 };
	private BukkitTask gK[] = { null, null };
	public long gL;
	private int[] gM = { 1, 0 };

	// ------:- PUBLIC | LOADER -:------------------------------------------------------------------

	public GameManager(Main a) {
		gA = a;
		gB = new UhcMenu(a);
		gC = new UhcServer(a);
		gD = new UhcTeams(a);
		gE = new UhcBook(a);
		gF = new UhcDmg(a);
	}

	// LOW: REMOVE / REPLACEMENT FOR {MaterialData} (deprecation)
	@SuppressWarnings("deprecation")
	public void loadGame() {
		gA.mA = GameStatus.LOADING;
		gA.getServer().getPluginManager().registerEvents(new EvGame(gA), gA);
		gB.load();
		Scoreboard a = gA.getServer().getScoreboardManager().getMainScoreboard();
		if (a.getObjective("hp") == null) {
			a.registerNewObjective("hp", "health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}
		if (gA.mC.cGa) {
			gD.load();
		}
		if (gA.mC.cKa) {
			gE.load();
		}
		ItemStack b = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta c = b.getItemMeta();
		c.setDisplayName("\u00A7bDefault Head Apple");
		b.setItemMeta(c);
		ShapedRecipe d = new ShapedRecipe(b);
		d.shape("xxx", "xhx", "xxx").setIngredient('x', Material.GOLD_NUGGET);
		d.setIngredient('h', new MaterialData(Material.SKULL_ITEM, (byte) 3));
		gG.add(d);
		c.setDisplayName("\u00A7dGolden Head Apple");
		b.setItemMeta(c);
		d = new ShapedRecipe(b);
		d.shape("xxx", "xhx", "xxx").setIngredient('x', Material.GOLD_INGOT);
		d.setIngredient('h', new MaterialData(Material.SKULL_ITEM, (byte) 3));
		gG.add(d);
		updateGoldenHead();
		for (World e : gA.getServer().getWorlds()) {
			if (!e.getName().equals("uhc_lobby")) {
				e.setDifficulty(Difficulty.PEACEFUL);
				e.setPVP(false);
				e.setTime(gA.mC.cIc);
				e.setGameRuleValue("doDaylightCycle", "" + false);
				e.setGameRuleValue("doEntityDrops", "" + gA.mC.cEb);
				e.setGameRuleValue("doFireTick", "" + gA.mC.cEc);
				e.setGameRuleValue("doMobLoot", "" + gA.mC.cEd);
				e.setGameRuleValue("doMobSpawning", "" + gA.mC.cEe);
				e.setGameRuleValue("doTileDrops", "" + gA.mC.cEf);
				e.setGameRuleValue("mobGriefing", "" + gA.mC.cEi);
				e.setGameRuleValue("randomTickSpeed", "" + gA.mC.cEj);
				e.setGameRuleValue("reducedDebugInfo", "" + gA.mC.cEk);
				e.setGameRuleValue("spectatorsGenerateChunks", "" + gA.mC.cEl);
				e.setGameRuleValue("spawnRadius", "" + gA.mC.cEm);
				e.setGameRuleValue("naturalRegeneration", "true");
				e.setGameRuleValue("keepInventory", "true");
			}
		}
		if (gA.mC.cFa) {
			gC.load();
			return;
		}
		getAllPlayers();
		gA.mA = GameStatus.WAITING;
	}

	public void unloadGame() {
		if (gA.mC.cFa) {
			gC.unLoad();
		}
		Scoreboard a = gA.getServer().getScoreboardManager().getMainScoreboard();
		if (a.getObjective("hp") != null) {
			a.getObjective("hp").unregister();
		}
		if (gA.mC.cGa) {
			gD.unload();
		}
		for (World b : gA.getServer().getWorlds()) {
			b.setPVP(true);
			b.getWorldBorder().setSize(Integer.MAX_VALUE);
			b.setGameRuleValue("doDaylightCycle", "true");
			b.setGameRuleValue("naturalRegeneration", "true");
			b.setGameRuleValue("keepInventory", "false");
		}
		if (new File(gA.getDataFolder(), "data").exists()) {
			try {
				FileUtils.deleteDirectory(new File(gA.getDataFolder(), "data"));
			} catch (IOException e) {
				gA.log(1, "Error, Directory 'data' could not deleted! " + e.getMessage());
			}
		}
	}

	// ------:- PUBLIC | INGAME DATA -:-------------------------------------------------------------

	public boolean isOffline(String a) {
		return gHc.containsKey(a);
	}

	public List<String> getIngamePlayers() {
		return gHb;
	}

	public int getIngameSize() {
		return gHa.size();
	}

	public int getOfflineSize() {
		return gHc.size();
	}

	public Location getSpawnLoc(Player a) {
		return gHa.get((gA.mC.cGa ? a.getScoreboard().getEntryTeam(a.getName()).getName() : a.getName()));
	}

	// ------:- PUBLIC | UPDATE / PLAYER SETTINGS -:------------------------------------------------

	public void getAllPlayers() {
		for (UHCPlayer a : gA.mB.getAllPlayers()) {
			a.resetPlayer(true);
			a.hubItems();
		}
	}

	public void updateGamerule() {
		for (World a : gA.getServer().getWorlds()) {
			if (!a.getName().equals("uhc_lobby")) {
				a.setGameRuleValue("doEntityDrops", "" + gA.mC.cEb);
				a.setGameRuleValue("doFireTick", "" + gA.mC.cEc);
				a.setGameRuleValue("doMobLoot", "" + gA.mC.cEd);
				a.setGameRuleValue("doMobSpawning", "" + gA.mC.cEe);
				a.setGameRuleValue("doTileDrops", "" + gA.mC.cEf);
				a.setGameRuleValue("doWeatherCycle", "" + gA.mC.cEg);
				a.setGameRuleValue("maxEntityCramming", "" + gA.mC.cEh);
				a.setGameRuleValue("mobGriefing", "" + gA.mC.cEi);
				a.setGameRuleValue("randomTickSpeed", "" + gA.mC.cEj);
				a.setGameRuleValue("reducedDebugInfo", "" + gA.mC.cEk);
				a.setGameRuleValue("spectatorsGenerateChunks", "" + gA.mC.cEl);
				a.setGameRuleValue("spawnRadius", "" + gA.mC.cEm);
			}
		}
		for (UHCPlayer a : gA.mB.getAllPlayers()) {
			a.updateEntityStatus(gA.mC.cEk);
		}
	}

	// ------:- PUBLIC | GAME -:--------------------------------------------------------------------

	public void gmOfflineNewTimer(String a) {
		BukkitTask b = gA.getServer().getScheduler().runTaskLater(gA, new Runnable() {
			@Override
			public void run() {
				if (gA.mC.cGa) {
					Team c = gA.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(a);
					gmBroadcastMsg(gA.mC.cPb.replace("{0}", c.getPrefix() + a + "\u00A7r"));
					c.removeEntry(a);
					if (c.getSize() == 0) {
						gHa.remove(c.getName());
						c.unregister();
					}
				} else {
					gmBroadcastMsg(gA.mC.cPb.replace("{0}", a));
				}
				gHb.remove(a);
				gHc.remove(a);
				gmStatusCheck();
			}
		}, ((60 * gA.mC.cPa) * 20) + (gA.mA.i() == 7 ? 0 : gJ[0]));
		gHc.put(a, b);
	}

	public void gmOfflineEndTimer(String a) {
		UHCPlayer b = gA.mB.getPlayer(a);
		if (gHd.contains(a)) {
			b.resetPlayer(true);
			b.uB.teleport(gHa.get((gA.mC.cGa ? b.uB.getScoreboard().getEntryTeam(a).getName() : a)).add(0.5, 1.5, 0.5));
			gHd.remove(a);
		}
		if (gA.mA.equals(GameStatus.STARTING) && gA.mC.cMa) {
			gA.getServer().getScheduler().runTaskLater(gA, new Runnable() {
				@Override
				public void run() {
					b.setCustomWorldborder(gHa.get((gA.mC.cGa ? b.uB.getScoreboard().getEntryTeam(a).getName() : a)), gA.mC.cMb);
				}
			}, 0);
		}
		if (gA.mA.equals(GameStatus.INGAME) && !b.uB.getGameMode().equals(GameMode.SURVIVAL)) {
			b.uB.setGameMode(GameMode.SURVIVAL);
		}
		gHc.get(a).cancel();
		gHc.remove(a);
	}

	public void gmRemoveIgPlayer(UHCPlayer a) {
		gHb.remove(a.uB.getName());
		if (gA.mC.cGa) {
			Team b = a.uB.getScoreboard().getEntryTeam(a.uB.getName());
			b.removeEntry(a.uB.getName());
			if (b.getSize() == 0) {
				gHa.remove(b.getName());
				b.unregister();
			}
		} else {
			gHa.remove(a.uB.getName());
		}
		gA.getServer().getScheduler().runTaskLater(gA, new Runnable() {
			@Override
			public void run() {
				if (a.uB.isOnline()) {
					a.uB.setGameMode(GameMode.SPECTATOR);
				}
			}
		}, 25);
		gmStatusCheck();
	}

	public void gmStart(int a) {
		gA.mA = GameStatus.STARTING;
		List<Location> b = gmSetSpawnList(a);
		int c = 0;
		for (UHCPlayer d : gA.mB.getAllPlayers()) {
			if (gA.mC.cGa) {
				if (d.uB.getScoreboard().getEntryTeam(d.uB.getName()) == null) {
					gmJoinSpectatorMode(d);
				} else {
					d.resetPlayer(true);
				}
			} else {
				if (d.uB.getGameMode().equals(GameMode.ADVENTURE)) {
					d.resetPlayer(true);
					gHb.add(d.uB.getName());
					gHa.put(d.uB.getName(), b.get(c));
					gmTpPlayerToArena(d.uB.getName());
					gJ[0] += 60;
					c++;
				} else {
					gmJoinSpectatorMode(d);
				}
			}
		}
		if (gA.mC.cGa) {
			Scoreboard d = gA.getServer().getScoreboardManager().getMainScoreboard();
			List<Team> e = new ArrayList<>();
			for (String f : gD.uCa) {
				if (d.getTeam(f) != null && d.getTeam(f).getSize() != 0) {
					e.add(d.getTeam(f));
					gHa.put(f, b.get(c));
					for (String h : d.getTeam(f).getEntries()) {
						gHb.add(h);
						gmTpPlayerToArena(h);
					}
					gJ[0] += 60;
					c++;
				} else {
					d.getTeam(f).unregister();
				}
			}
		}
		Sc d = new Sc();
		d.setTitle("\u00A7c\u00A7lULTRA HARDCORE 1.8 - " + (gA.mC.cGa ? "TEAM" : "SOLO"));
		d.addTextLn("");
		d.addTextLn("\u00A79>\u00A7a You will be teleported in a few seconds.");
		d.addTextLn("\u00A79>\u00A76 This game will start in \u00A7c" + (gJ[1] + (gJ[0] / 20)) + "\u00A76 seconds.");
		d.addTextLn("");
		d.addTextLn("\u00A76\u00A7lGood Luck!");
		for (String e : gHb) {
			if (gA.mB.getPlayer(e) != null) {
				UHCPlayer f = gA.mB.getPlayer(e);
				f.uB.sendMessage(d.o());
				f.sendTitleMessage("\u00A7a\u00A7lTeleported " + (gA.mC.cGa ? "teams" : "players"), "\u00A77Please wait", 10, 200, 0);
				if (!gA.mC.cFa) {
					f.uB.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2400, 1));
				}
			}
		}
		gmStartCountdown();
	}

	// ------:- PRIVATE | GAME -:-------------------------------------------------------------------

	private void gmBroadcastMsg(String a) {
		for (UHCPlayer b : gA.mB.getAllPlayers()) {
			b.uB.sendMessage(a);
		}
	}

	private void gmEndResult() {
		gA.getServer().getScheduler().runTaskLater(gA, new Runnable() {
			@Override
			public void run() {
				String a = getWinner();
				Sc b = new Sc();
				b.setTitle("ULTRA HARDCORE 1.8");
				b.addTextLn("");
				b.addTextLn(a + "\u00A77 has won the game.");
				for (UHCPlayer c : gA.mB.getAllPlayers()) {
					c.uB.sendMessage(b.o());
					c.sendTitleMessage(a, "\u00A7rwon the game", 10, 60, 10);
					c.playLocalSound(Sound.ENTITY_FIREWORK_LAUNCH, 0f);
				}
				for (World c : gA.getServer().getWorlds()) {
					c.getWorldBorder().setSize(c.getWorldBorder().getSize());
					c.setGameRuleValue("keepInventory", "true");
				}
				if (gHc.size() != 0) {
					for (BukkitTask c : gHc.values()) {
						c.cancel();
					}
					gHc.clear();
				}
				if (gA.mC.cFa) {
					gC.serverRestart();
				}
			}

			private String getWinner() {
				if (gA.mC.cGa) {
					Team a = gA.getServer().getScoreboardManager().getMainScoreboard().getTeam(gHa.keySet().iterator().next());
					for (String b : a.getEntries()) {
						gA.mE.gF.givePlayerItem(b);
					}
					return "\u00A7r\u00A7lTeam " + a.getPrefix() + "\u00A7l" + a.getName().replace("_", " ") + a.getSuffix();
				} else {
					gA.mE.gF.givePlayerItem(gHb.get(0));
					return "\u00A7r\u00A7l" + gHb.get(0) + "\u00A7r";
				}
			}
		}, 20);
	}

	private void gmJoinSpectatorMode(UHCPlayer a) {
		a.resetPlayer(false);
		a.sendCommandMessage("UHC", "You have been move to spectator mode.");
		if (gA.mC.cFa) {
			a.uB.teleport(gA.getServer().getWorlds().get(0).getSpawnLocation());
			if (gA.mC.cFv) {
				a.uB.getInventory().setItem(gA.mC.cFy, gA.mE.gC.uG);
			}
		}
	}

	private void gmMarkTimer() {
		gK[1] = gA.getServer().getScheduler().runTaskTimer(gA, new Runnable() {
			@Override
			public void run() {
				gM[0]++;
				gM[1] += gA.mC.cNb;
				gmBroadcastMsg(gA.mC.cNa.replaceFirst("\\{0}", "" + gM[1]).replaceFirst("\\{1}", "" + (gM[0] - 1)).replaceFirst("\\{2}", "" + gM[0]));
			}
		}, (20 * (gA.mC.cNb * 60)), (20 * (gA.mC.cNb * 60)));
	}

	private void gmStartCountdown() {
		gK[0] = gA.getServer().getScheduler().runTaskTimer(gA, new Runnable() {
			public void run() {
				if (gJ[1] == 0) {
					gA.mA = GameStatus.INGAME;
					gK[0].cancel();
					gL = System.currentTimeMillis();
					for (World b : gA.getServer().getWorlds()) {
						if (!b.getName().equals("uhc_lobby")) {
							b.setPVP(true);
							b.setGameRuleValue("doDaylightCycle", "" + gA.mC.cEa);
							b.setGameRuleValue("naturalRegeneration", "false");
							b.setGameRuleValue("keepInventory", "false");
							b.setDifficulty((gA.mC.cIa == 1 ? Difficulty.EASY : gA.mC.cIa == 2 ? Difficulty.NORMAL : Difficulty.HARD));
							b.getWorldBorder().setCenter(0d, 0d);
							if (gA.mC.cJb < gA.mC.cIb) {
								b.getWorldBorder().setSize((gA.mC.cIb * 2));
							} else {
								b.getWorldBorder().setSize((gA.mC.cJb * 2));
							}
						}
					}
					if (gA.mC.cJa != 0) {
						gK[0] = gA.getServer().getScheduler().runTaskLater(gA, new Runnable() {
							@Override
							public void run() {
								gmWorldBorderShrink();
							}
						}, (gA.mC.cJa * 20));
					} else {
						gmWorldBorderShrink();
					}
					if (gA.mC.cNa.length() != 0 && gA.mC.cNb != 0) {
						gmMarkTimer();
					}
				}
				for (String a : gHb) {
					if (gA.mB.getPlayer(a) != null) {
						UHCPlayer b = gA.mB.getPlayer(a);
						if (gJ[1] == 0) {
							b.sendTitleMessage("", "\u00A7a\u00A7lBEGIN!", 0, 21, 10);
							b.playLocalSound(Sound.ENTITY_ARROW_HIT_PLAYER, 1f);
							b.uB.setFoodLevel(20);
							b.uB.setHealth(b.uB.getMaxHealth());
							b.uB.setSaturation(20f);
							b.uB.setNoDamageTicks(0);
							b.uB.setGameMode(GameMode.SURVIVAL);
						} else if (gJ[1] < 6) {
							b.sendTitleMessage("", "\u00A76Starting in \u00A7c\u00A7l" + gJ[1] + "\u00A76 second" + (gJ[1] == 1 ? "" : "s"), 0, 21, 5);
							b.playLocalSound(Sound.BLOCK_NOTE_PLING, 1f);
						} else if (gJ[1] < 10) {
							b.sendTitleMessage("", "\u00A77" + gJ[1], 0, 21, 5);
						} else if (gJ[1] == 10) {
							b.sendActionMessage("");
							b.sendTitleMessage("", "\u00A7c" + gJ[1], 0, 21, 5);
							b.playLocalSound(Sound.BLOCK_NOTE_HAT, 1.8f);
						} else {
							if (gJ[1] % 10 == 0) {
								b.sendActionMessage("\u00A7c" + gJ[1]);
								b.playLocalSound(Sound.BLOCK_NOTE_HAT, 1.8f);
							} else {
								b.sendActionMessage("\u00A77" + gJ[1]);
							}
						}
					}
				}
				gJ[1]--;
			}
		}, gJ[0], 20);
	}

	private void gmStatusCheck() {
		if (gHa.size() > 1) {
			return;
		}
		gA.mA = GameStatus.FINISHED;
		gK[0].cancel();
		gK[1].cancel();
		gmEndResult();
	}

	private void gmTpPlayerToArena(String a) {
		gA.getServer().getScheduler().runTaskLater(gA, new Runnable() {
			public void run() {
				if (gA.mB.getPlayer(a) != null) {
					UHCPlayer b = gA.mB.getPlayer(a);
					if (gA.mC.cGa) {
						b.uB.teleport(gHa.get(b.uB.getScoreboard().getEntryTeam(a).getName()).add(0.5, 1.5, 0.5));
					} else {
						b.uB.teleport(gHa.get(b.uB.getName()).add(0.5, 1.5, 0.5));
					}
					b.uB.setVelocity(new Vector());
					b.uB.setNoDamageTicks(0);
					b.uB.damage(2d);
					b.uB.setNoDamageTicks((60 * gJ[0]));
					if (b.uB.hasPotionEffect(PotionEffectType.BLINDNESS)) {
						b.uB.removePotionEffect(PotionEffectType.BLINDNESS);
					}
					b.uB.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					if (gA.mC.cMa) {
						b.setCustomWorldborder(b.uB.getLocation(), gA.mC.cMb);
					}
				} else {
					gHd.add(a);
					gmOfflineNewTimer(a);
				}
				gJ[2]++;
				for (String b : gHb) {
					if (gA.mB.getPlayer(b) != null) {
						gA.mB.getPlayer(b).sendTitleMessage("\u00A7a\u00A7lTeleported " + (gA.mC.cGa ? "teams" : "players"), "\u00A77" + gJ[2] + " / " + gHa.size(), 0, (gJ[2] == gHa.size() ? 40 : 200), (gJ[2] == gHa.size() ? 10 : 0));
					}
				}
			}
		}, gJ[0]);
	}

	private void gmWorldBorderShrink() {
		if (gA.mC.cJd != 0) {
			for (World a : gA.getServer().getWorlds()) {
				if (!a.getName().equals("uhc_lobby")) {
					a.getWorldBorder().setSize((gA.mC.cJc * 2), gA.mC.cJd);
				}
			}
		}
	}

	// ------:- PRIVATE | GAME SETTINGS -:----------------------------------------------------------

	private int gmSetPlayerDistance(int a) {
		double b = 308 - (a * 3.636363636);
		if (b < 50) {
			return 0;
		}
		return (int) b;
	}

	private Location gmSetNewLoc(int a) {
		Location b = new Location(gA.getServer().getWorlds().get(0), (-a + gI.nextInt(a * 2)), 64, (-a + gI.nextInt(a * 2)));
		return new Location(b.getWorld(), b.getX(), b.getWorld().getHighestBlockYAt(b), b.getZ());
	}

	private List<Location> gmSetSpawnList(int a) {
		List<Location> b = new ArrayList<>();
		int[] c = { (gA.mC.cIb - 10), gmSetPlayerDistance(a), 1, 0, 0 };
		if (c[1] == 0) {
			for (int d = 0; d < a; d++) {
				b.add(gmSetNewLoc(c[0]));
			}
			return b;
		}
		while (c[2] <= a) {
			Location d = gmSetNewLoc(c[0]);
			if (b.size() != 0) {
				for (Location e : b) {
					if (d.distance(e) < c[1]) {
						c[3]++;
						c[4] = 1;
						break;
					}
				}
			}
			Block e = d.add(0, -1, 0).getBlock();
			if (e.getType().equals(Material.STATIONARY_LAVA) || e.getType().equals(Material.STATIONARY_WATER)) {
				c[4] = 1;
			}
			if (c[4] == 0 || c[3] > 49) {
				b.add(d);
				c[2]++;
				c[3] = 0;
				c[4] = 0;
			} else {
				c[3]++;
			}
		}
		return b;
	}

	// ------:- PUBLIC | GOLDEN HEAD -:-------------------------------------------------------------

	public ItemStack getPlayerHead(String a) {
		ItemStack b = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta c = (SkullMeta) b.getItemMeta();
		c.setOwner(a);
		b.setItemMeta(c);
		return b;
	}

	public void goldenAppleRegen(UHCPlayer a, boolean b) {
		gA.getServer().getScheduler().runTaskLater(gA, new Runnable() {
			public void run() {
				if (a.uB.hasPotionEffect(PotionEffectType.REGENERATION)) {
					a.uB.removePotionEffect(PotionEffectType.REGENERATION);
					a.uB.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (b ? 110 : 140), (b ? 2 : 1)));
				}
			}
		}, 0);
	}

	public ItemMeta setSkullOwner(ItemStack a, ItemStack b) {
		String c = "Unknown";
		SkullMeta d = (SkullMeta) b.getItemMeta();
		if (d.getOwner() != null) {
			c = d.getOwner();
		}
		ItemMeta e = a.getItemMeta();
		e.setLore(Arrays.asList("\u00A77Head of \u00A7a" + c));
		return e;
	}

	public void updateGoldenHead() {
		if (gA.mC.cLa) {
			List<ShapedRecipe> a = new ArrayList<>();
			if (gA.mC.cLb) {
				if (!isRecipeEx(gG.get(0).getResult())) {
					gA.getServer().addRecipe(gG.get(0));
				}
			} else {
				a.add(gG.get(0));
			}
			if (gA.mC.cLc) {
				if (!isRecipeEx(gG.get(1).getResult())) {
					gA.getServer().addRecipe(gG.get(1));
				}
			} else {
				a.add(gG.get(1));
			}
			if (a.size() != 0) {
				removeGoldenHead(a);
			}
		} else {
			removeGoldenHead(gG);
		}
	}

	// ------:- PRIVATE | GOLDEN HEAD -:------------------------------------------------------------

	private boolean isRecipeEx(ItemStack a) {
		ListIterator<Recipe> b = gA.getServer().getRecipesFor(a).listIterator();
		while (b.hasNext()) {
			if (b.next().getResult().equals(a)) {
				return true;
			}
		}
		return false;
	}

	private void removeGoldenHead(List<ShapedRecipe> a) {
		for (ShapedRecipe b : a) {
			Iterator<Recipe> c = gA.getServer().recipeIterator();
			while (c.hasNext()) {
				if (c.next().getResult().equals(b.getResult())) {
					c.remove();
					break;
				}
			}
		}
	}
}
