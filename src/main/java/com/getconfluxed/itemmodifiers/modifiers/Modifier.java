package com.getconfluxed.itemmodifiers.modifiers;

import com.getconfluxed.itemmodifiers.type.Type;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class Modifier extends IForgeRegistryEntry.Impl<Modifier> {

    /**
     * This multimap is empty and immutable. It's used by
     * {@link #getAttributeModifiers(EntityEquipmentSlot, ItemStack)} as an empty default for
     * performance reasons.
     */
    private static final Multimap<String, AttributeModifier> DEFAULT = Multimaps.unmodifiableMultimap(HashMultimap.create());

    /**
     * Gets a multimap of all the attribute modifiers provided by this Item modifier. This
     * should be deterministic, meaning the same ItemStack should provide the same attribute
     * modifiers. This is because this map is used to add/remove modifiers.
     *
     * @param slot The slot the item is in.
     * @param stack The stack to get modifiers for.
     * @return A multimap containing all the attribute modifiers provided by this item
     *         modifier.
     */
    public Multimap<String, AttributeModifier> getAttributeModifiers (EntityEquipmentSlot slot, ItemStack stack) {

        return DEFAULT;
    }

    /**
     * This method is fired when an item with this modifier is equipped.
     *
     * @param replacedStack The ItemStack that was replaced by our stack.
     * @param ourStack The stack that has this modifier applied to it.
     * @param entity The entity equipping the stack.
     * @param slot The slot the stack is going into.
     */
    public void onEquipped (ItemStack replacedStack, ItemStack ourStack, EntityLivingBase entity, EntityEquipmentSlot slot) {

        entity.getAttributeMap().applyAttributeModifiers(this.getAttributeModifiers(slot, ourStack));
    }

    /**
     * This method is fired when an item with this modifier is being unequipped.
     *
     * @param ourStack The ItemStack that is being unequipped.
     * @param newStack The ItemStack that is replacing our stack.
     * @param entity The entity unequipping the stack.
     * @param slot The slot the stack is being unequipped from.
     */
    public void onUnequipped (ItemStack ourStack, ItemStack newStack, EntityLivingBase entity, EntityEquipmentSlot slot) {

        entity.getAttributeMap().removeAttributeModifiers(this.getAttributeModifiers(slot, ourStack));
    }

    /**
     * This method is called when the modifier is applied to the item. It allows the ItemStack
     * to be edited and modified.
     *
     * @param stack The ItemStack the prefix is being applied to.
     */
    public void onApplied (ItemStack stack) {

    }

    /**
     * This method is called when the modifier is removed from the item. It allows for cleanup
     * of data that was set in {@link #onApplied(ItemStack)}.
     *
     * @param stack The ItemStack the prefix is being removed from.
     */
    public void onRemoved (ItemStack stack) {

    }

    /**
     * Gets the type of modifier. This is used to determine what types of items this can be
     * applied to.
     *
     * @return The type of items this modifier can be applied to.
     */
    public abstract Type getType ();
}