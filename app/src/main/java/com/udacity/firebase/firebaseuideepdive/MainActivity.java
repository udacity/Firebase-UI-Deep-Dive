package com.udacity.firebase.firebaseuideepdive;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.udacity.firebase.firebaseuideepdive.firebaseui.FirebaseListAdapter;
import com.udacity.firebase.firebaseuideepdive.model.ShoppingList;

public class MainActivity extends AppCompatActivity {

    final static String mNames[] = {"Lyla", "JP", "Walter", "Kagure", "Paulter"};
    final static String mListNames[][] = {{"Grocery", "Shopping", "Meeting"},
            {"List", "Party", "Todo"}};

    FirebaseListAdapter<ShoppingList> mShoppingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);

        final Firebase rootRef = new Firebase("https://deepdivefirebaseui.firebaseio.com/");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRef.push().setValue(new ShoppingList(
                        randomList(),
                        randomOwner()
                ));
            }
        });
        ListView listView = (ListView) findViewById(R.id.list_view);
        mShoppingListAdapter = new FirebaseListAdapter<ShoppingList>(this,
                ShoppingList.class, R.layout.single_active_list, rootRef) {
            @Override
            protected void populateView(View v, ShoppingList model) {
                super.populateView(v, model);
                TextView listNameView = (TextView) v.findViewById(R.id.text_view_list_name);
                TextView ownerView = (TextView) v.findViewById(R.id.text_view_created_by_user);
                listNameView.setText(model.getListName());
                ownerView.setText(model.getOwner());
            }
        };
        listView.setAdapter(mShoppingListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShoppingListAdapter.cleanup();
    }

    private String randomOwner() {
        return mNames[(int) Math.floor(Math.random() * mNames.length)];
    }

    private String randomList() {

        String listName = mListNames[0][(int) Math.floor(Math.random() * mListNames[0].length)] + " " +
                mListNames[1][(int) Math.floor(Math.random() * mListNames[1].length)];
        if (Math.random() > 0.5) {
            listName = randomOwner() + "'s " + listName;
        }
        return listName;
    }

}
