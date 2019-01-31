package com.getconfluxed.itemmodifiers;

import com.getconfluxed.itemmodifiers.inventory.ContainerListenerSyncModifiers;
import com.getconfluxed.itemmodifiers.modifiers.Modifier;

import net.darkhax.bookshelf.util.InventoryUtils;
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

    /**
     * A constant container listener which is applied to the player's container when they craft
     * an item. This is responsible for syncing data from the server to the client when the
     * player crafts the item.
     */
    private static final ContainerListenerSyncModifiers SYNC_LISTENER = new ContainerListenerSyncModifiers();

    @SubscribeEvent
    public static void onCrafting (PlayerEvent.ItemCraftedEvent event) {

        // Make sure the player exists, and this is the server world.
        if (event.player != null && !event.player.getEntityWorld().isRemote) {

            // Apply the prefix and suffix modifiers.
            final Modifier prefix = ItemModifierHelper.applyRandomModifier(event.crafting, true);
            final Modifier modifer = ItemModifierHelper.applyRandomModifier(event.crafting, false);

            // If the current container doesn't have the sync hack listener, apply it.
            if (!InventoryUtils.hasListener(event.player.openContainer, SYNC_LISTENER)) {

                event.player.openContainer.addListener(SYNC_LISTENER);
            }

            // Mark the crafted item as requiring a sync from the server.
            event.crafting.getTagCompound().setBoolean("SyncModifiers", true);
        }
    }

    @SubscribeEvent
    public static void onEquipmentChangedEvent (LivingEquipmentChangeEvent event) {

        final ItemStack previousStack = event.getFrom();
        final ItemStack newStack = event.getTo();

        // If there was a previous item, try to remove it's effects.
        if (!previousStack.isEmpty()) {

            // Get the modifier of the previous stack.
            for (final Modifier modifier : ItemModifierHelper.getModifiers(previousStack)) {

                // If there was a previous modifier, remove it's attributes and fire hook.
                if (modifier != null) {

                    modifier.onUnequipped(previousStack, newStack, event.getEntityLiving(), event.getSlot());
                }
            }
        }

        // If there is a new item, try to apply it's modifier effects.
        if (!newStack.isEmpty()) {

            // Get the modifier of the new stack.
            for (final Modifier modifier : ItemModifierHelper.getModifiers(newStack)) {

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
        for (final Modifier modifier : ItemModifierHelper.getModifiers(event.getItemStack())) {

            if (modifier != null) {

                // Add the name modifier name to the item.
                event.getToolTip().set(0, modifier.modifyItemName(event.getToolTip().get(0), event.getItemStack()));

                // Allow modifier to modify the tooltips.
                modifier.modifyTooltip(event.getToolTip(), event.getEntityPlayer(), event.getItemStack());
            }
        }
    }
}