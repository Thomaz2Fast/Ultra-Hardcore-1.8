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

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.thomaztwofast.uhc.Function;
import com.thomaztwofast.uhc.Main;

public class UhcDisabledEvent implements Listener {
	private Main pl;

	public UhcDisabledEvent(Main main) {
		this.pl = main;
	}

	/**
	 * ~ Player Join ~
	 * <p>
	 * When OP player join the game, that player will get a message at Ultra Hardcore 1.8 is
	 * disabled. This event only run when 'pluginMode' is false.
	 * </p>
	 * 
	 * @param e = PlayerJoinEvent (Player)
	 */
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e) {
		if (pl.tlhf) {
			Function.updateTabListHeaderFooter(e.getPlayer(), pl.tlh, pl.tlf);
		}
		if (e.getPlayer().isOp()) {
			CraftPlayer cp = (CraftPlayer) e.getPlayer();
			IChatBaseComponent icbc = ChatSerializer.a("[{text: '§9UHC>'},{text: '§7 Ultra Hardcore is disabled.', hoverEvent: {action: 'show_text', value: {text: '', extra: [{text: '§9§lHelp?\n\n§7How to enable this plugin?\n§7Open §econfig.yml§7 file to this plugin\n§7and change the \"Plugin Mode\" => \"true\"'}]}}}]");
			cp.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(icbc));
		}
	}
}