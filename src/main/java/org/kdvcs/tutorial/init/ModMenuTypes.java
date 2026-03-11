package org.kdvcs.tutorial.init;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.kdvcs.tutorial.Tutorial;
import org.kdvcs.tutorial.container.menu.IndustrialProcessingUnitMenu;

/**
 * 菜单类型（MenuType）注册类。
 *
 * MenuType 可以理解为“某一种界面的逻辑类型”。
 * 当服务端打开一个 GUI 时，实际上是先创建对应的 Menu，
 * 客户端再根据 MenuType 找到对应的 Screen 来进行渲染。
 *
 * 因此，在 Forge 中实现 GUI 时，MenuType 必须先注册。
 */
public class ModMenuTypes {

    /**
     * 创建菜单类型注册器。
     *
     * DeferredRegister 是 Forge 推荐的注册方式，
     * 用于在正确的生命周期阶段向注册表中添加内容。
     *
     * 这里使用的是 ForgeRegistries.MENU_TYPES，
     * 表示我们正在注册 MenuType。
     */
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Tutorial.MODID);

    //region 注册区

    /**
     * 工业处理单元的 MenuType。
     *
     * 这里注册的是界面逻辑类型，而不是界面本身。
     * 当服务器调用 NetworkHooks.openScreen 打开 GUI 时，
     * 实际上就是创建这个 MenuType 对应的 Menu。
     */
    public static final RegistryObject<MenuType<IndustrialProcessingUnitMenu>>
            INDUSTRIAL_PROCESSING_UNIT_MENU =
            registerMenuType(
                    "industrial_processing_unit_menu",
                    IndustrialProcessingUnitMenu::new
            );

    //endregion

    /**
     * 通用的 MenuType 注册方法。
     *
     * name：注册名称
     * factory：Menu 的构造器引用
     *
     * IForgeMenuType.create(factory) 会创建一个支持网络同步的 MenuType，
     * Forge 会利用这个 factory 在客户端和服务端分别构造 Menu。
     */
    private static <T extends AbstractContainerMenu>
    RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {

        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    /**
     * 将注册器挂载到 Forge 事件总线。
     *
     * 在模组初始化时调用此方法，
     * 这样 MENUS 中声明的所有 MenuType 才会真正被注册到游戏中。
     */
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
