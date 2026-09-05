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

    // Controla o estado do hit pra dar UM pulo so
    private boolean wasHurt = false;   // hit "ativo" (com dano recente)
    private boolean jumpedThisHit = false; // ja deu o pulo desse hit

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (JumpResetMod.toggleKey != null && JumpResetMod.toggleKey.isPressed()) {
            enabled = !enabled;
        }

        EntityPlayer p = mc.thePlayer;
        if (p == null || mc.theWorld == null) return;

        int jumpKey = mc.gameSettings.keyBindJump.getKeyCode();

        if (!enabled) {
            releaseIfNotHeld(jumpKey);
            return;
        }

        boolean isHurtNow = p.hurtTime > 0;   // animacao de dano = hit acabou de acontecer

        // 1) Detecta que um hit NOVO comecou
        if (isHurtNow && !wasHurt) {
            jumpedThisHit = false;   // arma pra dar o pulo de reset
        }
        wasHurt = isHurtNow;

        // 2) Deu o dano, estamos no chao e ainda nao resetamos -> UM pulo
        if (isHurtNow && p.onGround && !jumpedThisHit) {
            jumpedThisHit = true;
            KeyBinding.setKeyBindState(jumpKey, true); // pulo unico
        }

        // 3) Limpeza: hit terminou -> libera pro proximo hit
        if (!isHurtNow) {
            releaseIfNotHeld(jumpKey);
        }
    }

    private void releaseIfNotHeld(int jumpKey) {
        // Nunca "briga" com a tecla fisica: se o dedo esta segurando, deixa pular continuo
        if (jumpKey >= 0 && !Keyboard.isKeyDown(jumpKey)) {
            KeyBinding.setKeyBindState(jumpKey, false);
        }
    }
}
