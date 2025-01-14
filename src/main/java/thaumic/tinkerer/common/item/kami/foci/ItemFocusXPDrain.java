package thaumic.tinkerer.common.item.kami.foci;

import java.awt.*;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumic.tinkerer.common.ThaumicTinkerer;
import thaumic.tinkerer.common.core.handler.ConfigHandler;
import thaumic.tinkerer.common.core.helper.ExperienceHelper;
import thaumic.tinkerer.common.core.proxy.TTCommonProxy;
import thaumic.tinkerer.common.item.ItemXPTalisman;
import thaumic.tinkerer.common.item.kami.ItemKamiResource;
import thaumic.tinkerer.common.lib.LibItemNames;
import thaumic.tinkerer.common.lib.LibResearch;
import thaumic.tinkerer.common.registry.ThaumicTinkererInfusionRecipe;
import thaumic.tinkerer.common.registry.ThaumicTinkererRecipe;
import thaumic.tinkerer.common.research.IRegisterableResearch;
import thaumic.tinkerer.common.research.KamiResearchItem;
import thaumic.tinkerer.common.research.ResearchHelper;

public class ItemFocusXPDrain extends ItemModKamiFocus {

    AspectList visUsage = new AspectList();
    private int lastGiven = 0;

    @Override
    public boolean isVisCostPerTick(ItemStack stack) {
        return true;
    }

    public String getSortingHelper(ItemStack itemstack) {
        return "TTKXP" + super.getSortingHelper(itemstack);
    }

    @Override
    public void onUsingFocusTick(ItemStack paramItemStack, EntityPlayer paramEntityPlayer, int paramInt) {
        if (paramEntityPlayer.worldObj.isRemote) return;

        ItemWandCasting wand = (ItemWandCasting) paramItemStack.getItem();
        AspectList aspects = wand.getAllVis(paramItemStack);

        Aspect aspectToAdd = null;
        int takes = 0;

        while (aspectToAdd == null && takes < 7) {
            lastGiven = lastGiven == 5 ? 0 : lastGiven + 1;

            Aspect aspect = Aspect.getPrimalAspects().get(lastGiven);

            if (aspects.getAmount(aspect) < wand.getMaxVis(paramItemStack)) aspectToAdd = aspect;

            ++takes;
        }

        if (aspectToAdd != null) {
            int xpUse = getXpUse(paramItemStack);
            if (paramEntityPlayer.experienceTotal >= xpUse) {
                ExperienceHelper.drainPlayerXP(paramEntityPlayer, xpUse);
                int amount = wand.getVis(paramItemStack, aspectToAdd) + 500;
                ThaumicTinkerer.log.info(amount);
                wand.storeVis(paramItemStack, aspectToAdd, Math.min(wand.getMaxVis(paramItemStack), amount));
            }
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
        return getFocusColor(par1ItemStack);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        EntityPlayer player = ThaumicTinkerer.proxy.getClientPlayer();
        return player == null ? 0xFFFFFF : Color.HSBtoRGB(player.ticksExisted * 2 % 360 / 360F, 1F, 1F);
    }

    int getXpUse(ItemStack stack) {
        return 15;
    }

    @Override
    protected void addVisCostTooltip(AspectList cost, ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add(StatCollector.translateToLocal(isVisCostPerTick(stack) ? "item.Focus.cost2" : "item.Focus.cost1"));
        list.add(
                " " + EnumChatFormatting.GREEN
                        + StatCollector.translateToLocal("ttmisc.experience")
                        + EnumChatFormatting.WHITE
                        + " x "
                        + getXpUse(stack));
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return visUsage;
    }

    @Override
    public boolean canApplyUpgrade(ItemStack focusstack, EntityPlayer player, FocusUpgradeType type, int rank) {
        return false;
    }

    @Override
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return TTCommonProxy.kamiRarity;
    }

    @Override
    public String getItemName() {
        return LibItemNames.FOCUS_XP_DRAIN;
    }

    @Override
    public IRegisterableResearch getResearchItem() {
        if (!ConfigHandler.enableKami) return null;
        return (IRegisterableResearch) new KamiResearchItem(
                LibResearch.KEY_FOCUS_XP_DRAIN,
                new AspectList().add(Aspect.MIND, 2).add(Aspect.MAGIC, 1).add(Aspect.AURA, 1).add(Aspect.MAN, 1),
                12,
                3,
                5,
                new ItemStack(this)).setParents(LibResearch.KEY_ICHORCLOTH_ROD)
                        .setPages(new ResearchPage("0"), ResearchHelper.infusionPage(LibResearch.KEY_FOCUS_XP_DRAIN));
    }

    @Override
    public ThaumicTinkererRecipe getRecipeItem() {
        return new ThaumicTinkererInfusionRecipe(
                LibResearch.KEY_FOCUS_XP_DRAIN,
                new ItemStack(this),
                12,
                new AspectList().add(Aspect.MIND, 65).add(Aspect.TAINT, 16).add(Aspect.MAGIC, 50).add(Aspect.AURA, 32),
                new ItemStack(Items.ender_pearl),
                new ItemStack(ThaumicTinkerer.registry.getFirstItemFromClass(ItemKamiResource.class)),
                new ItemStack(Items.experience_bottle),
                new ItemStack(Items.diamond),
                new ItemStack(ThaumicTinkerer.registry.getFirstItemFromClass(ItemXPTalisman.class)),
                new ItemStack(Blocks.enchanting_table),
                new ItemStack(ThaumicTinkerer.registry.getFirstItemFromClass(ItemKamiResource.class)));
    }
}
