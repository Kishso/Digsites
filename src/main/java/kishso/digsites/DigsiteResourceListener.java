package kishso.digsites;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

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
            continue;
        }
    }
}
