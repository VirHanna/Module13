import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonPlaceholderClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final Logger LOGGER = Logger.getLogger(JsonPlaceholderClient.class.getName());

    private static final int NEW_USER_ID = 11;

    public static void main(String[] args) {

        String newUserJson = "{\n" +
                "    \"name\": \"Kate\",\n" +
                "    \"username\": \"Grandson\",\n" +
                "    \"email\": \"kategrandson@example.com\",\n" +
                "    \"address\": {\n" +
                "      \"street\": \"Apple Street\",\n" +
                "      \"suite\": \"Apt. 123\",\n" +
                "      \"city\": \"Los Angeles\",\n" +
                "      \"zipcode\": \"10501\",\n" +
                "      \"geo\": {\n" +
                "        \"lat\": \"24.8423\",\n" +
                "        \"lng\": \"-68.4060\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"phone\": \"1-354-123-4567\",\n" +
                "    \"website\": \"grandson.com\",\n" +
                "    \"company\": {\n" +
                "      \"name\": \"Grandson Enterprises\",\n" +
                "      \"catchPhrase\": \"Innovating the future\",\n" +
                "      \"bs\": \"synergy scalable e-commerce solutions\"\n" +
                "    }\n" +
                "  }";

        System.out.println("Creating new user:");
        String response = createUser(newUserJson);
        System.out.println(response);

        String updatedUserJson = "{\n" +
                "    \"id\": 11,\n" +
                "    \"name\": \"Kate\",\n" +
                "    \"username\": \"Grandson\",\n" +
                "    \"email\": \"kategrandsom@example.com\",\n" +
                "    \"address\": {\n" +
                "      \"street\": \"Apple Street\",\n" +
                "      \"suite\": \"Apt. 123\",\n" +
                "      \"city\": \"Los Angeles\",\n" +
                "      \"zipcode\": \"10501\",\n" +
                "      \"geo\": {\n" +
                "        \"lat\": \"24.8423\",\n" +
                "        \"lng\": \"-68.4060\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"phone\": \"1-354-123-4567\",\n" +
                "    \"website\": \"grandson.com\",\n" +
                "    \"company\": {\n" +
                "      \"name\": \"Grandson Enterprises\",\n" +
                "      \"catchPhrase\": \"Innovating the future\",\n" +
                "      \"bs\": \"synergy scalable e-commerce solutions\"\n" +
                "    }\n" +
                "  }";

        System.out.println("Updating user:");
        response = updateUser(NEW_USER_ID, updatedUserJson);
        System.out.println(response);

        System.out.println("Deleting user:");
        deleteUser(NEW_USER_ID);

        System.out.println("Getting all users:");
        System.out.println(getAllUsers());

        System.out.println("Getting user by ID:");
        System.out.println(getUserById(1));

        System.out.println("Getting user by username:");
        System.out.println(getUserByUsername("Antonette"));

        System.out.println("Getting all posts of user with ID 1:");
        getUserPostsAndCommentsAndSaveToFile(1);

        System.out.println("Getting open tasks for user with ID 1:");
        System.out.println(getOpenTasksForUser(1));
    }

    public static String getOpenTasksForUser(int userId) {
        try {
            URL url = new URL(BASE_URL + "/todos?userId=" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String response = readResponse(connection);
                // Фільтрація задач, де completed == false
                StringBuilder openTasks = new StringBuilder("Open tasks for user with ID " + userId + ":\n");
                // Витягуємо лише задачі, у яких completed == false
                String[] tasks = response.split("},");
                for (String task : tasks) {
                    if (task.contains("\"completed\": false")) {
                        openTasks.append(task.replace("},", "")).append("\n");
                    }
                }
                return openTasks.toString();
            } else {
                return "Error getting tasks for user: " + responseCode;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting open tasks for user", e);
            return "Error getting open tasks for user.";
        }
    }

    public static String createUser(String jsonInputString) {
        try {
            URL url = new URL(BASE_URL + "/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {

                String response = readResponse(connection);

                if (response.contains("\"id\": " + NEW_USER_ID)) {
                    return "User created successfully with ID: " + NEW_USER_ID;
                } else {
                    return "Error creating user: Unexpected ID in response.";
                }
            } else {
                return "Error creating user: " + responseCode;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
            return "Error creating user.";
        }
    }

    public static String updateUser(int userId, String jsonInputString) {
        try {
            URL url = new URL(BASE_URL + "/users/" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(jsonInputString.getBytes(StandardCharsets.UTF_8));

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                String updatedJson = readResponse(connection);

                return "User with ID " + userId + " updated successfully. Updated JSON: " + updatedJson;
            } else {
                return "Error updating user: " + responseCode;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            return "Error updating user.";
        }
    }

    public static void deleteUser(int userId) {
        try {
            URL url = new URL(BASE_URL + "/users/" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("User deleted successfully. Response code: " + responseCode);
            } else {
                System.out.println("Error deleting user: " + responseCode);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            System.out.println("Error deleting user.");
        }
    }

    public static String getAllUsers() {
        try {
            URL url = new URL(BASE_URL + "/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                return "Error getting all users: " + responseCode;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting all users", e);
            return "Error getting all users.";
        }
    }

    public static String getUserById(int userId) {
        try {
            URL url = new URL(BASE_URL + "/users/" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                return "Error getting user by ID: " + responseCode;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by ID", e);
            return "Error getting user by ID.";
        }
    }

    public static String getUserByUsername(String username) {
        try {
            URL url = new URL(BASE_URL + "/users?username=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                return "Error getting user by username: " + responseCode;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by username", e);
            return "Error getting user by username.";
        }
    }

    public static String getUserPosts(int userId) {
        try {
            URL url = new URL(BASE_URL + "/posts?userId=" + userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                return "Error getting posts of user: " + responseCode;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting posts of user", e);
            return "Error getting posts of user.";
        }
    }

    public static String getCommentsForPost(int postId) {
        try {
            URL url = new URL(BASE_URL + "/comments?postId=" + postId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                return "Error getting comments for post: " + responseCode;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting comments for post", e);
            return "Error getting comments for post.";
        }
    }

    public static void saveCommentsToFile(String commentsJson, int userId, int postId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user-" + userId + "-post-" + postId + "-comments.json"))) {
            writer.write(commentsJson);
            System.out.println("Comments saved to file: user-" + userId + "-post-" + postId + "-comments.json");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving comments to file", e);
        }
    }

    public static void getUserPostsAndCommentsAndSaveToFile(int userId) {
        String postsJson = getUserPosts(userId);
        System.out.println("Posts for user with ID " + userId + ": " + postsJson);

        int lastPostId = 10;

        String commentsJson = getCommentsForPost(lastPostId);
        saveCommentsToFile(commentsJson, userId, lastPostId);
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}
