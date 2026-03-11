package org.kdvcs.tutorial;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.kdvcs.tutorial.init.*;
import org.slf4j.Logger;

@Mod(Tutorial.MODID)
public class Tutorial {
    public static final String MODID = "tutorial";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Tutorial() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //region ModEventBus
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        //end region

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

}
