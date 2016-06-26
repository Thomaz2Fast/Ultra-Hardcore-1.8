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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class UhcMenu extends Function {
	private Main uA;
	private HashMap<String, Inventory> uB = new HashMap<>();
	private HashMap<UUID, String> uC = new HashMap<>();
	public ItemStack uD;

	public UhcMenu(Main a) {
		uA = a;
	}

	public void load() {
		uD = nItem(Material.WATCH, 0, "\u00A7cUltra Hardcore 1.8 - Menu", "");
		cIv("MAIN", "Menu", (uA.mC.cGa ? 18 : 9), -1);
		cIv("SETTINGS", "Game Settings", 45, 40);
		cIv("GAMERULE", "Gamerule", 36, 31);
		if (uA.mC.cGa) {
			cIv("TEAM", "Team Options", 9, 8);
		}
	}

	public void openMenu(UHCPlayer a) {
		if (uC.containsKey(a.uB.getUniqueId())) {
			a.uB.openInventory(uB.get(uC.get(a.uB.getUniqueId())));
			a.setInvKey("MENU", uC.get(a.uB.getUniqueId()));
			return;
		}
		uC.put(a.uB.getUniqueId(), "MAIN");
		a.uB.openInventory(uB.get("MAIN"));
		a.setInvKey("MENU", "MAIN");
	}

	public void clickEvent(UHCPlayer a, ClickType b, int c) {
		switch (a.uD[1].hashCode()) {
		case 2358713:
			cMIv(a, b, c);
			return;
		case -2077709277:
			cSIv(a, b, c);
			return;
		case -985626130:
			cGIv(a, b, c);
			return;
		case 2570845:
			cTIv(a, b, c);
			return;
		}
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private void cMIv(UHCPlayer a, ClickType b, int c) {
		if (b.equals(ClickType.LEFT)) {
			switch (c) {
			case 3:
				a.uB.openInventory(uB.get("SETTINGS"));
				a.setInvKey("MENU", "SETTINGS");
				uC.put(a.uB.getUniqueId(), "SETTINGS");
				a.playLocalSound(Sound.UI_BUTTON_CLICK, 1.2f);
				return;
			case 5:
				a.uB.openInventory(uB.get("GAMERULE"));
				a.setInvKey("MENU", "GAMERULE");
				uC.put(a.uB.getUniqueId(), "GAMERULE");
				a.playLocalSound(Sound.UI_BUTTON_CLICK, 1.2f);
				return;
			case 13:
				a.uB.openInventory(uB.get("TEAM"));
				a.setInvKey("MENU", "TEAM");
				uC.put(a.uB.getUniqueId(), "TEAM");
				a.playLocalSound(Sound.UI_BUTTON_CLICK, 1.2f);
				return;
			}
		}
	}

	private void cSIv(UHCPlayer a, ClickType b, int c) {
		if (b.equals(ClickType.LEFT)) {
			switch (c) {
			case 1:
				if (uA.mC.cIc == 23999) {
					uA.mC.cIc = 0;
				} else {
					uA.mC.cIc++;
				}
				upI("SETTINGS", 1);
				upS("WORLD");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 2:
				if (uA.mC.cJa == 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJa++;
				upI("SETTINGS", 2);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 3:
				if (uA.mC.cNb == 20) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cNb++;
				upI("SETTINGS", 3);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 4:
				if (uA.mC.cPa == 20) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cPa++;
				upI("SETTINGS", 4);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 5:
				uA.mC.cMa = !uA.mC.cMa;
				upI("SETTINGS", 5, 14);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 6:
				uA.mC.cOa = !uA.mC.cOa;
				upI("SETTINGS", 6);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 7:
				uA.mC.cLa = !uA.mC.cLa;
				upI("SETTINGS", 7, 16, 25);
				upS("GOLDHEAD");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 10:
				if (uA.mC.cIa == 3) {
					uA.mC.cIa = 1;
				} else {
					uA.mC.cIa++;
				}
				upI("SETTINGS", 10);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 11:
				if (uA.mC.cJb == 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJb++;
				upI("SETTINGS", 11);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 14:
				if (uA.mC.cMb == 15) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cMb++;
				upI("SETTINGS", 14);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 16:
				uA.mC.cLb = !uA.mC.cLb;
				upI("SETTINGS", 16);
				upS("GOLDHEAD");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 19:
				if (uA.mC.cIb == 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cIb++;
				upI("SETTINGS", 19);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 20:
				if (uA.mC.cJc == 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJc++;
				upI("SETTINGS", 20);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 25:
				uA.mC.cLc = !uA.mC.cLc;
				upI("SETTINGS", 25);
				upS("GOLDHEAD");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 29:
				if (uA.mC.cJd == 100000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJd++;
				upI("SETTINGS", 2, 20, 29);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 40:
				a.uB.openInventory(uB.get("MAIN"));
				a.setInvKey("MENU", "MAIN");
				uC.put(a.uB.getUniqueId(), "MAIN");
				a.playLocalSound(Sound.UI_BUTTON_CLICK, 1.2f);
				return;
			}
		} else if (b.equals(ClickType.RIGHT)) {
			switch (c) {
			case 1:
				if (uA.mC.cIc == 0) {
					uA.mC.cIc = 23999;
				} else {
					uA.mC.cIc--;
				}
				upI("SETTINGS", 1);
				upS("WORLD");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 2:
				if (uA.mC.cJa == 0) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJa--;
				upI("SETTINGS", 2);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 3:
				if (uA.mC.cNb == 0) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cNb--;
				upI("SETTINGS", 3);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 4:
				if (uA.mC.cPa == 1) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cPa--;
				upI("SETTINGS", 4);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 11:
				if (uA.mC.cJb == 50) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJb--;
				upI("SETTINGS", 11);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 14:
				if (uA.mC.cMb == 5) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cMb--;
				upI("SETTINGS", 14);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 19:
				if (uA.mC.cIb == 100) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cIb--;
				upI("SETTINGS", 19);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 20:
				if (uA.mC.cJc == 5) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJc--;
				upI("SETTINGS", 20);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 29:
				if (uA.mC.cJd == 0) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJd--;
				upI("SETTINGS", 2, 20, 29);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			}
		} else if (b.equals(ClickType.SHIFT_LEFT)) {
			switch (c) {
			case 1:
				int d = uA.mC.cIc + 100;
				if (d > 23999) {
					uA.mC.cIc = d - 24000;
				} else {
					uA.mC.cIc = d;
				}
				upI("SETTINGS", 1);
				upS("WORLD");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 2:
				d = uA.mC.cJa + 60;
				if (d > 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJa = d;
				upI("SETTINGS", 2);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 11:
				d = uA.mC.cJb + 100;
				if (d > 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJb = d;
				upI("SETTINGS", 11);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 19:
				d = uA.mC.cIb + 100;
				if (d > 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cIb = d;
				upI("SETTINGS", 19);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 20:
				d = uA.mC.cJc + 100;
				if (d > 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJc = d;
				upI("SETTINGS", 20);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 29:
				d = uA.mC.cJd + 60;
				if (d > 100000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJd = d;
				upI("SETTINGS", 2, 20, 29);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			}
		} else if (b.equals(ClickType.SHIFT_RIGHT)) {
			switch (c) {
			case 1:
				int d = uA.mC.cIc - 100;
				if (d < 0) {
					uA.mC.cIc = 24000 + d;
				} else {
					uA.mC.cIc = d;
				}
				upI("SETTINGS", 1);
				upS("WORLD");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 2:
				d = uA.mC.cJa - 60;
				if (d < 0) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJa = d;
				upI("SETTINGS", 2);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 11:
				d = uA.mC.cJb - 100;
				if (d < 50) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJb = d;
				upI("SETTINGS", 11);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 19:
				d = uA.mC.cIb - 100;
				if (d < 100) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cIb = d;
				upI("SETTINGS", 19);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 20:
				d = uA.mC.cJc - 100;
				if (d < 5) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJc = d;
				upI("SETTINGS", 20);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 29:
				d = uA.mC.cJd - 60;
				if (d < 0) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cJd = d;
				upI("SETTINGS", 2, 20, 29);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			}
		}
	}

	private void cGIv(UHCPlayer a, ClickType b, int c) {
		if (b.equals(ClickType.LEFT)) {
			switch (c) {
			case 9:
				uA.mC.cEa = !uA.mC.cEa;
				upI("GAMERULE", 9);
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 10:
				uA.mC.cEb = !uA.mC.cEb;
				upI("GAMERULE", 10);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 11:
				uA.mC.cEc = !uA.mC.cEc;
				upI("GAMERULE", 11);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 12:
				uA.mC.cEd = !uA.mC.cEd;
				upI("GAMERULE", 12);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 13:
				uA.mC.cEe = !uA.mC.cEe;
				upI("GAMERULE", 13);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 14:
				uA.mC.cEf = !uA.mC.cEf;
				upI("GAMERULE", 14);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 15:
				uA.mC.cEg = !uA.mC.cEg;
				upI("GAMERULE", 15);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 16:
				if (uA.mC.cEh == 10) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cEh++;
				upI("GAMERULE", 16);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 17:
				uA.mC.cEi = !uA.mC.cEi;
				upI("GAMERULE", 17);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 18:
				uA.mC.cEj = !uA.mC.cEj;
				upI("GAMERULE", 18);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 19:
				if (uA.mC.cEk == 10) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cEk++;
				upI("GAMERULE", 19);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 31:
				a.uB.openInventory(uB.get("MAIN"));
				a.setInvKey("MENU", "MAIN");
				uC.put(a.uB.getUniqueId(), "MAIN");
				a.playLocalSound(Sound.UI_BUTTON_CLICK, 1.2f);
				return;
			}
		} else if (b.equals(ClickType.RIGHT)) {
			switch (c) {
			case 16:
				if (uA.mC.cEh == 0) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cEh--;
				upI("GAMERULE", 16);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 19:
				if (uA.mC.cEk == 0) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cEk--;
				upI("GAMERULE", 19);
				upS("GAMERULE");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			}
		}
	}

	private void cTIv(UHCPlayer a, ClickType b, int c) {
		if (b.equals(ClickType.LEFT)) {
			switch (c) {
			case 1:
				uA.mC.cGd = !uA.mC.cGd;
				upI("TEAM", 1);
				upS("TEAM");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 2:
				uA.mC.cGe = !uA.mC.cGe;
				upI("TEAM", 2);
				upS("TEAM");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 3:
				if (uA.mC.cGf == 3) {
					uA.mC.cGf = 0;
				} else {
					uA.mC.cGf++;
				}
				upI("TEAM", 3);
				upS("TEAM");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 4:
				if (uA.mC.cGg == 3) {
					uA.mC.cGg = 0;
				} else {
					uA.mC.cGg++;
				}
				upI("TEAM", 4);
				upS("TEAM");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 6:
				if (uA.mC.cGb == 20000) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cGb++;
				upI("TEAM", 6);
				upS("TEAM2");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			case 8:
				a.uB.openInventory(uB.get("MAIN"));
				a.setInvKey("MENU", "MAIN");
				uC.put(a.uB.getUniqueId(), "MAIN");
				a.playLocalSound(Sound.UI_BUTTON_CLICK, 1.2f);
				return;
			}
		} else if (b.equals(ClickType.RIGHT)) {
			switch (c) {
			case 6:
				if (uA.mC.cGb == 1) {
					a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0.5f);
					return;
				}
				uA.mC.cGb--;
				upI("TEAM", 6);
				upS("TEAM2");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				return;
			}
		}
	}

	private void cIv(String a, String b, int c, int d) {
		Inventory e = uA.getServer().createInventory(null, c, "\u00A78UHC>\u00A7r " + b);
		uB.put(a, e);
		switch (a.hashCode()) {
		case 2358713:
			upI(a, 3, 5, (c == 18 ? 13 : -1));
			break;
		case -2077709277:
			upI(a, 1, 2, 3, 4, 5, 6, 7, 10, 11, 14, 16, 19, 20, 25, 29);
			break;
		case -985626130:
			upI(a, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
			break;
		case 2570845:
			upI(a, 1, 2, 3, 4, 6);
			break;
		}
		if (d != -1 && d < c) {
			e.setItem(d, nItem(Material.BARRIER, 0, "\u00A76\u00A7lGo Back?", ""));
		}
	}

	private void upS(String a) {
		switch (a.hashCode()) {
		case 82781042:
			uA.getServer().getWorlds().get(0).setTime(uA.mC.cIc);
			return;
		case -1475342784:
			uA.mE.updateGoldenHead();
			return;
		case -985626130:
			uA.mE.updateGamerule();
			return;
		case 2570845:
			uA.mE.gD.updateOptions(true);
			return;
		case 79696245:
			uA.mE.gD.updateOptions(false);
			return;
		}
	}

	private void upI(String a, int... b) {
		Inventory c = uB.get(a);
		switch (a.hashCode()) {
		case 2358713:
			for (int d : b) {
				switch (d) {
				case 3:
					c.setItem(3, nItem(Material.PAPER, 0, "\u00A76\u00A7lGame Settings", "0|Want to change some UHC", "0|settings?"));
					break;
				case 5:
					c.setItem(5, nItem(Material.PAPER, 0, "\u00A76\u00A7lGamerule", "0|Want to change some", "0|gamerule?"));
					break;
				case 13:
					c.setItem(13, nItem(Material.PAPER, 0, "\u00A76\u00A7lTeam Options", "0|Want to change some", "0|team settings?"));
					break;
				}
			}
			return;
		case -2077709277:
			for (int d : b) {
				switch (d) {
				case 1:
					c.setItem(1, nItem(Material.INK_SACK, 10, "\u00A73\u00A7l[W]\u00A76\u00A7l Sun Time", "1|World Settings", "3|" + asRealClock(uA.mC.cIc)));
					break;
				case 2:
					c.setItem(2, nItem(Material.INK_SACK, (uA.mC.cJa != 0 && uA.mC.cJd != 0 ? 10 : 8), "\u00A73\u00A7l[WB]\u00A76\u00A7l Start Delay", "1|World Border", "3|" + asClock(uA.mC.cJa)));
					break;
				case 3:
					c.setItem(3, nItem(Material.INK_SACK, (uA.mC.cNa.length() != 0 && uA.mC.cNb != 0 ? 10 : 8), "\u00A73\u00A7l[M]\u00A76\u00A7l Time Delay", "1|Marker", "3|" + asClock(uA.mC.cNb * 60)));
					break;
				case 4:
					c.setItem(4, nItem(Material.INK_SACK, 10, "\u00A73\u00A7l[DIG]\u00A76\u00A7l Max Disconnected Timeout", "1|Offline Kicker", "3|" + asClock(uA.mC.cPa * 60)));
					break;
				case 5:
					c.setItem(5, nItem(Material.INK_SACK, (uA.mC.cMa ? 10 : 8), "\u00A73\u00A7l[FSP]\u00A76\u00A7l Enabled", "1|Freeze Player", "2|" + statC(uA.mC.cMa)));
					break;
				case 6:
					c.setItem(6, nItem(Material.INK_SACK, (uA.mC.cOa ? 10 : 8), "\u00A73\u00A7l[DL]\u00A76\u00A7l Damage Logger", "1|Damage Logger", "2|" + statC(uA.mC.cOa)));
					break;
				case 7:
					c.setItem(7, nItem(Material.INK_SACK, (uA.mC.cLa ? 10 : 8), "\u00A73\u00A7l[GH]\u00A76\u00A7l Enabled", "1|Golden Head", "2|" + statC(uA.mC.cLa)));
					break;
				case 10:
					c.setItem(10, nItem(Material.INK_SACK, 10, "\u00A73\u00A7l[W]\u00A76\u00A7l Difficulty", "1|World Settings", "2|" + wDifC(uA.mC.cIa)));
					break;
				case 11:
					c.setItem(11, nItem(Material.INK_SACK, 10, "\u00A73\u00A7l[WB]\u00A76\u00A7l Start Position", "1|World Border", "3|" + uA.mC.cJb));
					break;
				case 14:
					c.setItem(14, nItem(Material.INK_SACK, (uA.mC.cMa ? 10 : 8), "\u00A73\u00A7l[FSP]\u00A76\u00A7l Radius Size", "1|Freeze Player", "3|" + uA.mC.cMb));
					break;
				case 16:
					c.setItem(16, nItem(Material.INK_SACK, (uA.mC.cLa ? (uA.mC.cLb ? 10 : 8) : 8), "\u00A73\u00A7l[GH]\u00A76\u00A7l Default Head Apple", "1|Golden Head", "2|" + statC(uA.mC.cLb)));
					break;
				case 19:
					c.setItem(19, nItem(Material.INK_SACK, 10, "\u00A73\u00A7l[W]\u00A76\u00A7l Arena Size", "1|World Settings", "3|" + uA.mC.cIb));
					break;
				case 20:
					c.setItem(20, nItem(Material.INK_SACK, (uA.mC.cJd != 0 ? 10 : 8), "\u00A73\u00A7l[WB]\u00A76\u00A7l Stop Position", "1|World Border", "3|" + uA.mC.cJc));
					break;
				case 25:
					c.setItem(25, nItem(Material.INK_SACK, (uA.mC.cLa ? (uA.mC.cLc ? 10 : 8) : 8), "\u00A73\u00A7l[GH]\u00A76\u00A7l Golden Head Apple", "1|Golden Head", "2|" + statC(uA.mC.cLc)));
					break;
				case 29:
					c.setItem(29, nItem(Material.INK_SACK, (uA.mC.cJd != 0 ? 10 : 8), "\u00A73\u00A7l[WB]\u00A76\u00A7l Shrink Time", "1|World Border", "3|" + asClock(uA.mC.cJd)));
					break;
				}
			}
			return;
		case -985626130:
			for (int d : b) {
				switch (d) {
				case 9:
					c.setItem(9, nItem(Material.INK_SACK, (uA.mC.cEa ? 8 : 10), "\u00A76\u00A7lEternal Day", "2|" + statC(uA.mC.cEa)));
					break;
				case 10:
					c.setItem(10, nItem(Material.INK_SACK, (uA.mC.cEb ? 10 : 8), "\u00A76\u00A7lEntity Drops", "2|" + statC(uA.mC.cEb)));
					break;
				case 11:
					c.setItem(11, nItem(Material.INK_SACK, (uA.mC.cEc ? 10 : 8), "\u00A76\u00A7lFire Tick", "2|" + statC(uA.mC.cEc)));
					break;
				case 12:
					c.setItem(12, nItem(Material.INK_SACK, (uA.mC.cEd ? 10 : 8), "\u00A76\u00A7lMob Loot", "2|" + statC(uA.mC.cEd)));
					break;
				case 13:
					c.setItem(13, nItem(Material.INK_SACK, (uA.mC.cEe ? 10 : 8), "\u00A76\u00A7lMob Spawning", "2|" + statC(uA.mC.cEe)));
					break;
				case 14:
					c.setItem(14, nItem(Material.INK_SACK, (uA.mC.cEf ? 10 : 8), "\u00A76\u00A7lTile Drops", "2|" + statC(uA.mC.cEf)));
					break;
				case 15:
					c.setItem(15, nItem(Material.INK_SACK, (uA.mC.cEg ? 10 : 8), "\u00A76\u00A7lMob Griefing", "2|" + statC(uA.mC.cEg)));
					break;
				case 16:
					c.setItem(16, nItem(Material.INK_SACK, (uA.mC.cEh != 0 ? 10 : 8), "\u00A76\u00A7lTick Speed", (uA.mC.cEh != 0 ? "3|" + uA.mC.cEh : "2|" + statC(uA.mC.cEh != 0))));
					break;
				case 17:
					c.setItem(17, nItem(Material.INK_SACK, (uA.mC.cEi ? 10 : 8), "\u00A76\u00A7lShort Debug Info", "2|" + statC(uA.mC.cEi)));
					break;
				case 18:
					c.setItem(18, nItem(Material.INK_SACK, (uA.mC.cEj ? 10 : 8), "\u00A76\u00A7lSpectators Generate Chunks", "2|" + statC(uA.mC.cEj)));
					break;
				case 19:
					c.setItem(19, nItem(Material.INK_SACK, (uA.mC.cEk != 0 ? 10 : 8), "\u00A76\u00A7lSpawn Radius", (uA.mC.cEk != 0 ? "3|" + uA.mC.cEk : "2|" + statC(uA.mC.cEk != 0))));
					break;
				}
			}
			return;
		case 2570845:
			for (int d : b) {
				switch (d) {
				case 1:
					c.setItem(1, nItem(Material.INK_SACK, (uA.mC.cGd ? 10 : 8), "\u00A76\u00A7lFriendly Fire", "2|" + statC(uA.mC.cGd)));
					break;
				case 2:
					c.setItem(2, nItem(Material.INK_SACK, (uA.mC.cGe ? 10 : 8), "\u00A76\u00A7lSee Friendly Invisibles", "2|" + statC(uA.mC.cGe)));
					break;
				case 3:
					c.setItem(3, nItem(Material.INK_SACK, (uA.mC.cGf != 3 ? 10 : 8), "\u00A76\u00A7lName Tag Visibillity", "2|" + teamTagC(uA.mC.cGf)));
					break;
				case 4:
					c.setItem(4, nItem(Material.INK_SACK, (uA.mC.cGg != 3 ? 10 : 8), "\u00A76\u00A7lCollision Rule", "2|" + teamColC(uA.mC.cGg)));
					break;
				case 6:
					c.setItem(6, nItem(Material.INK_SACK, 10, "\u00A76\u00A7lMax Team Players", "3|" + uA.mC.cGb));
					break;
				}
			}
			return;
		}
	}
}
