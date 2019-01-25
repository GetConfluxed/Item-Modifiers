package com.getconfluxed.itemmodifiers.type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.getconfluxed.itemmodifiers.ItemModifiersMod;

import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = ItemModifiersMod.MODID)
public class Types {

    /**
     * A temporary list which holds modifier types added by the base mod that need to be
     * registered. This list should never be accessed by other mods. It is cleared once the
     * registry event has been fired.
     */
    private static List<Type> toRegister = new ArrayList<>();

    public static final Type ARMOR_ALL = createType("armor_all", stack -> stack.getItem() instanceof ItemArmor);
    public static final Type ARMOR_FEET = createType("armor_feet", stack -> EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.FEET);
    public static final Type ARMOR_LEGS = createType("armor_legs", stack -> EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.LEGS);
    public static final Type ARMOR_CHESTPLATE = createType("armor_chestplate", stack -> EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.CHEST);
    public static final Type ARMOR_HELMET = createType("armor_helmet", stack -> EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.HEAD);
    public static final Type MAINHAND = createType("mainhand", stack -> EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.MAINHAND);
    public static final Type OFFHAND = createType("offhand", stack -> EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.OFFHAND);
    public static final Type SWORD = createType("sword", stack -> stack.getItem() instanceof ItemSword);
    public static final Type TOOL = createType("tool", stack -> stack.getItem() instanceof ItemTool);
    public static final Type FISHING = createType("fishing", stack -> stack.getItem() instanceof ItemFishingRod);
    public static final Type BOW = createType("bow", stack -> stack.getItem() instanceof ItemBow);
    public static final Type FOOD = createType("food", stack -> stack.getItem() instanceof ItemFood);

    @SubscribeEvent
    public static void registerEquipmentTypes (RegistryEvent.Register<Type> event) {

        final IForgeRegistry<Type> registry = event.getRegistry();

        for (final Type type : toRegister) {

            registry.register(type);
        }

        toRegister.clear();
    }

    private static Type createType (String id, Predicate<ItemStack> predicate) {

        final Type type = new TypePredicate(predicate);
        type.setRegistryName(new ResourceLocation(ItemModifiersMod.MODID, id));
        toRegister.add(type);
        return type;
    }
}
