package kishso.digsites;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import static kishso.digsites.Digsites.MOD_ID;



public class DigsiteResourceListener implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return Identifier.tryParse(MOD_ID, "my_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        // Clear Caches Here
        for(Identifier id : manager.findResources("worldgen/digsites", path -> path.getPath().endsWith(".json")).keySet()) {
            try {
                Optional<Resource> digsiteResource = manager.getResource(id);
                if(digsiteResource.isPresent()){
                    Resource digsiteRes = digsiteResource.get();
                    InputStream jsonStream = digsiteRes.getInputStream();

                    InputStreamReader jsonReader = new InputStreamReader(jsonStream);
                    JsonObject jsonData = JsonHelper.deserialize(jsonReader);

                    DigsiteType newType = new DigsiteType(jsonData);

                    DigsiteBookkeeper.LoadDigsiteTypes(newType.getDigsiteTypeId(), newType);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
