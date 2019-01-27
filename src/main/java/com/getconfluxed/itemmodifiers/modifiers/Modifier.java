package com.getconfluxed.itemmodifiers.modifiers;

import java.util.List;
import java.util.Map.Entry;

import com.getconfluxed.itemmodifiers.type.Type;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Modifier extends IForgeRegistryEntry.Impl<Modifier> {

    /**
     * The type of items this modifier can be applied to.
     */
    private final Type type;
    
    /**
     * The weight of the modifier. 
     */
    private final int weight;
    
    /**
     * A map of all the attribute modifiers applied by the item modifier.
     */
    private final Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
    
    public Modifier(Type type, int weight) {
        
        this.type = type;
        this.weight = weight;
    }

    /**
     * Gets the type of modifier. This is used to determine what types of items this can be
     * applied to.
     *
     * @return The type of items this modifier can be applied to.
     */
    public Type getType () {
        
        return this.type;
    }
    
    /**
     * The weight/rarity of the modifier.
     * @return The weight/rarity of the modifier.
     */
    public int getWeight() {
        
        return this.weight;
    }

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

        return modifiers;
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
     * Gets the localization key for the name of the modifier.
     *
     * @return A localization key for the name of the modifier.
     */
    @SideOnly(Side.CLIENT)
    public String getLocalizationKey () {

        final ResourceLocation id = this.getRegistryName();
        return "modifier." + id.getNamespace() + "." + id.getPath() + ".name";
    }

    /**
     * Gets a text component containing the name of the modifier.
     *
     * @return The text component for the name.
     */
    @SideOnly(Side.CLIENT)
    public ITextComponent getNameTextComponent () {

        return new TextComponentTranslation(this.getLocalizationKey());
    }

    @SideOnly(Side.CLIENT)
    public String modifyItemName (String originalName, ItemStack stack) {

        return I18n.format(this.getLocalizationKey()) + " " + originalName;
    }

    @SideOnly(Side.CLIENT)
    public void modifyTooltip (List<String> tooltip, EntityPlayer player, ItemStack stack) {

        // Check all the slot types to see if the item will have an effect.
        for (final EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {

            // Get the attributes map from the modifier
            final Multimap<String, AttributeModifier> attributeModifiers = this.getAttributeModifiers(entityequipmentslot, stack);

            if (!attributeModifiers.isEmpty()) {

                // Iterate any bonuses from the modifier and show them on the tooltip.
                for (final Entry<String, AttributeModifier> entry : attributeModifiers.entries()) {

                    final AttributeModifier attributemodifier = entry.getValue();
                    final double amount = attributemodifier.getAmount();

                    // Gets the display amount value. Operation 1 and 2 are percentages, so
                    // x100.
                    final double displayValue = attributemodifier.getOperation() > 0 ? amount * 100d : amount;

                    // Render value as positive
                    if (amount > 0.0D) {
                        tooltip.add(TextFormatting.GREEN + I18n.format("attribute.modifier.plus." + attributemodifier.getOperation(), ItemStack.DECIMALFORMAT.format(displayValue), I18n.format("attribute.name." + entry.getKey())));
                    }

                    // Render value as negative
                    else if (amount < 0.0D) {
                        tooltip.add(TextFormatting.RED + I18n.format("attribute.modifier.take." + attributemodifier.getOperation(), ItemStack.DECIMALFORMAT.format(displayValue * -1d), I18n.format("attribute.name." + entry.getKey())));
                    }
                }
            }
        }
    }
}