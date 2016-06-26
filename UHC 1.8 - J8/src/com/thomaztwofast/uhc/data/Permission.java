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

package com.thomaztwofast.uhc.data;

public enum Permission {
	ERROR("perm.login"),
	GLOBAL_COMMAND("perm.global.commands"),
	SELECTTEAM_ALL("commands.selectteam.admin"),
	UHC("commands.uhc");

	private String pA;

	private Permission(String a) {
		pA = a;
	}

	@Override
	public String toString() {
		return "com.thomaztwofast.uhc." + pA;
	}
}
