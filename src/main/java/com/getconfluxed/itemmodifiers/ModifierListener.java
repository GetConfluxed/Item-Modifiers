package com.getconfluxed.itemmodifiers;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ModifierListener implements IContainerListener {

    @Override
    public void sendAllContents (Container containerToSend, NonNullList<ItemStack> itemsList) {

    }

    @Override
    public void sendSlotContents (Container containerToSend, int slotInd, ItemStack stack) {

        final ItemStack actualStack = containerToSend.getSlot(slotInd).getStack();

        // Check if the stack requires syncing.
        if (stack.hasTagCompound() && actualStack.getTagCompound().getBoolean("SyncModifiers")) {

            // Remove the sync tag from nbt.
            // Modifying the ItemStack will cause the server to sync on it's own.
            // This code is stupid, but don't delete it yet.
            actualStack.getTagCompound().setBoolean("SyncModifiers", false);
        }
    }

    @Override
    public void sendWindowProperty (Container containerIn, int varToUpdate, int newValue) {

    }

    @Override
    public void sendAllWindowProperties (Container containerIn, IInventory inventory) {

    }
}