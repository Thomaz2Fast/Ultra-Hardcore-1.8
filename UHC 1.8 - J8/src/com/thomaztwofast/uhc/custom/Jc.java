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

import java.util.ArrayList;
import java.util.List;

public class Jc {
	private List<String> jA = new ArrayList<>();
	private String[] jB = { "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow" };
	private String[] jC = { "bold", "italic", "underlined", "strikethrough", "obfuscated" };

	public void add(String a, int[] b, int c, String d, String e) {
		if (a == null) {
			return;
		}
		StringBuilder f = new StringBuilder();
		f.append("{\"text\":\"" + a + "\"");
		if (b != null) {
			for (int g = 0; g < b.length; g++) {
				if (b[g] < jC.length) {
					f.append(",\"" + jC[b[g]] + "\":true");
				}
			}
		}
		f.append(",\"color\":");
		if (c >= 0 && c <= 14) {
			f.append("\"" + jB[c] + "\"");
		} else {
			f.append("\"white\"");
		}
		if (d != null) {
			String[] h = d.split("\\|");
			f.append(",\"clickEvent\":{\"action\":\"");
			switch (h[0].hashCode()) {
			case 48:
				f.append("open_url");
				break;
			case 49:
				f.append("open_file");
				break;
			case 50:
				f.append("run_command");
				break;
			default:
				f.append("suggest_command");
				break;
			}
			f.append("\",\"value\":\"" + h[1] + "\"}");
		}
		if (e != null) {
			f.append(",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + e + "\"}");
		}
		f.append("}");
		jA.add(f.toString());
	}

	public String o() {
		StringBuilder a = new StringBuilder();
		a.append("[\"\",");
		for (int b = 0; b < jA.size(); b++) {
			a.append(jA.get(b));
			if (jA.size() != (b + 1)) {
				a.append(",");
			}
		}
		a.append("]");
		return a.toString();
	}
}
