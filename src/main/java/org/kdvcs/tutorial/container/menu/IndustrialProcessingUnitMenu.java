package org.kdvcs.tutorial.container.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

        // 保存世界引用
        this.level = inv.player.level();

        // 保存数据同步容器
        this.data = data;

        // 注册数据同步槽
        // 这样 ContainerData 中的数据就会在服务端和客户端之间同步
        addDataSlots(data);
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
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
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
