package ru.medknc.channelschange;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 08.02.2015.
 */
public class Channels {
    /*
    путь до файла с каналами
     */
    String ChannelPath="";

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

    /*
    XML hashmap для аналоговых каналов
     */
    private Map<Integer,Integer> arXmlAnalogChannel; //порядок перестановки
    private Map<Integer,String> arXmlAnalogChannelContent;
    /*
    XML hashmap для цифровых каналов
     */
    private Map<Integer,Integer> arXmlDigitalChannel; //порядок перестановки
    private Map<Integer,String> arXmlDigitalChannelContent;


    Channels(Context v, String path){
        try {
            this.ChannelPath = path;
            this.content = this.getFileContent(path);
            this.arXmlDigitalChannel = new HashMap<Integer, Integer>();
            this.arXmlAnalogChannel = new HashMap<Integer, Integer>();
            this.arXmlDigitalChannelContent = new HashMap<Integer, String>();
            this.arXmlAnalogChannelContent = new HashMap<Integer, String>();
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
            this.arXmlAnalogChannel.put(Integer.parseInt(matchers.group(3)),Integer.parseInt(matchers.group(3)));
            this.arXmlAnalogChannelContent.put(Integer.parseInt(matchers.group(3)),matchers.group(0));

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
            this.arXmlDigitalChannel.put(Integer.parseInt(matchers.group(3)),Integer.parseInt(matchers.group(3)));
            this.arXmlDigitalChannelContent.put(Integer.parseInt(matchers.group(3)),matchers.group(0));
            channels.put(Integer.parseInt(matchers.group(3)),matchers.group(5));
            //Toast toast1 =  Toast.makeText(v,matchers.group(3),Toast.LENGTH_LONG);
            //toast1.show();
        };
        //
        return channels;
    }

    public void changeAnalogChannels(int fromChannel,int toChannel){
        int tmpChannel = this.arXmlAnalogChannel.get(toChannel);
        this.arXmlAnalogChannel.put(toChannel,this.arXmlAnalogChannel.get(fromChannel));
        this.arXmlAnalogChannel.put(fromChannel,tmpChannel);
    }

    public void changeDigitalChannels(int fromChannel,int toChannel){
        int tmpChannel = this.arXmlDigitalChannel.get(toChannel);
        this.arXmlDigitalChannel.put(toChannel,this.arXmlDigitalChannel.get(fromChannel));
        this.arXmlDigitalChannel.put(fromChannel,tmpChannel);
    }

    public int getMaxKeyMap(Set<Integer> keys){
        //final Integer FIRSTVALUE = 0;
        int max = 0;
        for (int key: keys){
            if ((max == 0) || (key > max)) max = key;
        }
        return max;
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


    public void save(Context v){
        /*
        Делаем замену в соответствующих XML фрагментах
         */
        String atv = "\n";
        for (int i: this.arXmlAnalogChannel.keySet()){
            atv = atv + this.arXmlAnalogChannelContent.get(i).replace ("<prNum>"+Integer.toString(i) +"</prNum>", "<prNum>"+this.arXmlAnalogChannel.get(i).toString()+"</prNum>")+"\n";
            this.arXmlAnalogChannel.put(i,i);
        }
        String dtv = "\n";
        for (int i: this.arXmlDigitalChannel.keySet()){
            dtv = dtv + this.arXmlDigitalChannelContent.get(i).replace ("<prNum>"+Integer.toString(i) +"</prNum>", "<prNum>"+this.arXmlDigitalChannel.get(i).toString()+"</prNum>")+"\n";
            this.arXmlDigitalChannel.put(i,i);
        }
        /*
        Собираем все в одну строку и перезаписываем файл
         */
        String content = this.start + "\n<ATV>" + atv + "</ATV>\n" + "<DTV>" + dtv + "</DTV>\n" + this.end;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(this.ChannelPath));
            out.write(content);
            out.close();
        }
        catch (Exception e){
            Toast toast =  Toast.makeText(v,e.getMessage().toString(),Toast.LENGTH_LONG);
            toast.show();
        }

    }
}
