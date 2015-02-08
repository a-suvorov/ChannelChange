package ru.medknc.channelschange;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements View.OnTouchListener, View.OnDragListener {

    boolean flagDrag;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button)findViewById(R.id.afirst);
        Button button2 = (Button)findViewById(R.id.asecond);
        LinearLayout layout1 = (LinearLayout)findViewById(R.id.layout1);
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.layout2);
        LinearLayout layout3 = (LinearLayout)findViewById(R.id.layout3);

        layout1.setOnDragListener(this);
        layout2.setOnDragListener(this);
        layout3.setOnDragListener(this);

        button1.setOnTouchListener(this);
        button2.setOnTouchListener(this);

        TabHost tabhost = (TabHost)findViewById(android.R.id.tabhost);
        tabhost.setup();
        TabHost.TabSpec tabSpec = tabhost.newTabSpec("tag1");
        tabSpec.setContent(R.id.analogCannels);
        tabSpec.setIndicator("Аналог. каналы");
        tabhost.addTab(tabSpec);
        tabSpec = tabhost.newTabSpec("tag2");
        tabSpec.setContent(R.id.digitalCannels);
        tabSpec.setIndicator("Цифр. каналы");
        tabhost.addTab(tabSpec);

        tabhost.setCurrentTab(0);
        //tabhost.addTab(R.id.analogCannels);
        //tabhost.addTab(R.id.digitalCannels);
        /*
        Read Channel file into the string
         */
        try {
            String content = getFileContent();
            LinearLayout textvew = (LinearLayout) findViewById(R.id.analogCannels);
            //textvew.setText(content);
            //Toast toast3 = Toast.makeText(this,content,Toast.LENGTH_LONG);
            Pattern p = Pattern.compile("([\\s\\S]*)<ATV>([\\s\\S]*)</ATV>([\\s\\S]*)<DTV>([\\s\\S]*)</DTV>([\\s\\S]*)");
            Matcher matchers = p.matcher(content);
            ArrayList<String> arContent = new ArrayList<String>();
            while (matchers.find()){
                arContent.add(matchers.group());
            }


        }
        catch (Exception e){
            Toast toast =  Toast.makeText(this,"FileNotFound",Toast.LENGTH_LONG);
            toast.show();
            Toast toast1 =  Toast.makeText(this,System.getProperty("user.dir"),Toast.LENGTH_LONG);
            toast1.show();
        }
    }

    private String getFileContent() throws IOException {
        StringBuilder content = new StringBuilder();
        //File F = new File("/sdcard/");
        BufferedReader in = new BufferedReader(new FileReader("/sdcard/GlobalClone00001.TLL"));
        String s;
        while ((s = in.readLine()) != null){
            content.append(s);
            content.append("\n");
        }
        in.close();
        return content.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            ClipData clip = ClipData.newPlainText("","");
            View.DragShadowBuilder shd = new View.DragShadowBuilder(v);
            v.startDrag(clip, shd, v, 0);
            v.setVisibility(View.INVISIBLE);
            return true;
        }
        else return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        View view = (View) event.getLocalState();//получаем объект который двигаем
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENDED:
                        if (!event.getResult()) {
                            view.setVisibility(View.VISIBLE);
                        }
                break;
            case DragEvent.ACTION_DROP:
                if (flagDrag) {

                    ViewGroup laypar = (ViewGroup) view.getParent();
                    laypar.removeView(view);

                    Toast toast1 = Toast.makeText(this, v.getTag().toString(), Toast.LENGTH_LONG);
                    toast1.show();

                    ((LinearLayout) v).addView(view);
                    ((LinearLayout) v).setGravity(Gravity.CENTER);
                    view.setVisibility(View.VISIBLE);
                }
                else return false;
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                //Здесь будем проверять есть ли в этом лайауте еще какой нибудь канал
                if (((LinearLayout)v).getChildCount()>0) flagDrag = false;
                    else flagDrag = true;

                break;
            case DragEvent.ACTION_DRAG_EXITED:
                flagDrag = false;
                break;
        }
        return true;
    }
}
