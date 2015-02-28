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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.PacketPlayOutEntityStatus;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

public class Gui {
	private static Main pl;
	private static boolean gmode = false;
	private static HashMap<UUID, Inventory> invHistory = new HashMap<UUID, Inventory>();

	/**
	 * ~ Load Gui ~
	 * 
	 * @param main = Main Plugin
	 */
	public static void onLoad(Main main) {
		pl = main;
		Inventory inv = pl.getServer().createInventory(null, (pl.tmMode ? 18 : 9), "UHC> Menu");
		inv.setItem(3, createItem(Material.PAPER, 0, "§6§lUltra Hardcore - Settings", Arrays.asList("", "§7Want to change some UHC", "§7settings without reloading", "§7the server?")));
		inv.setItem(5, createItem(Material.PAPER, 0, "§6§lGamerule", Arrays.asList("", "§7Want to change some", "§7gamerule?")));
		if (pl.tmMode) {
			inv.setItem(13, createItem(Material.PAPER, 0, "§6§lTeams Options", Arrays.asList("", "§7Want to change some", "§7team settings?")));
		}
		pl.invStore.put("gui_main", inv);
		inv = pl.getServer().createInventory(null, 45, "§7UHC>§r Menu> Settings");
		inv.setItem(1, createItem(Material.INK_SACK, 10, "§3§l[W]§6§l Sun time", Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woSunTime)));
		inv.setItem(2, createItem(Material.INK_SACK, 10, "§3§l[WB]§6§l Start position", Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbStartPos)));
		inv.setItem(3, createItem(Material.INK_SACK, 10, "§3§l[M]§6§l Time delay", Arrays.asList("§8Marker", "", "§7Value: §a" + pl.moMarkTime + "§7 min")));
		inv.setItem(4, createItem(Material.INK_SACK, 10, "§3§l[DIG]§6§l Max disconnected timeout", Arrays.asList("§8Offline Kicker", "", "§7Value: §a" + pl.okMaxTime + " §7min")));
		inv.setItem(5, createItem(Material.INK_SACK, (pl.fzp ? 10 : 8), "§3§l[FSP]§6§l Enabled", Arrays.asList("§8Freeze Player", "", "§7Status: " + (pl.fzp ? "§aOn" : "§cOff"))));
		inv.setItem(6, createItem(Material.INK_SACK, (pl.dmgLog ? 10 : 8), "§3§l[DL]§6§l Damage logger", Arrays.asList("§8Damage Logger", "", "§7Status: " + (pl.dmgLog ? "§aOn" : "§cOff"))));
		inv.setItem(10, createItem(Material.INK_SACK, 10, "§3§l[W]§6§l Difficulty", Arrays.asList("§8World Options", "", "§7Status: " + (pl.woDiff == 1 ? "§aEasy" : (pl.woDiff == 2 ? "§eNormal" : "§cHard")))));
		inv.setItem(11, createItem(Material.INK_SACK, 10, "§3§l[WB]§6§l Stop position", Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos)));
		inv.setItem(14, createItem(Material.INK_SACK, (pl.fzp ? 10 : 8), "§3§l[FSP]§6§l Radius size", Arrays.asList("§8Freeze Player", "", "§7Value: §a" + pl.fzSize)));
		inv.setItem(19, createItem(Material.INK_SACK, 10, "§3§l[W]§6§l Arena radius size", Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woArenaSize)));
		inv.setItem(20, createItem(Material.INK_SACK, 10, "§3§l[WB]§6§l Shrink time", Arrays.asList("§8WorldBorder", "", "§7Value: §a" + Function.getTimeFormat(pl.wbTime + "000"))));
		inv.setItem(40, createItem(Material.BARRIER, 0, "§6§lGo back?", null));
		pl.invStore.put("gui_settings", inv);
		inv = pl.getServer().createInventory(null, 36, "§7UHC>§r Menu> Gamerule");
		int i = 9;
		for (String grs : pl.grList) {
			String[] gr = grs.split("\\|");
			if (gr[1].toString().equalsIgnoreCase("true") || gr[1].toString().equalsIgnoreCase("false")) {
				if (Function.gameruleReplace(gr[0]).equalsIgnoreCase("Eternal day")) {
					inv.setItem(i, createItem(Material.INK_SACK, (gr[1].equals("true") ? 8 : 10), "§6§l" + Function.gameruleReplace(gr[0]), Arrays.asList("", "§7Status: " + (gr[1].equals("true") ? "§cOff" : "§aOn"))));
				} else {
					inv.setItem(i, createItem(Material.INK_SACK, (gr[1].equals("true") ? 10 : 8), "§6§l" + Function.gameruleReplace(gr[0]), Arrays.asList("", "§7Status: " + (gr[1].equals("true") ? "§aOn" : "§cOff"))));
				}
			} else {
				inv.setItem(i, createItem(Material.INK_SACK, (gr[1].equals("0") ? 8 : 10), "§6§l" + Function.gameruleReplace(gr[0]), Arrays.asList("", "§7Value: " + (gr[1].equals("0") ? "§c" : "§a") + gr[1])));
			}
			i++;
		}
		inv.setItem(31, createItem(Material.BARRIER, 0, "§6§lGo back?", null));
		pl.invStore.put("gui_gamerule", inv);
		if (pl.tmMode) {
			inv = pl.getServer().createInventory(null, 9, "§7UHC>§r Menu> Teams Options");
			inv.setItem(1, createItem(Material.INK_SACK, (pl.tmrFF ? 10 : 8), "§6§lFriendly fire", Arrays.asList("", "§7Status: " + (pl.tmrFF ? "§aOn" : "§cOff"))));
			inv.setItem(2, createItem(Material.INK_SACK, (pl.tmrSFI ? 10 : 8), "§6§lSee friendly invisibles", Arrays.asList("", "§7Status: " + (pl.tmrSFI ? "§aOn" : "§cOff"))));
			inv.setItem(4, createItem(Material.INK_SACK, 10, "§6§lMax team player", Arrays.asList("", "§7Value: §a" + pl.tmMaxPlayer)));
			inv.setItem(8, createItem(Material.BARRIER, 0, "§6§lGo back?", null));
			pl.invStore.put("gui_team", inv);
		}
	}

	/**
	 * ~ Give Player Gui Icon ~
	 * 
	 * @param p = Player
	 */
	public static void givePlayerGuiItem(Player p) {
		if (gmode && p.getInventory().contains(createItem(Material.WATCH, 0, "§eUHC - Menu", null))) {
			openInventory(p);
		} else {
			gmode = true;
			if (p.getInventory().getItem(4) == null) {
				p.getInventory().setItem(4, createItem(Material.WATCH, 0, "§eUHC - Menu", null));
			} else {
				p.getInventory().addItem(createItem(Material.WATCH, 0, "§eUHC - Menu", null));
			}
			openInventory(p);
		}
	}

	/**
	 * ~ Open Gui Inventory ~
	 * 
	 * @param p = Player <i>(Only for op player)</i>
	 */
	public static void openInventory(Player p) {
		if (p.isOp()) {
			if (invHistory.containsKey(p.getUniqueId())) {
				p.openInventory(invHistory.get(p.getUniqueId()));
			} else {
				p.openInventory(pl.invStore.get("gui_main"));
				invHistory.put(p.getUniqueId(), pl.invStore.get("gui_main"));
			}
		}
	}

	/**
	 * ~ Gui | Main Inventory ~
	 * 
	 * @param p = Player
	 * @param s = int <i>(Inventory slot raw id)</i>
	 */
	public static void clickGuiMain(Player p, int s) {
		if (s == 3) {
			p.openInventory(pl.invStore.get("gui_settings"));
			invHistory.put(p.getUniqueId(), pl.invStore.get("gui_settings"));
			p.playSound(p.getLocation(), Sound.CLICK, 5, 1.2f);
		} else if (s == 5) {
			p.openInventory(pl.invStore.get("gui_gamerule"));
			invHistory.put(p.getUniqueId(), pl.invStore.get("gui_gamerule"));
			p.playSound(p.getLocation(), Sound.CLICK, 5, 1.2f);
		} else if (s == 13) {
			p.openInventory(pl.invStore.get("gui_team"));
			invHistory.put(p.getUniqueId(), pl.invStore.get("gui_team"));
			p.playSound(p.getLocation(), Sound.CLICK, 5, 1.2f);
		}
	}

	/**
	 * ~ Gui | Settings Inventory ~
	 * 
	 * @param p = Player
	 * @param s = int <i>(Inventory slot raw id)</i>
	 * @param c = ClickType <i>(Click action)</i>
	 * @param is = ItemStack <i>(Get the item from clicked inventory)</i>
	 */
	public static void clickGuiSettings(Player p, int s, ClickType c, ItemStack is) {
		if (c == ClickType.LEFT) {
			if (s == 1) {
				if (pl.woSunTime >= Integer.MAX_VALUE) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.woSunTime++;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woSunTime));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 2) {
				if (pl.wbStartPos >= 20000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.wbStartPos++;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbStartPos));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 3) {
				if (pl.moMarkTime >= 20000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if (pl.moMarkTime == 0) {
						pl.moMarkTime++;
						pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("§8Marker", "", "§7Value: §a" + pl.moMarkTime + " §7min")));
					} else {
						pl.moMarkTime++;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8Marker", "", "§7Value: §a" + pl.moMarkTime + "§7 min"));
						is.setItemMeta(im);
					}
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_2");
				}
			} else if (s == 4) {
				if (pl.okMaxTime >= 20) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.okMaxTime++;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8Offline Kicker", "", "§7Value: §a" + pl.okMaxTime + "§7 min"));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_2");
				}
			} else if (s == 5) {
				if (pl.fzp) {
					pl.fzp = false;
					pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("§8Freeze Player", "", "§7Status: §cOff")));
					pl.invStore.get("gui_settings").setItem(14, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("§8Freeze Player", "", "§7Value: §a" + pl.fzSize)));
				} else {
					pl.fzp = true;
					pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("§8Freeze Player", "", "§7Status: §aOn")));
					pl.invStore.get("gui_settings").setItem(14, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("§8Freeze Player", "", "§7Value: §a" + pl.fzSize)));
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("settings_2");
			} else if (s == 6) {
				if (pl.dmgLog) {
					pl.dmgLog = false;
					pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("§8Damage Logger", "", "§7Status: §cOff")));
				} else {
					pl.dmgLog = true;
					pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("§8Damage Logger", "", "§7Status: §aOn")));
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("team");
			} else if (s == 10) {
				if (pl.woDiff > 2) {
					pl.woDiff = 1;
				} else {
					pl.woDiff++;
				}
				ItemMeta im = is.getItemMeta();
				im.setLore(Arrays.asList("§8World Options", "", "§7Status: " + (pl.woDiff == 1 ? "§aEasy" : (pl.woDiff == 2 ? "§eNormal" : "§cHard"))));
				is.setItemMeta(im);
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("settings_1");
			} else if (s == 11) {
				if (pl.wbEndPos >= 20000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.wbEndPos++;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 14) {
				if (pl.fzSize >= 15) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.fzSize++;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8Freeze Player", "", "§7Value: §a" + pl.fzSize));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_2");
				}
			} else if (s == 19) {
				if (pl.woArenaSize >= 20000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.woArenaSize++;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woArenaSize));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 20) {
				if (pl.wbTime >= 100000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if (pl.wbTime == 0) {
						pl.wbTime++;
						pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("§8WorldBorder", "", "§7Value: §a" + Function.getTimeFormat(pl.wbTime + "000"))));
						pl.invStore.get("gui_settings").setItem(11, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos)));
					} else {
						pl.wbTime++;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + Function.getTimeFormat(pl.wbTime + "000")));
						is.setItemMeta(im);
					}
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 40) {
				p.openInventory(pl.invStore.get("gui_main"));
				invHistory.put(p.getUniqueId(), pl.invStore.get("gui_main"));
				p.playSound(p.getLocation(), Sound.CLICK, 5, 1.2f);
			}
		} else if (c == ClickType.RIGHT) {
			if (s == 1) {
				if (pl.woSunTime <= 0) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.woSunTime--;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woSunTime));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 2) {
				if (pl.wbStartPos <= 100) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.wbStartPos--;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbStartPos));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 3) {
				if (pl.moMarkTime <= 0) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.moMarkTime--;
					if (pl.moMarkTime == 0) {
						pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("§8Marker", "", "§7Value: §c" + pl.moMarkTime + " §7min")));
					} else {
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8Marker", "", "§7Value: §a" + pl.moMarkTime + "§7 min"));
						is.setItemMeta(im);
					}
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_2");
				}
			} else if (s == 4) {
				if (pl.okMaxTime <= 1) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.okMaxTime--;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8Offline Kicker", "", "§7Value: §a" + pl.okMaxTime + "§7 min"));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_2");
				}
			} else if (s == 11) {
				if (pl.wbEndPos <= 10) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.wbEndPos--;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 14) {
				if (pl.fzSize <= 5) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.fzSize--;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8Freeze Player", "", "§7Value: §a" + pl.fzSize));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_2");
				}
			} else if (s == 19) {
				if (pl.woArenaSize <= 100) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.woArenaSize--;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woArenaSize));
					is.setItemMeta(im);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			} else if (s == 20) {
				if (pl.wbTime <= 0) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					pl.wbTime--;
					if (pl.wbTime == 0) {
						pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("§8WorldBorder", "", "§7Value: §c" + Function.getTimeFormat(pl.wbTime + "000"))));
						pl.invStore.get("gui_settings").setItem(11, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos)));
					} else {
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + Function.getTimeFormat(pl.wbTime + "000")));
						is.setItemMeta(im);
					}
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("settings_1");
				}
			}
		} else if (c == ClickType.SHIFT_LEFT) {
			if (s == 1) {
				if (pl.woSunTime >= Integer.MAX_VALUE) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.woSunTime + 100) > Integer.MAX_VALUE) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.woSunTime += 100;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woSunTime));
						is.setItemMeta(im);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}
			} else if (s == 2) {
				if (pl.wbStartPos >= 20000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.wbStartPos + 100) > 20000) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.wbStartPos += 100;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbStartPos));
						is.setItemMeta(im);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}

			} else if (s == 11) {
				if (pl.wbEndPos >= 20000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.wbEndPos + 100) > 20000) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.wbEndPos += 100;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos));
						is.setItemMeta(im);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}

			} else if (s == 19) {
				if (pl.woArenaSize >= 20000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.woArenaSize + 100) > 20000) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.woArenaSize += 100;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woArenaSize));
						is.setItemMeta(im);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}
			} else if (s == 20) {
				if (pl.wbTime >= 100000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.wbTime + 60) > 100000) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						if (pl.wbTime == 0) {
							pl.wbTime += 60;
							pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("§8WorldBorder", "", "§7Value: §a" + Function.getTimeFormat(pl.wbTime + "000"))));
							pl.invStore.get("gui_settings").setItem(11, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos)));
						} else {
							pl.wbTime += 60;
							ItemMeta im = is.getItemMeta();
							im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + Function.getTimeFormat(pl.wbTime + "000")));
							is.setItemMeta(im);
						}
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}
			}
		} else if (c == ClickType.SHIFT_RIGHT) {
			if (s == 1) {
				if (pl.woSunTime <= 0) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.woSunTime - 100) < 0) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.woSunTime -= 100;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woSunTime));
						is.setItemMeta(im);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}
			} else if (s == 2) {
				if (pl.wbStartPos <= 100) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.wbStartPos - 100) < 100) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.wbStartPos -= 100;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbStartPos));
						is.setItemMeta(im);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}
			} else if (s == 11) {
				if (pl.wbEndPos <= 10) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.wbEndPos - 100) < 10) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.wbEndPos -= 100;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos));
						is.setItemMeta(im);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}
			} else if (s == 19) {
				if (pl.woArenaSize <= 100) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.woArenaSize - 100) < 100) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.woArenaSize -= 100;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("§8World Options", "", "§7Value: §a" + pl.woArenaSize));
						is.setItemMeta(im);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}
			} else if (s == 20) {
				if (pl.wbTime <= 0) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if ((pl.wbTime - 60) < 0) {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					} else {
						pl.wbTime -= 60;
						if (pl.wbTime == 0) {
							pl.invStore.get("gui_settings").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("§8WorldBorder", "", "§7Value: §c" + Function.getTimeFormat(pl.wbTime + "000"))));
							pl.invStore.get("gui_settings").setItem(11, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("§8WorldBorder", "", "§7Value: §a" + pl.wbEndPos)));
						} else {
							ItemMeta im = is.getItemMeta();
							im.setLore(Arrays.asList("§8WorldBorder", "", "§7Value: §a" + Function.getTimeFormat(pl.wbTime + "000")));
							is.setItemMeta(im);
						}
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						updateWorldAndUhcBook("settings_1");
					}
				}
			}
		}
	}

	/**
	 * ~ Gui | Gamerule Inventory ~
	 * 
	 * @param p = Player
	 * @param s = int <i>(Inventory slot raw id)</i>
	 * @param c = ClickType <i>(Click action)</i>
	 * @param is = ItemStack <i>(Get the item from clicked inventory)</i>
	 */
	public static void clickGuiGamerule(Player p, int s, ClickType c, ItemStack is) {
		if (c == ClickType.LEFT) {
			if (s == 9) {
				if (pl.grList.contains("doDaylightCycle|false")) {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
					pl.grList.set(0, "doDaylightCycle|true");
				} else {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
					pl.grList.set(0, "doDaylightCycle|false");
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("gamerule");
			} else if (s == 10) {
				if (pl.grList.contains("doEntityDrops|true")) {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
					pl.grList.set(1, "doEntityDrops|false");
				} else {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
					pl.grList.set(1, "doEntityDrops|true");
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("gamerule");
			} else if (s == 11) {
				if (pl.grList.contains("doFireTick|true")) {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
					pl.grList.set(2, "doFireTick|false");
				} else {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
					pl.grList.set(2, "doFireTick|true");
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("gamerule");
			} else if (s == 12) {
				if (pl.grList.contains("doMobLoot|true")) {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
					pl.grList.set(3, "doMobLoot|false");
				} else {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
					pl.grList.set(3, "doMobLoot|true");
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("gamerule");
			} else if (s == 13) {
				if (pl.grList.contains("doMobSpawning|true")) {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
					pl.grList.set(4, "doMobSpawning|false");
				} else {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
					pl.grList.set(4, "doMobSpawning|true");
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("gamerule");
			} else if (s == 14) {
				if (pl.grList.contains("doTileDrops|true")) {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
					pl.grList.set(5, "doTileDrops|false");
				} else {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
					pl.grList.set(5, "doTileDrops|true");
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("gamerule");
			} else if (s == 15) {
				if (pl.grList.contains("mobGriefing|true")) {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
					pl.grList.set(6, "mobGriefing|false");
				} else {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
					pl.grList.set(6, "mobGriefing|true");
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("gamerule");
			} else if (s == 16) {
				int v = Integer.parseInt(pl.grList.get(7).split("\\|")[1]);
				if (v >= 10) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					if (v == 0) {
						v++;
						pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Value: §a" + v)));
					} else {
						v++;
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("", "§7Value: §a" + v));
						is.setItemMeta(im);
					}
					pl.grList.set(7, "randomTickSpeed|" + v);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("gamerule");
				}
			} else if (s == 17) {
				if (pl.grList.contains("reducedDebugInfo|true")) {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
					pl.grList.set(8, "reducedDebugInfo|false");
				} else {
					pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
					pl.grList.set(8, "reducedDebugInfo|true");
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("gamerule");
			} else if (s == 31) {
				p.openInventory(pl.invStore.get("gui_main"));
				invHistory.put(p.getUniqueId(), pl.invStore.get("gui_main"));
				p.playSound(p.getLocation(), Sound.CLICK, 5, 1.2f);
			}
		} else if (c == ClickType.RIGHT) {
			if (s == 16) {
				int v = Integer.parseInt(pl.grList.get(7).split("\\|")[1]);
				if (v <= 0) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					v--;
					pl.grList.set(7, "randomTickSpeed|" + v);
					if (v == 0) {
						pl.invStore.get("gui_gamerule").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Value: §c0")));
					} else {
						ItemMeta im = is.getItemMeta();
						im.setLore(Arrays.asList("", "§7Value: §a" + v));
						is.setItemMeta(im);
					}
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					updateWorldAndUhcBook("gamerule");
				}
			}
		}
	}

	/**
	 * ~ Gui | Teams Options Inventory ~
	 * 
	 * @param p = Player
	 * @param s = int <i>(Inventory slot raw id)</i>
	 * @param c = ClickType <i>(Click action)</i>
	 * @param is = ItemStack <i>(Get the item from clicked inventory)</i>
	 */
	public static void clickGuiTeam(Player p, int s, ClickType c, ItemStack is) {
		if (c == ClickType.LEFT) {
			if (s == 1) {
				if (pl.tmrFF) {
					pl.tmrFF = false;
					pl.invStore.get("gui_team").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
				} else {
					pl.tmrFF = true;
					pl.invStore.get("gui_team").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("team");
			} else if (s == 2) {
				if (pl.tmrSFI) {
					pl.tmrSFI = false;
					pl.invStore.get("gui_team").setItem(s, createItem(Material.INK_SACK, 8, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §cOff")));
				} else {
					pl.tmrSFI = true;
					pl.invStore.get("gui_team").setItem(s, createItem(Material.INK_SACK, 10, is.getItemMeta().getDisplayName(), Arrays.asList("", "§7Status: §aOn")));
				}
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
				updateWorldAndUhcBook("team");
			} else if (s == 4) {
				if (pl.tmMaxPlayer >= 20000) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					pl.tmMaxPlayer++;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("", "§7Value: §a" + pl.tmMaxPlayer));
					is.setItemMeta(im);
					Function.updateTeamInventory(pl);
					updateWorldAndUhcBook("team");
				}
			} else if (s == 8) {
				p.openInventory(pl.invStore.get("gui_main"));
				invHistory.put(p.getUniqueId(), pl.invStore.get("gui_main"));
				p.playSound(p.getLocation(), Sound.CLICK, 5, 1.2f);
			}
		} else if (c == ClickType.RIGHT) {
			if (s == 4) {
				if (pl.tmMaxPlayer <= 1) {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				} else {
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
					pl.tmMaxPlayer--;
					ItemMeta im = is.getItemMeta();
					im.setLore(Arrays.asList("", "§7Value: §a" + pl.tmMaxPlayer));
					is.setItemMeta(im);
					Function.updateTeamInventory(pl);
					updateWorldAndUhcBook("team");
				}
			}
		}
	}

	/**
	 * ~ Create ItemStack ~
	 * 
	 * @param m = Material <i>(Eks: APPLE or STONE)</i>
	 * @param b = int <i>(byte | 0 - 15)</i>
	 * @param d = String <i>(Item display name)</i>
	 * @param l = List<String> <i>(Item lord list)</i>
	 * @return ItemStack
	 */
	private static ItemStack createItem(Material m, int b, String d, List<String> l) {
		ItemStack is = new ItemStack(m, 1, (byte) b);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(d);
		if (l != null) {
			im.setLore(l);
		}
		is.setItemMeta(im);
		return is;
	}

	/**
	 * ~ Update World & Custom Ultra Hardcore Book ~
	 * 
	 * @param id = String <i>(ID)</i>
	 */
	private static void updateWorldAndUhcBook(String id) {
		if (pl.uhcBook && pl.uhcBookConf) {
			HashMap<ItemStack, Integer> b = pl.itemStore.get("Book");
			ItemStack is = b.keySet().iterator().next();
			BookMeta bm = (BookMeta) is.getItemMeta();
			StringBuilder page = new StringBuilder();
			int i = b.get(is);
			int pid = 0;
			if (id.equals("team")) {
				pid = 6;
				page.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				if (pl.tmMode) {
					page.append("§1Teams:§r\n");
					page.append("  §8Max player:§4 " + pl.tmMaxPlayer + "§r\n");
					page.append("  §8Friendly fire: " + (pl.tmrFF ? "§2On" : "§4Off") + "§r\n");
					page.append("  §8See friendly invi: " + (pl.tmrSFI ? "§2On" : "§4Off") + "§r\n \n");
				}
				page.append("§1Other:§r\n");
				page.append("  §8Damager Logger: " + (pl.dmgLog ? "§2On" : "§4Off") + "§r\n");
			} else if (id.equals("gamerule")) {
				pid = 5;
				page.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				page.append("§1Gamerule:§r\n");
				page.append("  §8Natural Regen: §4Off§r\n");
				if (pl.grList.size() != 0) {
					for (String grs : pl.grList) {
						String[] gr = grs.split("\\|");
						if (gr[1].toString().equalsIgnoreCase("true") || gr[1].toString().equalsIgnoreCase("false")) {
							if (Function.gameruleReplace(gr[0]).equalsIgnoreCase("Eternal day")) {
								page.append("  §8" + Function.gameruleReplace(gr[0]) + ": " + (gr[1].toString().equalsIgnoreCase("true") ? "§4Off" : "§2On") + "§r\n");
							} else {
								page.append("  §8" + Function.gameruleReplace(gr[0]) + ": " + (gr[1].toString().equalsIgnoreCase("true") ? "§2On" : "§4Off") + "§r\n");
							}
						} else {
							page.append("  §8" + Function.gameruleReplace(gr[0]) + ":§4 " + gr[1] + "§r\n");
						}
					}
				}
			} else if (id.equals("settings_2")) {
				pid = 4;
				page.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				page.append("§1Marker:§r\n");
				page.append("  §8Time delay:§4 " + (pl.moMarkTime != 0 ? pl.moMarkTime + " min" : "Off") + "§r\n");
				if (pl.moMarkTime != 0) {
					page.append("  §8Message: " + (pl.moMarkMeg.isEmpty() ? "§4Off" : "§2On") + "§r\n");
				}
				page.append(" \n§1Disconnected Players:§r\n");
				page.append("  §8Max timeout: §4" + pl.okMaxTime + " min§r\n");
				if (pl.fzp) {
					page.append(" \n§1Freeze Players:§r\n");
					page.append("  §8Radius size: §4" + pl.fzSize + "§r\n");
				}
			} else if (id.equals("settings_1")) {
				pid = 3;
				page.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				page.append("§1World:§r\n");
				page.append("  §8Difficulty: " + (pl.woDiff == 1 ? "§2Easy" : (pl.woDiff == 2 ? "§6Normal" : "§4Hard")) + "§r\n");
				page.append("  §8Arena size:§4 " + pl.woArenaSize + "§r\n");
				page.append("  §8Sun time:§4 " + pl.woSunTime + "§r\n \n");
				page.append("§1World Border:§r\n");
				page.append("  §8Start:§4 " + (pl.wbStartPos / 2) + "§r\n");
				if (pl.wbTime != 0) {
					page.append("  §8Stop:§4 " + (pl.wbEndPos / 2) + "§r\n");
					page.append("  §8Shrink:§4 " + Function.getTimeFormat(pl.wbTime + "000") + "§r\n");
				}
			}
			if (page != null) {
				bm.setPage(pid, page.toString());
				is.setItemMeta(bm);
				b.clear();
				b.put(is, i);
				pl.itemStore.put("Book", b);
				for (Player p : pl.getServer().getOnlinePlayers()) {
					p.getInventory().remove(Material.WRITTEN_BOOK);
					p.getInventory().setItem(i, is);
				}
			}
		}
		if (id.equals("team")) {
			if (pl.tmMode) {
				for (Team t : pl.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
					t.setAllowFriendlyFire(pl.tmrFF);
					t.setCanSeeFriendlyInvisibles(pl.tmrSFI);
				}
			}
		} else if (id.equals("gamerule")) {
			for (World w : pl.getServer().getWorlds()) {
				if (pl.grList != null) {
					if (pl.grList.size() != 0) {
						for (String gamerule : pl.grList) {
							String[] gr = gamerule.split("\\|");
							w.setGameRuleValue(gr[0], gr[1]);
						}
						w.setGameRuleValue("doDaylightCycle", "false");
						w.setGameRuleValue("naturalRegeneration", "true");
						w.setGameRuleValue("keepInventory", "true");
					}
				}
				w.setTime(pl.woSunTime);
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
			}
		} else if (id.equals("settings_1")) {
			pl.getServer().getWorlds().get(0).setTime(pl.woSunTime);
		}
	}
}