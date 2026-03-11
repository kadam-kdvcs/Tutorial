package org.kdvcs.tutorial.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.kdvcs.tutorial.Tutorial;
import org.kdvcs.tutorial.container.screen.IndustrialProcessingUnitScreen;
import org.kdvcs.tutorial.init.ModMenuTypes;

@Mod.EventBusSubscriber(modid = Tutorial.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        registerScreens();
    }

    private static void registerScreens() {
        MenuScreens.register(ModMenuTypes.INDUSTRIAL_PROCESSING_UNIT_MENU.get(), IndustrialProcessingUnitScreen::new);
    }

}
