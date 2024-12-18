package kishso.digsites;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static kishso.digsites.DigsitesMod.MODID;


public class DigsiteResourceListener extends SimplePreparableReloadListener<List<DigsiteType>> {
//
////    @Override
////    public Identifier getFabricId() {
////        return Identifier.tryParse(MOD_ID, "my_resources");
////    }
////
////    @Override
////    public void reload(ResourceManager manager) {
////        // Clear Caches Here
////        for(Identifier id : manager.findResources("worldgen/digsites", path -> path.getPath().endsWith(".json")).keySet()) {
////            try {
////                Optional<Resource> digsiteResource = manager.getResource(id);
////                if(digsiteResource.isPresent()){
////                    Resource digsiteRes = digsiteResource.get();
////                    InputStream jsonStream = digsiteRes.getInputStream();
////
////                    InputStreamReader jsonReader = new InputStreamReader(jsonStream);
////                    JsonObject jsonData = JsonHelper.deserialize(jsonReader);
////
////                    DigsiteType newType = new DigsiteType(jsonData);
////
////                    DigsiteBookkeeper.LoadDigsiteTypes(newType.getDigsiteTypeId(), newType);
////
////                }
////            } catch (IOException e) {
////                throw new RuntimeException(e);
////            }
////        }
////    }
//

    @Override
    protected List<DigsiteType> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        List<DigsiteType> digsiteTypes = new ArrayList<>();
        for(ResourceLocation resource : resourceManager.listResourceStacks("worldgen/digsites", path -> path.getPath().endsWith(".json")).keySet())
        {
            try {
                Optional<Resource> digsiteResource = resourceManager.getResource(resource);
                if(digsiteResource.isPresent()){
                    Resource digsiteRes = digsiteResource.get();
                    InputStream jsonStream = digsiteRes.open();

                    InputStreamReader jsonReader = new InputStreamReader(jsonStream);
                    JsonElement jsonData = JsonParser.parseReader(jsonReader);
                    if(jsonData instanceof JsonObject)
                    {
                        DigsiteType newType = new DigsiteType((JsonObject)jsonData);
                        digsiteTypes.add(newType);

                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return digsiteTypes;
    }

    @Override
    protected void apply(List<DigsiteType> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        for(DigsiteType type : object)
        {
            DigsiteBookkeeper.LoadDigsiteTypes(type.getDigsiteTypeId(), type);
        }
    }
}
