/**
 * This class was created by <Vazkii>. It's distributed as part of the ThaumicTinkerer Mod.
 *
 * ThaumicTinkerer is Open Source and distributed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * License (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 *
 * ThaumicTinkerer is a Derivative Work on Thaumcraft 4. Thaumcraft 4 (c) Azanor 2012
 * (http://www.minecraftforum.net/topic/1585216-)
 *
 * File Created @ [8 Sep 2013, 15:44:29 (GMT)]
 */
package thaumic.tinkerer.common.registry;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

import thaumic.tinkerer.client.core.helper.IconHelper;
import thaumic.tinkerer.common.core.handler.ModCreativeTab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemBase extends Item implements ITTinkererItem {

    public ItemBase() {
        super();
        setCreativeTab(ModCreativeTab.INSTANCE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        itemIcon = IconHelper.forItem(par1IconRegister, this);
    }

    @Override
    public ArrayList<Object> getSpecialParameters() {
        return null;
    }

    @Override
    public boolean shouldRegister() {
        return true;
    }

    @Override
    public boolean shouldDisplayInTab() {
        return true;
    }

    @Override
    public ThaumicTinkererRecipe getRecipeItem() {
        return null;
    }
}
