package kishso.digsites;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class DigsiteType {

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

//    String getDigsiteTypeId()
//    {
//        return digsiteTypeId;
//    }

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

    public NbtElement toNbt()
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("type_id", digsiteTypeId);
        nbt.putIntArray("xRange", new int[]{xRange.Lower, xRange.Upper});
        nbt.putIntArray("yRange", new int[]{yRange.Lower, yRange.Upper});
        nbt.putIntArray("zRange", new int[]{zRange.Lower, zRange.Upper});
        nbt.putString("lootTable", lootTableString);
        nbt.putInt("tickFrequency", tickFrequency);
        nbt.putFloat("convertPercentage", convertPercentage);

        return nbt;
    }

    public static DigsiteType fromNbt(NbtElement nbt)
    {
        if(nbt instanceof NbtCompound root)
        {
            String type = root.getString("type_id");

            int[] xRangeList = root.getIntArray("xRange");
            int[] yRangeList = root.getIntArray("yRange");
            int[] zRangeList = root.getIntArray("zRange");
            String lootTableString = root.getString("lootTable");

            float convertPer = root.getFloat("convertPercentage");
            int tickFreq = root.getInt("tickFrequency");

            return new DigsiteType(
                    type,
                    xRangeList[0], xRangeList[1],
                    yRangeList[0], yRangeList[1],
                    zRangeList[0], zRangeList[1],
                    convertPer, tickFreq,
                    lootTableString);
        }
        return null;
    }
}
