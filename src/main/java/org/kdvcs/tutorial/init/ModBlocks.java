package org.kdvcs.tutorial.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.kdvcs.tutorial.Tutorial;
import org.kdvcs.tutorial.block.machine.IndustrialProcessingUnitBlock;

import java.util.function.Supplier;

public class ModBlocks {

    // 创建一个方块注册器。
    // 第一个参数指定注册类型（方块），
    // 第二个参数指定本模组的 MODID。
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Tutorial.MODID);

    public static final RegistryObject<Block> RAW_MATERIAL_BLOCK =
            registerBlock("raw_material_block",
                    () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    // 使用无参构造器
    public static final RegistryObject<Block> INDUSTRIAL_PROCESSING_UNIT =
            registerBlock("industrial_processing_unit", IndustrialProcessingUnitBlock::new);

    // 通用方块注册方法。
    // name 为注册名，block 为方块的创建方法（Supplier）。
    // 使用泛型 <T extends Block>，使该方法可以注册任意 Block 子类。
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){

        // 向方块注册器中声明该方块
        RegistryObject<T> toReturn = BLOCKS.register(name, block);

        // 同时为该方块自动注册对应的 BlockItem，
        // 使方块能够出现在背包中并被玩家放置。
        registerBlockItem(name, toReturn);

        // 返回注册结果，方便在其他地方引用该方块
        return toReturn;
    }

    // 为已注册的方块创建并注册对应的 BlockItem。
    // name 为注册名（应与方块注册名保持一致），
    // block 为方块的 RegistryObject，用于获取方块实例。
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){

        // 在物品注册器中注册一个 BlockItem。
        // block.get() 获取已经声明的方块实例，
        // 这样玩家在背包中持有该物品时，才能放置出对应的方块。
        return ModItems.ITEMS.register(
                name,
                () -> new BlockItem(block.get(), new Item.Properties())
        );
    }

    // 将本类中的注册器挂载到 Mod 事件总线。
    // 只有调用此方法后，方块才会在加载阶段被真正注册到游戏中。
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
