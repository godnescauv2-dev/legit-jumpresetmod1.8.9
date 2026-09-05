package com.seuapelido.jumpreset;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JumpResetHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    // Estado do modulo
    private boolean enabled = false;
    private int jumpTicks = 0;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        // R apertado = liga/desliga
        if (JumpResetMod.toggleKey.isPressed()) {
            enabled = !enabled;
        }

        EntityPlayer p = mc.thePlayer;
        if (p == null || mc.theWorld == null) return;

        if (!enabled) {
            releaseJump();
            return;
        }

        // Se tomou dano (hurtResistantTime > 0) estando no chao,
        // dispara o pulo nos proximos ticks para negar o knockback.
        if (p.onGround && p.hurtResistantTime > 0) {
            jumpTicks = 2;
        }

        if (jumpTicks > 0) {
            KeyBinding.setKeyBindState(
                mc.gameSettings.keyBindJump.getKeyCode(), true);
            jumpTicks--;
        } else {
            releaseJump();
        }
    }

    private void releaseJump() {
        if (mc.thePlayer != null) {
            KeyBinding.setKeyBindState(
                mc.gameSettings.keyBindJump.getKeyCode(), false);
        }
    }
}
