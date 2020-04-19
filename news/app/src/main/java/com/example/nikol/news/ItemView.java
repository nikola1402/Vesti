package com.example.nikol.news;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by nikol on 29-Mar-17.
 */

public class ItemView extends AppCompatActivity {

    DatabaseBroker dbb = new DatabaseBroker(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Bundle bundle = getIntent().getExtras();

        final String title = bundle.getString("title");
        final String description = bundle.getString("description");
        final String url = bundle.getString("url");
        final String urlToImage = bundle.getString("urlToImage");
        final boolean isSavedItems = bundle.getBoolean("isSavedCalled");

        TextView Heading = (TextView) findViewById(R.id.heading);
        Heading.setText(title);

        TextView Description = (TextView) findViewById(R.id.desc);
        Description.setText(description);

        ImageView Image = (ImageView) findViewById(R.id.leftIco);
        Picasso.with(ItemView.this).load(urlToImage).into(Image);

        //Floating dugme prebaceno iz MainActivity. Tamo je obrisano.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);

        if (isSavedItems) {
            fab.setVisibility(View.GONE);
            fab1.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
            fab1.setVisibility(View.GONE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbb.open();
                String chck = dbb.checkArticle(url);

                if (chck.equals(title)) {
                    Snackbar.make(view, "Vest je vec sacuvana!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {

                    Snackbar.make(view, "Vest je uspešno sačuvana", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    dbb.insertArticle(title, description, url, urlToImage);
                }
                dbb.close();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbb.open();


                boolean del = dbb.deleteArticle(url);

                if (del) {
                    Snackbar.make(view, "Vest je uspesno obrisana!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Greska prilikom brisanja vesti!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                dbb.close();
            }
        });

    }
}
