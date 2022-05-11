package com.darryncampbell.dwgettingstartedjava.Model.Article;

public class Article {
    String Article;
    String Description;
    Boolean Rejection;

    public String getArticle() {
        return Article;
    }

    public void setArticle(String article) {
        Article = article;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Boolean getRejection() {
        return Rejection;
    }

    public void setRejection(Boolean rejection) {
        Rejection = rejection;
    }

    @Override
    public String toString() {
        return "Article{" +
                "Article='" + Article + '\'' +
                ", Description='" + Description + '\'' +
                ", Rejection='" + Rejection + '\'' +
                '}';
    }
}
