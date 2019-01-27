package com.getconfluxed.itemmodifiers.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.getconfluxed.itemmodifiers.ItemModifiersMod;
import com.getconfluxed.itemmodifiers.type.Type;
import com.getconfluxed.itemmodifiers.type.Types;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = ItemModifiersMod.MODID)
public class Modifiers {

    private static final List<Modifier> toRegister = new ArrayList<>();

    public static final Modifier POINTY = createAttrModifier("pointy", Types.SWORD, EntityEquipmentSlot.MAINHAND).setAttack(0.05, 2);
    public static final Modifier DULL = createAttrModifier("dull", Types.SWORD, EntityEquipmentSlot.MAINHAND).setAttack(-0.05, 2);

    @SubscribeEvent
    public static void registerItemModifiers (RegistryEvent.Register<Modifier> event) {

        final IForgeRegistry<Modifier> registry = event.getRegistry();

        for (final Modifier modifier : toRegister) {

            registry.register(modifier);
        }
    }

    private static ModifierAttributeBase createAttrModifier (String id, Type type, EntityEquipmentSlot slot) {

        return (ModifierAttributeBase) createModifier(id, new ModifierAttributeBase(type, slot));
    }

    private static Modifier createModifier (String id, Modifier modifier) {

        modifier.setRegistryName(ItemModifiersMod.MODID, id);
        toRegister.add(modifier);
        return modifier;
    }
}