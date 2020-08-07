package backend.tradesystem;

import backend.DatabaseFilePaths;

import java.io.File;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * For detecting if any files in the database got changed
 * Code partially taken from
 * https://stackoverflow.com/questions/54815226/how-can-i-detect-if-a-file-has-been-modified-using-lastmodified
 */
public abstract class DetectDatabaseChange extends TimerTask {
    private final ArrayList<Long> times  = new ArrayList<>();
    private final ArrayList<File> file = new ArrayList<>();

    /**
     * For making an instance that detects if any files in the database got changed
     */
    public DetectDatabaseChange() {
        int i = 0;
        for (DatabaseFilePaths path : DatabaseFilePaths.values()) {
            if (!path.isConfig()) return;
            file.add(new File(path.getFilePath()));
            times.add(file.get(i).lastModified());
            i++;
        }
    }

    /**
     * Checking to see if any files got changed
     */
    @Override
    public final void run() {
        for (int i = 0; i < times.size(); i++) {
            if (times.get(i) != file.get(i).lastModified()) {
                times.set(i, file.get(i).lastModified());
                onChange();
            }
        }
    }

    /**
     * If any files got changed
     */
    protected abstract void onChange();
}