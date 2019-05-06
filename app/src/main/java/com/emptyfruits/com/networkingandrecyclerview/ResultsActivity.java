package com.emptyfruits.com.networkingandrecyclerview;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.emptyfruits.com.networkingandrecyclerview.databinding.ActivityResultsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ResultsActivity extends AppCompatActivity {
    MyBooksAdapter booksAdapter;
    public static final String TAG = ResultsActivity.class.getName();
    ActivityResultsBinding resultsBinding;
    int maxResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultsBinding = DataBindingUtil.setContentView(this, R.layout.activity_results);
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            booksAdapter = new MyBooksAdapter(getApplicationContext());
            resultsBinding.bookList.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            resultsBinding.bookList.setAdapter(booksAdapter);
            BookAsyncTask task = new BookAsyncTask(this);
            Intent resultsIntent = getIntent();
            maxResults = resultsIntent.getIntExtra("max_results", 10);
            task.execute(resultsIntent.getStringExtra("query"));
        } else {
            Toast.makeText(this, "Network not available !!!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    static class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {
        private WeakReference<ResultsActivity> resultsActivityWeakReference;
        //You can get your API key from google api platform.
        static final String API_KEY = "YOUR API KEY HERE";
        static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

        BookAsyncTask(ResultsActivity resultsActivityWeakReference) {
            this.resultsActivityWeakReference = new WeakReference<>(resultsActivityWeakReference);
        }


        private HttpsURLConnection createConnection(String requestString) throws IOException {
            URL url = new URL(requestString);
            Log.d(TAG, "createConnection() : " + url.toString());
            return (HttpsURLConnection) url.openConnection();
        }

        private String readJsonFromStream(BufferedReader bufferedReader) throws IOException {
            String toAppend;
            StringBuilder builder = new StringBuilder();
            while ((toAppend = bufferedReader.readLine()) != null) {
                builder.append(toAppend);
            }

            return builder.toString();
        }

        @Override
        protected ArrayList<Book> doInBackground(String... query) {
            try {
                String request_url = BASE_URL + query[0] + "&maxResults="
                        + resultsActivityWeakReference.get().maxResults + "&key=" + API_KEY;
                HttpsURLConnection urlConnection = createConnection(request_url);
                if (urlConnection.getResponseCode() == 200) {
                    urlConnection.connect();
                    BufferedReader bufferedReader =
                            new BufferedReader
                                    (new InputStreamReader(urlConnection.getInputStream()));
                    String json = readJsonFromStream(bufferedReader);
                    ArrayList<Book> books = parseJSONToList(json);
                    Log.d(TAG, "doInBackground: " + books.size());
                    return books;
                } else {
                    Log.e(TAG, "doInBackground Error : " + urlConnection.getResponseMessage());
                }
                return null;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: " + e.getClass() + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            ResultsActivity resultsActivity = resultsActivityWeakReference.get();
            resultsActivity.booksAdapter.setBooksList(books);
            resultsActivity.resultsBinding.progressCircular.setVisibility(View.GONE);
            resultsActivity.resultsBinding.bookList.setVisibility(View.VISIBLE);
            resultsActivity.resultsBinding.bookList.setAdapter(resultsActivity.booksAdapter);
            super.onPostExecute(books);
        }


        private ArrayList<Book> parseJSONToList(String json) throws Exception {
            Log.d(TAG, "parseJSONToList() called with: json = [" + json + "]");
            JSONObject rootObject = new JSONObject(json);
            JSONArray items = rootObject.getJSONArray("items");
            ArrayList<Book> books = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject jsonObject = items.getJSONObject(i);
                    JSONObject bookJSON = jsonObject.getJSONObject("volumeInfo");
                    String title = bookJSON.getString("title");
                    String authors = bookJSON.getJSONArray("authors").join(", ");
                    books.add(new Book(authors, title));
                } catch (JSONException je) {
                    // continue
                    Log.e(TAG, "parseJSONToList: " + je.getMessage(), je.getCause());
                }
            }
            return books;
        }
    }
}