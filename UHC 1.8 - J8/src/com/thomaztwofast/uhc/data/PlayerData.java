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

package com.thomaztwofast.uhc.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.bukkit.Achievement;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.Main;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldBorder;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;

public class PlayerData {
	public CraftPlayer cp;
	private Main pl;
	private PlayerConnection pc;
	private File dmgLog = null;
	private FileConfiguration dmgLogFc = null;
	private long delay = 0;
	private int dmgLogNext = 0;
	private boolean ivKey = false;
	private String ivLoc = "";

	public PlayerData(Main main, Player p) {
		cp = (CraftPlayer) p;
		pc = ((CraftPlayer) p).getHandle().playerConnection;
		pl = main;
		cp.setScoreboard(main.getServer().getScoreboardManager().getMainScoreboard());
		if (!pl.getPlConf().pl_Enabled() && pl.getPlConf().tablist()) {
			updateTabListHeaderFooter(pl.getPlConf().tablist_Header(), pl.getPlConf().tablist_Footer());
		}
	}

	/**
	 * Is > Inventory lock?
	 */
	public boolean isInventoryLock() {
		return ivKey;
	}

	/**
	 * Check > Player has been damage?
	 */
	public boolean hasDmgLog() {
		if (dmgLog == null) {
			loadDmgLogFile();
		}
		if (dmgLogNext != 0) {
			return true;
		}
		return false;
	}

	/**
	 * Set > Inventory Lock
	 */
	public void setInventoryLock(boolean b, String i) {
		ivKey = b;
		ivLoc = i;
	}

	/**
	 * Get > Player Inventory Location
	 */
	public String getInventoryLocation() {
		return ivLoc;
	}

	/**
	 * Play > Local Sound
	 */
	public void playLocalSound(Sound s, float pit) {
		cp.playSound(cp.getLocation(), s, 5f, pit);
	}

	/**
	 * Clear > Player
	 */
	public void clearPlayer(boolean b) {
		if (pl.getPlConf().tablist()) {
			updateTabListHeaderFooter(pl.getPlConf().tablist_Header(), pl.getPlConf().tablist_Footer());
		}
		if (cp.getActivePotionEffects().size() != 0) {
			for (PotionEffect pf : cp.getActivePotionEffects()) {
				cp.removePotionEffect(pf.getType());
			}
		}
		for (ItemStack is : cp.getInventory().getContents()) {
			cp.getInventory().remove(is);
		}
		cp.getEquipment().setHelmet(null);
		cp.getEquipment().setChestplate(null);
		cp.getEquipment().setLeggings(null);
		cp.getEquipment().setBoots(null);
		cp.setBedSpawnLocation(null);
		cp.setFallDistance(0);
		cp.setNoDamageTicks(200);
		cp.setHealth(20d);
		cp.setFoodLevel(20);
		cp.setSaturation(20f);
		cp.setLevel(0);
		cp.setTotalExperience(0);
		cp.setExp(0f);
		if (b) {
			cp.setGameMode(GameMode.ADVENTURE);
			cp.removeAchievement(Achievement.OPEN_INVENTORY);
		} else {
			cp.setGameMode(GameMode.SPECTATOR);
		}
		cp.closeInventory();
		cp.setScoreboard(pl.getServer().getScoreboardManager().getMainScoreboard());
	}

	/**
	 * Send > Message
	 */
	public void sendMessage(String s1, String s2) {
		cp.sendMessage("§9" + s1 + ">§7 " + s2);
	}

	/**
	 * Send > Raw Message (JSON Format)
	 */
	public void sendRawICMessage(String s) {
		pc.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(s), (byte) 0));
	}

	/**
	 * Send > Action Message
	 */
	public void sendActionMessage(String s) {
		pc.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s + "\"}"), (byte) 2));
	}

	/**
	 * Send > Title Message
	 */
	public void sendTitleMessage(String t, String s, int fi, int d, int fo) {
		IChatBaseComponent tm = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + t + "\"}");
		IChatBaseComponent sm = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + s + "\"}");
		pc.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fi, d, fo));
		pc.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, sm));
		pc.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, tm));
	}

	/**
	 * Set > Custom World border
	 */
	public void setCustomWorldBorder(Location from, int size) {
		WorldBorder wb = new WorldBorder();
		wb.setCenter(from.getBlockX(), from.getBlockZ());
		wb.setSize(size);
		pc.sendPacket(new PacketPlayOutWorldBorder(wb, EnumWorldBorderAction.INITIALIZE));
	}

	/**
	 * Set > Tablist Header & Footer
	 */
	public void updateTabListHeaderFooter(String h, String f) {
		try {
			PacketPlayOutPlayerListHeaderFooter p = new PacketPlayOutPlayerListHeaderFooter();
			Field fh = p.getClass().getDeclaredField("a");
			Field ff = p.getClass().getDeclaredField("b");
			fh.setAccessible(true);
			ff.setAccessible(true);
			fh.set(p, IChatBaseComponent.ChatSerializer.a((h.length() != 0 ? "{'text': '" + h + "'}" : "{translate: ''}")));
			ff.set(p, IChatBaseComponent.ChatSerializer.a((f.length() != 0 ? "{'text': '" + f + "'}" : "{translate: ''}")));
			pc.sendPacket(p);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			pl.getPlLog().warn(e.getMessage());
		}
	}

	/**
	 * Save > Player Profile
	 */
	public void saveProfile(boolean b) {
		if (b) {
			if (pl.getPlConf().tablist()) {
				updateTabListHeaderFooter("", "");
				cp.setScoreboard(pl.getServer().getScoreboardManager().getMainScoreboard());
			}
		}
	}

	/**
	 * Storage > DamagerLogger data
	 */
	public void storagedamaged(long l, double d, String[] dmg) {
		try {
			if (dmgLog == null) {
				loadDmgLogFile();
			}
			dmgLogFc.set("dl." + dmgLogNext + ".t", (System.currentTimeMillis() - l));
			dmgLogFc.set("dl." + dmgLogNext + ".f", dmg[0]);
			dmgLogFc.set("dl." + dmgLogNext + ".u", dmg[1]);
			dmgLogFc.set("dl." + dmgLogNext + ".d", d);
			dmgLogNext++;
			dmgLogFc.set("nextValue", dmgLogNext);
			dmgLogFc.save(dmgLog);
		} catch (IOException e) {
			pl.getPlLog().warn("[DAMAGELOGGER] Something went wrong... " + e.getMessage());
		}
	}

	/**
	 * Get > DamagerLogger book.
	 */
	public ItemStack getDamagerLoggerBook() {
		if (dmgLog == null) {
			loadDmgLogFile();
		}
		if (dmgLogNext != 0) {
			ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bm = (BookMeta) is.getItemMeta();
			bm.setAuthor(pl.getDescription().getName() + " - Damager Logger");
			bm.setDisplayName("§cDamager Logger §7> §e" + cp.getName());
			bm.setTitle(pl.getDescription().getName());
			bm.setLore(Arrays.asList("", "§7List of all damage you", "§7have been gotten from this UHC."));
			bm.addPage("§l Damage Logger §r\n \nThis book will show you, Who has damaged you from this UHC.");
			String pg = "";
			int a = 0;
			int b = 1;
			for (String id : dmgLogFc.getConfigurationSection("dl").getKeys(false)) {
				String path = "dl." + id + ".";
				if (a == 3) {
					bm.addPage(pg);
					pg = "";
					a = 0;
				}
				pg += "§1" + b + ". §9" + pl.getPlFun().asClockFormat(dmgLogFc.getLong(path + "t") / 1000) + "§r\n";
				pg += "  §8" + dmgLogFc.getString(path + "f") + "§r\n";
				if (dmgLogFc.isString(path + "u")) {
					pg += "  §8" + dmgLogFc.getString(path + "u") + "§r\n";
				}
				pg += "  §c" + dmgLogFc.getDouble(path + "d") + "dmg§r\n \n";
				a++;
				b++;
			}
			if (pg.length() != 0) {
				bm.addPage(pg);
			}
			is.setItemMeta(bm);
			dmgLog.delete();
			dmgLog = null;
			dmgLogFc = null;
			dmgLogNext = 0;
			return is;
		}
		return null;
	}

	/**
	 * Get > Player team.
	 */
	public Team getPlayerTeam() {
		return cp.getScoreboard().getEntryTeam(cp.getName());
	}

	public void TpFallBackServer() {
		if ((System.currentTimeMillis() - delay) > 2500) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("Connect");
				out.writeUTF(pl.getPlConf().server_BungeeCordHub());
				cp.sendPluginMessage(pl, "BungeeCord", b.toByteArray());
				sendMessage("Server", "Connection to §e" + pl.getPlConf().server_BungeeCordHub() + "§7...");
			} catch (IOException e) {
				sendMessage("Server", "Connection error.");
			}
			delay = System.currentTimeMillis();
		}
	}

	// :: PRIVATE :: //

	/**
	 * Load > Damage logger file
	 */
	private void loadDmgLogFile() {
		dmgLog = new File(pl.getDataFolder(), "data/" + cp.getUniqueId() + ".yml");
		dmgLogFc = YamlConfiguration.loadConfiguration(dmgLog);
		if (dmgLogFc.isInt("nextValue")) {
			dmgLogNext = dmgLogFc.getInt("nextValue");
		} else {
			dmgLogFc.set("nextValue", dmgLogNext);
		}
	}
}