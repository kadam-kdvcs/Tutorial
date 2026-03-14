package org.kdvcs.tutorial.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdvcs.tutorial.block.machine.IndustrialProcessingUnitBlock;
import org.kdvcs.tutorial.container.menu.IndustrialProcessingUnitMenu;
import org.kdvcs.tutorial.init.ModBlockEntities;
import org.kdvcs.tutorial.init.ModItems;

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

    // 固定加工时长：100 tick
    private static final int maxProgress = 100;

    // 输入槽索引
    private static final int INPUT_SLOT = 0;
    // 输出槽索引
    private static final int OUTPUT_SLOT = 1;

    /**
     * 工业处理单元的内部物品栏。
     *
     * 这里使用 Forge 提供的 ItemStackHandler 作为库存实现。
     * 当前机器一共拥有两个槽位：
     * 0 -> 输入槽
     * 1 -> 输出槽
     */
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {

        /**
         * 当某个槽位内容发生变化时调用。
         *
         * 这里调用 setChanged()，告诉游戏：
         * 当前 BlockEntity 的数据已经发生修改，需要被标记为“已更改”，
         * 这样世界保存时才会把新数据写入存档。
         */
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        /**
         * 控制某个槽位是否允许放入指定物品。
         *
         * 当前实现中：
         * - 输入槽允许放入物品
         * - 输出槽不允许手动放入物品
         *
         * 这正符合大多数机器的常见逻辑：
         * 玩家把原料放进输入槽，产物只会出现在输出槽。
         */
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == INPUT_SLOT;
        }
    };

    /**
     * 将当前机器内部的所有物品掉落到世界中。
     *
     * ItemStackHandler 不是 Containers.dropContents 直接支持的容器类型，
     * 因此这里先创建一个临时的 SimpleContainer，
     * 再把 itemHandler 中的物品逐个拷贝进去，
     * 最后统一掉落。
     */
    public void drops() {
        // 创建一个临时容器，大小与机器槽位数量一致
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());

        // 将 itemHandler 中的每个槽位内容复制到临时容器中
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        // 将容器中的物品掉落到世界
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

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
                case 1 -> maxProgress;
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
            return 2;
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
     * 当前这里使用一个硬编码教学配方：
     * raw_material -> material_ingot
     *
     * 加工逻辑非常简单：
     * 1. 只有当输入槽中存在 raw_material，且输出槽能够接收 material_ingot 时，机器才会工作
     * 2. 每 tick 增加 1 点 progress
     * 3. 当 progress 达到 MAX_PROGRESS 时，消耗 1 个输入并产出 1 个输出
     * 4. progress 会被保存到 NBT，因此中途退出世界后不会丢失
     */
    public void tick() {

        if (hasRecipe()) {
            setWorkingState(true);

            progress++;
            setChanged();

            if (progress >= maxProgress) {
                craftItem();
            }
        } else {
            resetProgress();
            setWorkingState(false);
            setChanged();
        }
    }

    private void setWorkingState(boolean working) {

        BlockState currentState = level.getBlockState(worldPosition);

        // 保险起见，确认当前位置还是这个方块
        if (!currentState.hasProperty(IndustrialProcessingUnitBlock.WORKING)) {
            return;
        }

        // 只有状态真的变化时才更新
        if (currentState.getValue(IndustrialProcessingUnitBlock.WORKING) != working) {
            level.setBlock(worldPosition,
                    currentState.setValue(IndustrialProcessingUnitBlock.WORKING, working),
                    3);
        }
    }

    /**
     * 当前机器是否满足加工条件。
     *
     * 条件包括：
     * 1. 输入槽必须是 raw_material
     * 2. 输出槽必须为空，或已经是 material_ingot
     * 3. 输出槽必须还有空间容纳新的产物
     */
    private boolean hasRecipe() {
        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);

        // 输入槽必须放的是硬编码原料
        if (!inputStack.is(ModItems.RAW_MATERIAL.get())) {
            return false;
        }

        ItemStack result = new ItemStack(ModItems.MATERIAL_INGOT.get());

        // 输出槽为空，直接可以加工
        if (outputStack.isEmpty()) {
            return true;
        }

        // 输出槽里必须已经是同种产物
        if (!outputStack.is(result.getItem())) {
            return false;
        }

        // 输出槽数量不能超过堆叠上限
        return outputStack.getCount() < outputStack.getMaxStackSize();
    }

    /**
     * 真正执行一次加工：
     * - 消耗 1 个 raw_material
     * - 产出 1 个 material_ingot
     * - 重置 progress
     */
    private void craftItem() {
        ItemStack inputStack = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);

        inputStack.shrink(1);

        if (outputStack.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(ModItems.MATERIAL_INGOT.get(), 1));
        } else {
            outputStack.grow(1);
        }

        resetProgress();

        // 本次加工结束后，重新判断机器是否还能继续工作
        setWorkingState(hasRecipe());

        setChanged();
    }

    /**
     * 重置加工进度。
     */
    private void resetProgress() {
        progress = 0;
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

        // 将内部物品栏序列化后写入 NBT
        // "inventory" 是这一组库存数据在存档中的键名
        pTag.put("inventory", itemHandler.serializeNBT());

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

        // 从 NBT 中读取库存数据并恢复到 itemHandler
        // 键名必须与 saveAdditional 中保持一致
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));

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
        return Component.translatable("be.title.industrial_processing_unit");
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

    /**
     * 返回当前机器内部的物品处理器。
     *
     * Menu 会通过这个方法获取库存，
     * 再基于它创建真正的 GUI 槽位。
     */
    public IItemHandler getItemHandler() {
        return itemHandler;
    }

}
