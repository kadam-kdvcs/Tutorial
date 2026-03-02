package org.kdvcs.tutorial.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.kdvcs.tutorial.Tutorial;
import org.kdvcs.tutorial.blockentity.IndustrialProcessingUnitBlockEntity;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Tutorial.MODID);

    /**
     * 工业处理单元的 BlockEntityType 注册。
     *
     * 这里做了三件事：
     *  1. 指定构造函数（如何创建 BlockEntity）
     *  2. 指定可绑定的方块（哪个 Block 使用它）
     *  3. 构建并注册到游戏
     */
    public static final RegistryObject<BlockEntityType<IndustrialProcessingUnitBlockEntity>>
            INDUSTRIAL_PROCESSING_UNIT_BE =
            BLOCK_ENTITIES.register("industrial_processing_unit_be", () ->

                    // Builder.of(构造器引用, 绑定的方块...)
                    BlockEntityType.Builder.of(
                            IndustrialProcessingUnitBlockEntity::new, // 如何创建 BE
                            ModBlocks.INDUSTRIAL_PROCESSING_UNIT.get() // 哪些方块可以拥有它
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
