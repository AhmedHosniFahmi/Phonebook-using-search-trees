
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    static NamesTrie trie = new NamesTrie();
    static PhoneBook book = null;
    public static void main(String[] args) {
        String input = null;
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("Welcome to my trees Program!");
        waitTime(2000);
        System.out.println("Here you can save all your contacts and search for them using our smart algorithms.");
        waitTime(3000);
        System.out.println("It can sort your contacts and you can even add 2 contacts with the same name. Isn't that cool?!");
        waitTime(3000);
        System.out.println("Deleting contacts is also an option. ;)");
        waitTime(2000);
        System.out.println("I hope that you enjoy my program. To exit the program type \"Exit\". To restart the program type \"Restart\"");
        waitTime(3000);
        System.out.println();

        long start, end;

        while (true) {
            printMainMenu();
            System.out.print("Enter your choice: ");
            input = sc.next();
            if (input.equalsIgnoreCase("exit")) break;
            else if (input.equalsIgnoreCase("restart")) {
                book = null;
                System.out.println();
                continue;
            }
            else if (!input.equals("0") && book == null) {
                System.out.println("Please enter a file first.\n");
                waitTime(1000);
                continue;
            }

            while (!input.equals("0") && !input.equals("1") && !input.equals("2") && !input.equals("3") &&
            !input.equals("4") && !input.equals("5") && !input.equals("6") && !input.equals("7"))
            {
                System.out.print("Input not valid, please enter your choice again: ");
                input = sc.next();
            }
            System.out.println();

            switch (input) {
                case "0": readCSV(); break;
                case "1": insert(); break;
                case "2": delete(); break;
                case "3": search(); break;
                case "4":
                    start = System.currentTimeMillis();
                    book.inOrder();
                    end = System.currentTimeMillis();
                    System.out.println("Time taken to print all contacts was " + (end - start) + " milliseconds");
                    break;
                case "5":
                    start = System.currentTimeMillis();
                    System.out.println("The height of the tree is " + book.height());
                    end = System.currentTimeMillis();
                    System.out.println("Time taken to get the height of the tree was " + (end - start) + " milliseconds");
                    break;
                case "6": System.out.println("The number of contacts is " + book.size()); break;
                case "7": book.drawTree(); break;
            }
            waitTime(500);
            System.out.println();
        }
        sc.close();
        System.out.println("I hope that you enjoyed my program, and thanks for the star. ;)");
    }

    public static void printMainMenu() {
        System.out.println("0- Enter a contacts' file path");
        System.out.println("1- Insert a new contact");
        System.out.println("2- Delete a contact");
        System.out.println("3- Search a contact");
        System.out.println("4- Print all contacts");
        System.out.println("5- Height of your chosen tree");
        System.out.println("6- Number of contacts");
        System.out.println("7- Draw the tree");
    }


    // we insert the contact info as String[] to both the trie and the phone book
    private static void insert(String[] contact) {
        trie.insert(contact);
        book.insert(contact);
    }

    // allows the user to insert a new contact by taking the contact's info and then inserting the info to the insert method above
    public static void insert() {
        String[] contact = new String[5];
        Scanner sc = new Scanner(System.in);

        System.out.print("Name: ");
        String name = sc.nextLine();
        while (!checkName(name)) {
            System.out.print("Input invalid, please enter first & last name: ");
            name = sc.nextLine();
        }
        String[] names = name.split(" ");
        contact[0] = names[0];
        contact[1] = names[1];
        if (contact[0].equalsIgnoreCase("engy")) System.out.println("Hello, professor Engy!");
        System.out.print("Phone number: ");
        contact[2] = sc.nextLine();
        System.out.print("Email: ");
        contact[3] = sc.nextLine();
        System.out.print("Address: ");
        contact[4] = sc.nextLine();

        long start = System.currentTimeMillis();
        trie.insert(contact);
        book.insert(contact);
        long end = System.currentTimeMillis();
        System.out.println("Contact inserted successfully!");
        System.out.println("Time taken to insert the contact was " + (end - start) + " milliseconds");
    }

    // asks the user to enter the name to delete, if the exists multiple contacts with the same name it asks the user to choose
    // between them
    public static void delete() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a name: ");
        String name = sc.nextLine();
        while (!checkName(name)) {
            System.out.print("Input invalid, please enter first & last name: ");
            name = sc.nextLine();
        }
        int n = book.size();

        long start = System.currentTimeMillis();
        trie.delete(name);
        book.delete(name);
        long end = System.currentTimeMillis();

        if (book.size() < n) {
            System.out.println("Contact deleted successfully!");
            System.out.println("Time taken to delete the contact was " + (end - start) + " milliseconds");
        }
        else System.out.println("No such contact.");
    }

    // searches for all the contacts that has the same name entered
    public static void search() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a name: ");
        String name = sc.nextLine();

        LinkedList<String> names = trie.search(name);
        // System.out.println("Names: " + names);
        if (names.size() > 0) System.out.println("Contacts found:");
        else System.out.println("No contacts found.");

        long start = System.currentTimeMillis();
        for (String i: names) {
            System.out.print(book.contactToString(book.search(i), false));
        }
        long end = System.currentTimeMillis();
        System.out.println("The time taken was " + (end - start) + " milliseconds");
    }

    // reads the csv file
    public static void readCSV() {
        if (book == null) chooseTree();
        Scanner for_path = new Scanner(System.in);
        System.out.print("Enter the file's path: ");
        String path = for_path.nextLine();

        try {
            Scanner sc = new Scanner(new FileReader(path));
            sc.nextLine();
            long start = System.currentTimeMillis();
            while (sc.hasNextLine()) {
                String[] contact = new String[5];
                String[] input = sc.nextLine().split(",");
                contact[0] = input[0].split(" ")[0];
                contact[1] = input[0].split(" ")[1];
                for (int i = 1; i < 4; i++) {contact[i+1] = input[i];}
                if (input.length > 4) for (int i = 4; i < input.length; i++) {contact[4] += "," + input[i];}
                insert(contact);
            }
            long end = System.currentTimeMillis();
            System.out.println("Time taken to insert the contacts from the CSV was " + (end - start) + " milliseconds");
        } catch (FileNotFoundException e) {
            System.out.println("The file you entered was not found. Please enter the correct file path.");
            waitTime(1000);
            readCSV();
        }
    }

    public static void chooseTree() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please choose a tree to import the contacts to.");
        System.out.println("1) 2-4 tree\n2) AVL tree\n3) Heap");
        System.out.print("Your choice: ");
        String choice = sc.next();
        
        while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) {
            System.out.print("Input not valid, please enter your choice again: ");
            choice = sc.next();
        }

        switch (choice) {
            case "1": book = new TwoFourPhoneBook(); break;
            case "2": book = new AVLPhoneBook(); break;
            case "3": book = new HeapPhoneBook(); break;
        }
    }

    public static boolean checkName(String name) {return (name.split(" ").length == 2)? true: false;}

    public static void waitTime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
