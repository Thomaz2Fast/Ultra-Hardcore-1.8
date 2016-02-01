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
import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.PlayerData;

public class UhcTeam {
	private Main pl;
	private Config c;
	private Scoreboard sb;
	private String[] teamList = { "Black", "Dark_Blue", "Dark_Green", "Dark_Aqua", "Dark_Red", "Dark_Purple", "Gold", "Gray", "Dark_Gray", "Blue", "Green", "Aqua", "Red", "Light_Purple", "Yellow", "White" };
	private String[] teamColor = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	private boolean active = false;
	private Inventory iv;
	private ItemStack item;

	public UhcTeam(Main main) {
		pl = main;
		c = main.getPlConf();
		sb = pl.getServer().getScoreboardManager().getMainScoreboard();

		iv = pl.getServer().createInventory(null, 18, "          - Select Team -");
		for (int i = 0; i < teamList.length; i++) {
			if (sb.getTeam(teamList[i]) != null) {
				sb.getTeam(teamList[i]).unregister();
			}
			Team t = sb.registerNewTeam(teamList[i]);
			t.setPrefix("§" + teamColor[i]);
			t.setSuffix("§r");
			t.setAllowFriendlyFire(c.go_Friendly());
			t.setCanSeeFriendlyInvisibles(c.go_SeeFriendly());
			t.setNameTagVisibility(getNameTagVis(c.go_NameTagVisibility()));
			ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§" + teamColor[i] + "§l" + teamList[i].replace("_", " "));
			im.setLore(Arrays.asList("", "§8Max " + c.g_maxTeamPlayer() + " Player" + (c.g_maxTeamPlayer() == 1 ? "" : "s")));
			is.setItemMeta(im);
			iv.addItem(is);
		}
		ItemStack is = new ItemStack(Material.BARRIER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§cRemove me from my team");
		is.setItemMeta(im);
		iv.setItem(17, is);
		item = new ItemStack(Material.PAPER);
		im = item.getItemMeta();
		im.setDisplayName("§eSelect Team");
		item.setItemMeta(im);
	}

	/**
	 * Command > AutoTeam
	 */
	public void cmdAutoTeam() {
		ArrayList<PlayerData> playerDatas = new ArrayList<>();
		ArrayList<Team> tmBuff = new ArrayList<>();
		ArrayList<Team> tmSelect = new ArrayList<>();
		Random r = new Random();
		int max = Math.round(pl.getRegPlayerData().size() / c.g_maxTeamPlayer());
		int pos = 0;
		if (max == 0) {
			max = 1;
		} else if (max > 16) {
			max = 16;
		}
		for (PlayerData p : pl.getRegPlayerData().values()) {
			playerDatas.add(p);
		}
		for (Team t : sb.getTeams()) {
			if (t.getSize() != 0) {
				for (String e : t.getEntries()) {
					t.removeEntry(e);
				}
			}
			tmBuff.add(t);
		}
		for (int i = 0; i < max; i++) {
			Team t = tmBuff.get(r.nextInt(tmBuff.size()));
			tmSelect.add(t);
			tmBuff.remove(t);
		}
		for (int i = 0; i <= playerDatas.size(); i++) {
			PlayerData p = playerDatas.get(r.nextInt(playerDatas.size()));
			playerJoinTeam(p, tmSelect.get(pos).getName(), true);
			playerDatas.remove(p);
			if (pos == (tmSelect.size() - 1)) {
				pos = 0;
			} else {
				pos++;
			}
		}
	}

	/**
	 * Check > Team Selection is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Set > Active Team Selection on every player on join
	 */
	public void setActive() {
		active = true;
	}

	/**
	 * Get > Team Selection Item
	 */
	public ItemStack getTeamSelecter() {
		return item;
	}

	/**
	 * Get > Team Selection Inventory
	 */
	public Inventory getTeamSelecterInventory() {
		return iv;
	}

	/**
	 * Add team selection item to player inventory
	 */
	public void getTeamSelecterToPlayer(PlayerData p) {
		if (!p.cp.getInventory().contains(item)) {
			p.cp.getInventory().setItem(c.g_selectTeamSlot(), item);
			p.sendMessage("SelectTeam", "Right click on the §epaper item§7 to select team.");
		}
	}

	/**
	 * Inventory [Team Selection] > Click event
	 */
	public void clickEvent(PlayerData p, ClickType ct, int i, ItemStack is) {
		if (ct.equals(ClickType.LEFT)) {
			if (i < 16) {
				if (is.hasItemMeta()) {
					playerJoinTeam(p, is.getItemMeta().getDisplayName().replace(" ", "_").replaceAll("(§([a-fl0-9]))", ""), false);
				}
			} else if (i == 17) {
				playerRemoveTeam(p);
			}
		}
	}

	/**
	 * Add player to the team
	 */
	public void playerJoinTeam(PlayerData p, String s, boolean b) {
		if (b) {
			p.playLocalSound(Sound.ORB_PICKUP, 1f);
			sb.getTeam(s).addEntry(p.cp.getName());
			p.sendMessage("Team", "You joined team " + sb.getTeam(s).getPrefix() + "§l" + sb.getTeam(s).getName().replace("_", " ") + "§7.");
			updateInventory();
			return;
		}
		if (!sb.getTeam(s).getEntries().contains(p.cp.getName())) {
			if (sb.getTeam(s).getSize() < c.g_maxTeamPlayer()) {
				p.playLocalSound(Sound.ORB_PICKUP, 1f);
				sb.getTeam(s).addEntry(p.cp.getName());
				p.sendMessage("Team", "You joined team " + sb.getTeam(s).getPrefix() + "§l" + sb.getTeam(s).getName().replace("_", " ") + "§7.");
				updateInventory();
				if (c.server()) {
					pl.getGame().getServer().canStart();
				}
				return;
			}
		}
		p.playLocalSound(Sound.NOTE_BASS, 0f);
	}

	/**
	 * Remove player from team
	 */
	public void playerRemoveTeam(PlayerData p) {
		if (sb.getEntryTeam(p.cp.getName()) != null) {
			sb.getEntryTeam(p.cp.getName()).removeEntry(p.cp.getName());
			p.playLocalSound(Sound.ORB_PICKUP, 1f);
			p.sendMessage("Team", "You are no longer in team.");
			updateInventory();
			return;
		}
		p.playLocalSound(Sound.NOTE_BASS, 0f);
	}

	/**
	 * Unload Teams
	 */
	public void unLoadTeam() {
		for (String s : teamList) {
			if (sb.getTeam(s) != null) {
				sb.getTeam(s).unregister();
			}
		}
	}

	/**
	 * Update > Team Settings
	 */
	public void updateSettings() {
		if (c.g_teamMode()) {
			for (Team t : sb.getTeams()) {
				t.setAllowFriendlyFire(c.go_Friendly());
				t.setCanSeeFriendlyInvisibles(c.go_SeeFriendly());
				t.setNameTagVisibility(getNameTagVis(c.go_NameTagVisibility()));
			}
			updateInventory();
		}
	}

	/**
	 * Update > Team Selection Inventory
	 */
	public void updateInventory() {
		for (ItemStack is : iv) {
			if (is != null) {
				if (is.getType().equals(Material.ENCHANTED_BOOK)) {
					ArrayList<String> l = new ArrayList<>();
					ItemMeta im = is.getItemMeta();
					String t = im.getDisplayName().replace(" ", "_").replaceAll("(§([a-fl0-9]))", "");
					if (sb.getTeam(t).getSize() != 0) {
						int i = 1;
						for (String e : sb.getTeam(t).getEntries()) {
							if (i == 6) {
								l.add("");
								l.add("§7" + (sb.getTeam(t).getEntries().size() - (i - 1)) + " more player(s)");
								break;
							}
							l.add("§7§o" + e);
							i++;
						}
						l.add("");
					}
					l.add("§8Max " + c.g_maxTeamPlayer() + " Player" + (c.g_maxTeamPlayer() == 1 ? "" : "s"));
					im.setLore(l);
					is.setItemMeta(im);
				}
			}
		}
	}

	// :: PRIVATE :: //

	/**
	 * Get Name Tag visibility by id.
	 */
	public NameTagVisibility getNameTagVis(int i) {
		switch (i) {
		case 1:
			return NameTagVisibility.HIDE_FOR_OTHER_TEAMS;
		case 2:
			return NameTagVisibility.HIDE_FOR_OWN_TEAM;
		case 3:
			return NameTagVisibility.NEVER;
		default:
			return NameTagVisibility.ALWAYS;
		}
	}

}