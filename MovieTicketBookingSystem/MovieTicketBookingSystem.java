import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class MovieTicketBookingSystem {
    private static final String DATA_FILE = "TheatreData.json";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Movie Ticket Booking System!");
        loadData();

        while (true) {
            System.out.println("1. Register\n2. Login\n3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> {
                    saveData();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static JSONObject data = new JSONObject();

    private static void loadData() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
            data = new JSONObject(content);
        } catch (IOException e) {
            System.out.println("Data file not found. Creating new data.");
            data.put("customers", new JSONArray());
            data.put("employees", new JSONArray());
            data.put("movies", new JSONArray());
        }
    }

    private static void saveData() {
        try (FileWriter file = new FileWriter(DATA_FILE)) {
            file.write(data.toString(4));
            file.flush();
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }

    private static void register() {
        System.out.println("Register as:\n1. Customer\n2. Employee");
        int roleChoice = scanner.nextInt();
        scanner.nextLine();  

        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        JSONObject newUser = new JSONObject();
        newUser.put("name", name);
        newUser.put("username", username);
        newUser.put("password", password);

        if (roleChoice == 1) {
            newUser.put("bookedMovies", new JSONArray());
            data.getJSONArray("customers").put(newUser);
            System.out.println("Customer registered successfully.");
        } else if (roleChoice == 2) {
            data.getJSONArray("employees").put(newUser);
            System.out.println("Employee registered successfully.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (Object customerObj : data.getJSONArray("customers")) {
            JSONObject customer = (JSONObject) customerObj;
            if (customer.getString("username").equals(username) && customer.getString("password").equals(password)) {
                customerMenu(customer);
                return;
            }
        }

        for (Object employeeObj : data.getJSONArray("employees")) {
            JSONObject employee = (JSONObject) employeeObj;
            if (employee.getString("username").equals(username) && employee.getString("password").equals(password)) {
                employeeMenu();
                return;
            }
        }

        System.out.println("Invalid login credentials.");
    }

    private static void customerMenu(JSONObject customer) {
        while (true) {
            System.out.println("Customer Menu:\n1. Book Movie\n2. View Bookings\n3. Logout");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> bookMovie(customer);
                case 2 -> viewBookings(customer);
                case 3 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void bookMovie(JSONObject customer) {
        JSONArray movies = data.getJSONArray("movies");
        if (movies.isEmpty()) {
            System.out.println("No movies available to book.");
            return;
        }

        System.out.println("Available Movies:");
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            System.out.println((i + 1) + ". Title: " + movie.getString("title") +
                    ", Genre: " + movie.getString("genre") +
                    ", Timing: " + movie.getString("timing"));
        }

        System.out.print("Enter the number of the movie you want to book: ");
        int movieChoice = scanner.nextInt();
        scanner.nextLine();

        if (movieChoice > 0 && movieChoice <= movies.length()) {
            JSONObject selectedMovie = movies.getJSONObject(movieChoice - 1);
            customer.getJSONArray("bookedMovies").put(selectedMovie);
            System.out.println("Movie booked successfully.");
        } else {
            System.out.println("Invalid movie choice.");
        }
    }

    private static void viewBookings(JSONObject customer) {
        JSONArray bookings = customer.getJSONArray("bookedMovies");
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("Your Bookings:");
        for (Object booking : bookings) {
            JSONObject movie = (JSONObject) booking;
            System.out.println("Title: " + movie.getString("title") +
                    ", Genre: " + movie.getString("genre") +
                    ", Timing: " + movie.getString("timing"));
        }
    }

    private static void employeeMenu() {
        while (true) {
            System.out.println("Employee Menu:\n1. Add Movie\n2. Delete Movie\n3. Logout");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1 -> addMovie();
                case 2 -> deleteMovie();
                case 3 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addMovie() {
        System.out.print("Enter movie title: ");
        String title = scanner.nextLine();
        System.out.print("Enter movie genre: ");
        String genre = scanner.nextLine();
        System.out.print("Enter movie timing: ");
        String timing = scanner.nextLine();

        JSONObject movie = new JSONObject();
        movie.put("title", title);
        movie.put("genre", genre);
        movie.put("timing", timing);

        data.getJSONArray("movies").put(movie);
        System.out.println("Movie added successfully.");
    }

    private static void deleteMovie() {
        JSONArray movies = data.getJSONArray("movies");
        if (movies.isEmpty()) {
            System.out.println("No movies available to delete.");
            return;
        }

        System.out.println("Available Movies:");
        for (int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);
            System.out.println((i + 1) + ". Title: " + movie.getString("title") +
                    ", Genre: " + movie.getString("genre") +
                    ", Timing: " + movie.getString("timing"));
        }

        System.out.print("Enter the number of the movie you want to delete: ");
        int movieChoice = scanner.nextInt();
        scanner.nextLine(); 

        if (movieChoice > 0 && movieChoice <= movies.length()) {
            movies.remove(movieChoice - 1);
            System.out.println("Movie deleted successfully.");
        } else {
            System.out.println("Invalid movie choice.");
        }
    }
}
