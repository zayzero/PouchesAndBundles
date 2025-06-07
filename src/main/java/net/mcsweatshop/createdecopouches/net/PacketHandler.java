package net.mcsweatshop.createdecopouches.net;

import net.mcsweatshop.createdecopouches.CoinPouches;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public final static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
                    new ResourceLocation(CoinPouches.MODID, "main"))
            .serverAcceptedVersions((status) -> true)
            .clientAcceptedVersions((status) -> true)
            .networkProtocolVersion(() -> PacketHandler.PROTOCOL_VERSION).simpleChannel();

    private PacketHandler(){}
    public static void init() {
        int index = 0;
        INSTANCE.messageBuilder(DelayInformationPacket.class,index++, NetworkDirection.PLAY_TO_SERVER).encoder(DelayInformationPacket::encode).decoder(DelayInformationPacket::new).consumerMainThread(DelayInformationPacket::handle).add();
//        INSTANCE.messageBuilder(MouseReleasedPacket.class,index++, NetworkDirection.PLAY_TO_SERVER).encoder(MouseReleasedPacket::encode).decoder(MouseReleasedPacket::new).consumerMainThread(MouseReleasedPacket::handle).add();
        INSTANCE.messageBuilder(ServerToClientSyncPacket.class,index++, NetworkDirection.PLAY_TO_CLIENT).encoder(ServerToClientSyncPacket::encode).decoder(ServerToClientSyncPacket::new).consumerMainThread(ServerToClientSyncPacket::handle).add();

    }

}
