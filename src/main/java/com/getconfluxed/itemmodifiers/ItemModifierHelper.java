package com.getconfluxed.itemmodifiers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.getconfluxed.itemmodifiers.modifiers.Modifier;
import com.getconfluxed.itemmodifiers.type.Type;

import net.darkhax.bookshelf.util.StackUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ItemModifierHelper {

    private static final String MODIFIER_TAG_KEY = "ItemModifiers";
    private static final Map<String, IAttribute> knownAttributes = new HashMap<>();

    static {

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
     * Gets the modifier on an ItemStack. The result of this method is nullable!
     *
     * @param stack The ItemStack to read data from.
     * @return The modifier on the stack, or null if no stack is valid.
     */
    @Nullable
    public static Modifier getModifier (ItemStack stack) {

        final NBTTagCompound stackTag = stack.getTagCompound();

        if (stackTag != null && stackTag.hasKey(MODIFIER_TAG_KEY)) {

            final NBTTagCompound modifierTag = stackTag.getCompoundTag(MODIFIER_TAG_KEY);
            return ItemModifiersMod.MODIFIER_REGISTRY.getValue(new ResourceLocation(modifierTag.getString("id")));
        }

        return null;
    }

    /**
     * Sets a modifier to an ItemStack. If a modifier already exists
     * {@link #removeModifiers(ItemStack)} will be called automatically. This method is also
     * responsible for firing the {@link Modifier#onApplied(ItemStack)} hook.
     *
     * @param stack The ItemStack to set the modifier to.
     * @param modifier The modifier to apply.
     */
    public static void setModifier (ItemStack stack, Modifier modifier) {

        final NBTTagCompound stackTag = StackUtils.prepareStackTag(stack);

        // Handles the removal of any existing modifiers.
        if (stackTag.hasKey(MODIFIER_TAG_KEY)) {

            removeModifiers(stack);
        }

        // Handle adding the new modifier
        final NBTTagCompound modifierTag = new NBTTagCompound();
        modifierTag.setString("id", modifier.getRegistryName().toString());
        modifier.onApplied(stack);
        stackTag.setTag(MODIFIER_TAG_KEY, modifierTag);
    }

    /**
     * Removes all modifier data from an ItemStack. This includes deleting the NBT tag and
     * firing the {@link Modifier#onRemoved(ItemStack)} hook.
     *
     * @param stack The ItemStack to clean up.
     */
    public static void removeModifiers (ItemStack stack) {

        final NBTTagCompound stackTag = stack.getTagCompound();

        if (stackTag != null && stackTag.hasKey(MODIFIER_TAG_KEY)) {

            final NBTTagCompound modifierTag = stackTag.getCompoundTag(MODIFIER_TAG_KEY);

            // Retrieve the modifier from the registry.
            final Modifier existingModifier = ItemModifiersMod.MODIFIER_REGISTRY.getValue(new ResourceLocation(modifierTag.getString("id")));

            // Fire the removal cleanup hook
            if (existingModifier != null) {

                existingModifier.onRemoved(stack);
            }

            // Completely remove the modifier tag.
            stackTag.removeTag(MODIFIER_TAG_KEY);
        }
    }

    public static void addAttribute (IAttribute attribute) {

        final String name = attribute.getName();

        if (knownAttributes.containsKey(name)) {

            final IAttribute existing = knownAttributes.get(name);
            ItemModifiersMod.LOG.error("Could not register attribute {}. The {} attribute is already registered.", attribute.getName(), existing.getName(), name);
        }

        else {

            knownAttributes.put(name, attribute);
        }
    }

    public static IAttribute getAttribute (String name) {

        return knownAttributes.get(name);
    }

    public static Map<String, IAttribute> getKnownAttributes () {

        return Collections.unmodifiableMap(knownAttributes);
    }
}