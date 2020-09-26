package cn.catlemon.aol_core.advancement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import cn.catlemon.aol_core.AoLCore;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public final class GetSkillPointTrigger implements ICriterionTrigger<GetSkillPointTrigger.Instance> {
	private static final ResourceLocation triggerID = new ResourceLocation(AoLCore.MODID, "get_skillpoint");
	
	private final Map<PlayerAdvancements, Listeners> listeners = new HashMap<PlayerAdvancements, Listeners>();
	
	@Nonnull
	@Override
	public ResourceLocation getId() {
		return triggerID;
	}
	
	@Override
	public void addListener(PlayerAdvancements playerAdvancements, Listener<GetSkillPointTrigger.Instance> listener) {
		Listeners playerListeners = this.listeners.get(playerAdvancements);
		if (playerListeners == null)
			this.listeners.put(playerAdvancements, (playerListeners = new Listeners(playerAdvancements)));
		playerListeners.add(listener);
	}
	
	@Override
	public void removeListener(PlayerAdvancements playerAdvancements,
			Listener<GetSkillPointTrigger.Instance> listener) {
		Listeners playerListeners = this.listeners.get(playerAdvancements);
		if (playerListeners != null) {
			playerListeners.remove(listener);
			if (playerListeners.isEmpty())
				this.listeners.remove(playerAdvancements);
		}
	}
	
	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancements) {
		this.listeners.remove(playerAdvancements);
	}
	
	@Override
	public GetSkillPointTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		String skillPointType = json.has("skillpoint") ? JsonUtils.getString(json, "skillpoint").toLowerCase() : null;
		return new Instance(skillPointType);
	}
	
	public void trigger(EntityPlayerMP player, String skillPointType) {
		Listeners playerListeners = this.listeners.get(player.getAdvancements());
		if (playerListeners != null)
			playerListeners.trigger(skillPointType);
	}
	
	static final class Instance extends AbstractCriterionInstance {
		@Nullable
		private String _skillPointType;
		
		Instance(@Nullable String skillPointType) {
			super(triggerID);
			_skillPointType = skillPointType;
		}
		
		boolean test(String skillPointType) {
			return (_skillPointType == null || _skillPointType.equals(skillPointType));
		}
	}
	
	static class Listeners {
		private final PlayerAdvancements advancements;
		private final HashSet<Listener<Instance>> listeners = new HashSet<Listener<Instance>>();
		
		public Listeners(PlayerAdvancements playerAdvancements) {
			this.advancements = playerAdvancements;
		}
		
		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}
		
		public void add(Listener<Instance> listener) {
			this.listeners.add(listener);
		}
		
		public void remove(Listener<Instance> listener) {
			this.listeners.remove(listener);
		}
		
		public void trigger(String skillPointType) {
			ArrayList<Listener<Instance>> list = null;
			for (Listener<Instance> listener : this.listeners) {
				if (listener.getCriterionInstance().test(skillPointType)) {
					if (list == null)
						list = new ArrayList<Listener<Instance>>();
					
					list.add(listener);
				}
			}
			if (list != null) {
				for (Listener<Instance> listener : list) {
					listener.grantCriterion(this.advancements);
				}
			}
		}
	}
}
