package my.mod.item;

import net.fabricmc.api.ModInitializer;

public class MyModCommon implements ModInitializer {
    @Override
    public void onInitialize() { ModItems.init(); }
}
