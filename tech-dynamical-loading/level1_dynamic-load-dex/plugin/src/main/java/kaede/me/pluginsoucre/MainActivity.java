package kaede.me.pluginsoucre;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biu(MainActivity.this,"Click Button");
            }
        });
    }

    public void biu(Context context,String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
