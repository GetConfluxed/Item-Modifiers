package com.getconfluxed.itemmodifiers.modifiers;

import java.util.UUID;

import com.getconfluxed.itemmodifiers.type.Type;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ModifierAttributeBase extends Modifier {

    private final Type type;
    private final EntityEquipmentSlot applicableSlot;
    private final Multimap<String, AttributeModifier> attributes;

    public ModifierAttributeBase (Type type, EntityEquipmentSlot applicableSlot) {

        this.type = type;
        this.applicableSlot = applicableSlot;
        this.attributes = HashMultimap.create();
    }

    public ModifierAttributeBase setAttack (double value, int operation) {

        return this.setAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, value, operation);
    }

    public ModifierAttributeBase setAttribute (IAttribute attribute, double value, int operation) {

        final String name = this.getRegistryName().toString() + attribute.getName();
        final UUID id = UUID.nameUUIDFromBytes(name.getBytes());
        this.attributes.put(attribute.getName(), new AttributeModifier(id, name, value, operation));
        return this;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers (EntityEquipmentSlot slot, ItemStack stack) {

        return slot == this.applicableSlot ? this.attributes : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public Type getType () {

        return this.type;
    }

}