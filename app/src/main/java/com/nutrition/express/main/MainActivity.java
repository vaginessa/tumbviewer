package com.nutrition.express.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.follower.FollowersActivity;
import com.nutrition.express.login.LoginActivity;
import com.nutrition.express.model.helper.SearchHistoryHelper;
import com.nutrition.express.photolist.PhotoActivity;
import com.nutrition.express.videolist.VideoListActivity;


public class MainActivity extends AppCompatActivity {
    private SearchHistoryHelper historyHelper;
    private CommonRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.blog_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historyHelper = new SearchHistoryHelper();

        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.setData(historyHelper.getHistories());
        builder.addItemType(String.class, R.layout.item_text, new CommonRVAdapter.CreateViewHolder() {
            @Override
            public CommonViewHolder createVH(View view) {
                return new BaseVH(view);
            }
        });
        adapter = builder.build();
        recyclerView.setAdapter(adapter);
    }

    public class BaseVH extends CommonViewHolder<String>
            implements View.OnClickListener, View.OnLongClickListener {
        String name;

        public BaseVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void bindView(String object) {
            ((TextView) itemView).setText(object);
            name = object;
        }

        @Override
        public void onClick(View v) {
            openPostsVideo(name);
        }

        @Override
        public boolean onLongClick(View v) {
            showDeleteDialog(name, getAdapterPosition());
            return true;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.input);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("输入博客名");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                openPostsVideo(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.input:
                break;
            case R.id.followers:
                Intent intent = new Intent(this, FollowersActivity.class);
                startActivity(intent);
                break;
            case R.id.login:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.photos:
                Intent photoIntent = new Intent(this, PhotoActivity.class);
                startActivity(photoIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void openPostsVideo(String blogName) {
        Intent intent = new Intent(this, VideoListActivity.class);
        intent.putExtra("blog_name", blogName);
        startActivity(intent);
        historyHelper.add(blogName);
        adapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(final  String text, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("删除记录" + text + "?");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                historyHelper.remove(text);
                adapter.notifyItemRemoved(position);
            }
        });
        builder.create().show();
    }

}
