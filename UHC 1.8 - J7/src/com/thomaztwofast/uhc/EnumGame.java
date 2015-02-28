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

package com.thomaztwofast.uhc;

public enum EnumGame {
	DISABLED("Disabled"), FAILED("Failed"), WAITHING("Waithing"), STARTING("Starting"), INGAME("InGame"), FINISH("Finish");

	private final String s;

	EnumGame(String GameStatus) {
		this.s = GameStatus;
	}

	/**
	 * ~ Getting Game Status ~
	 * 
	 * @return <b>[String]</b> Get GameStatus in string format.
	 */
	public String getGameStatusName() {
		return s;
	}
}