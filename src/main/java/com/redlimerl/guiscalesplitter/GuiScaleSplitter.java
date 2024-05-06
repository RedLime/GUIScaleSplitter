package com.redlimerl.guiscalesplitter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GuiScaleSplitter implements ClientModInitializer, ModMenuApi {

    public static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("guiscalesplitter.json");
    public static JsonObject CONFIG_OBJECT = new JsonObject();

    @Override
    public void onInitializeClient() {
        loadOptions();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GuiScaleScreen::new;
    }

    public static float getOption(String option) {
        return getOption(option, 1f);
    }

    public static float getOption(String option, float defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsFloat();
    }

    public static void setOption(String option, float value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static void loadOptions() {
        try {
            if (CONFIG_FILE.toFile().exists())
                CONFIG_OBJECT = GSON.fromJson(Files.readString(CONFIG_FILE, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveOptions() {
        try {
            Files.writeString(CONFIG_FILE, GSON.toJson(CONFIG_OBJECT), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
