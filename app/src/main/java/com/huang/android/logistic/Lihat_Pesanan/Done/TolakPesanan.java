package com.huang.android.logistic.Lihat_Pesanan.Done;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huang.android.logistic.MainActivity;
import com.huang.android.logistic.R;


public class TolakPesanan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tolak_pesanan);
        setTitle(R.string.order_rejected);

    }
    public void confirm(View view){
        EditText isi = (EditText)findViewById(R.id.alasan);
        if (isi.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.alasantidakbolehkosong,Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
