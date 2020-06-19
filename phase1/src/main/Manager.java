package main;

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
 *
 * @param <T> the entry must have an id to identify different entries
 */
public abstract class Manager<T extends DatabaseItem> implements Serializable {
    private String filePath;

    private static final Logger LOGGER = Logger.getLogger(Manager.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public Manager(String filePath) throws IOException {
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
     * @return the old item in the entry or the new item if the old item doesn't exist
     * @throws ClassNotFoundException if the file contains a class that is not found
     */
    public T update(T newItem) throws ClassNotFoundException {
        LinkedList<T> allItems;
        T oldItem = newItem;
        try {
            allItems = getItems();
            for (int i = 0; i < allItems.size(); i++) {
                T currItem = allItems.get(i);
                if (currItem.getId() == newItem.getId()) {
                    allItems.set(i, newItem);
                    oldItem = currItem;
                    break;
                } else if (i == allItems.size() - 1) allItems.set(i, newItem);
            }
            save(allItems);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Input could not be read.", ex);
        }
        return oldItem;

    }

    /**
     * Deletes an entry in the list of items if it exists
     *
     * @param id the entry id
     * @return the deleted item
     * @throws ClassNotFoundException if items in the list has an unknown class
     * @throws EntryNotFoundException if the entry id doesn't exist in the list
     */
    public T delete(int id) throws ClassNotFoundException, EntryNotFoundException {
        LinkedList<T> allItems;
        T oldItem;
        try {
            allItems = getItems();
            for (int i = 0; i < allItems.size(); i++) {
                T currItem = allItems.get(i);
                if (currItem.getId() == id) {
                    oldItem = currItem;
                    allItems.remove(i);
                    save(allItems);
                    return oldItem;
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Input could not be read.", ex);
        }
        throw new EntryNotFoundException("Could not delete item #" + id);

    }

    /**
     * Returns the object instance equivalent of the id given
     *
     * @param id the id of the object that is requested
     * @return the object instance of the id
     * @throws ClassNotFoundException if the list of items contains a class that is unknown
     * @throws EntryNotFoundException if the id given does not exist in the list of items
     */
    public T populate(int id) throws ClassNotFoundException, EntryNotFoundException {
        LinkedList<T> allItems;
        try {
            allItems = getItems();
            for (int i = 0; i < allItems.size(); i++) {
                T currItem = allItems.get(i);
                if (currItem.getId() == id)
                    return currItem;
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Input could not be read.", ex);
        }
        throw new EntryNotFoundException("Could not find item #" + id);
    }

    private LinkedList<T> getItems() throws ClassNotFoundException, IOException {
        InputStream buffer = new BufferedInputStream(new FileInputStream(this.filePath));
        ObjectInput input = new ObjectInputStream(buffer);
        LinkedList<T> items = (LinkedList<T>) input.readObject();
        input.close();
        return items;
    }

    private void save(LinkedList<T> items) throws IOException {
        OutputStream buffer = new BufferedOutputStream(new FileOutputStream(filePath));
        ObjectOutput output = new ObjectOutputStream(buffer);
        output.writeObject(items);
        output.close();
    }
}

