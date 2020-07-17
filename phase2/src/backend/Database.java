package backend;


import backend.exceptions.EntryNotFoundException;
import backend.models.DatabaseItem;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to store a list of items with methods provided to update entries of that list.
 * The list of items is stored using a Linked List in .ser files. Any object being stored must be serializable.
 * Code is partially taken from logging.zip, StudentManager.java from week 6 slides and codes
 *
 * @param <T> the entry must have an id to identify different entries
 */
public class Database<T extends DatabaseItem> implements Serializable {
    private final String filePath;

    private static final Logger logger = Logger.getLogger(Database.class.getName());
    private static final Handler consoleHandler = new ConsoleHandler();

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public Database(String filePath) throws IOException {
        logger.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.WARNING);
        logger.addHandler(consoleHandler);
        this.filePath = filePath;
        new File(filePath).createNewFile();
    }

    /**
     * Updates the list with a new entry for the same id, if that entry exists
     *
     * @param newItem the item to replace to existing entry (if it exists)
     * @return the old item in the entry, if it doesn't exist then the new item is returned
     */
    public T update(T newItem) {
        ArrayList<T> allItems;
        T oldItem = newItem;
        try {
            allItems = getItems();
            if (allItems.size() == 0) {
                allItems.add(newItem);
                save(allItems);
                return oldItem;
            }
            for (int i = 0; i < allItems.size(); i++) {
                T currItem = allItems.get(i);
                if (currItem.getId().equals(newItem.getId())) {
                    allItems.set(i, newItem);
                    oldItem = currItem;
                    break;
                } else if (i == allItems.size() - 1) allItems.add(newItem);
            }
            save(allItems);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Input could not be read. Failed to update.", e);
        }
        return oldItem;

    }

    /**
     * Deletes an entry in the list of items if it exists
     *
     * @param id the entry id
     */
    public void delete(String id) {
        ArrayList<T> allItems;
        allItems = getItems();
        for (T item: allItems){
            if (item.getId().equals(id)) {
                allItems.remove(item);
                return;
            }
        }
    }

    /**
     * Returns the object instance equivalent of the id given
     *
     * @param id the id of the object that is requested
     * @return the object instance of the id
     * @throws EntryNotFoundException if the id given does not exist in the list of items
     */
    public T populate(String id) throws EntryNotFoundException {
        ArrayList<T> allItems = getItems();
        for (T item: allItems) {
            if (item.getId().equals(id))
                return item;
        }
        throw new EntryNotFoundException("Could not find item " + id);
    }

    /**
     * whether the database contains the id
     *
     * @param id the id being checked
     * @return whether the database contains the id
     */
    public boolean contains(String id) {
        try {
            populate(id);
        } catch (EntryNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * list of items in the database file
     *
     * @return all the items in the file
     */
    public ArrayList<T> getItems() {
        if (!new File(this.filePath).exists()) {
            logger.log(Level.SEVERE, "The file " + filePath + " doesn't exist.");
            return new ArrayList<T>();
        }
        try {
            InputStream buffer = new BufferedInputStream(new FileInputStream(this.filePath));
            ObjectInput input = new ObjectInputStream(buffer);
            Object tmp = input.readObject();
            input.close();
            return (ArrayList<T>) tmp;
        } catch (IOException e) {
            logger.log(Level.INFO, "Empty file was used.");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Input could not be read.", e);
        }
        return new ArrayList<T>();

    }

    /**
     * Overwrites the file and saves something new to it
     *
     * @param items the items that are being saved to the file
     * @throws FileNotFoundException if the file doesn't exist
     */
    public void save(ArrayList<T> items) throws FileNotFoundException {
        if (!new File(this.filePath).exists()) {
            logger.log(Level.SEVERE, "The file " + filePath + " doesn't exist.");
            throw new FileNotFoundException();
        }
        try {
            OutputStream buffer = new BufferedOutputStream(new FileOutputStream(filePath));
            ObjectOutput output = new ObjectOutputStream(buffer);
            output.writeObject(items);
            output.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to save.", e);
        }
    }
}

