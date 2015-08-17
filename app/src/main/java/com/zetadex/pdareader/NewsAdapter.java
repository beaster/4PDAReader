package com.zetadex.pdareader;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.squareup.picasso.Picasso;
import com.zetadex.pdareader.database.Columns;

public class NewsAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private IDataLoadCallback mCallback;
    private int mCurrentPage;

    public NewsAdapter(Context context, Cursor c) {
        super(context, R.layout.list_item, c, new String[]{Columns.NewsColumns.TITLE}, new int[]{R.id.text}, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mContext = context;
    }

    public void resetPage() {
        mCurrentPage = 1;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int page) {
        mCurrentPage = page;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mCallback != null) {
            boolean isBottom = position == getCount() - 1;
            boolean isDataPending = mCallback.isLoadPending();

            if (isBottom && !isDataPending) {
                mCallback.startLoad(++mCurrentPage);
            }
        }

        return super.getView(position, convertView, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        String photoUrl = cursor.getString(cursor.getColumnIndex(Columns.NewsColumns.PHOTO_URL));

        Picasso.with(mContext).load(photoUrl).into(icon);
    }

    public void setOnDataLoadListener(IDataLoadCallback listener) {
        mCallback = listener;
    }

    @Override
    protected void onContentChanged() {
        super.onContentChanged();
        if (mCallback != null) {
            mCallback.onFinishLoading();
        }
    }

}

