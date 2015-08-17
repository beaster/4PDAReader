package com.zetadex.pdareader;

public interface IDataLoadCallback {

    void startLoad(int page);

    void onStartLoading();

    boolean isLoadPending();

    void onFinishLoading();
}
