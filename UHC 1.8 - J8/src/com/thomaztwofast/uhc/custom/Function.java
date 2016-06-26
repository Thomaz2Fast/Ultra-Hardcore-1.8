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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Function {

	// ------:- PUBLIC | TIME FORMAT -:-------------------------------------------------------------

	public String asClock(long a) {
		StringBuilder b = new StringBuilder();
		long[] c = { 3600, 60, 0, 0 };
		if (a >= c[0]) {
			c[2] = Math.round(a / c[0]);
			a -= (c[2] * c[0]);
		}
		if (a >= c[1]) {
			c[3] = Math.round(a / c[1]);
			a -= (c[3] * c[1]);
		}
		if (c[2] != 0) {
			b.append(c[2] + "h ");
		}
		if (c[2] != 0 | c[3] != 0) {
			b.append((c[3] < 10 ? "0" : "") + c[3] + "m ");
		}
		b.append((a < 10 ? "0" : "") + a + "s");
		return b.toString();
	}

	public String asRealClock(double a) {
		StringBuilder b = new StringBuilder();
		double[] c = { 1000.0, 16.667, 0.278, 0.0, 0.0, 0.0 };
		c[3] = Math.floor(a / c[0]);
		a -= Math.floor(c[3] * c[0]);
		if (a != 0) {
			c[4] = Math.floor(a / c[1]);
			a -= (c[4] * c[1]);
			if (a != 0) {
				c[5] = Math.floor(a / c[2]);
			}
		}
		c[3] += 6;
		if (c[3] > 23) {
			c[3] -= 24;
		}
		b.append((c[3] < 10 ? "0" : "") + (int) c[3] + "h ");
		b.append((c[4] < 10 ? "0" : "") + (int) c[4] + "m ");
		b.append((c[5] < 10 ? "0" : "") + (int) c[5] + "s");
		return b.toString();
	}

	// ------:- PUBLIC | MC COLOR -:----------------------------------------------------------------

	public String color(String a) {
		return a.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
	}

	public String colorLn(String a) {
		return color(a).replace("{N}", "\n");
	}

	public String colorRemove(String a) {
		return a.replaceAll("(\u00A7([a-fk-or0-9]))", "");
	}

	// ------:- PUBLIC | ITEM CREATER -:------------------------------------------------------------

	public ItemStack nItem(Material a, int b, String c, String... d) {
		ItemStack e = new ItemStack(a, 1, (byte) b);
		if (c != null && c.length() != 0) {
			ItemMeta f = e.getItemMeta();
			f.setDisplayName(c);
			if (d.length != 0 && d[0].length() != 0) {
				List<String> g = new ArrayList<>();
				for (String h : d) {
					if (h.length() != 0) {
						String[] i = h.split("\\|", -1);
						switch (i[0].hashCode()) {
						case 49:
							g.add("\u00A78" + i[1]);
							break;
						case 50:
							g.add("");
							g.add("\u00A77Status:\u00A7a " + i[1]);
							break;
						case 51:
							g.add("");
							g.add("\u00A77Value:\u00A7a " + i[1]);
							break;
						default:
							g.add("\u00A77" + i[1]);
							break;
						}
					}
				}
				f.setLore(g);
			}
			e.setItemMeta(f);
		}
		return e;
	}

	// ------:- PUBLIC | STATS OUTPUT /W COLOR OR NOT -:--------------------------------------------

	public String stat(boolean a) {
		return (a ? "On" : "Off");
	}

	public String statC(boolean a) {
		return (a ? "\u00A7aOn" : "\u00A7cOff");
	}

	public String teamTag(int a) {
		if (a == 0) {
			return "On";
		} else if (a == 1) {
			return "Hide Other Team";
		} else if (a == 2) {
			return "Hide Own Team";
		}
		return "Off";
	}

	public String teamTagC(int a) {
		if (a == 0) {
			return "\u00A7aOn";
		} else if (a == 1) {
			return "\u00A7eHide Other Team";
		} else if (a == 2) {
			return "\u00A7eHide Own Team";
		}
		return "\u00A7cOff";
	}

	public String teamCol(int a) {
		if (a == 0) {
			return "On";
		} else if (a == 1) {
			return "Push Own Team";
		} else if (a == 2) {
			return "Push Other Team";
		}
		return "Off";
	}

	public String teamColC(int a) {
		if (a == 0) {
			return "\u00A7aOn";
		} else if (a == 1) {
			return "\u00A7ePush Own Team";
		} else if (a == 2) {
			return "\u00A7ePush Other Team";
		}
		return "\u00A7cOff";
	}

	public String wDif(int a) {
		if (a == 1) {
			return "Easy";
		} else if (a == 2) {
			return "Normal";
		}
		return "Hard";
	}

	public String wDifC(int a) {
		if (a == 1) {
			return "\u00A7aEasy";
		} else if (a == 2) {
			return "\u00A7eNormal";
		}
		return "\u00A7cHard";
	}
}
