package kishso.digsites.digsite_events;

import com.google.gson.JsonObject;

public class DigsiteEventFactory {

    public static DigsiteEvent parseDigsiteEvent(JsonObject jsonEvent){
        if(jsonEvent.has("event_name"))
        switch(jsonEvent.get("event_name").getAsString()){
            case "replace_block_event":
                return ReplaceBlockDigsiteEvent.fromJson(jsonEvent);
            default:
                break;
        }
        return null;
    }
}
