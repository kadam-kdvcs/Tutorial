package org.kdvcs.tutorial.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.kdvcs.tutorial.Tutorial;

import java.util.function.Consumer;
import java.util.function.Function;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Tutorial.MODID);

    // 注册本教程中的第一个示例物品：raw_material。
    // "raw_material" 为物品的注册名（Registry Name），
    // 同时也会作为资源文件与模型文件的命名基础。
    public static final RegistryObject<Item> RAW_MATERIAL = registerItem("raw_material");

    // 注册一个最基础的物品。
    // 仅需要提供注册名，使用默认 Item.Properties。
    // 适用于没有特殊属性的简单物品。
    private static RegistryObject<Item> registerItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    // 注册一个可自定义物品实例的通用方法。
    // factory 接收 Item.Properties 并返回一个 Item，
    // 用于创建自定义 Item 子类或具有特殊构造逻辑的物品。
    private static RegistryObject<Item> registerItem(String name, Function<Item.Properties, Item> factory) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties()));
    }


    // 注册一个仅修改属性的简单物品。
    // propertiesModifier 用于修改 Item.Properties（如堆叠数、食物属性等），
    // 但仍使用基础 Item 类型，不需要创建新的 Item 子类。
    private static RegistryObject<Item> registerSimplePropItem(
            String name,
            Consumer<Item.Properties> propertiesModifier) {

        return ITEMS.register(name, () -> {
            Item.Properties props = new Item.Properties();

            // 对默认属性进行修改
            propertiesModifier.accept(props);

            // 使用修改后的属性创建物品
            return new Item(props);
        });
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
