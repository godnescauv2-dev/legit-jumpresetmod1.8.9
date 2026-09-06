package com.seuapelido.jumpreset;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = JumpResetMod.MODID, name = "JumpReset", version = "1.1")
public class JumpResetMod {

    public static final String MODID = "jumpreset";

    private static boolean enabled = false;
    public static KeyBinding toggleKey;

    @Instance(MODID)
    public static JumpResetMod INSTANCE;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        toggleKey = new KeyBinding("Jump Reset", Keyboard.KEY_R, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(toggleKey);

        MinecraftForge.EVENT_BUS.register(new KeyTickHandler());
    }

    public static boolean isEnabled() {
        return enabled;
    }

    // Handler simples so pra tecla (o KB agora e tratado pelo Mixin)
    public static class KeyTickHandler {
        @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
        @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
        public void onTick(net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent e) {
            if (e.phase != net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END) return;
            if (toggleKey != null && toggleKey.isPressed()) {
                enabled = !enabled;
            }
        }
    }
}
