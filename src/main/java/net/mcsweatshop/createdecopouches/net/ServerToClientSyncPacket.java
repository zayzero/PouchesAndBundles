package net.mcsweatshop.createdecopouches.net;

import net.mcsweatshop.createdecopouches.api.ClientConfig;
import net.mcsweatshop.createdecopouches.core.ModObjects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ServerToClientSyncPacket {


    public ServerToClientSyncPacket(){}

    public ServerToClientSyncPacket(FriendlyByteBuf friendlyByteBuf) {
        ModObjects.PouchInstanceMap.clear();
        CompoundTag pouchInstanceMapNBT = friendlyByteBuf.readAnySizeNbt();
        for (String key : pouchInstanceMapNBT.getAllKeys()) {
            //pI is just pouchInstance
            if (!(pouchInstanceMapNBT.get(key) instanceof CompoundTag pI)) continue;
            int color = pI.getInt("color");
            int maxItems = pI.getInt("maxItems");
            int maxDifferentItems = pI.getInt("maxDifferentItems");
            boolean dropItems = pI.getBoolean("dropItems");
            boolean differentiateByNBT = pI.getBoolean("differentiateByNBT");
            List<String> itemTags=new ArrayList<>();
            for (Tag tag : pI.getList("itemTags", Tag.TAG_STRING)) if(tag instanceof StringTag stringTag) itemTags.add(stringTag.getAsString());
            List<String> whitelist=new ArrayList<>();
            for (Tag tag : pI.getList("whitelist", Tag.TAG_STRING)) if(tag instanceof StringTag stringTag) whitelist.add(stringTag.getAsString());
            ModObjects.PouchInstanceMap.put(key, new ModObjects.PouchInstance(maxItems,whitelist,itemTags,color,maxDifferentItems,differentiateByNBT,dropItems));
        }
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        CompoundTag pouchInstanceMapTag=new CompoundTag();
        ModObjects.PouchInstanceMap.forEach((id, pI)->{
            CompoundTag tag=new CompoundTag();
            tag.putInt("color",pI.color);
            tag.putInt("maxItems",pI.maxItems);
            tag.putInt("maxDifferentItems",pI.maxDifferentItems);
            tag.putBoolean("dropItems",pI.dropItems);
            tag.putBoolean("differentiateByNBT",pI.differentiateByNBT);
            ListTag itemTags=new ListTag();
            for (String itemTag : pI.itemTags) itemTags.add(StringTag.valueOf(itemTag));
            tag.put("itemTags",itemTags);
            ListTag itemWhitelist=new ListTag();
            for (String itemTag : pI.itemWhiteList) itemWhitelist.add(StringTag.valueOf(itemTag));
            tag.put("whitelist",itemWhitelist);
            pouchInstanceMapTag.put(id,tag);
        });
        friendlyByteBuf.writeNbt(pouchInstanceMapTag);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        PacketHandler.INSTANCE.sendToServer(new DelayInformationPacket(ClientConfig.DelayToStartInt,ClientConfig.DelayBetweenInt));
    }
}
