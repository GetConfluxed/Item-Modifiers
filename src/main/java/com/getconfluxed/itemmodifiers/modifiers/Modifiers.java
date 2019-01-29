package com.getconfluxed.itemmodifiers.modifiers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FilenameUtils;

import com.getconfluxed.itemmodifiers.ItemModifiersMod;
import com.getconfluxed.itemmodifiers.data.DataLoader;
import com.getconfluxed.itemmodifiers.data.ModifierEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = ItemModifiersMod.MODID)
public class Modifiers {

    private static Gson gson = new GsonBuilder().create();

    @SubscribeEvent
    public static void registerItemModifiers (RegistryEvent.Register<Modifier> event) {

        final IForgeRegistry<Modifier> registry = event.getRegistry();

        for (final ModContainer mod : Loader.instance().getActiveModList()) {

            loadJsonItemModifiers(mod, registry);
        }
    }

    private static void loadJsonItemModifiers (ModContainer mod, IForgeRegistry<Modifier> registry) {

        DataLoader.findFiles(mod, "data/" + mod.getModId() + "/" + ItemModifiersMod.MODID + "/modifiers", (root, file) -> {

            // Only load files with the json extension
            if ("json".equals(FilenameUtils.getExtension(file.toString()))) {

                final String relative = root.relativize(file).toString();
                final String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                final ResourceLocation entryId = new ResourceLocation(mod.getModId(), name);

                // Create a handle for the location of a potential override file.
                final File potentialOverride = new File("config/itemmodifiers/overrides/modifiers/" + mod.getModId() + "/" + name + ".json");

                // If the override exists, use that instead of the one from the bundled data.
                if (potentialOverride.exists()) {

                    file = potentialOverride.toPath();
                }

                try (BufferedReader reader = Files.newBufferedReader(file)) {

                    final ModifierEntry entry = gson.fromJson(reader, ModifierEntry.class);

                    if (entry != null) {

                        final Modifier modifier = entry.build(entryId);

                        if (modifier != null) {

                            modifier.setRegistryName(entryId);
                            registry.register(modifier);
                        }
                    }
                }

                catch (final JsonSyntaxException e) {

                    ItemModifiersMod.LOG.error("Failure to read modifier {} from {}. The file is invalid! {}", entryId, file, e.getMessage());
                }

                catch (final IOException e) {

                    ItemModifiersMod.LOG.error("Failure to read modifier {} from {}.", entryId, file, e);
                }
            }
        });
    }

}