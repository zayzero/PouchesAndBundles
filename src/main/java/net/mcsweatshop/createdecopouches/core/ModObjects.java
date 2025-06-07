package net.mcsweatshop.createdecopouches.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModObjects {

    //Player name, and delay config
    public static Map<String, DelayConfig> PlayerDelayConfigurations=new HashMap<>();

    //ID and instance
    public static Map<String, PouchInstance> PouchInstanceMap =new HashMap<>();

    public static class DelayConfig{
        public final int StartDelay;
        public final int DelayBetween;

        public DelayConfig(int startDelay, int delayBetween) {
            StartDelay = startDelay;
            DelayBetween = delayBetween;
        }
    }

    public static class PouchInstance{
        //if slot count is -1 it is unlimited
        public final int maxItems;
        public final List<String> itemWhiteList;
        public final List<String> itemTags;
        public final int color;
        public final int maxDifferentItems;
        public final boolean differentiateByNBT;
        public final boolean dropItems;

        public static final PouchInstance Empty=new PouchInstance(0,new ArrayList<>(),new ArrayList<>(),0,0,false,true);
        //maxItems is the total while, maxDifferentItems is how many different items exist i.e differentItemCount is 2 for: [{id:"minecraft:dirt",Count:3b},{id:"minecraft:stone",Count:2b}]
        //items with different tags count as same item, -1 means it is not checked. -1 is default

        public PouchInstance(int slotCount, List<String> whiteList, List<String> itemTags, int color, int maxDifferentItems, boolean differentiateByNBT, boolean dropItems) {
            this.maxItems = slotCount;
            this.itemWhiteList = whiteList;
            this.itemTags = itemTags;
            this.color = color;
            this.maxDifferentItems = maxDifferentItems;
            this.differentiateByNBT = differentiateByNBT;
            this.dropItems = dropItems;
        }
        public static void makeAndAddPouchInstance(ResourceLocation resourceLocation, JsonElement jsonElement){
            if (!jsonElement.isJsonObject()) return;
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (!isIntPrimitive(jsonObject,"maxItems")) return;
            int maxItems=jsonObject.get("maxItems").getAsInt();

            int maxDifferentItems=-1;
            if (isIntPrimitive(jsonObject,"maxDifferentItems")) maxDifferentItems = jsonObject.get("maxDifferentItems").getAsInt();

            boolean differentiateByNBT=false;
            if (isBooleanPrimitive(jsonObject,"differentiateByNBT")) differentiateByNBT = jsonObject.get("differentiateByNBT").getAsBoolean();

            boolean dropItems=true;
            if (isBooleanPrimitive(jsonObject,"dropItems")) dropItems = jsonObject.get("dropItems").getAsBoolean();

            int color=0;
            if (jsonObject.has("color")&&jsonObject.get("color").isJsonObject()) color=getColorJson(jsonObject.getAsJsonObject("color"));

            List<String> whitelist=new ArrayList<>();
            List<String> tags=new ArrayList<>();
            if (jsonObject.has("whitelist")&&jsonObject.get("whitelist").isJsonArray()) whitelist=getWhiteList(jsonObject.get("whitelist").getAsJsonArray(), tags);

            PouchInstanceMap.put(resourceLocation.toString(), new PouchInstance(maxItems,whitelist,tags,color,maxDifferentItems,differentiateByNBT,dropItems));
        }

        private static List<String> getWhiteList(JsonArray whitelist, List<String> itemTags) {
            ArrayList<String> list = new ArrayList<>();
            for (JsonElement jsonElement : whitelist) {
                if (!jsonElement.isJsonPrimitive()||!jsonElement.getAsJsonPrimitive().isString()) continue;
                String asString = jsonElement.getAsString();

                if (isTag(asString)) {
                    asString = asString.substring(1);
                    itemTags.add(asString);
                    continue;
                }
                else if (!ForgeRegistries.ITEMS.containsKey(new ResourceLocation(asString))) continue;

                list.add(asString);
            }
            return list;
        }

        private static boolean isTag(String asString) {
            return asString.charAt(0) == '#';
        }

        private static int getColorJson(JsonObject jsonObject) {
            int color=0;
            color+=isIntPrimitive(jsonObject,"r") ? jsonObject.get("r").getAsInt() : 0;
            color <<=8;
            color+=(isIntPrimitive(jsonObject,"g") ? jsonObject.get("g").getAsInt() : 0);
            color <<=8;
            color+=(isIntPrimitive(jsonObject,"b") ? jsonObject.get("b").getAsInt() : 0);
            return color;
        }

        public static int colorToInt(int r, int g, int b) {
            int result = 0;
            result += r;
            result <<= 8;
            result += g;
            result <<= 8;
            result += b;
            return result;
        }

        public static boolean isIntPrimitive(JsonObject jsonObject, String name){
            return jsonObject.has(name) &&
                    jsonObject.get(name).isJsonPrimitive() &&
                    jsonObject.get(name).getAsJsonPrimitive().isNumber();
        }
        public static boolean isBooleanPrimitive(JsonObject jsonObject, String name){
            return jsonObject.has(name) &&
                    jsonObject.get(name).isJsonPrimitive() &&
                    jsonObject.get(name).getAsJsonPrimitive().isBoolean();
        }
        public final void writeTags(ItemStack itemStack){

        }

        public static PouchInstance getInstance(@NotNull String id){
            if (id.isEmpty()||!PouchInstanceMap.containsKey(id)) return Empty;
            return PouchInstanceMap.get(id);
        }
        public static PouchInstance getInstance(@NotNull ItemStack item){
            String id = item.getOrCreateTag().contains("pouchid") ? item.getTag().getString("pouchid") : null;
            return id==null ? Empty : getInstance(id);
        }
    }
}
