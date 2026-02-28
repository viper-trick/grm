package com.you.hidegrass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class HideGrassConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/hidegrass.json");

    public static class Data {
        public boolean enabled = false;
        // הוסף כאן שדות נוספים בעתיד, למשל:
        // public boolean hideTallGrass = true;
        // public boolean hideShortGrass = true;
    }

    private static Data data = new Data();

    private HideGrassConfig() {}

    public static synchronized void load() {
        if (!CONFIG_FILE.exists()) {
            save(); // יוצר קובץ עם ברירות מחדל
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Data loaded = GSON.fromJson(reader, Data.class);
            if (loaded != null) {
                data = loaded;
            } else {
                data = new Data();
            }
        } catch (Exception e) {
            // שמור לוג אך המשך עם ברירות מחדל
            System.err.println("[HideGrass] Failed to load config, using defaults: " + e.getMessage());
            data = new Data();
            save();
        }
    }

    public static synchronized void save() {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("[HideGrass] Failed to save config: " + e.getMessage());
        }
    }

    public static synchronized boolean isEnabled() {
        return data.enabled;
    }

    public static synchronized void setEnabled(boolean enabled) {
        data.enabled = enabled;
        save();
    }

    public static synchronized void toggle() {
        setEnabled(!data.enabled);
    }

    // גישה ישירה לנתונים למקרים מתקדמים (זהירות בשימוש ישיר)
    public static synchronized Data getData() {
        // החזר עותק אם תרצה למנוע שינוי ישיר
        Data copy = new Data();
        copy.enabled = data.enabled;
        return copy;
    }

    public static synchronized void setData(Data newData) {
        if (newData == null) return;
        data = newData;
        save();
    }
}
