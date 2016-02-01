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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.thomaztwofast.uhc.custom.Perm;
import com.thomaztwofast.uhc.data.PlayerData;

public class Function {

	/**
	 * Minecraft Color Code
	 */
	public String MinecraftColor(String s, boolean b) {
		if (b) {
			s = s.replace("{N}", "\n");
		}
		return s.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
	}

	/**
	 * Convert second to clock time format.
	 * [hh:mm:ss]
	 */
	public String asClockFormat(long i) {
		String o = "";
		long h = 3600;
		long m = 60;
		long rh = 0;
		long rm = 0;
		if (i >= h) {
			rh = Math.round(i / h);
			i -= (rh * h);
		}
		if (i >= m) {
			rm = Math.round(i / m);
			i -= (rm * m);
		}
		if (rh != 0) {
			o += rh + "h ";
		}
		if (rh != 0 | rm != 0) {
			if (rm < 10) {
				o += "0" + rm + "m ";
			} else {
				o += rm + "m ";
			}
		}
		if (i < 10) {
			o += "0" + i;
		} else {
			o += i;
		}
		return o + "s";
	}

	/**
	 * Minecraft Clock Tick -> Real Time.
	 * [hh:mm:ss]
	 */
	public String tickFormat(double i) {
		String o = "";
		double h = 1000.0;
		double m = 16.667;
		double s = 0.278;
		double rh = 0.0;
		double rm = 0.0;
		double rs = 0.0;
		rh = Math.floor(i / h);
		i -= Math.floor(rh * h);
		if (i != 0) {
			rm = Math.floor(i / m);
			i -= (rm * m);
			if (i != 0) {
				rs = Math.floor(i / s);
			}
		}
		rh += 6;
		if (rh > 23) {
			rh -= 24;
		}
		if (rh < 10) {
			o = "0";
		}
		o += (int) rh + "h ";
		if (rm < 10) {
			o += "0";
		}
		o += (int) rm + "m ";
		if (rs < 10) {
			o += "0";
		}
		o += (int) rs + "s";
		return o;
	}

	/**
	 * Get all players on the server.
	 */
	public void getAllOnlinePlayers(Main m, boolean b) {
		if (m.getServer().getOnlinePlayers().size() != 0) {
			for (Player p : m.getServer().getOnlinePlayers()) {
				if (b) {
					m.regPlayer(p);
				} else {
					m.removeRegPlayer(p);
				}
			}
		}
	}

	/**
	 * Create ItemStack.
	 */
	public ItemStack createItem(Material m, int b, String t, String[] l) {
		ItemStack is = new ItemStack(m, 1, (short) b);
		ItemMeta im = is.getItemMeta();
		if (t != null && t.length() != 0) {
			im.setDisplayName(t);
		}
		if (l != null && l.length != 0) {
			im.setLore(createLord(l));
		}
		is.setItemMeta(im);
		return is;
	}

	/**
	 * Create Player Skull
	 */
	public ItemStack getPlayerSkull(PlayerData p) {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		sm.setOwner(p.cp.getName());
		is.setItemMeta(sm);
		return is;
	}

	/**
	 * Check if player has permission?
	 */
	public boolean hasPermission(CraftPlayer p, Perm pm) {
		if (p.isOp() | p.hasPermission(pm.toString())) {
			return true;
		}
		return false;
	}

	// :: PRIVATE :: //

	/**
	 * Create ItemStack Lore.
	 * Example: Integer|Integer|String
	 */
	private List<String> createLord(String[] l) {
		List<String> lore = new ArrayList<>();
		for (String str : l) {
			String[] s = str.split("\\|");
			if (s.length == 3) {
				switch (s[0]) {
				case "0":
					lore.add("");
					switch (s[1]) {
					case "0":
						lore.add("§7Value: " + s[2]);
						break;
					case "1":
						lore.add("§7Status: " + s[2]);
						break;
					}
					break;
				case "1":
					String o = "";
					for (String sl : s[2].split(" ")) {
						if (sl.equals("{N}")) {
							lore.add(o);
							o = "";
						} else {
							if (o.length() != 0) {
								o += " " + sl;
							} else {
								o = sl;
							}
						}
					}
					lore.add(o);
					break;
				}
			}
		}
		return lore;
	}
}