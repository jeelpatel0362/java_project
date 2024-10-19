package Practical_exam;

import java.io.*;
import java.util.*;

class Product {
    protected String productID;
    protected String productName;
    protected double price;

    public Product(String productID, String productName, double price) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
    }

    public double calculateDiscount() {
        return 0; // Default discount
    }

    public void displayDetails() {
        System.out.printf("Product [ID: %s, Name: %s, Price: %.2f]%n", productID, productName, price);
    }
}

class Electronics extends Product {
    public Electronics(String productID, String productName, double price) {
        super(productID, productName, price);
    }

    @Override
    public double calculateDiscount() {
        return price * 0.10; // 10% discount
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.printf("Discount: %.2f%n", calculateDiscount());
    }
}

class Clothing extends Product {
    public Clothing(String productID, String productName, double price) {
        super(productID, productName, price);
    }

    @Override
    public double calculateDiscount() {
        return price * 0.05; // 5% discount
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.printf("Discount: %.2f%n", calculateDiscount());
    }
}

public class InventoryManagementSystem {
    private static Map<String, Product> inventory = new HashMap<>();

    public static void main(String[] args) {
        loadInventoryFromFile("inventory.txt");

        // Adding products
        addProduct(new Electronics("E001", "Laptop", 1200.00));
        addProduct(new Clothing("C001", "T-Shirt", 25.00));

        // Display all products
        displayInventory();

        // Save inventory
        saveInventoryToFile("inventory.txt");
    }

    public static void addProduct(Product product) {
        inventory.put(product.productID, product);
        System.out.println("Added: " + product.productName);
    }

    public static void displayInventory() {
        System.out.println("Current Inventory:");
        for (Product product : inventory.values()) {
            product.displayDetails();
        }
    }

    public static void saveInventoryToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(inventory);
        } catch (IOException e) {
            System.out.println("Error saving inventory: " + e.getMessage());
        }
    }

    public static void loadInventoryFromFile(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            inventory = (Map<String, Product>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading inventory: " + e.getMessage());
        }
    }
}
