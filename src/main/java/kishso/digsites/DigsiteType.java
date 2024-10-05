package kishso.digsites;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kishso.digsites.digsite_events.DigsiteEvent;
import kishso.digsites.digsite_events.DigsiteEventFactory;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class DigsiteType {

    public static class JsonConstants {
        final static String digsiteTypeId = "digsite_type_id";
        final static String bounds = "bounds";
        final static String xRange = "x_range";
        final static String yRange = "y_range";
        final static String zRange = "z_range";
        final static String lowerBounds = "lower";
        final static String upperBounds = "upper";

        final static String events = "events";
    }

    public static class Range<T> {
        public final T Lower;
        public final T Upper;

        Range(T lower, T upper)
        {
            this.Lower = lower;
            this.Upper = upper;
        }

        Range<T> copy(){
            return new Range<>(this.Lower, this.Upper);
        }
    }

    protected final String digsiteTypeId;

    private final Range<Integer> xRange;
    private final Range<Integer> yRange;
    private final Range<Integer> zRange;

    private final List<DigsiteEvent> digsiteEvents = new ArrayList<>();

    public DigsiteType(JsonObject json)
    {
        this.digsiteTypeId = json.get(JsonConstants.digsiteTypeId).getAsString();

        JsonObject jsonBounds = json.getAsJsonObject(JsonConstants.bounds);
        JsonObject xRange = jsonBounds.getAsJsonObject(JsonConstants.xRange);
        JsonObject yRange = jsonBounds.getAsJsonObject(JsonConstants.yRange);
        JsonObject zRange = jsonBounds.getAsJsonObject(JsonConstants.zRange);

        this.xRange = new Range<>(xRange.get(JsonConstants.lowerBounds).getAsInt(),
                xRange.get(JsonConstants.upperBounds).getAsInt());
        this.yRange = new Range<>(yRange.get(JsonConstants.lowerBounds).getAsInt(),
                yRange.get(JsonConstants.upperBounds).getAsInt());
        this.zRange = new Range<>(zRange.get(JsonConstants.lowerBounds).getAsInt(),
                zRange.get(JsonConstants.upperBounds).getAsInt());

        JsonArray events = json.getAsJsonArray(JsonConstants.events);
        for(JsonElement eventJson : events.asList()){
            digsiteEvents.add(
                    DigsiteEventFactory.parseDigsiteEvent(eventJson.getAsJsonObject()));
        }
    }

    public String getDigsiteTypeId()
    {
        return digsiteTypeId;
    }

    public List<DigsiteEvent> getDigsiteEvents(){
        return digsiteEvents;
    }

    public Range<Integer> getXRange()
    {
        return xRange;
    }

    public Range<Integer> getYRange()
    {
        return yRange;
    }

    public Range<Integer> getZRange()
    {
        return zRange;
    }

    public Range<Integer> getXRange(Direction direction)
    {
        switch(direction){
            case SOUTH:
                return xRange.copy();
            case EAST:
                return new Range<>(zRange.Lower, zRange.Upper);
            case WEST:
                return new Range<>(zRange.Upper * -1, zRange.Lower * -1);
            case NORTH:
                return new Range<>(xRange.Upper * -1, xRange.Lower * -1);

        }
        // Default Rotation. Do Nothing
        return xRange;
    }

    public Range<Integer> getYRange(Direction direction)
    {
        switch(direction){
            case UP:
                break;
            case DOWN:
                break;

        }
        // Default Rotation. Do Nothing
        return yRange;
    }

    public Range<Integer> getZRange(Direction direction)
    {
        switch(direction){
            case SOUTH:
                return zRange.copy();
            case EAST:
                return new Range<>(xRange.Upper * -1, xRange.Lower * -1);
            case WEST:
                return new Range<>(xRange.Lower, xRange.Upper);
            case NORTH:
                return new Range<>(zRange.Upper * -1, zRange.Lower * -1);

        }
        // Default Rotation. Do Nothing
        return zRange;
    }
}
