package com.you.hidegrass;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class HideGrassConfigScreen {

    private HideGrassConfigScreen() {}

    public static Screen create(Screen parent) {
        try {
            // יצירת ה‑ConfigBuilder
            ConfigBuilder builder = ConfigBuilder.create();
            if (builder == null) throw new IllegalStateException("ConfigBuilder.create() returned null");

            builder.setParentScreen(parent).setTitle(Text.literal("Hide Grass Settings"));

            // קטגוריה כללית
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            if (entryBuilder == null) throw new IllegalStateException("entryBuilder() returned null");

            // Toggle: Enabled
            general.addEntry(
                    entryBuilder.startBooleanToggle(
                                    Text.literal("Enabled"),
                                    HideGrassConfig.isEnabled()
                            )
                            .setSaveConsumer(value -> {
                                HideGrassConfig.setEnabled(value);
                                HideGrassMod.refresh(); // רענון בזמן אמת
                            })
                            .setDefaultValue(HideGrassConfig.isEnabled())
                            .build()
            );

            // קטגוריה נוספת עם תיאור קצר
            builder.getOrCreateCategory(Text.literal("Advanced"))
                    .addEntry(entryBuilder.startTextDescription(Text.literal("Use the toggle above to hide/show tall grass.")).build());

            // Runnable לשמירה (אופציונלי)
            builder.setSavingRunnable(() -> {
                // אם יש מנגנון שמירה לקובץ, קרא לו כאן, לדוגמה:
                // HideGrassConfig.saveToDisk();
            });

            // החזרת המסך שנבנה
            return builder.build();

        } catch (Throwable t) {
            // לוג לקונסול למפתח
            t.printStackTrace();

            // הודעה לשחקן אם אפשר
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                client.execute(() -> client.player.sendMessage(Text.literal("[grm] Failed to open config: " + t.getClass().getSimpleName()), false));
            }

            // החזר מסך גיבוי ידידותי עם כפתור Close
            return new Screen(Text.literal("Config Error")) {
                @Override
                protected void init() {
                    super.init();
                    this.addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> MinecraftClient.getInstance().setScreen(parent))
                            .dimensions(this.width / 2 - 50, this.height / 2 - 10, 100, 20).build());
                }
            };
        }
    }
}
