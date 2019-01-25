package com.getconfluxed.itemmodifiers.type;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class TypeEnchantmentWrapped extends Type {

    public final EnumEnchantmentType enchantmentType;

    public TypeEnchantmentWrapped (EnumEnchantmentType enchantmentType) {

        this.enchantmentType = enchantmentType;
    }

    @Override
    public boolean isValidItem (ItemStack stack) {

        return this.enchantmentType.canEnchantItem(stack.getItem());
    }
}