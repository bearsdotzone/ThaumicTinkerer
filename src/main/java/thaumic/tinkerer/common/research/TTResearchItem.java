/**
 * This class was created by <Vazkii>. It's distributed as part of the ThaumicTinkerer Mod.
 * <p>
 * ThaumicTinkerer is Open Source and distributed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * License (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * <p>
 * ThaumicTinkerer is a Derivative Work on Thaumcraft 4. Thaumcraft 4 (c) Azanor 2012
 * (http://www.minecraftforum.net/topic/1585216-)
 * <p>
 * File Created @ [8 Sep 2013, 16:53:14 (GMT)]
 */
package thaumic.tinkerer.common.research;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.research.ResearchPage.PageType;
import thaumic.tinkerer.common.core.handler.ConfigHandler;
import thaumic.tinkerer.common.lib.LibResearch;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TTResearchItem extends ResearchItem implements IRegisterableResearch {

    public int warp = 0;
    /**
     * When ModTweaker scripts replace ThaumicTinkerer pages, it call {@link #setPages(ResearchPage...)} and if one of
     * provided pages is text page, Thaumic Tinkerer will generate its own {@link ResearchPage#text} adding prefixes to
     * the existing one that breaks localization.
     * <p>
     * So this variable will prevent Thaumic Tinkerer from changing page's text that came from MineTweaker scripts.
     */
    private boolean firstLoad = true;

    public TTResearchItem(String par1) {
        super(par1, LibResearch.CATEGORY_THAUMICTINKERER);
    }

    public TTResearchItem(String par1, AspectList tags, int par3, int par4, int par5, ItemStack icon) {
        super(par1, LibResearch.CATEGORY_THAUMICTINKERER, tags, par3, par4, par5, icon);
    }

    public TTResearchItem(String par1, AspectList tags, int par3, int par4, int par5, ResourceLocation icon) {
        super(par1, LibResearch.CATEGORY_THAUMICTINKERER, tags, par3, par4, par5, icon);
    }

    public TTResearchItem setWarp(int warp) {
        this.warp = warp;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getName() {
        return StatCollector.translateToLocal("ttresearch.name." + key);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getText() {
        return (ConfigHandler.useTootlipIndicators ? StatCollector.translateToLocal(getPrefix()) : "")
                + StatCollector.translateToLocal("ttresearch.lore." + key);
    }

    String getPrefix() {
        return "ttresearch.prefix";
    }

    @Override
    public ResearchItem setPages(ResearchPage... par) {
        for (ResearchPage page : par) {
            if (page.type == PageType.TEXT && firstLoad) {
                page.text = "ttresearch.page." + key + "." + page.text;
            }

            if (checkInfusion() && page.type == PageType.INFUSION_CRAFTING) {
                if (parentsHidden == null || parentsHidden.length == 0) parentsHidden = new String[] { "INFUSION" };
                else {
                    String[] newParents = new String[parentsHidden.length + 1];
                    newParents[0] = "INFUSION";
                    // noinspection ManualArrayCopy
                    for (int i = 0; i < parentsHidden.length; i++) newParents[i + 1] = parentsHidden[i];
                    parentsHidden = newParents;
                }
            }
        }

        firstLoad = false;
        return super.setPages(par);
    }

    boolean checkInfusion() {
        return true;
    }

    @Override
    public void registerResearch() {
        registerResearchItem();
        if (warp != 0) {
            ThaumcraftApi.addWarpToResearch(key, warp);
        }
    }
}
