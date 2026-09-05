package com.seuapelido.jumpreset;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class JumpResetHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean enabled = false;
    private boolean wasHurt = false;
    private boolean resetThisHit = false;

    // Quanto do KB horizontal vai sobrar.
    // 0.1 = bloqueia 90% (sobra 10%). 0.0 = bloqueia 100%. 0.5 = bloqueia 50%.
    private static final double KB_REMAINING = 0.1D;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (JumpResetMod.toggleKey != null && JumpResetMod.toggleKey.isPressed()) {
            enabled = !enabled;
        }

        EntityPlayer p = mc.thePlayer;
        if (p == null || mc.theWorld == null) return;

        if (!enabled) return;

        boolean isHurtNow = p.hurtTime > 0;

        if (isHurtNow && !wasHurt) {
            resetThisHit = false;
        }
        wasHurt = isHurtNow;

        if (!isHurtNow || resetThisHit) return;

        if (isBeingKnocked(p)) {
            resetThisHit = true;

            // 90% do KB horizontal cortado (sobra 10%)
            p.motionX *= KB_REMAINING;
            p.motionZ *= KB_REMAINING;

            // Reset/pulo pra quebrar o resto do impulso vertical
            if (p.onGround) {
                p.jump();
            } else {
                double vy = p.motionY;
                if (vy < 0.42D) {
                    p.motionY = Math.max(vy, 0.42D);
                }
            }
        }
    }

    private boolean isBeingKnocked(EntityPlayer p) {
        double hSpeed = Math.sqrt(p.motionX * p.motionX + p.motionZ * p.motionZ);
        boolean movingByKnockback = hSpeed > 0.01D;
        boolean falling = p.motionY < -0.1D && !p.onGround;
        return movingByKnockback || falling;
    }
}
