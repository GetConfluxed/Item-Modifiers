package com.getconfluxed.itemmodifiers.modifiers;

import java.util.ArrayList;
import java.util.List;

import com.getconfluxed.itemmodifiers.ItemModifiersMod;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = ItemModifiersMod.MODID)
public class Modifiers {

    private static final List<Modifier> toRegister = new ArrayList<>();

    @SubscribeEvent
    public static void registerItemModifiers (RegistryEvent.Register<Modifier> event) {

        final IForgeRegistry<Modifier> registry = event.getRegistry();

        for (final Modifier modifier : toRegister) {

            registry.register(modifier);
        }
    }
    
    private static Modifier createModifier (String id, Modifier modifier) {

        modifier.setRegistryName(ItemModifiersMod.MODID, id);
        toRegister.add(modifier);
        return modifier;
    }
}