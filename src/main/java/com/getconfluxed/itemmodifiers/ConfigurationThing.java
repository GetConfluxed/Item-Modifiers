package com.getconfluxed.itemmodifiers;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationThing {

    /**
     * Forges configuration object.
     */
    private final Configuration config;

    private float prefixChance;
    private float suffixChance;
    private boolean allowBoth;

    public ConfigurationThing (File file) {

        this.config = new Configuration(file);
        this.syncConfigData();
    }

    private void syncConfigData () {

        this.prefixChance = this.config.getFloat("prefixChance", Configuration.CATEGORY_GENERAL, 0.75f, 0, 1, "The chance that an item will get a prefix when crafted.");
        this.suffixChance = this.config.getFloat("suffixChance", Configuration.CATEGORY_GENERAL, 0.15f, 0, 1, "The chance that an item will get a suffix when crafted.");
        this.allowBoth = this.config.getBoolean("allowBoth", Configuration.CATEGORY_GENERAL, true, "Whether or not players should be allowed to get both a prefix and a suffix on the same item.");

        // If the config file isn't the same, save it to the file.
        if (this.config.hasChanged()) {

            this.config.save();
        }
    }

    public float getPrefixChance () {

        return this.prefixChance;
    }

    public float getSuffixChance () {

        return this.suffixChance;
    }

    public boolean isAllowBoth () {

        return this.allowBoth;
    }
}