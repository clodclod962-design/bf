package my.mod.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Кастомные предметы. Регистрируются и на клиенте, и на сервере - мод должен стоять на обеих сторонах. */
public class ModItems {
    public static final Item INFERNO_SWORD = new SwordItem(InfernoToolMaterial.INSTANCE, 3, -2.4f,
            new Item.Settings().fireproof());
    public static final Item INFERNO_HELMET = new ArmorItem(InfernoArmorMaterial.INSTANCE,
            ArmorItem.Type.HELMET, new Item.Settings().fireproof());
    public static final Item INFERNO_CHESTPLATE = new ArmorItem(InfernoArmorMaterial.INSTANCE,
            ArmorItem.Type.CHESTPLATE, new Item.Settings().fireproof());
    public static final Item INFERNO_LEGGINGS = new ArmorItem(InfernoArmorMaterial.INSTANCE,
            ArmorItem.Type.LEGGINGS, new Item.Settings().fireproof());
    public static final Item INFERNO_BOOTS = new ArmorItem(InfernoArmorMaterial.INSTANCE,
            ArmorItem.Type.BOOTS, new Item.Settings().fireproof());

    private static void reg(String id, Item item) {
        Registry.register(Registries.ITEM, new Identifier("mymod", id), item);
    }

    public static void init() {
        reg("inferno_sword", INFERNO_SWORD);
        reg("inferno_helmet", INFERNO_HELMET);
        reg("inferno_chestplate", INFERNO_CHESTPLATE);
        reg("inferno_leggings", INFERNO_LEGGINGS);
        reg("inferno_boots", INFERNO_BOOTS);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(e -> {
            e.add(INFERNO_SWORD);
            e.add(INFERNO_HELMET);
            e.add(INFERNO_CHESTPLATE);
            e.add(INFERNO_LEGGINGS);
            e.add(INFERNO_BOOTS);
        });
    }
}
