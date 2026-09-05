package com.seuapelido.jumpreset;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = JumpResetMod.MODID, name = "JumpReset", version = "1.0")
public class JumpResetMod {

    public static final String MODID = "jumpreset";

    // Tecla R = liga/desliga o modulo de jump reset
    public static KeyBinding toggleKey;

    @Instance(MODID)
    public static JumpResetMod INSTANCE;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Registra a tecla R
        toggleKey = new KeyBinding("Jump Reset", Keyboard.KEY_R, "key.categories.gameplay");
        ClientRegistry.registerKeyBinding(toggleKey);

        MinecraftForge.EVENT_BUS.register(new JumpResetHandler());
    }
}
