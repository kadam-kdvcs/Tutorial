package org.kdvcs.tutorial.block.machine;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

// 工业处理单元方块。
// 继承 HorizontalDirectionalBlock，使方块天然支持水平四方向（N/S/E/W）朝向。
public class IndustrialProcessingUnitBlock extends HorizontalDirectionalBlock {

    // 方块的朝向属性（水平四方向）。
    // 直接复用 Minecraft 已有的 FACING 定义，而不是重新创建一个属性。
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public IndustrialProcessingUnitBlock() {
        // 定义方块基础属性（硬度、声音等后续可在这里扩展）
        super(Properties.of());

        // 注册默认方块状态。
        // 当方块尚未被放置或没有额外信息时，默认朝向 NORTH。
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
        );
    }

    // 玩家放置方块时调用，用于确定最终的方块状态。
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {

        // 获取玩家当前面朝方向，并取反方向。
        // 这样机器的“正面”会朝向玩家，
        // 实现“放下去就看到正面”的直觉效果。
        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    // 向方块状态系统注册我们新增的属性。
    // 如果不在这里添加 FACING，游戏就无法获取到方块状态，进而在启动阶段崩溃。
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
}