package com.example.bioprep.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executes asynchronous HTTP GET requests using HttpURLConnection and ExecutorService.
 * Returns results safely on the Android main thread.
 */
public class HttpRequestManager {

    public interface HttpCallback {
        void onSuccess(String responseBody);
        void onFailure(Exception exception);
    }

    public static class HttpException extends java.io.IOException {
        private final int statusCode;

        public HttpException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    private static final String TAG = "HttpRequestManager";
    private static final int TIMEOUT_MILLIS = 12000;

    private final ExecutorService executor;
    private final Handler mainHandler;
    private volatile boolean isCancelled = false;

    public HttpRequestManager() {
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Performs an HTTP GET request to the given URL string.
     */
    public void executeGet(final String urlString, final HttpCallback callback) {
        isCancelled = false;
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                if (isCancelled) return;

                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(TIMEOUT_MILLIS);
                connection.setReadTimeout(TIMEOUT_MILLIS);
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "HTTP GET response code: " + responseCode);

                InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                if (inputStream == null) {
                    throw new java.io.IOException("No response stream available. HTTP code: " + responseCode);
                }

                reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                final String result = stringBuilder.toString();

                if (responseCode < 200 || responseCode >= 300) {
                    throw new HttpException(responseCode, "HTTP error code: " + responseCode + ", body: " + result);
                }

                if (!isCancelled && callback != null) {
                    mainHandler.post(() -> {
                        if (!isCancelled) {
                            callback.onSuccess(result);
                        }
                    });
                }

            } catch (final Exception e) {
                Log.e(TAG, "HTTP GET error: " + e.getMessage(), e);
                if (!isCancelled && callback != null) {
                    mainHandler.post(() -> {
                        if (!isCancelled) {
                            callback.onFailure(e);
                        }
                    });
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception ignored) {
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    /**
     * Cancels any pending or in-flight request callback delivery.
     */
    public void cancel() {
        isCancelled = true;
    }

    /**
     * Shuts down the background executor thread.
     */
    public void shutdown() {
        isCancelled = true;
        executor.shutdownNow();
    }
}

