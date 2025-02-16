import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

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
                System.out.println(response.body());
            }else{
                System.out.println("Error: " + response.statusCode());
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    
}