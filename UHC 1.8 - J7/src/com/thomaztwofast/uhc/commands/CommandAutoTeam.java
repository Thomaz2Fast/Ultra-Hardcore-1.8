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
import java.util.Random;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.thomaztwofast.uhc.EnumGame;
import com.thomaztwofast.uhc.Function;
import com.thomaztwofast.uhc.Main;

public class CommandAutoTeam implements CommandExecutor {
	private Main pl;
	private Random r = new Random();

	public CommandAutoTeam(Main main) {
		this.pl = main;
	}

	/**
	 * <b>~ Command: /autoteam ~</b>
	 * <p>
	 * When this command get trigger, it will remove all players from thay team and give them a
	 * new team, complete randomly selected player and selected team.
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
				Scoreboard sb = pl.getServer().getScoreboardManager().getMainScoreboard();
				ArrayList<Player> ps = new ArrayList<Player>();
				ArrayList<Team> atm = new ArrayList<Team>();
				ArrayList<Team> sts = new ArrayList<Team>();
				int mt = Math.round(pl.getServer().getOnlinePlayers().size() / pl.tmMaxPlayer);
				int a = 0;
				int s = 0;
				if (mt == 0) {
					mt = 1;
				}
				if (mt > 16) {
					mt = 16;
				}
				for (Player p : pl.getServer().getOnlinePlayers()) {
					ps.add(p);
				}
				for (Team t : sb.getTeams()) {
					if (t.getPlayers().size() != 0) {
						for (OfflinePlayer off : t.getPlayers()) {
							t.removePlayer(off);
						}
					}
					atm.add(t);
				}
				for (a = 0; a < mt; a++) {
					Team t = atm.get(r.nextInt(atm.size()));
					if (!sts.contains(t)) {
						sts.add(t);
						atm.remove(t);
					}
				}
				for (a = 0; a <= ps.size(); a++) {
					Player p = ps.get(r.nextInt(ps.size()));
					Function.joiningTeam(pl, p, sts.get(s).getName(), true);
					ps.remove(p);
					if (s == (sts.size() - 1)) {
						s = 0;
					} else {
						s++;
					}
				}
				sender.sendMessage("§9AutoTeam>§7 Completed!");
				return true;
			} else {
				CraftPlayer cp = (CraftPlayer) sender;
				IChatBaseComponent icbc = ChatSerializer.a("[{text: '§9AutoTeam>'},{text: '§7 Disabled!', hoverEvent: {action: 'show_text', value: {text: '', extra: [{text: '§9§lHelp?\n\n§7How to enable this command?\n§7Open §econfig.yml§7 file to this plugin\n§7and change the \"Plugin Mode\" => \"true\"\n§7and \"Team Mode\" => \"true\"'}]}}}]");
				cp.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(icbc));
				return true;
			}
		}
	}
}