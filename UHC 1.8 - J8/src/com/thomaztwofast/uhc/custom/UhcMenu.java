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

package com.thomaztwofast.uhc.custom;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.thomaztwofast.uhc.Function;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.PlayerData;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;

public class UhcMenu {
	private Main pl;
	private Config c;
	private Function f;
	private HashMap<String, Inventory> storeIvs = new HashMap<>();
	private HashMap<UUID, Inventory> hisy = new HashMap<>();
	private ItemStack menu;

	public UhcMenu(Main main) {
		pl = main;
		c = main.getPlConf();
		f = main.getPlFun();
		menu = f.createItem(Material.WATCH, 0, "§cUltra Hardcore 1.8 - Menu", null);
		Inventory iv = pl.getServer().createInventory(null, (c.g_teamMode() ? 18 : 9), "§8UHC>§r Menu");
		iv.setItem(3, f.createItem(Material.PAPER, 0, "§6§lGame Settings", new String[] { "1|0|§7Want to change some UHC {N} §7settings without reloading {N} §7the server?" }));
		iv.setItem(5, f.createItem(Material.PAPER, 0, "§6§lGamerule", new String[] { "1|0|§7Want to change some {N} §7gamerule?" }));
		if (c.g_teamMode()) {
			iv.setItem(13, f.createItem(Material.PAPER, 0, "§6§lTeam Options", new String[] { "1|0|§7Want to change some {N} §7team settings?" }));
		}
		storeIvs.put("MAIN", iv);
		iv = pl.getServer().createInventory(null, 45, "§8UHC>§r Game Settings");
		updateInventory("SETTINGS", iv, new int[] { 1, 2, 3, 4, 5, 6, 7, 10, 11, 14, 16, 19, 20, 25, 29 });
		iv.setItem(40, f.createItem(Material.BARRIER, 0, "§6§lGo back?", null));
		storeIvs.put("SETTINGS", iv);
		iv = pl.getServer().createInventory(null, 36, "§8UHC>§r Gamerule");
		updateInventory("GAMERULE", iv, new int[] { 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 });
		iv.setItem(31, f.createItem(Material.BARRIER, 0, "§6§lGo back?", null));
		storeIvs.put("GAMERULE", iv);
		if (c.g_teamMode()) {
			iv = pl.getServer().createInventory(null, 9, "§8UHC>§r Team Options");
			updateInventory("TEAM", iv, new int[] { 1, 2, 3, 5 });
			iv.setItem(8, f.createItem(Material.BARRIER, 0, "§6§lGo back?", null));
			storeIvs.put("TEAM", iv);
		}
	}

	// :: PUBLIC :: //

	/**
	 * Get UHC item.
	 */
	public ItemStack getItem() {
		return menu;
	}

	/**
	 * Get stored inventors.
	 */
	public HashMap<String, Inventory> getInventorys() {
		return storeIvs;
	}

	/**
	 * UHC Menu > Open Inventory
	 */
	public Inventory openMenu(UUID u) {
		if (hisy.containsKey(u)) {
			return hisy.get(u);
		}
		hisy.put(u, storeIvs.get("MAIN"));
		return storeIvs.get("MAIN");
	}

	/**
	 * UHC Menu > Detect inventory
	 */
	public void clickEvent(PlayerData p, Inventory iv, ClickType ct, int i) {
		if (iv.equals(storeIvs.get("MAIN"))) {
			clickMainInventory(p, ct, i);
		} else if (iv.equals(storeIvs.get("SETTINGS"))) {
			clickSettingsInventory(p, iv, ct, i);
		} else if (iv.equals(storeIvs.get("GAMERULE"))) {
			clickGameruleInventory(p, iv, ct, i);
		} else if (iv.equals(storeIvs.get("TEAM"))) {
			clickTeamInventory(p, iv, ct, i);
		}
	}

	// :: PRIVATE :: //

	/**
	 * UHC Menu > Click Listener > Main
	 */
	private void clickMainInventory(PlayerData p, ClickType ct, int i) {
		if (ct.equals(ClickType.LEFT)) {
			if (i == 3) {
				p.cp.openInventory(storeIvs.get("SETTINGS"));
				p.setInventoryLock(true, "MENU");
				hisy.put(p.cp.getUniqueId(), storeIvs.get("SETTINGS"));
				p.playLocalSound(Sound.CLICK, 1.2f);
			} else if (i == 5) {
				p.cp.openInventory(storeIvs.get("GAMERULE"));
				p.setInventoryLock(true, "MENU");
				hisy.put(p.cp.getUniqueId(), storeIvs.get("GAMERULE"));
				p.playLocalSound(Sound.CLICK, 1.2f);
			} else if (i == 13) {
				p.cp.openInventory(storeIvs.get("TEAM"));
				p.setInventoryLock(true, "MENU");
				hisy.put(p.cp.getUniqueId(), storeIvs.get("TEAM"));
				p.playLocalSound(Sound.CLICK, 1.2f);
			}
		}
	}

	/**
	 * UHC Menu > Click Listener > Settings
	 */
	private void clickSettingsInventory(PlayerData p, Inventory iv, ClickType ct, int i) {
		if (ct.equals(ClickType.LEFT)) {
			if (i == 1) {
				if (c.ws_SunTime() == 23999) {
					c.setWsSunTime(0);
				} else {
					c.setWsSunTime(c.ws_SunTime() + 1);
				}
				updateInventory("SETTINGS", iv, new int[] { 1 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 2) {
				if (c.wb_StartDelay() == 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbStartDelay(c.wb_StartDelay() + 1);
				updateInventory("SETTINGS", iv, new int[] { 2 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 3) {
				if (c.mk_Delay() == 20) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setMkDelay(c.mk_Delay() + 1);
				updateInventory("SETTINGS", iv, new int[] { 3 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_2");
			} else if (i == 4) {
				if (c.of_Timeout() == 20) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setOfTimeout(c.of_Timeout() + 1);
				updateInventory("SETTINGS", iv, new int[] { 4 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_2");
			} else if (i == 5) {
				c.toggleSettingsFreezePlayer();
				updateInventory("SETTINGS", iv, new int[] { 5, 14 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_2");
			} else if (i == 6) {
				c.toggleSettingsDamageLog();
				updateInventory("SETTINGS", iv, new int[] { 6 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_5");
			} else if (i == 7) {
				c.toggleSettingsGoldenHead();
				updateInventory("SETTINGS", iv, new int[] { 7, 16, 25 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_3");
				updateInfo("GOLDHEAD");
			} else if (i == 10) {
				c.toggleSettingsWorldDifficulty();
				updateInventory("SETTINGS", iv, new int[] { 10 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 11) {
				if (c.wb_StartPos() == 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbStartPos(c.wb_StartPos() + 1);
				updateInventory("SETTINGS", iv, new int[] { 11 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 14) {
				if (c.fp_Size() == 15) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.toggleSettingsFreezePlayerSize(true);
				updateInventory("SETTINGS", iv, new int[] { 14 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_2");
			} else if (i == 16) {
				c.toggleSettingsGoldenHeadDefault();
				updateInventory("SETTINGS", iv, new int[] { 16 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_3");
				updateInfo("GOLDHEAD");
			} else if (i == 19) {
				if (c.ws_ArenaSize() == 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWsArenaSize(c.ws_ArenaSize() + 1);
				updateInventory("SETTINGS", iv, new int[] { 19 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 20) {
				if (c.wb_EndPos() == 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbEndPos(c.wb_EndPos() + 1);
				updateInventory("SETTINGS", iv, new int[] { 20 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 25) {
				c.toggleSettingsGoldenHeadGold();
				updateInventory("SETTINGS", iv, new int[] { 25 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_3");
				updateInfo("GOLDHEAD");
			} else if (i == 29) {
				if (c.wb_Time() == 100000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbTimeDelay(c.wb_Time() + 1);
				updateInventory("SETTINGS", iv, new int[] { 2, 20, 29 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 40) {
				p.cp.openInventory(storeIvs.get("MAIN"));
				hisy.put(p.cp.getUniqueId(), storeIvs.get("MAIN"));
				p.setInventoryLock(true, "MENU");
				p.playLocalSound(Sound.CLICK, 1.2f);
			}
		} else if (ct.equals(ClickType.RIGHT)) {
			if (i == 1) {
				if (c.ws_SunTime() == 0) {
					c.setWsSunTime(23999);
				} else {
					c.setWsSunTime(c.ws_SunTime() - 1);
				}
				updateInventory("SETTINGS", iv, new int[] { 1 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 2) {
				if (c.wb_StartDelay() == 0) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbStartDelay(c.wb_StartDelay() - 1);
				updateInventory("SETTINGS", iv, new int[] { 2 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 3) {
				if (c.mk_Delay() == 0) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setMkDelay(c.mk_Delay() - 1);
				updateInventory("SETTINGS", iv, new int[] { 3 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_2");
			} else if (i == 4) {
				if (c.of_Timeout() == 1) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setOfTimeout(c.of_Timeout() - 1);
				updateInventory("SETTINGS", iv, new int[] { 4 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_2");
			} else if (i == 11) {
				if (c.wb_StartPos() == 50) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbStartPos(c.wb_StartPos() - 1);
				updateInventory("SETTINGS", iv, new int[] { 11 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 14) {
				if (c.fp_Size() == 5) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.toggleSettingsFreezePlayerSize(false);
				updateInventory("SETTINGS", iv, new int[] { 14 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_2");
			} else if (i == 19) {
				if (c.ws_ArenaSize() == 100) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWsArenaSize(c.ws_ArenaSize() - 1);
				updateInventory("SETTINGS", iv, new int[] { 19 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 20) {
				if (c.wb_EndPos() == 5) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbEndPos(c.wb_EndPos() - 1);
				updateInventory("SETTINGS", iv, new int[] { 20 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 29) {
				if (c.wb_Time() == 0) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbTimeDelay(c.wb_Time() - 1);
				updateInventory("SETTINGS", iv, new int[] { 2, 20, 29 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			}
		} else if (ct.equals(ClickType.SHIFT_LEFT)) {
			if (i == 1) {
				int v = (c.ws_SunTime() + 100);
				if (v > 23999) {
					c.setWsSunTime(v - 24000);
				} else {
					c.setWsSunTime(v);
				}
				updateInventory("SETTINGS", iv, new int[] { 1 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 2) {
				int v = (c.wb_StartDelay() + 60);
				if (v > 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbStartDelay(v);
				updateInventory("SETTINGS", iv, new int[] { 2 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 11) {
				int v = (c.wb_StartPos() + 100);
				if (v == 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbStartPos(v);
				updateInventory("SETTINGS", iv, new int[] { 11 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 19) {
				int v = (c.ws_ArenaSize() + 100);
				if (v > 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWsArenaSize(v);
				updateInventory("SETTINGS", iv, new int[] { 19 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 20) {
				int v = (c.wb_EndPos() + 100);
				if (v > 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbEndPos(v);
				updateInventory("SETTINGS", iv, new int[] { 20 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 29) {
				int v = (c.wb_Time() + 60);
				if (v > 100000) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbTimeDelay(v);
				updateInventory("SETTINGS", iv, new int[] { 2, 20, 29 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			}
		} else if (ct.equals(ClickType.SHIFT_RIGHT)) {
			if (i == 1) {
				int v = (c.ws_SunTime() - 100);
				if (v < 0) {
					c.setWsSunTime(24000 + v);
				} else {
					c.setWsSunTime(v);
				}
				updateInventory("SETTINGS", iv, new int[] { 1 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 2) {
				int v = (c.wb_StartDelay() - 60);
				if (v < 0) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbStartDelay(v);
				updateInventory("SETTINGS", iv, new int[] { 2 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 11) {
				int v = (c.wb_StartPos() - 100);
				if (v < 50) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbStartPos(v);
				updateInventory("SETTINGS", iv, new int[] { 11 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 19) {
				int v = (c.ws_ArenaSize() - 100);
				if (v < 100) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWsArenaSize(v);
				updateInventory("SETTINGS", iv, new int[] { 19 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 20) {
				int v = (c.wb_EndPos() - 100);
				if (v < 5) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbEndPos(v);
				updateInventory("SETTINGS", iv, new int[] { 20 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			} else if (i == 29) {
				int v = (c.wb_Time() - 60);
				if (v < 0) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.setWbTimeDelay(v);
				updateInventory("SETTINGS", iv, new int[] { 2, 20, 29 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("PG_1");
			}
		}
	}

	/**
	 * UHC Menu > Click Listener > Gamerule
	 */
	private void clickGameruleInventory(PlayerData p, Inventory iv, ClickType ct, int i) {
		if (ct.equals(ClickType.LEFT)) {
			if (i == 9) {
				c.toggleGameruleDaylight();
				updateInventory("GAMERULE", iv, new int[] { 9 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 10) {
				c.toggleGameruleEntityDrops();
				updateInventory("GAMERULE", iv, new int[] { 10 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 11) {
				c.toggleGameruleFireTick();
				updateInventory("GAMERULE", iv, new int[] { 11 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 12) {
				c.toggleGameruleMobLoot();
				updateInventory("GAMERULE", iv, new int[] { 12 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 13) {
				c.toggleGameruleMobSpawn();
				updateInventory("GAMERULE", iv, new int[] { 13 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 14) {
				c.toggleGameruleTilsDrops();
				updateInventory("GAMERULE", iv, new int[] { 14 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 15) {
				c.toggleGameruleMobGriefing();
				updateInventory("GAMERULE", iv, new int[] { 15 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 16) {
				if (c.gr_RandomTick() == 10) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.toggleGameruleTickSpeed(true);
				updateInventory("GAMERULE", iv, new int[] { 16 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 17) {
				c.toggleGameruleDebugInfo();
				updateInventory("GAMERULE", iv, new int[] { 17 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 18) {
				c.toggleGameruleSpectatorsGenerateChunks();
				updateInventory("GAMERULE", iv, new int[] { 18 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			} else if (i == 31) {
				p.cp.openInventory(storeIvs.get("MAIN"));
				hisy.put(p.cp.getUniqueId(), storeIvs.get("MAIN"));
				p.setInventoryLock(true, "MENU");
				p.playLocalSound(Sound.CLICK, 1.2f);
			}
		} else if (ct.equals(ClickType.RIGHT)) {
			if (i == 16) {
				if (c.gr_RandomTick() == 0) {
					p.playLocalSound(Sound.NOTE_BASS, 0.5f);
					return;
				}
				c.toggleGameruleTickSpeed(false);
				updateInventory("GAMERULE", iv, new int[] { 16 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("GAMERULE");
			}
		}
	}

	/**
	 * UHC Menu > Click Listener > Team
	 */
	private void clickTeamInventory(PlayerData p, Inventory iv, ClickType ct, int i) {
		if (ct.equals(ClickType.LEFT)) {
			if (i == 1) {
				c.toggleFriendlyFire();
				updateInventory("TEAM", iv, new int[] { 1 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("TEAM");
			} else if (i == 2) {
				c.toggleSeeFriendlyInvs();
				updateInventory("TEAM", iv, new int[] { 2 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("TEAM");
			} else if (i == 3) {
				c.toggleNameTagVis();
				updateInventory("TEAM", iv, new int[] { 3 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("TEAM");
			} else if (i == 5) {
				if (c.g_maxTeamPlayer() >= 20000) {
					p.playLocalSound(Sound.NOTE_BASS, 0f);
					return;
				}
				c.toggleMaxTeamPlayer(true);
				updateInventory("TEAM", iv, new int[] { 5 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("TEAM");
			} else if (i == 8) {
				p.cp.openInventory(storeIvs.get("MAIN"));
				hisy.put(p.cp.getUniqueId(), storeIvs.get("MAIN"));
				p.setInventoryLock(true, "MENU");
				p.playLocalSound(Sound.CLICK, 1.2f);
			}
		} else if (ct.equals(ClickType.RIGHT)) {
			if (i == 5) {
				if (c.g_maxTeamPlayer() <= 1) {
					p.playLocalSound(Sound.NOTE_BASS, 0f);
					return;
				}
				c.toggleMaxTeamPlayer(false);
				updateInventory("TEAM", iv, new int[] { 5 });
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				updateInfo("TEAM");
			}
		}
	}

	/**
	 * Update Item on specific inventory by id.
	 */
	private void updateInventory(String id, Inventory iv, int[] il) {
		switch (id) {
		case "SETTINGS":
			for (int i : il) {
				switch (i) {
				case 1:
					iv.setItem(i, f.createItem(Material.INK_SACK, 10, "§3§l[W]§6§l Sun Time", new String[] { "1|0|§8World Settings", "0|0|§a" + f.tickFormat(c.ws_SunTime()) }));
					break;
				case 2:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.wb_StartDelay() != 0 && c.wb_Time() != 0 ? 10 : 8), "§3§l[WB]§6§l Start Delay", new String[] { "1|0|§8World Border", "0|0|§a" + f.asClockFormat(c.wb_StartDelay()) }));
					break;
				case 3:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.mk_Delay() != 0 & c.mk_Message().length() != 0 ? 10 : 8), "§3§l[M]§6§l Time Delay", new String[] { "1|0|§8Marker", "0|0|§a" + f.asClockFormat((c.mk_Delay() * 60)) }));
					break;
				case 4:
					iv.setItem(i, f.createItem(Material.INK_SACK, 10, "§3§l[DIG]§6§l Max Disconnected Timeout", new String[] { "1|0|§8Offline Kicker", "0|0|§a" + f.asClockFormat((c.of_Timeout() * 60)) }));
					break;
				case 5:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.fp_Enabled() ? 10 : 8), "§3§l[FSP]§6§l Enabled", new String[] { "1|0|§8Freeze Player", "0|1|" + (c.fp_Enabled() ? "§aOn" : "§cOff") }));
					break;
				case 6:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.damageLog() ? 10 : 8), "§3§l[DL]§6§l Damage Logger", new String[] { "1|0|§8Damage Logger", "0|1|" + (c.damageLog() ? "§aOn" : "§cOff") }));
					break;
				case 7:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gh_Enalbed() ? 10 : 8), "§3§l[GH]§6§l Enabled", new String[] { "1|0|§8Golden Head", "0|1|" + (c.gh_Enalbed() ? "§aOn" : "§cOff") }));
					break;
				case 10:
					iv.setItem(i, f.createItem(Material.INK_SACK, 10, "§3§l[W]§6§l Difficulty", new String[] { "1|0|§8World Settings", "0|1|" + (c.ws_Difficulty() == 1 ? "§aEasy" : (c.ws_Difficulty() == 2 ? "§eNormal" : "§cHard")) }));
					break;
				case 11:
					iv.setItem(i, f.createItem(Material.INK_SACK, 10, "§3§l[WB]§6§l Start Position", new String[] { "1|0|§8World Border", "0|0|§a" + c.wb_StartPos() }));
					break;
				case 14:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.fp_Enabled() ? 10 : 8), "§3§l[FSP]§6§l Radius Size", new String[] { "1|0|§8Freeze Player", "0|0|§a" + c.fp_Size() }));
					break;
				case 16:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gh_Enalbed() ? (c.gh_DefaultApple() ? 10 : 8) : 8), "§3§l[GH]§6§l Default Head Apple", new String[] { "1|0|§8Golden Head", "0|1|" + (c.gh_DefaultApple() ? "§aOn" : "§cOff") }));
					break;
				case 19:
					iv.setItem(i, f.createItem(Material.INK_SACK, 10, "§3§l[W]§6§l Arena Size", new String[] { "1|0|§8World Settings", "0|0|§a" + c.ws_ArenaSize() }));
					break;
				case 20:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.wb_Time() != 0 ? 10 : 8), "§3§l[WB]§6§l Stop Position", new String[] { "1|0|§8World Border", "0|0|§a" + c.wb_EndPos() }));
					break;
				case 25:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gh_Enalbed() ? (c.gh_GoldenApple() ? 10 : 8) : 8), "§3§l[GH]§6§l Golden Head Apple", new String[] { "1|0|§8Golden Head", "0|1|" + (c.gh_GoldenApple() ? "§aOn" : "§cOff") }));
					break;
				case 29:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.wb_Time() != 0 ? 10 : 8), "§3§l[WB]§6§l Shrink Time", new String[] { "1|0|§8World Border", "0|0|§a" + f.asClockFormat(c.wb_Time()) }));
					break;
				}
			}
			break;
		case "GAMERULE":
			for (int i : il) {
				switch (i) {
				case 9:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_DaylightCycle() ? 8 : 10), "§6§lEternal Day", new String[] { "0|1|" + (c.gr_DaylightCycle() ? "§cOff" : "§aOn") }));
					break;
				case 10:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_EntityDrops() ? 10 : 8), "§6§lEntity Drops", new String[] { "0|1|" + (c.gr_EntityDrops() ? "§aOn" : "§cOff") }));
					break;
				case 11:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_FireTick() ? 10 : 8), "§6§lFire Tick", new String[] { "0|1|" + (c.gr_FireTick() ? "§aOn" : "§cOff") }));
					break;
				case 12:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_MobLoot() ? 10 : 8), "§6§lMob Loot", new String[] { "0|1|" + (c.gr_MobLoot() ? "§aOn" : "§cOff") }));
					break;
				case 13:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_MobSpawning() ? 10 : 8), "§6§lMob Spawning", new String[] { "0|1|" + (c.gr_MobSpawning() ? "§aOn" : "§cOff") }));
					break;
				case 14:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_TileDrops() ? 10 : 8), "§6§lTile Drops", new String[] { "0|1|" + (c.gr_TileDrops() ? "§aOn" : "§cOff") }));
					break;
				case 15:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_MobGriefing() ? 10 : 8), "§6§lMob Griefing", new String[] { "0|1|" + (c.gr_MobGriefing() ? "§aOn" : "§cOff") }));
					break;
				case 16:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_RandomTick() != 0 ? 10 : 8), "§6§lTick Speed", new String[] { c.gr_RandomTick() != 0 ? "0|0|§a" + c.gr_RandomTick() : "0|1|§cOff" }));
					break;
				case 17:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_ReducedDebugInfo() ? 10 : 8), "§6§lShort Debug Info", new String[] { "0|1|" + (c.gr_ReducedDebugInfo() ? "§aOn" : "§cOff") }));
					break;
				case 18:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.gr_SpectatorsGenerateChunks() ? 10 : 8), "§6§lSpectators Generate Chunks", new String[] { "1|0|§7Only for Minecraft 1.9", "0|1|" + (c.gr_SpectatorsGenerateChunks() ? "§aOn" : "§cOff") }));
					break;
				}
			}
			break;
		case "TEAM":
			for (int i : il) {
				switch (i) {
				case 1:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.go_Friendly() ? 10 : 8), "§6§lFriendly Fire", new String[] { "0|1|" + (c.go_Friendly() ? "§aOn" : "§cOff") }));
					break;
				case 2:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.go_SeeFriendly() ? 10 : 8), "§6§lSee Friendly Invisibles", new String[] { "0|1|" + (c.go_SeeFriendly() ? "§aOn" : "§cOff") }));
					break;
				case 3:
					iv.setItem(i, f.createItem(Material.INK_SACK, (c.go_NameTagVisibility() != 3 ? 10 : 8), "§6§lName Tag Visibility", new String[] { "0|1|" + (c.go_NameTagVisibility() == 0 ? "§aOn" : (c.go_NameTagVisibility() == 1 ? "§eHide Other Teams" : (c.go_NameTagVisibility() == 2 ? "§eHide Own Team" : "§cOff"))) }));
					break;
				case 5:
					iv.setItem(i, f.createItem(Material.INK_SACK, 10, "§6§lMax Team Player", new String[] { "0|0|§a" + c.g_maxTeamPlayer() }));
					break;
				}
			}
			break;
		}
	}

	/**
	 * Update Ultra Hardcore book and World.
	 */
	private void updateInfo(String k) {
		if (c.book_Enabled() & c.book_ShowSettings()) {
			switch (k) {
			case "PG_1":
				pl.getGame().updateBookSettings(1);
				break;
			case "PG_2":
				pl.getGame().updateBookSettings(2);
				break;
			case "PG_3":
				pl.getGame().updateBookSettings(3);
				break;
			case "GAMERULE":
				pl.getGame().updateBookSettings(4);
				break;
			case "PG_5":
			case "TEAM":
				pl.getGame().updateBookSettings(5);
				break;
			}
			for (PlayerData p : pl.getRegPlayerData().values()) {
				p.cp.getInventory().remove(Material.WRITTEN_BOOK);
				p.cp.getInventory().setItem(c.book_InventorySlot(), pl.getGame().getBook());
			}
		}

		switch (k) {
		case "PG_1":
			pl.getServer().getWorlds().get(0).setTime(c.ws_SunTime());
			break;
		case "GAMERULE":
			for (World w : pl.getServer().getWorlds()) {
				if (!w.getName().equalsIgnoreCase("uhc_lobby")) {
					w.setGameRuleValue("doEntityDrops", "" + c.gr_EntityDrops());
					w.setGameRuleValue("doFireTick", "" + c.gr_FireTick());
					w.setGameRuleValue("doMobLoot", "" + c.gr_MobLoot());
					w.setGameRuleValue("doMobSpawning", "" + c.gr_MobSpawning());
					w.setGameRuleValue("doTileDrops", "" + c.gr_TileDrops());
					w.setGameRuleValue("mobGriefing", "" + c.gr_MobGriefing());
					w.setGameRuleValue("randomTickSpeed", "" + c.gr_RandomTick());
					w.setGameRuleValue("reducedDebugInfo", "" + c.gr_ReducedDebugInfo());
					w.setGameRuleValue("spectatorsGenerateChunks", "" + c.gr_SpectatorsGenerateChunks());
				}
			}
			if (c.gr_ReducedDebugInfo()) {
				for (PlayerData p : pl.getRegPlayerData().values()) {
					p.cp.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(p.cp.getHandle().playerInteractManager.player, (byte) 22));
				}
			} else {
				for (PlayerData p : pl.getRegPlayerData().values()) {
					p.cp.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityStatus(p.cp.getHandle().playerInteractManager.player, (byte) 23));
				}
			}
			break;
		case "GOLDHEAD":
			pl.getGame().updateGoldenHeadApple();
			break;
		case "TEAM":
			pl.getGame().getTeam().updateSettings();
			break;
		}
	}
}