package com.example.puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Default_mode extends AppCompatActivity {

    private int x;
    private TextView textView;
    private final View games[] = new View[9];
    private String mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_default_mode);
        setText();
        findViews();
        setListeners();
    }
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.music);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop(this);
    }

    private void setText(){
        textView = findViewById(R.id.text_mode);
        Intent intent = getIntent();
        x = intent.getIntExtra("xy_default",3);
        switch (x){
            case 3:
                mode = "简单模式";
                break;
            case 6:
                mode = "中等模式";
                break;
            case 9:
                mode = "困难模式";
                break;
        }
        textView.setText(mode);
    }


    private void findViews() {
        games[0] = findViewById(R.id.game_1);
        games[1] = findViewById(R.id.game_2);
        games[2] = findViewById(R.id.game_3);
        games[3] = findViewById(R.id.game_4);
        games[4] = findViewById(R.id.game_5);
        games[5] = findViewById(R.id.game_6);
        games[6] = findViewById(R.id.game_7);
        games[7] = findViewById(R.id.game_8);
        games[8] = findViewById(R.id.game_9);
    }
    private void setListeners() {
        games[0].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(0);
            }});
        games[1].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(1);
            }});
        games[2].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(2);
            }});
        games[3].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(3);
            }});
        games[4].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(4);
            }});
        games[5].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(5);
            }});
        games[6].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(6);
            }});
        games[7].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(7);
            }});
        games[8].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                setGameIntent(8);
            }});
    }
    private void setGameIntent(int i){
        Intent intent = new Intent(Default_mode.this,Default_game.class);
        Bundle bundle = new Bundle();
        bundle.putInt("game_num",i+1);
        bundle.putInt("xy_default2",x);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
