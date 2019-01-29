package com.getconfluxed.itemmodifiers.data;

import java.util.UUID;

import javax.annotation.Nullable;

import com.getconfluxed.itemmodifiers.ItemModifierHelper;
import com.getconfluxed.itemmodifiers.ItemModifiersMod;
import com.getconfluxed.itemmodifiers.modifiers.Modifier;
import com.getconfluxed.itemmodifiers.type.Type;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.ResourceLocation;

public class ModifierEntry {

    @Expose
    private String type;

    @Expose
    private int weight;

    @Expose
    private final boolean prefix = true;

    @Expose
    private AttributeInfo[] attributes;

    class AttributeInfo {

        @Expose
        private String type;

        @Expose
        private double amount;

        @Expose
        private int operation;
    }

    @Nullable
    public Modifier build (ResourceLocation location) {

        if (this.type != null) {

            if (!"disabled".equals(this.type)) {

                final Type specifiedType = ItemModifiersMod.TYPE_REGISTRY.getValue(new ResourceLocation(this.type));

                if (specifiedType != null) {

                    final Modifier modifier = new Modifier(specifiedType, this.weight, this.prefix);

                    for (final AttributeInfo info : this.attributes) {

                        final IAttribute attribute = ItemModifierHelper.getAttribute(info.type);

                        if (attribute != null) {

                            final String name = location.toString() + info.type + info.operation + info.amount;
                            final UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
                            modifier.setAttribute(attribute, new AttributeModifier(uuid, name, info.amount, info.operation));
                        }

                        else {

                            ItemModifiersMod.LOG.error("Failed to load modifier {}. No attribute found for {}.", location.toString(), info.type);
                        }
                    }

                    return modifier;
                }

                else {

                    ItemModifiersMod.LOG.error("Failed to load modifier {}. The type {} is missing or malformed.", location.toString(), this.type);
                }
            }

            else {

                ItemModifiersMod.LOG.debug("The modifier {} was disabled.", location.toString());
            }
        }

        else {

            ItemModifiersMod.LOG.error("Failed to load modifier {}. No type value was specified.", location.toString());
        }

        return null;
    }
}