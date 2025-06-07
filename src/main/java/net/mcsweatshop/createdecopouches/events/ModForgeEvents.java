package net.mcsweatshop.createdecopouches.events;

import com.google.gson.JsonObject;
import net.mcsweatshop.createdecopouches.CoinPouches;
import net.mcsweatshop.createdecopouches.api.DataPackReader;
import net.mcsweatshop.createdecopouches.api.GivePouchCommand;
import net.mcsweatshop.createdecopouches.core.Pouch;
import net.mcsweatshop.createdecopouches.net.PacketHandler;
import net.mcsweatshop.createdecopouches.net.ServerToClientSyncPacket;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


@Mod.EventBusSubscriber(modid = CoinPouches.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModForgeEvents {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent e){
        new GivePouchCommand(e.getDispatcher());

    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent e){
        e.addListener(new DataPackReader());
    }

    @SubscribeEvent
    public static void itemStackOnOtherEvent(ItemStackedOnOtherEvent e){
        if (e.getClickAction()==ClickAction.PRIMARY) return;
        if (!e.getSlot().getItem().is(CoinPouches.POUCH.get())) return;
//        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> managePouchClient(e));
        e.setCanceled(true);
//        if (FMLEnvironment.dist==Dist.CLIENT&& Minecraft.getInstance().isSingleplayer()) managePouch(e);
//        else DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> ()-> managePouch(e));
        managePouch(e);
    }
    public static AtomicReference<ItemStack> itemStackAtomicReference=new AtomicReference<>();

    private static void managePouch(ItemStackedOnOtherEvent e) {
        if (e.getCarriedSlotAccess().get().is(ItemStack.EMPTY.getItem())) {
            boolean f = e.getCarriedSlotAccess().set(Pouch.getNextItem(e.getSlot().getItem(), true));
        } else {
            boolean addedToPouch = Pouch.addItem(e.getSlot().getItem(), e.getCarriedSlotAccess().get());
            if (addedToPouch) e.getCarriedSlotAccess().get().setCount(0);
        }

    }
    private static void managePouchClient(ItemStackedOnOtherEvent e) {

        if (e.getPlayer().isCreative()) managePouch(e);
//        if (e.getPlayer().isCreative()) System.out.println("in creative");
//        if (Minecraft.getInstance().isSingleplayer()) return;
//        if (e.getCarriedSlotAccess().get().is(ItemStack.EMPTY.getItem()))
//            e.getCarriedSlotAccess().set(Pouch.getNextItem(e.getSlot().getItem(), false));

    }

    @SubscribeEvent
    public static void joiningServer(PlayerEvent.PlayerLoggedInEvent e){
      PacketHandler.INSTANCE.send((PacketDistributor.PLAYER.with(()-> ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(e.getEntity().getUUID()))), new ServerToClientSyncPacket());
    }


}
