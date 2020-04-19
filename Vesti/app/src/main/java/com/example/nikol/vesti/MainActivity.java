package com.example.nikol.vesti;

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
import android.view.Menu;
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
import com.example.nikol.vesti.Retrofit2.Article;
import com.example.nikol.vesti.Retrofit2.EngadgetInterface;
import com.example.nikol.vesti.Retrofit2.NewsResponse;
import com.example.nikol.vesti.Retrofit2.RetroCreator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<NewsItemModel> newsFeed = new ArrayList<>();
    private List<NewsItemModel> newsList = new ArrayList<>();
    private List<Article> articlesLists; //byMilos

    private ListView newsItems;
    private ArrayAdapter<NewsItemModel> adapter;
    boolean savedActivity;
    DatabaseBroker dbb = new DatabaseBroker(this);

    String mParam1 = "https://newsapi.org/v1/articles?source=";
    String mParam2 = "&sortBy=latest&apiKey=";
    String apiKey = "aacda514ba6e4a4489bfcfcd0b3f2008";

    String link = new String();
    String url = new String();


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

        link = "http://webhose.io/search?token=4adbc4b1-042b-4233-ba22-d787f3c985dc&format=json&q=Technology%20&sort=relevancy";

        engine(link);
        onClickListener();

    }


    private void engine(String link) {

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET, link, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray newsItems = response.getJSONArray("posts");

                            for (int i = 0; i<10; i++) {
                                JSONObject temp = newsItems.getJSONObject(i);

                                String title = temp.getString("title");
                                String description = temp.getString("title");
                                String url = temp.getString("url");

                                JSONObject data = temp.getJSONObject("thread");
                                String urlToImage = data.getString("main_image");

                                newsFeed.add(new NewsItemModel(title, description, url, urlToImage));


                                Log.d(description, description);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }},

                new Response.ErrorListener(){
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

    private class customAdapter extends ArrayAdapter<NewsItemModel> {

        public customAdapter() {
            super(MainActivity.this, R.layout.item, newsFeed);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
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


    public void onClickListener () {

        registerForContextMenu(newsItems);

        newsItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                NewsItemModel currentItem = newsFeed.get(position);
                Intent i = new Intent("com.example.nikol.vesti.ItemView");
                i.putExtra("heading", currentItem.getTitle());
                i.putExtra("description", currentItem.getDescription());
                i.putExtra("url", currentItem.getUrl());
                i.putExtra("urlToImage", currentItem.getUrlToImage());
                i.putExtra("isSavedCalled", savedActivity);
                startActivity(i);
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_engaget) {
            apiKey = "aacda514ba6e4a4489bfcfcd0b3f2008";
            mParam1 = "https://newsapi.org/v1/articles?source=";
            mParam2 = "&sortBy=latest&apiKey=";
            link = mParam1 + "engadget" + mParam2 + apiKey;
            engine(link);
            savedActivity = false;

        } else if (id == R.id.nav_ign) {
            apiKey = "aacda514ba6e4a4489bfcfcd0b3f2008";
            mParam1 = "https://newsapi.org/v1/articles?source=";
            mParam2 = "&sortBy=latest&apiKey=";
            link = mParam1 + "ign" + mParam2 + apiKey;
            engine(link);
            savedActivity = false;

        } else if (id == R.id.nav_tc) {
            apiKey = "aacda514ba6e4a4489bfcfcd0b3f2008";
            mParam1 = "https://newsapi.org/v1/articles?source=";
            mParam2 = "&sortBy=latest&apiKey=";
            link = mParam1 + "techcrunch" + mParam2 + apiKey;
            engine(link);
            savedActivity = false;

        } else if (id == R.id.nav_verge) {
            apiKey = "aacda514ba6e4a4489bfcfcd0b3f2008";
            mParam1 = "https://newsapi.org/v1/articles?source=";
            mParam2 = "&sortBy=latest&apiKey=";
            link = mParam1 + "theverge" + mParam2 + apiKey;
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

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /**
     * Pogledaj RETROFIT II na internetru
     */
    private void getRetroNewsByMilos(String url) {

        EngadgetInterface service = RetroCreator.getService().create(EngadgetInterface.class);

        final Call<NewsResponse> reops = service.getTechRadar();
        reops.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, retrofit2.Response<NewsResponse> response) {
                articlesLists.addAll(response.body().articles);
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}