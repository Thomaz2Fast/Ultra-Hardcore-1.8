/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2019> Thomaz2Fast
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

import org.bukkit.ChatColor;
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

import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;

public class Teams {
	private Main pl;
	private Scoreboard scoreboard;
	public ItemStack selectItem;
	public Inventory inventory;
	public boolean isSelectItem = false;

	public Teams(Main pl) {
		this.pl = pl;
	}

	public void click(UHCPlayer u, ClickType click, int slot) {
		if (click.equals(ClickType.LEFT))
			if (slot < inventory.getSize() - 1 && inventory.getItem(slot) != null && inventory.getItem(slot).hasItemMeta()) {
				if (u.player.getCooldown(Material.ENCHANTED_BOOK) == 0)
					join(u, inventory.getItem(slot).getItemMeta().getDisplayName(), false);
			} else if (slot == inventory.getSize() - 1)
				quit(u);
	}

	public void join(UHCPlayer u, String name, boolean isAutoJoin) {
		Team team = scoreboard.getTeam(F.mcColorRm(name).replace(" ", "_"));
		if (isAutoJoin) {
			joinTeam(u, team);
			return;
		}
		if (!team.getEntries().contains(u.player.getName()) && team.getSize() < pl.config.gameMaxTeam) {
			joinTeam(u, team);
			u.player.setCooldown(Material.ENCHANTED_BOOK, 250);
			updateInv();
			if (pl.config.serverEnable)
				pl.gameManager.server.startCountdown();
			return;
		}
		u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0f);
	}

	public void load() {
		scoreboard = pl.getServer().getScoreboardManager().getMainScoreboard();
		selectItem = F.item(Material.PAPER, "&eSelect Team");
		inventory = pl.getServer().createInventory(null, (int) Math.ceil(((pl.config.gameTeamNames.size() + 1) / 9.0)) * 9, "          - Select Team -");
		for(String tm : pl.config.gameTeamNames) {
			String[] l = tm.split("\\|");
			Team team = scoreboard.registerNewTeam(l[0].replace(" ", "_"));
			team.setDisplayName("\u00A7l" + l[0]);
			team.setColor(ChatColor.getByChar(l[1]));
			team.setOption(Option.COLLISION_RULE, OptionStatus.values()[pl.config.gameCollision == 3 ? 2 : pl.config.gameCollision == 2 ? 3 : pl.config.gameCollision]);
			team.setAllowFriendlyFire(pl.config.gameIsFriendly);
			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.values()[pl.config.gameNameTag]);
			team.setCanSeeFriendlyInvisibles(pl.config.gameSeeFriendly);
			ItemStack c = F.item(Material.ENCHANTED_BOOK, team.getColor() + team.getDisplayName(), "0|", "0|&8Max " + pl.config.gameMaxTeam + " player" + (pl.config.gameMaxTeam != 1 ? "s" : ""));
			inventory.addItem(c);
		}
		inventory.setItem(inventory.getSize() - 1, F.item(Material.BARRIER, "&cSpectator Mode"));
	}

	public void openInventory(UHCPlayer u) {
		u.player.openInventory(inventory);
		u.setInvKey(1, 0);
	}

	public void quit(UHCPlayer u) {
		if (scoreboard.getEntryTeam(u.player.getName()) != null) {
			scoreboard.getEntryTeam(u.player.getName()).removeEntry(u.player.getName());
			u.sendActionMessage("\u00A7LYou are now in spectator mode");
			u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
			updateInv();
			return;
		}
		u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 0f);
	}

	public void unload() {
		for(String tm : pl.config.gameTeamNames)
			unRegistryTeam(tm.split("\\|")[0].replace(" ", "_"));
	}

	public void updateInv() {
		inventory.forEach(item -> {
			if (item != null && item.getType().equals(Material.ENCHANTED_BOOK)) {
				List<String> lore = new ArrayList<>();
				ItemMeta meta = item.getItemMeta();
				Team team = scoreboard.getTeam(F.mcColorRm(meta.getDisplayName()).replace(" ", "_"));
				if (team.getSize() != 0) {
					int i = 0;
					for (String e : team.getEntries()) {
						if (i == 5) {
							int size = team.getSize() - i;
							lore.add("");
							lore.add("\u00A77" + size + " more player" + (size != 1 ? "s" : ""));
							break;
						}
						lore.add("\u00A77\u00A7O" + e);
						i++;
					}
				}
				lore.add("");
				lore.add("\u00A78Max " + pl.config.gameMaxTeam + " player" + (pl.config.gameMaxTeam != 1 ? "s" : ""));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
		});
	}

	public void updateOptions(boolean b) {
		if (b) {
			for(String tm : pl.config.gameTeamNames) {
				Team team = scoreboard.getTeam(tm.split("\\|")[0].replace(" ", "_"));
				if(team != null) {
					team.setOption(Option.COLLISION_RULE, OptionStatus.values()[pl.config.gameCollision == 3 ? 2 : pl.config.gameCollision == 2 ? 3 : pl.config.gameCollision]);
					team.setAllowFriendlyFire(pl.config.gameIsFriendly);
					team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.values()[pl.config.gameNameTag]);
					team.setCanSeeFriendlyInvisibles(pl.config.gameSeeFriendly);
				}
			}
		}
		updateInv();
	}

	// ---------------------------------------------------------------------------

	private void joinTeam(UHCPlayer u, Team team) {
		team.addEntry(u.player.getName());
		u.sendActionMessage("\u00A7LYou joined team " + team.getColor() + team.getDisplayName());
		u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
	}

	private void unRegistryTeam(String name) {
		if (scoreboard.getTeam(name) != null)
			scoreboard.getTeam(name).unregister();
	}
}
