/*
 * Ultra Hardcore 1.8, a Minecraft survival game mode.
 * Copyright (C) <2018> Thomaz2Fast
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

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.thomaztwofast.uhc.Main;

public class F {

	private F() {
	}

	public static String asRealClock(double time) {
		double[] arr = { 1000D, 16.667, 0.278, 0D, 0D, 0D };
		String str = "";
		arr[3] = Math.floor(time / arr[0]);
		time -= Math.floor(arr[3] * arr[0]);
		if (time != 0) {
			arr[4] = Math.floor(time / arr[1]);
			time -= arr[4] * arr[1];
			if (time != 0)
				arr[5] = time / arr[2];
		}
		arr[3] += 6;
		arr[3] = arr[3] > 23 ? arr[3] - 24 : arr[3];
		str += (arr[3] < 10 ? "0" : "") + (int) arr[3] + "h ";
		str += (arr[4] < 10 ? "0" : "") + (int) arr[4] + "m ";
		return str + (arr[5] < 10 ? "0" : "") + (int) arr[5] + "s";
	}

	public static String chatCmd(String str) {
		return "\u00A7E\u00A7L>\u00A7R\u00A7A /" + str;
	}

	public static String chatTitle(String title, String str) {
		return "\u00A76\u00A7L" + title + "\u00A7R\n\u00A77" + str + "\u00A7R";
	}

	public static String getTimeLeft(long time) {
		long[] arr = { 3600, 60, 0, 0 };
		String str;
		if (time >= arr[0]) {
			arr[2] = Math.round(time / arr[0]);
			time -= arr[2] * arr[0];
		}
		if (time >= arr[1]) {
			arr[3] = Math.round(time / arr[1]);
			time -= arr[3] * arr[1];
		}
		str = arr[2] != 0 ? arr[2] + "h " : "";
		str += arr[2] != 0 || arr[3] != 0 ? (arr[3] < 10 ? "0" : "") + arr[3] + "m " : "";
		return str + (time < 10 ? "0" : "") + time + "s";
	}

	public static String isDifficulty(int lvl, boolean color) {
		return (color ? "\u00A7" + (lvl == 1 ? "A" : lvl == 2 ? "E" : "C") : "") + (lvl == 1 ? "Easy" : lvl == 2 ? "Normal" : "Hard");
	}

	public static String isOn(boolean on, boolean color) {
		return (color ? "\u00A7" + (on ? "A" : "C") : "") + (on ? "On" : "Off");
	}

	public static String isTeamsOption(int lvl, boolean b, boolean color) {
		return (color ? "\u00A7" + (lvl == 0 ? "A" : lvl == 1 ? "C" : "E") : "") + (lvl == 0 ? "On" : lvl == 1 ? "Off" : (b ? "Push " : "Hide ") + (lvl == 2 ? "Other" : "Own") + " Teams");
	}

	public static ItemStack item(Material material, String title, String... arr) {
		ArrayList<String> lore = new ArrayList<>();
		ItemStack item = new ItemStack(material, 1);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.setDisplayName(mcColor(title));
		for (String str : arr) {
			String[] l = str.split("\\|", 2);
			l[1] = mcColor(l[1]);
			switch (Integer.parseInt(l[0])) {
			case 1:
			case 2:
				lore.addAll(Arrays.asList("", "\u00A77" + (l[0].equals("2") ? "Value" : "Status") + ":\u00A7a " + l[1]));
				break;

			default:
				lore.add(l[1]);
				break;
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static String mcColor(String s) {
		return s.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
	}

	public static String mcCodeLn(String s) {
		return mcColor(s).replace("{N}", "\n");
	}

	public static String mcColorRm(String s) {
		return s.replaceAll("(\u00A7([a-fk-or0-9]))", "");
	}

	public static ShapedRecipe recipe(Main pl, ItemStack item, String key, String shape, Object... arr) {
		ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(pl, key), item);
		recipe.shape(shape.split("\\|"));
		for (int i = 0; i < arr.length; i++) {
			recipe.setIngredient((char) arr[i], (Material) arr[i + 1]);
			i++;
		}
		return recipe;
	}

	public static String strReplace(String s, Object... a) {
		for (Object o : a) {
			s = s.replaceFirst("%0", o + "");
		}
		return s;
	}

	public static String strReplaceMatch(String str, String... a) {
		for (String s : a) {
			String[] l = s.split("\\|", 2);
			str = str.replaceFirst(l[0], l[1]);
		}
		return str;
	}
}
