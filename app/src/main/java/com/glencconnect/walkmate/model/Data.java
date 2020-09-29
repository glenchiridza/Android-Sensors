package com.glencconnect.walkmate.model;


import java.util.Random;

/**
 * Created by glenc on 26 Sept 2020
 **/
public class Data {

    private static String[] complaint = {
      "What the hell",
      "What are you doing to me",
      "What the hack",
            "Please Stop it",
    };

    private static String[] concession = {
            "That's my boy, That's my boy",
            "Never do that again mate",
            "Good move",
            "If you hadn't stopped, l would have shut down this phone"
    };


    public static String getComplaint(){
        int size = complaint.length;
        Random random = new Random();
        int dataNext = random.nextInt(size);
        return complaint[dataNext];
    }

    public static String getConcession(){
        int size = concession.length;
        Random random = new Random(size);
        int dataNext = random.nextInt(size);
        return concession[dataNext];

    }


}
