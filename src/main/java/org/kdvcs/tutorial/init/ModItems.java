package org.kdvcs.tutorial.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.kdvcs.tutorial.Tutorial;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Tutorial.MODID);

    // 注册本教程中的第一个示例物品：raw_material。
    // "raw_material" 为物品的注册名（Registry Name），
    // 同时也会作为资源文件与模型文件的命名基础。
    public static final RegistryObject<Item> RAW_MATERIAL =
            ITEMS.register("raw_material",
                    // 创建一个基础物品实例。
                    // 当前不添加任何特殊属性，仅用于演示注册流程，
                    // 后续章节中将作为工业处理单元的加工原料使用。
                    () -> new Item(new Item.Properties()));

    // 为 raw_material_block 注册对应的物品形式（BlockItem）。
    // 该注册名必须与方块注册名保持一致，
    // 用于确保资源与掉落等逻辑正确关联。
    public static final RegistryObject<Item> RAW_MATERIAL_BLOCK =
            ITEMS.register("raw_material_block",
                    // 创建 BlockItem，将其与已注册的方块绑定。
                    // ModBlocks.RAW_MATERIAL_BLOCK.get() 获取对应的方块实例，
                    // 这样玩家在背包中持有该物品时，才能放置出对应的方块。
                    () -> new BlockItem(
                            ModBlocks.RAW_MATERIAL_BLOCK.get(),
                            new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
