package com.you.hidegrass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class HideGrassConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("hidegrass.json");

    public static class Data {
        public boolean enabled = false;
    }

    private static Data data = new Data();

    static {
        try {
            if (FILE.exists()) {
                data = GSON.fromJson(new FileReader(FILE), Data.class);
            } else {
                save();
            }
        } catch (Exception ignored) {}
    }

    public static void save() {
        try (Writer w = new FileWriter(FILE)) {
            GSON.toJson(data, w);
        } catch (Exception ignored) {}
    }

    public static boolean isEnabled() {
        return data.enabled;
    }

    public static void setEnabled(boolean enabled) {
        data.enabled = enabled;
        save();
    }

    public static void toggle() {
        setEnabled(!isEnabled());
    }
}
