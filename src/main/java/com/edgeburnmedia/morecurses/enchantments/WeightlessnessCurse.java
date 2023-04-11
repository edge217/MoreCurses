package com.edgeburnmedia.morecurses.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class WeightlessnessCurse extends Enchantment {

	public WeightlessnessCurse() {
		super(Rarity.RARE, EnchantmentTarget.ARMOR,
			new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
				EquipmentSlot.FEET});
	}

	@Override
	public boolean isCursed() {
		return true;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}
}
