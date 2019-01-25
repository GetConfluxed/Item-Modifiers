package com.getconfluxed.itemmodifiers.type;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

/**
 * This implementation checks if an item is valid using the provided predicate.
 */
public class TypePredicate extends Type {

    /**
     * The predicate used to test items.
     */
    private final Predicate<ItemStack> predicate;

    public TypePredicate (Predicate<ItemStack> predicate) {

        this.predicate = predicate;
    }

    @Override
    public boolean isValidItem (ItemStack stack) {

        return this.predicate.test(stack);
    }
}
