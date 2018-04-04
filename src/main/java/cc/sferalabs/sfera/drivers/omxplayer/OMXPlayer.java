package cc.sferalabs.sfera.drivers.omxplayer;

import java.io.IOException;

import cc.sferalabs.sfera.core.Configuration;
import cc.sferalabs.sfera.drivers.Driver;

public class OMXPlayer extends Driver {

	private OMXPlayerProcessHandler process;
	private String[] options;

	public OMXPlayer(String id) {
		super(id);
	}

	@Override
	protected boolean onInit(Configuration config) throws InterruptedException {
		String opts = config.get("options", null);
		if (opts != null) {
			options = opts.split("\\s+");
		}

		return true;
	}

	@Override
	protected boolean loop() throws InterruptedException {
		Thread.sleep(500000);
		return true;
	}

	@Override
	protected void onQuit() {
		try {
			stop();
		} catch (Exception e) {
		}
	}

	/**
	 * @param file
	 * @param options
	 * @throws IOException
	 */
	public void play(String file, String... options) throws IOException {
		try {
			stop();
		} catch (Exception e) {
		}
		process = new OMXPlayerProcessHandler(this, file, this.options, options);
		process.start();
	}

	/**
	 * @throws IOException
	 */
	public void pause() throws IOException {
		if (process != null) {
			process.write("p");
		}
	}

	/**
	 * @throws IOException
	 */
	public void stop() throws IOException {
		if (process != null) {
			process.write("q");
			process.quit();
		}
	}

	/**
	 * @param cmd
	 * @throws IOException
	 */
	public void keyCommand(String cmd) throws IOException {
		if (process != null) {
			process.write(cmd);
		}
	}

}
