package me.purox.devi.commands.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class NonblockingBufferedReader {

    private BlockingQueue<String> lines = new LinkedBlockingQueue<>();
    private volatile boolean closed = false;
    private Thread backgroundReaderThread ;

    NonblockingBufferedReader(final BufferedReader bufferedReader) {
        backgroundReaderThread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    String line = bufferedReader.readLine();
                    if (line == null) break;
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closed = true;
            }
        });
        backgroundReaderThread.setDaemon(true);
        backgroundReaderThread.start();
    }

    String readLine() throws IOException {
        try {
            return closed && lines.isEmpty() ? null : lines.poll(500L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new IOException("The BackgroundReaderThread was interrupted!", e);
        }
    }

    public void close() {
        if (backgroundReaderThread != null) {
            backgroundReaderThread.interrupt();
            backgroundReaderThread = null;
        }
    }
}
