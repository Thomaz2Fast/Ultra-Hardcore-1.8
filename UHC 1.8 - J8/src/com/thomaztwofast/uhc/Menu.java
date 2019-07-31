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

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;

public class Menu {
	private Main pl;
	private HashMap<Integer, Inventory> invStorage = new HashMap<>();
	private HashMap<String, Integer> playersHistory = new HashMap<>();
	public ItemStack itemStack;
	private Material[] materials = { Material.GRAY_DYE, Material.LIME_DYE, Material.PAPER, Material.BARRIER };

	public Menu(Main pl) {
		this.pl = pl;
	}

	public void click(UHCPlayer u, ClickType click, int slot) {
		switch (u.invHistory[1]) {
		case 0:
			if (click.equals(ClickType.LEFT)) {
				switch (slot) {
				case 3:
				case 5:
				case 13:
					changeInv(u, slot == 3 ? 1 : slot == 5 ? 2 : 3);
					return;
				}
			}
			return;
		case 1:
			if (click.equals(ClickType.LEFT)) {
				switch (slot) {
				case 0:
					pl.config.worldTime = pl.config.worldTime == 23999 ? 0 : pl.config.worldTime + 1;
					pl.getServer().getWorlds().get(0).setTime(pl.config.worldTime);
					updateSettings(u, slot);
					return;
				case 1:
					if (pl.config.borderDelay == 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderDelay++;
					updateSettings(u, slot);
					return;
				case 2:
					if (pl.config.markerDelay == 20) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.markerDelay++;
					updateSettings(u, slot);
					return;
				case 3:
					if (pl.config.kickerDelay == 20) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.kickerDelay++;
					updateSettings(u, slot);
					return;
				case 4:
					pl.config.freezeEnable = !pl.config.freezeEnable;
					updateSettings(u, slot, 13);
					return;
				case 5:
					pl.config.dmgEnable = !pl.config.dmgEnable;
					updateSettings(u, slot);
					return;
				case 6:
					pl.config.headEnable = !pl.config.headEnable;
					pl.gameManager.updateGoldenHead();
					updateSettings(u, slot, 15, 24);
					return;
				case 7:
					pl.config.gameOldCombat = !pl.config.gameOldCombat;
					pl.PLAYERS.values().forEach(a -> a.player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(pl.config.gameOldCombat ? 1000d : 4d));
					updateSettings(u, slot);
					return;
				case 9:
					pl.config.worldDifficulty = pl.config.worldDifficulty == 3 ? 1 : pl.config.worldDifficulty + 1;
					updateSettings(u, slot);
					return;
				case 10:
					if (pl.config.borderStartSize == 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderStartSize++;
					updateSettings(u, slot);
					return;
				case 13:
					if (pl.config.freezeSize == 15) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.freezeSize++;
					updateSettings(u, slot);
					return;
				case 15:
					pl.config.headDefault = !pl.config.headDefault;
					pl.gameManager.updateGoldenHead();
					updateSettings(u, slot);
					return;
				case 18:
					if (pl.config.worldSize == 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.worldSize++;
					updateSettings(u, slot);
					return;
				case 19:
					if (pl.config.borderEndSize == 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderEndSize++;
					updateSettings(u, slot);
					return;
				case 24:
					pl.config.headGolden = !pl.config.headGolden;
					pl.gameManager.updateGoldenHead();
					updateSettings(u, slot);
					return;
				case 28:
					if (pl.config.borderTime == 100000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderTime++;
					updateSettings(u, slot, 1, 19);
					return;
				case 40:
					changeInv(u, 0);
					return;
				}
			} else if (click.equals(ClickType.RIGHT)) {
				switch (slot) {
				case 0:
					pl.config.worldTime = pl.config.worldTime == 23999 ? 0 : pl.config.worldTime - 1;
					pl.getServer().getWorlds().get(0).setTime(pl.config.worldTime);
					updateSettings(u, slot);
					return;
				case 1:
					if (pl.config.borderDelay == 0) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderDelay--;
					updateSettings(u, slot);
					return;
				case 2:
					if (pl.config.markerDelay == 0) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.markerDelay--;
					updateSettings(u, slot);
					return;
				case 3:
					if (pl.config.kickerDelay == 1) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.kickerDelay--;
					updateSettings(u, slot);
					return;
				case 10:
					if (pl.config.borderStartSize == 50) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderStartSize--;
					updateSettings(u, slot);
					return;
				case 13:
					if (pl.config.freezeSize == 5) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.freezeSize--;
					updateSettings(u, slot);
					return;
				case 18:
					if (pl.config.worldSize == 100) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.worldSize--;
					updateSettings(u, slot);
					return;
				case 19:
					if (pl.config.borderEndSize == 5) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderEndSize--;
					updateSettings(u, slot);
					return;
				case 28:
					if (pl.config.borderTime == 0) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderTime--;
					updateSettings(u, slot, 1, 19);
					return;
				}
			} else if (click.equals(ClickType.SHIFT_LEFT)) {
				switch (slot) {
				case 0:
					pl.config.worldTime = pl.config.worldTime + 100 > 23999 ? pl.config.worldTime + 100 - 24000 : pl.config.worldTime + 100;
					pl.getServer().getWorlds().get(0).setTime(pl.config.worldTime);
					updateSettings(u, slot);
					return;
				case 1:
					if (pl.config.borderDelay + 60 > 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, slot);
						return;
					}
					pl.config.borderDelay += 60;
					updateSettings(u, slot);
					return;
				case 10:
					if (pl.config.borderStartSize + 100 > 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderStartSize += 100;
					updateSettings(u, slot);
					return;
				case 18:
					if (pl.config.worldSize + 100 > 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.worldSize += 100;
					updateSettings(u, slot);
					return;
				case 19:
					if (pl.config.borderEndSize + 100 > 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderEndSize += 100;
					updateSettings(u, slot);
					return;
				case 28:
					if (pl.config.borderTime + 60 > 100000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderTime += 60;
					updateSettings(u, slot, 1, 19);
					return;
				}
			} else if (click.equals(ClickType.SHIFT_RIGHT)) {
				switch (slot) {
				case 0:
					pl.config.worldTime = pl.config.worldTime - 100 < 0 ? 24000 + pl.config.worldTime - 100 : pl.config.worldTime - 100;
					pl.getServer().getWorlds().get(0).setTime(pl.config.worldTime);
					updateSettings(u, slot);
					return;
				case 1:
					if (pl.config.borderDelay - 60 < 0) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, slot);
						return;
					}
					pl.config.borderDelay -= 60;
					updateSettings(u, slot);
					return;
				case 10:
					if (pl.config.borderStartSize - 100 < 50) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderStartSize -= 100;
					updateSettings(u, slot);
					return;
				case 18:
					if (pl.config.worldSize - 100 < 100) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.worldSize -= 100;
					updateSettings(u, slot);
					return;
				case 19:
					if (pl.config.borderEndSize - 100 < 5) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderEndSize -= 100;
					updateSettings(u, slot);
					return;
				case 28:
					if (pl.config.borderTime - 60 < 0) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.borderTime -= 60;
					updateSettings(u, slot, 1, 19);
					return;
				}
			}
			return;
		case 2:
			if (click.equals(ClickType.LEFT)) {
				switch (slot) {
				case 0:
					pl.config.grDisabledElytra = !pl.config.grDisabledElytra;
					updateGamerules(u, slot, true);
					return;
				case 1:
					pl.config.grDaylight = !pl.config.grDaylight;
					updateGamerules(u, slot, false);
					return;
				case 2:
					pl.config.grEntityDrops = !pl.config.grEntityDrops;
					updateGamerules(u, slot, true);
					return;
				case 3:
					pl.config.grFireTick = !pl.config.grFireTick;
					updateGamerules(u, slot, true);
					return;
				case 4:
					pl.config.grLimitedCrafting = !pl.config.grLimitedCrafting;
					updateGamerules(u, slot, true);
					return;
				case 5:
					pl.config.grMobLoot = !pl.config.grMobLoot;
					updateGamerules(u, slot, true);
					return;
				case 6:
					pl.config.grMobSpawning = !pl.config.grMobSpawning;
					updateGamerules(u, slot, true);
					return;
				case 7:
					pl.config.grTileDrops = !pl.config.grTileDrops;
					updateGamerules(u, slot, true);
					return;
				case 8:
					pl.config.grWeather = !pl.config.grWeather;
					updateGamerules(u, slot, true);
					return;
				case 9:
					if (pl.config.grMaxCramming == 100) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.grMaxCramming++;
					updateGamerules(u, slot, true);
					return;
				case 10:
					pl.config.grMobGriefing = !pl.config.grMobGriefing;
					updateGamerules(u, slot, true);
					return;
				case 11:
					if (pl.config.grRandom == 10) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.grRandom++;
					updateGamerules(u, slot, true);
					return;
				case 12:
					pl.config.grDebugInfo = !pl.config.grDebugInfo;
					updateGamerules(u, slot, true);
					return;
				case 13:
					if (pl.config.grSpawnRadius == 10) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.grSpawnRadius++;
					updateGamerules(u, slot, true);
					return;
				case 14:
					pl.config.grSpectators = !pl.config.grSpectators;
					updateGamerules(u, slot, true);
					return;
				case 31:
					changeInv(u, 0);
					return;
				}
			} else if (click.equals(ClickType.RIGHT)) {
				switch (slot) {
				case 9:
					if (pl.config.grMaxCramming == 0) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.grMaxCramming--;
					updateGamerules(u, slot, true);
					return;
				case 11:
					if (pl.config.grRandom == 0) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.grRandom--;
					updateGamerules(u, slot, true);
					return;
				case 13:
					if (pl.config.grSpawnRadius == 0) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.grSpawnRadius--;
					updateGamerules(u, slot, true);
					return;
				}
			}
			return;
		case 3:
			if (click.equals(ClickType.LEFT)) {
				switch (slot) {
				case 1:
					pl.config.gameIsFriendly = !pl.config.gameIsFriendly;
					updateItems(3, slot);
					pl.gameManager.teams.updateOptions(true);
					u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
					return;
				case 2:
					pl.config.gameSeeFriendly = !pl.config.gameSeeFriendly;
					updateItems(3, slot);
					pl.gameManager.teams.updateOptions(true);
					u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
					return;
				case 3:
					pl.config.gameNameTag = pl.config.gameNameTag == 3 ? 0 : pl.config.gameNameTag + 1;
					updateItems(3, slot);
					pl.gameManager.teams.updateOptions(true);
					u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
					return;
				case 4:
					pl.config.gameCollision = pl.config.gameCollision == 3 ? 0 : pl.config.gameCollision + 1;
					updateItems(3, slot);
					pl.gameManager.teams.updateOptions(true);
					u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
					return;
				case 6:
					if (pl.config.gameMaxTeam == 20000) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.gameMaxTeam++;
					updateItems(3, slot);
					pl.gameManager.teams.updateOptions(false);
					u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
					return;
				case 8:
					changeInv(u, 0);
					return;
				}
			} else if (click.equals(ClickType.RIGHT)) {
				if (slot == 6) {
					if (pl.config.gameMaxTeam == 1) {
						u.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, .5f);
						return;
					}
					pl.config.gameMaxTeam--;
					updateItems(3, slot);
					pl.gameManager.teams.updateOptions(false);
					u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
					return;
				}
			}
			return;
		}
	}

	public void load() {
		itemStack = F.item(Material.CLOCK, "&cUHC 1.8 | Menu", "0|&7Edit the game settings.");
		createInv(0, "Menu", pl.config.gameInTeam ? 18 : 9, -1);
		createInv(1, "Game Settings", 45, 40);
		createInv(2, "Gamerule", 36, 31);
		if (pl.config.gameInTeam)
			createInv(3, "Team Settings", 9, 8);
	}

	public void openInv(UHCPlayer u) {
		if (playersHistory.containsKey(u.player.getName())) {
			u.player.openInventory(invStorage.get(playersHistory.get(u.player.getName())));
			u.setInvKey(0, playersHistory.get(u.player.getName()));
			return;
		}
		playersHistory.put(u.player.getName(), 0);
		u.player.openInventory(invStorage.get(0));
		u.setInvKey(0, 0);
	}

	// ---------------------------------------------------------------------------

	private void changeInv(UHCPlayer u, int invID) {
		u.player.openInventory(invStorage.get(invID));
		u.setInvKey(0, invID);
		playersHistory.put(u.player.getName(), invID);
		u.playSound(Sound.UI_BUTTON_CLICK, 1.2f);
	}

	private void updateGamerules(UHCPlayer u, int slot, boolean isWorld) {
		updateItems(2, slot);
		if (isWorld) {
			pl.getServer().getWorlds().forEach(a -> {
				if (!a.getName().equals("uhc_lobby"))
					pl.updateWorldGamerules(a);
			});
			pl.PLAYERS.values().forEach(a -> a.updateStatus(pl.config.grDebugInfo));
		}
		u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
	}

	private void updateSettings(UHCPlayer u, int... slots) {
		updateItems(1, slots);
		u.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f);
	}

	private void createInv(int invID, String name, int size, int returnSlot) {
		Inventory a = pl.getServer().createInventory(null, size, "UHC> " + name);
		invStorage.put(invID, a);
		switch (invID) {
		case 0:
			updateItems(invID, 3, 5, size == 18 ? 13 : -1);
			break;
		case 1:
			updateItems(invID, 0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 13, 15, 18, 19, 24, 28);
			break;
		case 2:
			updateItems(invID, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
			break;
		case 3:
			updateItems(invID, 1, 2, 3, 4, 6);
			break;
		}
		if (returnSlot != -1)
			a.setItem(returnSlot, F.item(materials[3], 
					"&c&lGo back", 
					"0|&7Back to the previous",
					"0|&7inventory."));
	}

	private void updateItems(int invID, int... slots) {
		Inventory inv = invStorage.get(invID);
		switch (invID) {
		case 0:
			for (int i : slots) {
				switch (i) {
				case 3:
					inv.setItem(i, F.item(materials[2], 
							"&6&lGame Settings", 
							"0|&7Edit the game settings?"));
					break;
				case 5:
					inv.setItem(i, F.item(materials[2], 
							"&6&lGamerules", 
							"0|&7Edit the worlds",
							"0|&7gamerules?"));
					break;
				case 13:
					inv.setItem(i, F.item(materials[2], 
							"&6&lTeam Settings", 
							"0|&7Edit the teams settings?"));
					break;
				}
			}
			break;
		case 1:
			for (int i : slots) {
				switch (i) {
				case 0:
					inv.setItem(i, F.item(materials[1], 
							"&6&lSun Time",
							"0|&8World Settings",
							"0|",
							"0|&7Set where the sun should be",
							"0|&7when the game is starting.",
							"2|" + F.asRealClock(pl.config.worldTime)));
					break;
				case 1:
					inv.setItem(i, F.item(materials[pl.config.borderDelay != 0 && pl.config.borderTime != 0 ? 1 : 0], 
							"&6&lStart Delay", 
							"0|&8World Border",
							"0|",
							"0|&7Set the delay when the",
							"0|&7border should be started to",
							"0|&7shrink.",
							"2|" + F.getTimeLeft(pl.config.borderDelay)));
					break;
				case 2:
					inv.setItem(i, F.item(materials[pl.config.markerMsg.length() != 0 && pl.config.markerDelay != 0 ? 1 : 0], 
							"&6&lTime Delay", 
							"0|&8Marker",
							"0|",
							"0|&7Set the delay when the",
							"0|&7marker should be announced.",
							(pl.config.markerDelay == 0 ? "1|" + F.isOn(false, true) : "2|" + F.getTimeLeft(pl.config.markerDelay * 60))));
					break;
				case 3:
					inv.setItem(i, F.item(materials[1], 
							"&6&lMax Disconnected Timeout",
							"0|&8Offline Kicker",
							"0|",
							"0|&7Set the delay for how",
							"0|&7long player can be offline.",
							"2|" + F.getTimeLeft(pl.config.kickerDelay * 60)));
					break;
				case 4:
					inv.setItem(i, F.item(materials[pl.config.freezeEnable ? 1 : 0], 
							"&6&lEnabled", 
							"0|&8Freeze Player",
							"0|",
							"0|&7Lock the player inside a",
							"0|&7worldborder.",
							"1|" + F.isOn(pl.config.freezeEnable, true)));
					break;
				case 5:
					inv.setItem(i, F.item(materials[pl.config.dmgEnable ? 1 : 0], 
							"&6&lEnabled", 
							"0|&8Damager Logger", 
							"0|",
							"0|&7Log every damage that player",
							"0|&7gets from the UHC.",
							"1|" + F.isOn(pl.config.dmgEnable, true)));
					break;
				case 6:
					inv.setItem(i, F.item(materials[pl.config.headEnable ? 1 : 0], 
							"&6&lEnabled", 
							"0|&8Golden Head", 
							"0|",
							"0|&7Enabled player head drop and",
							"0|&7able to craft golden head apple.",
							"1|" + F.isOn(pl.config.headEnable, true)));
					break;
				case 7:
					inv.setItem(i, F.item(materials[pl.config.gameOldCombat ? 1 : 0], 
							"&6&lEnabled", 
							"0|&8Old Combat Mode", 
							"0|",
							"0|&7Want to use the old",
							"0|&7Minecraft combat mode?",
							"0|",
							"0|&7&oSome of the items have been",
							"0|&7&ochange the damage value.",
							"1|" + F.isOn(pl.config.gameOldCombat, true)));
					break;
				case 9:
					inv.setItem(i, F.item(materials[1], 
							"&6&lDifficulty", 
							"0|&8World Settings",
							"0|",
							"0|&7Set the world difficulty.",
							"1|" + F.isDifficulty(pl.config.worldDifficulty, true)));
					break;
				case 10:
					inv.setItem(i, F.item(materials[1], 
							"&6&lStarting Position", 
							"0|&8World Border", 
							"0|",
							"0|&7Set the worldborder starting",
							"0|&7point.",
							"2|" + pl.config.borderStartSize));
					break;
				case 13:
					inv.setItem(i, F.item(materials[pl.config.freezeEnable ? 1 : 0], 
							"&6&lRadius Size", 
							"0|&8Freeze Player", 
							"0|",
							"0|&7Set the radius of the border.",
							"2|" + pl.config.freezeSize));
					break;
				case 15:
					inv.setItem(i, F.item(materials[pl.config.headEnable && pl.config.headDefault ? 1 : 0], 
							"&6&lDefault Head Apple", 
							"0|&8Golden Head", 
							"0|",
							"0|&7Enable to craft default head",
							"0|&7apple?",
							"1|" + F.isOn(pl.config.headDefault, true)));
					break;
				case 18:
					inv.setItem(i, F.item(materials[1], 
							"&6&lArena Size", 
							"0|&8World Settings", 
							"0|",
							"0|&7Here do you set where players",
							"0|&7can be spawning in?",
							"2|" + pl.config.worldSize));
					break;
				case 19:
					inv.setItem(i, F.item(materials[pl.config.borderTime != 0 ? 1 : 0], 
							"&6&lStop Position", 
							"0|&8World Border", 
							"0|",
							"0|&7Set the worldborder stopping",
							"0|&7point?",
							"2|" + pl.config.borderEndSize + ""));
					break;
				case 24:
					inv.setItem(i, F.item(materials[pl.config.headEnable && pl.config.headGolden ? 1 : 0], 
							"&6&lGolden Head Apple", 
							"0|&8Golden Head", 
							"0|",
							"0|&7Enable to craft golden head",
							"0|&7apple?",
							"1|" + F.isOn(pl.config.headGolden, true)));
					break;
				case 28:
					inv.setItem(i, F.item(materials[pl.config.borderTime != 0 ? 1 : 0], 
							"&6&lShrink Time", 
							"0|&8World Border", 
							"0|",
							"0|&7Set how long time to reach the",
							"0|&7worldborder stopping point?",
							"2|" + F.getTimeLeft(pl.config.borderTime)));
					break;
				}
			}
			return;
		case 2:
			for (int i : slots) {
				switch (i) {
				case 0:
					inv.setItem(i, F.item(materials[pl.config.grDisabledElytra ? 1 : 0], 
							"&6&lDisable Elytra Movement Check", 
							"0|&7Whether the server should skip",
							"0|&7checking player speed when the",
							"0|&7player is wearing elytra.",
							"1|" + F.isOn(pl.config.grDisabledElytra, true)));
					break;
				case 1:
					inv.setItem(i, F.item(materials[pl.config.grDaylight ? 1 : 0], 
							"&6&lDaylight Cycle", 
							"0|&7Whether the day-night cycle and",
							"0|&7moon phases progress.",
							"1|" + F.isOn(pl.config.grDaylight, true)));
					break;
				case 2:
					inv.setItem(i, F.item(materials[pl.config.grEntityDrops ? 1 : 0], 
							"&6&lEntity Drops", 
							"0|&7Whether entities that are not",
							"0|&7mobs should have drops.",
							"1|" + F.isOn(pl.config.grEntityDrops, true)));
					break;
				case 3:
					inv.setItem(i, F.item(materials[pl.config.grFireTick ? 1 : 0], 
							"&6&lFire Tick", 
							"0|&7Whether fire should spread and",
							"0|&7naturally extinguish.",
							"1|" + F.isOn(pl.config.grFireTick, true)));
					break;
				case 4:
					inv.setItem(i, F.item(materials[pl.config.grLimitedCrafting ? 1 : 0], 
							"&6&lLimited Crafting",
							"0|&7Whether players should only be",
							"0|&7able to craft recipes that they've",
							"0|&7unlocked first.",
							"1|" + F.isOn(pl.config.grLimitedCrafting, true)));
					break;
				case 5:
					inv.setItem(i, F.item(materials[pl.config.grMobLoot ? 1 : 0], 
							"&6&lMob Loot", 
							"0|&7Whether mobs should drop items.",
							"1|" + F.isOn(pl.config.grMobLoot, true)));
					break;
				case 6:
					inv.setItem(i, F.item(materials[pl.config.grMobSpawning ? 1 : 0], 
							"&6&lMob Spawning", 
							"0|&7Whether mobs should naturally",
							"0|&7spawn.",
							"1|" + F.isOn(pl.config.grMobSpawning, true)));
					break;
				case 7:
					inv.setItem(i, F.item(materials[pl.config.grTileDrops ? 1 : 0], 
							"&6&lTile Drops",
							"0|&7Whether blocks should have",
							"0|&7drops",
							"1|" + F.isOn(pl.config.grTileDrops, true)));
					break;
				case 8:
					inv.setItem(i, F.item(materials[pl.config.grWeather ? 1 : 0], 
							"&6&lWeather Cycle", 
							"0|&7Whether the weather will change.",
							"1|" + F.isOn(pl.config.grWeather, true)));
					break;
				case 9:
					inv.setItem(i, F.item(materials[pl.config.grMaxCramming != 0 ? 1 : 0], 
							"&6&lMax Entity Cramming", 
							"0|&7The maximum number of other",
							"0|&7pushable entities a mob or",
							"0|&7player can push, before",
							"0|&7taking damage.",
							pl.config.grMaxCramming == 0 ? "1|" + F.isOn(false, true) : "2|" + pl.config.grMaxCramming));
					break;
				case 10:
					inv.setItem(i, F.item(materials[pl.config.grMobGriefing ? 1 : 0], 
							"&6&lMob Griefing",
							"0|&7Whether mobs can destroy",
							"0|&7blocks and pick up items.",
							"1|" + F.isOn(pl.config.grMobGriefing, true)));
					break;
				case 11:
					inv.setItem(i, F.item(materials[pl.config.grRandom != 0 ? 1 : 0], 
							"&6&lRandom Tick Speed", 
							"0|&7How often a random block tick",
							"0|&7occurs per chunk section per",
							"0|&7game tick.",
							pl.config.grRandom == 0 ? "1|" + F.isOn(false, true) : "2|" + pl.config.grRandom));
					break;
				case 12:
					inv.setItem(i, F.item(materials[pl.config.grDebugInfo ? 1 : 0], 
							"&6&lReduced Debug Info", 
							"0|&7Whether the debug screen shows",
							"0|&7all or reduced information and",
							"0|&7whether the effects of entity",
							"0|&7hitboxes and chunk boundaries",
							"0|&7are shown.",
							"1|" + F.isOn(pl.config.grDebugInfo, true)));
					break;
				case 13:
					inv.setItem(i, F.item(materials[pl.config.grSpawnRadius != 0 ? 1 : 0], 
							"&6&lSpawn Radius", 
							"0|&7The number of blocks outward from",
							"0|&7the world spawn coordinates that",
							"0|&7a player will spawn in when first",
							"0|&7joining a server or when dying",
							"0|&7without a spawnpoint.",
							pl.config.grSpawnRadius == 0 ? "1|" + F.isOn(false, true) : "2|" + pl.config.grSpawnRadius));
					break;
				case 14:
					inv.setItem(i, F.item(materials[pl.config.grSpectators ? 1 : 0], 
							"&6&lSpectators Generate Chunks", 
							"0|&7Whether players in spectator",
							"0|&7mode can generate chunks.",
							"1|" + F.isOn(pl.config.grSpectators, true)));
					break;
				}
			}
			return;
		case 3:
			for (int i : slots) {
				switch (i) {
				case 1:
					inv.setItem(i, F.item(materials[pl.config.gameIsFriendly ? 1 : 0], 
							"&6&lFriendly Fire", 
							"0|&7If active, has no impact",
							"0|&7on PvP mechanics.",
							"1|" + F.isOn(pl.config.gameIsFriendly, true)));
					break;
				case 2:
					inv.setItem(i, F.item(materials[pl.config.gameSeeFriendly ? 1 : 0], 
							"&6&lSee Friendly Invisibles", 
							"0|&7If active, players will see",
							"0|&7invisible teammates.",
							"1|" + F.isOn(pl.config.gameSeeFriendly, true)));
					break;
				case 3:
					inv.setItem(i, F.item(materials[pl.config.gameNameTag != 1 ? 1 : 0], 
							"&6&lName Tag Visibillity", 
							"0|&7Controls the visibility of",
							"0|&7nametags for entities on the",
							"0|&7given team.",
							"1|" + F.isTeamsOption(pl.config.gameNameTag, false, true)));
					break;
				case 4:
					inv.setItem(i, F.item(materials[pl.config.gameCollision != 1 ? 1 : 0], 
							"&6&lCollision Rule", 
							"0|&7Controls the way the",
							"0|&7entities collide.",
							"1|" + F.isTeamsOption(pl.config.gameCollision, true, true)));
					break;
				case 6:
					inv.setItem(i, F.item(materials[1], 
							"&6&lMax Team Players", 
							"0|&7Set how many players can",
							"0|&7join the team?",
							"2|" + pl.config.gameMaxTeam));
					break;
				}
			}
			return;
		}
	}
}
