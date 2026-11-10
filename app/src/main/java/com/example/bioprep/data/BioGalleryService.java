package com.example.bioprep.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.bioprep.models.BioGalleryItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioGalleryService {

    private static final String TAG = "BioGalleryService";

    // Primary: Kingdom Animalia (taxon_id=1) with species rank gives real zoology species with common & scientific names
    private static final String API_URL_SPECIES =
            "https://api.inaturalist.org/v1/observations?taxon_id=1&hrank=species&photos=true&per_page=20&order_by=random";

    // Fallback: Exact user-specified endpoint
    private static final String API_URL_GENERAL =
            "https://api.inaturalist.org/v1/observations?taxon_name=Animalia&photos=true&per_page=20&order_by=random";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface GalleryCallback {
        void onSuccess(List<BioGalleryItem> items);
        void onError(String errorMessage);
    }

    public void fetchRandomZoologyObservations(GalleryCallback callback) {
        executor.execute(() -> {
            List<BioGalleryItem> items = executeFetch(API_URL_SPECIES);
            if (items.isEmpty()) {
                Log.d(TAG, "Primary species query yielded 0 items, trying general Animalia query...");
                items = executeFetch(API_URL_GENERAL);
            }

            final List<BioGalleryItem> finalItems = items;
            if (finalItems.isEmpty()) {
                mainHandler.post(() -> callback.onError("No zoological images found. Please check internet and refresh."));
            } else {
                mainHandler.post(() -> callback.onSuccess(finalItems));
            }
        });
    }

    private List<BioGalleryItem> executeFetch(String targetUrl) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(targetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(12000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-Agent", "BioPrepAcademicStudyGuide/1.0 (Android; Education)");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Response code: " + responseCode + " for " + targetUrl);
                return new ArrayList<>();
            }

            InputStream inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return parseObservationsJson(response.toString());

        } catch (Exception e) {
            Log.e(TAG, "Error fetching iNaturalist observations from " + targetUrl, e);
            return new ArrayList<>();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {}
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private List<BioGalleryItem> parseObservationsJson(String jsonStr) {
        List<BioGalleryItem> list = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonStr);
            JSONArray results = root.optJSONArray("results");
            if (results == null) return list;

            for (int i = 0; i < results.length(); i++) {
                JSONObject obs = results.getJSONObject(i);
                String obsId = obs.optString("id", String.valueOf(i));
                String placeGuess = obs.optString("place_guess", "");

                // Taxon info
                String commonName = "";
                String scientificName = "Animalia Specimen";
                if (obs.has("taxon") && !obs.isNull("taxon")) {
                    JSONObject taxon = obs.getJSONObject("taxon");
                    scientificName = taxon.optString("name", "Animalia Specimen");
                    commonName = taxon.optString("preferred_common_name", "");
                }

                if (commonName == null || commonName.trim().isEmpty()) {
                    commonName = scientificName;
                } else {
                    commonName = capitalizeWords(commonName);
                }

                // Photos array
                JSONArray photos = obs.optJSONArray("photos");
                if (photos != null && photos.length() > 0) {
                    JSONObject photo = photos.getJSONObject(0);
                    String rawUrl = photo.optString("url", "");
                    String attribution = photo.optString("attribution", "");

                    if (!rawUrl.isEmpty()) {
                        if (rawUrl.startsWith("http://")) {
                            rawUrl = "https://" + rawUrl.substring(7);
                        }
                        String mediumUrl = rawUrl.replace("/square.", "/medium.");
                        String largeUrl = rawUrl.replace("/square.", "/large.");

                        list.add(new BioGalleryItem(
                                obsId,
                                commonName,
                                scientificName,
                                mediumUrl,
                                largeUrl,
                                rawUrl,
                                attribution,
                                placeGuess
                        ));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "JSON parsing error", e);
        }
        return list;
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)));
                if (w.length() > 1) {
                    sb.append(w.substring(1));
                }
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}
