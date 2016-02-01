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
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.custom.DamagerLog;
import com.thomaztwofast.uhc.custom.JChat;
import com.thomaztwofast.uhc.custom.UhcMenu;
import com.thomaztwofast.uhc.custom.UhcServer;
import com.thomaztwofast.uhc.custom.UhcTeam;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.GameStatus;
import com.thomaztwofast.uhc.data.PlayerData;
import com.thomaztwofast.uhc.data.GameStatus.Stat;
import com.thomaztwofast.uhc.events.EvGame;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;

public class GameManager {
	private Main pl;
	private Config c;
	private GameStatus gs = new GameStatus();
	private UhcMenu um;
	private UhcTeam ut;
	private UhcServer us;
	private DamagerLog dmg = new DamagerLog();
	private ItemStack bkIm;
	private ItemStack bcHub;
	private ArrayList<ShapedRecipe> srArry = new ArrayList<>();
	private HashMap<String, Location> igTmsLoc = new HashMap<>();
	private HashMap<UUID, Location> igPlsLoc = new HashMap<>();
	private ArrayList<UUID> igPls = new ArrayList<>();
	private ArrayList<String> igNoName = new ArrayList<>();
	private HashMap<UUID, int[]> igOffs = new HashMap<>();
	private HashMap<String, Integer> igOffns = new HashMap<>();
	private int bkPgs = 0;
	private long sysGameTimer;
	private int sysCoolDown = 60;
	private int sysCountdown = 30;
	private BukkitTask sysTimer1;
	private BukkitTask sysTimer2;
	private int sysMkDay = 0;
	private int sysMkMin = 0;

	public GameManager(Main main) {
		pl = main;
		c = main.getPlConf();
	}

	// :: PUBLIC :: //

	/**
	 * Get > Game Status
	 */
	public GameStatus getStatus() {
		return gs;
	}

	/**
	 * Get > UHC Menu / Gui
	 */
	public UhcMenu getMenu() {
		return um;
	}

	/**
	 * Get > UHC Team
	 */
	public UhcTeam getTeam() {
		return ut;
	}

	/**
	 * Get > UHC Server
	 */
	public UhcServer getServer() {
		return us;
	}

	/**
	 * Get > DamagerLogger
	 */
	public DamagerLog getDmg() {
		return dmg;
	}

	/**
	 * Get > UHC Book
	 */
	public ItemStack getBook() {
		return bkIm;
	}

	public ItemStack getHubItem() {
		return bcHub;
	}

	/**
	 * Get > Alive Teams
	 */
	public int getAliveTeams() {
		return igTmsLoc.size();
	}

	/**
	 * Get > Alive players
	 */
	public int getAlivePlayers() {
		return igPls.size() + igNoName.size();
	}

	/**
	 * Get > Alive offline players
	 */
	public int getAliveOfflinePlayers() {
		return igOffs.size() + igOffns.size();
	}

	/**
	 * Get > Game time
	 */
	public long getGameTime() {
		return sysGameTimer;
	}

	/**
	 * Is > Player alive?
	 */
	public boolean isAlive(UUID u) {
		return igPls.contains(u);
	}

	/**
	 * Is > Alive offline player by UUID?
	 */
	public boolean isOffline(UUID u) {
		return igOffs.containsKey(u);
	}

	/**
	 * Is > Alive offline player by name?
	 */
	public boolean isOfflineByName(String n) {
		return igOffns.containsKey(n);
	}

	/**
	 * Load UHC
	 */
	@SuppressWarnings("deprecation")
	public void loadUHC() {
		gs.setStat(Stat.LOADING);
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		um = new UhcMenu(pl);
		pl.getServer().getPluginManager().registerEvents(new EvGame(pl), pl);
		if (sb.getObjective("hp") == null) {
			Objective hp = sb.registerNewObjective("hp", "health");
			hp.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}
		// IF > TEAM MODE
		if (c.g_teamMode()) {
			ut = new UhcTeam(pl);
		}
		// IF > BOOK ENABLED
		if (c.book_Enabled()) {
			bkIm = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bm = (BookMeta) bkIm.getItemMeta();
			bm.setAuthor(pl.getDescription().getName());
			bm.setTitle(c.book_Name());
			bm.setDisplayName(bm.getTitle());
			if (c.book_Lord().size() != 0) {
				bm.setLore(c.book_Lord());
			}
			if (c.book_Pages().size() != 0) {
				bm.setPages(c.book_Pages());
				bkPgs = bm.getPageCount();
			}
			bkIm.setItemMeta(bm);
			if (c.book_ShowSettings()) {
				for (int i = 1; i < 6; i++) {
					updateBookSettings(i);
				}
			}
		}
		// GOLDEN HEAD
		ItemStack is = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§bDefault Head Apple");
		is.setItemMeta(im);
		ShapedRecipe sr = new ShapedRecipe(is);
		sr.shape("xxx", "xhx", "xxx");
		sr.setIngredient('x', Material.GOLD_NUGGET);
		sr.setIngredient('h', new MaterialData(Material.SKULL_ITEM, (byte) 3));
		srArry.add(sr);
		im.setDisplayName("§dGolden Head Apple");
		is.setItemMeta(im);
		sr = new ShapedRecipe(is);
		sr.shape("xxx", "xhx", "xxx");
		sr.setIngredient('x', Material.GOLD_INGOT);
		sr.setIngredient('h', new MaterialData(Material.SKULL_ITEM, (byte) 3));
		srArry.add(sr);
		updateGoldenHeadApple();
		for (World w : pl.getServer().getWorlds()) {
			if (!w.getName().equalsIgnoreCase("uhc_lobby")) {
				w.setDifficulty(Difficulty.PEACEFUL);
				w.setPVP(false);
				w.setTime(c.ws_SunTime());
				w.setGameRuleValue("doEntityDrops", "" + c.gr_EntityDrops());
				w.setGameRuleValue("doFireTick", "" + c.gr_FireTick());
				w.setGameRuleValue("doMobLoot", "" + c.gr_MobLoot());
				w.setGameRuleValue("doMobSpawning", "" + c.gr_MobSpawning());
				w.setGameRuleValue("doTileDrops", "" + c.gr_TileDrops());
				w.setGameRuleValue("mobGriefing", "" + c.gr_MobGriefing());
				w.setGameRuleValue("randomTickSpeed", "" + c.gr_RandomTick());
				w.setGameRuleValue("reducedDebugInfo", "" + c.gr_ReducedDebugInfo());
				w.setGameRuleValue("spectatorsGenerateChunks", "" + c.gr_SpectatorsGenerateChunks());
				if (c.gr_ReducedDebugInfo()) {
					for (PlayerData p : pl.getRegPlayerData().values()) {
						p.cp.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(p.cp.getHandle().playerInteractManager.player, (byte) 22));
					}
				} else {
					for (PlayerData p : pl.getRegPlayerData().values()) {
						p.cp.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(p.cp.getHandle().playerInteractManager.player, (byte) 23));
					}
				}
				w.setGameRuleValue("doDaylightCycle", "false");
				w.setGameRuleValue("naturalRegeneration", "true");
				w.setGameRuleValue("keepInventory", "true");
			}
		}
		clearAllPlayers();
		giveAllPlayerHubItems();
		if (c.server()) {
			if (c.server_BungeeCord()) {
				pl.getServer().getMessenger().registerOutgoingPluginChannel(pl, "BungeeCord");
				String bci = c.server_BungeeCordItem();
				if (Material.matchMaterial(bci) == null) {
					bci = "WATCH";
				}
				bcHub = pl.getPlFun().createItem(Material.matchMaterial(bci), 0, "§eReturn to " + c.server_BungeeCordHub(), null);
			}
			if (c.g_teamMode()) {
				ut.setActive();
			}
			us = new UhcServer(pl);
		} else {
			gs.setStat(Stat.WAITING);
		}
	}

	/**
	 * Unload UHC
	 */
	public void unLoadUHC() {
		if (c.server()) {
			us.unLoad();
		}
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		if (sb.getObjective("hp") != null) {
			sb.getObjective("hp").unregister();
		}
		if (c.g_teamMode()) {
			ut.unLoadTeam();
		}
		for (World w : pl.getServer().getWorlds()) {
			w.setGameRuleValue("doDaylightCycle", "true");
			w.setGameRuleValue("naturalRegeneration", "true");
			w.setGameRuleValue("keepInventory", "false");
			w.getWorldBorder().setSize(1000000);
			w.setPVP(true);
		}
		File dl = new File(pl.getDataFolder(), "data");
		if (dl.exists()) {
			try {
				FileUtils.deleteDirectory(dl);
			} catch (IOException e) {
				pl.getPlLog().warn("[DAMAGER LOGGER] Directory 'data' could not deleted, " + e.getMessage());
			}
		}
	}

	/**
	 * Update > UHC Book Settings
	 */
	public void updateBookSettings(int i) {
		BookMeta bm = (BookMeta) bkIm.getItemMeta();
		String s = "§n    §l§nUHC SETTINGS§r§n    §r\n \n";
		switch (i) {
		case 2:
			s += "§1Marker§r\n";
			if (c.mk_Delay() != 0) {
				s += "  §8Delay:§4 " + pl.getPlFun().asClockFormat((c.mk_Delay() * 60)) + "§r\n";
			} else {
				s += "  §8Status:§4 Disabled§r\n";
			}
			s += " \n§1Freezing Players§r\n";
			s += "  §8Status:§4 " + (c.fp_Enabled() ? "On" : "Off") + "§r\n";
			s += "  §8Radius Size:§4 " + c.fp_Size() + "§r\n";
			s += " \n§1Disconnected Players§r\n";
			s += "  §8Max Timeout:§4 " + pl.getPlFun().asClockFormat((c.of_Timeout() * 60)) + "§r\n";
			break;
		case 3:
			s += "§1Golden Head§r\n";
			s += "  §8Status:§4 " + (c.gh_Enalbed() ? "On" : "Off") + "§r\n";
			if (c.gh_Enalbed()) {
				s += "  §8Default Apple:§4 " + (c.gh_DefaultApple() ? "On" : "Off") + "§r\n";
				s += "  §8Golden Apple:§4 " + (c.gh_GoldenApple() ? "On" : "Off") + "§r\n";
			}
			s += " \n§1Global Chat§r\n";
			if (c.g_teamMode()) {
				s += "  §8None Team Chat:§4 " + (c.gc_TeamDefault().length() != 0 ? "On" : "Off") + "§r\n";
				s += "  §8Team Chat:§4 " + (c.gc_TeamTeam().length() != 0 ? "On" : "Off") + "§r\n";
				s += "  §8Private Chat:§4 " + (c.gc_TeamPrivate().length() != 0 ? "On" : "Off") + "§r\n";
			} else {
				s += "  §8Default Chat:§4 " + (c.gc_Default().length() != 0 ? "On" : "Off") + "§r\n";
			}
			s += "  §8Spectator Chat:§4 " + (c.gc_Spectator().length() != 0 ? "On" : "Off") + "§r\n";
			break;
		case 4:
			s += "§1Minecraft Gamerules§r\n";
			s += "  §8Daylight Cycle:§4 " + (c.gr_DaylightCycle() ? "On" : "Off") + "§r\n";
			s += "  §8Entity Drops:§4 " + (c.gr_EntityDrops() ? "On" : "Off") + "§r\n";
			s += "  §8Fire Tick:§4 " + (c.gr_FireTick() ? "On" : "Off") + "§r\n";
			s += "  §8Mob Loot:§4 " + (c.gr_MobLoot() ? "On" : "Off") + "§r\n";
			s += "  §8Mob Spawning:§4 " + (c.gr_MobSpawning() ? "On" : "Off") + "§r\n";
			s += "  §8Tile Dropse:§4 " + (c.gr_TileDrops() ? "On" : "Off") + "§r\n";
			s += "  §8Mob Griefing:§4 " + (c.gr_MobGriefing() ? "On" : "Off") + "§r\n";
			s += "  §8Tick Speed:§4 " + c.gr_RandomTick() + "§r\n";
			s += "  §8Debug Info:§4 " + (c.gr_ReducedDebugInfo() ? "On" : "Off") + "§r\n";
			s += "  §8Spec. Gen. Chunk:§4 " + (c.gr_SpectatorsGenerateChunks() ? "On" : "Off") + "§r\n";
			break;
		case 5:
			s += "§1Other§r\n";
			s += "  §8Damage Logger:§4 " + (c.damageLog() ? "On" : "Off") + "§r\n";
			if (c.g_teamMode()) {
				s += " \n§1Teams§r\n";
				s += "  §8Max Players: " + c.g_maxTeamPlayer() + " §4§r\n";
				s += "  §8Friendly Fire:§4 " + (c.go_Friendly() ? "On" : "Off") + "§r\n";
				s += "  §8See friendly invi:§4 " + (c.go_SeeFriendly() ? "On" : "Off") + "§r\n";
				int nt = c.go_NameTagVisibility();
				s += "  §8NameTag:§4 " + (nt == 0 ? "On" : (nt == 1 ? "Hide Other Teams" : (nt == 2 ? "Hide Own Team" : "Off")));
				break;
			}
			s = null;
			break;
		default:
			s += "§1World§r\n";
			s += "  §8Time:§4 " + pl.getPlFun().tickFormat(c.ws_SunTime()) + "§r\n";
			s += "  §8Difficulty:§4 " + (c.ws_Difficulty() == 1 ? "Easy" : (c.ws_Difficulty() == 2 ? "Normal" : "Hard")) + "§r\n";
			s += "  §8Arena Size:§4 " + c.ws_ArenaSize() + "§r\n \n";
			s += "§1World Border§r\n";
			if (c.wb_Time() != 0) {
				if (c.wb_StartDelay() != 0) {
					s += "  §8Start Delay:§4 " + pl.getPlFun().asClockFormat(c.wb_StartDelay()) + "§r\n";
				}
				s += "  §8Start Pos:§4 " + c.wb_StartPos() + "§r\n";
				s += "  §8Stop Pos:§4 " + c.wb_EndPos() + "§r\n";
				s += "  §8Shrinks:§4 " + pl.getPlFun().asClockFormat(c.wb_Time()) + "§r\n";
			} else {
				s += "  §8Start Pos:§4 " + c.wb_StartPos() + "§r\n";
			}
			break;
		}
		if (bm.getPageCount() < (bkPgs + i)) {
			if (s != null) {
				bm.addPage(s);
			}
		} else {
			bm.setPage((bkPgs + i), s);
		}
		bkIm.setItemMeta(bm);
	}

	/**
	 * Update > Golden Head Apple
	 */
	public void updateGoldenHeadApple() {
		if (c.gh_Enalbed()) {
			ArrayList<ShapedRecipe> rm = new ArrayList<>();
			if (c.gh_DefaultApple()) {
				if (!isRecipeExist(srArry.get(0).getResult())) {
					pl.getServer().addRecipe(srArry.get(0));
				}
			} else {
				rm.add(srArry.get(0));
			}
			if (c.gh_GoldenApple()) {
				if (!isRecipeExist(srArry.get(1).getResult())) {
					pl.getServer().addRecipe(srArry.get(1));
				}
			} else {
				rm.add(srArry.get(1));
			}
			if (rm.size() != 0) {
				removeRecipes(rm);
			}
		} else {
			removeRecipes(srArry);
		}
	}

	/**
	 * Clear all player's
	 */
	public void clearAllPlayers() {
		for (PlayerData p : pl.getRegPlayerData().values()) {
			p.clearPlayer(true);
		}
	}

	/**
	 * Give > All player hub item's
	 */
	public void giveAllPlayerHubItems() {
		for (PlayerData p : pl.getRegPlayerData().values()) {
			givePlayerHubItems(p);
		}
	}

	/**
	 * Give > Hub Item's
	 */
	public void givePlayerHubItems(PlayerData p) {
		if (c.book_Enabled()) {
			p.cp.getInventory().setItem(c.book_InventorySlot(), bkIm);
		}
		if (c.g_teamMode()) {
			if (ut.isActive()) {
				p.cp.getInventory().setItem(c.g_selectTeamSlot(), ut.getTeamSelecter());
			}
		}
		if (c.server()) {
			if (c.server_BungeeCord()) {
				if (gs.getStat().equals(Stat.WAITING) | gs.getStat().equals(Stat.WAITING_STARTING)) {
					p.cp.getInventory().setItem(c.server_BungeeCordInvSlot(), bcHub);
				}
			}
		}
	}

	/**
	 * Get > Skull owner name
	 */
	public ItemMeta getSkullOwnerName(ItemStack i1, ItemStack i2) {
		String name = "Unknown";
		SkullMeta sm = (SkullMeta) i2.getItemMeta();
		if (sm.getOwner() != null) {
			name = sm.getOwner();
		}
		ItemMeta im = i1.getItemMeta();
		im.setLore(Arrays.asList("§7Head of §a" + name));
		return im;
	}

	/**
	 * Give > Player golden head effect's
	 */
	public void givePlayerRegenOfGoldenHead(Player p, boolean b) {
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				if (p.hasPotionEffect(PotionEffectType.REGENERATION)) {
					p.removePotionEffect(PotionEffectType.REGENERATION);
					p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (b ? 110 : 140), (b ? 2 : 1)));
				}
			}
		}, 0);
	}

	/**
	 * Game > Start
	 */
	public void startUhcGame(int i) {
		gs.setStat(Stat.STARTING);
		gameSetup(i);
	}

	/**
	 * Game > Start offline timer
	 */
	public void gameStartOfflineTimer(UUID u, String n, int t, int y) {
		BukkitTask id = pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				if (c.g_teamMode()) {
					Team t = pl.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(n);
					broadcastMessage(c.of_Message().replaceFirst("\\{0}", t.getPrefix() + n + "§r"));
					t.removeEntry(n);
					if (t.getSize() == 0) {
						igTmsLoc.remove(t.getName());
						t.unregister();
					}
				} else {
					broadcastMessage(c.of_Message().replaceFirst("\\{0}", n));
				}
				if (u != null) {
					igPls.remove(u);
					igOffs.remove(u);
				} else {
					igNoName.remove(n);
					igOffns.remove(n);
				}
				gameStatusCheck();
			}
		}, ((60 * c.of_Timeout()) * 20) + t);
		if (u != null) {
			igOffs.put(u, new int[] { id.getTaskId(), y });
		} else {
			igOffns.put(n, id.getTaskId());
		}
	}

	/**
	 * Game > Stop offline timer
	 */
	public void gameStopOfflineTimer(PlayerData p) {
		boolean b = false;
		int[] id;
		if (igOffs.containsKey(p.cp.getUniqueId())) {
			id = igOffs.get(p.cp.getUniqueId());
		} else {
			b = true;
			id = new int[] { igOffns.get(p.cp.getName()), 1 };
			igNoName.remove(p.cp.getName());
			igPls.add(p.cp.getUniqueId());
		}
		pl.getServer().getScheduler().cancelTask(id[0]);
		if (id[1] != 0) {
			p.clearPlayer(true);
			Location l;
			if (c.g_teamMode()) {
				l = igTmsLoc.get(p.getPlayerTeam().getName());
			} else {
				l = igPlsLoc.get(p.cp.getUniqueId());
			}
			l = new Location(l.getWorld(), l.getBlockX() + .5, l.getWorld().getHighestBlockYAt(l), l.getBlockZ() + .5);
			p.cp.teleport(l);
			if (p.cp.hasPotionEffect(PotionEffectType.BLINDNESS)) {
				p.cp.removePotionEffect(PotionEffectType.BLINDNESS);
			}
			p.cp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 3), 1));
		}
		if (gs.getStat().equals(Stat.STARTING)) {
			Location l;
			if (c.g_teamMode()) {
				l = igTmsLoc.get(p.getPlayerTeam().getName());
			} else {
				l = igPlsLoc.get(p.cp.getUniqueId());
			}
			if (c.fp_Enabled()) {
				pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
					@Override
					public void run() {
						p.setCustomWorldBorder(l, c.fp_Size());
					}
				}, 5);
			}
		} else if (!p.cp.getGameMode().equals(GameMode.SURVIVAL)) {
			p.cp.setGameMode(GameMode.SURVIVAL);
		}
		if (b) {
			igOffns.remove(p.cp.getName());
		} else {
			igOffs.remove(p.cp.getUniqueId());
		}
	}

	/**
	 * Remove > Alive player
	 */
	public void removeAlivePlayer(PlayerData p) {
		igPls.remove(p.cp.getUniqueId());
		if (c.g_teamMode()) {
			Team t = p.getPlayerTeam();
			t.removeEntry(p.cp.getName());
			if (t.getSize() == 0) {
				igTmsLoc.remove(t.getName());
				t.unregister();
			}
		}
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				if (p.cp.isOnline()) {
					p.cp.setGameMode(GameMode.SPECTATOR);
				}
			}
		}, 25);
		gameStatusCheck();
	}

	/**
	 * Get > Spawn point location
	 */
	public Location getSpawnLocation(PlayerData p) {
		if (c.g_teamMode()) {
			return igTmsLoc.get(p.getPlayerTeam().getName());
		}
		return igPlsLoc.get(p.cp.getUniqueId());
	}

	/**
	 * Get > Damager logger book to player's inventor
	 */
	public void getDamagerBookToInventory(PlayerData p, boolean b) {
		if (p.hasDmgLog()) {
			p.cp.getInventory().setItem(4, p.getDamagerLoggerBook());
			if (b) {
				p.sendMessage("Dmg", "If you want to see who has damage you? Right click on the damage log book.");
				return;
			}
			JChat s = new JChat();
			s.add("Dmg> ", null, 9, null, null);
			s.add("If you want to see who has damage you? Type ", null, 7, null, null);
			s.add("/dmg", null, 14, "2|/dmg", "§e§l>§r §a/dmg§r");
			p.sendRawICMessage(s.a());
		}
	}

	/**
	 * Broadcast message to all online players.
	 */
	public void broadcastMessage(String m) {
		if (pl.getServer().getOfflinePlayers().length != 0) {
			for (Player p : pl.getServer().getOnlinePlayers()) {
				p.sendMessage(m);
			}
		}
	}

	// :: PRIVATE :: //

	/**
	 * Check if item recipe exist { Golden Head Apple }
	 */
	private boolean isRecipeExist(ItemStack is) {
		Iterator<Recipe> irl = pl.getServer().getRecipesFor(is).iterator();
		while (irl.hasNext()) {
			if (irl.next().getResult().equals(is)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove Item recipes { Golden Head Apple }
	 */
	private void removeRecipes(ArrayList<ShapedRecipe> arr) {
		for (ShapedRecipe sr : arr) {
			Iterator<Recipe> irl = pl.getServer().recipeIterator();
			while (irl.hasNext()) {
				if (irl.next().getResult().equals(sr.getResult())) {
					irl.remove();
					break;
				}
			}
		}
	}

	/**
	 * Game > Start Setup
	 */
	private void gameSetup(int i) {
		ArrayList<Location> ll = gameCreateSpawnList(i);
		int a = 0;
		if (c.g_teamMode()) {
			for (Team t : pl.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
				if (t.getSize() == 0) {
					t.unregister();
				}
			}
		}
		for (PlayerData p : pl.getRegPlayerData().values()) {
			p.clearPlayer(true);
			if (c.g_teamMode()) {
				if (p.cp.getScoreboard().getEntryTeam(p.cp.getName()) == null) {
					p.cp.setGameMode(GameMode.SPECTATOR);
					p.sendMessage("Spectator", "You have been move to spectator mode.");
					if (c.server()) {
						p.cp.teleport(pl.getServer().getWorlds().get(0).getSpawnLocation());
						if (c.server_BungeeCord()) {
							p.cp.getInventory().setItem(c.server_BungeeCordInvSlot(), bcHub);
						}
					}
				}
			}
		}
		if (c.g_teamMode()) {
			HashMap<String, UUID> tmp = new HashMap<>();
			ArrayList<Team> tl = getTeamList();
			for (Team t : tl) {
				for (String off : t.getEntries()) {
					if (pl.getRegPlayerByName(off) != null) {
						UUID u = pl.getRegPlayerByName(off).cp.getUniqueId();
						igPls.add(u);
						tmp.put(off, u);
					} else {
						igNoName.add(off);
					}
				}
				igTmsLoc.put(t.getName(), ll.get(a));
				pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
					@Override
					public void run() {
						for (String off : t.getEntries()) {
							if (pl.getRegPlayerByName(off) != null) {
								PlayerData p = pl.getRegPlayerByName(off);
								if (igNoName.contains(off)) {
									igNoName.remove(off);
									igPls.add(p.cp.getUniqueId());
								}
								Location l = igTmsLoc.get(t.getName());
								l = new Location(l.getWorld(), l.getBlockX() + .5, l.getWorld().getHighestBlockYAt(l), l.getBlockZ() + .5);
								p.cp.teleport(l);
								if (c.fp_Enabled()) {
									p.setCustomWorldBorder(l, c.fp_Size());
								}
								p.cp.setNoDamageTicks(0);
								p.cp.damage(2.0);
								p.cp.setNoDamageTicks(60 * sysCoolDown);
								if (p.cp.hasPotionEffect(PotionEffectType.BLINDNESS)) {
									p.cp.removePotionEffect(PotionEffectType.BLINDNESS);
								}
								p.cp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 3), 1));
							} else {
								gameStartOfflineTimer(tmp.get(off), off, (sysCountdown + (sysCoolDown / 20)) * 20, 1);
							}
						}
					}
				}, sysCoolDown);
				sysCoolDown += 30;
				a++;
			}
		} else {
			for (PlayerData p : pl.getRegPlayerData().values()) {
				igPls.add(p.cp.getUniqueId());
				igPlsLoc.put(p.cp.getUniqueId(), ll.get(a));
				pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
					@Override
					public void run() {
						if (p.cp.isOnline()) {
							Location l = igPlsLoc.get(p.cp.getUniqueId());
							l = new Location(l.getWorld(), l.getBlockX() + .5, l.getWorld().getHighestBlockYAt(l), l.getBlockZ() + .5);
							p.cp.teleport(l);
							if (c.fp_Enabled()) {
								p.setCustomWorldBorder(l, c.fp_Size());
							}
							p.cp.setNoDamageTicks(0);
							p.cp.damage(2.0);
							p.cp.setNoDamageTicks(60 * sysCoolDown);
							if (p.cp.hasPotionEffect(PotionEffectType.BLINDNESS)) {
								p.cp.removePotionEffect(PotionEffectType.BLINDNESS);
							}
							p.cp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 3), 1));
						} else {
							gameStartOfflineTimer(p.cp.getUniqueId(), p.cp.getName(), (sysCountdown + (sysCoolDown / 20)) * 20, 1);
						}
					}
				}, sysCoolDown);
				sysCoolDown += 30;
				a++;
			}
		}
		String s = "§8§m--------------------------------------------§r";
		s += "\n §c§lULTRA HARDCORE - " + (c.g_teamMode() ? "TEAM" : "SOLO") + " MODE§r\n \n";
		s += " §9§l> §r§aYou will be teleported in few second.§r\n";
		s += " §9§l>§r §6This game is starting in §c" + (30 + (sysCoolDown / 20)) + "§6 seconds.§r\n";
		s += " \n §6§lGood Luck!§r\n";
		s += "\n§8§m--------------------------------------------§r";
		for (UUID uid : igPls) {
			if (pl.getRegPlayer(uid) != null) {
				PlayerData p = pl.getRegPlayer(uid);
				p.cp.sendMessage(s);
				if (!c.server()) {
					p.cp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 120), 1));
				}
			}
		}
		gameStartCountdown();
	}

	/**
	 * Create spawn point for each player / team
	 */
	private ArrayList<Location> gameCreateSpawnList(int i) {
		ArrayList<Location> l = new ArrayList<>();
		int sz = c.ws_ArenaSize() - 10;
		int dz = getDistanceFromPlayer(sz, i);
		if (dz == 0) {
			for (int a = 0; a < i; a++) {
				l.add(getNewSpawnLocation(sz));
			}
			return l;
		}
		int a = 1;
		int b;
		int c = 0;
		while (a <= i) {
			b = 1;
			Location nl = getNewSpawnLocation(sz);
			if (l.size() != 0) {
				for (Location lb : l) {
					if (nl.distance(lb) <= dz) {
						b = 0;
						c++;
						break;
					}
				}
			}
			if (b == 1) {
				Location tl = new Location(nl.getWorld(), nl.getX(), nl.getWorld().getHighestBlockYAt(nl), nl.getZ());
				Block bt = tl.add(0, -1, 0).getBlock();
				if (bt.getType().equals(Material.STATIONARY_WATER) | bt.getType().equals(Material.STATIONARY_LAVA)) {
					c++;
				} else {
					l.add(nl);
					a++;
					c = 0;
				}
			}
			if (c > 500) {
				l.add(nl);
				a++;
				c = 0;
			}
		}
		return l;
	}

	/**
	 * Set > Spawn distance from each player / team
	 */
	private int getDistanceFromPlayer(int sz, int i) {
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
	 * Get > Spawn location
	 */
	private Location getNewSpawnLocation(int sz) {
		World w = pl.getServer().getWorlds().get(0);
		Random r = new Random();
		return new Location(w, (-sz + r.nextInt(sz * 2)), 64, (-sz + r.nextInt(sz * 2)));
	}

	/**
	 * Get > Team list
	 */
	private ArrayList<Team> getTeamList() {
		ArrayList<Team> tl = new ArrayList<>();
		for (Team t : pl.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
			if (t.getSize() != 0) {
				tl.add(t);
			}
		}
		return tl;
	}

	/**
	 * Game > Countdown
	 */
	private void gameStartCountdown() {
		sysTimer1 = pl.getServer().getScheduler().runTaskTimer(pl, new Runnable() {
			@Override
			public void run() {
				if (sysCountdown == 0) {
					gs.setStat(Stat.INGAME);
					pl.getServer().getScheduler().cancelTask(sysTimer1.getTaskId());
					gameSetWorldInGame();
					if (c.mk_Delay() != 0 && c.mk_Message().length() != 0) {
						gameStartUHCTimer();
					}
				}
				for (UUID uid : igPls) {
					if (pl.getRegPlayer(uid) != null) {
						PlayerData p = pl.getRegPlayer(uid);
						if (sysCountdown <= 10) {
							if (sysCountdown == 10) {
								p.sendActionMessage("");
								p.sendTitleMessage("", "§c" + sysCountdown, 0, 21, 5);
								p.playLocalSound(Sound.NOTE_STICKS, 1.5f);
							} else {
								if (sysCountdown <= 5) {
									if (sysCountdown == 1) {
										p.sendTitleMessage("", "§6Starting in §c§l" + sysCountdown + "§r§6 secund", 0, 21, 5);
										p.playLocalSound(Sound.NOTE_PLING, 1f);
									} else if (sysCountdown == 0) {
										p.sendTitleMessage("", "§a§lBEGIN!", 0, 21, 10);
										p.playLocalSound(Sound.SUCCESSFUL_HIT, 1f);
										p.cp.setFoodLevel(20);
										p.cp.setHealth(20d);
										p.cp.setSaturation(20);
										p.cp.setNoDamageTicks(0);
										p.cp.setGameMode(GameMode.SURVIVAL);
									} else {
										p.sendTitleMessage("", "§6Starting in §c§l" + sysCountdown + "§r§6 secunds", 0, 21, 5);
										p.playLocalSound(Sound.NOTE_PLING, 1f);
									}
								} else {
									p.sendTitleMessage("", "§7" + sysCountdown, 0, 21, 5);
								}
							}
						} else {
							if (sysCountdown % 10 == 0) {
								p.sendActionMessage("§c" + sysCountdown);
								p.playLocalSound(Sound.NOTE_STICKS, 1.5f);
							} else {
								p.sendActionMessage("§7" + sysCountdown);
							}
						}
					}
				}
				sysCountdown--;
			}
		}, sysCoolDown, 20);
	}

	/**
	 * Start Marker Timer
	 */
	private void gameStartUHCTimer() {
		sysTimer1 = pl.getServer().getScheduler().runTaskTimer(pl, new Runnable() {
			@Override
			public void run() {
				sysMkDay++;
				sysMkMin += c.mk_Delay();
				broadcastMessage(c.mk_Message().replaceFirst("\\{1}", sysMkDay + "").replaceFirst("\\{2}", (sysMkDay + 1) + "").replaceFirst("\\{0}", sysMkMin + ""));
			}
		}, 20 * (c.mk_Delay() * 60), 20 * (c.mk_Delay() * 60));
	}

	/**
	 * Game > Setup World Settings
	 */
	private void gameSetWorldInGame() {
		sysGameTimer = System.currentTimeMillis();
		for (World w : pl.getServer().getWorlds()) {
			w.setPVP(true);
			w.setGameRuleValue("doDaylightCycle", c.gr_DaylightCycle() + "");
			w.setGameRuleValue("naturalRegeneration", "false");
			w.setGameRuleValue("keepInventory", "false");
			w.setDifficulty((c.ws_Difficulty() == 1 ? Difficulty.EASY : (c.ws_Difficulty() == 2 ? Difficulty.NORMAL : Difficulty.HARD)));
			w.getWorldBorder().setCenter(0, 0);
			if (c.wb_StartPos() <= c.ws_ArenaSize()) {
				w.getWorldBorder().setSize(c.ws_ArenaSize() * 2);
			} else {
				w.getWorldBorder().setSize(c.wb_StartPos() * 2);
			}
		}
		if (c.wb_StartDelay() != 0) {
			sysTimer2 = pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
				@Override
				public void run() {
					startWorldborderShrink();
					sysTimer2 = null;
				}
			}, c.wb_StartDelay() * 20);
		} else {
			startWorldborderShrink();
		}
	}

	/**
	 * Start world border shrink
	 */
	private void startWorldborderShrink() {
		if (c.wb_Time() != 0) {
			for (World w : pl.getServer().getWorlds()) {
				w.getWorldBorder().setSize(c.wb_EndPos() * 2, c.wb_Time());
			}
		}
	}

	/**
	 * Game > Status Check
	 */
	private void gameStatusCheck() {
		if (c.g_teamMode()) {
			if (igTmsLoc.size() > 1) {
				return;
			}
		} else if (igPls.size() > 1) {
			return;
		}
		gs.setStat(Stat.FINISHED);
		pl.getServer().getScheduler().cancelTask(sysTimer1.getTaskId());
		if (sysTimer2 != null) {
			pl.getServer().getScheduler().cancelTask(sysTimer2.getTaskId());
		}
		gameEndResults();
	}

	/**
	 * Game > End Result
	 */
	private void gameEndResults() {
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				String s = "§8§m--------------------------------------------§r";
				String win = "";
				s += "\n §lULTRA HARDCORE 1.8§r\n \n ";
				if (c.g_teamMode()) {
					Team t = pl.getServer().getScoreboardManager().getMainScoreboard().getTeam(igTmsLoc.keySet().iterator().next());
					win = "§6§lTeam " + t.getPrefix() + "§l" + t.getName().replace("_", " ");
					s += win;
					for (String tp : t.getEntries()) {
						if (pl.getRegPlayerByName(tp) != null) {
							PlayerData p = pl.getRegPlayerByName(tp);
							if (c.damageLog()) {
								if (p.hasDmgLog()) {
									getDamagerBookToInventory(p, true);
								}
							}
							if (c.server() & c.server_BungeeCord()) {
								p.cp.getInventory().setItem(c.server_BungeeCordInvSlot(), bcHub);
							}
						}
					}
				} else {
					OfflinePlayer of = pl.getServer().getOfflinePlayer(igPls.get(0));
					win = "§l" + of.getName();
					s += win;
					if (of.isOnline()) {
						PlayerData p = pl.getRegPlayer(of.getUniqueId());
						if (c.damageLog()) {
							if (p.hasDmgLog()) {
								getDamagerBookToInventory(p, true);
							}
						}
						if (c.server() & c.server_BungeeCord()) {
							p.cp.getInventory().setItem(c.server_BungeeCordInvSlot(), bcHub);
						}
					}
				}
				s += " §6§lhas won the game.§r\n";
				s += "\n§8§m--------------------------------------------§r";
				if (pl.getRegPlayerData().size() != 0) {
					for (PlayerData p : pl.getRegPlayerData().values()) {
						p.cp.sendMessage(s);
						p.sendTitleMessage(win, "§6won the game", 10, 60, 10);
						p.playLocalSound(Sound.FIREWORK_LAUNCH, 0f);
					}
				}
				for (World w : pl.getServer().getWorlds()) {
					w.getWorldBorder().setSize(w.getWorldBorder().getSize());
					w.setGameRuleValue("keepInventory", "true");
				}
				if (getAliveOfflinePlayers() != 0) {
					if (igOffs.size() != 0) {
						for (UUID u : igOffs.keySet()) {
							pl.getServer().getScheduler().cancelTask(igOffs.get(u)[0]);
						}
						igOffs.clear();
					}
					if (igOffns.size() != 0) {
						for (String n : igOffns.keySet()) {
							pl.getServer().getScheduler().cancelTask(igOffns.get(n));
						}
						igOffns.clear();
					}
				}
				if (c.server()) {
					us.serverRestart();
				}
			}
		}, 20);
	}
}