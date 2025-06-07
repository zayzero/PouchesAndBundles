package net.mcsweatshop.createdecopouches.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.mcsweatshop.createdecopouches.core.ModObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class DataPackReader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public DataPackReader() {
        super(GSON, "pouches");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> p_10793_, ResourceManager p_10794_, ProfilerFiller p_10795_) {
        ModObjects.PouchInstanceMap.clear();
        p_10793_.forEach(ModObjects.PouchInstance::makeAndAddPouchInstance);
    }
}
