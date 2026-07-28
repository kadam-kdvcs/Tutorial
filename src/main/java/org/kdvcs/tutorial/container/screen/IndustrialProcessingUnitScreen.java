package org.kdvcs.tutorial.container.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.kdvcs.tutorial.Tutorial;
import org.kdvcs.tutorial.container.menu.IndustrialProcessingUnitMenu;

public class IndustrialProcessingUnitScreen extends AbstractContainerScreen<IndustrialProcessingUnitMenu> {

    /**
     * GUI 背景贴图的位置。
     * ResourceLocation 的格式为：modid:path
     * 这里对应的实际文件路径是：
     * assets/tutorial/textures/container/industrial_processing_unit.png
     */
    private static final ResourceLocation GUI =
            new ResourceLocation(Tutorial.MODID, "textures/container/industrial_processing_unit.png");

    /**
     * Screen 构造器。
     *
     * menu：当前界面绑定的 Menu（逻辑层）
     * playerInventory：玩家物品栏
     * title：界面标题
     *
     * Screen 只负责渲染界面，并不直接处理机器逻辑，
     * 真正的数据来源仍然是 Menu → BlockEntity。
     */
    public IndustrialProcessingUnitScreen(IndustrialProcessingUnitMenu menu,
                                          Inventory playerInventory,
                                          Component title) {
        super(menu, playerInventory, title);

        // GUI 的宽度与高度（像素）
        // 这些值通常需要与背景贴图尺寸保持一致
        this.imageWidth = 176;
        this.imageHeight = 174;
    }


    @Override
    protected void init() {
        super.init();
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 81;
    }

    /**
     * 渲染 GUI 背景。
     *
     * 该方法负责绘制界面的底层贴图。
     * 在这里我们只绘制一张固定的 GUI 背景图。
     */
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {

        // 设置渲染使用的 Shader
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        // 设置颜色（RGBA），1 表示不改变原贴图颜色
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        // 绑定要绘制的纹理
        RenderSystem.setShaderTexture(0, GUI);

        // 计算 GUI 左上角的位置，使界面居中显示
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 绘制贴图
        // 参数含义：
        // GUI：纹理
        // x,y：屏幕上的绘制位置
        // 0,0：纹理起始坐标
        // imageWidth,imageHeight：绘制区域大小
        guiGraphics.blit(GUI, x, y, 0, 0, imageWidth, imageHeight);

    }

    /**
     * 整个界面的渲染入口。
     *
     * 渲染顺序通常是：
     * 1. 绘制背景
     * 2. 绘制 GUI
     * 3. 绘制按钮、槽位等组件
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        // 绘制界面背景（灰色遮罩）
        renderBackground(graphics);

        // 调用父类渲染 GUI 元素
        super.render(graphics, mouseX, mouseY, partialTick);

        // 渲染鼠标悬停在物品上的提示信息
        renderTooltip(graphics, mouseX, mouseY);
    }
}
