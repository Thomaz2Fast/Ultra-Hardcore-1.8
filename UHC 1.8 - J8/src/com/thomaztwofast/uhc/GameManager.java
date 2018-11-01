/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2018> Thomaz2Fast
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
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.thomaztwofast.uhc.data.Status;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.events.EvDisabled;
import com.thomaztwofast.uhc.events.EvEnd;
import com.thomaztwofast.uhc.events.EvGame;
import com.thomaztwofast.uhc.lib.F;
import com.thomaztwofast.uhc.lib.S;

public class GameManager {
	private Main pl;
	public Menu menu;
	public Server server;
	public Teams teams;
	public Book book;
	public DamagerLogger damagerLogger;
	public HashMap<String, Location> locations = new HashMap<>();
	public List<String> inGamePlayers = new ArrayList<>();
	public HashMap<String, BukkitTask> offlineTasks = new HashMap<>();
	private List<String> offlinePlayers = new ArrayList<>();
	private List<ShapedRecipe> recipes = new ArrayList<>();
	private BukkitTask gameTask[] = { null, null };
	private int[] markerData = { 1, 0 };
	private int[] countdownData = { 120, 30, 0 };
	private Random r = new Random();
	private String lastTeamTp = "";
	public long timestamp;

	public GameManager(Main pl) {
		this.pl = pl;
		menu = new Menu(pl);
		server = new Server(pl);
		teams = new Teams(pl);
		book = new Book(pl);
		damagerLogger = new DamagerLogger(pl);
	}

	public void addOfflineTimer(String name) {
		BukkitTask task = pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				if (pl.config.gameInTeam) {
					Team team = pl.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(name);
					broadcastMessage(F.strReplace(pl.config.kickerMsg, team.getColor() + name + "\u00A7R"));
					team.removeEntry(name);
					if (team.getSize() == 0) {
						locations.remove(team.getName());
						team.unregister();
					}
				} else {
					broadcastMessage(F.strReplace(pl.config.kickerMsg, name));
					locations.remove(name);
				}
				inGamePlayers.remove(name);
				offlineTasks.remove(name);
				gameCheck();
			}
		}, ((60 * pl.config.kickerDelay) * 20) + (pl.status.ordinal() == 7 ? 0 : countdownData[0]));
		offlineTasks.put(name, task);
	}

	public void load() {
		if (pl.config.pluginEnable) {
			Scoreboard scoreboard = pl.getServer().getScoreboardManager().getMainScoreboard();
			pl.status = Status.LOADING;
			pl.getServer().getPluginManager().registerEvents(new EvGame(pl), pl);
			if (!pl.config.chatEnable)
				AsyncPlayerChatEvent.getHandlerList().unregister(pl);
			menu.load();
			if (scoreboard.getObjective("hp") == null)
				scoreboard.registerNewObjective("hp", "health", "hp").setDisplaySlot(DisplaySlot.PLAYER_LIST);
			if (pl.config.gamePlayerListHearts)
				activeListHeath();
			if (pl.config.gameInTeam)
				teams.load();
			if (pl.config.bookEnable)
				book.load();
			recipes.add(F.recipe(pl, F.item(Material.GOLDEN_APPLE, "&bDefault Head Apple"), "gh_default", "xxx|xhx|xxx", 'x', Material.GOLD_NUGGET, 'h', Material.PLAYER_HEAD));
			recipes.add(F.recipe(pl, F.item(Material.GOLDEN_APPLE, "&dGolden Head Apple"), "gh_golden", "xxx|xhx|xxx", 'x', Material.GOLD_INGOT, 'h', Material.PLAYER_HEAD));
			if (pl.config.headEnable) {
				if (pl.config.headDefault)
					pl.getServer().addRecipe(recipes.get(0));
				if (pl.config.headGolden)
					pl.getServer().addRecipe(recipes.get(1));
			}
			pl.getServer().getWorlds().forEach(e -> {
				if (!e.getName().equals("uhc_lobby")) {
					e.setDifficulty(Difficulty.PEACEFUL);
					e.setPVP(false);
					e.setTime(pl.config.worldTime);
					e.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
					pl.updateWorldGamerules(e);
				}
			});
			if (pl.config.serverEnable) {
				server.load();
				return;
			}
			pl.PLAYERS.values().forEach(e -> {
				e.resetPlayer(true);
				e.getHubItems();
			});
			pl.status = Status.WAITING;
			return;
		}
		pl.getServer().getPluginManager().registerEvents(new EvDisabled(pl), pl);
	}

	public Location getEntryLocation(UHCPlayer u) {
		return locations.get(pl.config.gameInTeam ? u.player.getScoreboard().getEntryTeam(u.player.getName()).getName() : u.player.getName());
	}

	public void removeInGamePlayer(UHCPlayer u) {
		inGamePlayers.remove(u.player.getName());
		if (pl.config.gameInTeam) {
			Team team = u.player.getScoreboard().getEntryTeam(u.player.getName());
			team.removeEntry(u.player.getName());
			if (team.getSize() == 0) {
				locations.remove(team.getName());
				team.unregister();
			}
		} else
			locations.remove(u.player.getName());
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				if (u.player.isOnline())
					u.player.setGameMode(GameMode.SPECTATOR);
			}
		}, 25);
		gameCheck();
	}

	public void removeOfflineTimer(UHCPlayer u) {
		if (offlinePlayers.contains(u.player.getName())) {
			u.resetPlayer(true);
			u.player.teleport(locations.get((pl.config.gameInTeam ? u.player.getScoreboard().getEntryTeam(u.player.getName()).getName() : u.player.getName())).add(0.5, 1.5, 0.5));
			offlinePlayers.remove(u.player.getName());
		}
		if (pl.status.equals(Status.STARTING) && pl.config.freezeEnable) {
			pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
				public void run() {
					u.setCustomWorldborder(locations.get((pl.config.gameInTeam ? u.player.getScoreboard().getEntryTeam(u.player.getName()).getName() : u.player.getName())), pl.config.freezeSize);
				}
			}, 0);
		}
		if (pl.status.equals(Status.INGAME) && !u.player.getGameMode().equals(GameMode.SURVIVAL))
			u.player.setGameMode(GameMode.SURVIVAL);
		offlineTasks.get(u.player.getName()).cancel();
		offlineTasks.remove(u.player.getName());
	}

	public ItemStack setGoldenHeadName(UUID uuid) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(pl.getServer().getOfflinePlayer(uuid));
		item.setItemMeta(meta);
		return item;
	}

	public void setGoldenHeadRegen(UHCPlayer u, boolean isGolden) {
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				if (u.player.hasPotionEffect(PotionEffectType.REGENERATION)) {
					u.player.removePotionEffect(PotionEffectType.REGENERATION);
					u.player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (isGolden ? 110 : 140), (isGolden ? 2 : 1)));
				}
			}
		}, 0);
	}

	public ItemMeta setGoldenHeadLore(ItemStack item, ItemStack skullItem) {
		SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(Arrays.asList("\u00A77Head of \u00A7A" + (skullMeta.serialize().containsKey("skull-owner") ? skullMeta.serialize().get("skull-owner") : "Unknown")));
		meta.addEnchant(Enchantment.DURABILITY, 0, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		return meta;
	}

	public void startGame(int value) {
		pl.status = Status.STARTING;
		List<Location> localtions = setSpawnLocations(value);
		int i = 0;
		for (UHCPlayer u : pl.PLAYERS.values()) {
			if (pl.config.gameInTeam)
				if (u.player.getScoreboard().getEntryTeam(u.player.getName()) == null)
					setPlayerAsSpectator(u);
				else
					u.resetPlayer(true);
			else {
				if (u.player.getGameMode().equals(GameMode.ADVENTURE)) {
					u.resetPlayer(true);
					inGamePlayers.add(u.player.getName());
					locations.put(u.player.getName(), localtions.get(i));
					teleportPlayerToArena(u.player.getName());
					countdownData[0] += 60;
					i++;
				} else
					setPlayerAsSpectator(u);
			}
		}
		if (pl.config.gameInTeam) {
			Scoreboard scoreboard = pl.getServer().getScoreboardManager().getMainScoreboard();
			List<Team> team = new ArrayList<>();
			for (ChatColor color : ChatColor.values()) {
				if (scoreboard.getTeam(color.name()) != null && scoreboard.getTeam(color.name()).getSize() != 0) {
					if (color.isColor()) {
						team.add(scoreboard.getTeam(color.name()));
						locations.put(color.name(), localtions.get(i));
						for (String e : scoreboard.getTeam(color.name()).getEntries()) {
							inGamePlayers.add(e);
							teleportPlayerToArena(e);
						}
						countdownData[0] += 60;
						i++;
					} else
						scoreboard.getTeam(color.name()).unregister();
				}
			}
		}
		S str = new S(false);
		str.setTitle("\u00A7C\u00A7LULTRA HARDCORE - " + (pl.config.gameInTeam ? "TEAM" : "SOLO") + " MODE");
		str.addText("\n \u00A79> \u00A7AYou will be teleported in a few seconds.");
		str.addText("\u00A79> \u00A76This game will start in \u00A7C" + (countdownData[1] + (countdownData[0] / 20)) + "\u00A76 seconds.");
		str.addText("\u00A76\u00A7LGood Luck");
		for (String name : inGamePlayers) {
			if (pl.getRegisterPlayer(name) != null) {
				UHCPlayer u = pl.getRegisterPlayer(name);
				u.player.sendMessage(str.print());
				u.player.sendTitle("\u00A7A\u00A7LTeleported " + (pl.config.gameInTeam ? "Teams" : "Players"), "\u00A77Please wait", 15, 250, 0);
				if (!pl.config.serverEnable)
					u.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2400, 1));
			}
		}
		taskCountdown();
	}

	public void unLoad() {
		if (pl.config.pluginEnable) {
			Scoreboard scoreboard = pl.getServer().getScoreboardManager().getMainScoreboard();
			if (pl.config.serverEnable) {
				server.unLoad();
			}
			if (scoreboard.getObjective("hp") != null)
				scoreboard.getObjective("hp").unregister();
			if (pl.config.gameInTeam)
				teams.unload();
			pl.getServer().getWorlds().forEach(b -> {
				b.setPVP(true);
				b.getWorldBorder().setSize(Integer.MAX_VALUE);
				b.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
				b.setGameRule(GameRule.NATURAL_REGENERATION, true);
				b.setGameRule(GameRule.KEEP_INVENTORY, false);
			});
			if (new File(pl.getDataFolder(), "data").exists()) {
				try {
					FileUtils.deleteDirectory(new File(pl.getDataFolder(), "data"));
				} catch (IOException err) {
					pl.log(1, "Error, Directory 'data' could not deleted!");
				}
			}
		}
	}

	public void updateGoldenHead() {
		List<ItemStack> items = new ArrayList<>();
		List<Recipe> recipes = new ArrayList<>(this.recipes);
		recipes.forEach(e -> items.add(e.getResult()));
		if (pl.config.headEnable) {
			if (!pl.config.headDefault)
				recipes.remove(recipes.get(0));
			if (!pl.config.headGolden)
				recipes.remove(recipes.get(1));
		} else
			recipes.clear();
		pl.getServer().recipeIterator().forEachRemaining(e -> {
			if (e.getResult().hasItemMeta() && e.getResult().getItemMeta().serialize().get("meta-type").equals("UNSPECIFIC") && !items.contains(e.getResult()))
				recipes.add(e);
		});
		pl.getServer().resetRecipes();
		recipes.forEach(e -> pl.getServer().addRecipe(e));
	}

	// ---------------------------------------------------------------------------

	// TODO Check if Spigot is going to implement this method / function on the objective class.
	private void activeListHeath() {
		try {
			Class<?> craftBoard = Class.forName("org.bukkit.craftbukkit." + pl.NMS_VER + ".scoreboard.CraftScoreboard");
			Class<?> craftHealt = Class.forName("net.minecraft.server." + pl.NMS_VER + ".IScoreboardCriteria");
			Object handle = craftBoard.getDeclaredMethod("getHandle", new Class[0]).invoke(craftBoard.cast(pl.getServer().getScoreboardManager().getMainScoreboard()), new Object[0]);
			handle = handle.getClass().getMethod("c", new Class[] { String.class }).invoke(handle, "hp");
			handle.getClass().getDeclaredMethod("a", new Class[] { craftHealt.getClasses()[0] }).invoke(handle, craftHealt.getClasses()[0].getEnumConstants()[1]);
		} catch (Exception e) {
			pl.log(1, "Could not update player list heaths settings.");
		}
	}

	private void broadcastMessage(String input) {
		pl.PLAYERS.values().forEach(e -> e.player.sendMessage(input));
	}

	private void endGame() {
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				String winner = getWinner();
				S str = new S(false);
				str.setTitle("ULTRA HARDCORE 1.8");
				str.addText("\n " + winner + "\u00A76\u00A7l has won the game.");
				pl.PLAYERS.values().forEach(e -> {
					e.player.sendMessage(str.print());
					e.player.sendTitle(winner, "\u00A77won the game", 10, 60, 10);
					e.playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f);
				});
				pl.getServer().getWorlds().forEach(e -> {
					e.getWorldBorder().setSize(e.getWorldBorder().getSize());
					e.setGameRule(GameRule.KEEP_INVENTORY, true);
				});
				if (offlineTasks.size() != 0) {
					offlineTasks.values().forEach(e -> e.cancel());
					offlineTasks.clear();
				}
				if (pl.config.serverEnable)
					server.shutdown();
				else
					pl.getServer().getPluginManager().registerEvents(new EvEnd(pl, winner), pl);
			}

			private String getWinner() {
				if (pl.config.gameInTeam) {
					Team team = pl.getServer().getScoreboardManager().getMainScoreboard().getTeam(locations.keySet().iterator().next());
					team.getEntries().forEach(e -> pl.gameManager.damagerLogger.giveItem(e));
					return "\u00A7r\u00A76\u00A7lTeam " + team.getColor() + team.getDisplayName();
				} else {
					pl.gameManager.damagerLogger.giveItem(inGamePlayers.get(0));
					return "\u00A7r\u00A7l" + inGamePlayers.get(0) + "\u00A7r";
				}
			}
		}, 20);
	}

	private void gameCheck() {
		if (locations.size() > 1)
			return;
		pl.status = Status.FINISHED;
		gameTask[0].cancel();
		gameTask[1].cancel();
		endGame();
	}

	private int setLocationDistance(int value) {
		double size = 308 - (value * 3.636363636);
		if (size < 50)
			return 0;
		return (int) size;
	}

	private void setPlayerAsSpectator(UHCPlayer u) {
		u.resetPlayer(false);
		u.sendCmdMessage("UHC", "You have been move to spectator mode.");
		if (pl.config.serverEnable) {
			u.player.teleport(pl.getServer().getWorlds().get(0).getSpawnLocation());
			if (pl.config.serverIsBungeeCord)
				u.player.getInventory().setItem(pl.config.serverInventorySlot, pl.gameManager.server.itemStack);
		}
	}

	private List<Location> setSpawnLocations(int size) {
		List<Location> locations = new ArrayList<>();
		int[] data = { (pl.config.worldSize - 10), setLocationDistance(size), 1, 0, 0 };
		if (data[1] == 0) {
			for (int i = 0; i < size; i++)
				locations.add(setSpawnPoint(data[0]));
			return locations;
		}
		while (data[2] <= size) {
			Location location = setSpawnPoint(data[0]);
			if (locations.size() != 0) {
				for (Location e : locations) {
					if (location.distance(e) < data[1]) {
						data[3]++;
						data[4] = 1;
						break;
					}
				}
			}
			Block block = location.add(0, -1, 0).getBlock();
			if (block.getType().equals(Material.LAVA) || block.getType().equals(Material.WATER))
				data[4] = 1;
			if (data[4] == 0 || data[3] > 49) {
				if (data[3] > 49)
					for (int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++)
						for (int z = location.getBlockZ() - 1; z <= location.getBlockZ() + 1; z++) {
							Block b = location.getWorld().getBlockAt(x, location.getBlockY(), z);
							if (b.getType().equals(Material.LAVA) || b.getType().equals(Material.WATER))
								b.setType(Material.DIRT);
						}
				locations.add(location);
				data[2]++;
				data[3] = 0;
				data[4] = 0;
			} else
				data[3]++;
		}
		return locations;
	}

	private Location setSpawnPoint(int pos) {
		Location location = new Location(pl.getServer().getWorlds().get(0), (-pos + r.nextInt(pos * 2)), 64, (-pos + r.nextInt(pos * 2)));
		return new Location(location.getWorld(), location.getX(), location.getWorld().getHighestBlockYAt(location), location.getZ());
	}

	private void taskCountdown() {
		gameTask[0] = pl.getServer().getScheduler().runTaskTimer(pl, new Runnable() {
			public void run() {
				if (countdownData[1] == 0) {
					pl.status = Status.INGAME;
					gameTask[0].cancel();
					timestamp = System.currentTimeMillis();
					pl.getServer().getWorlds().forEach(w -> {
						if (!w.getName().equals("uhc_lobby")) {
							w.setPVP(true);
							w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, pl.config.grDaylight);
							w.setGameRule(GameRule.NATURAL_REGENERATION, false);
							w.setGameRule(GameRule.KEEP_INVENTORY, false);
							w.setDifficulty((pl.config.worldDifficulty == 1 ? Difficulty.EASY : pl.config.worldDifficulty == 2 ? Difficulty.NORMAL : Difficulty.HARD));
							w.getWorldBorder().setCenter(0d, 0d);
							if (pl.config.borderStartSize < pl.config.worldSize) {
								w.getWorldBorder().setSize((pl.config.worldSize * 2));
							} else {
								w.getWorldBorder().setSize((pl.config.borderStartSize * 2));
							}
						}
					});
					if (pl.config.borderDelay != 0) {
						gameTask[0] = pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
							public void run() {
								worldBorderShrink();
							}
						}, (pl.config.borderDelay * 20));
					} else
						worldBorderShrink();
					if (pl.config.markerMsg.length() != 0 && pl.config.markerDelay != 0)
						taskMarkers();
				}
				inGamePlayers.forEach(e -> {
					if (pl.getRegisterPlayer(e) != null) {
						UHCPlayer u = pl.getRegisterPlayer(e);
						if (countdownData[1] == 0) {
							u.player.sendTitle("", "\u00A7A\u00A7LBEGIN!", 0, 21, 10);
							u.playSound(Sound.ENTITY_ARROW_HIT_PLAYER, 1f);
							u.player.setFoodLevel(20);
							u.player.setHealth(u.player.getHealthScale());
							u.player.setSaturation(5f);
							u.player.setNoDamageTicks(0);
							u.player.setGameMode(GameMode.SURVIVAL);
						} else if (countdownData[1] < 6) {
							u.player.sendTitle("", "\u00A76Starting in \u00A7C\u00A7L" + countdownData[1] + "\u00A7R\u00A76 Second" + (countdownData[1] == 1 ? "" : "s"), 5, 21, 5);
							u.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f);
						} else if (countdownData[1] < 10)
							u.player.sendTitle("", "\u00A77" + countdownData[1], 0, 21, 5);
						else if (countdownData[1] == 10) {
							u.sendActionMessage("");
							u.player.sendTitle("", "\u00A7c" + countdownData[1], 0, 21, 5);
							u.playSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1.8f);
						} else {
							if (countdownData[1] % 10 == 0) {
								u.sendActionMessage("\u00A7c" + countdownData[1]);
								u.playSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1.8f);
							} else
								u.sendActionMessage("\u00A77" + countdownData[1]);
						}
					}
				});
				countdownData[1]--;
			}
		}, countdownData[0], 20);
	}

	private void taskMarkers() {
		gameTask[1] = pl.getServer().getScheduler().runTaskTimer(pl, new Runnable() {
			public void run() {
				markerData[0]++;
				markerData[1] += pl.config.markerDelay;
				broadcastMessage(F.strReplaceMatch(pl.config.markerMsg, "%0|" + markerData[1], "%1|" + (markerData[0] - 1), "%2|" + markerData[0]));
			}
		}, (20 * (pl.config.markerDelay * 60)), (20 * (pl.config.markerDelay * 60)));
	}

	private void teleportPlayerToArena(String name) {
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				if (pl.getRegisterPlayer(name) != null) {
					UHCPlayer u = pl.getRegisterPlayer(name);
					if (pl.config.gameInTeam)
						u.player.teleport(locations.get(u.player.getScoreboard().getEntryTeam(name).getName()).add(0.5, 1.5, 0.5));
					else
						u.player.teleport(locations.get(u.player.getName()).add(0.5, 1.5, 0.5));
					u.player.setVelocity(new Vector());
					u.player.setNoDamageTicks(0);
					u.player.damage(2d);
					u.player.setNoDamageTicks((60 * countdownData[0]));
					if (u.player.hasPotionEffect(PotionEffectType.BLINDNESS))
						u.player.removePotionEffect(PotionEffectType.BLINDNESS);
					u.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					if (pl.config.freezeEnable)
						u.setCustomWorldborder(u.player.getLocation(), pl.config.freezeSize);
				} else {
					offlinePlayers.add(name);
					addOfflineTimer(name);
				}
				if (pl.config.gameInTeam && !lastTeamTp.equals(pl.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(name).getName())) {
					countdownData[2]++;
					lastTeamTp = pl.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(name).getName();
				} else if (!pl.config.gameInTeam) {
					countdownData[2]++;
				}
				for (String e : inGamePlayers) {
					if (pl.getRegisterPlayer(e) != null)
						pl.getRegisterPlayer(e).player.sendTitle("\u00A7A\u00A7LTeleported " + (pl.config.gameInTeam ? "Teams" : "Players"), "\u00A77" + countdownData[2] + " / " + locations.size(), 0, countdownData[2] == locations.size() ? 40 : 250, countdownData[2] == locations.size() ? 10 : 0);
				}
			}
		}, countdownData[0]);
	}

	private void worldBorderShrink() {
		if (pl.config.borderTime != 0) {
			pl.getServer().getWorlds().forEach(e -> {
				if (!e.getName().equals("uhc_lobby"))
					e.getWorldBorder().setSize((pl.config.borderEndSize * 2), pl.config.borderTime);
			});
		}
	}
}
