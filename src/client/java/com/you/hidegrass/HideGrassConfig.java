package com.you.hidegrass;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import java.util.HashSet;
import java.util.Set;

@Config(name = "hidegrass")
public class HideGrassConfig implements ConfigData {

    public boolean enabled = true;

    // Excluded so AutoConfig doesn't try to render Set<String> in its default GUI
    // (we manage this ourselves via HideGrassConfigScreen)
    @ConfigEntry.Gui.Excluded
    public Set<String> hiddenPlants = new HashSet<>();

    // ── Helpers ───────────────────────────────────────────────────────────────

    public static HideGrassConfig get() {
        return AutoConfig.getConfigHolder(HideGrassConfig.class).getConfig();
    }

    public static void save() {
        AutoConfig.getConfigHolder(HideGrassConfig.class).save();
    }

    public static void init() {
        AutoConfig.register(HideGrassConfig.class, GsonConfigSerializer::new);
    }

    // Always access via get() — never via static fields directly
    public static boolean isEnabled() {
        return get().enabled;
    }

    public static boolean shouldHide(String blockId) {
        HideGrassConfig cfg = get();
        return cfg.enabled && cfg.hiddenPlants.contains(blockId);
    }

    public static void setPlantHidden(String blockId, boolean hidden) {
        HideGrassConfig cfg = get();
        if (hidden) {
            cfg.hiddenPlants.add(blockId);
        } else {
            cfg.hiddenPlants.remove(blockId);
        }
    }
}