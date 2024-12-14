package kishso.digsites.datagen;

import kishso.digsites.datagen.loot_table.DigsiteNormalLootProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DigsitesDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack basicDigsitesPack = fabricDataGenerator.createPack();

		basicDigsitesPack.addProvider(DigsiteNormalLootProvider::new);
	}


}
