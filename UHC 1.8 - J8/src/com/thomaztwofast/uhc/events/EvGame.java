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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
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
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.scoreboard.Team;
import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.GameStatus;
import com.thomaztwofast.uhc.data.Permission;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class EvGame implements Listener {
	private Main eA;

	public EvGame(Main a) {
		eA = a;
	}

	@EventHandler
	public void globalChat(AsyncPlayerChatEvent a) {
		if (eA.mC.cQa) {
			UHCPlayer b = eA.mB.getPlayer(a.getPlayer().getName());
			if (b.uB.getGameMode().equals(GameMode.SPECTATOR)) {
				if (eA.mC.cQc.length() != 0) {
					a.setFormat(eA.mC.cQc.replaceFirst("\\{0}", b.uB.getName()).replaceFirst("\\{1}", a.getMessage()));
					return;
				}
				a.setCancelled(true);
				return;
			}
			if (!a.isCancelled() && eA.mC.cGa) {
				if (b.uB.getScoreboard().getEntryTeam(b.uB.getName()) != null) {
					Team c = b.uB.getScoreboard().getEntryTeam(b.uB.getName());
					if (eA.mC.cQd.length() != 0) {
						if (eA.mC.cQe.length() != 0 && eA.mA.i() > 5 && a.getMessage().charAt(0) == '@') {
							for (String d : c.getEntries()) {
								UHCPlayer e = eA.mB.getPlayer(d);
								e.uB.sendMessage(eA.mC.cQe.replaceFirst("\\{0}", c.getPrefix() + b.uB.getName() + c.getSuffix()).replaceFirst("\\{1}", a.getMessage().substring(1)));
								if (b != e) {
									e.playLocalSound(Sound.BLOCK_NOTE_HARP, 1.7f);
								}
							}
							a.setCancelled(true);
							return;
						}
						a.setFormat(eA.mC.cQd.replaceFirst("\\{0}", c.getPrefix() + b.uB.getName() + c.getSuffix()).replaceFirst("\\{1}", a.getMessage()));
						return;
					}
					a.setCancelled(true);
				}
			}
			if (!a.isCancelled() && eA.mC.cQb.length() != 0) {
				a.setFormat(eA.mC.cQb.replaceFirst("\\{0}", b.uB.getName()).replaceFirst("\\{1}", a.getMessage()));
				return;
			}
			b.sendCommandMessage("Chat", "Disabled!");
			a.setCancelled(true);
		}
	}

	@EventHandler
	public void login(PlayerLoginEvent a) {
		if (eA.mC.cFa) {
			if (eA.mA.equals(GameStatus.ERROR)) {
				if (!a.getPlayer().hasPermission(Permission.ERROR.toString())) {
					a.disallow(Result.KICK_OTHER, eA.mC.cFq);
				}
				return;
			} else if (eA.mA.equals(GameStatus.DISABLED) || eA.mA.equals(GameStatus.LOADING)) {
				a.disallow(Result.KICK_OTHER, eA.mC.cFr);
			} else if (eA.mA.equals(GameStatus.FINISHED)) {
				a.disallow(Result.KICK_OTHER, eA.mC.cFs);
			}
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent a) {
		UHCPlayer b = eA.mB.addPlayer(eA, a.getPlayer());
		if (eA.mA.i() > 5) {
			if (eA.mA.equals(GameStatus.FINISHED) && eA.mE.getIngamePlayers().contains(b.uB.getName())) {
				return;
			} else if (eA.mE.isOffline(b.uB.getName())) {
				eA.mE.gmOfflineEndTimer(b.uB.getName());
				return;
			}
			b.resetPlayer(false);
			if (!eA.mA.equals(GameStatus.FINISHED)) {
				b.sendCommandMessage("UHC", "This game are in progress.");
			} else {
				b.sendCommandMessage("UHC", "This game is finished.");
			}
			b.sendCommandMessage("UHC", "You are now in spectator mode.");
			if (eA.mC.cFa) {
				if (b.uB.getWorld().equals(eA.mE.gC.uB)) {
					b.uB.teleport(eA.getServer().getWorlds().get(0).getSpawnLocation());
				}
				if (eA.mC.cFv) {
					b.uB.getInventory().setItem(eA.mC.cFy, eA.mE.gC.uG);
				}
			}
			return;
		}
		if (eA.mC.cFa) {
			eA.mE.gC.newPlayer(b);
			return;
		}
		b.resetPlayer(true);
		b.hubItems();
	}

	@EventHandler
	public void quit(PlayerQuitEvent a) {
		UHCPlayer b = eA.mB.getPlayer(a.getPlayer().getName());
		if (eA.mA.i() > 5 && eA.mA.i() < 8 && eA.mE.getIngamePlayers().contains(b.uB.getName())) {
			eA.mE.gmOfflineNewTimer(b.uB.getName());
		} else if (eA.mC.cFa && eA.mA.i() > 3 && eA.mA.i() < 6) {
			eA.mE.gC.removePlayer(b);
		}
		eA.mB.removePlayer(b.uB);
	}

	@EventHandler
	public void death(PlayerDeathEvent a) {
		if (eA.mA.equals(GameStatus.INGAME)) {
			UHCPlayer b = eA.mB.getPlayer(a.getEntity().getName());
			if (eA.mE.getIngamePlayers().contains(b.uB.getName())) {
				eA.mE.gmRemoveIgPlayer(b);
				if (eA.mC.cLa) {
					a.getDrops().add(eA.mE.getPlayerHead(b.uB.getName()));
				}
				return;
			}
		}
		a.setDeathMessage(null);
		a.setDroppedExp(0);
		a.getDrops().clear();
	}

	@EventHandler
	public void respawn(PlayerRespawnEvent a) {
		UHCPlayer b = eA.mB.getPlayer(a.getPlayer().getName());
		if (eA.mA.i() > 5) {
			if (eA.mE.getIngamePlayers().contains(b.uB.getName())) {
				a.setRespawnLocation(eA.mE.getSpawnLoc(b.uB));
			} else {
				if (eA.mC.cOa) {
					eA.mE.gF.givePlayerItem(b.uB.getName());
				}
				if (eA.mC.cFa && eA.mC.cFv) {
					b.uB.getInventory().setItem(eA.mC.cFy, eA.mE.gC.uG);
				}
				a.setRespawnLocation(new Location(eA.getServer().getWorlds().get(0), 0, 100, 0));
			}
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent a) {
		if (eA.mA.i() < 6) {
			if ((a.getAction().equals(Action.RIGHT_CLICK_AIR) || a.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && a.hasItem()) {
				if (a.getItem().equals(eA.mE.gB.uD)) {
					eA.mE.gB.openMenu(eA.mB.getPlayer(a.getPlayer().getName()));
					a.setCancelled(true);
					return;
				}
				if (eA.mC.cGa && a.getItem().equals(eA.mE.gD.uD)) {
					eA.mE.gD.openTeam(eA.mB.getPlayer(a.getPlayer().getName()));
					a.setCancelled(true);
					return;
				}
				if (eA.mC.cFa && eA.mC.cFv && a.getItem().equals(eA.mE.gC.uG)) {
					eA.mB.getPlayer(a.getPlayer().getName()).tpFallbackServer();
					a.setCancelled(true);
					return;
				}
			}
		} else if (eA.mA.equals(GameStatus.FINISHED) && (a.getAction().equals(Action.RIGHT_CLICK_AIR) || a.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && a.hasItem()) {
			if (eA.mC.cOa && a.getItem().getType().equals(Material.NETHER_STAR) && a.getItem().hasItemMeta()) {
				eA.mE.gF.openDmgIv(a.getPlayer().getName());
				a.setCancelled(true);
				return;
			}
			if (eA.mC.cFa && eA.mC.cFv && eA.mE.getIngamePlayers().contains(a.getPlayer().getName()) && a.getItem().equals(eA.mE.gC.uG)) {
				eA.mB.getPlayer(a.getPlayer().getName()).tpFallbackServer();
				a.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void invClick(InventoryClickEvent a) {
		if (eA.mA.i() < 6) {
			UHCPlayer b = eA.mB.getPlayer(a.getWhoClicked().getName());
			if (b.uC) {
				a.setCancelled(true);
				if (a.getRawSlot() >= 0 && a.getRawSlot() < a.getInventory().getSize()) {
					switch (b.uD[0].hashCode()) {
					case 2362719:
						eA.mE.gB.clickEvent(b, a.getClick(), a.getRawSlot());
						return;
					case 2570845:
						eA.mE.gD.clickEvent(b, a.getClick(), a.getRawSlot());
						return;
					}
				}
			}
		} else {
			if (eA.mC.cOa) {
				UHCPlayer b = eA.mB.getPlayer(a.getWhoClicked().getName());
				if (b.uC) {
					a.setCancelled(true);
					if (a.getRawSlot() >= 0 && a.getRawSlot() < a.getInventory().getSize()) {
						if (b.uD[0].equals("DMG")) {
							eA.mE.gF.clickEvent(b, a.getClick(), a.getRawSlot(), a.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE));
							return;
						}
					}
				}
				if (!eA.mE.getIngamePlayers().contains(b.uB.getName()) && a.getRawSlot() == 40 && a.getInventory().getType().equals(InventoryType.CRAFTING)) {
					eA.mE.gF.openDmgIv(b.uB.getName());
					return;
				}
			}
			if (eA.mC.cLa && a.getInventory().getType().equals(InventoryType.WORKBENCH) && a.getRawSlot() == 0 && a.getCurrentItem().getType().equals(Material.GOLDEN_APPLE)) {
				if (a.getInventory().getItem(5) != null && a.getInventory().getItem(5).getType().equals(Material.SKULL_ITEM)) {
					a.getCurrentItem().setItemMeta(eA.mE.setSkullOwner(a.getCurrentItem(), a.getInventory().getItem(5)));
					return;
				}
			}
			if (eA.mC.cFa && eA.mC.cFv && !eA.mE.getIngamePlayers().contains(a.getWhoClicked().getName()) && a.getInventory().getType().equals(InventoryType.CRAFTING) && a.getSlotType().equals(SlotType.QUICKBAR)) {
				if (a.getSlot() == eA.mC.cFy) {
					eA.mB.getPlayer(a.getWhoClicked().getName()).tpFallbackServer();
				}
			}
		}
	}

	@EventHandler
	public void invClose(InventoryCloseEvent a) {
		if (eA.mA.i() < 6) {
			eA.mB.getPlayer(a.getPlayer().getName()).uC = false;
		}
	}

	@EventHandler
	public void itemConsume(PlayerItemConsumeEvent a) {
		if (eA.mC.cCa && a.getItem().getType().equals(Material.GOLDEN_APPLE) && a.getItem().hasItemMeta()) {
			switch (a.getItem().getItemMeta().getDisplayName().hashCode()) {
			case 853026900:
				eA.mE.goldenAppleRegen(eA.mB.getPlayer(a.getPlayer().getName()), false);
				break;
			case -1130703500:
				eA.mE.goldenAppleRegen(eA.mB.getPlayer(a.getPlayer().getName()), true);
				break;
			}

		}
	}

	@EventHandler
	public void achievementAwarded(PlayerAchievementAwardedEvent a) {
		if (eA.mA.i() > 5 && eA.mA.i() < 8 && eA.mE.getIngamePlayers().contains(a.getPlayer().getName())) {
			return;
		}
		a.setCancelled(true);
	}

	@EventHandler
	public void itemDrop(PlayerDropItemEvent a) {
		if (eA.mA.i() < 6) {
			a.setCancelled(true);
		}
	}

	@EventHandler
	public void damager(EntityDamageEvent a) {
		if (eA.mC.cFa && eA.mA.i() > 3 && eA.mA.i() < 7 && a.getEntity() instanceof Player) {
			if (eA.mA.i() != 6 && a.getCause().equals(DamageCause.VOID)) {
				a.getEntity().teleport(a.getEntity().getWorld().getSpawnLocation().add(0.5, 1, 0.5), TeleportCause.PLUGIN);
				a.getEntity().setFallDistance(0f);
			}
			a.setCancelled(true);
			return;
		}
		if (eA.mC.cOa && eA.mA.i() == 7 && a.getEntity() instanceof Player) {
			UHCPlayer b = eA.mB.getPlayer(a.getEntity().getName());
			if (eA.mE.getIngamePlayers().contains(b.uB.getName())) {
				b.dmgStorage(eA.mE.gL, a.getDamage(), eA.mE.gF.source(a));
			}
		}
	}

	@EventHandler
	public void command(PlayerCommandPreprocessEvent a) {
		String b = a.getMessage().split(" ", 0)[0].substring(1).toLowerCase();
		UHCPlayer c = eA.mB.getPlayer(a.getPlayer().getName());
		if (eA.mA.i() > 5 && eA.mA.i() < 8 && c.uB.hasPermission(Permission.GLOBAL_COMMAND.toString())) {
			return;
		} else if ((eA.mA.i() < 6 || eA.mA.i() > 7) && ((eA.getCommand(b) != null && c.uB.hasPermission(eA.getCommand(b).getPermission())) || c.uB.hasPermission(Permission.GLOBAL_COMMAND.toString()))) {
			return;
		}
		c.sendCommandMessage("Command", "Disabled!");
		a.setCancelled(true);
	}

	@EventHandler
	public void listPing(ServerListPingEvent a) {
		if (eA.mC.cFa) {
			String b = "";
			if (eA.mC.cFb) {
				switch (eA.mA) {
				case ERROR:
					b = eA.mC.cFi;
					break;
				case FINISHED:
					b = eA.mC.cFp;
					break;
				case INGAME:
					b = eA.mC.cFo;
					break;
				case LOADING:
					b = eA.mC.cFj;
					break;
				case RESET:
					b = eA.mC.cFk;
					break;
				case STARTING:
					b = eA.mC.cFn;
					break;
				case WAITING:
					b = eA.mC.cFl;
					break;
				case WAITING_STARTING:
					b = eA.mC.cFm;
					break;
				default:
					b = eA.mC.cFh;
					break;
				}
			} else if (eA.mC.cFg.length() != 0) {
				b = eA.mC.cFg;
			}
			b = b.replaceFirst("\\{0}", "" + eA.mC.cFc);
			b = b.replaceFirst("\\{1}", eA.mA.toString());
			b = b.replaceFirst("\\{2}", "" + eA.mB.getAllPlayers().size());
			b = b.replaceFirst("\\{3}", "" + eA.mE.getIngamePlayers().size());
			b = b.replace("\\{N}", "\n");
			a.setMotd(b);
		}
	}
}
