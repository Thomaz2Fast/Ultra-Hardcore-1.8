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

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.projectiles.CraftBlockProjectileSource;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;

import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;

public class DamagerLog {

	/**
	 * Detect what entity get damage from
	 */
	public String[] getPlayerDamageSource(EntityDamageEvent e) {
		if (e.getEventName().equals("EntityDamageByEntityEvent")) {
			return dmgFmEnt(e);
		} else if (e.getEventName().equals("EntityDamageByBlockEvent")) {
			return dmgFmBk(e.getCause());
		} else {
			return dmgFmEnv(e.getCause());
		}
	}

	/**
	 * Open book in spectator mode
	 */
	public void openSpectatorBook(CraftPlayer cp) {
		ItemStack isMc = new ItemStack(Item.d("minecraft:written_book"));
		cp.getInventory().setHeldItemSlot(4);
		cp.getHandle().openBook(isMc);
	}

	// :: PRIVATE :: //

	/**
	 * Damage > From Entity
	 */
	private String[] dmgFmEnt(EntityDamageEvent e) {
		EntityDamageByEntityEvent ebe = (EntityDamageByEntityEvent) e;
		switch (e.getCause()) {
		case ENTITY_ATTACK:
			if (ebe.getDamager() instanceof Player) {
				Player p = (Player) ebe.getDamager();
				return out(p.getName(), getIm(p.getItemInHand().getType()));
			}
			return out(getEnt(ebe.getDamager()), null);
		case ENTITY_EXPLOSION:
			return out(getEnt(ebe.getDamager()), null);
		case FALL:
			return out("Ender Pearl", null);
		case FALLING_BLOCK:
			return out("Falling Anvil", null);
		case LIGHTNING:
			return out("Lightning", null);
		case MAGIC:
			ThrownPotion tp = (ThrownPotion) ebe.getDamager();
			if (tp.getShooter() instanceof Player) {
				Player p = (Player) tp.getShooter();
				return out(p.getName(), getEffs(tp.getEffects()));
			} else if (tp.getShooter() != null) {
				return out(getEnt((Entity) tp.getShooter()), getEffs(tp.getEffects()));
			}
			return out("Unknown 0x11." + ebe.getDamager().getType().ordinal(), getEffs(tp.getEffects()));
		case PROJECTILE:
			Projectile pj = (Projectile) ebe.getDamager();
			if (pj.getShooter() instanceof Player) {
				Player p = (Player) pj.getShooter();
				return out(p.getName(), getEnt(pj));
			} else if (pj.getShooter() instanceof CraftBlockProjectileSource) {
				return out(getBk((CraftBlockProjectileSource) pj.getShooter()), getEnt(pj));
			} else if (pj.getShooter() != null) {
				return out(getEnt((Entity) pj.getShooter()), getEnt(pj));
			}
			return out("Unknown 0x12." + ebe.getDamager().getType().ordinal(), getEnt(pj));
		case THORNS:
			if (ebe.getDamager() instanceof Player) {
				Player p = (Player) ebe.getDamager();
				return out(p.getName(), "Thorns");
			}
			return out(getEnt(ebe.getDamager()), "Thorns");
		default:
			return out("Unknown Entity 0x13." + e.getCause().ordinal(), null);
		}
	}

	/**
	 * Damage > From Block
	 */
	private String[] dmgFmBk(DamageCause c) {
		switch (c) {
		case CONTACT:
			return out("Cactus", null);
		case LAVA:
			return out("Lava", null);
		case VOID:
			return out("Void", null);
		default:
			return out("Unknown 0x21. " + c.ordinal(), null);
		}
	}

	/**
	 * Damage > From Environment
	 */
	private String[] dmgFmEnv(DamageCause c) {
		switch (c) {
		case DROWNING:
			return out("Drowning", null);
		case FALL:
			return out("Fall", null);
		case FIRE:
		case FIRE_TICK:
			return out("Fire", null);
		case MAGIC:
		case POISON:
			return out("Poison", null);
		case SUFFOCATION:
			return out("Suffocation", null);
		case STARVATION:
			return out("Starvation", null);
		case SUICIDE:
			return out("Suicide", null);
		case WITHER:
			return out("Wither Poison", null);
		default:
			return out("Unknown 0x31." + c.ordinal(), null);
		}
	}

	/**
	 * Get > Entity Name.
	 */
	private String getEnt(Entity e) {
		switch (e.getType()) {
		case ARROW:
			return "Archery";
		case BLAZE:
			return "Blaze";
		case CAVE_SPIDER:
			return "Cave Spider";
		case CREEPER:
			Creeper c = (Creeper) e;
			if (c.isPowered()) {
				return "Charged Creeper";
			}
			return "Creeper";
		case EGG:
			return "Egg";
		case ENDER_CRYSTAL:
			return "Ender Crystal";
		case ENDER_DRAGON:
			return "Ender Dragon";
		case ENDERMAN:
			return "Enderman";
		case ENDERMITE:
			return "Endermite";
		case GHAST:
			return "Ghast";
		case FIREBALL:
			return "Fireball";
		case FISHING_HOOK:
			return "Fishing Hook";
		case GUARDIAN:
			Guardian g = (Guardian) e;
			if (g.isElder()) {
				return "Elder Guardian";
			}
			return "Guardian";
		case IRON_GOLEM:
			return "Iron Golem";
		case MAGMA_CUBE:
			MagmaCube mc = (MagmaCube) e;
			switch (mc.getSize()) {
			case 1:
				return "Tiny Magma Cube";
			case 2:
				return "Small Magma Cube";
			case 4:
				return "Big Magma Cube";
			default:
				return "Magma Cube";
			}
		case MINECART_TNT:
			return "Minecart TNT";
		case PIG_ZOMBIE:
			PigZombie pz = (PigZombie) e;
			if (pz.isBaby()) {
				if (pz.isInsideVehicle()) {
					if (pz.isVillager()) {
						return "(Zombie Pigman Villager) Chicken Jockey";
					}
					return "(Zombie Pigman) Chicken Jockey";
				} else if (pz.isVillager()) {
					return "Baby Zombie Pigman Villager";
				}
				return "Baby Zombie Pigman";
			} else if (pz.isVillager()) {
				return "Zombie Pigman Villager";
			}
			return "Zombie Pigman";
		case RABBIT:
			return "Rabbit";
		case SILVERFISH:
			return "Silverfish";
		case SKELETON:
			Skeleton sk = (Skeleton) e;
			if (sk.getSkeletonType().equals(SkeletonType.WITHER)) {
				return "Wither Skeleton";
			}
			return "Skeleton";
		case SLIME:
			Slime sl = (Slime) e;
			switch (sl.getSize()) {
			case 2:
				return "Small Slime";
			case 4:
				return "Big Slime";
			default:
				return "Slime";
			}
		case SNOWBALL:
			return "Snowball";
		case SNOWMAN:
			return "Snowman";
		case SMALL_FIREBALL:
			return "Small Fireball";
		case SPIDER:
			Spider sp = (Spider) e;
			if (sp.getPassenger() != null) {
				if (sp.getPassenger().getType().equals(EntityType.SKELETON)) {
					return "Spider Jockey";
				}
			}
			return "Spider";
		case PRIMED_TNT:
			return "TNT";
		case WITCH:
			return "Witch";
		case WITHER:
			return "Wither Boss";
		case WITHER_SKULL:
			return "Wither Skull";
		case WOLF:
			Wolf w = (Wolf) e;
			if (w.isAdult()) {
				return "Wolf";
			}
			return "Baby Wolf";
		case ZOMBIE:
			Zombie z = (Zombie) e;
			if (z.isBaby()) {
				if (z.isInsideVehicle()) {
					if (z.getVehicle().getType().equals(EntityType.CHICKEN)) {
						if (z.isVillager()) {
							return "(Zombie Villager) Chicken Jockey";
						}
						return "Chicken Jockey";
					}
				} else if (z.isVillager()) {
					return "Baby Zombie Villager";
				}
				return "Baby Zombie";
			} else if (z.isVillager()) {
				return "Zombie Villager";
			}
			return "Zombie";
		default:
			return "Unknown Entity 0x15." + e.getType().ordinal();
		}
	}

	/**
	 * Get > Item Name.
	 */
	private String getIm(Material m) {
		switch (m) {
		case WOOD_SPADE:
			return "Wooden Shovel";
		case WOOD_PICKAXE:
			return "Wooden Pickaxe";
		case WOOD_AXE:
			return "Wooden Ake";
		case WOOD_HOE:
			return "Wooden Hoe";
		case WOOD_SWORD:
			return "Wooden Sword";
		case STONE_SPADE:
			return "Stone Shovel";
		case STONE_PICKAXE:
			return "Stone Pickaxe";
		case STONE_AXE:
			return "Stone Ake";
		case STONE_HOE:
			return "Stone Hoe";
		case STONE_SWORD:
			return "Stone Sword";
		case DIAMOND_SPADE:
			return "Diamond Shovel";
		case DIAMOND_PICKAXE:
			return "Diamond Pickaxe";
		case DIAMOND_AXE:
			return "Diamond Ake";
		case DIAMOND_HOE:
			return "Diamond Hoe";
		case DIAMOND_SWORD:
			return "Diamond Sword";
		case IRON_SPADE:
			return "Iron Shovel";
		case IRON_PICKAXE:
			return "Iron Pickaxe";
		case IRON_AXE:
			return "Iron Ake";
		case IRON_HOE:
			return "Iron Hoe";
		case IRON_SWORD:
			return "Iron Sword";
		case GOLD_SPADE:
			return "Golden Shovel";
		case GOLD_PICKAXE:
			return "Golden Pickaxe";
		case GOLD_AXE:
			return "Golden Ake";
		case GOLD_HOE:
			return "Golden Hoe";
		case GOLD_SWORD:
			return "Golden Sword";
		default:
			return "Fist";
		}
	}

	/**
	 * Get > Block Name.
	 */
	private String getBk(CraftBlockProjectileSource e) {
		switch (e.getBlock().getType()) {
		case DISPENSER:
			return "Dispenser";
		default:
			return "Unknown Block 0x14." + e.getBlock().getType().ordinal();
		}
	}

	/**
	 * Get > Potions Effect List
	 */
	private String getEffs(Collection<PotionEffect> e) {
		String s = "";
		for (PotionEffect ef : e) {
			if (s.length() != 0) {
				s += ", ";
			}
			s += getEffn(ef) + " Potion";
		}
		return s;
	}

	/**
	 * Get > Potion Effect Name
	 */
	private String getEffn(PotionEffect ef) {
		switch (ef.getType().getName()) {
		case "HARM":
			if (ef.getAmplifier() == 1) {
				return "Harm II";
			}
			return "Harm I";
		default:
			return "Unknown Potion 0x16." + ef.getType().getName();
		}
	}

	/**
	 * Output
	 */
	private String[] out(String s1, String s2) {
		return new String[] { s1, s2 };
	}
}