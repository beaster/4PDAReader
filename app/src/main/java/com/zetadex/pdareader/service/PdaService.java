package com.zetadex.pdareader.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.zetadex.pdareader.Category;
import com.zetadex.pdareader.database.Columns;
import com.zetadex.pdareader.database.PdaContentProvider;
import com.zetadex.pdareader.model.NewsItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PdaService extends IntentService {

    public PdaService() {
        super("NewsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int categoryId = intent.getIntExtra("category", 0);
        int page = intent.getIntExtra("page", 1);

        Category category = Category.values()[categoryId];

        StringBuilder url = new StringBuilder(category.getBaseUrl());
        url.append("page/").append(page).append("/");

        try {
            Document builder = Jsoup.connect(url.toString()).get();

            Elements titles = builder.select(category.getTitleAttr());
            Elements photos = builder.select(category.getPhotoAttr());
            Elements urls = builder.select(category.getUrlAttr());

            int count = photos.size();
            ContentValues[] values = new ContentValues[count];
            for (int i = 0; i < count; i++) {
                String title = titles.get(i).attr("title");
                String articleUrl = urls.get(i).attr("href");
                String photoUrl = photos.get(i).attr("src");

                //add to content provider
                NewsItem item = new NewsItem(title, photoUrl, articleUrl, categoryId);
                values[i] = item.toContentValues();
            }

            ContentResolver cr = getApplicationContext().getContentResolver();
            if (page == 1) { //delete all articles, because we use simple cache:)
                cr.delete(PdaContentProvider.ARTICLES_CONTENT_URI, Columns.NewsColumns.CATEGORY + " = " + categoryId, null);
            }

            cr.bulkInsert(PdaContentProvider.ARTICLES_CONTENT_URI, values);
        } catch (IOException e) {
            e.printStackTrace();
            //notify error to UI
            //TODO:
        }

    }
}
