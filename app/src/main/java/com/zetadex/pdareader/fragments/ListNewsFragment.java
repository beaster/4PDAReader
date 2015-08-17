package com.zetadex.pdareader.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zetadex.pdareader.Category;
import com.zetadex.pdareader.IDataLoadCallback;
import com.zetadex.pdareader.ItemNewsActivity;
import com.zetadex.pdareader.MainActivity;
import com.zetadex.pdareader.NewsAdapter;
import com.zetadex.pdareader.R;
import com.zetadex.pdareader.database.Columns;
import com.zetadex.pdareader.database.PdaContentProvider;
import com.zetadex.pdareader.service.ServiceHelper;

public class ListNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, IDataLoadCallback,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private NewsAdapter mAdapter;
    private int mCategoryId;
    private int mCurrentPage = 1;   //TODO: need to improve, because it's no good solution

    public static ListNewsFragment newInstance(int sectionNumber) {
        ListNewsFragment fragment = new ListNewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ListNewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        initViews(rootView);

        return rootView;

    }

    private void initViews(View rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListView = (ListView) rootView.findViewById(R.id.listview);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) mAdapter.getItem(position);

                String title = c.getString(c.getColumnIndex(Columns.NewsColumns.TITLE));
                String url = c.getString(c.getColumnIndex(Columns.NewsColumns.ARTICLE_URL));

                Intent intent = new Intent(getActivity(), ItemNewsActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("url", "http://4pda.ru/" + url);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(1, null, this);

        mAdapter = new NewsAdapter(getActivity().getApplicationContext(), null);
        mAdapter.setOnDataLoadListener(this);

        mCurrentPage = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(Category.values()[mCategoryId].name(), 1);
        mAdapter.setCurrentPage(mCurrentPage);

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCategoryId = getArguments().getInt(ARG_SECTION_NUMBER);
        ((MainActivity) activity).onSectionAttached(mCategoryId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putInt(Category.values()[mCategoryId].name(), mAdapter.getCurrentPage());
        editor.commit();
    }

    @Override
    public void onRefresh() {
        update();
    }

    private void update() {
        mAdapter.resetPage();
        startLoad(mAdapter.getCurrentPage());
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0);
    }

    @Override
    public void startLoad(int page) {
        ServiceHelper.getInstance(getActivity().getApplicationContext()).loadNews(mCategoryId, page);
        onStartLoading();
    }

    @Override
    public void onStartLoading() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public boolean isLoadPending() {
        return false;
    }

    @Override
    public void onFinishLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
        if (mAdapter.getCurrentPage() != 1) {
            mListView.setSelection(mListView.getLastVisiblePosition() + 1);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                PdaContentProvider.ARTICLES_CONTENT_URI, null, Columns.NewsColumns.CATEGORY + " = " + mCategoryId, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (data.getCount() == 0) {
            update();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
