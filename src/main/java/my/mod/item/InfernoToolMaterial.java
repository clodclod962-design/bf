package my.mod.item;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class InfernoToolMaterial implements ToolMaterial {
    public static final InfernoToolMaterial INSTANCE = new InfernoToolMaterial();
    @Override public int getDurability() { return 2500; }
    @Override public float getMiningSpeedMultiplier() { return 9.0f; }
    @Override public float getAttackDamage() { return 5.0f; }
    @Override public int getMiningLevel() { return 4; }
    @Override public int getEnchantability() { return 20; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.ofItems(Items.NETHERITE_INGOT); }
}
