package com.getconfluxed.itemmodifiers;

import java.util.Collection;
import java.util.List;

import com.getconfluxed.itemmodifiers.modifiers.Modifier;
import com.getconfluxed.itemmodifiers.type.Types;
import com.google.common.collect.Iterables;

import net.darkhax.bookshelf.lib.Constants;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = ItemModifiersMod.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onCrafting (PlayerEvent.ItemCraftedEvent event) {

        Collection<Modifier> prefixes = ItemModifiersMod.PREFIXES.get(Types.SWORD);
        Collection<Modifier> suffixes = ItemModifiersMod.SUFFIXES.get(Types.SWORD);
        ItemModifierHelper.setModifier(event.crafting, Iterables.get(prefixes, Constants.RANDOM.nextInt(prefixes.size())));
        ItemModifierHelper.setModifier(event.crafting, Iterables.get(suffixes, Constants.RANDOM.nextInt(suffixes.size())));
    }

    @SubscribeEvent
    public static void onEquipmentChangedEvent (LivingEquipmentChangeEvent event) {

        final ItemStack previousStack = event.getFrom();
        final ItemStack newStack = event.getTo();

        // If there was a previous item, try to remove it's effects.
        if (!previousStack.isEmpty()) {

            // Get the modifier of the previous stack.
            for (Modifier modifier : ItemModifierHelper.getModifiers(previousStack)) {
                
                // If there was a previous modifier, remove it's attributes and fire hook.
                if (modifier != null) {

                    modifier.onUnequipped(previousStack, newStack, event.getEntityLiving(), event.getSlot());
                }
            }
        }

        // If there is a new item, try to apply it's modifier effects.
        if (!previousStack.isEmpty()) {

            // Get the modifier of the new stack.
            for (Modifier modifier : ItemModifierHelper.getModifiers(previousStack)) {
                
                // If there was a new modifier, fire the equipped hook.
                if (modifier != null) {

                    modifier.onEquipped(previousStack, newStack, event.getEntityLiving(), event.getSlot());
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onTooltipRendered (ItemTooltipEvent event) {

        // Try to get a modifier from the item.
        for (Modifier modifier : ItemModifierHelper.getModifiers(event.getItemStack())) {
            
            if (modifier != null) {

                // Add the name modifier name to the item.
                event.getToolTip().set(0, modifier.modifyItemName(event.getToolTip().get(0), event.getItemStack()));

                // Allow modifier to modify the tooltips.
                modifier.modifyTooltip(event.getToolTip(), event.getEntityPlayer(), event.getItemStack());
            }
        }
    }
}