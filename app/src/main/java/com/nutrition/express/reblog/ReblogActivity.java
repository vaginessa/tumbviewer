package com.nutrition.express.reblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.nutrition.express.R;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.rest.bean.BlogInfoItem;

import java.util.ArrayList;
import java.util.List;


public class ReblogActivity extends AppCompatActivity implements ReblogContract.View {
    private String name, id, key, type;
    private ReblogPresenter presenter;

    private EditText commentET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reblog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        commentET = (EditText) findViewById(R.id.comment);

        List<BlogInfoItem> blogInfoItems = DataManager.getInstance().getUsers().getBlogs();
        List<String> names = new ArrayList<>();
        for (BlogInfoItem item : blogInfoItems) {
            names.add(item.getName());
        }
        if (names.size() > 0) {
            name = names.get(0);
            AppCompatSpinner spinner = (AppCompatSpinner) toolbar.findViewById(R.id.spinner);
            spinner.setVisibility(View.VISIBLE);
            SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.item_text, R.id.text, names);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    name = (String) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            Toast.makeText(this, "Error happened", Toast.LENGTH_SHORT).show();
            finish();
        }

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        key = intent.getStringExtra("reblog_key");
        type = intent.getStringExtra("type");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reblog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.post) {
            if (presenter == null) {
                presenter = new ReblogPresenter(this);
            }
            presenter.reblog(name, id, key, type, commentET.getText().toString());
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Toast.makeText(this, R.string.reblog_failure, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int code, String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, R.string.reblog_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDetach();
        }
    }

}
