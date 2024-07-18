import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CricketAPI {
    private static final String API_URL = "https://api.cuvora.com/car/partner/cricket-data";
    private static final String API_KEY = "test -creds@2320";

    public static List<CricketMatch> getCricketMatchesFromAPI() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL)
                .header("apiKey", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String jsonResponse = response.body().string();
            JsonArray matchesArray = new Gson().fromJson(jsonResponse, JsonArray.class);

            List<CricketMatch> matches = new ArrayList<>();
            for (int i = 0; i < matchesArray.size(); i++) {
                JsonObject match = matchesArray.get(i).getAsJsonObject();
                String team1Name = match.get("team1_name").getAsString();
                int team1Score = match.get("team1_score").getAsInt();
                String team2Name = match.get("team2_name").getAsString();
                int team2Score = match.get("team2_score").getAsInt();

                CricketMatch cricketMatch = new CricketMatch(team1Name, team1Score, team2Name, team2Score);
                matches.add(cricketMatch);
            }

            return matches;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        List<CricketMatch> matches = getCricketMatchesFromAPI();
        if (matches == null) {
            System.out.println("Failed to fetch matches data.");
            return;
        }

        int highestScore = 0;
        String highestScoringTeam = "";
        int matchesWith300Plus = 0;

        for (CricketMatch match : matches) {
            if (match.getTeam1Score() > highestScore) {
                highestScore = match.getTeam1Score();
                highestScoringTeam = match.getTeam1Name();
            }
            if (match.getTeam2Score() > highestScore) {
                highestScore = match.getTeam2Score();
                highestScoringTeam = match.getTeam2Name();
            }

            if (match.getTeam1Score() + match.getTeam2Score() > 300) {
                matchesWith300Plus++;
            }
        }

        String result = String.format("Highest Score: %d and Team Name is: %s\nNumber Of Matches with total 300 Plus Score: %d",
                highestScore, highestScoringTeam, matchesWith300Plus);

        System.out.println(result);
    }
}
