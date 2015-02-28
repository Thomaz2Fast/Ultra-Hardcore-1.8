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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumDifficulty;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.thomaztwofast.uhc.EnumGame;
import com.thomaztwofast.uhc.Function;
import com.thomaztwofast.uhc.Main;

public class CommandStart implements CommandExecutor, TabCompleter {
	private List<String> tab = ImmutableList.of("uhc");
	private Main pl;
	private Random r = new Random();
	private ArrayList<UUID> sysOP = new ArrayList<UUID>();
	int sysCountdown = 30;
	int sysTpCoolDown = 60;
	int sysMT = 0;
	int sysMD = 0;

	public CommandStart(Main main) {
		this.pl = main;
	}

	/**
	 * <b>~ Command: /start ~</b>
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
			if (pl.gmStat.equals(EnumGame.WAITHING)) {
				if (args.length == 0) {
					gameRaport(sender);
					return true;
				} else {
					if (args[0].equalsIgnoreCase("uhc") && sysOP.contains(((Player) sender).getUniqueId())) {
						String[] data;
						if (pl.tmMode) {
							data = getTeamReport();
						} else {
							data = getSoloReport();
						}
						if (Integer.parseInt(data[0]) <= 1) {
							gameRaport(sender);
							return true;
						} else {
							gamePreparing(Integer.parseInt(data[0]));
							return true;
						}
					} else {
						gameRaport(sender);
						return true;
					}
				}
			} else {
				CraftPlayer cp = (CraftPlayer) sender;
				IChatBaseComponent icbc = ChatSerializer.a("[{text: '§9Start>'},{text: '§7 Disabled!', hoverEvent: {action: 'show_text', value: {text: '', extra: [{text: '§9§lHelp?\n\n§7How to enable this command?\n§7Open §econfig.yml§7 file to this plugin\n§7and change the \"Plugin Mode\" => \"true\"'}]}}}]");
				cp.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(icbc));
				return true;
			}
		}
	}

	/**
	 * <b>~ Start Auto Tab Completer ~</b>
	 * <p>
	 * Show all matches tab args for specific command args.
	 * </p>
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (pl.plMode) {
			if (sender instanceof Player) {
				if (args.length == 1) {
					return StringUtil.copyPartialMatches(args[0], tab, new ArrayList<String>(tab.size()));
				}
			}
		}
		return null;
	}

	/**
	 * <b>~ Befor Start report ~</b>
	 * 
	 * @param sender = Get player how trigger the command.
	 */
	private void gameRaport(CommandSender sender) {
		StringBuilder sb = new StringBuilder();
		String[] data;
		sb.append("§8§m--------------------------------------------§r");
		sb.append("\n §lULTRA HARDCORE - " + (pl.tmMode ? "TEAM" : "SOLO") + " MODE§r\n \n");
		if (pl.tmMode) {
			data = getTeamReport();
			sb.append(" §aTotal Teams:§e " + data[0] + "§r\n");
			if (!data[2].isEmpty()) {
				sb.append(" \n §eWarning:§r\n");
				sb.append(" §7There " + (data[1].equals("1") ? "is" : "are") + " §e" + data[1] + "§7 player" + (data[1].equals("1") ? "" : "s") + ". Which is not in the team yet.§r\n");
				sb.append(" §7Player" + (data[1].equals("1") ? "" : "s") + ": §8[" + data[2] + "§8]§r\n");
				sb.append(" \n §8§oFor those who have not in the team yet, will be move to spectator mode.§r\n");
			}
		} else {
			data = getSoloReport();
			sb.append(" §aTotal Players:§e " + data[0] + "§r\n");
		}
		if (Integer.parseInt(data[0]) <= 1) {
			sb.append(" \n §cError:§r\n §7Not enough " + (pl.tmMode ? "teams" : "players") + " to start the game.§r\n §7Required: 2 or more " + (pl.tmMode ? "teams" : "players") + ".§r\n");
			sysOP.remove(((Player) sender).getUniqueId());
		} else {
			sb.append(" \n §7If everyone is ready to start?§r\n");
			sb.append(" §7Type the command below to start the game.§r\n §8/start uhc§r\n");
			if (!sysOP.contains(((Player) sender).getUniqueId())) {
				sysOP.add(((Player) sender).getUniqueId());
			}
		}
		sb.append("\n§8§m--------------------------------------------§r");
		sender.sendMessage(sb.toString());
	}

	/**
	 * <b>~ Solo Game Mode Raport ~</b>
	 * 
	 * @return String[]
	 */
	private String[] getSoloReport() {
		String[] data = { "0" };
		data[0] = pl.getServer().getOnlinePlayers().size() + "";
		return data;
	}

	/**
	 * <b>~ Team Game Mode Raport ~</b>
	 * 
	 * @return String[]
	 */
	private String[] getTeamReport() {
		String[] data = { "0", "", "" };
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		int a = 0;
		int b = 0;
		String noneTeamPlayer = "";
		for (Team t : sb.getTeams()) {
			if (t.getSize() != 0) {
				a++;
			}
		}
		for (Player p : pl.getServer().getOnlinePlayers()) {
			if (sb.getPlayerTeam(p) == null) {
				if (!noneTeamPlayer.isEmpty()) {
					noneTeamPlayer += "§7, ";
				}
				noneTeamPlayer += "§e" + p.getName();
				b++;
			}
		}
		data[0] = a + "";
		data[1] = b + "";
		data[2] = noneTeamPlayer;
		return data;
	}

	/**
	 * <b>~ Preperting the game to start the game ~</b>
	 * 
	 * @param i = <i>(int)</i> Total player / team.
	 */
	private void gamePreparing(int i) {
		pl.gmStat = EnumGame.STARTING;
		Function.gamePreparingToStartSetup(pl);
		ArrayList<Location> sl = getSpawnList(i);
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
	 * <b>~ Get Spawn location for player / team ~</b>
	 * 
	 * @param i = <i>(int)</i> Total player / team
	 * @return ArrayList<Location> = Spawn location of player / team.
	 */
	private ArrayList<Location> getSpawnList(int i) {
		ArrayList<Location> l = new ArrayList<Location>();
		World w = pl.getServer().getWorlds().get(0);
		int sz = pl.woArenaSize - 10;
		int d = getDistance(pl.woArenaSize, i);
		if (d == 0) {
			for (int a = 0; a < i; a++) {
				l.add(new Location(w, (-sz + r.nextInt(sz * 2)), 64, (-sz + r.nextInt(sz * 2))));
			}
			return l;
		} else {
			int a = 1;
			int b;
			while (a <= i) {
				b = 1;
				Location la = new Location(w, (-sz + r.nextInt(sz * 2)), 64, (-sz + r.nextInt(sz * 2)));
				if (l.size() != 0) {
					for (Location lb : l) {
						if (la.distance(lb) <= d) {
							b = 0;
							break;
						}
					}
				}
				if (b == 1) {
					l.add(la);
					a++;
				}
			}
			return l;
		}
	}

	/**
	 * <b>~ Set max distance for each player / team ~</b>
	 * 
	 * @param sz = (int) = Arena size
	 * @param i = <i>(int)</i> Total player / team
	 * @return int = Max distance.
	 */
	private int getDistance(int sz, int i) {
		int[] d = { 400, 300, 200, 100, 50 };
		int a = 0;
		for (int di : d) {
			double max = (Math.pow((sz * 2), 2) / (di * i));
			if (a == 4) {
				if (max > 159.9) {
					return 50;
				} else {
					return 0;
				}
			}
			if (a == 3) {
				if (max > 266) {
					return 100;
				} else {
					a++;
				}
			}
			if (a == 2) {
				if (max > 399) {
					return 200;
				} else {
					a++;
				}
			}
			if (a == 1) {
				if (max > 444) {
					return 300;
				} else {
					a++;
				}
			}
			if (a == 0) {
				if (max > 624) {
					return 400;
				} else {
					a++;
				}
			}
		}
		return 0;
	}

	/**
	 * <b>~ Get teams ~</b>
	 * 
	 * @return ArrayList<Team>
	 */
	private ArrayList<Team> getTeamList() {
		ArrayList<Team> tms = new ArrayList<Team>();
		for (Team t : pl.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
			if (t.getSize() != 0) {
				tms.add(t);
			}
		}
		return tms;
	}

	/**
	 * <b>~ Start countdown ~</b>
	 */
	private void gameStartCountDown() {
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
				sysCountdown--;
			}
		}, sysTpCoolDown, 20);
	}

	/**
	 * <b>~ UHC Timer / Mark ~</b>
	 */
	protected void uhcTimer() {
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