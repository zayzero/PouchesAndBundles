package net.mcsweatshop.createdecopouches.net;

import net.mcsweatshop.createdecopouches.core.ModObjects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DelayInformationPacket {
    public final int delayToStart;
    public final int delayBetween;

    public DelayInformationPacket(int delayToStart, int delayBetween){
        this.delayToStart = delayToStart;
        this.delayBetween = delayBetween;
    }

    public DelayInformationPacket(FriendlyByteBuf friendlyByteBuf) {
        this.delayToStart=friendlyByteBuf.readInt();
        this.delayBetween=friendlyByteBuf.readInt();
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(delayToStart);
        friendlyByteBuf.writeInt(delayBetween);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayer sender = contextSupplier.get().getSender();
        if (sender==null) return;
        ModObjects.PlayerDelayConfigurations.put(sender.getName().getString(),new ModObjects.DelayConfig(delayToStart,delayBetween));

    }
}
