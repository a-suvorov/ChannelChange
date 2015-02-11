package ru.medknc.channelschange;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements View.OnTouchListener, View.OnDragListener {

    boolean flagDrag;
    final int REQUEST_SAVE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
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
*/
        //создаем табы для отображения аналоговых и цифровых каналов
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



        /*
        try {


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

        }
        */
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
        if (id == R.id.action_numeric) {
            Intent intent = new Intent(getBaseContext(), FileDialog.class);
            intent.putExtra(FileDialog.START_PATH, "/sdcard");

            //can user select directories or not

            intent.putExtra(FileDialog.CAN_SELECT_DIR, true);

            //alternatively you can set file filter
            //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

            startActivityForResult(intent, REQUEST_SAVE);

        }

        return super.onOptionsItemSelected(item);
    }

    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(FileDialog.RESULT_FILE);
            if (filePath == "") {
                Toast toast2 = Toast.makeText(this, "Файл не найден", Toast.LENGTH_LONG);
                toast2.show();
            }
            else {
                Channels cannels = new Channels(this, filePath);
                //заполняем таб с аналоговыми каналами
                Map<Integer,String> atvMap = cannels.getAnalogNameChannels(this);
                LinearLayout analogTab = (LinearLayout) findViewById(R.id.analogCannels);
                for (Integer key: atvMap.keySet()){
                    LinearLayout achannel = new LinearLayout(this);

                    Button achannelName = new Button(this);
                    achannelName.setText(atvMap.get(key));
                    achannelName.setGravity(Gravity.CENTER);

                    TextView channelNum = new TextView(this);

                    achannel.addView(achannelName);
                    analogTab.addView(achannel);
                }
                //заполняем таб с цифровыми каналами

                Map<Integer,String> dtvMap = cannels.getDigitalNameChannels(this);
                //TextView text = (TextView)findViewById(R.id.textview);
                LinearLayout digitalTab = (LinearLayout) findViewById(R.id.digitalCannels);
                for (Integer key: dtvMap.keySet()){
                    //text.setText(text.getText()+key.toString()+dtvMap.get(key));
                    LinearLayout dchannel = new LinearLayout(this);

                    Button dchannelName = new Button(this);
                    dchannelName.setText(dtvMap.get(key));
                    dchannelName.setGravity(Gravity.CENTER);

                    TextView dchannelNum = new TextView(this);

                    dchannel.addView(dchannelName);
                    digitalTab.addView(dchannel);
                }

               //text.setText(str);
              // text.setText(cannels.getContent());
            }

        }
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
