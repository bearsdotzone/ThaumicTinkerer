/**
 * This class was created by <Vazkii>. It's distributed as part of the ThaumicTinkerer Mod.
 *
 * ThaumicTinkerer is Open Source and distributed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * License (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 *
 * ThaumicTinkerer is a Derivative Work on Thaumcraft 4. Thaumcraft 4 (c) Azanor 2012
 * (http://www.minecraftforum.net/topic/1585216-)
 *
 * File Created @ [29 Sep 2013, 11:52:00 (GMT)]
 */
package thaumic.tinkerer.common.enchantment;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import thaumic.tinkerer.common.lib.LibEnchantIDs;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModEnchantmentHandler {

    public final String NBTLastTarget = "TTEnchantLastTarget";
    public final String NBTLastTarget2 = "TTEnchantLastTarget2";

    public final String NBTSuccessiveStrike = "TTEnchantSuccessiveStrike";

    public final String NBTTunnelDirection = "TTEnchantTunnelDir";

    @SubscribeEvent
    public void onEntityDamaged(LivingHurtEvent event) {
        if (event.source.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase) event.source.getEntity();
            ItemStack heldItem = attacker.getHeldItem();

            if (heldItem == null) return;

            if (attacker instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) attacker;

                ItemStack legs = player.getCurrentArmor(1);
                int pounce = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.pounce, legs);
                if (pounce > 0) {
                    if (player.worldObj.getBlock(
                            (int) Math.floor(player.posX),
                            (int) Math.floor(player.posY) - 1,
                            (int) Math.floor(player.posZ)) == net.minecraft.init.Blocks.air) {

                        event.ammount *= 1 + (.25 * pounce);
                    }
                }
            }

            int finalStrike = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.finalStrike, heldItem);
            if (finalStrike > 0) {
                Random rand = new Random();
                if (rand.nextInt(20 - finalStrike) == 0) {
                    event.ammount *= 3;
                }
            }

            int valiance = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.valiance, heldItem);
            if (valiance > 0) {
                if (attacker.getHealth() / attacker.getMaxHealth() < .5F) {
                    event.ammount *= (1 + .1 * valiance);
                }
            }

            int focusedStrikes = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.focusedStrike, heldItem);

            int dispersedStrikes = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.dispersedStrikes, heldItem);

            if (focusedStrikes > 0 || dispersedStrikes > 0) {
                if (heldItem.stackTagCompound == null) {
                    heldItem.stackTagCompound = new NBTTagCompound();
                }
                UUID lastTarget = new UUID(
                        heldItem.stackTagCompound.getLong(NBTLastTarget),
                        heldItem.stackTagCompound.getLong(NBTLastTarget2));
                int successiveStrikes = heldItem.stackTagCompound.getInteger(NBTSuccessiveStrike);
                UUID entityId = event.entityLiving.getUniqueID();

                if (!lastTarget.equals(entityId)) {
                    successiveStrikes = 0;
                    heldItem.stackTagCompound.setLong(NBTLastTarget, entityId.getMostSignificantBits());
                    heldItem.stackTagCompound.setLong(NBTLastTarget2, entityId.getLeastSignificantBits());
                } else {
                    successiveStrikes = Math.max(successiveStrikes + 1, 5);
                }
                heldItem.stackTagCompound.setInteger(NBTSuccessiveStrike, successiveStrikes);

                if (focusedStrikes > 0) {
                    event.ammount /= 2;
                    event.ammount += (.5 * Math.max(successiveStrikes, 5) * event.ammount * focusedStrikes);
                }
                if (dispersedStrikes > 0) {
                    event.ammount *= 1 + 0.6 * dispersedStrikes + (successiveStrikes / 5);
                    event.ammount /= 1 + 0.25 * dispersedStrikes + (successiveStrikes * 2.5);
                }
            }

            int vampirism = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.idVampirism, heldItem);
            if (vampirism > 0) {
                attacker.heal(vampirism);
                event.entityLiving.worldObj.playSoundAtEntity(event.entityLiving, "thaumcraft:zap", 0.6F, 1F);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityUpdate(LivingUpdateEvent event) {
        final double min = -0.0784000015258789;

        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            int slowfall = EnchantmentHelper
                    .getMaxEnchantmentLevel(LibEnchantIDs.idSlowFall, player.inventory.armorInventory);
            if (slowfall > 0 && !event.entityLiving.isSneaking()
                    && event.entityLiving.motionY < min
                    && event.entityLiving.fallDistance >= 2.9) {
                event.entityLiving.motionY /= 1 + slowfall * 0.33F;
                event.entityLiving.fallDistance = Math.max(2.9F, player.fallDistance - slowfall / 3F);

                player.worldObj.spawnParticle(
                        "cloud",
                        player.posX + 0.25,
                        player.posY - 1,
                        player.posZ + 0.25,
                        -player.motionX,
                        player.motionY,
                        -player.motionZ);
            }

            ItemStack heldItem = player.getHeldItem();

            if (heldItem == null) return;

            int quickDraw = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.idQuickDraw, heldItem);
            ItemStack usingItem = player.itemInUse;
            int time = player.itemInUseCount;
            if (quickDraw > 0 && usingItem != null && usingItem.getItem() instanceof ItemBow)
                if ((usingItem.getItem().getMaxItemUseDuration(usingItem) - time) % (6 - quickDraw) == 0)
                    player.itemInUseCount = time - 1;
        }
    }

    @SubscribeEvent
    public void onPlayerJump(LivingJumpEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            int boost = EnchantmentHelper
                    .getMaxEnchantmentLevel(LibEnchantIDs.idAscentBoost, player.inventory.armorInventory);

            if (boost >= 1 && !player.isSneaking()) player.motionY *= (boost + 2) / 2D;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onFall(LivingFallEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            ItemStack boots = ((EntityPlayer) event.entityLiving).getCurrentArmor(0);
            int shockwave = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.shockwave, boots);
            if (shockwave > 0) {
                for (EntityLivingBase target : (List<EntityLivingBase>) event.entity.worldObj.getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        AxisAlignedBB.getBoundingBox(
                                event.entity.posX - 10,
                                event.entity.posY - 10,
                                event.entity.posZ - 10,
                                event.entity.posX + 10,
                                event.entity.posY + 10,
                                event.entity.posZ + 10))) {
                    if (target != event.entity && event.distance > 3) {
                        target.attackEntityFrom(DamageSource.fall, .1F * shockwave * event.distance);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        ItemStack item = event.getPlayer().getCurrentEquippedItem();
        int tunnel = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.tunnel, item);
        if (tunnel > 0) {
            if (item.stackTagCompound == null) {
                item.stackTagCompound = new NBTTagCompound();
            }
            float dir = event.getPlayer().rotationYaw;
            item.stackTagCompound.setFloat(NBTTunnelDirection, dir);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGetHarvestSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.entityPlayer.getHeldItem();

        if (heldItem == null) return;

        int shatter = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.shatter, heldItem);
        if (shatter > 0) {
            if (event.block.getBlockHardness(event.entityPlayer.worldObj, 0, 0, 0) > 20F) {
                event.newSpeed *= (3 * shatter);
            } else {
                event.newSpeed *= .8;
            }
        }

        int tunnel = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.tunnel, heldItem);
        if (tunnel > 0) {
            float dir = event.entityPlayer.rotationYaw;
            if (heldItem.stackTagCompound != null && heldItem.stackTagCompound.hasKey(NBTTunnelDirection)) {
                float oldDir = heldItem.stackTagCompound.getFloat(NBTTunnelDirection);
                float dif = Math.abs(oldDir - dir);
                if (dif < 50) {
                    event.newSpeed *= (1 + (.2 * tunnel));
                } else {
                    event.newSpeed *= .3;
                }
            }
        }

        int desintegrate = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.idDesintegrate, heldItem);
        int autoSmelt = EnchantmentHelper.getEnchantmentLevel(LibEnchantIDs.idAutoSmelt, heldItem);

        boolean desintegrateApplies = desintegrate > 0 && event.block.blockHardness <= 1.5F;
        boolean autoSmeltApplies = autoSmelt > 0 && event.block.getMaterial() == Material.wood;

        if (desintegrateApplies || autoSmeltApplies) {
            heldItem.damageItem(1, event.entityPlayer);
            event.newSpeed = Float.MAX_VALUE;
        } else if (desintegrate > 0 || autoSmelt > 0) event.setCanceled(true);
    }
}
