package cc.sferalabs.sfera.drivers.omxplayer;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.sferalabs.sfera.drivers.omxplayer.events.OMXPlayerDurationEvent;
import cc.sferalabs.sfera.drivers.omxplayer.events.OMXPlayerPlayEvent;
import cc.sferalabs.sfera.events.Bus;
import cc.sferalabs.sfera.util.files.FilesUtil;
import cc.sferalabs.sfera.util.os.ProcessHandler;
import cc.sferalabs.sfera.util.os.ProcessListener;

public class OMXPlayerProcessHandler extends ProcessHandler implements ProcessListener {

	private static final Logger log = LoggerFactory.getLogger(OMXPlayerProcessHandler.class);

	private final OMXPlayer omx;
	private final String file;
	private boolean firstOutput = true;

	public OMXPlayerProcessHandler(OMXPlayer omx, String file, String[] options1, String[] options2) {
		super("OMXPlayer", getCommad(options1, options2, file));
		this.omx = omx;
		this.file = file;
		setListener(this);
	}

	/**
	 * @param options1
	 * @param options2
	 * @param file
	 * @return
	 */
	private static String[] getCommad(String[] options1, String[] options2, String file) {
		int len1 = 0;
		int len2 = 0;
		if (options1 != null) {
			len1 += options1.length;
		}
		if (options2 != null) {
			len2 += options2.length;
		}
		String[] cmd = new String[3 + len1 + len2];
		cmd[0] = "omxplayer";
		cmd[1] = "--with-info";
		if (options1 != null) {
			System.arraycopy(options1, 0, cmd, 2, options1.length);
		}
		if (options2 != null) {
			System.arraycopy(options2, 0, cmd, 2 + len1, options2.length);
		}
		Path p = FilesUtil.resolveAgainstRoot(file);
		if (!FilesUtil.isInRoot(p)) {
			throw new IllegalArgumentException("file outside root");
		}
		cmd[cmd.length - 1] = p.toString();
		return cmd;
	}

	@Override
	public void onStarted() {
		this.firstOutput = true;
	}

	@Override
	public void onOutputLine(String line) {
		log.debug("OUT: {}", line);
	}

	@Override
	public void onErrorOutputLine(String line) {
		log.debug("ERR: {}", line);
		if (line != null) {
			if (firstOutput) {
				firstOutput = false;
				Bus.post(new OMXPlayerPlayEvent(omx, file));
			}
			line = line.trim();
			if (line.startsWith("Duration:")) {
				int end = line.indexOf(',');
				String dur = line.substring(9, end).trim();
				String[] hh_mm_ss_dd = dur.split("[:\\.]");
				long h = Long.parseLong(hh_mm_ss_dd[0]);
				long m = Long.parseLong(hh_mm_ss_dd[1]);
				long s = Long.parseLong(hh_mm_ss_dd[2]);
				long millis;
				if (hh_mm_ss_dd.length > 3) {
					String d = hh_mm_ss_dd[3] + "000";
					d = d.substring(0, 3);
					millis = Long.parseLong(d);
				} else {
					millis = 0;
				}
				millis += (s * 1000) + (m * 60 * 1000) + (h * 60 * 60 * 1000);
				Bus.post(new OMXPlayerDurationEvent(omx, millis));
			}
		}
	}

	@Override
	public void onReadError(Throwable e) {
		log.warn("onReadError", e);
	}

	@Override
	public void onTerminated() {
		Bus.post(new OMXPlayerPlayEvent(omx, null));
		Bus.post(new OMXPlayerDurationEvent(omx, null));
	}

}
