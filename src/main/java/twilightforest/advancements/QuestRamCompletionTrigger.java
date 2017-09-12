package twilightforest.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import twilightforest.TwilightForestMod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class QuestRamCompletionTrigger implements ICriterionTrigger<QuestRamCompletionTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(TwilightForestMod.ID, "complete_quest_ram");
    private final Map<PlayerAdvancements, QuestRamCompletionTrigger.Listeners> listeners = Maps.newHashMap();

    public ResourceLocation getId()
    {
        return ID;
    }

    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<QuestRamCompletionTrigger.Instance> listener) {
        QuestRamCompletionTrigger.Listeners listeners = this.listeners.computeIfAbsent(playerAdvancementsIn, Listeners::new);

        listeners.add(listener);
    }

    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<QuestRamCompletionTrigger.Instance> listener) {
        QuestRamCompletionTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners != null) {
            listeners.remove(listener);

            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    public QuestRamCompletionTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new QuestRamCompletionTrigger.Instance();
    }

    public void trigger(EntityPlayerMP player) {
        QuestRamCompletionTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());

        if (listeners != null) {
            listeners.trigger();
        }
    }

    public static class Instance extends AbstractCriterionInstance {
        public Instance() {
            super(QuestRamCompletionTrigger.ID);
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<QuestRamCompletionTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(Listener<QuestRamCompletionTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<QuestRamCompletionTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger() {
            for (Listener<QuestRamCompletionTrigger.Instance> listener : Lists.newArrayList(this.listeners)) {
                listener.grantCriterion(this.playerAdvancements);
            }
        }
    }
}