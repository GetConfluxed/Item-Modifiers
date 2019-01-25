package com.getconfluxed.itemmodifiers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.getconfluxed.itemmodifiers.type.Type;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = ItemModifiersMod.MODID, name = ItemModifiersMod.NAME, version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@")
public class ItemModifiersMod {

    // Properties
    public static final String MODID = "itemmodifiers";
    public static final String NAME = "Item Modifiers";
    public static final Logger LOG = LogManager.getLogger(NAME);

    // Equipment Types
    public static final IForgeRegistry<Type> TYPE_REGISTRY = createRegistry("types", Type.class);

    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        LOG.info("Loaded {} modifier type categories.", TYPE_REGISTRY.getValuesCollection().size());

        // TODO This is debug code, it should be removed or put behind a boolean.

        for (final Type type : TYPE_REGISTRY) {

            // TODO Client side issues here.
            if (!I18n.hasKey(type.getLocalizationKey())) {

                LOG.error("Missing localization for type {}. Localization Key: {}", type.getRegistryName().toString(), type.getLocalizationKey());
            }
        }
    }

    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> createRegistry (String regName, Class<T> type) {

        return new RegistryBuilder<T>().setName(new ResourceLocation(MODID, regName)).setType(type).allowModification().setMaxID(Integer.MAX_VALUE >> 5).create();
    }
}