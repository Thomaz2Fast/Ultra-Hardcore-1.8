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
import com.thomaztwofast.uhc.custom.Jc;
import com.thomaztwofast.uhc.data.Permission;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class CmdSelectTeam implements CommandExecutor {
	private Main cA;

	public CmdSelectTeam(Main a) {
		cA = a;
	}

	/**
	 * Command - - - - - - - - - - > - SelectTeam
	 * Enabled Console - - - - - - > - false
	 * Default Permission  - - - - > - Everyone
	 * Special Permission  - - - - > - OP | com.thomaztwofast.uhc.commands.selectteam.admin
	 */
	@Override
	public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
		if (a instanceof Player) {
			UHCPlayer e = cA.mB.getPlayer(a.getName());
			if (cA.mC.cCa && cA.mC.cGa && !cA.mC.cFa && cA.mA.i() <= 5) {
				if (e.uB.hasPermission(Permission.SELECTTEAM_ALL.toString())) {
					for (UHCPlayer f : cA.mB.getAllPlayers()) {
						getTeamSelectorItem(f);
					}
					cA.mE.gD.uCc = true;
					return true;
				}
				getTeamSelectorItem(e);
				return true;
			}
			Jc f = new Jc();
			f.add("SelectTeam> ", new int[] { 1 }, 8, null, null);
			if (e.uB.hasPermission(Permission.UHC.toString())) {
				f.add("Disabled!", new int[] { 1 }, 7, "2|/uhc help page 2", "\u00A76\u00A7lHelp Information\n\u00A77Click here to find out how to\n\u00A77enable this command?");
			} else {
				f.add("Disabled!", new int[] { 1 }, 7, null, null);
			}
			e.sendJsonMessage(f.o());
			return true;
		}
		cA.log(0, "Command '/SelectTeam' can only execute from ingame player.");
		return true;
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private void getTeamSelectorItem(UHCPlayer a) {
		if (!a.uB.getInventory().contains(cA.mE.gD.uD)) {
			a.uB.getInventory().setItem(cA.mC.cGc, cA.mE.gD.uD);
			a.sendCommandMessage("SelectTeam", "Right click on the \u00A7e\u00A7opaper item\u00A77\u00A7o to select team.");
		}
	}
}
