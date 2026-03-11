package org.kdvcs.tutorial.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.kdvcs.tutorial.container.menu.IndustrialProcessingUnitMenu;
import org.kdvcs.tutorial.init.ModBlockEntities;

public class IndustrialProcessingUnitBlockEntity extends BlockEntity implements MenuProvider {

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
     * 用于 Menu 与客户端同步数据的容器。
     *
     * ContainerData 的作用是把 BlockEntity 中的整数数据
     * 暴露给 Menu 系统，从而在客户端与服务端之间自动同步。
     *
     * 在本例中我们只同步一个字段：
     * index = 0  → progress
     *
     * 如果以后需要同步更多数据（例如最大进度、能量等），
     * 只需要增加新的 index 即可。
     */
    protected final ContainerData data = new ContainerData() {

        /**
         * Menu 读取数据时调用。
         * 根据 index 返回对应的数据值。
         */
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                default -> 0;
            };
        }

        /**
         * Menu 写入数据时调用。
         * 客户端同步数据时会通过这里写回。
         */
        @Override
        public void set(int index, int value) {
            if (index == 0) progress = value;
        }

        /**
         * 返回需要同步的数据数量。
         *
         * 因为这里只有 progress 一个变量，
         * 所以返回 1。
         */
        @Override
        public int getCount() {
            return 1;
        }
    };

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
     * 返回界面标题。
     *
     * 当玩家打开 GUI 时，
     * Screen 会使用这个 Component 作为窗口标题。
     */
    @Override
    public Component getDisplayName() {
        return Component.literal("be.title.industrial_processing_unit");
    }

    /**
     * 创建 Menu。
     *
     * 当玩家打开这个方块的界面时，
     * Forge 会调用这个方法来创建对应的 Menu。
     *
     * id：菜单同步 ID
     * inventory：玩家物品栏
     * player：打开界面的玩家
     *
     * 这里我们把当前 BlockEntity 与 ContainerData
     * 传入 Menu，使界面能够访问机器数据并进行同步。
     */
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new IndustrialProcessingUnitMenu(id, inventory, this, data);
    }
}
