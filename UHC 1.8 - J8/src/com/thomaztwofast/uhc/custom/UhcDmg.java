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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;

import com.thomaztwofast.uhc.Main;
import com.thomaztwofast.uhc.data.UHCPlayer;

public class UhcDmg extends Function {
	private Main uA;
	private HashMap<String, HashMap<String, Inventory>> uB = new HashMap<>();

	public UhcDmg(Main a) {
		uA = a;
	}

	public String[] source(EntityDamageEvent a) {
		switch (a.getEventName().hashCode()) {
		case -438677650:
			return fEe((EntityDamageByEntityEvent) a);
		case 238032950:
			return fBe(a.getCause());
		default:
			return fE(a.getCause());
		}
	}

	// ------:- PUBLIC | INVENTORY -:---------------------------------------------------------------

	public void clickEvent(UHCPlayer a, ClickType b, int c, boolean d) {
		if (d && b.equals(ClickType.LEFT)) {
			if (c == 45) {
				int e = (Integer.valueOf(a.uD[1].replaceAll("[^0-9]", "")) - 1);
				a.uB.openInventory(uB.get(a.uB.getName()).get("IV" + e));
				a.setInvKey("DMG", "IV" + e);
				a.playLocalSound(Sound.UI_BUTTON_CLICK, 1f);
			} else if (c == 53) {
				int e = (Integer.valueOf(a.uD[1].replaceAll("[^0-9]", "")) + 1);
				a.uB.openInventory(uB.get(a.uB.getName()).get("IV" + e));
				a.setInvKey("DMG", "IV" + e);
				a.playLocalSound(Sound.UI_BUTTON_CLICK, 1f);
			}
		}
	}

	public void givePlayerItem(String a) {
		if (uA.mC.cOa && uA.mB.getPlayer(a) != null) {
			UHCPlayer b = uA.mB.getPlayer(a);
			if (new File(uA.getDataFolder(), "data/" + b.uB.getUniqueId() + ".yml").exists()) {
				b.sendCommandMessage("UHC", "Click on the \u00A7e\u00A7oNether Star\u00A77\u00A7o item to see how has damage you.");
				b.uB.getInventory().setItem(4, getItem());
			}
		}
	}

	public void openDmgIv(String a) {
		UHCPlayer b = uA.mB.getPlayer(a);
		if (uB.containsKey(a)) {
			if (b.uD[0].equals("DMG")) {
				b.uB.openInventory(uB.get(a).get(b.uD[1]));
				b.setInvKey("DMG", b.uD[1]);
			} else {
				b.uB.openInventory(uB.get(a).get("IV1"));
				b.setInvKey("DMG", "IV1");
			}
			return;
		}
		HashMap<String, Inventory> c = new HashMap<>();
		File d = new File(uA.getDataFolder(), "data/" + b.uB.getUniqueId() + ".yml");
		FileConfiguration e = YamlConfiguration.loadConfiguration(d);
		int f = (int) Math.ceil(e.getInt("i") / 28d);
		int g[] = { 0, 10, 0 };
		for (int h = 0; h < f; h++) {
			Inventory i = uA.getServer().createInventory(null, 54, "\u00A78DMG>" + a + ">P>\u00A7r " + (h + 1));
			for (int j = 0; j < 28; j++) {
				if (!e.isConfigurationSection("d." + g[0])) {
					break;
				}
				String[] k = { "", "", "" };
				k[0] = "0|\u00A7aFrom: \u00A7c" + e.getString("d." + g[0] + ".f");
				k[1] = (e.getString("d." + g[0] + ".i").length() != 0 ? "0|\u00A7aUsing: \u00A7c" + e.getString("d." + g[0] + ".i") : "");
				k[2] = "0|\u00A7aDmg: \u00A7c" + e.getDouble("d." + g[0] + ".d");
				i.setItem(g[1] + j, nItem(Material.PAPER, 0, "\u00A76\u00A7l" + (g[0] + 1) + ": \u00A7e\u00A7l" + asClock(e.getLong("d." + g[0] + ".t") / 1000), k));
				if (g[2] == 6) {
					g[2] = 0;
					g[1] += 2;
				} else {
					g[2]++;
				}
				if (j == 27) {
					g[1] = 10;
					g[2] = 0;
				}
				g[0]++;
			}
			if (h != 0 && (h + 1) <= f) {
				i.setItem(45, nItem(Material.STAINED_GLASS_PANE, 14, "\u00A76\u00A7lBack", ""));
			}
			if (f > (h + 1)) {
				i.setItem(53, nItem(Material.STAINED_GLASS_PANE, 13, "\u00A76\u00A7lNext", ""));
			}
			c.put("IV" + (h + 1), i);
		}
		d.delete();
		uB.put(a, c);
		b.uB.openInventory(uB.get(a).get("IV1"));
		b.setInvKey("DMG", "IV1");

	}

	// ------:- PRIVATE | INVENTORY -:--------------------------------------------------------------

	private ItemStack getItem() {
		return nItem(Material.NETHER_STAR, 0, "\u00A7c\u00A7lDamager Logger", "0|List of damage", "0|you have been taken from", "0|this UHC game.");
	}

	// ------:- PRIVATE | DATA -:--------------------------------------------------------------

	private String[] fEe(EntityDamageByEntityEvent a) {
		switch (a.getCause()) {
		case ENTITY_ATTACK:
			if (a.getDamager() instanceof Player) {
				Player b = (Player) a.getDamager();
				return o(b.getName(), gI(b.getInventory().getItemInMainHand().getType()));
			}
		case ENTITY_EXPLOSION:
			return o(gE(a.getDamager()));
		case FALL:
			return o("Ender Pearl");
		case FALLING_BLOCK:
			return o("Falling Anvil");
		case LIGHTNING:
			return o("Lighning");
		case MAGIC:
			ThrownPotion b = (ThrownPotion) a.getDamager();
			if (b.getShooter() instanceof Player) {
				return o(((Player) b.getShooter()).getName(), gEf(b.getEffects()));
			}
			return o(gE((Entity) b.getShooter()), gEf(b.getEffects()));
		case PROJECTILE:
			Projectile c = (Projectile) a.getDamager();
			if (c.getShooter() instanceof Player) {
				return o(((Player) c.getShooter()).getName(), gE(c));
			} else if (c.getShooter() instanceof BlockProjectileSource) {
				return o(gB((BlockProjectileSource) c.getShooter()), gE(c));
			}
			return o(gE((Entity) c.getShooter()), gE(c));
		case THORNS:
			if (a.getDamager() instanceof Player) {
				return o(((Player) a.getDamager()).getName(), "Thorns");
			}
			return o(gE(a.getDamager()), "Thorns");
		default:
			return o("Unknown 0x1." + a.getCause().ordinal() + " {" + a.getCause() + "}");
		}
	}

	private String[] fBe(DamageCause a) {
		switch (a) {
		case CONTACT:
			return o("Cactus");
		case HOT_FLOOR:
			return o("Lava Floor");
		case LAVA:
			return o("Lava");
		case VOID:
			return o("Void");
		default:
			return o("Unknown 0x2." + a.ordinal() + " {" + a + "}");
		}
	}

	private String[] fE(DamageCause a) {
		switch (a) {
		case DROWNING:
			return o("Drowning");
		case FALL:
			return o("Fall");
		case FIRE:
			return o("Fire");
		case FIRE_TICK:
			return o("Burning");
		case FLY_INTO_WALL:
			return o("Fly Into Wall");
		case MAGIC:
		case POISON:
			return o("Poison");
		case SUFFOCATION:
			return o("Suffocation");
		case STARVATION:
			return o("Starvation");
		case WITHER:
			return o("Wither Poison");
		default:
			return o("Unknown 0x3." + a.ordinal() + " {" + a + "}");
		}
	}

	private String gE(Entity a) {
		switch (a.getType()) {
		case AREA_EFFECT_CLOUD:
			AreaEffectCloud b = (AreaEffectCloud) a;
			return gPD(b.getBasePotionData(), b.getBasePotionData().isUpgraded()) + " (Cloud Effect)";
		case ARROW:
			return "Archery";
		case BLAZE:
			return "Blaze";
		case CAVE_SPIDER:
			return "Cave Spider";
		case CREEPER:
			return (((Creeper) a).isPowered() ? "Charged " : "") + "Creeper";
		case DRAGON_FIREBALL:
			return "Dragon Fireball";
		case EGG:
			return "Egg";
		case ELDER_GUARDIAN:
			return "Elder Guardian";
		case ENDER_CRYSTAL:
			return "Ender Crystal";
		case ENDER_DRAGON:
			return "Ender Dragon";
		case ENDERMAN:
			return "Enderman";
		case ENDERMITE:
			return "Endermite";
		case EVOKER:
			return "Evoker";
		case EVOKER_FANGS:
			return "Evoker Fang";
		case FIREBALL:
			return "Fireball";
		case LLAMA:
			return "Lama";
		case LLAMA_SPIT:
			return "Spit";
		case HUSK:
			return (((Husk) a).isBaby() ? "Baby " : "") + "Husk";
		case GHAST:
			return "Ghast";
		case GUARDIAN:
			return "Guardian";
		case IRON_GOLEM:
			return "Iron Golem";
		case MAGMA_CUBE:
			return gS(((MagmaCube) a).getSize()) + "Magma Cube";
		case MINECART_TNT:
			return "Minecart TNT";
		case PIG_ZOMBIE:
			PigZombie c = (PigZombie) a;
			if (c.isBaby()) {
				if (c.isInsideVehicle() && c.getVehicle().getType().equals(EntityType.CHICKEN)) {
					return "Chicken Jockey (Zombie Pigman)";
				}
				return "Baby Zombie Pigman";
			}
			return "Zombie Pigman";
		case POLAR_BEAR:
			return "Polar Bear";
		case PRIMED_TNT:
			return "TNT";
		case RABBIT:
			return "Rabbit";
		case SHULKER:
			return "Shulker";
		case SHULKER_BULLET:
			return "Shulker Missile";
		case SILVERFISH:
			return "Silverfish";
		case SKELETON:
			return "Skeleton";
		case SLIME:
			return gS(((Slime) a).getSize()) + "Slime";
		case SMALL_FIREBALL:
			return "Small Fireball";
		case SNOWBALL:
			return "Snowball";
		case SNOWMAN:
			return "Snowman";
		case SPIDER:
			Spider e = (Spider) a;
			if (e.getPassenger() != null && e.getPassenger().getType().equals(EntityType.SKELETON)) {
				return "Spider Jockey";
			}
			return "Spider";
		case SPECTRAL_ARROW:
			return "Spectral Arrow";
		case STRAY:
			return "Stray Skeleton";
		case TIPPED_ARROW:
			TippedArrow f = (TippedArrow) a;
			return gPD(f.getBasePotionData(), f.getBasePotionData().isUpgraded()) + " Tipped Arrow";
		case VEX:
			return "Vex";
		case VINDICATOR:
			return "Vindicator";
		case WITCH:
			return "Witch";
		case WITHER:
			return "Wither Boss";
		case WITHER_SKELETON:
			return "Wither Skeleton";
		case WITHER_SKULL:
			return "Wither Skull";
		case WOLF:
			return (((Wolf) a).isAdult() ? "" : "Baby ") + "Wolf";
		case ZOMBIE:
			Zombie g = (Zombie) a;
			if (g.isBaby()) {
				if (g.isInsideVehicle() && g.getVehicle().getType().equals(EntityType.CHICKEN)) {
					return "Chicken Jockey";
				}
				return "Baby Zombie";
			}
			return "Zombie";
		case ZOMBIE_VILLAGER:
			ZombieVillager h = (ZombieVillager) a;
			return ds(h.getVillagerProfession()) + (h.isBaby() ? " Baby" : "") + " Zombie Villager";
		default:
			return "Unknown 0.4." + a.getType().ordinal() + " {" + a.getType() + "}";
		}
	}

	private String gI(Material a) {
		switch (a) {
		case DIAMOND_AXE:
			return "Diamond Axe";
		case DIAMOND_HOE:
			return "Diamond Hoe";
		case DIAMOND_PICKAXE:
			return "Diamond Pickaxe";
		case DIAMOND_SPADE:
			return "Diamond Spade";
		case DIAMOND_SWORD:
			return "Diamond Sword";
		case GOLD_AXE:
			return "Golden Axe";
		case GOLD_HOE:
			return "Golden Hoe";
		case GOLD_PICKAXE:
			return "Golden Pickaxe";
		case GOLD_SPADE:
			return "Golden Spade";
		case GOLD_SWORD:
			return "Golden Sword";
		case IRON_AXE:
			return "Iron Axe";
		case IRON_HOE:
			return "Iron Hoe";
		case IRON_PICKAXE:
			return "Iron Pickaxe";
		case IRON_SPADE:
			return "Iron Spade";
		case IRON_SWORD:
			return "Iron Sword";
		case STONE_AXE:
			return "Stone Axe";
		case STONE_HOE:
			return "Stone Hoe";
		case STONE_PICKAXE:
			return "Stone Pickaxe";
		case STONE_SPADE:
			return "Stone Spade";
		case STONE_SWORD:
			return "Stone Sword";
		case WOOD_AXE:
			return "Wooden Axe";
		case WOOD_HOE:
			return "Wooden Hoe";
		case WOOD_PICKAXE:
			return "Wooden Pickaxe";
		case WOOD_SPADE:
			return "Wooden Spade";
		case WOOD_SWORD:
			return "Wooden Sword";
		default:
			return "Fist";
		}
	}

	private String gB(BlockProjectileSource a) {
		switch (a.getBlock().getType()) {
		case DISPENSER:
			return "Dispenser";
		default:
			return "Unknown 0x5." + a.getBlock().getType().ordinal() + " {" + a.getBlock().getType() + "}";
		}
	}

	private String gEf(Collection<PotionEffect> a) {
		StringBuilder b = new StringBuilder();
		for (PotionEffect c : a) {
			b.append((b.length() != 0 ? ", " : "") + gEff(c.getType(), c.getAmplifier()));
		}
		return b.toString();
	}

	private String gEff(PotionEffectType a, int b) {
		switch (a.getName()) {
		case "HARM":
			return "Harm " + (b == 1 ? "II" : "I");
		default:
			return "Unknown 0x6";
		}
	}

	private String gPD(PotionData a, boolean b) {
		switch (a.getType()) {
		case AWKWARD:
			return "Awkward " + (b ? "II" : "I");
		case FIRE_RESISTANCE:
			return "Fire Fesistance " + (b ? "II" : "I");
		case INSTANT_DAMAGE:
			return "Instant Damage " + (b ? "II" : "I");
		case INSTANT_HEAL:
			return "Instant Heal " + (b ? "II" : "I");
		case INVISIBILITY:
			return "Invisibility " + (b ? "II" : "I");
		case JUMP:
			return "Jump " + (b ? "II" : "I");
		case LUCK:
			return "Luck " + (b ? "II" : "I");
		case MUNDANE:
			return "Mundane " + (b ? "II" : "I");
		case NIGHT_VISION:
			return "Night Vision " + (b ? "II" : "I");
		case POISON:
			return "poison " + (b ? "II" : "I");
		case REGEN:
			return "Regen " + (b ? "II" : "I");
		case SLOWNESS:
			return "Slowness " + (b ? "II" : "I");
		case SPEED:
			return "Speed " + (b ? "II" : "I");
		case STRENGTH:
			return "Strength " + (b ? "II" : "I");
		case THICK:
			return "Thick " + (b ? "II" : "I");
		case WATER:
			return "Water " + (b ? "II" : "I");
		case WATER_BREATHING:
			return "Water Breathing " + (b ? "II" : "I");
		case WEAKNESS:
			return "Weakness " + (b ? "II" : "I");
		case UNCRAFTABLE:
			return "Dragon Breath " + (b ? "II" : "I");
		default:
			return "Unknown 0x7";
		}
	}

	private String ds(Villager.Profession a) {
		switch (a) {
		case BLACKSMITH:
			return "Blacksmith";
		case BUTCHER:
			return "Butcher";
		case FARMER:
			return "Farmer";
		case HUSK:
			return "Husk";
		case LIBRARIAN:
			return "Librarian";
		case NITWIT:
			return "Nitwit";
		case PRIEST:
			return "Priest";
		default:
			return "Unknown 0x8." + a.ordinal();
		}
	}

	private String gS(int a) {
		switch (a) {
		case 1:
			return "Tiny ";
		case 2:
			return "Small ";
		case 3:
			return "Big ";
		default:
			return "";
		}
	}

	// ------:- PRIVATE | OUTPUT -:-----------------------------------------------------------------

	private String[] o(String... a) {
		return a;
	}
}
