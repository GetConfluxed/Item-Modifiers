package com.getconfluxed.itemmodifiers;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * This class acts as a replacement for Forge's registry Impl. This replacement removes some of
 * the overly restrictive code and has better performance and type safety.
 *
 * The main changes are
 *
 * Switched from TypeToken to Class<T> set by constructor.
 *
 * setRegistryName methods are no longer final, allowing them to be overridden.
 *
 * setRegistryName now uses ResourceLocation as the backing input type. Minor performance
 * improvement.
 *
 * The annoying "invalid prefix" messages wont happen, so you can load these like data packs.
 */
public class Registerable<T extends Registerable<T>> implements IForgeRegistryEntry<T> {

    private final Class<T> registryType;
    private ResourceLocation registryName;

    public Registerable (Class<T> registryType) {

        this.registryType = registryType;
    }

    public T setRegistryName (String name) {

        return this.setRegistryName(new ResourceLocation(name));
    }

    public T setRegistryName (String modID, String name) {

        return this.setRegistryName(new ResourceLocation(modID, name));
    }

    @Override
    public T setRegistryName (ResourceLocation name) {

        this.registryName = name;
        return (T) this;
    }

    @Override
    @Nullable
    public final ResourceLocation getRegistryName () {

        return this.registryName;
    }

    @Override
    public final Class<T> getRegistryType () {

        return this.registryType;
    };
}