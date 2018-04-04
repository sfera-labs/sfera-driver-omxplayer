package cc.sferalabs.sfera.drivers.omxplayer.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.NumberEvent;

public class OMXPlayerDurationEvent extends NumberEvent implements OMXPlayerEvent {

	public OMXPlayerDurationEvent(Node source, Number value) {
		super(source, "duration", value);
	}

}
