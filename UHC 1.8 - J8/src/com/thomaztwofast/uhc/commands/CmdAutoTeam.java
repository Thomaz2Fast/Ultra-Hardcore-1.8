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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.custom.Jc;
import com.thomaztwofast.uhc.data.Permission;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class CmdAutoTeam implements CommandExecutor {
	private Main cA;
	private Scoreboard cB;

	public CmdAutoTeam(Main a) {
		cA = a;
		cB = a.getServer().getScoreboardManager().getMainScoreboard();
	}

	/**
	 * Command - - - - - - - - - - > - AutoTeam
	 * Enabled Console - - - - - - > - false
	 * Default Permission  - - - - > - OP
	 */
	@Override
	public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
		if (a instanceof Player) {
			UHCPlayer e = cA.mB.getPlayer(a.getName());
			if (cA.mC.cCa && cA.mC.cGa && !cA.mC.cFa) {
				if (cA.mA.i() <= 5) {
					a();
					e.sendCommandMessage("AutoTeam", "Done.");
					return true;
				}
			}
			Jc f = new Jc();
			f.add("AutoTeam> ", new int[] { 1 }, 8, null, null);
			if (e.uB.hasPermission(Permission.UHC.toString())) {
				f.add("Disabled!", new int[] { 1 }, 7, "2|/uhc help page 2", "\u00A76\u00A7lHelp Information\n\u00A77Click here to find out how to\n\u00A77enable this command?");
			} else {
				f.add("Disabled!", new int[] { 1 }, 7, null, null);
			}
			e.sendJsonMessage(f.o());
			return true;
		}
		cA.log(0, "Command '/AutoTeam' can only execute from ingame player.");
		return true;
	}

	// ------:- PRIVATE -:--------------------------------------------------------------------------

	private void a() {
		List<UHCPlayer> a = new ArrayList<>();
		List<Team> b = new ArrayList<>();
		List<Team> c = new ArrayList<>();
		Random d = new Random();
		int e = (int) Math.ceil(cA.mB.getAllPlayers().size() / (double) cA.mC.cGb);
		int f = 0;
		e = (e == 0 ? 1 : e > 16 ? 16 : e);
		a.addAll(cA.mB.getAllPlayers());
		for (String g : cA.mE.gD.uCa) {
			if (cB.getTeam(g) != null && cB.getTeam(g).getSize() != 0) {
				for (String h : cB.getTeam(g).getEntries()) {
					cB.getTeam(g).removeEntry(h);
				}
			}
			b.add(cB.getTeam(g));
		}
		for (int g = 0; g < e; g++) {
			Team h = b.get(d.nextInt(b.size()));
			c.add(h);
			b.remove(h);
		}
		while (a.size() != 0) {
			UHCPlayer h = a.get(d.nextInt(a.size()));
			cA.mE.gD.joinTeam(h, c.get(f).getName(), true);
			a.remove(h);
			if (f == (c.size() - 1)) {
				f = 0;
			} else {
				f++;
			}
		}
		cA.mE.gD.updateInv();
	}
}
