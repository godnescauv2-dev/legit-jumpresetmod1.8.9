package com.seuapelido.jumpreset.mixin;

import com.seuapelido.jumpreset.JumpResetMod;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public abstract class NetworkManagerMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V",
            at = @At("HEAD"), cancellable = true)
    private void onPacketRead(ChannelHandlerContext context, net.minecraft.network.Packet packet, CallbackInfo ci) {
        if (!(packet instanceof S12PacketEntityVelocity)) return;

        S12PacketEntityVelocity vel = (S12PacketEntityVelocity) packet;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // So mexe se a velocity for pra SEU player
        if (vel.getEntityID() != mc.thePlayer.getEntityId()) return;

        // So mexe se o modulo ta ligado
        if (!JumpResetMod.isEnabled()) return;

        // Cancela o packet original...
        ci.cancel();

        // ...e aplica a velocity reduzida (10% = 90% de reducao) direto no player
        double factor = 0.1D;

        // Packet vem em unidades de 1/8000 de bloco por tick
        double vx = vel.getMotionX() / 8000.0D * factor;
        double vy = vel.getMotionY() / 8000.0D * factor;
        double vz = vel.getMotionZ() / 8000.0D * factor;

        Entity player = mc.thePlayer;
        player.motionX = vx;
        player.motionY = vy;
        player.motionZ = vz;
    }
}
