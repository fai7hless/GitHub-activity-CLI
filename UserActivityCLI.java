import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserActivityCLI {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username of github acc: ");
        String username = sc.nextLine();
        sc.close();

        fetchUserData(username);
    }

    private static void fetchUserData(String username){
        String API_URL = "https://api.github.com/users/"+ username + "/events";
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(API_URL)).header("Accept", "application/vnd.github+json").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                System.out.println("User not found");
            }
            if (response.statusCode() == 200) {
                JsonArray arr = JsonParser.parseString(response.body()).getAsJsonArray();
                displayUserData(arr);
            }else{
                System.out.println("Error: " + response.statusCode());
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static void displayUserData(JsonArray arr){
        for (JsonElement element : arr){
            JsonObject obj = element.getAsJsonObject();
            String type = obj.get("type").getAsString();
            String msg;
            switch (type) {
                case "PushEvent":
                    int count = obj.get("payload").getAsJsonObject().get("size").getAsInt();
                    msg = "Pushed " + count + " commit/s to " + obj.get("repo").getAsJsonObject().get("name");
                    break;

                case "CreateEvent":
                    msg = "Created " + obj.get("payload").getAsJsonObject().get("ref_type").getAsString()
                    + " in " + obj.get("repo").getAsJsonObject().get("name").getAsString();
                    break;
                
                case "WatchEvent":
                    msg = "Starred " + obj.get("repo").getAsJsonObject().get("name").getAsString();
                    break;

                case "IssuesEvent":
                    msg = obj.get("payload").getAsJsonObject().get("action").getAsString()
                    + " issue in " + obj.get("repo").getAsJsonObject().get("name").getAsString();
                    break;
                
                case "ForkEvent":
                    msg = "Forked " + obj.get("repo").getAsJsonObject().get("name").getAsString();
                    break;  

                case "DeleteEvent":
                    msg = "Deleted " + obj.get("payload").getAsJsonObject().get("ref_type").getAsString()
                    + " in " + obj.get("repo").getAsJsonObject().get("name").getAsString();
                    break;
                
                case "PullRequestEvent":
                    msg = obj.get("payload").getAsJsonObject().get("action").getAsString()
                    + " pull request in " + obj.get("repo").getAsJsonObject().get("name").getAsString();
                    break;
                    
                default:
                    msg = obj.get("type").getAsString().replace("Event", "")
                            + " in " + obj.get("repo").getAsJsonObject().get("name").getAsString();
                    break;
            }
            System.out.println(msg);
        }
    }
    
}