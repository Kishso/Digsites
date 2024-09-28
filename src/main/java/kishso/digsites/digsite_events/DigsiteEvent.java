package kishso.digsites.digsite_events;

import com.google.gson.JsonObject;

public interface DigsiteEvent {

    static DigsiteEvent fromJson(JsonObject jsonEvent){
        return null;
    }

    boolean isConditionsMet();

    void run();
}
