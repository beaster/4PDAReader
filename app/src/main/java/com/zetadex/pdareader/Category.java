package com.zetadex.pdareader;


import android.content.Context;
import android.content.res.Resources;

public enum Category {

    NEWS, ARTICLES, REVIEWS, SOFTWARE, GAMES;

    public String toString(Context ctx) {
        int resId = -1;
        switch (this) {
            case NEWS:
                resId = R.string.category_news;
                break;
            case ARTICLES:
                resId = R.string.category_articles;
                break;
            case REVIEWS:
                resId = R.string.category_reviews;
                break;
            case SOFTWARE:
                resId = R.string.category_software;
                break;
            case GAMES:
                resId = R.string.category_games;
                break;
        }

        String name = null;
        if (resId != -1) {
            Resources res = ctx.getResources();
            name = res.getString(resId);
        }

        return name;
    }

    public String getBaseUrl() {
        switch (this) {
            case NEWS:
                return "http://4pda.ru/news/";
            case ARTICLES:
                return "http://4pda.ru/articles/";
            case REVIEWS:
                return "http://4pda.ru/reviews/";
            case SOFTWARE:
                return "http://4pda.ru/software/";
            case GAMES:
                return "http://4pda.ru/games/";
            default:
                throw new IllegalArgumentException("Unknow category");
        }
    }

    public String getDataAttr() {
        switch (this) {
            case NEWS:
            case ARTICLES:
            case SOFTWARE:
            case GAMES:
                return "article > div > div[itemprop]";
            case REVIEWS:
                return "div > div > ul > li > div[class = content]";
            default:
                throw new IllegalArgumentException("Unknow category");
        }
    }

    public String getPhotoAttr() {
        switch (this) {
            case NEWS:
            case ARTICLES:
            case SOFTWARE:
            case GAMES:
                return "article > div > a > img";
            case REVIEWS:
                return "div > div > ul > li > div > a > img";
            default:
                throw new IllegalArgumentException("Unknow category");
        }
    }

    public String getTitleAttr() {
        switch (this) {
            case NEWS:
            case ARTICLES:
            case SOFTWARE:
            case GAMES:
                return "article > div > h1 > a";
            case REVIEWS:
                return "div > div > ul > li > div > h1 > a";
            default:
                throw new IllegalArgumentException("Unknow category");
        }
    }

    public String getUrlAttr() {
        switch (this) {
            case NEWS:
            case ARTICLES:
            case SOFTWARE:
            case GAMES:
                return "article > div > h1 > a";
            case REVIEWS:
                return "div > div > ul > li > div > h1 > a";
            default:
                throw new IllegalArgumentException("Unknow category");
        }
    }

}
