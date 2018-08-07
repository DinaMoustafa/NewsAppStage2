package com.example.dina.newsappstage2;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dina.newsappstage2.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    private NewsAdapter mAdapter;
    private TextView mEmptyState;
    private static final int NEWS_LOADER_ID = 1;
    private static final String API_KEY = "57fac28f-c07f-47fe-ad65-28f50d9140af";
    private static final String REQUEST_URL =
            "http://content.guardianapis.com/search";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ListView listView = findViewById(R.id.list);
        mEmptyState = findViewById(R.id.empty_state);
        listView.setEmptyView(mEmptyState);
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(mAdapter);
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                News currentNews = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getmNewsUrl());
                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loaders. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyState.setText(R.string.no_internet_connection);
        }
    }


    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String searchPharse = sharedPrefs.getString(
                getString(R.string.settings_search_key),
                getString(R.string.settings_search_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        //parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(REQUEST_URL);
        //buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //Append query parameter and its value
        if (searchPharse != "") {
            uriBuilder.appendQueryParameter("q", searchPharse);
        }
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        //Create a new loader for the given completed URL
        return new NewsLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        View loadingIndicator = findViewById(R.id.progress_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No News found."
        mEmptyState.setText(R.string.no_news);
        // If there is a valid list of {@link New}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }


    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }


    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
