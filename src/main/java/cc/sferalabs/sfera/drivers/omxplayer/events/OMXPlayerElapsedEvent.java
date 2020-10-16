package cc.sferalabs.sfera.drivers.omxplayer.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.NumberEvent;

public class OMXPlayerElapsedEvent extends NumberEvent implements OMXPlayerEvent {

	public OMXPlayerElapsedEvent(Node source, Number value) {
		super(source, "elapsed", value);
	}

}
