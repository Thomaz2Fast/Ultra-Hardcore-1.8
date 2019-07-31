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

package com.thomaztwofast.uhc.events;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Node;
import com.thomaztwofast.uhc.data.Status;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;

public class EvGame implements Listener {
	private Main pl;

	public EvGame(Main pl) {
		this.pl = pl;
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		UHCPlayer u = pl.getRegisterPlayer(e.getPlayer().getName());
		e.setCancelled(true);
		if (u.player.getGameMode() == GameMode.SPECTATOR) {
			if (!pl.config.chatSpectator.isEmpty()) {
				chat(pl.config.chatSpectator, u.player.getName(), e.getMessage());
			} else {
				u.sendActionMessage("\u00A78\u00A7LSpectator Chat\u00A7R \u00A7LDisabled!");
			}
			return;
		}
		if (pl.config.gameInTeam) {
			Team team = u.player.getScoreboard().getEntryTeam(e.getPlayer().getName());
			if (team != null) {
				if (!pl.config.chatTeam.isEmpty()) {
					if (e.getMessage().charAt(0) == '!' && !pl.config.chatTeamMsg.isEmpty() && pl.status.ordinal() > 5) {
						team.getEntries().forEach(p -> {
							pl.getRegisterPlayer(p).player.sendMessage(F.strReplace(pl.config.chatTeamMsg, team.getColor() + u.player.getName() + "\u00A7R", e.getMessage().substring(1)));
							if (!u.player.getName().equals(p))
								pl.getRegisterPlayer(p).playSound(Sound.BLOCK_NOTE_BLOCK_HARP, 1.7f);
						});
						return;
					}
					chat(pl.config.chatTeam, team.getPrefix() + team.getColor() + u.player.getName() + "\u00A7R", e.getMessage());
				} else {
					u.sendActionMessage("\u00A78\u00A7LTeam Chat\u00A7R \u00A7LDisabled!");
				}
				return;
			}
		}
		if (!pl.config.chatDefault.isEmpty()) {
			chat(pl.config.chatDefault, u.player.getName(), e.getMessage());
			return;
		}
		u.sendActionMessage("\u00A78\u00A7LChat\u00A7R \u00A7LDisabled!");
	}

	@EventHandler
	public void craftingItem(PrepareItemCraftEvent e) {
		if (pl.config.headEnable && e.getInventory().getType().equals(InventoryType.WORKBENCH)) {
			ItemStack[] items = e.getInventory().getContents();
			if (items[5].getType().equals(Material.PLAYER_HEAD) && items[0].getType().equals(Material.GOLDEN_APPLE))
				items[0].setItemMeta(pl.gameManager.setGoldenHeadLore(items[0], items[5]));
		}
	}

	@EventHandler
	public void damager(EntityDamageEvent e) {
		if (pl.config.serverEnable && pl.status.ordinal() > 3 && pl.status.ordinal() < 7 && e.getEntity() instanceof Player) {
			if (pl.status.ordinal() != 6 && e.getCause().equals(DamageCause.VOID)) {
				e.getEntity().teleport(e.getEntity().getWorld().getSpawnLocation().add(0.5, 1, 0.5), TeleportCause.PLUGIN);
				e.getEntity().setFallDistance(0f);
			}
			e.setCancelled(true);
			return;
		}
		if (pl.config.gameOldCombat && pl.status.ordinal() == 7 && e.getEventName().hashCode() == -438677650 && ((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
			switch (((Player) ((EntityDamageByEntityEvent) e).getDamager()).getInventory().getItemInMainHand().getType()) {
			case WOODEN_AXE:
			case GOLDEN_AXE:
				e.setDamage(e.getDamage() - 4d);
				break;
			case STONE_AXE:
				e.setDamage(e.getDamage() - 5d);
				break;
			case IRON_AXE:
				e.setDamage(e.getDamage() - 4d);
				break;
			case DIAMOND_AXE:
				e.setDamage(e.getDamage() - 3d);
				break;
			default:
				break;
			}
			e.setDamage(e.getDamage() < 0 ? 0d : e.getDamage());
		}
		if (pl.config.dmgEnable && pl.status.ordinal() == 7 && e.getEntity() instanceof Player) {
			UHCPlayer u = pl.getRegisterPlayer(e.getEntity().getName());
			if (pl.gameManager.inGamePlayers.contains(u.player.getName()))
				u.dmgStorage(pl.gameManager.timestamp, e.getDamage(), pl.gameManager.damagerLogger.getDamager(e));
		}
	}

	@EventHandler
	public void playerCommand(PlayerCommandPreprocessEvent e) {
		String str = e.getMessage().split(" ", 0)[0].substring(1).toLowerCase();
		UHCPlayer u = pl.getRegisterPlayer(e.getPlayer().getName());
		if (pl.status.ordinal() > 5 && pl.status.ordinal() < 8 && u.hasNode(Node.GL_CMD))
			return;
		else if ((pl.status.ordinal() < 6 || pl.status.ordinal() > 7) && ((pl.getCommand(str) != null && u.player.hasPermission(pl.getCommand(str).getPermission())) || u.hasNode(Node.GL_CMD)))
			return;
		u.sendCmdMessage("Command", "Disabled!");
		e.setCancelled(true);
	}

	@EventHandler
	public void playerLogin(PlayerLoginEvent e) {
		if (pl.config.serverEnable) {
			switch (pl.status) {
			case ERROR:
				if (!e.getPlayer().hasPermission(Node.ERR))
					e.disallow(Result.KICK_OTHER, pl.config.serverKickError);
				return;
			case DISABLED:
			case LOADING:
				e.disallow(Result.KICK_OTHER, pl.config.serverKickLoading + pl.gameManager.server.getChunkloaderProgress());
				return;
			case FINISHED:
				e.disallow(Result.KICK_OTHER, pl.config.serverKickEnding);
				return;
			default:
				return;
			}
		}
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		UHCPlayer u = pl.registerPlayer(e.getPlayer());
		if (pl.status.ordinal() > 5) {
			if (pl.status.equals(Status.FINISHED) && pl.gameManager.inGamePlayers.contains(u.player.getName()))
				return;
			else if (pl.gameManager.offlineTasks.containsKey(u.player.getName())) {
				pl.gameManager.removeOfflineTimer(u);
				return;
			}
			u.resetPlayer(false);
			u.sendCmdMessage("UHC", pl.status.equals(Status.FINISHED) ? "This game is finished." : "This game are in progress.");
			u.sendCmdMessage("UHC", "You are now in spectator mode.");
			if (pl.config.serverEnable) {
				if (u.player.getWorld().equals(pl.gameManager.server.lobby))
					u.player.teleport(pl.getServer().getWorlds().get(0).getSpawnLocation());
				if (pl.config.serverIsBungeeCord)
					u.player.getInventory().setItem(pl.config.serverInventorySlot, pl.gameManager.server.itemStack);
			}
			return;
		}
		if (pl.config.serverEnable)
			pl.gameManager.server.join(u);
		else {
			u.resetPlayer(true);
			u.getHubItems();
		}
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		UHCPlayer u = pl.getRegisterPlayer(e.getPlayer().getName());
		if (pl.status.ordinal() > 5 && pl.status.ordinal() < 8 && pl.gameManager.inGamePlayers.contains(u.player.getName()))
			pl.gameManager.addOfflineTimer(u.player.getName());
		else if (pl.config.serverEnable && pl.status.ordinal() > 3 && pl.status.ordinal() < 6)
			pl.gameManager.server.quit(u);
		pl.unRegisterPlayer(u.player);
	}

	@EventHandler
	public void playerDie(PlayerDeathEvent e) {
		if (pl.status.equals(Status.INGAME)) {
			UHCPlayer u = pl.getRegisterPlayer(e.getEntity().getName());
			if (pl.gameManager.inGamePlayers.contains(u.player.getName())) {
				pl.gameManager.removeInGamePlayer(u);
				if (pl.config.headEnable)
					e.getDrops().add(pl.gameManager.setGoldenHeadName(u.player.getUniqueId()));
			}
			return;
		}
		e.setDeathMessage(null);
		e.setDroppedExp(0);
		e.getDrops().clear();
	}

	@EventHandler
	public void playerRespawn(PlayerRespawnEvent e) {
		UHCPlayer u = pl.getRegisterPlayer(e.getPlayer().getName());
		if (pl.status.ordinal() > 5) {
			if (pl.gameManager.inGamePlayers.contains(u.player.getName()))
				e.setRespawnLocation(pl.gameManager.getEntryLocation(u));
			else {
				if (pl.config.dmgEnable)
					pl.gameManager.damagerLogger.giveItem(u.player.getName());
				if (pl.config.serverEnable && pl.config.serverIsBungeeCord)
					u.player.getInventory().setItem(pl.config.serverInventorySlot, pl.gameManager.server.itemStack);
				e.setRespawnLocation(new Location(pl.getServer().getWorlds().get(0), 0, 100, 0));
			}
		}
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		if (pl.status.ordinal() < 6) {
			if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.hasItem()) {
				if (e.getItem().equals(pl.gameManager.menu.itemStack)) {
					pl.gameManager.menu.openInv(pl.getRegisterPlayer(e.getPlayer().getName()));
					e.setCancelled(true);
					return;
				}
				if (pl.config.gameInTeam && e.getItem().equals(pl.gameManager.teams.selectItem)) {
					pl.gameManager.teams.openInventory(pl.getRegisterPlayer(e.getPlayer().getName()));
					e.setCancelled(true);
					return;
				}
				if (pl.config.serverEnable && pl.config.serverIsBungeeCord && e.getItem().equals(pl.gameManager.server.itemStack)) {
					pl.getRegisterPlayer(e.getPlayer().getName()).fallbackServer(e.getMaterial());
					e.setCancelled(true);
					return;
				}
			}
		} else if (pl.status.equals(Status.FINISHED) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && e.hasItem()) {
			if (pl.config.dmgEnable && e.getItem().getType().equals(Material.NETHER_STAR) && e.getItem().hasItemMeta()) {
				pl.gameManager.damagerLogger.openInv(pl.getRegisterPlayer(e.getPlayer().getName()));
				e.setCancelled(true);
				return;
			}
			if (pl.config.serverEnable && pl.config.serverIsBungeeCord && pl.gameManager.inGamePlayers.contains(e.getPlayer().getName()) && e.getItem().equals(pl.gameManager.server.itemStack)) {
				pl.getRegisterPlayer(e.getPlayer().getName()).fallbackServer(e.getMaterial());
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (pl.status.ordinal() < 6) {
			UHCPlayer u = pl.getRegisterPlayer(e.getWhoClicked().getName());
			if (u.isInvLock) {
				e.setCancelled(true);
				if (e.getRawSlot() >= 0 && e.getRawSlot() < e.getInventory().getSize()) {
					switch (u.invHistory[0]) {
					case 0:
						pl.gameManager.menu.click(u, e.getClick(), e.getRawSlot());
						return;
					case 1:
						pl.gameManager.teams.click(u, e.getClick(), e.getRawSlot());
						return;
					}
				}
			}
		} else {
			if (pl.config.dmgEnable) {
				UHCPlayer u = pl.getRegisterPlayer(e.getWhoClicked().getName());
				if (u.isInvLock) {
					e.setCancelled(true);
					if (e.getRawSlot() >= 0 && e.getRawSlot() < e.getInventory().getSize()) {
						if (u.invHistory[0] == 2) {
							pl.gameManager.damagerLogger.click(u, e.getClick(), e.getRawSlot(), e.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS_PANE) || e.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE));
							return;
						}
					}
				}
				if (!pl.gameManager.inGamePlayers.contains(u.player.getName()) && e.getRawSlot() == 40 && e.getInventory().getType().equals(InventoryType.CRAFTING)) {
					pl.gameManager.damagerLogger.openInv(u);
					return;
				}
			}
			if (pl.config.serverEnable && pl.config.serverIsBungeeCord && !pl.gameManager.inGamePlayers.contains(e.getWhoClicked().getName()) && e.getInventory().getType().equals(InventoryType.CRAFTING) && e.getSlotType().equals(SlotType.QUICKBAR))
				if (e.getSlot() == pl.config.serverInventorySlot)
					pl.getRegisterPlayer(e.getWhoClicked().getName()).fallbackServer(e.getCurrentItem().getType());
		}
	}

	@EventHandler
	public void inventoryClose(InventoryCloseEvent e) {
		if (pl.status.ordinal() < 6)
			pl.getRegisterPlayer(e.getPlayer().getName()).isInvLock = false;
	}

	@EventHandler
	public void playerConsume(PlayerItemConsumeEvent e) {
		if (pl.config.pluginEnable && e.getItem().getType().equals(Material.GOLDEN_APPLE) && e.getItem().hasItemMeta())
			pl.gameManager.setGoldenHeadRegen(pl.getRegisterPlayer(e.getPlayer().getName()), e.getItem().getItemMeta().getDisplayName().hashCode() == 853026900 ? false : true);
	}

	@EventHandler
	public void playerDrop(PlayerDropItemEvent e) {
		if (pl.status.ordinal() < 6)
			e.setCancelled(true);
	}

	@EventHandler
	public void serverPing(ServerListPingEvent e) {
		if (!pl.config.serverEnable)
			return;
		String str = "";
		if (pl.config.serverAdvancedMotd) {
			switch (pl.status) {
			case ERROR:
				str = pl.config.serverStatusError;
				break;
			case RESET:
				str = pl.config.serverStatusReset;
				break;
			case LOADING:
				str = pl.config.serverStatusLoading.replace("%C", pl.gameManager.server.getChunkloaderProgress());

				break;
			case WAITING:
				str = pl.config.serverStatusWaiting;
				break;
			case WAITING_STARTING:
				str = pl.config.serverStatusCountdown;
				break;
			case STARTING:
				str = pl.config.serverStatusStarting;
				break;
			case INGAME:
				str = pl.config.serverStatusInGame;
				break;
			case FINISHED:
				str = pl.config.serverStatusFinished;
				break;
			default:
				str = pl.config.serverStatusDisabled;
				break;
			}
		} else
			str = pl.config.serverMotd;
		str = F.strReplaceMatch(str, "%0|" + pl.config.serverID, "%1|" + pl.status, "%2|" + pl.PLAYERS.size(), "%3|" + pl.gameManager.inGamePlayers.size());
		e.setMotd(str.replace("{N}", "\n"));
	}

	// ---------------------------------------------------------------------------

	private void chat(String f, String p, String msg) {
		pl.PLAYERS.values().forEach(e -> e.player.sendMessage(F.strReplace(f, p, msg)));
	}
}
