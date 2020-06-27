package main;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        TextInterface tInterface = new TextInterface();
        try {
            tInterface.run();
        } catch(IOException | ClassNotFoundException e) {
            System.out.println("Ruh Roh!");
        }
    }
}
