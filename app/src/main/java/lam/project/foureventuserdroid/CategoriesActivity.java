package lam.project.foureventuserdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lam.project.foureventuserdroid.model.Cateogory;
import lam.project.foureventuserdroid.utils.complete_profile.CategoryManager;

/**
 * Created by Vale on 09/08/2016.
 */

public class CategoriesActivity extends AppCompatActivity {

    private CategoryManager categoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_choice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_categories);
        setSupportActionBar(toolbar);

        categoryManager = CategoryManager.get(this);
    }

    public void selectedButton(final View view) {

        view.setSelected(!view.isSelected());

        final int id = (int) view.getTag();
        final String title = ((TextView)view).getText().toString();

        categoryManager.AddFavourite(Cateogory.Builder.create(id,title).build());
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
        if (id == R.id.action_next) {
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
