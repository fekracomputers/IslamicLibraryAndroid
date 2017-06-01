package com.fekracomputers.islamiclibrary.model;

/**
 * Created by Mohammad Yahia on 05/9/2016.
 */
public class BookInfo {


    //TODO is it better to make them public and remove setters and getters?
    private String name;
    private int downloadStatus;
    private String booKLongDescription;
    private AuthorInfo authorInfo;
    private String informationCard;
    private int bookId;
    private BookCategory category;

    public BookInfo(int bookId, String bookTitle, String bookInfo, String informationCard, AuthorInfo authorInfo, BookCategory category) {
        this.bookId = bookId;
        this.name = bookTitle;
        booKLongDescription=bookInfo;
        this.informationCard = informationCard;
        this.authorInfo = authorInfo;
        this.category = category;
    }

    public BookInfo(int bookId, String bookTitle, int authorId, String authorName, int downloadStatus) {

        this.bookId = bookId;
        this.name = bookTitle;
        this.downloadStatus = downloadStatus;
        this.authorInfo = new AuthorInfo(authorId, authorName);
    }

    public String getInformationCard() {
        return informationCard;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorName() {
        return authorInfo.name;
    }

    public AuthorInfo getAuthorInfo() {
        return authorInfo;
    }


    public String getBooKLongDescription() {
        return booKLongDescription;
    }


    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "name='" + name + '\'' +
                ", authorInfo=" + authorInfo.toString() +
                ", bookId=" + bookId +

                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookInfo bookInfo = (BookInfo) o;

        return bookId == bookInfo.bookId;

    }

    @Override
    public int hashCode() {
        return bookId;
    }


    public BookCategory getCategory() {
        return category;
    }
}
