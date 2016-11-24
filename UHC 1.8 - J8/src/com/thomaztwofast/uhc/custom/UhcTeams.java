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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class UhcTeams extends Function {
	private Main uA;
	private Scoreboard uB;
	public String[] uCa = { "Black", "Dark_Blue", "Dark_Green", "Dark_Aqua", "Dark_Red", "Dark_Purple", "Gold", "Gray", "Dark_Gray", "Blue", "Green", "Aqua", "Red", "Light_Purple", "Yellow", "White" };
	private String[] uCb = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	public boolean uCc = false;
	public ItemStack uD;
	public Inventory uE;

	public UhcTeams(Main a) {
		uA = a;
	}

	public void load() {
		uB = uA.getServer().getScoreboardManager().getMainScoreboard();
		uD = nItem(Material.PAPER, 0, "\u00A7eSelect Team", "");
		cInv();
	}

	public void unload() {
		for (int a = 0; a < uCa.length; a++) {
			if (uB.getTeam(uCa[a]) != null) {
				uB.getTeam(uCa[a]).unregister();
			}
		}
	}

	public void joinTeam(UHCPlayer a, String b, boolean c) {
		if (c) {
			uB.getTeam(b).addEntry(a.uB.getName());
			a.sendCommandMessage("Team", "You joined team " + uB.getTeam(b).getPrefix() + "\u00A7l\u00A7o" + b.replace("_", " ") + "\u00A77\u00A7o.");
			a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
			return;
		}
		if (!uB.getTeam(b).getEntries().contains(a.uB.getName())) {
			if (uB.getTeam(b).getSize() < uA.mC.cGb) {
				uB.getTeam(b).addEntry(a.uB.getName());
				a.sendCommandMessage("Team", "You joined team " + uB.getTeam(b).getPrefix() + "\u00A7l\u00A7o" + b.replace("_", " ") + "\u00A77\u00A7o.");
				a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
				updateInv();
				if (uA.mC.cFa) {
					uA.mE.gC.canStart();
				}
				return;
			}
		}
		a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0f);
	}

	public void quitTeam(UHCPlayer a) {
		if (uB.getEntryTeam(a.uB.getName()) != null) {
			uB.getEntryTeam(a.uB.getName()).removeEntry(a.uB.getName());
			a.sendCommandMessage("Team", "You are now in spectator mode.");
			a.playLocalSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
			updateInv();
			return;
		}
		a.playLocalSound(Sound.BLOCK_NOTE_BASS, 0f);
	}

	public void openTeam(UHCPlayer a) {
		a.uB.openInventory(uE);
		a.setInvKey("TEAM", "0");
	}

	public void clickEvent(UHCPlayer a, ClickType b, int c) {
		if (b.equals(ClickType.LEFT)) {
			if (c < 16 && uE.getItem(c).hasItemMeta()) {
				joinTeam(a, uE.getItem(c).getItemMeta().getDisplayName().replaceAll("(\u00A7([a-fl0-9]))", "").replace(" ", "_"), false);
			} else if (c == 17) {
				quitTeam(a);
			}
		}
	}

	public void updateInv() {
		for (ItemStack a : uE) {
			if (a != null) {
				if (a.getType().equals(Material.ENCHANTED_BOOK)) {
					List<String> b = new ArrayList<>();
					ItemMeta c = a.getItemMeta();
					String d = c.getDisplayName().replaceAll("(\u00A7([a-fl0-9]))", "").replace(" ", "_");
					if (uB.getTeam(d).getSize() != 0) {
						int e = 0;
						for (String f : uB.getTeam(d).getEntries()) {
							if (e == 5) {
								int h = uB.getTeam(d).getSize() - e;
								b.add("");
								b.add("\u00A77 " + h + " more player" + (h > 1 ? "s" : ""));
								break;
							}
							b.add("\u00A77\u00A7o" + f);
							e++;
						}
					}
					b.add("");
					b.add("\u00A78Max " + uA.mC.cGb + " Player" + (uA.mC.cGb > 1 ? "s" : ""));
					c.setLore(b);
					a.setItemMeta(c);
				}
			}
		}
	}

	public void updateOptions(boolean a) {
		if (a) {
			for (int b = 0; b < uCa.length; b++) {
				if (uB.getTeam(uCa[b]) != null) {
					Team c = uB.getTeam(uCa[b]);
					c.setAllowFriendlyFire(uA.mC.cGd);
					c.setCanSeeFriendlyInvisibles(uA.mC.cGe);
					c.setOption(Option.NAME_TAG_VISIBILITY, tStat(uA.mC.cGf));
					c.setOption(Option.COLLISION_RULE, tStat(uA.mC.cGg));
				}
			}
			return;
		}
		updateInv();
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private void cInv() {
		uE = uA.getServer().createInventory(null, 18, "          - Select Team -");
		for (int a = 0; a < uCa.length; a++) {
			if (uB.getTeam(uCa[a]) != null) {
				uB.getTeam(uCa[a]).unregister();
			}
			Team b = uB.registerNewTeam(uCa[a]);
			b.setPrefix("\u00A7" + uCb[a]);
			b.setSuffix("\u00A7r");
			b.setAllowFriendlyFire(uA.mC.cGd);
			b.setCanSeeFriendlyInvisibles(uA.mC.cGe);
			b.setOption(Option.NAME_TAG_VISIBILITY, tStat(uA.mC.cGf));
			b.setOption(Option.COLLISION_RULE, tStat(uA.mC.cGf));
			ItemStack c = nItem(Material.ENCHANTED_BOOK, 0, "\u00A7" + uCb[a] + "\u00A7l" + uCa[a].replace("_", " "), "0|", "1|\u00A78Max " + uA.mC.cGb + " Player" + (uA.mC.cGb > 1 ? "s" : ""));
			uE.addItem(c);
		}
		uE.setItem(17, nItem(Material.BARRIER, 0, "\u00A7cSpectator Mode", ""));
	}

	private OptionStatus tStat(int a) {
		if (a == 0) {
			return OptionStatus.ALWAYS;
		} else if (a == 1) {
			return OptionStatus.FOR_OTHER_TEAMS;
		} else if (a == 2) {
			return OptionStatus.FOR_OWN_TEAM;
		}
		return OptionStatus.NEVER;
	}
}
