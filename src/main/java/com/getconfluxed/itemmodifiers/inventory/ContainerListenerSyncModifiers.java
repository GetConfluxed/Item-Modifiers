package com.getconfluxed.itemmodifiers.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * This container listener is used to sync the server side modifier data to the client. This is
 * done by modifying the NBT of the item once it is placed in the players inventory.
 */
public class ContainerListenerSyncModifiers implements IContainerListener {

    @Override
    public void sendAllContents (Container containerToSend, NonNullList<ItemStack> itemsList) {

        // Not used
    }

    @Override
    public void sendSlotContents (Container containerToSend, int slotInd, ItemStack stack) {

        // Get the stack from the container. The one passed to us is a copy.
        final ItemStack actualStack = containerToSend.getSlot(slotInd).getStack();

        // Check if the stack requires syncing.
        if (stack.hasTagCompound() && actualStack.getTagCompound().getBoolean("SyncModifiers")) {

            // Remove the sync tag from the item stack. Changing the stack's NBT will cause it
            // to sync. Tag is removed rather than being set to false to decrease the footprint
            // of this mod. Yes this is a hack, but it works on both integrated and dedicated
            // servers. If you have a better solution to this, please let me know :p
            actualStack.getTagCompound().removeTag("SyncModifiers");
        }
    }

    @Override
    public void sendWindowProperty (Container containerIn, int varToUpdate, int newValue) {

        // Not used
    }

    @Override
    public void sendAllWindowProperties (Container containerIn, IInventory inventory) {

        // Not used
    }
}