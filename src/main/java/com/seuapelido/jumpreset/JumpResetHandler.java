package com.seuapelido.jumpreset;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JumpResetHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean enabled = false;
    private boolean wasHurt = false;
    private boolean resetThisHit = false;

    // 0.1 = bloqueia 90% do KB horizontal
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

        if (p.onGround) {
            // Chão: pulo legítimo + corte do KB horizontal
            resetThisHit = true;
            double mx = p.motionX;
            double mz = p.motionZ;
            p.jump();
            // Corta o KB, mas SEM zerar o motion do proprio pulo
            p.motionX = mx * KB_REMAINING + p.motionX * 0.0D;
            p.motionZ = mz * KB_REMAINING;
            // p.jump() so seta motionY, entao motionX/Z aqui sao os pre-jump
        } else {
            // Ar: NUNCA seta motionY pra cima (setback garantido).
            // Apenas corta o horizontal, mantendo motionY intacto.
            resetThisHit = true;
            p.motionX *= KB_REMAINING;
            p.motionZ *= KB_REMAINING;
        }
    }
}
