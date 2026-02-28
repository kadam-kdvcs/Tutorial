package org.kdvcs.tutorial.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.kdvcs.tutorial.Tutorial;

public class ModBlocks {

    // 创建一个方块注册器。
    // 第一个参数指定注册类型（方块），
    // 第二个参数指定本模组的 MODID。
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Tutorial.MODID);

    // 注册一个示例方块：raw_material_block。
    // "raw_material_block" 为方块的注册名，
    // 同时用于资源文件（模型、贴图等）的命名基础。
    public static final RegistryObject<Block> RAW_MATERIAL_BLOCK =
            BLOCKS.register("raw_material_block",
                    // 创建方块实例。
                    // 这里通过 copy(Blocks.IRON_BLOCK) 复制铁块的基础属性，
                    // 使该方块拥有类似的硬度、抗爆性等行为，
                    // 作为当前阶段的简单示例方块使用。
                    () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    // 将本类中的注册器挂载到 Mod 事件总线。
    // 只有调用此方法后，方块才会在加载阶段被真正注册到游戏中。
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
