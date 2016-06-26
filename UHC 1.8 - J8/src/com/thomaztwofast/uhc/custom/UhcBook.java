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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.thomaztwofast.uhc.Main;

public class UhcBook extends Function {
	private Main uA;
	public ItemStack uB;

	public UhcBook(Main a) {
		uA = a;
	}

	public void load() {
		uB = nItem(Material.WRITTEN_BOOK, 0, uA.mC.cKc, uA.mC.cKd);
		BookMeta a = (BookMeta) uB.getItemMeta();
		a.setAuthor(uA.getDescription().getName());
		a.setTitle(uA.mC.cKc);
		if (uA.mC.cKe.length != 0) {
			a.setPages(uA.mC.cKe);
		}
		uB.setItemMeta(a);
	}
}
