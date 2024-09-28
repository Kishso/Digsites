package kishso.digsites.digsite_events;

import com.google.gson.JsonObject;

public class ReplaceBlockDigsiteEvent implements DigsiteEvent{
    @Override
    public static DigsiteEvent fromJson(JsonObject jsonEvent) {
        return null;
    }

    @Override
    public boolean isConditionsMet() {
        return false;
    }

    @Override
    public void run() {

    }
}
