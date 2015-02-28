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

import java.util.HashMap;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.thomaztwofast.uhc.EnumGame;
import com.thomaztwofast.uhc.Main;

public class CommandSelectTeam implements CommandExecutor {
	private Main pl;

	public CommandSelectTeam(Main main) {
		this.pl = main;
	}

	/**
	 * <b>~ Command: /selectteam ~</b>
	 * <p>
	 * When this command get trigger, it will give all player on the server a item, that thay<br>
	 * can right click on it and can select what team thay want to be in.
	 * </p>
	 * 
	 * @param sender = Get the player how trigger the command.
	 * @param cmd = Get the command that player type in.
	 * @param label = Get the command in string format.
	 * @param args = Get all argument that player type in the command.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player == false) {
			pl.getLogger().info("Only ingame player can use this command.");
			return true;
		} else {
			if (pl.gmStat == EnumGame.WAITHING && pl.tmMode) {
				pl.tmsST = true;
				HashMap<ItemStack, Integer> item = pl.itemStore.get("selectteam");
				for (Player p : pl.getServer().getOnlinePlayers()) {
					p.getInventory().setItem(item.get(item.keySet().iterator().next()), item.keySet().iterator().next());
					p.sendMessage("§9SelectTeam>§7 Right click on paper to select team.");
				}
				return true;
			} else {
				CraftPlayer cp = (CraftPlayer) sender;
				IChatBaseComponent icbc = ChatSerializer.a("[{text: '§9SelectTeam>'},{text: '§7 Disabled!', hoverEvent: {action: 'show_text', value: {text: '', extra: [{text: '§9§lHelp?\n\n§7How to enable this command?\n§7Open §econfig.yml§7 file to this plugin\n§7and change the \"Plugin Mode\" => \"true\"\n§7and \"Team Mode\" => \"true\"'}]}}}]");
				cp.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(icbc));
				return true;
			}
		}
	}
}