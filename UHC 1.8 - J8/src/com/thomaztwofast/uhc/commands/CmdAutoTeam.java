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

package com.thomaztwofast.uhc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.JChat;
import com.thomaztwofast.uhc.custom.Perm;
import com.thomaztwofast.uhc.data.PlayerData;

public class CmdAutoTeam implements CommandExecutor {
	private Main pl;

	public CmdAutoTeam(Main main) {
		pl = main;
	}

	/**
	 * Command				>	AutoTeam
	 * Enabled Console		>	No
	 * Default Permission	>	OP
	 * 
	 * Description			>	Set all registered players to a random team selection.
	 */
	@Override
	public boolean onCommand(CommandSender send, Command cmd, String lab, String[] arg) {
		if (send instanceof Player) {
			PlayerData p = pl.getRegPlayer(((Player) send).getUniqueId());
			if (pl.getPlConf().pl_Enabled() & pl.getPlConf().g_teamMode() & !pl.getPlConf().server()) {
				if (pl.getGame().getStatus().getStat().getLvl() <= 5) {
					pl.getGame().getTeam().cmdAutoTeam();
					p.sendMessage("AutoTeam", "Done.");
					return true;
				}
			}
			JChat ic = new JChat();
			ic.add("AutoTeam> ", null, 9, null, null);
			if (pl.getPlFun().hasPermission(p.cp, Perm.UHC)) {
				ic.add("Disabled!", null, 7, "2|/uhc help page 2", "§6§lHelp Information\n§7Click here to find out how to\n§7enable this command?");
			} else {
				ic.add("Disabled!", null, 7, null, null);
			}
			p.sendRawICMessage(ic.a());
			return true;
		}
		pl.getPlLog().info("Only ingame player can use this command.");
		return true;
	}
}