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

package com.thomaztwofast.uhc.events;

import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.Item;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.EnumGame;
import com.thomaztwofast.uhc.Function;
import com.thomaztwofast.uhc.Gui;
import com.thomaztwofast.uhc.Main;

public class UhcEvents implements Listener {
	private Main pl;

	public UhcEvents(Main main) {
		this.pl = main;
	}

	/**
	 * ~ Global Chat Format ~
	 * <p>
	 * Trigger when a player chat.<br>
	 * Change the chat format for spectator, none team player and team player.
	 * </p>
	 * 
	 * @param e = AsyncPlayerChatEvent (Player, Chat message)
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Team t = p.getScoreboard().getEntryTeam(p.getName());
		if (p.getGameMode().equals(GameMode.SPECTATOR)) {
			if (pl.chatS.isEmpty()) {
				p.sendMessage("§9Chat>§7 Disabled!");
				e.setCancelled(true);
			}
			e.setFormat(pl.chatS.replace("$[P]", p.getName()).replace("$[M]", e.getMessage()));
		} else {
			if (t != null) {
				if (pl.chatTT.isEmpty()) {
					p.sendMessage("§9Chat>§7 Disabled!");
					e.setCancelled(true);
				}
				if (!pl.chatTPC.isEmpty() && e.getMessage().startsWith("@")) {
					for (OfflinePlayer op : t.getPlayers()) {
						if (op.isOnline()) {
							Player tp = (Player) op;
							tp.sendMessage(pl.chatTPC.replace("$[P]", t.getPrefix() + p.getName() + t.getSuffix()).replace("$[M]", e.getMessage().replaceFirst("@", "")));
							if (!tp.equals(p)) {
								tp.playSound(tp.getLocation(), Sound.NOTE_PIANO, 5f, 1.7f);
							}
						}
					}
					e.setCancelled(true);
				} else {
					e.setFormat(pl.chatTT.replace("$[P]", t.getPrefix() + p.getName() + t.getSuffix()).replace("$[M]", e.getMessage()));
				}
			} else {
				if (pl.tmMode) {
					if (pl.chatTD.isEmpty()) {
						e.getPlayer().sendMessage("§9Chat>§7 Disabled!");
						e.setCancelled(true);
					}
					e.setFormat(pl.chatTD.replace("$[P]", p.getName()).replace("$[M]", e.getMessage()));
				} else {
					if (pl.chatD.isEmpty()) {
						e.getPlayer().sendMessage("§9Chat>§7 Disabled!");
						e.setCancelled(true);
					}
					e.setFormat(pl.chatD.replace("$[P]", p.getName()).replace("$[M]", e.getMessage()));
				}
			}
		}
	}

	/**
	 * ~ Player Join ~
	 * <p>
	 * Trigger when the player joining the game / server.
	 * </p>
	 * 
	 * @param e = PlayerJoinEvent (Player)
	 */
	@EventHandler
	public void playerJoin(final PlayerJoinEvent e) {
		if (pl.tlhf) {
			Function.updateTabListHeaderFooter(e.getPlayer(), pl.tlh, pl.tlf);
		}
		if (pl.gmStat != EnumGame.DISABLED) {
			if (pl.gmStat == EnumGame.WAITHING) {
				Function.clearPlayer(e.getPlayer(), true);
				Function.playerHubItem(pl, e.getPlayer());
			} else {
				if (pl.igPs.containsKey(e.getPlayer().getUniqueId())) {
					pl.igPs.put(e.getPlayer().getUniqueId(), e.getPlayer());
					if (pl.igOffPs.containsKey(e.getPlayer().getUniqueId())) {
						pl.getServer().getScheduler().cancelTask(pl.igOffPs.get(e.getPlayer().getUniqueId()));
						if (pl.offPs.contains(e.getPlayer().getUniqueId())) {
							Location l;
							if (pl.tmMode) {
								l = pl.igTms.get(e.getPlayer().getScoreboard().getEntryTeam(e.getPlayer().getName()).getName());
								l = new Location(l.getWorld(), l.getBlockX() + .5, l.getWorld().getHighestBlockYAt(l) + 2, l.getBlockZ() + .5);
							} else {
								l = pl.igPsl.get(e.getPlayer().getUniqueId());
								l = new Location(l.getWorld(), l.getBlockX() + .5, l.getWorld().getHighestBlockYAt(l) + 2, l.getBlockZ() + .5);
							}
							Function.clearPlayer(e.getPlayer(), true);
							e.getPlayer().teleport(l);
							if (e.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS)) {
								e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
								e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 3), 1));
							} else {
								e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (20 * 3), 1));
							}
							pl.offPs.remove(e.getPlayer().getUniqueId());
						}
						pl.igOffPs.remove(e.getPlayer().getUniqueId());
					}
					if (pl.gmStat == EnumGame.STARTING) {
						if (pl.fzp) {
							pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
								@Override
								public void run() {
									if (pl.tmMode) {
										Function.customWorldBorder(e.getPlayer(), pl.igTms.get(e.getPlayer().getScoreboard().getEntryTeam(e.getPlayer().getName()).getName()), pl.fzSize);
									} else {
										Function.customWorldBorder(e.getPlayer(), pl.igPsl.get(e.getPlayer().getUniqueId()), pl.fzSize);
									}
								}
							}, 5);
						}
					} else if (pl.gmStat == EnumGame.INGAME) {
						if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) {
							Function.updateDifficultyLvlOnClient(e.getPlayer(), (pl.woDiff == 1 ? EnumDifficulty.EASY : (pl.woDiff == 2 ? EnumDifficulty.NORMAL : EnumDifficulty.HARD)));
							e.getPlayer().setGameMode(GameMode.SURVIVAL);
						}
					}
				} else {
					Function.clearPlayer(e.getPlayer(), false);
				}
			}
		}
	}

	/**
	 * ~ Player Quit ~
	 * <p>
	 * Trigger when the player leave the game / server.
	 * </p>
	 * 
	 * @param e = PlayerQuitEvent (Player)
	 */
	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		if (pl.gmStat != EnumGame.DISABLED) {
			if (pl.gmStat == EnumGame.STARTING || pl.gmStat == EnumGame.INGAME) {
				if (pl.igPs.containsKey(e.getPlayer().getUniqueId())) {
					Function.startOfflinePlayerTimer(pl, e.getPlayer().getUniqueId(), 0);
				}
			}
		}
	}

	/**
	 * ~ Player Interact ~
	 * <p>
	 * Trigger when player interact left and right click action on item or block.
	 * </p>
	 * 
	 * @param e = PlayerInteractEvent (Player, Block, Item)
	 */
	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		if (pl.gmStat != EnumGame.DISABLED) {
			if (pl.gmStat == EnumGame.WAITHING && pl.tmMode) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (e.getItem() != null) {
						if (e.getItem().getType() == Material.PAPER) {
							if (e.getItem().getItemMeta().getDisplayName() != null) {
								if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§eSelect Team")) {
									e.getPlayer().openInventory(pl.invStore.get("selectteam"));
									e.setCancelled(true);
								}
							}
						}
					}
				}
			}
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (e.getItem() != null) {
					if (e.getItem().getType() == Material.WATCH) {
						if (e.getItem().getItemMeta().getDisplayName() != null) {
							if (e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§eUHC - Menu")) {
								Gui.openInventory(e.getPlayer());
								e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ~ Inventory Click ~
	 * <p>
	 * Registed left or right click action on selected inventory slot / item.
	 * </p>
	 * 
	 * @param e = inventoryClick (Inventory, Player)
	 */
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (pl.gmStat != EnumGame.DISABLED) {
			if (pl.invStore.containsValue(e.getInventory())) {
				e.setCancelled(true);
				if (e.getRawSlot() >= 0 && e.getRawSlot() < e.getInventory().getSize()) {
					if (pl.gmStat == EnumGame.WAITHING && pl.tmMode) {
						if (e.getInventory().equals(pl.invStore.get("selectteam"))) {
							if (e.getClick() == ClickType.LEFT) {
								if (e.getRawSlot() < 16) {
									if (e.getCurrentItem() != null) {
										if (e.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)) {
											if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
												Function.joiningTeam(pl, ((Player) e.getWhoClicked()), e.getCurrentItem().getItemMeta().getDisplayName().replace(" ", "_").replaceAll("(§([a-fk-or0-9]))", ""), false);
											}
										}
									}
								} else if (e.getRawSlot() == 17) {
									if (e.getCurrentItem() != null) {
										if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
											if (e.getCurrentItem().getItemMeta().getDisplayName() != null) {
												if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§cRemove me from my team")) {
													Function.removeTeam(pl, ((Player) e.getWhoClicked()));
												}
											}
										}
									}
								}
							}
						}
					}
					if (e.getInventory().equals(pl.invStore.get("gui_main"))) {
						if (e.getClick() == ClickType.LEFT) {
							Gui.clickGuiMain(((Player) e.getWhoClicked()), e.getRawSlot());
						}
					}
					if (e.getInventory().equals(pl.invStore.get("gui_settings"))) {
						Gui.clickGuiSettings(((Player) e.getWhoClicked()), e.getRawSlot(), e.getClick(), e.getCurrentItem());

					}
					if (e.getInventory().equals(pl.invStore.get("gui_gamerule"))) {
						Gui.clickGuiGamerule(((Player) e.getWhoClicked()), e.getRawSlot(), e.getClick(), e.getCurrentItem());
					}
					if (e.getInventory().equals(pl.invStore.get("gui_team"))) {
						Gui.clickGuiTeam(((Player) e.getWhoClicked()), e.getRawSlot(), e.getClick(), e.getCurrentItem());
					}
				}
			}
		}
	}

	/**
	 * ~ Player Command ~
	 * <p>
	 * Trigger when player type in command.
	 * </p>
	 * 
	 * @param e = PlayerCommandPreprocessEvent (Player, Command)
	 */
	@EventHandler
	public void playerCommand(PlayerCommandPreprocessEvent e) {
		if (pl.gmStat != EnumGame.DISABLED) {
			if (e.getPlayer().isOp()) {
				if (!(pl.gmStat == EnumGame.WAITHING || pl.gmStat == EnumGame.FINISH || pl.gmStat == EnumGame.FAILED)) {
					e.setCancelled(true);
					e.getPlayer().sendMessage("§9Command>§7 Disabled!");
				}
			} else {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§9Command>§7 Disabled!");
			}
			if (pl.dmgLog) {
				if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
					if (e.getMessage().split(" ")[0].equalsIgnoreCase("/dmg")) {
						ItemStack is = e.getPlayer().getInventory().getItem(4);
						if (is != null) {
							if (is.getType() == Material.WRITTEN_BOOK) {
								net.minecraft.server.v1_8_R3.ItemStack is_ms = new net.minecraft.server.v1_8_R3.ItemStack(Item.d("minecraft:written_book"));
								CraftPlayer cp = (CraftPlayer) e.getPlayer();
								cp.getInventory().setHeldItemSlot(4);
								cp.getHandle().openBook(is_ms);
								e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ~ Entity Damage ~
	 * <p>
	 * Trigger when the player get damage from entity.
	 * </p>
	 * 
	 * @param e = EntityDamageEvent (Player, Entity, Damage)
	 */
	@EventHandler
	public void entityDamage(EntityDamageEvent e) {
		if (pl.dmgLog) {
			if (pl.gmStat == EnumGame.INGAME) {
				if (e.getEntity() instanceof Player) {
					Player p = (Player) e.getEntity();
					if (pl.igPs.containsKey(p.getUniqueId())) {
						String[] dmgD;
						double dmgV = e.getDamage();
						if (e.getEventName().equalsIgnoreCase("EntityDamageByEntityEvent")) {
							dmgD = Function.getDamageFromEntity(e);
						} else if (e.getEventName().equalsIgnoreCase("EntityDamageByBlockEvent")) {
							dmgD = Function.getDamageFromBlock(e.getCause());
						} else {
							dmgD = Function.getDamageFromEnvironment(e.getCause());
						}
						Function.damageLoggerSave(pl, p.getUniqueId(), dmgD, dmgV);
					}
				}
			}
		}
	}

	/**
	 * ~ Player Death ~
	 * <p>
	 * Trigger when player die in the game.
	 * </p>
	 * 
	 * @param e = PlayerDeathEvent (Player)
	 */
	@EventHandler
	public void playerDeath(final PlayerDeathEvent e) {
		if (pl.gmStat != EnumGame.DISABLED) {
			if (pl.gmStat == EnumGame.INGAME) {
				if (pl.igPs.containsKey(e.getEntity().getUniqueId())) {
					pl.igPs.remove(e.getEntity().getUniqueId());
					pl.igPsl.remove(e.getEntity().getUniqueId());
					if (pl.tmMode) {
						Team t = e.getEntity().getScoreboard().getEntryTeam(e.getEntity().getName());
						t.removeEntry(e.getEntity().getName());
						if (t.getSize() == 0) {
							pl.igTms.remove(t.getName());
						}
					}
					pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
						@Override
						public void run() {
							if (e.getEntity().isOnline()) {
								e.getEntity().setGameMode(GameMode.SPECTATOR);
							}
						}
					}, 25);
					Function.uhcGameCheck(pl);
				} else {
					e.setDeathMessage(null);
					e.setDroppedExp(0);
					e.getDrops().clear();
				}
			} else if (pl.gmStat == EnumGame.STARTING) {
				if (!pl.igPs.containsKey(e.getEntity().getUniqueId())) {
					e.getEntity().setGameMode(GameMode.SPECTATOR);
				}
				e.setDeathMessage(null);
				e.setDroppedExp(0);
				e.getDrops().clear();
			}
		}
	}

	/**
	 * ~ Player Respawn ~
	 * <p>
	 * Trigger when player respawn.
	 * </p>
	 * 
	 * @param e = PlayerRespawnEvent (Player, Respawn location)
	 */
	@EventHandler
	public void playerRespawn(PlayerRespawnEvent e) {
		if (pl.gmStat != EnumGame.DISABLED) {
			if (pl.gmStat == EnumGame.STARTING || pl.gmStat == EnumGame.INGAME || pl.gmStat == EnumGame.FINISH) {
				if (pl.igPs.containsKey(e.getPlayer().getUniqueId())) {
					if (pl.tmMode) {
						e.setRespawnLocation(pl.igTms.get(e.getPlayer().getScoreboard().getEntryTeam(e.getPlayer().getName()).getName()).add(0.5, 0, 0.5));
					} else {
						e.setRespawnLocation(pl.igPsl.get(e.getPlayer().getUniqueId()).add(0.5, 0, 0.5));
					}
				} else {
					e.setRespawnLocation(new Location(pl.getServer().getWorlds().get(0), 0.5, 100, 0.5));
				}
			}
			if (pl.dmgLog) {
				if (pl.gmStat == EnumGame.INGAME || pl.gmStat == EnumGame.FINISH) {
					Function.getDamageLogBook(pl, e.getPlayer(), false);
				}
			}
		}
	}

	/**
	 * ~ Achievement Awarded ~
	 * <p>
	 * Trigger when player awarded a achievement.
	 * </p>
	 * 
	 * @param e = PlaPlayerAchievementAwardedEvent (Player, Achievement)
	 */
	@EventHandler
	public void playerAchievementAwarded(PlayerAchievementAwardedEvent e) {
		if (pl.gmStat != EnumGame.DISABLED) {
			if (!(pl.gmStat == EnumGame.STARTING || pl.gmStat == EnumGame.INGAME)) {
				e.setCancelled(true);
			} else {
				if (!pl.igPs.containsKey(e.getPlayer().getUniqueId())) {
					e.setCancelled(true);
				}
			}
		}
	}

	/**
	 * ~ Player Drop Item ~
	 * 
	 * @param e = PlayerDropItemEvent
	 */
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent e) {
		if (pl.gmStat != EnumGame.DISABLED) {
			if (pl.gmStat == EnumGame.WAITHING) {
				e.setCancelled(true);
			}
		}
	}
}