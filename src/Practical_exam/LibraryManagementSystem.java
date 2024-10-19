package Practical_exam;

import java.io.*;
import java.util.HashMap;

class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}

class Book implements Serializable {
    private String title;
    private String author;
    private String ISBN;
    private boolean available;
    private static int totalBooks = 0;

    public Book(String title, String author, String ISBN) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.available = true;
        totalBooks++;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getISBN() { return ISBN; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public static int getTotalBooks() { return totalBooks; }

    public void displayDetails() {
        System.out.println("Title: " + title + ", Author: " + author + ", ISBN: " + ISBN + ", Available: " + available);
    }
}

class Ebook extends Book {
    private String fileFormat;
    private double fileSize;

    public Ebook(String title, String author, String ISBN, String fileFormat, double fileSize) {
        super(title, author, ISBN);
        this.fileFormat = fileFormat;
        this.fileSize = fileSize;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("File Format: " + fileFormat + ", File Size: " + fileSize + " MB");
    }
}

class PrintedBook extends Book {
    private int numberOfPages;
    private String bindingType;

    public PrintedBook(String title, String author, String ISBN, int numberOfPages, String bindingType) {
        super(title, author, ISBN);
        this.numberOfPages = numberOfPages;
        this.bindingType = bindingType;
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Number of Pages: " + numberOfPages + ", Binding Type: " + bindingType);
    }
}

class Library {
    protected HashMap<String, Book> bookCollection = new HashMap<>();
    private String filePath;

    public Library(String filePath) {
        this.filePath = filePath;
        readFromFile();
    }

    public synchronized void addBook(Book book) {
        bookCollection.put(book.getISBN(), book);
        saveAllBooks();
    }

    public synchronized void updateBook(String ISBN, Book newBook) throws BookNotFoundException {
        if (!bookCollection.containsKey(ISBN)) {
            throw new BookNotFoundException("Book not found!");
        }
        bookCollection.put(ISBN, newBook);
        saveAllBooks();
    }

    public synchronized Book searchBook(String ISBN) throws BookNotFoundException {
        if (!bookCollection.containsKey(ISBN)) {
            throw new BookNotFoundException("Book not found!");
        }
        return bookCollection.get(ISBN);
    }

    public synchronized void deleteBook(String ISBN) throws BookNotFoundException {
        if (!bookCollection.containsKey(ISBN)) {
            throw new BookNotFoundException("Book not found!");
        }
        bookCollection.remove(ISBN);
        saveAllBooks();
    }

    public void displayAllBooks() {
        for (Book book : bookCollection.values()) {
            book.displayDetails();
        }
    }

    private void saveAllBooks() {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            for (Book book : bookCollection.values()) {
                oos.writeObject(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromFile() {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    Book book = (Book) ois.readObject();
                    bookCollection.put(book.getISBN(), book);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class StockUpdater implements Runnable {
    private Library library;

    public StockUpdater(Library library) {
        this.library = library;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(5000);
                for (Book book : library.bookCollection.values()) {
                    book.setAvailable(!book.isAvailable());
                }
                System.out.println("Stock updated.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class AvailabilityChecker implements Runnable {
    private Library library;

    public AvailabilityChecker(Library library) {
        this.library = library;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(10000);
                library.displayAllBooks();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

public class LibraryManagementSystem {
    public static void main(String[] args) {
        String filePath = "books.txt";
        if (args.length > 0) {
            filePath = args[0];
        }

        Library library = new Library(filePath);

        Thread stockUpdaterThread = new Thread(new StockUpdater(library));
        Thread availabilityCheckerThread = new Thread(new AvailabilityChecker(library));

        stockUpdaterThread.start();
        availabilityCheckerThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            stockUpdaterThread.interrupt();
            availabilityCheckerThread.interrupt();
        }));

        library.addBook(new PrintedBook("1984", "George Orwell", "123456789", 328, "Hardcover"));
        library.addBook(new Ebook("Digital Fortress", "Dan Brown", "987654321", "PDF", 1.5));
    }
}
