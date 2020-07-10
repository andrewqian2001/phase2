package main.Database;

import exceptions.EntryNotFoundException;

import java.io.*;
import java.util.LinkedList;
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
    private String filePath;

    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public Database(String filePath) throws IOException {
        LOGGER.setLevel(Level.ALL);
        CONSOLE_HANDLER.setLevel(Level.WARNING);
        LOGGER.addHandler(CONSOLE_HANDLER);
        this.filePath = filePath;
        File file = new File(filePath);
        if (!file.exists()) file.createNewFile();
    }

    /**
     * Updates the list with a new entry for the same id, if that entry exists
     *
     * @param newItem the item to replace to existing entry (if it exists)
     * @return the old item in the entry, if it doesn't exist then the new item is returned
     */
    public T update(T newItem) {
        LinkedList<T> allItems;
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
            LOGGER.log(Level.SEVERE, "Input could not be read. Failed to update.", e);
        }
        return oldItem;

    }

    /**
     * Deletes an entry in the list of items if it exists
     *
     * @param id the entry id
     * @return the deleted item
     * @throws EntryNotFoundException if the entry id doesn't exist in the list
     */
    public T delete(String id) throws EntryNotFoundException {
        LinkedList<T> allItems;
        T oldItem;
        try {
            allItems = getItems();
            for (int i = 0; i < allItems.size(); i++) {
                T currItem = allItems.get(i);
                if (currItem.getId().equals(id)) {
                    oldItem = currItem;
                    allItems.remove(i);
                    save(allItems);
                    return oldItem;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Input could not be read.", e);
        }
        throw new EntryNotFoundException("Could not delete item " + id);

    }

    /**
     * Returns the object instance equivalent of the id given
     *
     * @param id the id of the object that is requested
     * @return the object instance of the id
     * @throws EntryNotFoundException if the id given does not exist in the list of items
     */
    public T populate(String id) throws EntryNotFoundException {
        LinkedList<T> allItems;
        allItems = getItems();
        for (int i = 0; i < allItems.size(); i++) {
            T currItem = allItems.get(i);
            if (currItem.getId().equals(id))
                return currItem;
        }
        throw new EntryNotFoundException("Could not find item " + id);
    }

    /**
     * @return LinkedList containing all the items in the file
     */
    public LinkedList<T> getItems() {
        if (!new File(this.filePath).exists()) {
            LOGGER.log(Level.SEVERE, "The file " + filePath + " doesn't exist.");
            return new LinkedList<T>();
        }

        try {
            InputStream buffer = new BufferedInputStream(new FileInputStream(this.filePath));
            ObjectInput input = new ObjectInputStream(buffer);
            LinkedList<T> items = (LinkedList<T>) input.readObject();
            input.close();
            return items;
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Empty file was used.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Input could not be read.", e);
        }
        return new LinkedList<T>();

    }

    /**
     * Overwrites the file and saves something new to it
     *
     * @param items the items that are being saved to the file
     * @throws FileNotFoundException if the file doesn't exist
     */
    public void save(LinkedList<T> items) throws FileNotFoundException {
        if (!new File(this.filePath).exists()) {
            LOGGER.log(Level.SEVERE, "The file " + filePath + " doesn't exist.");
            throw new FileNotFoundException();
        }
        try {
            OutputStream buffer = new BufferedOutputStream(new FileOutputStream(filePath));
            ObjectOutput output = new ObjectOutputStream(buffer);
            output.writeObject(items);
            output.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Unable to save.", e);
        }
    }
}

