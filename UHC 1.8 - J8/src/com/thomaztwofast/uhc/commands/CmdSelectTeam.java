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

package com.thomaztwofast.uhc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.Node;
import com.thomaztwofast.uhc.data.UHCPlayer;
import com.thomaztwofast.uhc.lib.F;
import com.thomaztwofast.uhc.lib.J;

public class CmdSelectTeam implements CommandExecutor {
	private Main pl;

	public CmdSelectTeam(Main pl) {
		this.pl = pl;
	}

	/*
	 * Command - - - - - - - - - - > - SelectTeam
	 * Access  - - - - - - - - - - > - Private Mode
	 * Who can use it  - - - - - - > - Players
	 * Arguments - - - - - - - - - > - None  
	 * 
	 * Default Permission  - - - - > - Everyone
	 * Permission Node - - - - - - > - com.thomaztwofast.uhc.commands.selectteam       [ See plugin.yml ]
	 * Special Permission Node - - > - com.thomaztwofast.uhc.commands.selectteam.admin [ See Node.java @ TEAM ]
	 * 
	 * ----------------------------
	 * 
	 * This command will give the player a item that player can select a team.
	 * If special permission player trigger this command all the player will get a item.
	 * 
	 * The item will only go to that player don't have the item on they inventory.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			UHCPlayer u = pl.getRegisterPlayer(sender.getName());
			if (pl.config.pluginEnable && pl.config.gameInTeam && !pl.config.serverEnable && pl.status.ordinal() <= 5) {
				if (u.hasNode(Node.TEAM)) {
					pl.PLAYERS.values().forEach(e -> setItem(e));
					pl.gameManager.teams.isSelectItem = true;
					return true;
				}
				setItem(u);
				return true;
			}
			J str = new J("SelectTeam> ", "8", "bi");
			if (u.hasNode(Node.UHC))
				str.addTextWithCmd("Disabled!", "7", "i", "/uhc help page 2", F.chatTitle("Help Information", "Click here to find out how to enable this command?"));
			else
				str.addText("Disabled!", "7", "i");
			u.sendJsonMessage(str.print());
			return true;
		}
		pl.log(0, "Command 'SelectTeam' can only execute from player.");
		return true;
	}

	// ---------------------------------------------------------------------------

	private void setItem(UHCPlayer u) {
		if (!u.player.getInventory().contains(pl.gameManager.teams.selectItem)) {
			u.player.getInventory().setItem(pl.config.gameSelectTeamInventory, pl.gameManager.teams.selectItem);
			u.sendCmdMessage("SelectTeam", "Right click on the \u00A7E\u00A7Opaper item\u00A77\u00A7O to select team.");
		}
	}
}
