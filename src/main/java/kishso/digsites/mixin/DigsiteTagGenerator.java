package kishso.digsites.mixin;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

class DigsiteTagGenerator extends ItemTagProvider {
    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {

    }
    public DigsiteTagGenerator(FabricDataOutput output,
                               CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture,
                               CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture)
    {
        super(output, completableFuture, tagLookupCompletableFuture);
    }
}
