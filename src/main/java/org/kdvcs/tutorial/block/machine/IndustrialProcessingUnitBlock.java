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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

    public static final BooleanProperty WORKING = BooleanProperty.create("working");

    public IndustrialProcessingUnitBlock() {
        // 定义方块基础属性（硬度、声音等后续可在这里扩展）
        super(Properties.of().lightLevel(state -> state.getValue(WORKING) ? 15 : 0));

        // 注册默认方块状态。
        // 当方块尚未被放置或没有额外信息时，默认朝向 NORTH。
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(WORKING, false)
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
                .setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(WORKING, false);
    }

    // 向方块状态系统注册我们新增的属性。
    // 如果不在这里添加 FACING，游戏就无法获取到方块状态，进而在启动阶段崩溃。
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, WORKING);
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
         * 这里必须注意：
         * 机器的加工逻辑只应该在服务端运行，
         * 客户端只负责接收同步后的结果并进行显示。
         *
         */
        if (pLevel.isClientSide()) {
            return null;
        }

        return pBlockEntityType == ModBlockEntities.INDUSTRIAL_PROCESSING_UNIT_BE.get()
                ? (lvl, pos, state, be) ->
                ((IndustrialProcessingUnitBlockEntity) be).tick()
                : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level,
                                 BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {

        // 方块被玩家右键时调用。
        // 这里我们用它作为打开 GUI 的入口。

        // GUI 必须由服务端发起，因此只在服务端执行打开逻辑。
        // 客户端只负责渲染界面，不负责创建 Menu。
        if (!level.isClientSide()) {

            // 获取当前位置绑定的 BlockEntity
            BlockEntity entity = level.getBlockEntity(pos);

            // 确认该实体确实是我们的工业处理单元
            if (entity instanceof IndustrialProcessingUnitBlockEntity juicer) {

                // 打开界面。
                // NetworkHooks.openScreen 会：
                // 1. 在服务端创建 Menu
                // 2. 通过网络把打开界面的信息发送给客户端
                // 3. 客户端根据 MenuType 创建对应的 Screen
                //
                // 这里传入 pos，是为了让客户端能够找到对应位置的 BlockEntity。
                NetworkHooks.openScreen((ServerPlayer) player, juicer, pos);

            } else {
                // 如果当前位置没有正确的 BlockEntity，
                // 说明出现了逻辑错误，直接抛出异常。
                throw new IllegalStateException("Missing Container!");
            }
        }

        // 返回交互结果。
        // sidedSuccess 会在客户端和服务端分别返回正确的结果，
        // 保证交互逻辑在两端保持一致。
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {

        // 只有当旧方块与新方块不是同一个方块时，
        // 才说明当前方块真的被替换/破坏了
        if (pState.getBlock() != pNewState.getBlock()) {

            // 取出当前位置的 BlockEntity
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

            // 如果它确实是工业处理单元的 BE，就执行掉落逻辑
            if (blockEntity instanceof IndustrialProcessingUnitBlockEntity industrialProcessingUnit) {
                industrialProcessingUnit.drops();
            }
        }

        // 保留父类逻辑
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
