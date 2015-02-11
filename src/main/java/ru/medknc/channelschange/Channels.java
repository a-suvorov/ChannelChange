package ru.medknc.channelschange;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    Channels(Context v, String path){
        try {
            this.content = this.getFileContent(path);

            Pattern p = Pattern.compile("([\\s\\S]*)<ATV>([\\s\\S]*)</ATV>([\\s\\S]*)<DTV>([\\s\\S]*)</DTV>([\\s\\S]*)");
            Matcher matchers = p.matcher(this.content);
            matchers.find();
            this.start = matchers.group(1);
            this.atv = matchers.group(2);
            this.dtv = matchers.group(4);
            this.end = matchers.group(5);

        }
        catch (Exception e){
            Toast toast =  Toast.makeText(v,e.getMessage().toString(),Toast.LENGTH_LONG);
            toast.show();
            //Toast toast1 =  Toast.makeText(v,path,Toast.LENGTH_LONG);
            //toast1.show();
        }
    }

    public String getContent(){
        Pattern p = Pattern.compile("([\\s\\S]*)<ATV>([\\s\\S]*)</ATV>([\\s\\S]*)<DTV>([\\s\\S]*)</DTV>([\\s\\S]*)");
        Matcher matchers = p.matcher(this.content);
        matchers.find();
        return matchers.group(4);
    }

    public Map<Integer,String> getAnalogNameChannels(Context v){
        Map<Integer,String> channels =new HashMap();
        Pattern p = Pattern.compile("(<ITEM>([\\s\\S]*?)<prNum>([\\s\\S]*?)</prNum>([\\s\\S]*?)<vchName>([\\s\\S]*?)</vchName>([\\s\\S]*?)</ITEM>)");
        Matcher matchers = p.matcher(this.atv);
        while(matchers.find()){
            channels.put(Integer.parseInt(matchers.group(3)) ,matchers.group(5));
            //Toast toast1 =  Toast.makeText(v,matchers.group(3),Toast.LENGTH_LONG);
            //toast1.show();
        };
        //
        return channels;
    }

    public Map<Integer,String>  getDigitalNameChannels(Context v){
        Map<Integer,String> channels =new HashMap();
        //знак ? указывает что поиск ленивый и будет искать наименьшее совпадение
        Pattern p = Pattern.compile("(<ITEM>([\\s\\S]*?)<prNum>([\\s\\S]*?)</prNum>([\\s\\S]*?)<vchName>([\\s\\S]*?)</vchName>([\\s\\S]*?)</ITEM>)");
        Matcher matchers = p.matcher(this.dtv);
        while(matchers.find()){
            channels.put(Integer.parseInt(matchers.group(3)),matchers.group(5));
            //Toast toast1 =  Toast.makeText(v,matchers.group(3),Toast.LENGTH_LONG);
            //toast1.show();
        };
        //
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



    private String getFileContent(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        //File F = new File("/sdcard/");
        BufferedReader in = new BufferedReader(new FileReader(path));
        String s;
        while ((s = in.readLine()) != null){
            content.append(s);
            content.append("\n");
        }
        in.close();
        return content.toString();
    }
}
