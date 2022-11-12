package com.beckytech.og_artiiwwankutaa5ffaa.model;

import java.io.Serializable;

public class Model implements Serializable {
    private final String title;
    private final int endPage;
    private final int startPage;
    private final String subTitle;

    public Model(String title, String subTitle, int endPage, int startPage) {
        this.title = title;
        this.endPage = endPage;
        this.startPage = startPage;
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getEndPage() {
        return endPage;
    }

    public int getStartPage() {
        return startPage;
    }
}
