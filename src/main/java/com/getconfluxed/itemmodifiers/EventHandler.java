package com.getconfluxed.itemmodifiers;

import com.getconfluxed.itemmodifiers.modifiers.Modifier;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = ItemModifiersMod.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onEquipmentChangedEvent (LivingEquipmentChangeEvent event) {

        final ItemStack previousStack = event.getFrom();
        final ItemStack newStack = event.getTo();

        // If there was a previous item, try to remove it's effects.
        if (!previousStack.isEmpty()) {

            // Get the modifier of the previous stack.
            final Modifier previousModifier = ItemModifierHelper.getModifier(event.getFrom());

            // If there was a previous modifier, remove it's attributes and fire hook.
            if (previousModifier != null) {

                previousModifier.onUnequipped(previousStack, newStack, event.getEntityLiving(), event.getSlot());
            }
        }

        // If there is a new item, try to apply it's modifier effects.
        if (!previousStack.isEmpty()) {

            // Get the modifier of the new stack.
            final Modifier newModifier = ItemModifierHelper.getModifier(event.getFrom());

            // If there was a new modifier, fire the equipped hook.
            if (newModifier != null) {

                newModifier.onEquipped(previousStack, newStack, event.getEntityLiving(), event.getSlot());
            }
        }
    }
}