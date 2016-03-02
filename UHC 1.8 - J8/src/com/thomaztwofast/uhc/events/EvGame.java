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

package com.thomaztwofast.uhc.events;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.thomaztwofast.uhc.GameManager;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.Perm;
import com.thomaztwofast.uhc.data.Config;
import com.thomaztwofast.uhc.data.GameStatus.Stat;
import com.thomaztwofast.uhc.data.PlayerData;

public class EvGame implements Listener {
	private Main pl;
	private Config c;
	private GameManager gm;
	private HashMap<String, Integer> wbWarn = new HashMap<>();

	public EvGame(Main main) {
		pl = main;
		c = main.getPlConf();
		gm = main.getGame();
	}

	/**
	 * Event > Player Chat
	 */
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
		PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
		if (p.cp.getGameMode().equals(GameMode.SPECTATOR)) {
			if (c.gc_Spectator().length() == 0) {
				p.sendMessage("Chat", "Disabled!");
				e.setCancelled(true);
			} else {
				e.setFormat(
						c.gc_Spectator().replaceFirst("\\{0}", p.cp.getName()).replaceFirst("\\{1}", e.getMessage()));
			}
			return;
		}
		if (c.g_teamMode()) {
			if (p.cp.getScoreboard().getEntryTeam(p.cp.getName()) != null) {
				Team t = p.cp.getScoreboard().getEntryTeam(p.cp.getName());
				if (c.gc_TeamTeam().length() == 0) {
					p.sendMessage("Chat", "Disabled!");
					e.setCancelled(true);
				} else {
					if (e.getMessage().charAt(0) == '@') {
						if (c.gc_TeamPrivate().length() != 0) {
							for (String ep : t.getEntries()) {
								if (pl.getRegPlayerByName(ep) != null) {
									PlayerData pd = pl.getRegPlayerByName(ep);
									pd.cp.sendMessage(c.gc_TeamPrivate()
											.replaceFirst("\\{0}", t.getPrefix() + p.cp.getName() + t.getSuffix())
											.replaceFirst("\\{1}", e.getMessage().substring(1)));
									if (!p.equals(pd)) {
										pd.playLocalSound(Sound.NOTE_PIANO, 1.7f);
									}
								}
							}
							e.setCancelled(true);
							return;
						}
					}
					e.setFormat(c.gc_TeamTeam().replaceFirst("\\{0}", t.getPrefix() + p.cp.getName() + t.getSuffix())
							.replaceFirst("\\{1}", e.getMessage()));
				}
			} else {
				if (c.gc_TeamDefault().length() == 0) {
					p.sendMessage("Chat", "Disabled!");
					e.setCancelled(true);
				} else {
					e.setFormat(c.gc_TeamDefault().replaceFirst("\\{0}", p.cp.getName()).replaceFirst("\\{1}",
							e.getMessage()));
				}
			}
			return;
		}
		if (c.gc_Default().length() == 0) {
			p.sendMessage("Chat", "Disabled!");
			e.setCancelled(true);
		} else {
			e.setFormat(c.gc_Default().replaceFirst("\\{0}", p.cp.getName()).replaceFirst("\\{1}", e.getMessage()));
		}
	}

	/**
	 * Event > Player Login
	 */
	@EventHandler
	public void playerLogin(PlayerLoginEvent e) {
		if (c.server()) {
			Stat gs = gm.getStatus().getStat();
			if (gs.equals(Stat.ERROR)) {
				if (!pl.getPlFun().hasPermission(((CraftPlayer) e.getPlayer()), Perm.ERROR)) {
					e.disallow(Result.KICK_OTHER, c.server_KickMessage_PlayerJoinError());
				}
				return;
			} else if (gs.equals(Stat.DISABLED) | gs.equals(Stat.LOADING)) {
				e.disallow(Result.KICK_OTHER, c.server_KickMessage_PlayerJoinLoading());
				return;
			} else if (gs.equals(Stat.FINISHED)) {
				e.disallow(Result.KICK_OTHER, c.server_KickMessage_PlayerJoinGameEnd());
				return;
			}
		}
	}

	/**
	 * Event > Player Join
	 */
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		pl.regPlayer(e.getPlayer());
		PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
		if (gm.getStatus().getStat().getLvl() > 5) {
			if (gm.getStatus().getStat().equals(Stat.FINISHED)) {
				if (gm.isAlive(p.cp.getUniqueId())) {
					return;
				}
			} else if (gm.isOffline(p.cp.getUniqueId()) | gm.isOfflineByName(p.cp.getName())) {
				gm.gameStopOfflineTimer(p);
				return;
			}
			p.clearPlayer(false);
			p.sendMessage("UHC", "This game are in progress.");
			p.sendMessage("UHC", "You are now in spectator mode.");
			if (c.server()) {
				if (p.cp.getLocation().getWorld().equals(pl.getGame().getServer().getLobby())) {
					p.cp.teleport(pl.getServer().getWorlds().get(0).getSpawnLocation());
				}
				if (c.server_BungeeCord()) {
					p.cp.getInventory().setItem(c.server_BungeeCordInvSlot(), gm.getHubItem());
				}
			}
			return;
		}
		if (c.server()) {
			gm.getServer().newPlayer(p);
			return;
		}
		p.clearPlayer(true);
		gm.givePlayerHubItems(p);
	}

	/**
	 * Event > Player Quit
	 */
	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
		if (!gm.getStatus().getStat().isActive()) {
			if (gm.isAlive(p.cp.getUniqueId())) {
				gm.gameStartOfflineTimer(p.cp.getUniqueId(), p.cp.getName(), 0, 0);
			}
		} else if (c.server()) {
			Stat gs = gm.getStatus().getStat();
			if (gs.equals(Stat.WAITING) | gs.equals(Stat.WAITING_STARTING)) {
				gm.getServer().removePlayer(p);
			}
		}
		pl.removeRegPlayer(e.getPlayer());
	}

	/**
	 * Event > Player Death
	 */
	@EventHandler
	public void sdf(PlayerDeathEvent e) {
		if (gm.getStatus().getStat().equals(Stat.INGAME)) {
			PlayerData p = pl.getRegPlayer(e.getEntity().getUniqueId());
			if (gm.isAlive(p.cp.getUniqueId())) {
				gm.removeAlivePlayer(p);
				if (c.gh_Enalbed()) {
					e.getDrops().add(pl.getPlFun().getPlayerSkull(p));
				}
				return;
			}
		}
		e.setDeathMessage(null);
		e.setDroppedExp(0);
		e.getDrops().clear();
	}

	/**
	 * Event > Player Respawn
	 */
	@EventHandler
	public void sdf(PlayerRespawnEvent e) {
		Stat s = gm.getStatus().getStat();
		PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
		if (s.equals(Stat.STARTING) || s.equals(Stat.INGAME) || s.equals(Stat.FINISHED)) {
			if (gm.isAlive(p.cp.getUniqueId())) {
				e.setRespawnLocation(gm.getSpawnLocation(p));
			} else {
				e.setRespawnLocation(new Location(pl.getServer().getWorlds().get(0), 0.5, 100, 0.5));
				if (c.damageLog()) {
					if (s.equals(Stat.INGAME) || s.equals(Stat.FINISHED)) {
						gm.getDamagerBookToInventory(p, false);
					}
				}
				if (c.server()) {
					if (c.server_BungeeCord()) {
						p.cp.getInventory().setItem(c.server_BungeeCordInvSlot(), gm.getHubItem());
					}
				}
			}
		}
	}

	/**
	 * Event > Player Interact
	 */
	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		if (gm.getStatus().getStat().getLvl() <= 5) {
			if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (e.hasItem()) {
					if (e.getItem().equals(gm.getMenu().getItem())) {
						PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
						p.cp.openInventory(gm.getMenu().openMenu(p.cp.getUniqueId()));
						p.setInventoryLock(true, "MENU");
						e.setCancelled(true);
						return;
					}
					if (c.g_teamMode()) {
						if (e.getItem().equals(gm.getTeam().getTeamSelecter())) {
							PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
							p.cp.openInventory(gm.getTeam().getTeamSelecterInventory());
							p.setInventoryLock(true, "SELECT_TEAM");
							return;
						}
					}
					if (c.server()) {
						if (c.server_BungeeCord()) {
							if (e.getItem().equals(gm.getHubItem())) {
								PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
								p.TpFallBackServer();
								e.setCancelled(true);
								return;
							}
						}
					}
				}
			}
		} else if (gm.getStatus().getStat().equals(Stat.FINISHED)) {
			if (c.server() & c.server_BungeeCord()) {
				if (gm.isAlive(e.getPlayer().getUniqueId())) {
					if (e.getAction().equals(Action.RIGHT_CLICK_AIR)
							|| e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						if (e.hasItem()) {
							if (e.getItem().equals(gm.getHubItem())) {
								PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
								p.TpFallBackServer();
								e.setCancelled(true);
								return;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Event > Inventory Click
	 */
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (gm.getStatus().getStat().getLvl() <= 5) {
			PlayerData p = pl.getRegPlayer(e.getWhoClicked().getUniqueId());
			if (p.isInventoryLock()) {
				e.setCancelled(true);
				if (e.getRawSlot() >= 0 && e.getRawSlot() < e.getInventory().getSize()) {
					switch (p.getInventoryLocation()) {
					case "MENU":
						gm.getMenu().clickEvent(p, e.getInventory(), e.getClick(), e.getRawSlot());
						return;

					case "SELECT_TEAM":
						gm.getTeam().clickEvent(p, e.getClick(), e.getRawSlot(), e.getCurrentItem());
						return;
					}
				}
			}
		} else {
			if (c.gh_Enalbed()) {
				if (e.getInventory().getType().equals(InventoryType.WORKBENCH)) {

					if (e.getRawSlot() == 0) {
						if (e.getInventory().getItem(5) == null) {

							return;
						}

						if (e.getCurrentItem().getType().equals(Material.GOLDEN_APPLE)
								& e.getInventory().getItem(5).getType().equals(Material.SKULL_ITEM)) {
							e.getCurrentItem()
									.setItemMeta(gm.getSkullOwnerName(e.getCurrentItem(), e.getInventory().getItem(5)));
							return;
						}
					}
				}
			}
			if (c.server()) {
				if (c.server_BungeeCord()) {
					if (!gm.isAlive(e.getWhoClicked().getUniqueId())) {
						if (e.getInventory().getType().equals(InventoryType.CRAFTING)) {
							if (e.getSlot() == c.server_BungeeCordInvSlot()) {
								PlayerData p = pl.getRegPlayer(e.getWhoClicked().getUniqueId());
								p.TpFallBackServer();
								return;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Event > Inventory Close
	 */
	@EventHandler
	public void inventoryClose(InventoryCloseEvent e) {
		if (gm.getStatus().getStat().getLvl() <= 5) {
			PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
			if (p.isInventoryLock()) {
				p.setInventoryLock(false, "");
			}
		}
	}

	/**
	 * Event > Player Item Consume
	 */
	@EventHandler
	public void itemConsume(PlayerItemConsumeEvent e) {
		if (c.gh_Enalbed()) {
			if (e.getItem().getType().equals(Material.GOLDEN_APPLE) & e.getItem().hasItemMeta()) {
				if (e.getItem().getItemMeta().getDisplayName().equals("§bDefault Head Apple")) {
					gm.givePlayerRegenOfGoldenHead(e.getPlayer(), false);
				} else if (e.getItem().getItemMeta().getDisplayName().equals("§dGolden Head Apple")) {
					gm.givePlayerRegenOfGoldenHead(e.getPlayer(), true);
				}
			}
		}
	}

	/**
	 * Event > Player Achievement Awarded
	 */
	@EventHandler
	public void playerAchievement(PlayerAchievementAwardedEvent e) {
		if (!gm.getStatus().getStat().isActive()) {
			if (gm.isAlive(e.getPlayer().getUniqueId())) {
				return;
			}
		}
		e.setCancelled(true);
	}

	/**
	 * Event > Player Drop Item
	 */
	@EventHandler
	public void playerDrop(PlayerDropItemEvent e) {
		if (gm.getStatus().getStat().getLvl() <= 5) {
			e.setCancelled(true);
		}
	}

	/**
	 * Event > Entity Damage
	 */
	@EventHandler
	public void entityDamage(EntityDamageEvent e) {
		if (c.server()) {
			Stat gs = gm.getStatus().getStat();
			if (gs.equals(Stat.WAITING) | gs.equals(Stat.WAITING_STARTING) | gs.equals(Stat.STARTING)) {
				if (e.getEntity() instanceof Player) {
					if (e.getCause().equals(DamageCause.VOID)) {
						e.getEntity().teleport(e.getEntity().getWorld().getSpawnLocation().add(0.5, 0, 0.5));
						e.getEntity().setFallDistance(0);
					}
					e.setCancelled(true);
					return;
				}
			}
		}
		if (c.damageLog()) {
			if (gm.getStatus().getStat().equals(Stat.INGAME)) {
				if (e.getEntity() instanceof Player) {
					PlayerData p = pl.getRegPlayer(e.getEntity().getUniqueId());
					if (gm.isAlive(p.cp.getUniqueId())) {
						String[] dmg = gm.getDmg().getPlayerDamageSource(e);
						p.storagedamaged(gm.getGameTime(), e.getDamage(), dmg);
					}
				}
			}
		}
	}

	/**
	 * Event > Player Command
	 */
	@EventHandler
	public void playerCommand(PlayerCommandPreprocessEvent e) {
		String lab = e.getMessage().split(" ", 0)[0].substring(1).toLowerCase();
		PlayerData p = pl.getRegPlayer(e.getPlayer().getUniqueId());
		if (c.damageLog() & gm.getStatus().getStat().getLvl() > 6) {
			if (p.cp.getGameMode().equals(GameMode.SPECTATOR)) {
				if (lab.equals("dmg")) {
					if (p.cp.getInventory().getItem(4).getType().equals(Material.WRITTEN_BOOK)) {
						gm.getDmg().openSpectatorBook(p.cp);
						e.setCancelled(true);
						return;
					}
				}
			}
		}

		if (canUseCommand(p.cp, lab)) {
			return;
		}

		p.sendMessage("Command", "Disabled!");
		e.setCancelled(true);
	}

	/**
	 * Event > Server List Ping
	 */
	@EventHandler
	public void serverListPing(ServerListPingEvent e) {
		if (c.server()) {
			String s = "";
			if (c.serverAdvancedMotd()) {
				switch (gm.getStatus().getStat()) {
				case ERROR:
					s = c.server_AdvancedMotd_Error();
					break;
				case LOADING:
					s = c.server_AdvancedMotd_Loading();
					break;
				case RESET:
					s = c.server_AdvancedMotd_Reset();
					break;
				case WAITING:
					s = c.server_AdvancedMotd_Waiting();
					break;
				case WAITING_STARTING:
					s = c.server_AdvancedMotd_WaitingStart();
					break;
				case STARTING:
					s = c.server_AdvancedMotd_Starting();
					break;
				case INGAME:
					s = c.server_AdvancedMotd_InGame();
					break;
				case FINISHED:
					s = c.server_AdvancedMotd_Finished();
					break;
				default:
					s = c.server_AdvancedMotd_Disabled();
					break;
				}
			} else if (c.server_SimpleMotd().length() != 0) {
				s = c.server_SimpleMotd();
			}
			e.setMotd(motdConverter(s));
		}
	}

	/**
	 * Event > Player Move
	 */
	@EventHandler
	public void playerMove(PlayerMoveEvent e) {
		if (gm.getStatus().getStat().getLvl() > 5) {
			if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
				Player p = e.getPlayer();
				Location c = p.getWorld().getWorldBorder().getCenter();
				double s = p.getWorld().getWorldBorder().getSize() / 2;
				if (p.getLocation().getX() > (c.getX() + s)) {
					p.setVelocity(new Vector(-1, 0, 0));
					warnPlayer(p);
				} else if (p.getLocation().getX() < (c.getX() - s)) {
					p.setVelocity(new Vector(1, 0, 0));
					warnPlayer(p);
				} else if (p.getLocation().getZ() > (c.getZ() + s)) {
					p.setVelocity(new Vector(0, 0, -1));
					warnPlayer(p);
				} else if (p.getLocation().getZ() < (c.getZ() - s)) {
					p.setVelocity(new Vector(0, 0, 1));
					warnPlayer(p);
				}
			}
		}
	}

	// :: PRIVATE :: //

	/**
	 * Motd Converter
	 */
	private String motdConverter(String s) {
		s = s.replaceFirst("\\{0}", c.server_ID() + "");
		s = s.replaceFirst("\\{1}", gm.getStatus().getStat().toString());
		s = s.replaceFirst("\\{2}", pl.getRegPlayerData().size() + "");
		s = s.replaceFirst("\\{3}", gm.getAlivePlayers() + "");
		s = s.replaceFirst("\\{N}", "\n");
		return s;
	}

	/**
	 * See if the player has permission to run the command.
	 */
	private boolean canUseCommand(Player p, String lab) {
		Command cmd = pl.getCommand(lab);
		if (cmd != null) {
			if (p.hasPermission(pl.getCommand(lab).getPermission())) {
				if (c.server() | gm.getStatus().getStat().isActive()) {
					return true;
				}
			}
		} else if (pl.getPlFun().hasPermission(((CraftPlayer) p), Perm.GLOBAL_COMMAND)) {
			if (c.server() | gm.getStatus().getStat().isActive()) {
				return true;
			}
		}
		return false;
	}

	private void warnPlayer(Player p) {
		if (wbWarn.containsKey(p.getName())) {
			if (wbWarn.get(p.getName()) > 4) {
				p.teleport(new Location(pl.getServer().getWorlds().get(0), 0, 120, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25, 1));
				p.sendMessage("§9§l>§r§7 You have been teleported back to 0,0");
				wbWarn.remove(p.getName());
				return;
			}
			wbWarn.put(p.getName(), wbWarn.get(p.getName()) + 1);
			return;
		}
		wbWarn.put(p.getName(), 1);
	}
}