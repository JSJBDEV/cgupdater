package uk.co.conclipsegames.projDart.Item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class Items {
    public static Item forceGem;
    public static ItemDust itemDust;
    public static ItemIngot itemIngot;

    public static final void init() {
        forceGem = new Item().setUnlocalizedName("Force Gem").setCreativeTab(CreativeTabs.tabMisc);
        itemDust = new ItemDust();
        itemIngot = new ItemIngot();
        GameRegistry.registerItem(forceGem, "forceGem");
        GameRegistry.registerItem(itemDust, "itemDust");
        GameRegistry.registerItem(itemIngot, "itemIngot");
    }
}
