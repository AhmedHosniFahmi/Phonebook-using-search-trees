
import java.util.Scanner;

public abstract class PhoneBook {

    abstract public void insert(String[] element);
    abstract public void delete(String name);
    abstract public String[] search(String name);
    abstract public void inOrder();
    abstract public int height();
    abstract public int size();
    abstract public void drawTree();

    public String contactToString(String[] contact, boolean numbered) {
        int number_of_contacts = (contact.length - 2) / 3;
        String to_return = "";

        for (int i = 0; i < number_of_contacts; i++) {
            String[] c = new String[5];
            c[0] = contact[0];
            c[1] = contact[1];
            for (int j = 2; j < 5; j++) {
                c[j] = contact[j + (3 * i)];
            }
            if (numbered) to_return += i + "- ";
            to_return += String.format("Name: %s, Phone Number: %s, Email: %s, Address: %s\n", c[0] + " " + c[1], c[2], c[3], c[4]);
        }
        return to_return;
    }

    protected int compareNames(String[] name1, String[] name2) {
        /*
        * Returns -1 if name1 alphabatically precedes name2, 0 if name1 == name2, 1 if name2 alphabatically precedes name1
        */
        if (name1[0].equalsIgnoreCase(name2[0]) && name1[1].equalsIgnoreCase(name2[1])) return 0;
        else {
            if (name1[0].equalsIgnoreCase(name2[0])) {
                int end = (name1[1].length() < name2[1].length())? name1[1].length(): name2[1].length();
                for (int i = 0; i < end; i++) {
                    if (name1[1].charAt(i) < name2[1].charAt(i)) return -1;
                    else if (name1[1].charAt(i) > name2[1].charAt(i)) return 1;
                }
                return (name1[1].length() < name2[1].length())? -1: 1;
            } else {
                int end = (name1[0].length() < name2[0].length())? name1[0].length(): name2[0].length();
                for (int i = 0; i < end; i++) {
                    if (name1[0].charAt(i) < name2[0].charAt(i)) return -1;
                    else if (name1[0].charAt(i) > name2[0].charAt(i)) return 1;
                }
                return (name1[0].length() < name2[0].length())? -1: 1;
            }
        }
    }

    protected String[] upgradeContact(String[] contact, String[] new_contact) {
        /*
        * Use when a user inserts a contact with a name that already exists
        */

        // Check that this contact doesn't exist
        boolean is_same_contact = false;
        int number_of_contacts = (contact.length - 2) / 3;
        for (int i = 0; i < number_of_contacts; i++) {
            is_same_contact = true;
            for (int j = 2; j < 5; j++) {
                if (!new_contact[j].equals(contact[j + (3 * i)])) is_same_contact = false;
            }
            if (is_same_contact) return contact;
        }

        String[] upgrade = new String[contact.length + 3];
        int index = 2;
        for (int j = 0; j < contact.length; j++) {
            upgrade[j] = contact[j];
        }
        for (int j = contact.length; j < contact.length + 3; j++) {
            upgrade[j] = new_contact[index];
            index++;
        }
        return upgrade;
    }

    protected String[] choose(String[] contact) {
        /*
        * Use this method if a user wanted to delete a contact with a repeated name to get the new contact,
        * so you can use it to replace the old one using ->  node.getValue().set(index, choose(contact));
        * Input: the contact containing the name
        */
        
        System.out.println("There are more than one contact with this name.");
        System.out.println(contactToString(contact, true));
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please choose a contact: ");
        int ans = scanner.nextInt();

        String[] new_contact = new String[contact.length - 3];
        int index = 0;
        for (int j = 0; index < contact.length; j++) {
            if (index != 2 + (3*ans) && index != 3 + (3*ans) && index != 4 + (3*ans)) new_contact[j] = contact[index];
            else j--;
            index++;
        }
        return new_contact;
    }
}
