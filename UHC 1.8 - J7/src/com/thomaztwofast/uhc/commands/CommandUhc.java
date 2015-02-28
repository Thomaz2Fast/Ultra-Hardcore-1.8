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

package com.thomaztwofast.uhc.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;
import com.thomaztwofast.uhc.EnumGame;
import com.thomaztwofast.uhc.Function;
import com.thomaztwofast.uhc.Gui;
import com.thomaztwofast.uhc.Main;

public class CommandUhc implements CommandExecutor, TabCompleter {
	private List<String> tab = ImmutableList.of("menu", "settings", "status");
	private Main pl;

	public CommandUhc(Main main) {
		this.pl = main;
	}

	/**
	 * Command: Uhc
	 * 
	 * @param sender = CommandSender
	 * @param cmd = Command
	 * @param label = CommandText
	 * @param args = Args
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/** From Console **/
		if (sender instanceof Player == false) {
			if (args.length == 0) {
				sender.sendMessage(uhcConsole());
				return true;
			} else {
				switch (args[0].toLowerCase()) {
				case "settings":
					sender.sendMessage(uhcSettings(1, null));
					return true;
				case "status":
					sender.sendMessage(uhcStatus(1));
					return true;
				default:
					sender.sendMessage(uhcConsole());
					return true;
				}
			}
		} else {
			/** From Player **/
			if (args.length == 0) {
				uhcPlayer(sender);
				return true;
			} else {
				switch (args[0].toLowerCase()) {
				case "menu":
					if (pl.plMode) {
						if (pl.gmStat == EnumGame.WAITHING) {
							Gui.givePlayerGuiItem(((Player) sender));
							return true;
						} else {
							sender.sendMessage("§9Menu>§7 Disabled!");
							return true;
						}
					} else {
						CraftPlayer cp = (CraftPlayer) sender;
						IChatBaseComponent icbc = ChatSerializer.a("[{text: '§9Menu>'},{text: '§7 Disabled!', hoverEvent: {action: 'show_text', value: {text: '', extra: [{text: '§9§lHelp?\n\n§7How to enable this command?\n§7Open §econfig.yml§7 file to this plugin\n§7and change the \"Plugin Mode\" => \"true\"'}]}}}]");
						cp.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(icbc));
						return true;
					}
				case "settings":
					if (args.length == 2) {
						sender.sendMessage(uhcSettings(0, args[1]));
						return true;
					} else {
						sender.sendMessage(uhcSettings(0, "1"));
						return true;
					}
				case "status":
					sender.sendMessage(uhcStatus(0));
					return true;
				default:
					uhcPlayer(sender);
					return true;
				}
			}
		}
	}

	/**
	 * <b>~ Uhc Auto Tab Completer ~</b>
	 * <p>
	 * Show all matches tab args for specific command args.
	 * </p>
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			return StringUtil.copyPartialMatches(args[0], tab, new ArrayList<String>(tab.size()));
		}
		return null;
	}

	/**
	 * Show info about Uhc help center / plugin info. (Only Console)
	 * 
	 * @return Text
	 */
	private String uhcConsole() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n§8§m--------------------------------------------§r");
		sb.append("\n §lULTRA HARDCORE§r\n \n");
		sb.append(" §6Commands:§r\n");
		if (pl.plMode) {
			if (pl.tmMode) {
				sb.append(" §a/AutoTeam:§7 " + pl.getCommand("autoteam").getDescription() + "\n");
				sb.append(" §a/SelectTeam:§7 " + pl.getCommand("selectteam").getDescription() + "\n");
			}
			sb.append(" §a/Start:§7 " + pl.getCommand("start").getDescription() + "\n \n");
		} else {
			sb.append(" §a/ChunkLoader:§7 " + pl.getCommand("chunkloader").getDescription() + "§r\n \n");
		}
		sb.append(" §6Plugin:§r\n");
		sb.append(" §aVersion: §e" + pl.getDescription().getVersion() + "§r\n");
		sb.append(" §aAuthor: §e" + authorList() + "§r\n");
		sb.append("§8§m--------------------------------------------§r");
		return sb.toString();
	}

	/**
	 * Show info about Uhc help center / plugin info. (Only for ingame player)
	 * 
	 * If player type /uhc
	 * 
	 * @param s = Command Sender / Player
	 */
	private void uhcPlayer(CommandSender s) {
		CraftPlayer cp = (CraftPlayer) s;
		StringBuilder sb = new StringBuilder();
		sb.append("[{text: '§8§m--------------------------------------------\n§l ULTRA HARDCORE\n\n §6Commands:\n");
		if (pl.plMode) {
			if (pl.tmMode) {
				sb.append(" §a/AutoTeam:§7 " + pl.getCommand("autoteam").getDescription() + "\n");
				sb.append(" §a/SelectTeam:§7 " + pl.getCommand("selectteam").getDescription() + "\n");
			}
			sb.append(" §a/Start:§7 " + pl.getCommand("start").getDescription() + "\n\n");
		} else {
			sb.append(" §a/ChunkLoader:§7 " + pl.getCommand("chunkloader").getDescription() + "\n\n");
		}
		sb.append(" §6Plugin:\n");
		sb.append(" §aVersion:§e " + pl.getDescription().getVersion() + "\n");
		sb.append(" §aAuthor: '},{text: '§e" + authorList() + "', hoverEvent: {action: 'show_text', value: {text: '', extra:[{text: '§8§oPN7913.P6WP9M'}]}}},");
		sb.append("{text: '\n§8§m--------------------------------------------'}]");
		cp.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(sb.toString())));
	}

	/**
	 * Get info about settings of the game.
	 * 
	 * @param i = (1 = Console | 0 = Player)
	 * @param args
	 * @return Text
	 */
	private String uhcSettings(int i, String ii) {
		StringBuilder sb = new StringBuilder();
		if (i == 1) {
			sb.append(" \n§8§m--------------------------------------------§r");
			sb.append("\n §lULTRA HARDCORE - SETTINGS§r\n \n");
			sb.append(" §6World:§r\n");
			sb.append(" §aSun time:§7 " + pl.woSunTime + "§r\n");
			sb.append(" §aDifficulty:§7 " + (pl.woDiff == 1 ? "Easy" : (pl.woDiff == 2 ? "Normal" : "Hard")) + "§r\n");
			sb.append(" §aArena radius size:§7 " + pl.woArenaSize + " blocks from 0,0§r\n \n");
			sb.append(" §6World Border:§r\n");
			sb.append(" §aStart position:§7 " + (pl.wbStartPos / 2) + " blocks§r\n");
			if (pl.wbTime != 0) {
				sb.append(" §aStop position:§7 " + (pl.wbEndPos / 2) + " blocks§r\n");
				sb.append(" §aShrinks time:§7 " + Function.getTimeFormat(pl.wbTime + "000") + "§r\n");
			}
			if (pl.moMarkTime != 0) {
				sb.append(" \n §6Marker:§r\n");
				if (pl.moMarkMeg.isEmpty()) {
					sb.append(" §aTime delay:§7 " + pl.moMarkTime + " min§r\n \n");
				} else {
					sb.append(" §aMessage:§7 " + pl.moMarkMeg + "§r\n");
					sb.append(" §aTime delay:§7 " + pl.moMarkTime + " min§r\n");
				}
			}
			if (pl.tmMode) {
				sb.append(" \n §6Teams:§r\n");
				sb.append(" §aMax players in teams:§7 " + pl.tmMaxPlayer + "§r\n");
				sb.append(" §aFriendly fire:§7 " + ((pl.tmrFF) ? "On" : "Off") + "§r\n");
				sb.append(" §aSee friendly invisibles:§7 " + ((pl.tmrSFI) ? "On" : "Off") + "§r\n");
			}
			sb.append(" \n §6Disconnected Ingame Players:§r\n");
			sb.append(" §aMax disconnected timeout:§7 " + pl.okMaxTime + " min§r\n");
			sb.append(" §aMessage:§7 " + pl.okMsg + "§r\n \n");
			sb.append(" §6Freezing Starting Players:§r\n");
			sb.append(" §aEnabled:§7 " + ((pl.fzp) ? "On" : "Off") + "§r\n");
			sb.append(" §aRadius size:§7 " + pl.fzSize + " blocks§r\n \n");
			sb.append(" §6Gamerule:§r\n");
			sb.append(" §aNatural Regen:§7 Off§r\n");
			if (pl.grList.size() != 0) {
				for (String grs : pl.grList) {
					String[] gr = grs.split("\\|");
					if (gr[1].toString().equalsIgnoreCase("true") || gr[1].toString().equalsIgnoreCase("false")) {
						if (Function.gameruleReplace(gr[0]).equalsIgnoreCase("Eternal day")) {
							sb.append(" §a" + Function.gameruleReplace(gr[0]) + ":§7 " + (gr[1].toString().equalsIgnoreCase("true") ? "Off" : "On") + "§r\n");
						} else {
							sb.append(" §a" + Function.gameruleReplace(gr[0]) + ":§7 " + (gr[1].toString().equalsIgnoreCase("true") ? "On" : "Off") + "§r\n");
						}
					} else {
						sb.append(" §a" + Function.gameruleReplace(gr[0]) + ":§7 " + gr[1] + "§r\n");
					}
				}
			}
			sb.append(" \n §6Other:§r\n");
			sb.append(" §aDamage logger:§7 " + ((pl.dmgLog) ? "On" : "Off") + "§r\n");
			sb.append("§8§m--------------------------------------------§r");
		} else {
			int a = 1;
			ii = ii.replaceAll("[\\D+]", "");
			if (!ii.isEmpty()) {
				if (!(ii.equalsIgnoreCase("0")) && ii.length() < 2) {
					a = Integer.parseInt(ii);
				}
			}
			if (a > 3) {
				a = 3;
			}
			sb.append("§8§m--------------------------------------------§r");
			sb.append("\n §lULTRA HARDCORE - SETTINGS §8§l[§a§l" + a + " - 3§8§l]§r\n \n");
			if (a == 1) {
				sb.append(" §6World:§r\n");
				sb.append(" §aSun time:§7 " + pl.woSunTime + "§r\n");
				sb.append(" §aDifficulty:§7 " + (pl.woDiff == 1 ? "Easy" : (pl.woDiff == 2 ? "Normal" : "Hard")) + "§r\n");
				sb.append(" §aArena radius size:§7 " + pl.woArenaSize + " blocks from 0,0§r\n \n");
				sb.append(" §6World Border:§r\n");
				sb.append(" §aStart position:§7 " + (pl.wbStartPos / 2) + " blocks§r\n");
				if (pl.wbTime != 0) {
					sb.append(" §aStop position:§7 " + (pl.wbEndPos / 2) + " blocks§r\n");
					sb.append(" §aShrinks time:§7 " + Function.getTimeFormat(pl.wbTime + "000") + "§r\n");
				}
				if (pl.moMarkTime != 0) {
					sb.append(" \n §6Marker:§r\n");
					if (pl.moMarkMeg.isEmpty()) {
						sb.append(" §aTime delay:§7 " + pl.moMarkTime + " min§r\n \n");
					} else {
						sb.append(" §aMessage:§7 " + pl.moMarkMeg + "§r\n");
						sb.append(" §aTime delay:§7 " + pl.moMarkTime + " min§r\n");
					}
				}
			} else if (a == 2) {
				if (pl.tmMode) {
					sb.append(" §6Teams:§r\n");
					sb.append(" §aMax players in teams:§7 " + pl.tmMaxPlayer + "§r\n");
					sb.append(" §aFriendly fire:§7 " + ((pl.tmrFF) ? "On" : "Off") + "§r\n");
					sb.append(" §aSee friendly invisibles:§7 " + ((pl.tmrSFI) ? "On" : "Off") + "§r\n \n");
				}
				sb.append(" §6Disconnected Ingame Players:§r\n");
				sb.append(" §aMax disconnected timeout:§7 " + pl.okMaxTime + " min§r\n");
				sb.append(" §aMessage:§r " + pl.okMsg + "§r\n \n");
				sb.append(" §6Freezing Starting Players:§r\n");
				sb.append(" §aEnabled:§7 " + ((pl.fzp) ? "On" : "Off") + "§r\n");
				sb.append(" §aRadius size:§7 " + pl.fzSize + " blocks§r\n");
			} else {
				sb.append(" §6Gamerule:§r\n");
				sb.append(" §aNatural Regen:§7 Off§r\n");
				if (pl.grList.size() != 0) {
					for (String grs : pl.grList) {
						String[] gr = grs.split("\\|");
						if (gr[1].toString().equalsIgnoreCase("true") || gr[1].toString().equalsIgnoreCase("false")) {
							if (Function.gameruleReplace(gr[0]).equalsIgnoreCase("Eternal day")) {
								sb.append(" §a" + Function.gameruleReplace(gr[0]) + ":§7 " + (gr[1].toString().equalsIgnoreCase("true") ? "Off" : "On") + "§r\n");
							} else {
								sb.append(" §a" + Function.gameruleReplace(gr[0]) + ":§7 " + (gr[1].toString().equalsIgnoreCase("true") ? "On" : "Off") + "§r\n");
							}
						} else {
							sb.append(" §a" + Function.gameruleReplace(gr[0]) + ":§7 " + gr[1] + "§r\n");
						}
					}
				}
				sb.append(" \n §6Other:§r\n");
				sb.append(" §aDamage logger:§7 " + ((pl.dmgLog) ? "On" : "Off") + "§r\n");
			}
			sb.append("§8§m--------------------------------------------§r");
		}
		return sb.toString();
	}

	/**
	 * Get status about this plugin / Ultra Hardcore game.
	 * 
	 * @param i = (1 = Console | 0 = Player)
	 * @return Text
	 */
	private String uhcStatus(int i) {
		StringBuilder sb = new StringBuilder();
		sb.append(((i == 1) ? " \n" : "") + "§8§m--------------------------------------------§r");
		sb.append("\n §lULTRA HARDCORE - STATUS§r\n \n");
		sb.append(" §aPlugin:§7 " + ((pl.plMode) ? "Enabled" : "Disabled") + "§r\n");
		if (pl.plMode) {
			sb.append(" §aGame Status:§7 " + pl.gmStat.getGameStatusName() + "§r\n");
			sb.append(" §aGame Mode:§7 " + ((pl.tmMode) ? "Team" : "Solo") + " Mode§r\n");
		}
		if (pl.gmStat == EnumGame.STARTING || pl.gmStat == EnumGame.INGAME) {
			sb.append(" \n §6InGame Status:§r\n");
			if (pl.tmMode) {
				sb.append(" §aAlive Teams:§7 " + pl.igTms.size() + "§r\n");
			}
			sb.append(" §aAlive Players:§7 " + pl.igPs.size() + "§r\n");
			if (pl.igOffPs.size() != 0) {
				sb.append(" §aDisconnected Players:§7 " + pl.igOffPs.size() + "§r\n");
			}

		}
		sb.append("§8§m--------------------------------------------§r");
		return sb.toString();
	}

	/**
	 * Get Author list from the plugin.
	 * 
	 * @return Author (Text)
	 */
	private String authorList() {
		String as = "";
		for (String a : pl.getDescription().getAuthors()) {
			if (as.isEmpty()) {
				as = a;
			} else {
				as += ", " + a;
			}
		}
		return as;
	}
}