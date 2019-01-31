package com.getconfluxed.itemmodifiers;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.getconfluxed.itemmodifiers.modifiers.Modifier;
import com.getconfluxed.itemmodifiers.type.Type;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = ItemModifiersMod.MODID, name = ItemModifiersMod.NAME, version = "@VERSION@", certificateFingerprint = "@FINGERPRINT@")
public class ItemModifiersMod {

    // Properties
    public static final String MODID = "itemmodifiers";
    public static final String NAME = "Item Modifiers";
    public static final Logger LOG = LogManager.getLogger(NAME);

    // Registries
    public static final IForgeRegistry<Type> TYPE_REGISTRY = createRegistry("a-types", Type.class);
    public static final IForgeRegistry<Modifier> MODIFIER_REGISTRY = createRegistry("b-modifiers", Modifier.class);

    // Caches
    public static final Multimap<Type, Modifier> PREFIXES = HashMultimap.create();
    public static final Multimap<Type, Modifier> SUFFIXES = HashMultimap.create();

    public static final File CONFIG_DIR = createDirectory(new File("config/" + MODID));
    public static final File OVERRIDES_DIR = createDirectory(new File(CONFIG_DIR, "overrides"));
    public static final File MODIFIES_DIR = createDirectory(new File(OVERRIDES_DIR, "modifiers"));

    public static ConfigurationThing config;

    @EventHandler
    public void onPreInit (FMLPreInitializationEvent event) {

        config = new ConfigurationThing(new File(CONFIG_DIR, "itemmodifiers.cfg"));
    }

    @EventHandler
    public void onLoadComplete (FMLLoadCompleteEvent event) {

        LOG.info("Loaded {} modifier type categories.", TYPE_REGISTRY.getValuesCollection().size());
        LOG.info("Loaded {} modifiers.", MODIFIER_REGISTRY.getValuesCollection().size());

        // Iterate all modifiers and build the typed map cache.
        for (final Modifier modifier : MODIFIER_REGISTRY) {

            // Add the modifier to the appropriate map cache.
            (modifier.isPrefix() ? PREFIXES : SUFFIXES).put(modifier.getType(), modifier);
        }
    }

    /**
     * Creates a new registry for this mod. This should not be used externally, because it uses
     * this mod id.
     *
     * @param regName The name of the registry.
     * @param type The class of the intended held object.
     * @return The created registry.
     */
    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> createRegistry (String regName, Class<T> type) {

        return new RegistryBuilder<T>().setName(new ResourceLocation(MODID, regName)).setType(type).allowModification().setMaxID(Integer.MAX_VALUE >> 5).create();
    }

    private static File createDirectory (File file) {

        if (!file.exists()) {

            file.mkdirs();
        }

        return file;
    }
}