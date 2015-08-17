package com.zetadex.pdareader.service;

import android.content.Context;
import android.content.Intent;

import com.zetadex.pdareader.Category;

public class ServiceHelper {

    private volatile static ServiceHelper sInstance;

    public static ServiceHelper getInstance(Context ctx) {
        ServiceHelper localInstance = sInstance;
        if (localInstance == null) {
            synchronized (ServiceHelper.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new ServiceHelper(ctx);
                }
            }
        }
        return localInstance;
    }

    private Context mContext;

    private ServiceHelper(Context ctx) {
        mContext = ctx;
    }

    public void loadNews(int categoryId, int page) {
        Intent intent = new Intent(mContext, PdaService.class);
        intent.putExtra("category", categoryId);
        intent.putExtra("page", page);

        mContext.startService(intent);
    }

}
