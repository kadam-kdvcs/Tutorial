package org.kdvcs.tutorial.container.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.kdvcs.tutorial.blockentity.IndustrialProcessingUnitBlockEntity;
import org.kdvcs.tutorial.init.ModBlocks;
import org.kdvcs.tutorial.init.ModMenuTypes;

public class IndustrialProcessingUnitMenu extends AbstractContainerMenu {

    /**
     * 当前菜单绑定的方块实体。
     * Menu 本身并不存储机器逻辑，它只是作为界面逻辑层，
     * 因此需要持有 BlockEntity 的引用来访问真实数据。
     */
    public final IndustrialProcessingUnitBlockEntity blockEntity;

    /**
     * 当前菜单所在的世界。
     * 主要用于 stillValid 检查玩家是否仍然可以访问该方块。
     */
    private final Level level;

    /**
     * 用于同步简单整数数据的容器。
     * 这里主要用于同步 BlockEntity 中的 progress 等数值。
     */
    private final ContainerData data;

    // 输入槽索引
    private static final int INPUT_SLOT = 0;
    // 输出槽索引
    private static final int OUTPUT_SLOT = 1;

    /**
     * 客户端构造器。
     *
     * 当服务端要求客户端打开界面时，
     * Forge 会通过网络发送一个 FriendlyByteBuf，
     * 其中包含方块的位置等信息。
     *
     * 客户端通过读取这个位置，
     * 再从世界中获取对应的 BlockEntity。
     */
    public IndustrialProcessingUnitMenu(int id, Inventory inv, FriendlyByteBuf buf) {

        // 从网络数据中读取方块位置，并找到对应的 BlockEntity
        this(id, inv,
                inv.player.level().getBlockEntity(buf.readBlockPos()),
                new SimpleContainerData(1));
    }

    /**
     * 服务端构造器。
     *
     * 当玩家真正打开界面时，服务端会创建 Menu，
     * 并把 BlockEntity 与 ContainerData 传入。
     */
    public IndustrialProcessingUnitMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {

        // 指定该菜单对应的 MenuType
        super(ModMenuTypes.INDUSTRIAL_PROCESSING_UNIT_MENU.get(), id);

        // 保存方块实体引用
        this.blockEntity = (IndustrialProcessingUnitBlockEntity) entity;
        this.addMachineSlots(blockEntity.getItemHandler());

        // 保存世界引用
        this.level = inv.player.level();

        // 保存数据同步容器
        this.data = data;

        addPlayerInventory(inv, 8, 92);
        addPlayerHotbar(inv, 8, 150);

        // 注册数据同步槽
        // 这样 ContainerData 中的数据就会在服务端和客户端之间同步
        addDataSlots(data);
    }

    /**
     * 向当前 Menu 中添加机器自身的槽位。
     *
     * 这里使用 SlotItemHandler，将 BlockEntity 中的 ItemStackHandler
     * 直接绑定到 GUI 槽位上。
     *
     * 参数说明：
     * handler     -> 机器内部库存
     * INPUT_SLOT  -> 输入槽索引
     * OUTPUT_SLOT -> 输出槽索引
     * 77, 38      -> 输入槽在 GUI 中的位置
     * 142, 38     -> 输出槽在 GUI 中的位置
     */
    private void addMachineSlots(IItemHandler handler) {
        this.addSlot(new SlotItemHandler(handler, INPUT_SLOT, 77, 38));
        this.addSlot(new SlotItemHandler(handler, OUTPUT_SLOT, 142, 38));
    }

    /**
     * 添加玩家背包区域（3 行 * 9 列）。
     *
     * Inventory 中前 9 个槽位属于快捷栏，
     * 从索引 9 开始才是主背包，因此这里使用：
     * col + row * 9 + 9
     */
    private void addPlayerInventory(Inventory inv, int leftCol, int topRow) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(
                        inv,
                        col + row * 9 + 9,
                        leftCol + col * 18,
                        topRow + row * 18
                ));
            }
        }
    }

    /**
     * 添加玩家快捷栏（1 行 * 9 列）。
     *
     * 快捷栏在玩家 Inventory 中对应索引 0 ~ 8。
     */
    private void addPlayerHotbar(Inventory inv, int leftCol, int topRow) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(
                    inv,
                    col,
                    leftCol + col * 18,
                    topRow
            ));
        }
    }

    /**
     * Shift 点击快速移动物品的逻辑。
     *
     * 由于当前菜单还没有任何物品槽位，
     * 因此这里暂时返回 null。
     * 在后续实现物品槽时，这里会被完善。
     * 因为没有槽位，所以目前返回 null 是安全的
     *
     */
    private static final int TE_SLOT_COUNT = 2;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {

        // 先拿到当前被 Shift 点击的槽位
        Slot slot = this.slots.get(index);

        // 如果槽位不存在，或者槽位里没有物品，直接返回空
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        // 当前槽位中的物品
        ItemStack stack = slot.getItem();

        // 复制一份原物品，作为方法返回值
        ItemStack copy = stack.copy();

        // ========= 槽位区间划分 =========
        // 机器槽位：
        // 0 -> 输入槽
        // 1 -> 输出槽
        final int INPUT_SLOT = 0;
        final int OUTPUT_SLOT = 1;

        final int TE_START = 0;
        final int TE_END = TE_START + TE_SLOT_COUNT;   // [0, 2)

        // 玩家主背包：27 格
        final int PLAYER_INV_START = TE_END;
        final int PLAYER_INV_END = PLAYER_INV_START + 27;   // [2, 29)

        // 玩家快捷栏：9 格
        final int HOTBAR_START = PLAYER_INV_END;
        final int HOTBAR_END = HOTBAR_START + 9;            // [29, 38)

        // ========= 快速移动逻辑 =========

        // 情况 1：如果点击的是输出槽
        // 优先移动到快捷栏，快捷栏放不下再移动到主背包
        if (index == OUTPUT_SLOT) {
            if (!this.moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        // 情况 2：如果点击的是输入槽
        // 直接移动到玩家背包 + 快捷栏
        else if (index == INPUT_SLOT) {
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        }

        // 情况 3：如果点击的是玩家背包或快捷栏
        // 只尝试进入输入槽，不会进入输出槽
        else if (index >= TE_END && index < HOTBAR_END) {
            if (!this.moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        // 其他异常情况，直接返回空
        else {
            return ItemStack.EMPTY;
        }

        // ========= 更新原槽位状态 =========

        // 如果原物品已经被搬空，就把原槽位设为空
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        // 触发槽位取出逻辑
        slot.onTake(player, stack);

        return copy;
    }

    /**
     * 检查玩家是否仍然可以使用该界面。
     *
     * 如果玩家距离方块太远，或者方块已经被破坏，
     * 菜单就会自动关闭。
     */
    @Override
    public boolean stillValid(Player player) {
        return stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player,
                ModBlocks.INDUSTRIAL_PROCESSING_UNIT.get()
        );
    }

    /**
     * 提供对 BlockEntity 的访问。
     * Screen 或其他逻辑可以通过 Menu 获取对应的机器实例。
     */
    public IndustrialProcessingUnitBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
