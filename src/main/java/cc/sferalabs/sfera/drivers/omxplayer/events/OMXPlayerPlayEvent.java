package cc.sferalabs.sfera.drivers.omxplayer.events;

import cc.sferalabs.sfera.events.Node;
import cc.sferalabs.sfera.events.StringEvent;

public class OMXPlayerPlayEvent extends StringEvent implements OMXPlayerEvent {

	public OMXPlayerPlayEvent(Node source, String value) {
		super(source, "play", value);
	}
	
}
