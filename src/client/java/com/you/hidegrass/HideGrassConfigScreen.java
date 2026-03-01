package com.you.hidegrass;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HideGrassConfigScreen extends Screen {

    private final Screen parent;
    private CheckboxWidget enabledCheckbox;

    // רשימת הצמחים שניתן להסתיר — תוכל להרחיב בעתיד
    private static final List<String> KNOWN_PLANTS = List.of(
            "minecraft:grass",
            "minecraft:short_grass",
            "minecraft:tall_grass",
            "minecraft:fern",
            "minecraft:large_fern",
            "minecraft:dead_bush",
            "minecraft:dandelion",
            "minecraft:poppy",
            "minecraft:blue_orchid",
            "minecraft:allium",
            "minecraft:azure_bluet",
            "minecraft:red_tulip",
            "minecraft:orange_tulip",
            "minecraft:white_tulip",
            "minecraft:pink_tulip",
            "minecraft:oxeye_daisy",
            "minecraft:cornflower",
            "minecraft:lily_of_the_valley",
            "minecraft:sunflower",
            "minecraft:lilac",
            "minecraft:rose_bush",
            "minecraft:peony",
            "minecraft:pitcher_plant",
            "minecraft:torchflower",
            "minecraft:brown_mushroom",
            "minecraft:red_mushroom",
            "minecraft:sugar_cane",
            "minecraft:bamboo"
    );

    // כמה צמחים להציג בכל עמוד
    private static final int PAGE_SIZE = 8;
    private int currentPage = 0;

    private final List<CheckboxWidget> plantCheckboxes = new ArrayList<>();

    public HideGrassConfigScreen(Screen parent) {
        super(Text.literal("HideGrass Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        plantCheckboxes.clear();
        TextRenderer tr = this.textRenderer;

        // Enable mod checkbox
        enabledCheckbox = CheckboxWidget.builder(Text.literal("Enable mod"), tr)
                .checked(HideGrassConfig.isEnabled())
                .callback((cb, checked) -> {
                    HideGrassConfig.get().enabled = checked;
                    HideGrassConfig.save();
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.worldRenderer != null) {
                        mc.worldRenderer.reload();
                    }
                })
                .pos(this.width / 2 - 100, 30)
                .build();
        this.addDrawableChild(enabledCheckbox);

        // ── כותרת לרשימת הצמחים ──────────────────────────────────────────
        // (מצוירת ב-render)

        // Plant list per current page
        int startY = 70;
        int startIndex = currentPage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, KNOWN_PLANTS.size());

        for (int i = startIndex; i < endIndex; i++) {
            String plantId = KNOWN_PLANTS.get(i);
            String displayName = plantId.replace("minecraft:", "").replace("_", " ");

            CheckboxWidget cb = CheckboxWidget.builder(Text.literal(displayName), tr)
                    .checked(HideGrassConfig.get().hiddenPlants.contains(plantId))
                    .callback((checkbox, checked) -> {
                        HideGrassConfig.setPlantHidden(plantId, checked);
                        HideGrassConfig.save();
                        // Reload chunks immediately so the change is visible right away
                        MinecraftClient mc = MinecraftClient.getInstance();
                        if (mc.worldRenderer != null) {
                            mc.worldRenderer.reload();
                        }
                    })
                    .pos(this.width / 2 - 100, startY + (i - startIndex) * 22)
                    .build();

            plantCheckboxes.add(cb);
            this.addDrawableChild(cb);
        }

        // Pagination buttons
        int navY = startY + PAGE_SIZE * 22 + 6;
        int totalPages = (int) Math.ceil((double) KNOWN_PLANTS.size() / PAGE_SIZE);

        if (currentPage > 0) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("◀ Prev"), b -> {
                currentPage--;
                this.clearAndInit();
            }).dimensions(this.width / 2 - 105, navY, 80, 20).build());
        }

        if (currentPage < totalPages - 1) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Next ▶"), b -> {
                currentPage++;
                this.clearAndInit();
            }).dimensions(this.width / 2 + 25, navY, 80, 20).build());
        }

        // Close button — forces chunk reload so hidden plants disappear immediately
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), b -> {
            HideGrassConfig.save();
            // BlockModelRenderer runs during chunk compilation, not every frame.
            // Without a reload, already-compiled chunks keep showing hidden plants.
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.worldRenderer != null) {
                mc.worldRenderer.reload();
            }
            mc.setScreen(parent);
        }).dimensions(this.width / 2 - 50, this.height - 35, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // ב-1.21.11 super.render() כבר קורא renderBackground בפנים —
        // קריאה כפולה גורמת ל-"Can only blur once per frame" crash
        super.render(context, mouseX, mouseY, delta);

        // Main title
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("HideGrass Settings"),
                this.width / 2, 10,
                0xFFFFFF
        );

        // Plant list subtitle
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("— Select plants to hide —"),
                this.width / 2, 55,
                0xAAAAAA
        );

        // Page indicator
        int totalPages = (int) Math.ceil((double) KNOWN_PLANTS.size() / PAGE_SIZE);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Page " + (currentPage + 1) + " of " + totalPages),
                this.width / 2, 70 + PAGE_SIZE * 22 + 10,
                0x888888
        );
    }

    public static Screen create(Screen parent) {
        return new HideGrassConfigScreen(parent);
    }
}