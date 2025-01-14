/**
 * This class was created by <Vazkii>. It's distributed as part of the ThaumicTinkerer Mod.
 *
 * ThaumicTinkerer is Open Source and distributed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * License (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 *
 * ThaumicTinkerer is a Derivative Work on Thaumcraft 4. Thaumcraft 4 (c) Azanor 2012
 * (http://www.minecraftforum.net/topic/1585216-)
 *
 * File Created @ [9 Sep 2013, 15:54:36 (GMT)]
 */
package thaumic.tinkerer.common.block.tile.tablet;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import thaumcraft.common.lib.FakeThaumcraftPlayer;

import com.mojang.authlib.GameProfile;

public class TabletFakePlayer extends FakeThaumcraftPlayer {

    TileAnimationTablet tablet;

    public TabletFakePlayer(TileAnimationTablet tablet) { // ,String name) {
        super(
                tablet.getWorldObj(),
                new GameProfile(UUID.fromString("a8f026a0-135b-11e4-9191-0800200c9a66"), "[ThaumcraftTablet]"));
        // super(tablet.getWorldObj(),"[ThaumcraftTablet]");
        this.tablet = tablet;
    }

    @Override
    public void setDead() {
        inventory.clearInventory(null, -1);
        super.setDead();
    }

    @Override
    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
        // NO-OP
    }

    @Override
    public void onUpdate() {
        capabilities.isCreativeMode = false;

        posX = tablet.xCoord + 0.5;
        posY = tablet.yCoord + 1.6;
        posZ = tablet.zCoord + 0.5;

        if (riddenByEntity != null) riddenByEntity.ridingEntity = null;
        if (ridingEntity != null) ridingEntity.riddenByEntity = null;
        riddenByEntity = null;
        ridingEntity = null;

        motionX = motionY = motionZ = 0;
        setHealth(20);
        isDead = false;

        int meta = tablet.getBlockMetadata() & 7;
        int rotation = meta == 2 ? 180 : meta == 3 ? 0 : meta == 4 ? 90 : -90;
        rotationYaw = rotationYawHead = rotation;
        rotationPitch = -15;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (i != inventory.currentItem) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null) {
                    entityDropItem(stack, 1.0f);
                    inventory.setInventorySlotContents(i, null);
                }
            }
        }
    }

    @Override
    public void addChatMessage(IChatComponent var1) {}

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return new ChunkCoordinates(tablet.xCoord, tablet.yCoord, tablet.zCoord);
    }
}
