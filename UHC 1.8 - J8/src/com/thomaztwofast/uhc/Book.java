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

package com.thomaztwofast.uhc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.thomaztwofast.uhc.lib.F;

public class Book {
	private Main pl;
	public ItemStack itemStack;

	public Book(Main pl) {
		this.pl = pl;
	}

	public void load() {
		itemStack = F.item(Material.WRITTEN_BOOK, pl.config.boolTitle, pl.config.bookLore);
		BookMeta meta = (BookMeta) itemStack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.setAuthor(pl.getDescription().getName());
		meta.setTitle(pl.config.boolTitle);
		meta.setPages(pl.config.bookPages);
		itemStack.setItemMeta(meta);
	}
}
