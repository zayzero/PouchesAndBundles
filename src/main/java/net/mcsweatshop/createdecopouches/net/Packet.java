package net.mcsweatshop.createdecopouches.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet {
    public Packet(FriendlyByteBuf friendlyByteBuf) {
    }

    abstract public void encode(FriendlyByteBuf friendlyByteBuf);

    public abstract void handle(Supplier<NetworkEvent.Context> contextSupplier);
}
