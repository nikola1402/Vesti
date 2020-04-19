package com.example.nikol.news;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<NewsItemModel> newsFeed = new ArrayList<>();
    private List<NewsItemModel> newsList = new ArrayList<>();

    private ListView newsItems;
    private ArrayAdapter<NewsItemModel> adapter;
    boolean savedActivity;
    DatabaseBroker dbb = new DatabaseBroker(this);

    String mParam1 = new String();
    String mParam2 = new String();
    String link = new String();
    String query = new String();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new customAdapter();
        newsItems = (ListView) findViewById(R.id.newsItems1);

        newsItems.setAdapter(adapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        link = "http://webhose.io/search?token=4adbc4b1-042b-4233-ba22-d787f3c985dc&format=json&q=technology%20popular%20language%3A(english)%20(site_type%3Anews%20OR%20site_type%3Ablogs)";

        engine(link);
        onClickListener();

    }


    private void engine(String lnk) {

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET, lnk, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray news = response.getJSONArray("posts");

                            newsFeed.clear();

                            for (int i = 0; i < news.length(); i++) {
                                JSONObject temp = news.getJSONObject(i);

                                String title = temp.getString("title");
                                String description = temp.getString("text");
                                String url = temp.getString("url");
                                JSONObject data = temp.getJSONObject("thread");
                                String urlToImage = data.getString("main_image");

                                if (!urlToImage.isEmpty())
                                newsFeed.add(new NewsItemModel(title, description, url, urlToImage));
                            }
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("myTag", error.toString());
                    }
                }
        );

        myReq.setRetryPolicy(new DefaultRetryPolicy(
                10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(myReq);
    }


    public void onClickListener() {

        registerForContextMenu(newsItems);

        newsItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                NewsItemModel currentItem = newsFeed.get(position);
                Intent i = new Intent("com.example.nikol.news.ItemView");
                i.putExtra("title", currentItem.getTitle());
                i.putExtra("description", currentItem.getDescription());
                i.putExtra("url", currentItem.getUrl());
                i.putExtra("urlToImage", currentItem.getUrlToImage());
                i.putExtra("isSavedCalled", savedActivity);
                startActivity(i);
            }
        });
    }


    private class customAdapter extends ArrayAdapter<NewsItemModel> {

        public customAdapter() {
            super(MainActivity.this, R.layout.item, newsFeed);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item, parent, false);
            }

            NewsItemModel currentItem = newsFeed.get(position);
            ImageView newsImage = (ImageView) convertView.findViewById(R.id.leftIco);
            TextView title = (TextView) convertView.findViewById(R.id.heading);
            TextView description = (TextView) convertView.findViewById(R.id.desc);

            title.setText(currentItem.getTitleSmall());
            description.setText(currentItem.getDescriptionSmall());
            Picasso.with(MainActivity.this).load(currentItem.getUrlToImage()).into(newsImage);

            notifyDataSetChanged();
            return convertView;
        }
    }


    public void showMessage(String Title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(Title);
        builder.setMessage(Message);
        builder.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            query = "technology%20popular";
            mParam1 = "http://webhose.io/search?token=4adbc4b1-042b-4233-ba22-d787f3c985dc&format=json&q=";
            mParam2 = "%20language%3A(english)%20(site_type%3Anews%20OR%20site_type%3Ablogs)";
            link = mParam1 + query + mParam2;
            engine(link);
            savedActivity = false;
        }
        else if (id == R.id.nav_microsoft) {
            query = "microsoft%20windows%20popular";
            mParam1 = "http://webhose.io/search?token=4adbc4b1-042b-4233-ba22-d787f3c985dc&format=json&q=";
            mParam2 = "%20language%3A(english)%20(site_type%3Anews%20OR%20site_type%3Ablogs)";
            link = mParam1 + query + mParam2;
            engine(link);
            savedActivity = false;
        }
        else if (id == R.id.nav_google) {
            query = "google%20technology%20popular";
            mParam1 = "http://webhose.io/search?token=4adbc4b1-042b-4233-ba22-d787f3c985dc&format=json&q=";
            mParam2 = "%20language%3A(english)%20(site_type%3Anews%20OR%20site_type%3Ablogs)";
            link = mParam1 + query + mParam2;
            engine(link);
            savedActivity = false;
        }
        else if (id == R.id.nav_android) {
            query = "android%20popular";
            mParam1 = "http://webhose.io/search?token=4adbc4b1-042b-4233-ba22-d787f3c985dc&format=json&q=";
            mParam2 = "%20language%3A(english)%20(site_type%3Anews%20OR%20site_type%3Ablogs)";
            link = mParam1 + query + mParam2;
            engine(link);
            savedActivity = false;
        }
        else if (id == R.id.nav_apple) {
            query = "apple%20technology%20popular";
            mParam1 = "http://webhose.io/search?token=4adbc4b1-042b-4233-ba22-d787f3c985dc&format=json&q=";
            mParam2 = "%20language%3A(english)%20(site_type%3Anews%20OR%20site_type%3Ablogs)";
            link = mParam1 + query + mParam2;
            engine(link);
            savedActivity = false;
        }
        else if (id == R.id.nav_science) {
            query = "science%20popular";
            mParam1 = "http://webhose.io/search?token=4adbc4b1-042b-4233-ba22-d787f3c985dc&format=json&q=";
            mParam2 = "%20language%3A(english)%20(site_type%3Anews%20OR%20site_type%3Ablogs)";
            link = mParam1 + query + mParam2;
            engine(link);
            savedActivity = false;
        } else if (id == R.id.nav_dbb) {

            newsFeed.clear();
            dbb.open();
            Cursor res = dbb.getAllArticles();

            if (res.getCount() == 0) {
                showMessage("Greska", "Nema sacuvanih vesti!");
            }

            while (res.moveToNext()) {
                newsFeed.add(new NewsItemModel(res.getString(1), res.getString(2), res.getString(3), res.getString(4)));
            }
            Collections.reverse(newsFeed);

            savedActivity = true;
            adapter.notifyDataSetChanged();
        }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;

    }
}

