package kishso.digsites.digsite_events;

import com.google.gson.JsonObject;
import kishso.digsites.Digsite;

public abstract class DigsiteEvent {

    public static class JsonConstants {
        final static String eventName = "event_name";
        final static String tickFrequency = "tick_frequency";

        final static String eventsDetails = "event_details";
    }

    private final int tickFrequency;
    private final int tickOffset;
    private final String eventName;

    DigsiteEvent (JsonObject jsonEvent){
        this.eventName = jsonEvent.get(JsonConstants.eventName).getAsString();
        this.tickFrequency = jsonEvent.get(JsonConstants.tickFrequency).getAsInt();

        this.tickOffset = (int)(Math.random() * tickFrequency);

    }

    public abstract boolean isConditionsMet(Digsite currentDigsite);

    public abstract void run(Digsite currentDigsite);

    public String getEventName(){
        return eventName;
    }

    public boolean checkTick(long currentTick){
        return (currentTick % tickFrequency) == tickOffset;
    }
}
