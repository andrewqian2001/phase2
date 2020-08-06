package backend.tradesystem;

import backend.DatabaseFilePaths;

import java.io.File;
import java.util.TimerTask;

/**
 * For detecting if any files in the database got changed
 * Code partially taken from
 * https://stackoverflow.com/questions/54815226/how-can-i-detect-if-a-file-has-been-modified-using-lastmodified
 */
public abstract class DetectDatabaseChange extends TimerTask {
    private final long[] times;
    private final File[] file;

    /**
     * For making an instance that detects if any files in the database got changed
     */
    public DetectDatabaseChange() {
        File[] file = new File[DatabaseFilePaths.values().length];
        long[] times = new long[DatabaseFilePaths.values().length];
        int i = 0;
        for (DatabaseFilePaths path : DatabaseFilePaths.values()) {
            file[i] = new File(path.getFilePath());
            times[i] = file[i].lastModified();
            i++;
        }
        this.file = file;
        this.times = times;
    }

    /**
     * Checking to see if any files got changed
     */
    @Override
    public final void run() {
        for (int i = 0; i < DatabaseFilePaths.values().length; i++) {
            if (times[i] != file[i].lastModified()) {
                times[i] = file[i].lastModified();
                onChange();
            }
        }
    }

    /**
     * If any files got changed
     */
    protected abstract void onChange();
}