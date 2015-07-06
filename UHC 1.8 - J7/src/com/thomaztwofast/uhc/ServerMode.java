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

package com.thomaztwofast.uhc;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ServerMode {
	private static Main pl;
	private static boolean smsc = false;
	private static boolean smscOp = false;
	private static DecimalFormat clDF = new DecimalFormat("#.##");
	private static ArrayList<World> clWs = new ArrayList<World>();
	private static ArrayList<String> clCs = new ArrayList<String>();
	private static int cl1 = 1;
	private static int cl2 = 0;
	private static int clTP = 0;
	private static int clWP = 0;
	private static int clSysId = 0;
	private static int smcw = 0;
	private static int smSysCw;
	private static int sysCountdown = 30;
	private static int sysTpCoolDown = 60;
	private static int sysMT = 0;
	private static int sysMD = 0;

	/**
	 * ~ Loading Server Mode ~
	 * <p>
	 * Trigger when server loading up.
	 * </p>
	 * 
	 * @param main = Main Plugin
	 */
	public static void onLoad(Main main) {
		pl = main;
		smcw = main.smCn;
		loadUhcChunkLoader();
		loadUhcLobbyWorld();
		loadUhcSetup();
	}

	/**
	 * ~ Server Disabled ~
	 * <p>
	 * Trigger when server reload or stopping the server.
	 * </p>
	 */
	public static void onDisable() {
		Function.kickAllPlayers(pl);
		Function.uhcRemove(pl);
	}

	/**
	 * ~ New Player~
	 * <p>
	 * Trigger when a player joining the server.
	 * </p>
	 * 
	 * @param p = Player
	 */
	public static void newPlayer(Player p) {
		p.teleport(pl.getServer().getWorld("uhc_lobby").getSpawnLocation().add(0.5, 0, 0.5));
		p.setGameMode(GameMode.ADVENTURE);
		Function.updateDifficultyLvlOnClient(p, EnumDifficulty.PEACEFUL);
		Function.playerHubItem(pl, p);
		updateWaitingScoreboard();
		if (!pl.tmMode) {
			if (!smsc && pl.getServer().getOnlinePlayers().size() >= pl.smMps) {
				countdownWaiting();
			}
		}
	}

	/**
	 * ~ Enough Team To Start ~
	 * <p>
	 * Check if there is enough teams to start the game
	 * </p>
	 * 
	 * @return int
	 */
	public static int enoughTeamToStart() {
		int a = 0;
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		for (Team t : sb.getTeams()) {
			if (t.getSize() != 0) {
				a++;
			}
		}
		return a;
	}

	/**
	 * ~ Start The Countdown ~
	 * 
	 */
	public static void onTeamStart() {
		if (!smsc) {
			countdownWaiting();
		}
	}

	/**
	 * ~ Start The Countdown from /start command ~
	 * 
	 * @param p = Player Name
	 * 
	 */
	public static void onStart(String p, UUID up) {
		if (!smsc) {
			smsc = true;
			smscOp = true;
			pl.getServer().broadcastMessage("§9Game>§e " + p + "§7 has started the game.");
			countdownWaiting();
		} else {
			Player pla = pl.getServer().getPlayer(up);
			pla.sendMessage("§9Start>§7 Countdown is already started.");

		}
	}

	/**
	 * ~ Is Countdown Started? ~
	 * 
	 * @return String
	 */
	public static String isStarting() {
		if (smsc) {
			return "-START";
		}
		return "";
	}

	/**
	 * ~ Load Uhc Arena Chunk ~
	 * <p>
	 * If world folder have this file 'uhc.yml' it will skip it.
	 * </p>
	 */
	private static void loadUhcChunkLoader() {
		pl.getLogger().info("ChunkLoader> Preparing to start.");
		for (World w : pl.getServer().getWorlds()) {
			if (!(w.getEnvironment().equals(Environment.THE_END) || w.getName().equalsIgnoreCase("uhc_lobby"))) {
				File f = new File(w.getName() + "/uhc.yml");
				if (!f.exists()) {
					clWs.add(w);
					cl2++;
				}
			}
		}
		if (clWs.size() != 0) {
			int az = pl.woArenaSize + pl.clBorder;
			Location l1 = new Location(pl.getServer().getWorlds().get(0), (0 - az), 64, (0 - az));
			Location l2 = new Location(pl.getServer().getWorlds().get(0), (0 + az), 64, (0 + az));
			for (int i = l1.getChunk().getX(); i <= l2.getChunk().getZ(); i++) {
				for (int o = l1.getChunk().getX(); o <= l2.getChunk().getZ(); o++) {
					clCs.add(i + "|" + o);
				}
			}
			pl.getLogger().info("ChunkLoader> Found " + clWs.size() + " world.");
			pl.getLogger().info("ChunkLoader> Chunkloader will completed in " + Function.getTimeFormat((int) ((clWs.size() * clCs.size()) / ((20.0 / pl.clTick) * pl.clTask)) + "000"));
			pl.getLogger().info("ChunkLoader> Starting to load chunk in world '" + clWs.get(cl1 - 1).getName() + "' [#" + cl1 + "]");
			clSysId = pl.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i <= pl.clTask; i++) {
						if (clWP >= clCs.size()) {
							clWs.get(cl1 - 1).save();
							if (cl1 == cl2) {
								pl.getServer().getScheduler().cancelTask(clSysId);
								clWs.clear();
								clCs.clear();
								cl1 = 1;
								cl2 = 0;
								clTP = 0;
								clWP = 0;
								pl.getLogger().info("ChunkLoader> Done.");
								pl.gmStat = EnumGame.WAITHING;
								break;
							} else {
								cl1++;
								clWP = 0;
								pl.getLogger().info("ChunkLoader> Starting to load chunk in world '" + clWs.get(cl1 - 1).getName() + "' [#" + cl1 + "]");
								break;
							}
						}
						String[] clID = clCs.get(clWP).split("\\|");
						clWs.get(cl1 - 1).loadChunk(Integer.parseInt(clID[0]), Integer.parseInt(clID[1]), true);
						clWP++;
						clTP++;
						if (clTP % 1500 == 0) {
							clWs.get(cl1 - 1).save();
							float a = (float) ((float) (clTP + .0) / (clCs.size() * clWs.size()) + .0) * 100;
							pl.getLogger().info("ChunkLoader> " + clDF.format(a) + "%");
						}
					}
				}
			}, 0, pl.clTick);
		} else {
			pl.getLogger().info("ChunkLoader> Done.");
			pl.gmStat = EnumGame.WAITHING;
		}
	}

	/**
	 * ~ Load UHC Lobby World ~
	 */
	private static void loadUhcLobbyWorld() {
		World uhcLobby = pl.getServer().createWorld(WorldCreator.name("uhc_lobby"));
		uhcLobby.setDifficulty(Difficulty.PEACEFUL);
		uhcLobby.setPVP(false);
		uhcLobby.setGameRuleValue("doDaylightCycle", "false");
		uhcLobby.setGameRuleValue("keepInventory", "true");
		uhcLobby.setGameRuleValue("randomTickSpeed", "0");
		uhcLobby.setTime(6000);
		uhcLobby.setSpawnFlags(false, false);
	}

	/**
	 * ~ UHC Setup ~
	 */
	private static void loadUhcSetup() {
		Gui.onLoad(pl);
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		if (sb.getObjective("hp") == null) {
			Objective hp = sb.registerNewObjective("hp", "health");
			hp.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}
		if (pl.tmMode) {
			String[] tsl = { "Black|0", "Dark_Blue|1", "Dark_Green|2", "Dark_Aqua|3", "Dark_Red|4", "Dark_Purple|5", "Gold|6", "Gray|7", "Dark_Gray|8", "Blue|9", "Green|a", "Aqua|b", "Red|c", "Light_Purple|d", "Yellow|e", "White|f" };
			pl.invStore.put("selectteam", pl.getServer().createInventory(null, 18, "          - Select Team -"));
			for (String team : tsl) {
				String[] tm = team.split("\\|");
				if (sb.getTeam(tm[0]) == null) {
					Team t = sb.registerNewTeam(tm[0]);
					t.setPrefix("§" + tm[1]);
					t.setSuffix("§r");
					t.setAllowFriendlyFire(pl.tmrFF);
					t.setCanSeeFriendlyInvisibles(pl.tmrSFI);
					ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName("§" + tm[1] + "§l" + tm[0].replace("_", " "));
					im.setLore(Arrays.asList("", "§8Max " + pl.tmMaxPlayer + " Player" + (pl.tmMaxPlayer == 1 ? "" : "s")));
					is.setItemMeta(im);
					pl.invStore.get("selectteam").addItem(is);
				}
			}
			ItemStack is = new ItemStack(Material.BARRIER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§cRemove me from my team");
			is.setItemMeta(im);
			pl.invStore.get("selectteam").setItem(17, is);
			HashMap<ItemStack, Integer> item = new HashMap<ItemStack, Integer>();
			is = new ItemStack(Material.PAPER);
			im = is.getItemMeta();
			im.setDisplayName("§eSelect Team");
			is.setItemMeta(im);
			item.put(is, pl.tmSelTmInvSlot);
			pl.itemStore.put("selectteam", item);
			pl.tmsST = true;
		}
		if (pl.uhcBook) {
			HashMap<ItemStack, Integer> item = new HashMap<ItemStack, Integer>();
			ItemStack b = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bm = (BookMeta) b.getItemMeta();
			bm.setAuthor(pl.getDescription().getName());
			bm.setTitle(pl.ubName);
			bm.setDisplayName(bm.getTitle());
			if (!pl.ubLord.isEmpty()) {
				bm.setLore(pl.ubLord);
			}
			if (!pl.ubPages.isEmpty()) {
				for (String pg : pl.ubPages) {
					bm.addPage(pg);
				}
			}
			if (pl.uhcBookConf) {
				StringBuilder str = new StringBuilder();
				str.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				str.append("§1World:§r\n");
				str.append("  §8Difficulty: " + (pl.woDiff == 1 ? "§2Easy" : (pl.woDiff == 2 ? "§6Normal" : "§4Hard")) + "§r\n");
				str.append("  §8Arena size:§4 " + pl.woArenaSize + "§r\n");
				str.append("  §8Sun time:§4 " + pl.woSunTime + "§r\n \n");
				str.append("§1World Border:§r\n");
				str.append("  §8Start:§4 " + (pl.wbStartPos / 2) + "§r\n");
				if (pl.wbTime != 0) {
					str.append("  §8Stop:§4 " + (pl.wbEndPos / 2) + "§r\n");
					str.append("  §8Shrink:§4 " + Function.getTimeFormat(pl.wbTime + "000") + "§r\n");
				}
				bm.addPage(str.toString());
				str = new StringBuilder();
				str.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				str.append("§1Marker:§r\n");
				str.append("  §8Time delay:§4 " + (pl.moMarkTime != 0 ? pl.moMarkTime + " min" : "Off") + "§r\n");
				if (pl.moMarkTime != 0) {
					str.append("  §8Message: " + (pl.moMarkMeg.isEmpty() ? "§4Off" : "§2On") + "§r\n");
				}
				str.append(" \n§1Disconnected Players:§r\n");
				str.append("  §8Max timeout: §4" + pl.okMaxTime + " min§r\n");
				if (pl.fzp) {
					str.append(" \n§1Freeze Players:§r\n");
					str.append("  §8Radius size: §4" + pl.fzSize + "§r\n");
				}
				bm.addPage(str.toString());
				str = new StringBuilder();
				str.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				str.append("§1Gamerule:§r\n");
				str.append("  §8Natural Regen: §4Off§r\n");
				if (pl.grList.size() != 0) {
					for (String grs : pl.grList) {
						String[] gr = grs.split("\\|");
						if (gr[1].toString().equalsIgnoreCase("true") || gr[1].toString().equalsIgnoreCase("false")) {
							if (Function.gameruleReplace(gr[0]).equalsIgnoreCase("Eternal day")) {
								str.append("  §8" + Function.gameruleReplace(gr[0]) + ": " + (gr[1].toString().equalsIgnoreCase("true") ? "§4Off" : "§2On") + "§r\n");
							} else {
								str.append("  §8" + Function.gameruleReplace(gr[0]) + ": " + (gr[1].toString().equalsIgnoreCase("true") ? "§2On" : "§4Off") + "§r\n");
							}
						} else {
							str.append("  §8" + Function.gameruleReplace(gr[0]) + ":§4 " + gr[1] + "§r\n");
						}
					}
				}
				bm.addPage(str.toString());
				str = new StringBuilder();
				str.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				if (pl.tmMode) {
					str.append("§1Teams:§r\n");
					str.append("  §8Max player:§4 " + pl.tmMaxPlayer + "§r\n");
					str.append("  §8Friendly fire: " + (pl.tmrFF ? "§2On" : "§4Off") + "§r\n");
					str.append("  §8See friendly invi: " + (pl.tmrSFI ? "§2On" : "§4Off") + "§r\n \n");
				}
				str.append("§1Other:§r\n");
				str.append("  §8Damager Logger: " + (pl.dmgLog ? "§2On" : "§4Off") + "§r\n");
				bm.addPage(str.toString());
			}
			b.setItemMeta(bm);
			item.put(b, pl.ubInvSlot);
			pl.itemStore.put("Book", item);
		}
		for (World w : pl.getServer().getWorlds()) {
			if (!w.getName().equalsIgnoreCase("uhc_lobby")) {
				w.setDifficulty(Difficulty.PEACEFUL);
				w.setPVP(false);
				w.setTime(pl.woSunTime);
				if (pl.grList != null) {
					if (pl.grList.size() != 0) {
						for (String gamerule : pl.grList) {
							String[] gr = gamerule.split("\\|");
							w.setGameRuleValue(gr[0], gr[1]);
						}
						if (pl.grList.contains("reducedDebugInfo|true")) {
							for (Player p : pl.getServer().getOnlinePlayers()) {
								CraftPlayer cp = (CraftPlayer) p;
								cp.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(cp.getHandle().playerInteractManager.player, (byte) 22));
							}
						} else {
							for (Player p : pl.getServer().getOnlinePlayers()) {
								CraftPlayer cp = (CraftPlayer) p;
								cp.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(cp.getHandle().playerInteractManager.player, (byte) 23));
							}
						}
						w.setGameRuleValue("doDaylightCycle", "false");
						w.setGameRuleValue("naturalRegeneration", "true");
						w.setGameRuleValue("keepInventory", "true");
					}
				}
			}
		}
	}

	/**
	 * ~ Waiting Countdown ~
	 * <p>
	 * Countdown to start the game. Trigger when thare are enuff player to start the game.
	 * </p>
	 * 
	 */
	private static void countdownWaiting() {
		smsc = true;
		smSysCw = pl.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			@Override
			public void run() {
				if (((pl.tmMode == false && pl.getServer().getOnlinePlayers().size() >= pl.smMps) || (smscOp && pl.getServer().getOnlinePlayers().size() >= 2)) || ((pl.tmMode && enoughTeamToStart() >= pl.smMts) || (smscOp && enoughTeamToStart() >= 2))) {
					if (smcw == 0) {
						pl.getServer().getScheduler().cancelTask(smSysCw);
						Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
						sb.getObjective("i").unregister();
						if (pl.tmMode) {
							serverGamePreparing(enoughTeamToStart());
						} else {
							serverGamePreparing(pl.getServer().getOnlinePlayers().size());
						}
					} else {
						for (Player p : pl.getServer().getOnlinePlayers()) {
							if (pl.tmMode) {
								if (smcw % 10 == 5) {
									if (p.getScoreboard().getEntryTeam(p.getName()) == null) {
										Function.sendActionBarPlayerMessages(p, "§6§lRemember to select a team.");
									}
								}
								if (smcw % 10 == 0) {
									if (p.getScoreboard().getEntryTeam(p.getName()) == null) {
										Function.sendActionBarPlayerMessages(p, "§6§lYou are join as Spectator Mode.");
									}
								}
							}
						}
						updateWaitingScoreboard();
						smcw--;
					}
				} else {
					pl.getServer().getScheduler().cancelTask(smSysCw);
					smsc = false;
					smscOp = false;
					smcw = pl.smCn;
					updateWaitingScoreboard();
				}
			}
		}, 20L, 20L);
	}

	/**
	 * ~ Game Preparing ~
	 * 
	 * @param i = (Int) Total players / teams.
	 */
	@SuppressWarnings("deprecation")
	private static void serverGamePreparing(int i) {
		pl.gmStat = EnumGame.STARTING;
		Function.gamePreparingToStartSetup(pl);
		ArrayList<Location> sl = Function.getSpawnList(pl, i);
		int a = 0;
		if (pl.tmMode) {
			ArrayList<Team> tl = getTeamList();
			for (final Team t : tl) {
				for (OfflinePlayer off : t.getPlayers()) {
					if (off.isOnline()) {
						pl.igPs.put(off.getUniqueId(), ((Player) off));
					} else {
						pl.igPs.put(off.getUniqueId(), null);
					}
				}
				pl.igTms.put(t.getName(), sl.get(a));
				pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
					@Override
					public void run() {
						for (OfflinePlayer off : t.getPlayers()) {
							if (off.isOnline()) {
								Player p = (Player) off;
								Location l = pl.igTms.get(t.getName());
								l = new Location(l.getWorld(), l.getBlockX() + .5, l.getWorld().getHighestBlockYAt(l) + 2, l.getBlockZ() + .5);
								p.teleport(l);
								if (pl.fzp) {
									Function.customWorldBorder(p, l, pl.fzSize);
								}
								p.setNoDamageTicks(0);
								p.damage(2.0);
								p.setNoDamageTicks(60 * sysCountdown);
								if (p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
									p.removePotionEffect(PotionEffectType.BLINDNESS);
									p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 3), 1));
								}
							} else {
								pl.offPs.add(off.getUniqueId());
								Function.startOfflinePlayerTimer(pl, off.getUniqueId(), (sysCountdown + (20 * sysCountdown)));
							}
						}
					}
				}, sysTpCoolDown);
				a++;
				sysTpCoolDown += 30;
			}
		} else {
			for (final Player p : pl.getServer().getOnlinePlayers()) {
				pl.igPs.put(p.getUniqueId(), p);
				pl.igPsl.put(p.getUniqueId(), sl.get(a));
				pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
					@Override
					public void run() {
						if (p.isOnline()) {
							Location l = pl.igPsl.get(p.getUniqueId());
							l = new Location(l.getWorld(), l.getBlockX() + .5, l.getWorld().getHighestBlockYAt(l) + 2, l.getBlockZ() + .5);
							p.teleport(l);
							if (pl.fzp) {
								Function.customWorldBorder(p, l, pl.fzSize);
							}
							p.setNoDamageTicks(0);
							p.damage(2.0);
							p.setNoDamageTicks(60 * sysCountdown);
							if (p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
								p.removePotionEffect(PotionEffectType.BLINDNESS);
								p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 3), 1));
							}
						} else {
							pl.offPs.add(p.getUniqueId());
							Function.startOfflinePlayerTimer(pl, p.getUniqueId(), (sysCountdown + (20 * sysCountdown)));
						}
					}
				}, sysTpCoolDown);
				a++;
				sysTpCoolDown += 30;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("§8§m--------------------------------------------§r");
		sb.append("\n §c§lULTRA HARDCORE - " + (pl.tmMode ? "TEAM" : "SOLO") + " MODE§r\n \n");
		sb.append(" §9§l> §r§aYou will be teleported in few second.§r\n");
		sb.append(" §9§l>§r §6This game is starting in §c" + (sysCountdown + (sysTpCoolDown / 20)) + "§6 seconds.§r\n");
		sb.append(" \n §6§lGood Luck!§r\n");
		sb.append("\n§8§m--------------------------------------------§r");
		for (UUID pid : pl.igPs.keySet()) {
			if (pl.igPs.get(pid) != null) {
				Player p = pl.igPs.get(pid);
				if (p.isOnline()) {
					p.sendMessage(sb.toString());
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 120), 1));
				}
			}
		}
		gameStartCountDown();
	}

	/**
	 * ~ Start countdown ~
	 */
	private static void gameStartCountDown() {
		pl.sysTimeStart = pl.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			@Override
			public void run() {
				if (sysCountdown == 0) {
					pl.gmStat = EnumGame.INGAME;
					pl.getServer().getScheduler().cancelTask(pl.sysTimeStart);
					Function.startUhc(pl);
					if (pl.moMarkTime != 0) {
						uhcTimer();
					}
				}
				for (UUID pid : pl.igPs.keySet()) {
					if (pl.igPs.get(pid) != null) {
						Player p = pl.igPs.get(pid);
						if (p.isOnline()) {
							if (sysCountdown <= 10) {
								if (sysCountdown == 10) {
									Function.sendActionBarPlayerMessages(p, " ");
									Function.sendTitlePlayerMessages(p, "§c" + sysCountdown, 21, 5);
									p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1, 1.5f);
								} else {
									if (sysCountdown <= 5) {
										if (sysCountdown == 1) {
											Function.sendTitlePlayerMessages(p, "§6Starting in §c§l" + sysCountdown + "§r§6 secund", 21, 5);
											p.playSound(p.getLocation(), Sound.NOTE_PLING, 5, 1f);
										} else if (sysCountdown == 0) {
											Function.sendTitlePlayerMessages(p, "§a§lBEGIN!", 21, 10);
											Function.updateDifficultyLvlOnClient(p, (pl.woDiff == 1 ? EnumDifficulty.EASY : (pl.woDiff == 2 ? EnumDifficulty.NORMAL : EnumDifficulty.HARD)));
											p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 5, 1f);
											p.setFoodLevel(20);
											p.setHealth(20.0);
											p.setSaturation(20);
											p.setNoDamageTicks(0);
											p.setGameMode(GameMode.SURVIVAL);
										} else {
											Function.sendTitlePlayerMessages(p, "§6Starting in §c§l" + sysCountdown + "§r§6 secunds", 21, 5);
											p.playSound(p.getLocation(), Sound.NOTE_PLING, 5, 1f);
										}
									} else {
										Function.sendTitlePlayerMessages(p, "§7" + sysCountdown, 21, 5);
									}
								}
							} else {
								if (sysCountdown % 10 == 0) {
									Function.sendActionBarPlayerMessages(p, "§c" + sysCountdown);
									p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1, 1.5f);
								} else {
									Function.sendActionBarPlayerMessages(p, "§7" + sysCountdown);
								}
							}
						}
					}
				}
				if (sysCountdown == 30) {
					for (UUID u : pl.igPs.keySet()) {
						if (pl.igPs.get(u) != null) {
							Player p = pl.igPs.get(u);
							if (p.isOnline()) {
								p.teleport(p.getLocation().add(0, 0.5, 0));
							}
						}
					}
				}
				sysCountdown--;
			}
		}, sysTpCoolDown, 20);
	}

	/**
	 * ~ Get teams ~
	 * 
	 * @return ArrayList<Team>
	 */
	private static ArrayList<Team> getTeamList() {
		ArrayList<Team> tms = new ArrayList<Team>();
		for (Team t : pl.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
			if (t.getSize() != 0) {
				tms.add(t);
			}
		}
		return tms;
	}

	/**
	 * ~ Update Scoreboard - Lobby ~
	 * 
	 */
	private static void updateWaitingScoreboard() {
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		if (sb.getObjective("i") != null) {
			sb.getObjective("i").unregister();
			int a = 0;
			for (String ent : sb.getEntries()) {
				for (Player pn : pl.getServer().getOnlinePlayers()) {
					if (pn.getName().equals(ent)) {
						a = 1;
						break;
					}
				}
				if (a == 0) {
					sb.resetScores(ent);
				}
				a = 0;
			}
		}
		Objective o = sb.registerNewObjective("i", "dummy");
		o.setDisplayName("§c§lUHC-" + pl.smSID + " §8§l: §e§l" + (pl.tmMode ? "Team Mode" : "Solo Mode"));
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score s = o.getScore(" ");
		s.setScore(6);
		s = o.getScore("§6§lStatus");
		s.setScore(5);
		s = o.getScore("§7" + (smsc ? "§a§oStarting in §6§o" + smcw + "§a§o " + (smcw == 1 ? "second" : "seconds") : "§7§oWaiting for players..."));
		s.setScore(4);
		s = o.getScore("  ");
		s.setScore(3);
		s = o.getScore("§6§lPlayers");
		s.setScore(2);
		s = o.getScore("§7" + pl.getServer().getOnlinePlayers().size() + " / " + pl.getServer().getMaxPlayers());
		s.setScore(1);
	}

	/**
	 * ~ UHC Timer / Mark ~
	 */
	protected static void uhcTimer() {
		pl.sysTimeGame = pl.getServer().getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
			@Override
			public void run() {
				sysMD++;
				sysMT += pl.moMarkTime;
				if (pl.moMarkMeg != "") {
					pl.getServer().broadcastMessage(pl.moMarkMeg.replace("$[T]", "" + sysMT).replace("$[E]", "" + sysMD));
				}
			}
		}, (20 * (60 * pl.moMarkTime)), (20 * (60 * pl.moMarkTime)));
	}
}