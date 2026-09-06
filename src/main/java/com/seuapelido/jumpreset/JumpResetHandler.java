package com.seuapelido.jumpreset;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class JumpResetHandler {

    private final Minecraft mc = Minecraft.getMinecraft();

    private boolean wasHurt = false;     // hit "ativo" (dano recente)
    private boolean jumpedThisHit = false; // ja deu o pulo desse hit

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer p = mc.thePlayer;
        if (p == null || mc.theWorld == null) return;

        // Modulo desligado? Nao faz nada
        if (!JumpResetMod.isEnabled()) {
            wasHurt = false;
            jumpedThisHit = false;
            return;
        }

        boolean isHurtNow = p.hurtTime > 0;

        // 1) Novo hit detectado -> arma o pulo
        if (isHurtNow && !wasHurt) {
            jumpedThisHit = false;
        }
        wasHurt = isHurtNow;

        // 2) Pulo unico no chao, no inicio do hit
        //    (a reducao de KB agora e 100% responsabilidade do Mixin)
        if (isHurtNow && p.onGround && !jumpedThisHit) {
            jumpedThisHit = true;
            p.jump(); // pulo legitimo vanilla, sem mexer em motion manual
        }

        // 3) Hit terminou -> libera pro proximo
        if (!isHurtNow) {
            jumpedThisHit = false;
        }
    }
}
