package clc65.hoangluu.vdlamviecsqlite;

public class BOOKS {
    int bookID;
    String bookName;
    int bookPage;
    float bookPrice;
    String bookDescription;

    public BOOKS() {
    }

    public BOOKS(int bookID, String bookName, int bookPage, float bookPrice, String bookDescription) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.bookPage = bookPage;
        this.bookPrice = bookPrice;
        this.bookDescription = bookDescription;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getBookPage() {
        return bookPage;
    }

    public void setBookPage(int bookPage) {
        this.bookPage = bookPage;
    }

    public float getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(float bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }
}