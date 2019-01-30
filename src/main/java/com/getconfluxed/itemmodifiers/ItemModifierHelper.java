package com.getconfluxed.itemmodifiers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.getconfluxed.itemmodifiers.modifiers.Modifier;
import com.getconfluxed.itemmodifiers.type.Type;

import net.darkhax.bookshelf.lib.WeightedSelector;
import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/**
 * This class contains a bunch of static utility methods for interacting with the mod and the
 * data of the mod. These should be used over custom methods where possible, as new events and
 * hooks will be added to these methods, and custom methods may not implement those events and
 * hooks.
 */
public class ItemModifierHelper {

    /**
     * The constant name for the modifier data tag.
     */
    private static final String MODIFIER_TAG_KEY = "ItemModifiers";

    /**
     * A map of known attributes. Other mods can expand this by called
     * {@link #addAttribute(IAttribute)}.
     */
    private static final Map<String, IAttribute> KNOWN_ATTRIBUTES = new HashMap<>();

    static {

        // Register the vanilla base attributes
        addAttribute(SharedMonsterAttributes.MAX_HEALTH);
        addAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        addAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
        addAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        addAttribute(SharedMonsterAttributes.FLYING_SPEED);
        addAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        addAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        addAttribute(SharedMonsterAttributes.ARMOR);
        addAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS);
        addAttribute(SharedMonsterAttributes.LUCK);

        // Register Forge's attributes.
        addAttribute(EntityPlayer.REACH_DISTANCE);
        addAttribute(EntityLivingBase.SWIM_SPEED);
    }

    /**
     * Gets a set of all applicable modifiers for an ItemStack.
     *
     * @param stack The item to check for.
     * @return A set of all applicable modifiers. It will be empty if there are no results
     *         found.
     */
    public static Set<Type> getApplicableTypes (ItemStack stack) {

        final Set<Type> types = new HashSet<>();

        // Search through all available types
        for (final Type type : ItemModifiersMod.TYPE_REGISTRY) {

            // Add the type if it is valid.
            if (type.isValidItem(stack)) {

                types.add(type);
            }
        }

        return types;
    }

    /**
     * Applies a random modifier to the item. If the item already has the specified modifier it
     * will be replaced.
     *
     * @param stack The ItemStack to apply modifiers to.
     * @param prefix Whether or not you want to apply a prefix or a suffix.
     * @return The modifier that was applied. This can be null if no modifiers were given.
     */
    @Nullable
    public static Modifier applyRandomModifier (ItemStack stack, boolean prefix) {

        // Gets all the applicable modifier types for the item.
        final Set<Type> types = ItemModifierHelper.getApplicableTypes(stack);

        // Creates a new weighted table.
        final WeightedSelector<Modifier> weightedTable = new WeightedSelector<>();

        for (final Type type : types) {

            // Loop through all the applicable prefixes for the item and desired modifier type.
            for (final Modifier modifier : prefix ? ItemModifiersMod.PREFIXES.get(type) : ItemModifiersMod.SUFFIXES.get(type)) {

                // Populate the weightedTable with modifiers.
                weightedTable.addEntry(modifier, modifier.getWeight());
            }
        }

        // Prevent getting an entry if there are none to be gotten. Prevents out of bounds.
        if (!weightedTable.getEntries().isEmpty()) {

            // Select a random modifier based on weight.
            final Modifier modifier = weightedTable.getRandomEntry().getEntry();

            // Apply the modifier.
            ItemModifierHelper.setModifier(stack, modifier);
            return modifier;
        }

        return null;
    }

    /**
     * Gets the modifier tag for an item, or optionally creates it.
     *
     * @param stack The item to get the modifier tag on.
     * @param create Whether or not the tag should be created if it does not exist.
     * @return The tag, or null if it couldn't be found and create param was false.
     */
    @Nullable
    public static NBTTagCompound getModifierTag (ItemStack stack, boolean create) {

        // Check if the item already has a tag.
        if (stack.hasTagCompound()) {

            // Grab the modifier tag.
            final NBTTagCompound modifierTag = stack.getTagCompound().getCompoundTag(MODIFIER_TAG_KEY);

            // If the tag is found, just return it now. Otherwise go on to check if it should
            // be created.
            if (modifierTag != null) {

                return modifierTag;
            }
        }

        // If the create param is true, and the tag doesn't already exist.
        if (create) {

            // Get or create the stack tag compound.
            final NBTTagCompound stackTag = StackUtils.prepareStackTag(stack);
            // Create, set, return the modifier tag.
            final NBTTagCompound modifierTag = new NBTTagCompound();
            stackTag.setTag(MODIFIER_TAG_KEY, modifierTag);
            return modifierTag;
        }

        return null;
    }

    /**
     * Get all the modifiers for a stack. This is just the prefix and the suffix right now. Be
     * aware that these entries may be null!
     *
     * @param stack The stack to read modifiers from.
     * @return An array of the modifiers. Please be aware that these can be null.
     */
    public static Modifier[] getModifiers (ItemStack stack) {

        return new Modifier[] { getPrefix(stack), getSuffix(stack) };
    }

    /**
     * Reads a prefix modifier from an ItemStack.
     *
     * @param stack The ItemStack to read from.
     * @return The modifier that was found. This can be null.
     */
    @Nullable
    public static Modifier getPrefix (ItemStack stack) {

        // Get the modifier tag
        final NBTTagCompound tag = getModifierTag(stack, false);

        // If tag isn't null, read the modifier.
        if (tag != null) {

            final NBTTagCompound modifierTag = tag.getCompoundTag("Prefix");
            return ItemModifiersMod.MODIFIER_REGISTRY.getValue(new ResourceLocation(modifierTag.getString("Id")));
        }

        return null;
    }

    /**
     * Reads a suffix modifier from an ItemStack.
     *
     * @param stack The ItemStack to read from.
     * @return The modifier that was found. This can be null.
     */
    @Nullable
    public static Modifier getSuffix (ItemStack stack) {

        // Get the modifier tag.
        final NBTTagCompound tag = getModifierTag(stack, false);

        // If tag isn't null, read the modifier.
        if (tag != null) {

            final NBTTagCompound modifierTag = tag.getCompoundTag("Suffix");
            return ItemModifiersMod.MODIFIER_REGISTRY.getValue(new ResourceLocation(modifierTag.getString("Id")));
        }

        return null;
    }

    /**
     * Sets a modifier on an ItemStack. This will automatically handle storing the modifier as
     * a prefix/suffix depending on the results of {@link Modifier#isPrefix()}. This method
     * will also fire the {@link Modifier#onRemoved(ItemStack)} hook if there is a previous
     * modifier.
     *
     * @param stack The stack to apply it to.
     * @param modifier The modifier to add.
     */
    public static void setModifier (ItemStack stack, Modifier modifier) {

        // Get or create the modifier tag.
        final NBTTagCompound tag = getModifierTag(stack, true);

        // Try to get the existing modifier.
        final Modifier existing = modifier.isPrefix() ? getPrefix(stack) : getSuffix(stack);

        // If the existing modifier exists, call the onRemoved hook.
        if (existing != null) {

            existing.onRemoved(stack);
        }

        // Replace the modifier tag with the new modifier tag.
        final NBTTagCompound modifierTag = new NBTTagCompound();
        modifierTag.setString("Id", modifier.getRegistryName().toString());
        tag.setTag(modifier.isPrefix() ? "Prefix" : "Suffix", modifierTag);

        // Call the applied hook on the new modifier.
        modifier.onApplied(stack);
    }

    /**
     * Adds an IAttribute to the list of known attributes. This is needed for serialization of
     * attributes from json and other things.
     *
     * @param attribute The attribute to register.
     */
    public static void addAttribute (IAttribute attribute) {

        final String name = attribute.getName();

        if (KNOWN_ATTRIBUTES.containsKey(name)) {

            final IAttribute existing = KNOWN_ATTRIBUTES.get(name);
            ItemModifiersMod.LOG.error("Could not register attribute {}. The {} attribute is already registered.", attribute.getName(), existing.getName(), name);
        }

        else {

            KNOWN_ATTRIBUTES.put(name, attribute);
        }
    }

    /**
     * Gets an attribute by it's name.
     *
     * @param name The name of the attribute.
     * @return The attribute registered to this name. This can be null.
     */
    @Nullable
    public static IAttribute getAttribute (String name) {

        return KNOWN_ATTRIBUTES.get(name);
    }

    /**
     * Gets all the known attributes as a map.
     *
     * @return An unmodifiable map containing all the known attributes.
     */
    public static Map<String, IAttribute> getKnownAttributes () {

        return Collections.unmodifiableMap(KNOWN_ATTRIBUTES);
    }
}