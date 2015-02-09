package ru.medknc.channelschange;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 08.02.2015.
 */
public class Channels {
    /*
    содержимое XML-файла
     */
    private String content;

    /*
    начало XML-файла - блок до аналоговых каналов
     */
    private String start;

    /*
    блок XML-файла с аналоговыми каналами
     */
    private String atv;

    /*
    блок XML-файла с цифровыми каналами
     */
    private String dtv;

    /*
    конец XML-файла - блок после цифровых каналов
     */
    private String end;

    Channels(Context v){
        try {
            this.content = this.getFileContent();

            Pattern p = Pattern.compile("([\\s\\S]*)<ATV>([\\s\\S]*)</ATV>([\\s\\S]*)<DTV>([\\s\\S]*)</DTV>([\\s\\S]*)");
            Matcher matchers = p.matcher(content);
            this.start = matchers.group(1);
            this.atv = matchers.group(2);
            this.dtv = matchers.group(4);
            this.end = matchers.group(5);
        }
        catch (Exception e){
            Toast toast =  Toast.makeText(v,"FileNotFound",Toast.LENGTH_LONG);
            toast.show();
            Toast toast1 =  Toast.makeText(v,System.getProperty("user.dir"),Toast.LENGTH_LONG);
            toast1.show();
        }
    }

    public ArrayList<String> getAnalogNameChannels(){
        ArrayList<String> channels =new ArrayList<String>();
        //
        return channels;
    }

    public ArrayList<String> getDigitalNameChannels(){
        ArrayList<String> channels =new ArrayList<String>();

        return channels;
    }

    public ArrayList<String> getAnalogXmlChannels(){
        ArrayList<String> channels =new ArrayList<String>();

        return channels;
    }

    public ArrayList<String> getDigitalXmlChannels(){
        ArrayList<String> channels =new ArrayList<String>();

        return channels;
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
}
