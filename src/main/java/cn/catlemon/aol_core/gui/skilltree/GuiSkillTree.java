package cn.catlemon.aol_core.gui.skilltree;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.Coordinate;
import cn.catlemon.aol_core.api.PackedResourceLocation;
import cn.catlemon.aol_core.api.SkillBase;
import cn.catlemon.aol_core.api.SkillTreePage;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.ISkillPoint;
import cn.catlemon.aol_core.capability.ISkillTree;
import cn.catlemon.aol_core.gui.GuiHandler;
import cn.catlemon.aol_core.network.NetworkHandler;
import cn.catlemon.aol_core.network.PacketEvent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

//TODO 全部换成自定义大小的材质
public class GuiSkillTree extends GuiScreen {
	private EntityPlayer player;
	private SkillTreePage page;
	private List<String> pages;
	private int learnLevel;
	
	private static final int GUI_WIDTH = 256;
	private static final int GUI_HEIGHT = 160;
	private int guiLeft, guiTop;
	
	private static final int SKILL_SCREEN_WIDTH = 132;
	private static final int SKILL_SCREEN_HEIGHT = 140;
	private static final int SKILL_SCREEN_OFFSET_X = 82;
	private static final int SKILL_SCREEN_OFFSET_Y = 10;
	private int skillScreenLeft, skillScreenTop;
	
	private static final int DESC_SCREEN_WIDTH = 60;
	private static final int DESC_SCREEN_HEIGHT = 108;
	private static final int DESC_SCREEN_OFFSET_X = 10;
	private static final int DESC_SCREEN_OFFSET_Y = 10;
	private int descScreenLeft, descScreenTop;
	
	private int x, y;
	private int prevX, prevY;
	private int minX, minY;
	private int maxX, maxY;
	private boolean isClicking = false;
	
	private int currentTab, currentPage, totalPages;
	
	private PackedResourceLocation BACKGROUND = new AoLCore.AoLResourceLocation("textures/gui/skilltree_background.png",
			new Coordinate<Integer>(0), 192, 192);
	private static final PackedResourceLocation WINDOW = new AoLCore.AoLResourceLocation("textures/gui/skilltree.png",
			new Coordinate<Integer>(0), 256, 160);
	private static final PackedResourceLocation ENABLED_LEARN_BUTTON = new AoLCore.AoLResourceLocation(
			"textures/gui/skilltree.png", new Coordinate<Integer>(0, 160), 48, 24);
	private static final PackedResourceLocation DISABLED_LEARN_BUTTON = new AoLCore.AoLResourceLocation(
			"textures/gui/skilltree.png", new Coordinate<Integer>(0, 192), 48, 24);
	private static final PackedResourceLocation ACTIVED_TAB = new AoLCore.AoLResourceLocation(
			"textures/gui/skilltree.png", new Coordinate<Integer>(222, 160), 34, 24);
	private static final PackedResourceLocation DISACTIVED_TAB = new AoLCore.AoLResourceLocation(
			"textures/gui/skilltree.png", new Coordinate<Integer>(222, 192), 34, 24);
	private static final PackedResourceLocation PREV_BUTTON = new AoLCore.AoLResourceLocation(
			"textures/gui/skilltree.png", new Coordinate<Integer>(240, 224), 16, 16);
	private static final PackedResourceLocation NEXT_BUTTON = new AoLCore.AoLResourceLocation(
			"textures/gui/skilltree.png", new Coordinate<Integer>(224, 224), 16, 16);
	private static final PackedResourceLocation DEFAULT_SKILL_BACKGROUND = new AoLCore.AoLResourceLocation(
			"textures/skill/background/default.png", new Coordinate<Integer>(0), 24, 24);
	
	private boolean buttonPagePrevEnabled, buttonPageNextEnabled, buttonLearnEnabled;
	
	private SkillBase currentSelectedSkill = null;
	private Set<String> skills = null;
	
	private enum SkillState {
		UNSURE, UNLEARNABLE, LEARNABLE, SELECTED_LEARNABLE, LEARNED, SELECTED_LEARNED
	};
	
	private Map<String, SkillState> skillState = new HashMap<String, SkillState>();
	
	public GuiSkillTree(EntityPlayer player, int pageGuiId, int learnLevel) {
		this.player = player;
		this.pages = player.getCapability(CapabilityHandler.capSkillTree, null).getPageList();
		this.page = player.getCapability(CapabilityHandler.capSkillTree, null).getPage(pages.get(pageGuiId));
		totalPages = (pages.size() + 4) / 5;
		int pageIndex = pages.indexOf(page.getSkillTreePageId());
		currentTab = pageIndex % 5;
		currentPage = pageIndex / 5;
		this.learnLevel = learnLevel;
		if (page.getGuiBackground() != null)
			BACKGROUND = page.getGuiBackground();
		buttonLearnEnabled = false;
		
		skills = page.getSkillSet();
		int minX = -10000, minY = -10000;
		int maxX = 10000, maxY = 10000;
		for (String skillId : skills) {
			skillState.put(skillId, SkillState.UNSURE);
			Coordinate<Integer> skillCor = page.getSkill(skillId).getSkillLocation();
			minX = skillCor.x * 48 > minX ? skillCor.x * 48 : minX;
			minY = -skillCor.y * 48 > minY ? -skillCor.y * 48 : minY;
			maxX = skillCor.x * 48 < maxX ? skillCor.x * 48 : maxX;
			maxY = -skillCor.y * 48 < maxY ? -skillCor.y * 48 : maxY;
		}
		this.minX = -minX - 500;
		this.minY = -minY - 500;
		this.maxX = -maxX + 500;
		this.maxY = -maxY + 500;
		x = 0;
		y = 0;
		
		updateSkillState();
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void initGui() {
		guiLeft = (width - GUI_WIDTH) / 2;
		guiTop = (height - GUI_HEIGHT) / 2;
		skillScreenLeft = guiLeft + SKILL_SCREEN_OFFSET_X;
		skillScreenTop = guiTop + SKILL_SCREEN_OFFSET_Y;
		descScreenLeft = guiLeft + DESC_SCREEN_OFFSET_X;
		descScreenTop = guiTop + DESC_SCREEN_OFFSET_Y;
	}
	
	@Override
	public void updateScreen() {
		guiLeft = (width - GUI_WIDTH) / 2;
		guiTop = (height - GUI_HEIGHT) / 2;
		skillScreenLeft = guiLeft + SKILL_SCREEN_OFFSET_X;
		skillScreenTop = guiTop + SKILL_SCREEN_OFFSET_Y;
		descScreenLeft = guiLeft + DESC_SCREEN_OFFSET_X;
		descScreenTop = guiTop + DESC_SCREEN_OFFSET_Y;
		
		updateSkillState();
		
		if (currentPage > 0)
			buttonPagePrevEnabled = true;
		else
			buttonPagePrevEnabled = false;
		if (currentPage < totalPages - 1)
			buttonPageNextEnabled = true;
		else
			buttonPageNextEnabled = false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		updateXY(mouseX, mouseY);
		
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		drawBackground();
		drawSkills();
		drawWindow();
		drawLearnButton();
		drawTab();
		drawText();
	}
	
	private void draw(@Nonnull PackedResourceLocation prLocation, @Nonnull Coordinate<Integer> drawLocation) {
		draw(prLocation, drawLocation, new Coordinate<Integer>(0), prLocation.width, prLocation.height);
	}
	
	private void draw(@Nonnull PackedResourceLocation prLocation, @Nonnull Coordinate<Integer> drawLocation, int width,
			int height) {
		draw(prLocation, drawLocation, new Coordinate<Integer>(0), width, height);
	}
	
	private void draw(@Nonnull PackedResourceLocation prLocation, @Nonnull Coordinate<Integer> drawLocation,
			@Nonnull Coordinate<Integer> offset, int width, int height) {
		setDefaultRenderSettings();
		mc.getTextureManager().bindTexture(prLocation.resourceLocation);
		drawTexturedModalRect(drawLocation.x, drawLocation.y, prLocation.offset.x + offset.x,
				prLocation.offset.y + offset.y, width, height);
	}
	
	private void drawBackground() {
		draw(BACKGROUND, new Coordinate<Integer>(skillScreenLeft, skillScreenTop),
				new Coordinate<Integer>(16 - Math.abs((x + 10000) % 16), 16 - Math.abs((y + 10000) % 16)),
				SKILL_SCREEN_WIDTH, SKILL_SCREEN_HEIGHT);
	}
	
	private void drawSkills() {
		zLevel += 5;
		for (String skillId : skills) {
			SkillBase skill = page.getSkill(skillId);
			Coordinate<Double> cor = new Coordinate<Double>(
					(double) (skillScreenLeft + SKILL_SCREEN_WIDTH / 2 + x + skill.getSkillLocation().x * 48),
					(double) (skillScreenTop + SKILL_SCREEN_HEIGHT / 2 + y - skill.getSkillLocation().y * 48 - 12));
			for (String childSkillId : skill.getSkillDependents()) {
				if (!page.hasSkill(childSkillId))
					continue;
				Coordinate<Double> childCor = new Coordinate<Double>(
						(double) (skillScreenLeft + SKILL_SCREEN_WIDTH / 2 + x
								+ page.getSkill(childSkillId).getSkillLocation().x * 48),
						(double) (skillScreenTop + SKILL_SCREEN_HEIGHT / 2 + y
								- page.getSkill(childSkillId).getSkillLocation().y * 48 + 12));
				if (cor.x > childCor.x) {
					Coordinate<Double> t = cor;
					cor = childCor;
					childCor = t;
				}
				Coordinate<Double> tmp;
				tmp = getLineCross(cor, childCor, new Coordinate<Integer>(skillScreenLeft, skillScreenTop),
						new Coordinate<Integer>(skillScreenLeft, skillScreenTop + SKILL_SCREEN_HEIGHT));
				if (tmp != null)
					cor = tmp;
				tmp = getLineCross(cor, childCor,
						new Coordinate<Integer>(skillScreenLeft + SKILL_SCREEN_WIDTH, skillScreenTop),
						new Coordinate<Integer>(skillScreenLeft + SKILL_SCREEN_WIDTH,
								skillScreenTop + SKILL_SCREEN_HEIGHT));
				if (tmp != null)
					childCor = tmp;
				if (cor.y < childCor.y) {
					Coordinate<Double> t = cor;
					cor = childCor;
					childCor = t;
				}
				tmp = getLineCross(cor, childCor,
						new Coordinate<Integer>(skillScreenLeft, skillScreenTop + SKILL_SCREEN_HEIGHT),
						new Coordinate<Integer>(skillScreenLeft + SKILL_SCREEN_WIDTH,
								skillScreenTop + SKILL_SCREEN_HEIGHT));
				if (tmp != null)
					cor = tmp;
				tmp = getLineCross(cor, childCor, new Coordinate<Integer>(skillScreenLeft, skillScreenTop),
						new Coordinate<Integer>(skillScreenLeft + SKILL_SCREEN_WIDTH, skillScreenTop));
				if (tmp != null)
					childCor = tmp;
				if (cor.x >= skillScreenLeft - 1 && cor.x <= skillScreenLeft + SKILL_SCREEN_WIDTH + 1
						&& cor.y >= skillScreenTop - 1 && cor.y <= skillScreenTop + SKILL_SCREEN_HEIGHT + 1
						&& childCor.x >= skillScreenLeft - 1 && childCor.x <= skillScreenLeft + SKILL_SCREEN_WIDTH + 1
						&& childCor.y >= skillScreenTop - 1 && childCor.y <= skillScreenTop + SKILL_SCREEN_HEIGHT + 1)
					drawLine(cor.x, cor.y, childCor.x, childCor.y, skill.isLearned());
			}
		}
		for (String skillId : skills) {
			SkillBase skill = page.getSkill(skillId);
			Coordinate<Integer> cor = skill.getSkillLocation();
			if (skillState.get(skillId) == SkillState.UNSURE)
				continue;
			PackedResourceLocation skillBackground = (skill.getSkillBackground() == null) ? DEFAULT_SKILL_BACKGROUND
					: skill.getSkillBackground();
			int baseX = 0, baseY = 0;
			int offsetX = 0, offsetY = 0;
			int sizeX = skillBackground.width, sizeY = skillBackground.height;
			int startX = skillScreenLeft + SKILL_SCREEN_WIDTH / 2 + x + cor.x * 48 - sizeX / 2;
			int startY = skillScreenTop + SKILL_SCREEN_HEIGHT / 2 + y - cor.y * 48 - sizeY / 2;
			int iconStartX = startX + (sizeX - 16) / 2;
			int iconStartY = startY + (sizeY - 16) / 2;
			switch (skillState.get(skillId)) {
				case LEARNABLE:
					baseX = sizeX;
					baseY = 0;
					break;
				case LEARNED:
					baseX = 2 * sizeX;
					baseY = 0;
					break;
				case SELECTED_LEARNABLE:
					baseX = sizeX;
					baseY = sizeY;
					break;
				case SELECTED_LEARNED:
					baseX = 2 * sizeX;
					baseY = sizeY;
					break;
				case UNLEARNABLE:
					baseX = 0;
					baseY = 0;
					break;
				default:
					break;
			}
			int drawSizeX = sizeX, drawSizeY = sizeY;
			if (startX < skillScreenLeft) {
				offsetX += skillScreenLeft - startX;
				drawSizeX -= skillScreenLeft - startX;
				startX = skillScreenLeft;
			} else if (startX + sizeX > skillScreenLeft + SKILL_SCREEN_WIDTH) {
				drawSizeX -= startX + sizeX - skillScreenLeft - SKILL_SCREEN_WIDTH;
			}
			if (iconStartX < skillScreenLeft)
				iconStartX = skillScreenLeft;
			if (startY < skillScreenTop) {
				offsetY += skillScreenTop - startY;
				drawSizeY -= skillScreenTop - startY;
				startY = skillScreenTop;
			} else if (startY + sizeY > skillScreenTop + SKILL_SCREEN_HEIGHT) {
				drawSizeY -= startY + sizeY - skillScreenTop - SKILL_SCREEN_HEIGHT;
			}
			if (iconStartY < skillScreenTop)
				iconStartY = skillScreenTop;
			if (drawSizeX > 0 && drawSizeY > 0)
				draw(skillBackground, new Coordinate<Integer>(startX, startY),
						new Coordinate<Integer>(baseX + offsetX, baseY + offsetY), drawSizeX, drawSizeY);
			
			startX = (startX < skillScreenLeft) ? ((sizeX > 20) ? startX + sizeX - 20 : startX) : startX + 4;
			startY = (startY < skillScreenTop) ? ((sizeY > 20) ? startY + sizeY - 20 : startY) : startY + 4;
			offsetX = (offsetX > (sizeX - 16) / 2) ? offsetX - (sizeX - 16) / 2 : 0;
			offsetY = (offsetY > (sizeY - 16) / 2) ? offsetY - (sizeY - 16) / 2 : 0;
			drawSizeX = (drawSizeX >= (16 + sizeX) / 2) ? 16 : drawSizeX - (sizeX - 16) / 2;
			drawSizeY = (drawSizeY >= (16 + sizeY) / 2) ? 16 : drawSizeY - (sizeY - 16) / 2;
			// 绘制16x16技能图标
			if (skill.getSkillIcon() != null) {
				draw(skill.getSkillIcon(), new Coordinate<Integer>(iconStartX, iconStartY),
						new Coordinate<Integer>(offsetX, offsetY), drawSizeX, drawSizeY);
			}
		}
		zLevel -= 5;
	}
	
	private Coordinate<Double> getLineCross(Coordinate<Double> line1_start, Coordinate<Double> line1_end,
			Coordinate<Integer> line2_start, Coordinate<Integer> line2_end) {
		if (line2_end.x.equals(line2_start.x)) {
			if (line1_end.x.equals(line1_start.x))
				return null;
			if ((line1_end.x - line2_end.x) * (line1_start.x - line2_start.x) > 0)
				return null;
			return new Coordinate<Double>((double) line2_end.x, ((double) line2_end.x - line1_start.x)
					* (line1_end.y - line1_start.y) / (line1_end.x - line1_start.x) + line1_start.y);
		} else if (line2_end.y.equals(line2_start.y)) {
			if (line1_end.y.equals(line1_start.y))
				return null;
			if ((line1_end.y - line2_end.y) * (line1_start.y - line2_start.y) > 0)
				return null;
			return new Coordinate<Double>(((double) line2_end.y - line1_start.y) * (line1_end.x - line1_start.x)
					/ (line1_end.y - line1_start.y) + line1_start.x, (double) line2_end.y);
		}
		return null;
	}
	
	private void drawLine(double startX, double startY, double endX, double endY, boolean enabled) {
		int color = enabled ? 0xFFFFFFFF : 0xFFAAAAAA;
		float alpha = (color >> 24 & 0xFF) / 255.0F;
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(red, green, blue, alpha);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(startX - 1, startY, 0.0D).endVertex();
		buffer.pos(startX + 1, startY, 0.0D).endVertex();
		buffer.pos(endX + 1, endY, 0.0D).endVertex();
		buffer.pos(endX - 1, endY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	private void drawWindow() {
		zLevel += 10;
		draw(WINDOW, new Coordinate<Integer>(guiLeft, guiTop));
		zLevel -= 10;
	}
	
	private void drawLearnButton() {
		zLevel += 20;
		draw(buttonLearnEnabled ? ENABLED_LEARN_BUTTON : DISABLED_LEARN_BUTTON,
				new Coordinate<Integer>(guiLeft + 16, guiTop + 128));
		String buttonText = I18n.format("gui." + AoLCore.MODID + ".skilltree.learnbutton");
		GlStateManager.translate(0, 0, 25);
		fontRenderer.drawString((buttonLearnEnabled ? "§f" : "§7") + buttonText,
				guiLeft + 16 + (ENABLED_LEARN_BUTTON.width - fontRenderer.getStringWidth(buttonText)) / 2,
				guiTop + 128 + (ENABLED_LEARN_BUTTON.height - 8) / 2, 0xffffff, false);
		GlStateManager.translate(0, 0, -25);
		zLevel -= 20;
	}
	
	private void drawTab() {
		zLevel += 20;
		int tabNums = (currentPage == totalPages - 1) ? pages.size() % 5 : 5;
		boolean thisPageContainsActiveOne = (pages.indexOf(page.getSkillTreePageId()) / 5 == currentPage);
		for (int tab = 0; tab < tabNums; tab++) {
			draw((thisPageContainsActiveOne && tab == currentTab) ? ACTIVED_TAB : DISACTIVED_TAB,
					new Coordinate<Integer>(guiLeft + 222, guiTop + 24 * tab + 8));
			zLevel += 10;
			SkillTreePage currentDrawingPage = player.getCapability(CapabilityHandler.capSkillTree, null)
					.getPage(pages.get(5 * currentPage + tab));
			PackedResourceLocation pageIcon = currentDrawingPage.getPageIcon();
			if (pageIcon != null)
				draw(pageIcon, new Coordinate<Integer>(guiLeft + 232, guiTop + 24 * tab + 12), 16, 16);
			zLevel -= 10;
		}
		if (buttonPagePrevEnabled) {
			draw(PREV_BUTTON, new Coordinate<Integer>(guiLeft + 224, guiTop + 144));
		}
		if (buttonPageNextEnabled) {
			draw(NEXT_BUTTON, new Coordinate<Integer>(guiLeft + 240, guiTop + 144));
		}
		zLevel -= 20;
	}
	
	private void drawText() {
		if (currentSelectedSkill == null)
			return;
		GlStateManager.translate(0, 0, 25);
		String title = I18n.format("misc.skill." + currentSelectedSkill.getSkillId());
		String desc = I18n.format("misc.skill." + currentSelectedSkill.getSkillId() + ".desc");
		title = fontRenderer.trimStringToWidth(title, 2 * DESC_SCREEN_WIDTH - 10);
		fontRenderer.drawSplitString(title, descScreenLeft + 2, descScreenTop + 2, DESC_SCREEN_WIDTH - 4, 0xffffff);
		GlStateManager.translate(0, 0, -25);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton == 0) {
			if (buttonPagePrevEnabled && mouseX >= guiLeft + 224 && mouseX < guiLeft + 240 && mouseY >= guiTop + 144
					&& mouseY < guiTop + 160) {
				currentPage--;
			}
			if (buttonPageNextEnabled && mouseX >= guiLeft + 240 && mouseX < guiLeft + 256 && mouseY >= guiTop + 144
					&& mouseY < guiTop + 160) {
				currentPage++;
			}
			if (buttonLearnEnabled && mouseX >= guiLeft + 16 && mouseX < guiLeft + 64 && mouseY >= guiTop + 128
					&& mouseY < guiTop + 152) {
				if (currentSelectedSkill != null) {
					sync_learn(currentSelectedSkill.getSkillId());
					buttonLearnEnabled = false;
				}
			}
			int tabNums = (currentPage == totalPages - 1) ? pages.size() % 5 : 5;
			boolean thisPageContainsActiveOne = (pages.indexOf(page.getSkillTreePageId()) / 5 == currentPage);
			for (int tab = 0; tab < tabNums; tab++) {
				if (thisPageContainsActiveOne && tab == currentTab) {
					continue;
				} else {
					if (mouseX >= guiLeft + 224 && mouseX < guiLeft + 256 && mouseY >= guiTop + 24 * tab + 8
							&& mouseY < guiTop + 24 * tab + 32) {
						player.openGui(AoLCore.instance, GuiHandler.Gui_SKILLTREE, player.world, currentPage * 5 + tab,
								1, 0);
					}
				}
			}
			if (mouseX >= skillScreenLeft && mouseX < skillScreenLeft + SKILL_SCREEN_WIDTH && mouseY >= skillScreenTop
					&& mouseY < skillScreenTop + SKILL_SCREEN_HEIGHT) {
				currentSelectedSkill = getSkillAt(mouseX, mouseY);
				if (currentSelectedSkill != null) {
					buttonLearnEnabled = (skillState.get(currentSelectedSkill.getSkillId()) == SkillState.LEARNABLE)
							|| (skillState.get(currentSelectedSkill.getSkillId()) == SkillState.SELECTED_LEARNABLE);
				} else {
					isClicking = true;
				}
			}
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		isClicking = false;
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	private SkillBase getSkillAt(int mouseX, int mouseY) {
		for (String skillId : skills) {
			SkillBase skill = page.getSkill(skillId);
			Coordinate<Integer> cor = skill.getSkillLocation();
			int startX = skillScreenLeft + SKILL_SCREEN_WIDTH / 2 + x + cor.x * 48 - 12;
			int startY = skillScreenTop + SKILL_SCREEN_HEIGHT / 2 + y - cor.y * 48 - 12;
			if (skillState.get(skillId) == SkillState.UNSURE)
				continue;
			int sizeX = 24, sizeY = 24;
			if (startX < skillScreenLeft) {
				sizeX -= skillScreenLeft - startX;
				startX = skillScreenLeft;
			} else if (startX + 24 > skillScreenLeft + SKILL_SCREEN_WIDTH) {
				sizeX -= startX + 24 - skillScreenLeft - SKILL_SCREEN_WIDTH;
			}
			if (startY < skillScreenTop) {
				sizeY -= skillScreenTop - startY;
				startY = skillScreenTop;
			} else if (startY + 24 > skillScreenTop + SKILL_SCREEN_HEIGHT) {
				sizeY -= startY + 24 - skillScreenTop - SKILL_SCREEN_HEIGHT;
			}
			if (mouseX >= startX && mouseX < startX + sizeX && mouseY >= startY && mouseY < startY + sizeY)
				return skill;
		}
		return null;
	}
	
	protected void setDefaultRenderSettings() {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
	}
	
	private void updateXY(int mouseX, int mouseY) {
		if (isClicking) {
			x += mouseX - prevX;
			y += mouseY - prevY;
		}
		if (x < minX)
			x = minX;
		else if (x > maxX)
			x = maxX;
		if (y < minY)
			y = minY;
		else if (y > maxY)
			y = maxY;
		prevX = mouseX;
		prevY = mouseY;
	}
	
	private void updateSkillState() {
		for (Map.Entry<String, SkillState> entry : skillState.entrySet()) {
			SkillBase skill = page.getSkill(entry.getKey());
			if (skill.isLearned()) {
				skillState.replace(entry.getKey(), SkillState.LEARNED);
				continue;
			}
			boolean couldLearn = skill.getLearnLevel() <= learnLevel;
			ISkillPoint sp = player.getCapability(CapabilityHandler.capSkillPoint, null);
			for (Map.Entry<String, Integer> entrySP : skill.getSkillPointRequirement().entrySet()) {
				if (sp.getSPNum(entrySP.getKey()) < entrySP.getValue()) {
					couldLearn = false;
					break;
				}
			}
			ISkillTree st = player.getCapability(CapabilityHandler.capSkillTree, null);
			for (String dependency : skill.getSkillDependencies()) {
				if (!st.getSkill(dependency).isLearned()) {
					couldLearn = false;
					break;
				}
			}
			skillState.replace(entry.getKey(), couldLearn ? SkillState.LEARNABLE : SkillState.UNLEARNABLE);
		}
		if (currentSelectedSkill != null) {
			switch (skillState.get(currentSelectedSkill.getSkillId())) {
				case LEARNABLE:
					skillState.replace(currentSelectedSkill.getSkillId(), SkillState.SELECTED_LEARNABLE);
					break;
				case LEARNED:
					skillState.replace(currentSelectedSkill.getSkillId(), SkillState.SELECTED_LEARNED);
					break;
				default:
					break;
			}
		}
	}
	
	private void sync_learn(String skillID) {
		NetworkHandler.sync(player.world, new PacketEvent(PacketEvent.LEARN_SKILL_EVENT, skillID));
	}
}
