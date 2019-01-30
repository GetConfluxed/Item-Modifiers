# Item Modifiers [![](http://cf.way2muchnoise.eu/311440.svg)](https://minecraft.curseforge.com/projects/311440) [![](http://cf.way2muchnoise.eu/versions/311440.svg)](https://minecraft.curseforge.com/projects/311440)

This mod adds a modifier system to the game. Modifiers are effects that apply to an item, and can change the stats or give them other abilities. For example, you may get a "Pointy Stone Sword" which gives the sword a 5% increase to damage. This mod allows for both prefixes and suffixes, meaning an item can have two modifiers at the same time. 

Modifiers are applied to items when they are crafted. By default there is a 75% chance for an item to receive a prefix, and a 15% chance for it to receive a suffix. Both of these chances can configured, and setting a chance to 0% will disable it. 

This mod also has a very robust content loading system which allows mods, modpacks, and players to add or configure modifiers. Read further in the documentation for more information on this. 

## Modifiers
The following table contains every single modifier added by the mod. 

## Types

Modifiers have a type value which is used to define what types of items it can be applied to. For example, the `itemmodifiers:sword` type only allows the modifier to be applied to a sword. Types use a namespaced id to allow other mods to define their own types. The following table has all of the types included by default. 

| Type ID                        | Description                                                         |
|:-------------------------------|:--------------------------------------------------------------------|
| itemmodifiers:enchantable      | Any item that is enchantable.                                       |
| itemmodifiers:armor_all        | Any item that is considered armor.                                  |
| itemmodifiers:armor_feet       | Any item that is considered armor, and goes in the feet slot.       |
| itemmodifiers:armor_legs       | Any item that is considered armor, and goes in the legs slot.       |
| itemmodifiers:armor_chestplate | Any item that is considered armor, and goes in the chestplate slot. |
| itemmodifiers:armor_helmet     | Any item that is considered armor, and goes in the helmet slot.     |
| itemmodifiers:mainhand         | Any item that is intended to be in the mainhand.                    |
| itemmodifiers:offhand          | Any item that is intended to be in the offhand.                     |
| itemmodifiers:sword            | Any item that is considered a sword.                                |
| itemmodifiers:tool             | Any item that is considered a tool.                                 |
| itemmodifiers:fishing          | Any item that is considered a fishing rod.                          |
| itemmodifiers:bow              | Any item that is considered a bow.                                  |
| itemmodifiers:wearable         | Any item that is wearable. (pumpkins)                               |
| itemmodifiers:breakable        | Any item with durability.                                           |
| itemmodifiers:food             | Any item that is considered food.                                   |

## Slot Type

Modifiers typically only work when they're in an intended slot. For example, "Speedy Leather Boots" should make the player faster when they're being worn and not when they're in the mainhand or offhand slot. This is a list of all the vanilla slot types. These names are not case sensitive in this mod. 

| Equipment Slot Name |
|:--------------------|
| mainhand            |
| offhand             |
| feet                |
| legs                |
| chest               |
| head                |

## Attributes Type

Attributes are statistics that can be easily modified. This list contains vanilla and forge attributes, but other mods can add more types of attributes.

| Name                 | Id                          | Description                                                                                                             |
|:---------------------|:----------------------------|:------------------------------------------------------------------------------------------------------------------------|
| Follow Range         | generic.followRange         | The range for mobs to track players. This does not affect players.                                                      |
| Max Health           | generic.maxHealth           | The maximum amount of health that the user has.                                                                         |
| Movement Speed       | generic.movementSpeed       | The speed the user moves at.                                                                                            |
| Luck                 | generic.luck                | Improves the amount of loot received from loot tables.                                                                  |
| Attack Speed         | generic.attackSpeed         | How fast the weapon is swung, and the attack cooldown lasts.                                                            |
| Attack Damage        | generic.attackDamage        | The amount of damage dealt.                                                                                             |
| Armor Toughness      | generic.armorToughness      | The toughness of the armor.                                                                                             |
| Knockback Resistance | generic.knockbackResistance | How resistant the user is to knockback.                                                                                 |
| Armor                | generic.armor               | How much armor protection the user has.                                                                                 |
| Fly Speed            | generic.flyingSpeed         | How fast the player moves when flying.                                                                                  |
| Reach Distance       | generic.reachDistance       | How far the entity can reach. This only affects block distance and not entity distance due to a bug/oversight in Forge. |
| Swim Speed           | forge.swimSpeed             | The speed of the entity when moving through water.                                                                      

## Configuring Modifiers

This mod allows for modifiers to be configured and overridden. This is done by placing a json file with the same name as the target modifier in the appropriate location. For example, if you wanted to modify the `itemmodifiers:pointy` modifier, you would place a file here `config/itemmodifiers/overrides/modifiers/itemmodifiers/pointy.json`. If you wanted to override `examplemod:examplemodifier` you would place it here `config/itemmodifiers/overrides/examplemod/examplemodifier.json`

Here is an example of what the json file should look like. 

```json
{
  "type": "itemmodifiers:sword",
  "weight": 10,
  "slot": "mainhand",
  "prefix": true,
  "attributes": [
    {
      "type": "generic.attackDamage",
      "amount": 0.05,
      "operation": 1
    }
  ]
}
```

Please note that `prefix` is optional. It will default to true by default. `slot` is also optional and will default to mainhand. If you wish to disable the modifier completely, setting the `type` value to disabled will prevent the modifier from being loaded. 

## Adding Modifiers (Users/Modpacks)

New modifiers can be added by placing the json file in the `config/itemmodifiers/additions/` folder. Any file added here will be loaded as a new modifier. The json file follows the same format shown in the configuring modifiers section. The id for new modifiers will be `itemmodifiers_config:filename`. Files **MUST** be named all lower case, and have **NO** spaces in the name. 

| Example              | Valid? | Reasoning                                         |
|:---------------------|:------:|:--------------------------------------------------|
| my new modifier.json | ❌      | Spaces are not allowed in the file name.          |
| My_New_Modifier.json | ❌      | Capital letters are not allowed in the file name. |
| my_new_modifier.json | ✔️      | This name is valid.                               |

## Adding Modifiers (Mods)

Mods can add new prefixes by including json files in their `data/modid/itemmodifiers/modifiers` folder. Any json file loaded from this directory will be loaded into the registry automatically. Modifiers loaded this way will also support the overriding system documented in the Configuring Modifiers section. Information on the json specification can also be found in this section. 

Alternatively, mods can define their modifiers using code. These modifiers can be registered to the game by listening to the Register<Modifier> event. Additionally, new modifier types can be registered using the Register<Type> event. You can also register IAttributes by calling `ItemModifierHelper.addAttribute`.
