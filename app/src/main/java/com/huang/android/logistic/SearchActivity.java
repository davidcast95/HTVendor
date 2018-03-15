package com.huang.android.logistic;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by davidwibisono on 08/11/17.
 */

public class SearchActivity extends AppCompatActivity {

    protected MenuItem mSearchItem;
    protected SearchView sv;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu); // Put your search menu in "menu_search" menu file.
        mSearchItem = menu.findItem(R.id.search);
        sv = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        sv.setIconified(true);

        SearchManager searchManager = (SearchManager)  getSystemService(Context.SEARCH_SERVICE);
        sv.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchQuery(query);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.search:
                onSearchRequested();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void searchQuery(String keyword) {

    }
}

