package com.getconfluxed.itemmodifiers.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * This class is used to define classification of equipment. It is functionally predicate with
 * a registry and name. Please use Forge's registry system to register custom types.
 */
public class Type extends IForgeRegistryEntry.Impl<Type> {

    /**
     * Checks if an ItemStack fits within the category.
     *
     * @param stack The item stack that is being checked.
     * @return Whether or not the ItemStack is valid for this category.
     */
    public boolean isValidItem (ItemStack stack) {

        return false;
    }

    /**
     * Gets the localization key for the name of the equipment type.
     *
     * @return A localization key for the name of the equipment type.
     */
    public String getLocalizationKey () {

        final ResourceLocation id = this.getRegistryName();
        return "type." + id.getNamespace() + "." + id.getPath() + ".name";
    }

    /**
     * Gets a text component containing the name of the equipment type.
     *
     * @return The text component for the name.
     */
    public ITextComponent getNameTextComponent () {

        return new TextComponentTranslation(this.getLocalizationKey());
    }
}