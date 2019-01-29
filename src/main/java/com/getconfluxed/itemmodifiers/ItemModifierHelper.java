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

    @Nullable
    public static Modifier applyRandomModifier (ItemStack stack, boolean prefix) {

        final Set<Type> types = ItemModifierHelper.getApplicableTypes(stack);

        final WeightedSelector<Modifier> weight = new WeightedSelector<>();

        for (final Type type : types) {

            for (final Modifier modifier : prefix ? ItemModifiersMod.PREFIXES.get(type) : ItemModifiersMod.SUFFIXES.get(type)) {

                weight.addEntry(modifier, modifier.getWeight());
            }
        }

        if (!weight.getEntries().isEmpty()) {

            final Modifier modifier = weight.getRandomEntry().getEntry();
            ItemModifierHelper.setModifier(stack, modifier);
            return modifier;
        }

        return null;
    }

    public static NBTTagCompound getModifierTag (ItemStack stack, boolean create) {

        if (stack.hasTagCompound()) {

            final NBTTagCompound stackTag = stack.getTagCompound();

            if (stackTag != null) {

                final NBTTagCompound modifierTag = stackTag.getCompoundTag(MODIFIER_TAG_KEY);

                if (modifierTag != null) {

                    return modifierTag;
                }
            }
        }

        if (create) {

            final NBTTagCompound stackTag = StackUtils.prepareStackTag(stack);
            final NBTTagCompound modifierTag = new NBTTagCompound();
            stackTag.setTag(MODIFIER_TAG_KEY, modifierTag);
            return modifierTag;
        }

        return null;
    }

    public static Modifier[] getModifiers (ItemStack stack) {

        return new Modifier[] { getPrefix(stack), getSuffix(stack) };
    }

    public static Modifier getPrefix (ItemStack stack) {

        final NBTTagCompound tag = getModifierTag(stack, false);

        if (tag != null) {

            final NBTTagCompound modifierTag = tag.getCompoundTag("Prefix");
            return ItemModifiersMod.MODIFIER_REGISTRY.getValue(new ResourceLocation(modifierTag.getString("Id")));
        }

        return null;
    }

    public static Modifier getSuffix (ItemStack stack) {

        final NBTTagCompound tag = getModifierTag(stack, false);

        if (tag != null) {

            final NBTTagCompound modifierTag = tag.getCompoundTag("Suffix");
            return ItemModifiersMod.MODIFIER_REGISTRY.getValue(new ResourceLocation(modifierTag.getString("Id")));
        }

        return null;
    }

    public static void setModifier (ItemStack stack, Modifier modifier) {

        final NBTTagCompound tag = getModifierTag(stack, true);
        final Modifier existing = modifier.isPrefix() ? getPrefix(stack) : getSuffix(stack);

        if (existing != null) {

            existing.onRemoved(stack);
        }

        final NBTTagCompound modifierTag = new NBTTagCompound();
        modifierTag.setString("Id", modifier.getRegistryName().toString());
        tag.setTag(modifier.isPrefix() ? "Prefix" : "Suffix", modifierTag);
        modifier.onApplied(stack);
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