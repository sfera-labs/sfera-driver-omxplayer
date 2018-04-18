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
	 * @return
	 */
	private boolean isProcessRunning() {
		if (process == null) {
			return false;
		}
		return process.getProcess().isAlive();
	}

	/**
	 * Launches OMXPlayer to play the specified file and adding the specified
	 * options.
	 * 
	 * @param file
	 *            the file to play
	 * @param options
	 *            optional options
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public synchronized void play(String file, String... options) throws IOException {
		try {
			stop();
		} catch (Exception e) {
		}
		process = new OMXPlayerProcessHandler(this, file, this.options, options);
		process.start();
	}

	/**
	 * Pauses/resumes the current stream.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public synchronized void pauseResume() throws IOException {
		keyCommand("p");
	}

	/**
	 * Stops the playback.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs
	 * @throws InterruptedException
	 *             if interrupted while waiting for termination
	 */
	public synchronized void stop() throws IOException, InterruptedException {
		keyCommand("q");
		for (int i = 0; i < 20; i++) {
			if (isProcessRunning()) {
				Thread.sleep(50);
			} else {
				return;
			}
		}
		if (isProcessRunning()) {
			process.quit();
		}
	}

	/**
	 * Sends the specified command to the current playback. Run
	 * {@code omxplayer --keys} to see the list of available commands.
	 * 
	 * @param cmd
	 *            the command to send
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	public synchronized void keyCommand(String cmd) throws IOException {
		if (isProcessRunning()) {
			process.write(cmd);
		}
	}

}
