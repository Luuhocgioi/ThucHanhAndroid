package clc65.hoangluu.vdlamviecsqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    EditText edtTenSach, edtGiaBan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tạo cơ sở dữ lệu
        db = openOrCreateDatabase("books.db", MODE_PRIVATE, null);
//      getBookData();
        ArrayList<String> dsTenSach = getBookName();






//        //Tạo bảng BOOK
////        String sqlXoaBang = "DROP TABLE IF EXISTS BOOK";
////        String sqlTaoBang =   "CREATE TABLE BOOKS(BookID integer PRIMARY KEY, BookName text, Page integer, Price Float, Description text)";
////        db.execSQL(sqlXoaBang);
////        db.execSQL(sqlTaoBang);
////        //thêm vào một số dòng dữ lệu
////        String sqlThem1 =  "INSERT INTO BOOKS VALUES(1, 'Java', 100, 9.99, 'sách về java')";
////        String sqlThem2 =  "INSERT INTO BOOKS VALUES(2, 'Android', 320, 19.00, 'Android cơ bản')";
////        String sqlThem3 =  "INSERT INTO BOOKS VALUES(3, 'Học làm giàu', 120, 0.99, 'sách đọc cho vui')";
////        db.execSQL(sqlThem1);
////        db.execSQL(sqlThem2);
////        db.execSQL(sqlThem3);
////        //Test
//
//        //Truy vấn
//        String sqlSelectAll = "SELECT * FROM BOOKS";
//        Cursor resultSet = db.rawQuery(sqlSelectAll,null);
//        ArrayList<String> dsTenSach = new ArrayList<String>();
//        ArrayList<BOOKS> dsSach = new ArrayList<BOOKS>();
//        resultSet.moveToFirst();
//        while (true) {
//            int maSach = resultSet.getInt(0);
//            String tenSach = resultSet.getString(1);
//            int soTrang = resultSet.getInt(2);
//            float gia = resultSet.getFloat(3);
//            String moTa = resultSet.getString(4);
//            BOOKS books = new BOOKS(maSach, tenSach, soTrang, gia, moTa);
//            // Gói vào 1 đối tượng ==> tạo một thực thể/lớp có cấu trúc tương đương có nghiax là 1 lớp có bấy nhiêu thành phần
//
//
//            // Ở bài demo này, ta chỉ hiển ra tên sách lên ListView
//
//
//            // Thêm vào 1 biến danh sách
//            // Dùng một danh sách(ArrayList<String> để chứa tên sách
//            dsTenSach.add(tenSach);
//            dsSach.add(books);
//
//            //Di chuyển đến bản ghi tiếp theo, neesu đã hết, thì thoát khỏi vòng lập
//            resultSet.moveToNext();
//            if (resultSet.moveToNext() == false) break;
//        }
//        db.close();

        //Hiện ListView
        /*ArrayList<String> dsTenSach = new ArrayList<String>();*/
        ListView lvSach = findViewById(R.id.lvSach);
        ArrayAdapter<String> adapterSach = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dsTenSach);
        lvSach.setAdapter(adapterSach);


        Button btnAddBook = findViewById(R.id.btnAddBook);
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lay du lieu
                edtTenSach = findViewById(R.id.edtTenSach);
                edtGiaBan = findViewById(R.id.edtGiaBan);
                String tenSach = edtTenSach.getText().toString().trim();
                String gia = edtGiaBan.getText().toString().trim();
                float giaBan = Float.parseFloat(gia);


                //them vao db
                ContentValues row = new ContentValues();
                row.put("BookName", tenSach);
                row.put("Price", giaBan);
                db.insert("BOOKS", null, row);


                //lam tuoi lai ListView
                dsTenSach.clear();
                dsTenSach.addAll(getBookName());
                adapterSach.notifyDataSetChanged();
            }
        });
    }

    ArrayList<BOOKS> getBookData() {

        db = openOrCreateDatabase("books.db", MODE_PRIVATE, null);
        String sqlSelectAll = "SELECT * FROM BOOKS";
        Cursor resultSet = db.rawQuery(sqlSelectAll, null);
        ArrayList<BOOKS> dsSach = new ArrayList<BOOKS>();
        while (true) {
            int maSach = resultSet.getInt(0);
            String tenSach = resultSet.getString(1);
            int soTrang = resultSet.getInt(2);
            float gia = resultSet.getFloat(3);
            String moTa = resultSet.getString(4);

            BOOKS books = new BOOKS(maSach, tenSach, soTrang, gia, moTa);

            dsSach.add(books);

            resultSet.moveToNext();
            if (resultSet.moveToNext() == false) break;
        }
        db.close();
        return dsSach;
    }
    ArrayList<String> getBookName() {

        String sqlSelectAll = "SELECT * FROM BOOKS";
        Cursor resultSet = db.rawQuery(sqlSelectAll, null);
        ArrayList<String> dsTenSach = new ArrayList<String>();
        while (resultSet.moveToNext()) {
            dsTenSach.add(resultSet.getString(1));
        }
        resultSet.close();
        return dsTenSach;
    }
}