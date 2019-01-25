package com.getconfluxed.itemmodifiers.type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.getconfluxed.itemmodifiers.ItemModifiersMod;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
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

    public static final Type ENCHANTABLE = createType("enchantable", EnumEnchantmentType.ALL);
    public static final Type ARMOR_ALL = createType("armor_all", EnumEnchantmentType.ARMOR);
    public static final Type ARMOR_FEET = createType("armor_feet", EnumEnchantmentType.ARMOR_FEET);
    public static final Type ARMOR_LEGS = createType("armor_legs", EnumEnchantmentType.ARMOR_LEGS);
    public static final Type ARMOR_CHESTPLATE = createType("armor_chestplate", EnumEnchantmentType.ARMOR_CHEST);
    public static final Type ARMOR_HELMET = createType("armor_helmet", EnumEnchantmentType.ARMOR_HEAD);
    public static final Type MAINHAND = createType("mainhand", stack -> EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.MAINHAND);
    public static final Type OFFHAND = createType("offhand", stack -> EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.OFFHAND);
    public static final Type SWORD = createType("sword", EnumEnchantmentType.WEAPON);
    public static final Type TOOL = createType("tool", EnumEnchantmentType.DIGGER);
    public static final Type FISHING = createType("fishing", EnumEnchantmentType.FISHING_ROD);
    public static final Type BOW = createType("bow", EnumEnchantmentType.BOW);
    public static final Type WEARABLE = createType("wearable", EnumEnchantmentType.WEARABLE);
    public static final Type BREAKABLE = createType("breakable", EnumEnchantmentType.BREAKABLE);
    public static final Type FOOD = createType("food", stack -> stack.getItem() instanceof ItemFood);

    @SubscribeEvent
    public static void registerEquipmentTypes (RegistryEvent.Register<Type> event) {

        final IForgeRegistry<Type> registry = event.getRegistry();

        for (final Type type : toRegister) {

            registry.register(type);
        }

        toRegister.clear();
    }

    /**
     * Creates a new type and registers it based on an enchantment type. It is registered with
     * the mod's id.
     *
     * @param id The id of the type to register.
     * @param type The EnumEnchantmentType to base it on.
     * @return The type that was registered.
     */
    private static Type createType (String id, EnumEnchantmentType type) {

        return createType(id, new TypeEnchantmentWrapped(type));
    }

    /**
     * Creates a new type and registers it based on a predicate. It is registered with the
     * mod's id.
     *
     * @param id The id of the type to register.
     * @param predicate The predicate used for the type.
     * @return The type that was registered.
     */
    private static Type createType (String id, Predicate<ItemStack> predicate) {

        return createType(id, new TypePredicate(predicate));
    }

    /**
     * Creates a new type and registers it. This is used internally and registers it with the
     * mod's id.
     *
     * @param id The id of the type to register.
     * @param type The type to register.
     * @return The type that was registered.
     */
    private static Type createType (String id, Type type) {

        type.setRegistryName(new ResourceLocation(ItemModifiersMod.MODID, id));
        toRegister.add(type);
        return type;
    }
}
