package com.example.puzzle;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button choose;
    private Button default_mode;
    private Button about;
    private Button exit;
    private String item[] = {"简单","中等","困难"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        choose = findViewById(R.id.choose);
        default_mode = findViewById(R.id.default_mode);
        about = findViewById(R.id.about);
        exit = findViewById(R.id.exit);

        choose.setOnClickListener(this);
        default_mode.setOnClickListener(this);
        about.setOnClickListener(this);
        exit.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.music);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, Prefs.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setChoose(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择难度");
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this,MapStorage.class);
                Bundle bundle = new Bundle();
                switch (which){
                    case 0:
                        bundle.putInt("xy_album",3);
                        break;
                    case 1:
                        bundle.putInt("xy_album",6);
                        break;
                    case 2:
                        bundle.putInt("xy_album",9);
                        break;
                }
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    private void setAbout(){
        AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage(R.string.about_text);
        AlertDialog alertdialog=alertdialogbuilder.create();
        alertdialog.show();
    }

    private void setDefault_pic(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("选择难度");
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, Default_mode.class);
                Bundle bundle = new Bundle();
                switch (which){
                    case 0:
                        bundle.putInt("xy_default",3);
                        break;
                    case 1:
                        bundle.putInt("xy_default",6);
                        break;
                    case 2:
                        bundle.putInt("xy_default",9);
                        break;
                }
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose:
                setChoose();
                break;
            case R.id.default_mode:
                setDefault_pic();
                break;
            case R.id.about:
                setAbout();
                break;
            case R.id.exit:
                finish();
                break;
        }
    }
}
