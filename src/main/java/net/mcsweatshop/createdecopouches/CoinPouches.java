package net.mcsweatshop.createdecopouches;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.mcsweatshop.createdecopouches.api.ClientConfig;
import net.mcsweatshop.createdecopouches.api.Config;
import net.mcsweatshop.createdecopouches.core.Pouch;
import net.mcsweatshop.createdecopouches.events.ModEvents;
import net.mcsweatshop.createdecopouches.events.ModForgeEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CoinPouches.MODID)
public class CoinPouches
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "coinpouches";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> POUCH = ITEMS.register("pouch", ()-> new Pouch());


    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
//    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
//            .withTabsBefore(CreativeModeTabs.COMBAT)
//            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
//            .displayItems((parameters, output) -> {
//                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
//            }).build());

    public CoinPouches()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS<>);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        ITEMS.register(modEventBus);

        modEventBus.register(ModEvents.class);

//        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> modEventBus.register(ClientModEvents.class));

//        MinecraftForge.EVENT_BUS.register(ModForgeEvents.class);
//        ServerLifecycleHooks.getCurrentServer().getServerResources().managers().getRecipeManager().getRecipeFor(RecipeType.CRAFTING,)

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModForgeEvents.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
//        LOGGER.info("HELLO FROM COMMON SETUP");
//
//        if (Config.logDirtBlock)
//            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
//
//        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
//
//        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }


}
