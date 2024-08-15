package kishso.digsites.datagen.loot_table;

import kishso.digsites.Digsites;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class DigsiteNormalLootProvider extends SimpleFabricLootTableProvider {

    public static final RegistryKey<LootTable> DIGSITE_ARCHAEOLOGY =
            RegistryKey.of(RegistryKeys.LOOT_TABLE, Digsites.id("digsite_normal"));

    public DigsiteNormalLootProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup, LootContextTypes.ARCHAEOLOGY);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        lootTableBiConsumer.accept(DIGSITE_ARCHAEOLOGY, LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                        .with(ItemEntry.builder(Items.BUNDLE)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f)))
                        )
                        .with(ItemEntry.builder(Items.RAW_IRON)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2,8)))
                        )
                        .with(ItemEntry.builder(Items.BONE)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1,3)))
                        )
                        .with(ItemEntry.builder(Items.RAW_COPPER)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(4,12)))
                        )
                        .with(ItemEntry.builder(Items.SKELETON_SKULL)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f)))
                        )
                )
        );
    }
}
