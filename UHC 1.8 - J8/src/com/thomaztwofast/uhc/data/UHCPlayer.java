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

package com.thomaztwofast.uhc.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.thomaztwofast.uhc.Main;

public class UHCPlayer {
	private Main pl;
	public Player player;
	public boolean isInvLock = false;
	public int[] invHistory = { -1, -1 };
	private Class<?> craftPlayer;
	private Class<?> nmsPacket;
	private Class<?> nmsOutChat;
	private Class<?> nmsOutStatus;
	private Class<?> nmsOutBorder;
	private Class<?> nmsIChat;
	private Class<?> nmsBorder;
	private Class<?> nmsWorld;
	private Class<?> nsmChatType;

	public UHCPlayer(Main pl, Player p) {
		this.pl = pl;
		this.player = p;
		try {
			craftPlayer = Class.forName("org.bukkit.craftbukkit." + pl.NMS_VER + ".entity.CraftPlayer");
			nmsPacket = Class.forName("net.minecraft.server." + pl.NMS_VER + ".Packet");
			nmsOutChat = Class.forName("net.minecraft.server." + pl.NMS_VER + ".PacketPlayOutChat");
			nmsOutStatus = Class.forName("net.minecraft.server." + pl.NMS_VER + ".PacketPlayOutEntityStatus");
			nmsOutBorder = Class.forName("net.minecraft.server." + pl.NMS_VER + ".PacketPlayOutWorldBorder");
			nmsIChat = Class.forName("net.minecraft.server." + pl.NMS_VER + ".IChatBaseComponent");
			nmsBorder = Class.forName("net.minecraft.server." + pl.NMS_VER + ".WorldBorder");
			nmsWorld = Class.forName("net.minecraft.server." + pl.NMS_VER + ".WorldServer");
			nsmChatType = Class.forName("net.minecraft.server." + pl.NMS_VER + ".ChatMessageType");
		} catch (ClassNotFoundException err) {
			log("Error, Some of the important player packed could not load...");
			log("Are you using the correct version of Bukkit/Spigot?");
			pl.status = Status.ERROR;
		}
		if (pl.config.pluginEnable) {
			if (pl.config.tabEnable)
				p.setPlayerListHeaderFooter(pl.config.tabHeader, pl.config.tabFooter);
			p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(pl.config.gameOldCombat ? 1000d : 4d);
		}
	}

	public void dmgStorage(long time, double dmg, String[] args) {
		try {
			File file = new File(pl.getDataFolder(), "data/" + player.getUniqueId() + ".yml");
			FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
			int i = yaml.isInt("i") ? yaml.getInt("i") : 0;
			yaml.options().header("AUTO-GENERATED FILE. DO NOT MODIFY");
			yaml.set("d." + i + ".t", (System.currentTimeMillis() - time));
			yaml.set("d." + i + ".f", args[0]);
			yaml.set("d." + i + ".i", (args.length == 2 ? args[1] : ""));
			yaml.set("d." + i + ".d", dmg);
			yaml.set("i", i + 1);
			yaml.save(file);
		} catch (IOException err) {
			pl.log(1, "Error, Could not save damager logger file for player '" + player.getName() + "'");
			pl.log(1, "Error message: " + err.getMessage());
		}
	}

	public void fallbackServer(Material material) {
		if (player.getCooldown(material) == 0) {
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				DataOutputStream data = new DataOutputStream(stream);
				data.writeUTF("Connect");
				data.writeUTF(pl.config.serverHub);
				player.sendPluginMessage(pl, "BungeeCord", stream.toByteArray());
				sendCmdMessage("Server", "Connection to \u00A7E\u00A7O" + pl.config.serverHub + "\u00A77\u00A7O...");
			} catch (IOException err) {
				sendCmdMessage("Server", "Connection error. Try again later...");
			}
			player.setCooldown(material, 100);
		}
	}

	public boolean hasNode(String node) {
		return player.isOp() || player.hasPermission(node);
	}

	public void getHubItems() {
		if (pl.config.bookEnable)
			player.getInventory().setItem(pl.config.bookInventorySlot, pl.gameManager.book.itemStack);
		if (pl.config.gameInTeam && pl.gameManager.teams.isSelectItem)
			player.getInventory().setItem(pl.config.gameSelectTeamInventory, pl.gameManager.teams.selectItem);
		if (pl.config.serverEnable && pl.config.serverIsBungeeCord)
			player.getInventory().setItem(pl.config.serverInventorySlot, pl.gameManager.server.itemStack);
	}

	public void resetPlayer(boolean isAlive) {
		player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
		for (ItemStack item : player.getInventory().getContents())
			player.getInventory().remove(item);
		pl.getServer().advancementIterator().forEachRemaining(a -> {
			if (player.getAdvancementProgress(a).isDone())
				player.getAdvancementProgress(a).getAwardedCriteria().forEach(b -> player.getAdvancementProgress(a).revokeCriteria(b));
		});
		updateStatus(pl.config.grDebugInfo);
		player.getEquipment().setBoots(null);
		player.getEquipment().setChestplate(null);
		player.getEquipment().setHelmet(null);
		player.getEquipment().setLeggings(null);
		player.getEquipment().setItemInOffHand(null);
		player.setBedSpawnLocation(null);
		player.setExp(0f);
		player.setFallDistance(0f);
		player.setFireTicks(0);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setNoDamageTicks(200);
		player.setSaturation(5f);
		player.setHealth(player.getHealthScale());
		player.setTotalExperience(0);
		player.closeInventory();
		player.setScoreboard(pl.getServer().getScoreboardManager().getMainScoreboard());
		player.setGameMode(isAlive ? GameMode.ADVENTURE : GameMode.SPECTATOR);
	}

	public void playSound(Sound sound, float pitch) {
		player.playSound(player.getLocation(), sound, SoundCategory.MASTER, 10, pitch);
	}

	public void sendActionMessage(String input) {
		sendJson(input, true, 2);
	}

	public void sendCmdMessage(String label, String input) {
		player.sendMessage("\u00A78\u00A7L\u00A7o" + label + ">\u00A7R\u00A77\u00A7O " + input);
	}

	public void sendJsonMessage(String input) {
		sendJson(input, false, 0);
	}

	public void sendJsonMessageLater(String input, Sound sound, float pitch, int delay) {
		pl.getServer().getScheduler().runTaskLater(pl, new Runnable() {
			public void run() {
				if (player.isOnline()) {
					sendJsonMessage(input);
					if (sound != null)
						playSound(sound, pitch);
				}
			}
		}, delay);
	}

	public void setCustomWorldborder(Location location, int size) {
		try {
			Object objBorder = nmsBorder.getConstructor(new Class[0]).newInstance();
			objBorder.getClass().getDeclaredMethod("setCenter", new Class[] { Double.TYPE, Double.TYPE }).invoke(objBorder, location.getX(), location.getZ());
			objBorder.getClass().getDeclaredMethod("setSize", new Class[] { Double.TYPE }).invoke(objBorder, size);
			Object objCraftPlayer = craftPlayer.getDeclaredMethod("getHandle", new Class[0]).invoke(craftPlayer.cast(player), new Object[0]);
			objBorder.getClass().getField("world").set(objBorder, nmsWorld.cast(objCraftPlayer.getClass().getField("world").get(objCraftPlayer)));
			sendClientPacked(nmsOutBorder.getConstructor(new Class[] { nmsBorder, nmsOutBorder.getClasses()[0] }).newInstance(objBorder, nmsOutBorder.getClasses()[0].getEnumConstants()[3]));
		} catch (Exception err) {
			log("Error, Custom worldborder could not be set!");
		}
	}

	public void setInvKey(int id, int value) {
		isInvLock = true;
		invHistory = new int[] { id, value };
	}

	public void updateStatus(boolean status) {
		try {
			sendClientPacked(nmsOutStatus.getConstructors()[1].newInstance(craftPlayer.getMethod("getHandle", new Class[0]).invoke(craftPlayer.cast(player)), (byte) (status ? 22 : 23)));
		} catch (Exception err) {
			log("Error, Player status can not be updated!");
		}
	}

	// ---------------------------------------------------------------------------

	private Object asJsonFormat(String input, boolean isText) {
		try {
			return nmsIChat.getClasses()[0].getMethod("a", new Class[] { String.class }).invoke(nmsIChat, (isText ? "{\"text\": \"" + input + "\"}" : input));
		} catch (Exception err) {
			log("Error, Can not set JSON format.");
			return null;
		}
	}

	private void log(String log) {
		pl.log(1, "[PLAYER] " + log);
	}

	private void sendJson(String input, boolean isText, int value) {
		try {
			sendClientPacked(nmsOutChat.getConstructor(new Class[] { nmsIChat, nsmChatType }).newInstance(asJsonFormat(input, isText), nsmChatType.getEnumConstants()[value]));
		} catch (Exception err) {
			log("Error, Could not send message packed to the player.");
		}
	}

	private void sendClientPacked(Object packed) {
		try {
			Object objCraftPlayer = craftPlayer.getDeclaredMethod("getHandle", new Class[0]).invoke(craftPlayer.cast(player), new Object[0]);
			Field field = objCraftPlayer.getClass().getDeclaredField("playerConnection");
			field.get(objCraftPlayer).getClass().getDeclaredMethod("sendPacket", new Class[] { nmsPacket }).invoke(field.get(objCraftPlayer), packed);
		} catch (Exception err) {
			log("Error, Custom packed could not be sendt!");
		}
	}
}
