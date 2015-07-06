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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutServerDifficulty;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_8_R3.WorldBorder;

import org.apache.commons.io.FileUtils;
import org.bukkit.Achievement;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Function {

	/**
	 * ~ Load Variable From Config To Memory ~
	 * 
	 * @param pl = Main Plugin
	 * @param c = FileConfiguration <i>(Config.yml file)</i>
	 */
	public static void loadConfig(Main pl, FileConfiguration c) {
		if (c.isBoolean("Plugin")) {
			pl.plMode = c.getBoolean("Plugin");
		} else {
			pl.getLogger().warning("Config> Cold not load 'Plugin'");
		}
		if (c.isConfigurationSection("ServerMode")) {
			if (c.isBoolean("ServerMode.Enabled")) {
				pl.sm = c.getBoolean("ServerMode.Enabled");
			} else {
				pl.getLogger().warning("Config> Cold not load 'ServerMode.Enabled'");
			}
			if (c.isInt("ServerMode.ServerID")) {
				if (integerData(c.getInt("ServerMode.ServerID"), 1, 9999)) {
					pl.smSID = c.getInt("ServerMode.ServerID");
				} else {
					pl.getLogger().warning("Config> Error at 'ServerMode.ServerID', Only be (1 - 9999)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'ServerMode.ServerID'");
			}
			if (c.isInt("ServerMode.MinPlayerToStart")) {
				if (integerData(c.getInt("ServerMode.MinPlayerToStart"), 2, 100)) {
					pl.smMps = c.getInt("ServerMode.MinPlayerToStart");
				} else {
					pl.getLogger().warning("Config> Error at 'ServerMode.MinPlayerToStart', Only be (2 - 100)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'ServerMode.MinPlayerToStart'");
			}
			if (c.isInt("ServerMode.MinTeamToStart")) {
				if (integerData(c.getInt("ServerMode.MinTeamToStart"), 2, 16)) {
					pl.smMts = c.getInt("ServerMode.MinTeamToStart");
				} else {
					pl.getLogger().warning("Config> Error at 'ServerMode.MinTeamToStart', Only be (2 - 16)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'ServerMode.MinTeamToStart'");
			}
			if (c.isInt("ServerMode.Countdown")) {
				if (integerData(c.getInt("ServerMode.Countdown"), 10, 20000)) {
					pl.smCn = c.getInt("ServerMode.Countdown");
				} else {
					pl.getLogger().warning("Config> Error at 'ServerMode.Countdown', Only be (10 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'ServerMode.Countdown'");
			}
			if (c.isString("ServerMode.Motd")) {
				pl.smMotd = minecraftColor(c.getString("ServerMode.Motd"));
				if (pl.smMotd.isEmpty()) {
					pl.getLogger().info("Config> Server Motd => Is empty, show as default.");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'ServerMode.Motd'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'ServerMode'");
		}
		if (c.isConfigurationSection("TabList")) {
			if (c.isBoolean("TabList.Enabled")) {
				pl.tlhf = c.getBoolean("TabList.Enabled");
			} else {
				pl.getLogger().warning("Config> Cold not load 'TabList.Enabled'");
			}
			if (c.isString("TabList.Header")) {
				pl.tlh = minecraftColor(c.getString("TabList.Header"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'TabList.Header'");
			}
			if (c.isString("TabList.Footer")) {
				pl.tlf = minecraftColor(c.getString("TabList.Footer"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'TabList.Footer'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'TabList'");
		}
		if (c.isConfigurationSection("ChunkLoader")) {
			if (c.isBoolean("ChunkLoader.ShowHiddenDetail")) {
				pl.clShd = c.getBoolean("ChunkLoader.ShowHiddenDetail");
			} else {
				pl.getLogger().warning("Config> Cold not load 'ChunkLoader.ShowHiddenDetail'");
			}
			if (c.isInt("ChunkLoader.DelayTick")) {
				if (integerData(c.getInt("ChunkLoader.DelayTick"), 0, 20000)) {
					pl.clTick = c.getInt("ChunkLoader.DelayTick");
				} else {
					pl.getLogger().warning("Config> Error at 'ChunkLoader.DelayTick', Only be (0 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'ChunkLoader.DelayTick'");
			}
			if (c.isInt("ChunkLoader.Task")) {
				if (integerData(c.getInt("ChunkLoader.Task"), 0, 20000)) {
					pl.clTask = c.getInt("ChunkLoader.Task");
				} else {
					pl.getLogger().warning("Config> Error at 'ChunkLoader.Task', Only be (0 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'ChunkLoader.Task'");
			}
			if (c.isInt("ChunkLoader.ArenaBorder")) {
				if (integerData(c.getInt("ChunkLoader.ArenaBorder"), 0, 20000)) {
					pl.clBorder = c.getInt("ChunkLoader.ArenaBorder");
				} else {
					pl.getLogger().warning("Config> Error at 'ChunkLoader.ArenaBorder', Only be (0 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'ChunkLoader.ArenaBorder'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'ChunkLoader'");
		}
		if (pl.plMode) {
			if (c.isConfigurationSection("Book")) {
				if (c.isBoolean("Book.Enabled")) {
					pl.uhcBook = c.getBoolean("Book.Enabled");
				} else {
					pl.getLogger().warning("Config> Cold not load 'Book.Enabled'");
				}
				if (c.isBoolean("Book.ShowSettings")) {
					pl.uhcBookConf = c.getBoolean("Book.ShowSettings");
				} else {
					pl.getLogger().warning("Config> Cold not load 'Book.ShowSettings'");
				}
				if (c.isInt("Book.InventorySlot")) {
					if (integerData(c.getInt("Book.InventorySlot"), 0, 9)) {
						pl.ubInvSlot = c.getInt("Book.InventorySlot");
					} else {
						pl.getLogger().warning("Config> Error at 'Book.InventorySlot', Only be (0 - 9)");
					}
				} else {
					pl.getLogger().warning("Config> Cold not load 'Book.InventorySlot'");
				}
				if (c.isString("Book.Name")) {
					if (!c.getString("Book.Name").isEmpty()) {
						pl.ubName = minecraftColor(c.getString("Book.Name"));
					} else {
						pl.getLogger().warning("Config> 'Book.Name' can not be empty, ignoring it");
					}
				} else {
					pl.getLogger().warning("Config> Cold not load 'Book.Name'");
				}
				if (c.isList("Book.Lord")) {
					if (!c.getStringList("Book.Lord").isEmpty()) {
						for (String l : c.getStringList("Book.Lord")) {
							pl.ubLord.add(minecraftColor(l));
						}
						if (pl.uhcBookConf) {
							pl.ubLord.add("§6- §7Ultra Harcore Settings");
						}
					} else {
						if (pl.uhcBookConf) {
							pl.ubLord.add("");
							pl.ubLord.add("§6- §7Ultra Harcore Settings");
						}
						pl.getLogger().info("Config> 'Book.Lord' is empty.");
					}
				} else {
					pl.ubLord.add("");
					pl.ubLord.add("§6- §7Info");
					pl.ubLord.add("§6- §7Rules");
					if (pl.uhcBookConf) {
						pl.ubLord.add("§6- §7Ultra Harcore Settings");
					}
					pl.getLogger().warning("Config> Cold not load 'Book.Lord'");
				}
				if (c.isList("Book.Pages")) {
					if (!c.getStringList("Book.Pages").isEmpty()) {
						for (String l : c.getStringList("Book.Pages")) {
							pl.ubPages.add(minecraftColor(l.replace("$[N]", "\n")));
						}
					} else {
						pl.getLogger().info("Config> 'Book.Pages' is empty.");
					}
				} else {
					pl.ubPages.add("Welcome to §4§lUHC§0.\n\nThis game you can only regenerate health by\n §8- §1Golden Apple.§r\n §8- §1Potions.§r\n\nI wish you §5Good Luck§r\nand may the best player / team win.");
					pl.ubPages.add("§l§n    UHC - Rules     §r\n\n§11.§r Branch Mining\n§8 You can not branch\n mining but if you\n hear a sound,\n you can dig to it.§r\n\n§12. Staircases§r\n§8 You can only dig\n staircases if you\n want to find a cave.§r");
					pl.getLogger().warning("Config> Cold not load 'Book.Pages'");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'Book'");
			}
		}
		if (c.isConfigurationSection("WorldOptions")) {
			if (c.isInt("WorldOptions.Difficulty")) {
				if (integerData(c.getInt("WorldOptions.Difficulty"), 1, 3)) {
					pl.woDiff = c.getInt("WorldOptions.Difficulty");
				} else {
					pl.getLogger().warning("Config> Error at 'WorldOptions.Difficulty', Only be (1 - 3)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'WorldOptions.Difficulty'");
			}
			if (c.isInt("WorldOptions.ArenaSize")) {
				if (integerData(c.getInt("WorldOptions.ArenaSize"), 100, 20000)) {
					pl.woArenaSize = c.getInt("WorldOptions.ArenaSize");
				} else {
					pl.getLogger().warning("Config> Error at 'WorldOptions.ArenaSize', Only be (100 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'WorldOptions.ArenaSize'");
			}
			if (c.isInt("WorldOptions.SunTime")) {
				if (integerData(c.getInt("WorldOptions.SunTime"), Integer.MIN_VALUE, Integer.MAX_VALUE)) {
					pl.woSunTime = c.getInt("WorldOptions.SunTime");
				} else {
					pl.getLogger().warning("Config> Error at 'WorldOptions.SunTime', Only be (" + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ")");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'WorldOptions.SunTime'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'WorldOptions'");
		}
		if (c.isConfigurationSection("Gamerules")) {
			if (c.isBoolean("Gamerules.doDaylightCycle")) {
				pl.grList.add("doDaylightCycle|" + c.getBoolean("Gamerules.doDaylightCycle"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.doDaylightCycle'");
			}
			if (c.isBoolean("Gamerules.doEntityDrops")) {
				pl.grList.add("doEntityDrops|" + c.getBoolean("Gamerules.doEntityDrops"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.doEntityDrops'");
			}
			if (c.isBoolean("Gamerules.doFireTick")) {
				pl.grList.add("doFireTick|" + c.getBoolean("Gamerules.doFireTick"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.doFireTick'");
			}
			if (c.isBoolean("Gamerules.doMobLoot")) {
				pl.grList.add("doMobLoot|" + c.getBoolean("Gamerules.doMobLoot"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.doMobLoot'");
			}
			if (c.isBoolean("Gamerules.doMobSpawning")) {
				pl.grList.add("doMobSpawning|" + c.getBoolean("Gamerules.doMobSpawning"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.doMobSpawning'");
			}
			if (c.isBoolean("Gamerules.doTileDrops")) {
				pl.grList.add("doTileDrops|" + c.getBoolean("Gamerules.doTileDrops"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.doTileDrops'");
			}
			if (c.isBoolean("Gamerules.mobGriefing")) {
				pl.grList.add("mobGriefing|" + c.getBoolean("Gamerules.mobGriefing"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.mobGriefing'");
			}
			if (c.isInt("Gamerules.randomTickSpeed")) {
				if (integerData(c.getInt("Gamerules.randomTickSpeed"), 0, 10)) {
					pl.grList.add("randomTickSpeed|" + c.getInt("Gamerules.randomTickSpeed"));
				} else {
					pl.grList.add("randomTickSpeed|3");
					pl.getLogger().warning("Config> Error at 'Gamerules.randomTickSpeed', Only be (0 - 10)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.randomTickSpeed'");
			}
			if (c.isBoolean("Gamerules.reducedDebugInfo")) {
				pl.grList.add("reducedDebugInfo|" + c.getBoolean("Gamerules.reducedDebugInfo"));
			} else {
				pl.getLogger().warning("Config> Cold not load 'Gamerules.reducedDebugInfo'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'Gamerules'");
		}
		if (c.isConfigurationSection("Worldborder")) {
			if (c.isInt("Worldborder.StartPos")) {
				if (integerData(c.getInt("Worldborder.StartPos"), 100, 20000)) {
					pl.wbStartPos = c.getInt("Worldborder.StartPos");
				} else {
					pl.getLogger().warning("Config> Error at 'Worldborder.StartPos', Only be (100 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'Worldborder.StartPos'");
			}
			if (c.isInt("Worldborder.EndPos")) {
				if (integerData(c.getInt("Worldborder.EndPos"), 10, 20000)) {
					pl.wbEndPos = c.getInt("Worldborder.EndPos");
				} else {
					pl.getLogger().warning("Config> Error at 'Worldborder.EndPos', Only be (10 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'Worldborder.EndPos'");
			}
			if (c.isInt("Worldborder.Time")) {
				if (integerData(c.getInt("Worldborder.Time"), 0, 100000)) {
					pl.wbTime = c.getInt("Worldborder.Time");
					if (pl.wbTime == 0) {
						pl.getLogger().info("Config> Worldborder shrinks time => Disabled");
					}
				} else {
					pl.getLogger().warning("Config> Error at 'Worldborder.Time', Only be (0 - 100000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'Worldborder.Time'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'Worldborder'");
		}
		if (c.isConfigurationSection("FreezePlayer")) {
			if (c.isBoolean("FreezePlayer.Enabled")) {
				pl.fzp = c.getBoolean("FreezePlayer.Enabled");
			} else {
				pl.getLogger().warning("Config> Cold not load 'FreezePlayer.Enabled'");
			}
			if (c.isInt("FreezePlayer.Size")) {
				if (integerData(c.getInt("FreezePlayer.Size"), 5, 15)) {
					pl.fzSize = c.getInt("FreezePlayer.Size");
				} else {
					pl.getLogger().warning("Config> Error at 'FreezePlayer.Size', Only be (5 - 15)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'FreezePlayer.Size'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'FreezePlayer'");
		}
		if (c.isConfigurationSection("Marker")) {
			if (c.isString("Marker.Message")) {
				pl.moMarkMeg = minecraftColor(c.getString("Marker.Message"));
				if (pl.moMarkMeg.isEmpty()) {
					pl.getLogger().info("Config> Marker message => Disabled");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'Marker.Message'");
			}
			if (c.isInt("Marker.TimeDelay")) {
				if (integerData(c.getInt("Marker.TimeDelay"), 0, 20000)) {
					pl.moMarkTime = c.getInt("Marker.TimeDelay");
					if (pl.moMarkTime == 0) {
						pl.getLogger().info("Config> Marker time => Disabled");
					}
				} else {
					pl.getLogger().warning("Config> Error at 'Marker.TimeDelay', Only be (0 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'Marker.TimeDelay'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'Marker'");
		}
		if (c.isConfigurationSection("GameMode")) {
			if (c.isBoolean("GameMode.TeamMode")) {
				pl.tmMode = c.getBoolean("GameMode.TeamMode");
			} else {
				pl.getLogger().warning("Config> Cold not load 'GameMode.TeamMode'");
			}
			if (c.isInt("GameMode.MaxTeamPlayer")) {
				if (integerData(c.getInt("GameMode.MaxTeamPlayer"), 1, 20000)) {
					pl.tmMaxPlayer = c.getInt("GameMode.MaxTeamPlayer");
				} else {
					pl.getLogger().warning("Config> Error at 'GameMode.MaxTeamPlayer', Only be (1 - 20000)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'GameMode.MaxTeamPlayer'");
			}
			if (c.isInt("GameMode.SelectTeamInvSlot")) {
				if (integerData(c.getInt("GameMode.SelectTeamInvSlot"), 0, 9)) {
					pl.tmSelTmInvSlot = c.getInt("GameMode.SelectTeamInvSlot");
				} else {
					pl.getLogger().warning("Config> Error at 'GameMode.SelectTeamInvSlot', Only be (0 - 9)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'GameMode.SelectTeamInvSlot'");
			}
			if (c.isConfigurationSection("GameMode.Options")) {
				if (c.isBoolean("GameMode.Options.FriendlyFire")) {
					pl.tmrFF = c.getBoolean("GameMode.Options.FriendlyFire");
				} else {
					pl.getLogger().warning("Config> Cold not load 'GameMode.Options.FriendlyFire'");
				}
				if (c.isBoolean("GameMode.Options.SeeFriendlyInvisibles")) {
					pl.tmrSFI = c.getBoolean("GameMode.Options.SeeFriendlyInvisibles");
				} else {
					pl.getLogger().warning("Config> Cold not load 'GameMode.Options.SeeFriendlyInvisibles'");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'GameMode.Options'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'GameMode'");
		}
		if (c.isBoolean("DamagerLogger")) {
			pl.dmgLog = c.getBoolean("DamagerLogger");
		} else {
			pl.getLogger().warning("Config> Cold not load 'DamagerLogger'");
		}
		if (c.isConfigurationSection("OfflineKicker")) {
			if (c.isInt("OfflineKicker.Timeout")) {
				if (integerData(c.getInt("OfflineKicker.Timeout"), 1, 20)) {
					pl.okMaxTime = c.getInt("OfflineKicker.Timeout");
				} else {
					pl.getLogger().warning("Config> Error at 'OfflineKicker.Timeout', Only be (1 - 20)");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'OfflineKicker.Timeout'");
			}
			if (c.isString("OfflineKicker.Message")) {
				if (!c.getString("OfflineKicker.Message").isEmpty()) {
					pl.okMsg = minecraftColor(c.getString("OfflineKicker.Message"));
				} else {
					pl.getLogger().warning("Config> 'OfflineKicker.Message' can not be empty, ignoring it");
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'OfflineKicker.Message'");
			}
		} else {
			pl.getLogger().warning("Config> Cold not load 'OfflineKicker'");
		}
		if (pl.plMode) {
			if (c.isConfigurationSection("GlobalChat")) {
				if (!pl.tmMode) {
					if (c.isString("GlobalChat.Default")) {
						pl.chatD = minecraftColor(c.getString("GlobalChat.Default"));
						if (pl.chatD.isEmpty()) {
							pl.getLogger().info("Config> Global chat is disabled.");
						}
					} else {
						pl.getLogger().warning("Config> Cold not load 'GlobalChat.Default'");
					}
				}
				if (c.isString("GlobalChat.Spectator")) {
					pl.chatS = minecraftColor(c.getString("GlobalChat.Spectator"));
					if (pl.chatS.isEmpty()) {
						pl.getLogger().info("Config> Global spectator chat is disabled.");
					}
				} else {
					pl.getLogger().warning("Config> Cold not load 'GlobalChat.Spectator'");
				}
				if (pl.tmMode) {
					if (c.isConfigurationSection("GlobalChat.Teams")) {
						if (c.isString("GlobalChat.Teams.Default")) {
							pl.chatTD = minecraftColor(c.getString("GlobalChat.Teams.Default"));
							if (pl.chatTD.isEmpty()) {
								pl.getLogger().info("Config> Global chat is disabled.");
							}
						} else {
							pl.getLogger().warning("Config> Cold not load 'GlobalChat.Teams.Default'");
						}
						if (c.isString("GlobalChat.Teams.Team")) {
							pl.chatTT = minecraftColor(c.getString("GlobalChat.Teams.Team"));
							if (pl.chatTT.isEmpty()) {
								pl.getLogger().info("Config> Global chat for team player is disabled.");
							}
						} else {
							pl.getLogger().warning("Config> Cold not load 'GlobalChat.Teams.Team'");
						}
						if (c.isString("GlobalChat.Teams.PrivateChat")) {
							pl.chatTPC = minecraftColor(c.getString("GlobalChat.Teams.PrivateChat"));
							if (pl.chatTPC.isEmpty()) {
								pl.getLogger().info("Config> Private team chat is disabled.");
							}
						} else {
							pl.getLogger().warning("Config> Cold not load 'GlobalChat.Teams.PrivateChat'");
						}
					} else {
						pl.getLogger().warning("Config> Cold not load 'GlobalChat.Teams'");
					}
				}
			} else {
				pl.getLogger().warning("Config> Cold not load 'GlobalChat'");
			}
		}
	}

	/**
	 * ~ Action Bar Message [1.8] ~
	 * <p>
	 * Sending a action bar message to player.
	 * </p>
	 * 
	 * @param p = Player
	 * @param msg = String <i>(Message)</i>
	 */
	public static void sendActionBarPlayerMessages(Player p, String msg) {
		CraftPlayer cp = (CraftPlayer) p;
		PacketPlayOutChat ppoc = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + msg + "\"}"), (byte) 2);
		cp.getHandle().playerConnection.sendPacket(ppoc);
	}

	/**
	 * ~ Title & Subtitle [1.8]~
	 * <p>
	 * Sending a title and subtitle to player.
	 * </p>
	 * 
	 * @param p = Player
	 * @param msg = String <i>(Message)</i>
	 * @param t = int <i>(Stay delay)</i>
	 * @param f = int <i>(Fade delay)</i>
	 */
	public static void sendTitlePlayerMessages(Player p, String msg, int td, int fd) {
		CraftPlayer cp = (CraftPlayer) p;
		IChatBaseComponent t = IChatBaseComponent.ChatSerializer.a("{\"text\": \" \"}");
		IChatBaseComponent s = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + msg + "\"}");
		cp.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TIMES, null, 0, td, fd));
		cp.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, s));
		cp.getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, t));
	}

	/**
	 * ~ Update All Player Tablist Header & Footer [1.8] ~
	 * 
	 * @param op = Collection<? extends Player> <i>Online Players</i>
	 * @param h = String <i>(Header)</i>
	 * @param f = String <i>(Footer)</i>
	 */
	public static void updateTabListHeaderFooterAllPlayers(Collection<? extends Player> op, String h, String f) {
		for (Player p : op) {
			updateTabListHeaderFooter(p, h, f);
		}
	}

	/**
	 * ~ Update Tablist Header & Footer To Player [1.8] ~
	 * 
	 * @param p = Player
	 * @param h = String <i>(Header)</i>
	 * @param f = String <i>(Footer)</i>
	 */
	public static void updateTabListHeaderFooter(Player p, String h, String f) {
		try {
			CraftPlayer cp = (CraftPlayer) p;
			PacketPlayOutPlayerListHeaderFooter d = new PacketPlayOutPlayerListHeaderFooter();
			Field a = d.getClass().getDeclaredField("a");
			Field b = d.getClass().getDeclaredField("b");
			a.setAccessible(true);
			b.setAccessible(true);
			if (h.isEmpty()) {
				a.set(d, IChatBaseComponent.ChatSerializer.a("{translate: ''}"));
			} else {
				a.set(d, IChatBaseComponent.ChatSerializer.a("{'text': '" + h + "'}"));
			}
			if (f.isEmpty()) {
				b.set(d, IChatBaseComponent.ChatSerializer.a("{translate: ''}"));
			} else {
				b.set(d, IChatBaseComponent.ChatSerializer.a("{'text': '" + f + "'}"));
			}
			cp.getHandle().playerConnection.sendPacket(d);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			p.getServer().getLogger().warning("Error at " + e.getMessage().toString());
		}
	}

	/**
	 * ~ Update Difficulty On Client Side ~
	 * <p>
	 * Update difficulty on client side to selected player.
	 * </p>
	 * 
	 * @param p = Player
	 * @param df = EnumDifficulty <i>Difficulty</i>
	 */
	public static void updateDifficultyLvlOnClient(Player p, EnumDifficulty df) {
		CraftPlayer cp = (CraftPlayer) p;
		cp.getHandle().playerConnection.sendPacket(new PacketPlayOutServerDifficulty(df, true));
	}

	/**
	 * ~ Custome WorldBorder To Player [1.8] ~
	 * <p>
	 * Set custome worldborder to selected player.
	 * </p>
	 * 
	 * @param p = Player
	 * @param l = Location
	 * @param size = int <i>(Worldborder size)</i>
	 */
	public static void customWorldBorder(Player p, Location l, int size) {
		CraftPlayer cp = (CraftPlayer) p;
		WorldBorder wb = new WorldBorder();
		wb.setCenter(l.getBlockX(), l.getZ());
		cp.getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, EnumWorldBorderAction.SET_CENTER));
		wb.setSize(size);
		cp.getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, EnumWorldBorderAction.SET_SIZE));
	}

	/**
	 * ~ Ultra Hardcore 1.8 | Setup ~
	 * <p>
	 * Setting up for:
	 * <li>Scoreboard.</li>
	 * <li>Teams- (If Selected)</li>
	 * <li>Selected Team Inventory- (If Selected)</li>
	 * <li>Ultra Hardcore Book- (If Selected)</li>
	 * <li>World Option.</li>
	 * </p>
	 * 
	 * @param pl = Main Plugin
	 */
	public static void uhcSetup(Main pl) {
		Gui.onLoad(pl);
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		if (sb.getObjective("hp") == null) {
			Objective hp = sb.registerNewObjective("hp", "health");
			hp.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}
		if (pl.tmMode) {
			String[] tsl = { "Black|0", "Dark_Blue|1", "Dark_Green|2", "Dark_Aqua|3", "Dark_Red|4", "Dark_Purple|5", "Gold|6", "Gray|7", "Dark_Gray|8", "Blue|9", "Green|a", "Aqua|b", "Red|c", "Light_Purple|d", "Yellow|e", "White|f" };
			pl.invStore.put("selectteam", pl.getServer().createInventory(null, 18, "          - Select Team -"));
			for (String team : tsl) {
				String[] tm = team.split("\\|");
				if (sb.getTeam(tm[0]) == null) {
					Team t = sb.registerNewTeam(tm[0]);
					t.setPrefix("§" + tm[1]);
					t.setSuffix("§r");
					t.setAllowFriendlyFire(pl.tmrFF);
					t.setCanSeeFriendlyInvisibles(pl.tmrSFI);
					ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName("§" + tm[1] + "§l" + tm[0].replace("_", " "));
					im.setLore(Arrays.asList("", "§8Max " + pl.tmMaxPlayer + " Player" + (pl.tmMaxPlayer == 1 ? "" : "s")));
					is.setItemMeta(im);
					pl.invStore.get("selectteam").addItem(is);
				}
			}
			ItemStack is = new ItemStack(Material.BARRIER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§cRemove me from my team");
			is.setItemMeta(im);
			pl.invStore.get("selectteam").setItem(17, is);
			HashMap<ItemStack, Integer> item = new HashMap<ItemStack, Integer>();
			is = new ItemStack(Material.PAPER);
			im = is.getItemMeta();
			im.setDisplayName("§eSelect Team");
			is.setItemMeta(im);
			item.put(is, pl.tmSelTmInvSlot);
			pl.itemStore.put("selectteam", item);
		}
		if (pl.uhcBook) {
			HashMap<ItemStack, Integer> item = new HashMap<ItemStack, Integer>();
			ItemStack b = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bm = (BookMeta) b.getItemMeta();
			bm.setAuthor(pl.getDescription().getName());
			bm.setTitle(pl.ubName);
			bm.setDisplayName(bm.getTitle());
			if (!pl.ubLord.isEmpty()) {
				bm.setLore(pl.ubLord);
			}
			if (!pl.ubPages.isEmpty()) {
				for (String pg : pl.ubPages) {
					bm.addPage(pg);
				}
			}
			if (pl.uhcBookConf) {
				StringBuilder str = new StringBuilder();
				str.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				str.append("§1World:§r\n");
				str.append("  §8Difficulty: " + (pl.woDiff == 1 ? "§2Easy" : (pl.woDiff == 2 ? "§6Normal" : "§4Hard")) + "§r\n");
				str.append("  §8Arena size:§4 " + pl.woArenaSize + "§r\n");
				str.append("  §8Sun time:§4 " + pl.woSunTime + "§r\n \n");
				str.append("§1World Border:§r\n");
				str.append("  §8Start:§4 " + (pl.wbStartPos / 2) + "§r\n");
				if (pl.wbTime != 0) {
					str.append("  §8Stop:§4 " + (pl.wbEndPos / 2) + "§r\n");
					str.append("  §8Shrink:§4 " + getTimeFormat(pl.wbTime + "000") + "§r\n");
				}
				bm.addPage(str.toString());
				str = new StringBuilder();
				str.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				str.append("§1Marker:§r\n");
				str.append("  §8Time delay:§4 " + (pl.moMarkTime != 0 ? pl.moMarkTime + " min" : "Off") + "§r\n");
				if (pl.moMarkTime != 0) {
					str.append("  §8Message: " + (pl.moMarkMeg.isEmpty() ? "§4Off" : "§2On") + "§r\n");
				}
				str.append(" \n§1Disconnected Players:§r\n");
				str.append("  §8Max timeout: §4" + pl.okMaxTime + " min§r\n");
				if (pl.fzp) {
					str.append(" \n§1Freeze Players:§r\n");
					str.append("  §8Radius size: §4" + pl.fzSize + "§r\n");
				}
				bm.addPage(str.toString());
				str = new StringBuilder();
				str.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				str.append("§1Gamerule:§r\n");
				str.append("  §8Natural Regen: §4Off§r\n");
				if (pl.grList.size() != 0) {
					for (String grs : pl.grList) {
						String[] gr = grs.split("\\|");
						if (gr[1].toString().equalsIgnoreCase("true") || gr[1].toString().equalsIgnoreCase("false")) {
							if (gameruleReplace(gr[0]).equalsIgnoreCase("Eternal day")) {
								str.append("  §8" + gameruleReplace(gr[0]) + ": " + (gr[1].toString().equalsIgnoreCase("true") ? "§4Off" : "§2On") + "§r\n");
							} else {
								str.append("  §8" + gameruleReplace(gr[0]) + ": " + (gr[1].toString().equalsIgnoreCase("true") ? "§2On" : "§4Off") + "§r\n");
							}
						} else {
							str.append("  §8" + gameruleReplace(gr[0]) + ":§4 " + gr[1] + "§r\n");
						}
					}
				}
				bm.addPage(str.toString());
				str = new StringBuilder();
				str.append("§n    §l§nUHC SETTINGS§r§n    §r\n \n");
				if (pl.tmMode) {
					str.append("§1Teams:§r\n");
					str.append("  §8Max player:§4 " + pl.tmMaxPlayer + "§r\n");
					str.append("  §8Friendly fire: " + (pl.tmrFF ? "§2On" : "§4Off") + "§r\n");
					str.append("  §8See friendly invi: " + (pl.tmrSFI ? "§2On" : "§4Off") + "§r\n \n");
				}
				str.append("§1Other:§r\n");
				str.append("  §8Damager Logger: " + (pl.dmgLog ? "§2On" : "§4Off") + "§r\n");
				bm.addPage(str.toString());
			}
			b.setItemMeta(bm);
			item.put(b, pl.ubInvSlot);
			pl.itemStore.put("Book", item);
		}
		for (World w : pl.getServer().getWorlds()) {
			w.setDifficulty(Difficulty.PEACEFUL);
			w.setPVP(false);
			w.setTime(pl.woSunTime);
			if (pl.grList != null) {
				if (pl.grList.size() != 0) {
					for (String gamerule : pl.grList) {
						String[] gr = gamerule.split("\\|");
						w.setGameRuleValue(gr[0], gr[1]);
					}
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
					w.setGameRuleValue("doDaylightCycle", "false");
					w.setGameRuleValue("naturalRegeneration", "true");
					w.setGameRuleValue("keepInventory", "true");
				}
			}
		}
		clearAllPlayer(pl);
		allPlayerHubItem(pl);
		pl.gmStat = EnumGame.WAITHING;
	}

	/**
	 * ~ Ultra Hardcore 1.8 | Remove ~
	 * <p>
	 * Remove Ultra Hardcore Game Settings
	 * </p>
	 * 
	 * @param pl = Main Plugin
	 */
	public static void uhcRemove(Main pl) {
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		if (sb.getObjective("hp") != null) {
			sb.getObjective("hp").unregister();
		}
		if (pl.tmMode) {
			String[] ts = { "Black", "Dark_Blue", "Dark_Green", "Dark_Aqua", "Dark_Red", "Dark_Purple", "Gold", "Gray", "Dark_Gray", "Blue", "Green", "Aqua", "Red", "Light_Purple", "Yellow", "White" };
			for (String t : ts) {
				if (sb.getTeam(t) != null) {
					sb.getTeam(t).unregister();
				}
			}
		}
		for (World w : pl.getServer().getWorlds()) {
			w.setGameRuleValue("doDaylightCycle", "true");
			w.setGameRuleValue("naturalRegeneration", "true");
			w.setGameRuleValue("keepInventory", "false");
			w.getWorldBorder().setSize(1000000);
			w.setPVP(true);
		}
		File dl = new File(pl.getDataFolder(), "data");
		if (dl.exists()) {
			try {
				FileUtils.deleteDirectory(dl);
			} catch (IOException e) {
				pl.getLogger().warning("Filesystem> Directory 'plugins/" + pl.getDescription().getName() + "/data' was not deleted!");
			}
		}
		clearAllPlayer(pl);
	}

	/**
	 * ~ Clear All Player ~
	 * <p>
	 * Reset all player on the server.
	 * </p>
	 * 
	 * @param pl = Main Plugin
	 */
	public static void clearAllPlayer(Main pl) {
		for (Player p : pl.getServer().getOnlinePlayers()) {
			clearPlayer(p, true);
		}
	}

	/**
	 * ~ Reset Player ~
	 * 
	 * @param p = Player
	 * @param i = boolean <i>(true: Waithing, false: Ingame.)</i>
	 */
	public static void clearPlayer(Player p, boolean i) {
		for (ItemStack is : p.getInventory().getContents()) {
			p.getInventory().remove(is);
		}
		p.getEquipment().setHelmet(null);
		p.getEquipment().setChestplate(null);
		p.getEquipment().setLeggings(null);
		p.getEquipment().setBoots(null);
		p.setBedSpawnLocation(null);
		p.setExp(0);
		p.setFallDistance(0);
		p.setFireTicks(0);
		p.setFoodLevel(20);
		p.setHealth(20.0);
		p.setLevel(0);
		p.setNoDamageTicks(200);
		p.closeInventory();
		if (i) {
			p.setGameMode(GameMode.ADVENTURE);
			p.removeAchievement(Achievement.OPEN_INVENTORY);
			Function.updateDifficultyLvlOnClient(p, EnumDifficulty.PEACEFUL);
		} else {
			p.setGameMode(GameMode.SPECTATOR);
		}
	}

	/**
	 * ~ Hub Items For All Playes ~
	 * <p>
	 * Give all player hub items.
	 * </p>
	 * 
	 * @param pl = Main Plugin
	 */
	public static void allPlayerHubItem(Main pl) {
		for (Player p : pl.getServer().getOnlinePlayers()) {
			playerHubItem(pl, p);
		}
	}

	/**
	 * ~ Hub Items To Player ~
	 * 
	 * @param pl = Main Plugin
	 * @param p = Player
	 */
	public static void playerHubItem(Main pl, Player p) {
		if (pl.uhcBook) {
			HashMap<ItemStack, Integer> item = pl.itemStore.get("Book");
			p.getInventory().setItem(item.get(item.keySet().iterator().next()), item.keySet().iterator().next());
		}
		if (pl.tmsST) {
			HashMap<ItemStack, Integer> item = pl.itemStore.get("selectteam");
			p.getInventory().setItem(item.get(item.keySet().iterator().next()), item.keySet().iterator().next());
		}
	}

	/**
	 * ~ Minecraft Color Code ~
	 * <p>
	 * Minecraft Color Coder. Change &(0-9) - &(a-f) to minecraft color codex.
	 * </p>
	 * 
	 * @param str = String
	 * @return String
	 */
	public static String minecraftColor(String str) {
		return str.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
	}

	/**
	 * ~ Team | Add Player ~
	 * 
	 * @param pl = Main Plugin
	 * @param p = Player
	 * @param team = String <i>(Scoreboard Team Name)</i>
	 * @param i = boolean <i>(True = AutoTeam | False = SelectedTeam)</i>
	 */
	@SuppressWarnings("deprecation")
	public static void joiningTeam(Main pl, Player p, String t, boolean i) {
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		if (i) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
			sb.getTeam(t).addPlayer(p);
			p.sendMessage("§9Team>§7 You joined team" + sb.getTeam(t).getPrefix() + "§l " + t.replace("_", " ") + "§7.");
			updateTeamInventory(pl);
		} else {
			if (sb.getTeam(t) != null) {
				if (!sb.getTeam(t).getPlayers().contains(p)) {
					if (!(sb.getTeam(t).getPlayers().size() >= pl.tmMaxPlayer)) {
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
						sb.getTeam(t).addPlayer(p);
						p.sendMessage("§9Team>§7 You joined team" + sb.getTeam(t).getPrefix() + "§l " + t.replace("_", " ") + "§7.");
						updateTeamInventory(pl);
						if (pl.sm) {
							if (ServerMode.enoughTeamToStart() >= pl.smMts) {
								ServerMode.onTeamStart();
							}
						}
					} else {
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
					}
				} else {
					p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
				}
			} else {
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
			}
		}
	}

	/**
	 * ~ Team | Remove Player ~
	 * <p>
	 * Player leave the team he/she was in.
	 * </p>
	 * 
	 * @param pl = Main Plugin
	 * @param p = Player
	 */
	public static void removeTeam(Main pl, Player p) {
		if (p.getScoreboard().getEntryTeam(p.getName()) != null) {
			p.getScoreboard().getEntryTeam(p.getName()).removeEntry(p.getName());
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 5, 1f);
			p.sendMessage("§9Team>§7 You are no longer in team.");
			updateTeamInventory(pl);
		} else {
			p.playSound(p.getLocation(), Sound.NOTE_BASS, 5, 0f);
		}
	}

	/**
	 * ~ Ultra Hardcore Game | Start ~
	 * 
	 * @param pl = Main Plugin
	 */
	public static void startUhc(Main pl) {
		pl.gameTimer = System.currentTimeMillis();
		for (World w : pl.getServer().getWorlds()) {
			w.setPVP(true);
			w.setDifficulty(Difficulty.NORMAL);
			if (pl.grList != null) {
				if (getGamerule(pl.grList, "doDaylightCycle").equalsIgnoreCase("true")) {
					w.setGameRuleValue("doDaylightCycle", "true");
				}
			}
			w.setGameRuleValue("naturalRegeneration", "false");
			w.getWorldBorder().setCenter(0, 0);
			w.getWorldBorder().setSize(pl.wbStartPos);
			if (pl.wbTime != 0) {
				w.getWorldBorder().setSize(pl.wbEndPos, pl.wbTime);
			}
		}
	}

	/**
	 * ~ Ultra Harcore Game | Game Check ~
	 * <p>
	 * Check if the game is over or not.
	 * </p>
	 * 
	 * @param pl = Main Plugin
	 */
	public static void uhcGameCheck(Main pl) {
		if (pl.tmMode) {
			if (pl.igTms.size() == 1) {
				pl.gmStat = EnumGame.FINISH;
				pl.getServer().getScheduler().cancelTask(pl.sysTimeGame);
				gameEndReport(pl);
			}
		} else {
			if (pl.igPs.size() == 1) {
				pl.gmStat = EnumGame.FINISH;
				pl.getServer().getScheduler().cancelTask(pl.sysTimeGame);
				gameEndReport(pl);
			}
		}
	}

	/**
	 * ~ Damager Logger | From Entity ~
	 * 
	 * @param e = EntityDamageEvent
	 * @return String[]
	 */
	public static String[] getDamageFromEntity(EntityDamageEvent e) {
		String[] data = { "", "" };
		switch (e.getCause()) {
		case ENTITY_ATTACK:
			data = getEntityAttackerCause(e);
			return data;
		case ENTITY_EXPLOSION:
			data = getEntityAttackerCause(e);
			return data;
		case FALL:
			data[0] = "Ender Pearl";
			return data;
		case FALLING_BLOCK:
			data[0] = "Falling Anvil";
			return data;
		case LIGHTNING:
			data[0] = "Lightning";
			return data;
		case MAGIC:
			data = getEntityAttackerCause(e);
			return data;
		case PROJECTILE:
			data = getEntityAttackerCause(e);
			return data;
		case THORNS:
			data = getEntityAttackerCause(e);
			return data;
		default:
			data[0] = "Unknown Entity";
			return data;
		}
	}

	/**
	 * ~ Damager Logger | From Block ~
	 * 
	 * @param c = DamageCause
	 * @return String[]
	 */
	public static String[] getDamageFromBlock(DamageCause c) {
		String[] data = { "", "" };
		switch (c) {
		case CONTACT:
			data[0] = "Cactus";
			return data;
		case LAVA:
			data[0] = "Lava";
			return data;
		case VOID:
			data[0] = "Void";
			return data;
		default:
			data[0] = "Unknown";
			return data;
		}
	}

	/**
	 * ~ Damager Logger | From Environment ~
	 * 
	 * @param c = DamageCause
	 * @return String[]
	 */
	public static String[] getDamageFromEnvironment(DamageCause c) {
		String[] data = { "", "" };
		switch (c) {
		case DROWNING:
			data[0] = "Drowning";
			return data;
		case FALL:
			data[0] = "Fall";
			return data;
		case FIRE:
			data[0] = "Fire";
			return data;
		case FIRE_TICK:
			data[0] = "Fire";
			return data;
		case MAGIC:
			data[0] = "Poison";
			return data;
		case POISON:
			data[0] = "Poison";
			return data;
		case SUFFOCATION:
			data[0] = "Suffocation";
			return data;
		case STARVATION:
			data[0] = "Starvation";
			return data;
		case SUICIDE:
			data[0] = "Suicide";
			return data;
		case WITHER:
			data[0] = "Wither Poison";
			return data;
		default:
			data[0] = "Unknown";
			return data;
		}
	}

	/**
	 * ~ Damager Logger | Save Player Profile ~
	 * 
	 * @param pl = Main Plugin
	 * @param uuid = UUID <i>(Player Uuid)</i>
	 * @param data = String <i>(Damager info)</i>
	 * @param dmg = double <i>(Damage value)</i>
	 */
	public static void damageLoggerSave(Main pl, UUID uuid, String[] data, double dmg) {
		try {
			long t = (System.currentTimeMillis() - pl.gameTimer);
			File f = new File(pl.getDataFolder(), "data/" + uuid + ".yml");
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			fc.set("dl." + t + ".f", data[0]);
			fc.set("dl." + t + ".u", data[1]);
			fc.set("dl." + t + ".d", dmg);
			fc.save(f);
		} catch (IOException e) {
			pl.getLogger().warning("DamageLogger> Something went wrong here... \n" + e.getMessage());
		}
	}

	/**
	 * ~ Damager Logger | Get Player Profile ~
	 * 
	 * @param pl = Main Plugin
	 * @param uuid = UUID <i>(Player Uuid)</i>
	 * @param name = String <i>(Player Name)</i>
	 * @return ItemStack <i>(Item | Damager Logger Book)</i>
	 */
	public static ItemStack getPlayerDamagerLoggerBook(Main pl, UUID uuid, String name) {
		File f = new File(pl.getDataFolder(), "data/" + uuid + ".yml");
		if (f.exists()) {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
			ItemStack is = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bm = (BookMeta) is.getItemMeta();
			bm.setAuthor(pl.getDescription().getName() + " - Damager Logger");
			bm.setDisplayName("§cDamager Logger §7>§e " + name);
			bm.setTitle("UHC");
			bm.setLore(Arrays.asList("", "§7List of all damage you", "§7have got from this UHC."));
			bm.addPage("§l Damage Logger §r\n \nThis book will show you. Who has damaged you from this UHC.");
			String pg = "";
			int i = 1;
			int ii = 1;
			for (String t : fc.getConfigurationSection("dl").getKeys(false)) {
				if (i == 4) {
					bm.addPage(pg);
					pg = "";
					i = 1;
				}
				if (pg.isEmpty()) {
					pg = "§1" + ii + ".§9 " + getTimeFormat(t) + "§r\n";
				} else {
					pg += "§1" + ii + ".§9 " + getTimeFormat(t) + "§r\n";
				}
				pg += "   §8" + fc.getString("dl." + t + ".f") + "§r\n";
				pg += (fc.getString("dl." + t + ".u").isEmpty() ? "" : "   §8" + fc.getString("dl." + t + ".u") + "§r\n");
				pg += "   §c" + fc.getDouble("dl." + t + ".d") + " dmg§r\n \n";
				i++;
				ii++;
			}
			bm.addPage(pg);
			is.setItemMeta(bm);
			f.delete();
			return is;
		}
		return null;
	}

	/**
	 * ~ Damager Logger | Get Book ~
	 * 
	 * @param pl = Main Plugin
	 * @param p = Player
	 * @param type = boolean <i>(true == Alive player | false = Spectator player)</i>
	 */
	public static void getDamageLogBook(Main pl, Player p, boolean type) {
		ItemStack is = Function.getPlayerDamagerLoggerBook(pl, p.getUniqueId(), p.getName());
		if (is != null) {
			p.getInventory().setItem(4, is);
			if (type) {
				p.sendMessage("§9Dmg>§7 If you want to see who has damage you? Right click on the damage log book.");
			} else {
				p.sendMessage("§9Dmg>§7 If you want to see who has damage you? Type §e/dmg");
			}
		}
	}

	/**
	 * ~ Ultra Hardcore Game | Preparing To Start ~
	 * 
	 * @param pl = Main Plugin
	 */
	public static void gamePreparingToStartSetup(Main pl) {
		clearAllPlayer(pl);
		for (World w : pl.getServer().getWorlds()) {
			w.setGameRuleValue("keepInventory", "false");
		}
		if (pl.tmMode) {
			for (Player p : pl.getServer().getOnlinePlayers()) {
				if (p.getScoreboard().getEntryTeam(p.getName()) == null) {
					p.setGameMode(GameMode.SPECTATOR);
					p.sendMessage("§9Spectator>§7 You have been move to spectator mode.");
				}
			}
		}
	}

	/**
	 * ~ Start Offline Timer For Disconnected Player ~
	 * <p>
	 * Set offline InGame player a timer. if thay are out more enn 'pl.okMaxTime' thay will be
	 * kild by offline timer
	 * </p>
	 * 
	 * @param pl = Main Plugin
	 * @param pId = UUID <i>(Player Uuid)</i>
	 * @param t = int <i>(Add more delay)</i>
	 */
	public static void startOfflinePlayerTimer(final Main pl, UUID pId, int t) {
		final OfflinePlayer off = pl.getServer().getOfflinePlayer(pId);
		if (!pl.igOffPs.containsKey(off.getUniqueId())) {
			int sysID = pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
				@Override
				public void run() {
					for (Player p : pl.getServer().getOnlinePlayers()) {
						if (pl.tmMode) {
							Team t = pl.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(off.getName());
							p.sendMessage(pl.okMsg.replace("$[P]", t.getPrefix() + off.getName() + "§r"));
						} else {
							p.sendMessage(pl.okMsg.replace("$[P]", off.getName()));
						}
					}
					if (pl.tmMode) {
						Team t = pl.getServer().getScoreboardManager().getMainScoreboard().getEntryTeam(off.getName());
						t.removeEntry(off.getName());
						if (t.getSize() == 0) {
							pl.igTms.remove(t.getName());
						}
					}
					pl.igPs.remove(off.getUniqueId());
					pl.igOffPs.remove(off.getUniqueId());
					if (pl.offPs.contains(off.getUniqueId())) {
						pl.offPs.remove(off.getUniqueId());
					}
					uhcGameCheck(pl);
				}
			}, ((20 * 60) * pl.okMaxTime + t));
			pl.igOffPs.put(off.getUniqueId(), sysID);
		}
	}

	/**
	 * ~ Get Clock Format ~
	 * 
	 * @param t = String <i>(Milliseconds)</i>
	 * @return String <i>(Clock Format)</i>
	 */
	public static String getTimeFormat(String t) {
		String o = "";
		int sec = 1000, min = 60 * sec, hour = 60 * min, h = 0, m = 0, s = 0, i = 0;
		t = t.replaceFirst("\\D+", "");
		if (t.isEmpty()) {
			t = "0";
		}
		if (t.length() > 9) {
			o = "+99:59:59";
		} else {
			i = Integer.parseInt(t);
			if (i >= hour) {
				h = i / hour;
				i %= hour;
			}
			if (i >= min) {
				m = i / min;
				i %= min;
			}
			if (i >= sec) {
				s = i / sec;
				i %= sec;
			}
			if (h != 0) {
				o = h + ":";
			}
			if (m < 10) {
				o += "0" + m + ":";
			} else {
				o += m + ":";
			}
			if (s < 10) {
				o += "0" + s;
			} else {
				o += s;
			}
		}
		return o;
	}

	/**
	 * ~ Replace Gamerule To Text ~
	 * 
	 * @param gr = String <i>(Minecraft Gamerule)</i>
	 * @return String
	 */
	public static String gameruleReplace(String gr) {
		switch (gr) {
		case "doDaylightCycle":
			return "Eternal day";
		case "doEntityDrops":
			return "Entity drops";
		case "doFireTick":
			return "Fire tick";
		case "doMobLoot":
			return "Mob loot";
		case "doMobSpawning":
			return "Mob spawning";
		case "doTileDrops":
			return "Tile drop";
		case "mobGriefing":
			return "Mob griefing";
		case "randomTickSpeed":
			return "Tick speed";
		case "reducedDebugInfo":
			return "Short debug info";
		default:
			return gr;
		}
	}

	/**
	 * ~ Kick all players ~
	 * 
	 * @param pl = Main Plugin
	 */
	public static void kickAllPlayers(Main pl) {
		for (Player p : pl.getServer().getOnlinePlayers()) {
			p.kickPlayer("§c§lUltra Hardcore 1.8 - Update/Reload");
		}
	}

	/**
	 * ~ Get Spawn location for player / team ~
	 * 
	 * @param pl = <i>Main Plugin<i>
	 * @param i = <i>(int)</i> Total player / team
	 * @return ArrayList<Location> = Spawn location of player / team.
	 */
	public static ArrayList<Location> getSpawnList(Main pl, int i) {
		ArrayList<Location> l = new ArrayList<Location>();
		World w = pl.getServer().getWorlds().get(0);
		int sz = pl.woArenaSize - 10;
		int d = getDistance(pl.woArenaSize, i);
		Random r = new Random();
		if (d == 0) {
			for (int a = 0; a < i; a++) {
				l.add(new Location(w, (-sz + r.nextInt(sz * 2)), 64, (-sz + r.nextInt(sz * 2))));
			}
			return l;
		} else {
			int a = 1;
			int b;
			int e = 0;
			while (a <= i) {
				b = 1;
				Location la = new Location(w, (-sz + r.nextInt(sz * 2)), 64, (-sz + r.nextInt(sz * 2)));
				if (l.size() != 0) {
					for (Location lb : l) {
						if (la.distance(lb) <= d) {
							b = 0;
							e++;
							break;
						}
					}
				}
				if (b == 1) {
					Location c = new Location(la.getWorld(), la.getX(), la.getWorld().getHighestBlockYAt(la), la.getZ());
					if (c.add(0, -1, 0).getBlock().getType().equals(Material.STATIONARY_WATER)) {
						e++;
					} else {
						l.add(la);
						e = 0;
						a++;
					}
				}
				if (e >= 500) {
					l.add(la);
					e = 0;
					a++;
				}
			}
			return l;
		}
	}

	/**
	 * ~ Set max distance for each player / team ~
	 * 
	 * @param sz = (int) = Arena size
	 * @param i = <i>(int)</i> Total player / team
	 * @return int = Max distance.
	 */
	private static int getDistance(int sz, int i) {
		int[] d = { 400, 300, 200, 100, 50 };
		int a = 0;
		for (int di : d) {
			double max = (Math.pow((sz * 2), 2) / (di * i));
			if (a == 4) {
				if (max > 159.9) {
					return 50;
				} else {
					return 0;
				}
			}
			if (a == 3) {
				if (max > 266) {
					return 100;
				} else {
					a++;
				}
			}
			if (a == 2) {
				if (max > 399) {
					return 200;
				} else {
					a++;
				}
			}
			if (a == 1) {
				if (max > 444) {
					return 300;
				} else {
					a++;
				}
			}
			if (a == 0) {
				if (max > 624) {
					return 400;
				} else {
					a++;
				}
			}
		}
		return 0;
	}

	/**
	 * ~ Get Gamerule From List ~
	 * 
	 * @param pl = Main Plugin
	 * @param grList = ArrayList<String>
	 * @param str = GameRule
	 * @return String
	 */
	private static String getGamerule(ArrayList<String> grList, String str) {
		for (String Gamerule : grList) {
			String[] gr = Gamerule.split("\\|");
			if (gr[0].equals(str)) {
				return gr[1];
			}
		}
		return null;
	}

	/**
	 * ~ Damager Logger | Get Entity Attacker Name ~
	 * 
	 * @param e = EntityDamageEvent
	 * @return String[]
	 */
	private static String[] getEntityAttackerCause(EntityDamageEvent e) {
		String[] data = { "", "" };
		switch (e.getCause()) {
		case ENTITY_ATTACK:
			if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
				Player a = (Player) ((EntityDamageByEntityEvent) e).getDamager();
				data[0] = a.getName();
				data[1] = getItemName(a.getItemInHand().getType());
				return data;
			} else {
				data[0] = getEntityName(((EntityDamageByEntityEvent) e).getDamager());
				return data;
			}
		case ENTITY_EXPLOSION:
			data[0] = getEntityName(((EntityDamageByEntityEvent) e).getDamager());
			return data;
		case MAGIC:
			ThrownPotion tp = (ThrownPotion) ((EntityDamageByEntityEvent) e).getDamager();
			if (tp.getShooter() instanceof Player) {
				data[0] = ((Player) tp.getShooter()).getName();
				data[1] = getPotionEffects(tp.getEffects());
				return data;
			} else {
				if (tp.getShooter() != null) {
					data[0] = getEntityName((Entity) tp.getShooter());
					data[1] = getPotionEffects(tp.getEffects());
					return data;
				} else {
					data[0] = "Unknown";
					data[1] = getPotionEffects(tp.getEffects());
					return data;
				}
			}
		case PROJECTILE:
			Projectile pj = (Projectile) ((EntityDamageByEntityEvent) e).getDamager();
			if (pj.getShooter() instanceof Player) {
				data[0] = ((Player) pj.getShooter()).getName();
				data[1] = getEntityName(pj);
				return data;
			} else {
				if (pj.getShooter() != null) {
					data[0] = getEntityName((Entity) pj.getShooter());
					data[1] = getEntityName(pj);
					return data;
				} else {
					data[0] = "Unknown";
					data[1] = getEntityName(pj);
					return data;
				}
			}
		case THORNS:
			if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
				data[0] = ((Player) ((EntityDamageByEntityEvent) e).getDamager()).getName();
				data[1] = "Thorns";
				return data;
			} else {
				data[0] = getEntityName(((EntityDamageByEntityEvent) e).getDamager());
				data[1] = "Thorns";
				return data;
			}
		default:
			data[0] = "Unknown Entity";
			return data;
		}
	}

	/**
	 * ~ Damager Logger | Get Potion Effects ~
	 * 
	 * @param effects = Collection<PotionEffect>
	 * @return String
	 */
	private static String getPotionEffects(Collection<PotionEffect> effs) {
		String e = "";
		for (PotionEffect ef : effs) {
			if (e.isEmpty()) {
				e = ef.getType().getName().substring(0, 1).toUpperCase() + ef.getType().getName().substring(1, ef.getType().getName().length()).toLowerCase();
			} else {
				e = ", " + ef.getType().getName().substring(0, 1).toUpperCase() + ef.getType().getName().substring(1, ef.getType().getName().length()).toLowerCase();
			}
		}
		return e;
	}

	/**
	 * ~ Damager Logger | Get Entity Name ~
	 * 
	 * @param e = Entity
	 * @return String
	 */
	private static String getEntityName(Entity e) {
		switch (e.getType()) {
		case ARROW:
			return "Archery";
		case BLAZE:
			return "Blaze";
		case CAVE_SPIDER:
			return "Cave Spider";
		case CREEPER:
			return "Creeper";
		case EGG:
			return "Egg";
		case ENDER_CRYSTAL:
			return "Ender Crystal";
		case ENDER_DRAGON:
			return "Ender Dragon";
		case ENDERMAN:
			return "Enderman";
		case ENDERMITE:
			return "Endermite";
		case GHAST:
			return "Ghast";
		case FIREBALL:
			return "Fireball";
		case FISHING_HOOK:
			return "Fishing Hook";
		case GUARDIAN:
			return "Guardian";
		case MAGMA_CUBE:
			return "Magma Cube";
		case PIG_ZOMBIE:
			PigZombie pz = (PigZombie) e;
			if (pz.isBaby()) {
				if (pz.isInsideVehicle()) {
					if (pz.getVehicle().getType() == EntityType.CHICKEN) {
						return "(Pigman) Chicken Jockey";
					} else {
						return "Baby Zombie Pigman";
					}
				} else {
					return "Baby Zombie Pigman";
				}
			} else {
				return "Zombie Pigman";
			}
		case RABBIT:
			return "Rabbit";
		case SILVERFISH:
			return "Silverfish";
		case SKELETON:
			Skeleton s = (Skeleton) e;
			if (s.getSkeletonType() == SkeletonType.WITHER) {
				return "Wither Skeleton";
			} else {
				return "Skeleton";
			}
		case SLIME:
			return "Slime";
		case SNOWBALL:
			return "Snowball";
		case SPIDER:
			return "Spider";
		case PRIMED_TNT:
			return "TNT";
		case WITCH:
			return "Witch";
		case WITHER:
			return "Wither Boss";
		case WITHER_SKULL:
			return "Wither Skull";
		case WOLF:
			Wolf w = (Wolf) e;
			if (w.isAdult()) {
				return "Wolf";
			} else {
				return "Baby Wolf";
			}
		case ZOMBIE:
			Zombie z = (Zombie) e;
			if (z.isBaby()) {
				if (z.isVillager()) {
					if (z.isInsideVehicle()) {
						if (z.getVehicle().getType() == EntityType.CHICKEN) {
							return "Chicken Jockey Villager";
						} else {
							return "Baby Zombie Villager";
						}
					} else {
						return "Baby Zombie Villager";
					}
				} else {
					if (z.isInsideVehicle()) {
						if (z.getVehicle().getType() == EntityType.CHICKEN) {
							return "Chicken Jockey";
						} else {
							return "Baby Zombie";
						}
					} else {
						return "Baby Zombie";
					}
				}
			} else {
				if (z.isVillager()) {
					return "Zombie Villager";
				} else {
					return "Zombie";
				}
			}
		default:
			return "Unknown Entity";
		}
	}

	/**
	 * ~ Damager Logger | Get Item Name ~
	 * 
	 * @param m = Material
	 * @return String
	 */
	private static String getItemName(Material m) {
		switch (m) {
		case WOOD_SPADE:
			return "Wooden Shovel";
		case WOOD_PICKAXE:
			return "Wooden Pickaxe";
		case WOOD_AXE:
			return "Wooden Ake";
		case WOOD_HOE:
			return "Wooden Hoe";
		case WOOD_SWORD:
			return "Wooden Sword";
		case STONE_SPADE:
			return "Stone Shovel";
		case STONE_PICKAXE:
			return "Stone Pickaxe";
		case STONE_AXE:
			return "Stone Ake";
		case STONE_HOE:
			return "Stone Hoe";
		case STONE_SWORD:
			return "Stone Sword";
		case DIAMOND_SPADE:
			return "Diamond Shovel";
		case DIAMOND_PICKAXE:
			return "Diamond Pickaxe";
		case DIAMOND_AXE:
			return "Diamond Ake";
		case DIAMOND_HOE:
			return "Diamond Hoe";
		case DIAMOND_SWORD:
			return "Diamond Sword";
		case IRON_SPADE:
			return "Iron Shovel";
		case IRON_PICKAXE:
			return "Iron Pickaxe";
		case IRON_AXE:
			return "Iron Ake";
		case IRON_HOE:
			return "Iron Hoe";
		case IRON_SWORD:
			return "Iron Sword";
		case GOLD_SPADE:
			return "Golden Shovel";
		case GOLD_PICKAXE:
			return "Golden Pickaxe";
		case GOLD_AXE:
			return "Golden Ake";
		case GOLD_HOE:
			return "Golden Hoe";
		case GOLD_SWORD:
			return "Golden Sword";
		default:
			return "Fist";
		}
	}

	/**
	 * ~ Update Selected Team Inventory ~
	 * 
	 * @param pl = Main Plugin
	 */
	@SuppressWarnings("deprecation")
	public static void updateTeamInventory(Main pl) {
		Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
		for (ItemStack is : pl.invStore.get("selectteam").getContents()) {
			if (is != null) {
				if (!is.getType().equals(Material.BARRIER)) {
					ItemMeta im = is.getItemMeta();
					if (sb.getTeam(im.getDisplayName().replace(" ", "_").replaceAll("(§([a-fk-or0-9]))", "")).getSize() != 0) {
						ArrayList<String> lore = new ArrayList<String>();
						for (OfflinePlayer p : sb.getTeam(im.getDisplayName().replace(" ", "_").replaceAll("(§([a-fk-or0-9]))", "")).getPlayers()) {
							lore.add("§7§o" + p.getName());
						}
						lore.add(" ");
						lore.add("§8Max " + pl.tmMaxPlayer + " player" + (pl.tmMaxPlayer == 1 ? "" : "s"));
						im.setLore(lore);
					} else {
						im.setLore(Arrays.asList(" ", "§8Max " + pl.tmMaxPlayer + " player"));
					}
					is.setItemMeta(im);
				}
			}
		}
	}

	/**
	 * ~ Integer Checker ~
	 * 
	 * @param i = int <i>(Value)</i>
	 * @param min = int <i>(Minimum Value)</i>
	 * @param max = int <i>(Maximum Value)</i>
	 * @return boolean
	 */
	private static boolean integerData(int i, int min, int max) {
		if (Math.abs(i) < Integer.MAX_VALUE) {
			if (i >= min && i <= max) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ~ Ultra Hardcore Game | End ~
	 * 
	 * @param pl = Main Plugin
	 */
	private static void gameEndReport(final Main pl) {
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				sb.append("§8§m--------------------------------------------§r");
				sb.append("\n §lULTRA HARDCORE§r\n \n");
				if (pl.tmMode) {
					String t = pl.igTms.keySet().iterator().next();
					Team tm = pl.getServer().getScoreboardManager().getMainScoreboard().getTeam(t);
					sb.append(" §6§lTeam " + tm.getPrefix() + "§l" + tm.getName().replace("_", " ") + "§6§l has won the game.§r\n");
				} else {
					sb.append(" §f§l" + pl.getServer().getOfflinePlayer(pl.igPs.keySet().iterator().next()).getName() + "§6§l has won the game.§r\n");
				}
				sb.append("\n§8§m--------------------------------------------§r");
				for (Player p : pl.getServer().getOnlinePlayers()) {
					p.sendMessage(sb.toString());
					p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 5, 0f);
				}
				if (pl.tmMode) {
					String t = pl.igTms.keySet().iterator().next();
					Team tm = pl.getServer().getScoreboardManager().getMainScoreboard().getTeam(t);
					for (OfflinePlayer winner : tm.getPlayers()) {
						if (winner.isOnline()) {
							Function.sendActionBarPlayerMessages((Player) winner, "§6§lCongratulations. Your team won the game!");
							if (pl.dmgLog) {
								getDamageLogBook(pl, ((Player) winner), true);
							}
						}
					}
				} else {
					Player winner = pl.igPs.get(pl.igPs.keySet().iterator().next());
					Function.sendActionBarPlayerMessages(winner, "§6§lCongratulations. You won the game!");
					if (pl.dmgLog) {
						getDamageLogBook(pl, (winner), true);
					}
				}
				for (World w : pl.getServer().getWorlds()) {
					w.getWorldBorder().setSize(w.getWorldBorder().getSize());
					w.setGameRuleValue("keepInventory", "true");
				}
				for (UUID off : pl.igOffPs.keySet()) {
					pl.getServer().getScheduler().cancelTask(pl.igOffPs.get(off));
				}
				if (pl.sm) {
					onServerRestart(pl);
				}
			}
		}, 20);
	}

	/**
	 * ~ Server Restart ~
	 * 
	 * @param pl = Main Plugin
	 */
	private static void onServerRestart(final Main pl) {
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {

			@Override
			public void run() {
				pl.getServer().broadcastMessage("§9Server>§7 Server is about to restart in ca 60 seconds.");
			}
		}, 20 * 5);
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {

			@Override
			public void run() {
				for (Player p : pl.getServer().getOnlinePlayers()) {
					p.kickPlayer("§c§lUltra Hardcore 1.8§r\n§cServer Restart§r\n§aThanks for playing.");
				}
			}
		}, 20 * 55);
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {

			@Override
			public void run() {
				pl.getServer().shutdown();
			}
		}, 20 * 60);
	}
}