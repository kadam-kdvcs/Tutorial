package org.kdvcs.tutorial.block.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.kdvcs.tutorial.blockentity.IndustrialProcessingUnitBlockEntity;
import org.kdvcs.tutorial.init.ModBlockEntities;

// 工业处理单元方块。
// 继承 HorizontalDirectionalBlock，使方块天然支持水平四方向（N/S/E/W）朝向。
public class IndustrialProcessingUnitBlock extends HorizontalDirectionalBlock implements EntityBlock {

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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {

        /**
         * 当方块被放置到世界中时调用。
         *
         * 这个方法负责告诉游戏：
         * “这个方块在该位置应该创建哪一种 BlockEntity。”
         *
         * 每一个拥有 BlockEntity 的方块，都必须实现这个方法，
         * 否则即使注册了 BlockEntityType，世界中也不会真正生成实体。
         */
        return new IndustrialProcessingUnitBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level pLevel,
            BlockState pState,
            BlockEntityType<T> pBlockEntityType) {

        /**
         * ticker 用于把 BlockEntity 接入游戏的 tick 循环。
         *
         * Minecraft 每游戏刻都会询问方块：
         * “这个位置的 BlockEntity 需要执行更新逻辑吗？”
         *
         * 如果返回一个 ticker，游戏就会每 tick 调用它；
         * 返回 null，则表示该方块实体不需要更新。
         */

        return pBlockEntityType == ModBlockEntities.INDUSTRIAL_PROCESSING_UNIT_BE.get()
                // 类型匹配时，每 tick 调用我们的 BlockEntity.tick()
                ? (lvl, pos, state, be) ->
                ((IndustrialProcessingUnitBlockEntity) be).tick()
                : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level,
                                 BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof IndustrialProcessingUnitBlockEntity juicer) {
                NetworkHooks.openScreen((ServerPlayer) player, juicer, pos);
            } else {
                throw new IllegalStateException("Missing Container!");
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());

    }
}