package org.kdvcs.tutorial.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.kdvcs.tutorial.init.ModBlockEntities;

public class IndustrialProcessingUnitBlockEntity extends BlockEntity {

    /**
     * 一个教学用的示例字段。
     *
     * progress 在本章并不代表真实机器逻辑，
     * 它只是一个“计数器”，用于验证：
     * BlockEntity 是否在 tick
     * 数据是否能被保存
     * 数据是否能在重进世界后恢复
     */
    private int progress = 0;

    /**
     * 工业处理单元的方块实体（BlockEntity）。
     *
     * BlockEntity 用于为方块提供“可存储的数据与运行逻辑”。
     * 与普通 Block 不同，它可以：
     *  - 保存数据（NBT）
     *  - 在每 tick 执行逻辑
     *  - 在世界重新加载后恢复状态
     *
     * @param pPos         方块在世界中的位置
     * @param pBlockState  当前方块状态（BlockState）
     */
    public IndustrialProcessingUnitBlockEntity(BlockPos pPos, BlockState pBlockState) {

        // 绑定 BlockEntityType + 世界坐标 + 当前状态
        // 这一步决定：
        //   1. 它属于哪种实体类型
        //   2. 它附着在哪个位置
        //   3. 它对应的方块状态是什么
        super(ModBlockEntities.INDUSTRIAL_PROCESSING_UNIT_BE.get(), pPos, pBlockState);
    }

    /**
     * 每游戏刻执行一次（前提是 Block 中注册了 ticker）。
     *
     * 这里我们让 progress 每 tick 自增，
     * 用于证明 BlockEntity 正在参与游戏循环。
     *
     * setChanged() 表示数据已被修改，
     * 告诉游戏该实体需要被保存。
     */
    public void tick() {
        progress++;
        setChanged();
    }

    /**
     * 对外提供当前进度值。
     *
     * 目前我们还没有使用到它。
     * 但在后续 GUI 章节中，界面会通过这种 getter 方法读取数据。
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 写入存档数据（NBT）。
     *
     * 当世界保存或区块卸载时调用。
     * 只有在这里写入的数据，才能在重进世界后恢复。
     */
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        // 将 progress 写入 NBT
        pTag.putInt("Progress", progress);
    }

    /**
     * 从存档读取数据（NBT）。
     *
     * 当区块加载或方块实体被重建时调用。
     * 必须与 saveAdditional 使用相同的键名。
     */
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        // 从 NBT 中读取 progress
        progress = pTag.getInt("Progress");
    }

    /**
     * 教学用调试方法。
     *
     * 在没有 GUI 的情况下，
     * 通过聊天信息输出当前进度，
     * 用于验证 tick 与 NBT 是否正常工作。
     */
    public Component getDebugMessages() {
        return Component.literal("Progress: " + progress);
    }
}