package netron90.personnalize.personnalize_co_admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class Diapositive extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diapositive);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_diapo);
    }
}
