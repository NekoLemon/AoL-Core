package cn.catlemon.aol_core.gui.skilltree;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.catlemon.aol_core.AoLCore;
import cn.catlemon.aol_core.api.Coordinate;
import cn.catlemon.aol_core.api.SkillBase;
import cn.catlemon.aol_core.api.SkillTreePage;
import cn.catlemon.aol_core.capability.CapabilityHandler;
import cn.catlemon.aol_core.capability.ISkillPoint;
import cn.catlemon.aol_core.capability.ISkillTree;
import cn.catlemon.aol_core.gui.GuiHandler;
import cn.catlemon.aol_core.network.NetworkHandler;
import cn.catlemon.aol_core.network.PacketSkillPoint;
import cn.catlemon.aol_core.network.PacketSkillTree;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

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
	private static final int SKILL_SCREEN_OFFSET_X = 20;
	private int skillScreenLeft, skillScreenTop;
	
	private static final int DESC_SCREEN_WIDTH = 60;
	private static final int DESC_SCREEN_HEIGHT = 108;
	private static final int DESC_SCREEN_OFFSET_X = -88;
	private static final int DESC_SCREEN_OFFSET_Y = -16;
	private int descScreenLeft, descScreenTop;
	
	private int x, y;
	private int prevX, prevY;
	private int minX, minY;
	private int maxX, maxY;
	private boolean isClicking = false;
	
	private int currentTab, currentPage, totalPages;
	
	private ResourceLocation BACKGROUND = new ResourceLocation(
			AoLCore.MODID + ":" + "textures/gui/skilltree_background.png");
	private static ResourceLocation WINDOW = new ResourceLocation(AoLCore.MODID + ":" + "textures/gui/skilltree.png");
	
	private boolean buttonPagePrevEnabled, buttonPageNextEnabled, buttonLearnEnabled;
	
	private SkillBase currentSelectedSkill = null;
	private Set<String> skills = null;
	
	private enum SkillState {
		UNSURE, UNLEARNABLE, LEARNABLE, SELECTED_LEARNABLE, LEARNED, SELECTED_LEARNED
	};
	
	private Map<String, SkillState> skillState = new HashMap<String, SkillState>();
	
	public GuiSkillTree(EntityPlayer player, int pageGuiId, int learnLevel) {
		this.player = player;
		this.page = player.getCapability(CapabilityHandler.capSkillTree, null).getPage(pageGuiId);
		this.pages = player.getCapability(CapabilityHandler.capSkillTree, null).getPageList();
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
		skillScreenLeft = (width - SKILL_SCREEN_WIDTH) / 2 + SKILL_SCREEN_OFFSET_X;
		skillScreenTop = (height - SKILL_SCREEN_HEIGHT) / 2;
		descScreenLeft = (width - DESC_SCREEN_WIDTH) / 2 + DESC_SCREEN_OFFSET_X;
		descScreenTop = (height - DESC_SCREEN_HEIGHT) / 2 + DESC_SCREEN_OFFSET_Y;
	}
	
	@Override
	public void updateScreen() {
		guiLeft = (width - GUI_WIDTH) / 2;
		guiTop = (height - GUI_HEIGHT) / 2;
		skillScreenLeft = (width - SKILL_SCREEN_WIDTH) / 2 + SKILL_SCREEN_OFFSET_X;
		skillScreenTop = (height - SKILL_SCREEN_HEIGHT) / 2;
		descScreenLeft = (width - DESC_SCREEN_WIDTH) / 2 + DESC_SCREEN_OFFSET_X;
		descScreenTop = (height - DESC_SCREEN_HEIGHT) / 2 + DESC_SCREEN_OFFSET_Y;
		
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
	}
	
	public void drawBackground() {
		setDefaultRenderSettings();
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(skillScreenLeft, skillScreenTop, 16 - Math.abs((x + 10000) % 16),
				16 - Math.abs((y + 10000) % 16), SKILL_SCREEN_WIDTH, SKILL_SCREEN_HEIGHT);
	}
	
	public void drawSkills() {
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
			int startX = skillScreenLeft + SKILL_SCREEN_WIDTH / 2 + x + cor.x * 48 - 12;
			int startY = skillScreenTop + SKILL_SCREEN_HEIGHT / 2 + y - cor.y * 48 - 12;
			int iconStartX = startX + 4;
			int iconStartY = startY + 4;
			setDefaultRenderSettings();
			mc.getTextureManager().bindTexture(WINDOW);
			if (skillState.get(skillId) == SkillState.UNSURE)
				continue;
			int baseX = 0, baseY = 0;
			int offsetX = 0, offsetY = 0;
			int sizeX = 24, sizeY = 24;
			switch (skillState.get(skillId)) {
				case LEARNABLE:
					baseX = 148;
					baseY = 164;
					break;
				case LEARNED:
					baseX = 180;
					baseY = 164;
					break;
				case SELECTED_LEARNABLE:
					baseX = 148;
					baseY = 196;
					break;
				case SELECTED_LEARNED:
					baseX = 180;
					baseY = 196;
					break;
				case UNLEARNABLE:
					baseX = 116;
					baseY = 164;
					break;
				default:
					break;
			}
			if (startX < skillScreenLeft) {
				offsetX += skillScreenLeft - startX;
				sizeX -= skillScreenLeft - startX;
				startX = skillScreenLeft;
			} else if (startX + 24 > skillScreenLeft + SKILL_SCREEN_WIDTH) {
				sizeX -= startX + 24 - skillScreenLeft - SKILL_SCREEN_WIDTH;
			}
			if (iconStartX < skillScreenLeft)
				iconStartX = skillScreenLeft;
			if (startY < skillScreenTop) {
				offsetY += skillScreenTop - startY;
				sizeY -= skillScreenTop - startY;
				startY = skillScreenTop;
			} else if (startY + 24 > skillScreenTop + SKILL_SCREEN_HEIGHT) {
				sizeY -= startY + 24 - skillScreenTop - SKILL_SCREEN_HEIGHT;
			}
			if (iconStartY < skillScreenTop)
				iconStartY = skillScreenTop;
			if (sizeX > 0 && sizeY > 0)
				drawTexturedModalRect(startX, startY, baseX + offsetX, baseY + offsetY, sizeX, sizeY);
			
			startX = (startX < skillScreenLeft) ? ((sizeX > 20) ? startX + sizeX - 20 : startX) : startX + 4;
			startY = (startY < skillScreenTop) ? ((sizeY > 20) ? startY + sizeY - 20 : startY) : startY + 4;
			offsetX = (offsetX > 4) ? offsetX - 4 : 0;
			offsetY = (offsetY > 4) ? offsetY - 4 : 0;
			sizeX = (sizeX >= 20) ? 16 : sizeX - 4;
			sizeY = (sizeY >= 20) ? 16 : sizeY - 4;
			// 绘制16x16技能图标
			if (skill.getSkillIcon() != null) {
				setDefaultRenderSettings();
				mc.getTextureManager().bindTexture(skill.getSkillIcon());
				drawTexturedModalRect(iconStartX, iconStartY, skill.getSkillIconLocation().x + offsetX,
						skill.getSkillIconLocation().y + offsetY, sizeX, sizeY);
			}
		}
		zLevel -= 5;
	}
	
	public Coordinate<Double> getLineCross(Coordinate<Double> line1_start, Coordinate<Double> line1_end,
			Coordinate<Integer> line2_start, Coordinate<Integer> line2_end) {
		/*
		 * if (line1_end.x == line1_start.x) { if (line2_end.x == line2_start.x) return
		 * null; if ((line2_start.x - line1_start.x) * (line2_end.x - line1_start.x) >
		 * 0) return null; return new Coordinate<Double>(line1_start.x, (line1_start.x -
		 * line2_start.x) * (line2_end.y - line2_start.y) / (line2_end.x -
		 * line2_start.x)); } double k = (line1_end.y - line1_start.y) / (line1_end.x -
		 * line1_start.x); double b = line1_end.y - k * line1_end.x; if (line2_end.x ==
		 * line2_start.x) { double x = line2_end.x; double y = k * x + b; if
		 * ((line1_end.x - line2_end.x) * (line1_start.x - line2_end.x) > 0) return
		 * null; return new Coordinate<Double>(x, y); } double k2 = (line2_end.y -
		 * line2_start.y) / (line2_end.x - line2_start.x); double b2 = line2_end.y - k2
		 * * line2_end.x; if (k == k2) return null; double x = (b - b2) / (k - k2);
		 * double y = k * x + b; if ((x - line1_end.x) * (x - line1_start.x) > 0) return
		 * null; return new Coordinate<Double>(x, y);
		 */
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
	
	public void drawLine(double startX, double startY, double endX, double endY, boolean enabled) {
		int color = enabled ? 0xFFFFFFFF : 0xFFAAAAAA;
		float alpha = (color >> 24 & 0xFF) / 255.0F;
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.glLineWidth(5);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.color(red, green, blue, alpha);
		buffer.begin(1, DefaultVertexFormats.POSITION);
		buffer.pos(startX, startY, 0.0D).endVertex();
		buffer.pos(endX, endY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.glLineWidth(1);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	public void drawWindow() {
		zLevel += 10;
		setDefaultRenderSettings();
		mc.getTextureManager().bindTexture(WINDOW);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);
		zLevel -= 10;
	}
	
	public void drawLearnButton() {
		zLevel += 20;
		setDefaultRenderSettings();
		mc.getTextureManager().bindTexture(WINDOW);
		if (buttonLearnEnabled) {
			drawTexturedModalRect(guiLeft + 16, guiTop + 128, 0, 160, 48, 24);
		} else {
			drawTexturedModalRect(guiLeft + 16, guiTop + 128, 0, 192, 48, 24);
		}
		zLevel -= 20;
	}
	
	public void drawTab() {
		zLevel += 20;
		int tabNums = (currentPage == totalPages - 1) ? pages.size() % 5 : 5;
		boolean thisPageContainsActiveOne = (pages.indexOf(page.getSkillTreePageId()) / 5 == currentPage);
		for (int tab = 0; tab < tabNums; tab++) {
			setDefaultRenderSettings();
			mc.getTextureManager().bindTexture(WINDOW);
			if (thisPageContainsActiveOne && tab == currentTab) {
				drawTexturedModalRect(guiLeft + 222, guiTop + 24 * tab + 8, 222, 160, 34, 24);
			} else {
				drawTexturedModalRect(guiLeft + 222, guiTop + 24 * tab + 8, 222, 192, 34, 24);
			}
			zLevel += 10;
			setDefaultRenderSettings();
			SkillTreePage currentDrawingPage = player.getCapability(CapabilityHandler.capSkillTree, null)
					.getPage(pages.get(5 * currentPage + tab));
			// TODO 画技能页的标志
			zLevel -= 10;
		}
		if (buttonPagePrevEnabled) {
			setDefaultRenderSettings();
			mc.getTextureManager().bindTexture(WINDOW);
			drawTexturedModalRect(guiLeft + 224, guiTop + 144, 224, 224, 16, 16);
		}
		if (buttonPageNextEnabled) {
			setDefaultRenderSettings();
			mc.getTextureManager().bindTexture(WINDOW);
			drawTexturedModalRect(guiLeft + 240, guiTop + 144, 240, 224, 16, 16);
		}
		zLevel -= 20;
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
				page.learnSkill((EntityPlayerMP) player, currentSelectedSkill.getSkillId());
				buttonLearnEnabled = false;
				updateSkillState();
				sync();
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
		// TODO
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
		// TODO
		for (Map.Entry<String, SkillState> entry : skillState.entrySet()) {
			skillState.replace(entry.getKey(), SkillState.LEARNABLE);
		}
	}
	
	private void sync() {
		{
			PacketSkillPoint message = new PacketSkillPoint();
			ISkillPoint skillPoint = player.getCapability(CapabilityHandler.capSkillPoint, null);
			Capability.IStorage<ISkillPoint> storage = CapabilityHandler.capSkillPoint.getStorage();
			message.compound = new NBTTagCompound();
			message.compound.setTag(CapabilityHandler.TAGSKILLPOINT,
					storage.writeNBT(CapabilityHandler.capSkillPoint, skillPoint, null));
			NetworkHandler.sync(player.world, message);
		}
		{
			
			PacketSkillTree message = new PacketSkillTree();
			ISkillTree skillTree = player.getCapability(CapabilityHandler.capSkillTree, null);
			Capability.IStorage<ISkillTree> storage = CapabilityHandler.capSkillTree.getStorage();
			message.compound = new NBTTagCompound();
			message.compound.setTag(CapabilityHandler.TAGSKILLTREE,
					storage.writeNBT(CapabilityHandler.capSkillTree, skillTree, null));
			NetworkHandler.sync(player.world, message);
		}
	}
}
