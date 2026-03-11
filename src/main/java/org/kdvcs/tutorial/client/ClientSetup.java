package org.kdvcs.tutorial.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.kdvcs.tutorial.Tutorial;
import org.kdvcs.tutorial.container.screen.IndustrialProcessingUnitScreen;
import org.kdvcs.tutorial.init.ModMenuTypes;

/**
 * 客户端初始化类。
 *
 * 该类只在客户端环境加载，用于注册所有与客户端相关的内容，
 * 例如 Screen、渲染器、模型层等。
 *
 * 这里我们主要完成一件事：注册 Menu 与 Screen 的对应关系。
 */
@Mod.EventBusSubscriber(
        modid = Tutorial.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT // 指定该类只在客户端加载
)
public class ClientSetup {

    /**
     * Forge 客户端初始化事件。
     *
     * 当客户端完成基本加载后，这个方法会被调用。
     * 我们在这里注册所有客户端专属内容。
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        registerScreens();
    }

    /**
     * 注册 GUI Screen。
     *
     * MenuScreens.register 建立 MenuType 与 Screen 的对应关系：
     *
     * MenuType → Screen
     *
     * 当服务端打开 INDUSTRIAL_PROCESSING_UNIT_MENU 时，
     * 客户端就会自动创建 IndustrialProcessingUnitScreen。
     */
    private static void registerScreens() {

        // 第一个参数：MenuType
        // 第二个参数：Screen 构造器
        MenuScreens.register(
                ModMenuTypes.INDUSTRIAL_PROCESSING_UNIT_MENU.get(),
                IndustrialProcessingUnitScreen::new
        );
    }

}
