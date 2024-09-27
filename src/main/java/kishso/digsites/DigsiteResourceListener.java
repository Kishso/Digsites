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

                    String digsiteIdString = jsonData.get("digsite_type_id").getAsString();

                    JsonObject jsonBounds = jsonData.getAsJsonObject("bounds");
                    JsonObject xRange = jsonBounds.getAsJsonObject("x_range");
                    JsonObject yRange = jsonBounds.getAsJsonObject("y_range");
                    JsonObject zRange = jsonBounds.getAsJsonObject("z_range");

                    JsonObject jsonUpdate = jsonData.getAsJsonObject("update");

                    DigsiteType newType = new DigsiteType( digsiteIdString,
                            xRange.get("lower").getAsInt(), xRange.get("upper").getAsInt(),
                            yRange.get("lower").getAsInt(), yRange.get("upper").getAsInt(),
                            zRange.get("lower").getAsInt(), zRange.get("upper").getAsInt(),
                            jsonUpdate.get("chance_to_convert").getAsFloat(),
                            jsonUpdate.get("tick_frequency").getAsInt(),
                            jsonData.get("loot_table_type_id").getAsString());

                    DigsiteBookkeeper.LoadDigsiteTypes(digsiteIdString, newType);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            continue;
        }
    }
}
