package kishso.digsites;

import com.google.gson.JsonObject;
import kishso.digsites.digsite_events.DigsiteEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;
import java.util.List;

public class DigsiteType {

    List<DigsiteEvent> digsiteEvents = new ArrayList<>();

    public static class Range<T> {
        public final T Lower;
        public final T Upper;

        Range(T lower, T upper)
        {
            this.Lower = lower;
            this.Upper = upper;
        }
    }

    protected String digsiteTypeId;

    private final Range<Integer> xRange;
    private final Range<Integer> yRange;
    private final Range<Integer> zRange;


    private final float convertPercentage;
    private final int tickFrequency;

    private final String lootTableString;

    public DigsiteType(
            String digsiteTypeString,
           int xRangeLower, int xRangeUpper,
           int yRangeLower, int yRangeUpper,
           int zRangeLower, int zRangeUpper,
           float convertPercentage, int tickFrequency,
           String lootTableIdString)
    {
        this.digsiteTypeId = digsiteTypeString;

        this.xRange = new Range<>(xRangeLower, xRangeUpper);
        this.yRange = new Range<>(yRangeLower, yRangeUpper);
        this.zRange = new Range<>(zRangeLower, zRangeUpper);

        this.convertPercentage = convertPercentage;
        this.tickFrequency = tickFrequency;

        this.lootTableString = lootTableIdString;
    }

    public static DigsiteType fromJson(JsonObject json)
    {

    }

    public String getDigsiteTypeId()
    {
        return digsiteTypeId;
    }

    Range<Integer> getXRange()
    {
        return xRange;
    }

    Range<Integer> getYRange()
    {
        return yRange;
    }

    Range<Integer> getZRange()
    {
        return zRange;
    }

    float getConvertPercentage()
    {
        return convertPercentage;
    }

    int getTickFrequency()
    {
        return tickFrequency;
    }

    String getLootTableString()
    {
        return lootTableString;
    }
}
