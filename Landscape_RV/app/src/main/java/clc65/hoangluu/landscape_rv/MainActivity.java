package clc65.hoangluu.landscape_rv;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    LandScapeAdapter landScapeAdapter;
    ArrayList<LandScape> dsList;

    RecyclerView rvLandscape;
    void Tim(){
        rvLandscape = findViewById(R.id.rvLandscape);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tim();
        dsList=getData();
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        rvLandscape.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rvLandscape.setLayoutManager(layoutManager);
        landScapeAdapter = new LandScapeAdapter(this,dsList);
        rvLandscape.setAdapter(landScapeAdapter);

    }
    ArrayList<LandScape> getData(){
        ArrayList<LandScape> dsList = new ArrayList<>();
        dsList.add(new LandScape("NhaTrang1","anhnhatrang1"));
        dsList.add(new LandScape("Nha Trang 2","anhnhatrang2"));
        dsList.add(new LandScape("Nha Trang 3","anhnhatrang3"));
        dsList.add(new LandScape("Nha Trang 4","anhnhatrang4"));
        return dsList;
    }

}