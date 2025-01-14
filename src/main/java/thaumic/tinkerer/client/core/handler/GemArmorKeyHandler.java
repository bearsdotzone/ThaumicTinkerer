package thaumic.tinkerer.client.core.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import thaumic.tinkerer.client.core.handler.kami.KamiArmorClientHandler;
import thaumic.tinkerer.common.ThaumicTinkerer;
import thaumic.tinkerer.common.item.kami.armor.ItemIchorclothArmorAdv;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Created by Katrina on 28/02/14.
 */
@SideOnly(Side.CLIENT)
public class GemArmorKeyHandler {

    static KeyBinding SpecialAbility = new KeyBinding("ttmisc.toggleArmor", Keyboard.KEY_NONE, "ttmisc.keyCategory");

    public GemArmorKeyHandler() {
        FMLCommonHandler.instance().bus().register(this);
        ClientRegistry.registerKeyBinding(SpecialAbility);
    }

    @SubscribeEvent
    public void keyUp(InputEvent.KeyInputEvent event) {
        if (SpecialAbility.isPressed()) {
            if ((Minecraft.getMinecraft().thePlayer.getCurrentArmor(0) != null && Minecraft.getMinecraft().thePlayer
                    .getCurrentArmor(0).getItem() instanceof ItemIchorclothArmorAdv)
                    || (Minecraft.getMinecraft().thePlayer.getCurrentArmor(1) != null
                            && Minecraft.getMinecraft().thePlayer.getCurrentArmor(1)
                                    .getItem() instanceof ItemIchorclothArmorAdv)
                    || (Minecraft.getMinecraft().thePlayer.getCurrentArmor(2) != null
                            && Minecraft.getMinecraft().thePlayer.getCurrentArmor(2)
                                    .getItem() instanceof ItemIchorclothArmorAdv)
                    || (Minecraft.getMinecraft().thePlayer.getCurrentArmor(3) != null
                            && Minecraft.getMinecraft().thePlayer.getCurrentArmor(3)
                                    .getItem() instanceof ItemIchorclothArmorAdv)) {
                KamiArmorClientHandler
                        .SetStatus(!ThaumicTinkerer.proxy.armorStatus(ThaumicTinkerer.proxy.getClientPlayer()));
            }
        }
    }
}
