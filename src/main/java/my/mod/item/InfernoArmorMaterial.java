package my.mod.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class InfernoArmorMaterial implements ArmorMaterial {
    public static final InfernoArmorMaterial INSTANCE = new InfernoArmorMaterial();
    private static final int[] DURABILITY = {481, 555, 592, 407}; // boots, legs, chest, helmet (x multiplier)
    private static final int[] PROTECTION = {4, 7, 9, 4};

    private static int idx(ArmorItem.Type t) {
        return switch (t) { case BOOTS -> 0; case LEGGINGS -> 1; case CHESTPLATE -> 2; case HELMET -> 3; };
    }
    @Override public int getDurability(ArmorItem.Type type) { return DURABILITY[idx(type)]; }
    @Override public int getProtection(ArmorItem.Type type) { return PROTECTION[idx(type)]; }
    @Override public int getEnchantability() { return 20; }
    @Override public SoundEvent getEquipSound() { return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.ofItems(Items.NETHERITE_INGOT); }
    @Override public String getName() { return "mymod:inferno"; }
    @Override public float getToughness() { return 3.5f; }
    @Override public float getKnockbackResistance() { return 0.1f; }
}
