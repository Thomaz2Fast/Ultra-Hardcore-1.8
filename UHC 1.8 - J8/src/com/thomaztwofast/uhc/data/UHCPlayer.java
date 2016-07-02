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
import org.bukkit.Achievement;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.thomaztwofast.uhc.Main;

public class UHCPlayer {
	private Main uA;
	public Player uB;
	public boolean uC = false;
	public String[] uD = { "", "" };
	private long uE = 0;
	private Class<?> uFa; // > CraftPlayer
	private Class<?> uFb; // > IChatBaseComponent
	private Class<?> uFc; // > Packet
	private Class<?> uFd; // > PacketPlayOutChat
	private Class<?> uFe; // > PacketPlayOutEntityStatus
	private Class<?> uFf; // > PacketPlayOutPlayerListHeaderFooter
	private Class<?> uFg; // > PacketPlayOutTitle
	private Class<?> uFh; // > PacketPlayOutWorldBorder
	private Class<?> uFi; // > WorldBorder

	public UHCPlayer(Main a, Player b) {
		uA = a;
		uB = b;
		lC();
	}

	public void hubItems() {
		if (uA.mC.cKa) {
			uB.getInventory().setItem(uA.mC.cKb, uA.mE.gE.uB);
		}
		if (uA.mC.cGa && uA.mE.gD.uCc) {
			uB.getInventory().setItem(uA.mC.cGc, uA.mE.gD.uD);
		}
		if (uA.mC.cFa && uA.mC.cFv) {
			uB.getInventory().setItem(uA.mC.cFy, uA.mE.gC.uG);
		}
	}

	public void playLocalSound(Sound a, float b) {
		uB.playSound(uB.getLocation(), a, 10, b);
	}

	public void resetPlayer(boolean a) {
		if (uA.mC.cHa) {
			setTabListHeaderAndFooter(uA.mC.cHb, uA.mC.cHc);
		}
		if (uB.getActivePotionEffects().size() != 0) {
			for (PotionEffect b : uB.getActivePotionEffects()) {
				uB.removePotionEffect(b.getType());
			}
		}
		for (ItemStack b : uB.getInventory().getContents()) {
			uB.getInventory().remove(b);
		}
		updateEntityStatus(uA.mC.cEi);
		uB.getEquipment().setBoots(null);
		uB.getEquipment().setChestplate(null);
		uB.getEquipment().setHelmet(null);
		uB.getEquipment().setLeggings(null);
		uB.getEquipment().setItemInOffHand(null);
		uB.setBedSpawnLocation(null);
		uB.setExp(0f);
		uB.setFallDistance(0f);
		uB.setFireTicks(0);
		uB.setFoodLevel(20);
		uB.setLevel(0);
		uB.setNoDamageTicks(200);
		uB.setSaturation(20f);
		uB.setHealth(uB.getMaxHealth());
		uB.setTotalExperience(0);
		uB.closeInventory();
		uB.setScoreboard(uA.getServer().getScoreboardManager().getMainScoreboard());
		if (a) {
			uB.setGameMode(GameMode.ADVENTURE);
			uB.removeAchievement(Achievement.OPEN_INVENTORY);
		} else {
			uB.setGameMode(GameMode.SPECTATOR);
		}
	}

	public void sendActionMessage(String a) {
		try {
			sP(uFd.getConstructor(new Class[] { uFb, Byte.TYPE }).newInstance(jT(a, true), (byte) 2));
		} catch (Exception b) {
			uA.log(1, "Action message can not be send!");
		}
	}

	public void sendCommandMessage(String a, String b) {
		uB.sendMessage("\u00A78\u00A7o" + a + "> \u00A77\u00A7o" + b);
	}

	public void sendJsonMessage(String a) {
		try {
			sP(uFd.getConstructor(new Class[] { uFb, Byte.TYPE }).newInstance(jT(a, false), (byte) 0));
		} catch (Exception b) {
			uA.log(1, "Json message can not be send!");
		}
	}

	public void sendJsonMessageLater(String a, Sound b, float c, int d) {
		uA.getServer().getScheduler().runTaskLater(uA, new Runnable() {
			public void run() {
				if (uB.isOnline()) {
					sendJsonMessage(a);
					if (b != null) {
						playLocalSound(b, c);
					}
				}
			}
		}, d);
	}

	public void sendTitleMessage(String a, String b, int c, int d, int e) {
		try {
			sP(uFg.getConstructor(new Class[] { uFg.getClasses()[0], uFb, Integer.TYPE, Integer.TYPE, Integer.TYPE }).newInstance(uFg.getClasses()[0].getEnumConstants()[2], null, c, d, e));
			sP(uFg.getConstructor(new Class[] { uFg.getClasses()[0], uFb }).newInstance(uFg.getClasses()[0].getEnumConstants()[1], jT(b, true)));
			sP(uFg.getConstructor(new Class[] { uFg.getClasses()[0], uFb }).newInstance(uFg.getClasses()[0].getEnumConstants()[0], jT(a, true)));
		} catch (Exception f) {
			uA.log(1, "Title message can not be send!");
		}
	}

	public void setCustomWorldborder(Location a, int b) {
		try {
			Object c = uFi.getConstructor(new Class[0]).newInstance();
			c.getClass().getMethod("setCenter", new Class[] { Double.TYPE, Double.TYPE }).invoke(c, a.getX(), a.getZ());
			c.getClass().getMethod("setSize", new Class[] { Double.TYPE }).invoke(c, b);
			sP(uFh.getConstructor(new Class[] { uFi, uFh.getClasses()[0] }).newInstance(c, uFh.getClasses()[0].getEnumConstants()[3]));
		} catch (Exception d) {
			uA.log(1, "Worldborder can not be send!");
		}
	}

	public void setInvKey(String a, String b) {
		uC = true;
		uD[0] = a;
		uD[1] = b;
	}

	public void setTabListHeaderAndFooter(String a, String b) {
		try {
			Object c = uFf.getConstructor(new Class[0]).newInstance();
			Field d = c.getClass().getDeclaredField("a");
			Field e = c.getClass().getDeclaredField("b");
			d.setAccessible(true);
			e.setAccessible(true);
			d.set(c, jT((a.length() != 0 ? "{\"text\": \"" + a + "\"}" : "{\"translate\": \"\"}"), false));
			e.set(c, jT((b.length() != 0 ? "{\"text\": \"" + b + "\"}" : "{\"translate\": \"\"}"), false));
			sP(c);
		} catch (Exception c) {
			uA.log(1, "Update player TabList can not be send!");
		}
	}

	public void tpFallbackServer() {
		if ((System.currentTimeMillis() - uE) > 2500) {
			try {
				ByteArrayOutputStream a = new ByteArrayOutputStream();
				DataOutputStream b = new DataOutputStream(a);
				b.writeUTF("Connect");
				b.writeUTF(uA.mC.cFw);
				uB.sendPluginMessage(uA, "BungeeCord", a.toByteArray());
				sendCommandMessage("Server", "Connection to \u00A7e\u00A7o" + uA.mC.cFw + "\u00A77\u00A7o...");
			} catch (IOException e) {
				sendCommandMessage("Server", "Connection error.");
			}
			uE = System.currentTimeMillis();
		}
	}

	public void dmgStorage(long a, double b, String[] c) {
		try {
			int d = 0;
			File e = new java.io.File(uA.getDataFolder(), "data/" + uB.getUniqueId() + ".yml");
			FileConfiguration f = YamlConfiguration.loadConfiguration(e);
			if (f.isInt("i")) {
				d = f.getInt("i");
			}
			f.options().header("AUTO-GENERATED FILE. DO NOT MODIFY");
			f.set("d." + d + ".t", (System.currentTimeMillis() - a));
			f.set("d." + d + ".f", c[0]);
			f.set("d." + d + ".i", (c.length == 2 ? c[1] : ""));
			f.set("d." + d + ".d", b);
			d++;
			f.set("i", d);
			f.save(e);
		} catch (IOException g) {
			uA.log(1, "Error, could not save dmg log file for player '" + uB.getName() + "'");
			uA.log(1, "Error message: " + g.getMessage());
		}
	}

	public void updateEntityStatus(boolean a) {
		try {
			sP(uFe.getConstructors()[1].newInstance(uFa.getMethod("getHandle", new Class[0]).invoke(uFa.cast(uB)), (byte) (a ? 22 : 23)));
		} catch (Exception b) {
			uA.log(1, "Update player status can not be send!");
		}
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private void lC() {
		String[] a = uA.getServer().getBukkitVersion().split("-");
		String[] b = a[0].split("\\.");
		String c = b[0] + "_" + b[1] + "_R";
		for (int d = 1; d < 5; d++) {
			try {
				if (Class.forName("net.minecraft.server.v" + c + d + ".Packet") != null) {
					c += d;
					break;
				}
			} catch (ClassNotFoundException e) {
			}
		}
		try {
			uFa = Class.forName("org.bukkit.craftbukkit.v" + c + ".entity.CraftPlayer");
			uFb = Class.forName("net.minecraft.server.v" + c + ".IChatBaseComponent");
			uFc = Class.forName("net.minecraft.server.v" + c + ".Packet");
			uFd = Class.forName("net.minecraft.server.v" + c + ".PacketPlayOutChat");
			uFe = Class.forName("net.minecraft.server.v" + c + ".PacketPlayOutEntityStatus");
			uFf = Class.forName("net.minecraft.server.v" + c + ".PacketPlayOutPlayerListHeaderFooter");
			uFg = Class.forName("net.minecraft.server.v" + c + ".PacketPlayOutTitle");
			uFh = Class.forName("net.minecraft.server.v" + c + ".PacketPlayOutWorldBorder");
			uFi = Class.forName("net.minecraft.server.v" + c + ".WorldBorder");
		} catch (ClassNotFoundException d) {
			uA.log(1, "Error, you are using wrong server version.");
			uA.mA = GameStatus.ERROR;
		}
	}

	private Object jT(String a, boolean b) {
		try {
			return uFb.getClasses()[0].getMethod("a", new Class[] { String.class }).invoke(uFb, (b ? "{\"text\": \"" + a + "\"}" : a));
		} catch (Exception c) {
			uA.log(1, "Error, can not set json format.");
			return null;
		}
	}

	private void sP(Object a) {
		try {
			Object b = uFa.getDeclaredMethod("getHandle", new Class[0]).invoke(uFa.cast(uB), new Object[0]);
			Field c = b.getClass().getDeclaredField("playerConnection");
			c.get(b).getClass().getDeclaredMethod("sendPacket", new Class[] { uFc }).invoke(c.get(b), a);
		} catch (Exception b) {
			uA.log(1, "Packet error, can not be send!");
		}
	}
}
