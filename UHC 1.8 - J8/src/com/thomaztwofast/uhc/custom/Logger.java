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

package com.thomaztwofast.uhc.custom;

import com.thomaztwofast.uhc.Main;

public class Logger {
	private String px;

	public Logger(Main main) {
		px = "[" + main.getDescription().getName() + "] ";
	}

	/**
	 * Log > Info
	 */
	public void info(String m) {
		System.out.println(px + m);
	}

	/**
	 * Log > Error
	 */
	public void warn(String m) {
		System.err.println("\u001B[31m" + px + m + "\u001B[0m");
	}

	/**
	 * Log > Debug
	 */
	public void debug(String m) {
		System.out.println("\u001B[35m" + px + "[DEBUG] " + m + "\u001B[0m");
	}
}