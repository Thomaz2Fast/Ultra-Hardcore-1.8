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

package com.thomaztwofast.uhc.lib;

public class S {
	private final String LINE = "\u00A78\u00A7M                                                                   \u00A7R";
	private final String CLINE = "\u00A78\u00A7M--------------------------------------------\u00A7R";
	private boolean bool = false;
	private StringBuilder sb = new StringBuilder();
	private String title;

	public S(boolean bool) {
		this.bool = bool;
	}

	public void addHeader(String str) {
		sb.append(" \n\u00A76 " + str + "\n");
	}

	public void addText(String str) {
		sb.append("\u00a7A " + str + "\n");
	}

	public void addText(String str, String str1) {
		sb.append("\u00a7A " + str + ":\u00a77 " + str1 + "\n");
	}

	public void setTitle(String title) {
		this.title = (bool ? "\n" + CLINE : LINE) + "\n\u00A7L " + title + "\n";
	}

	public String print() {
		return title + sb + (bool ? CLINE : LINE);
	}
}
