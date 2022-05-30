package com.tosky.board2.util;

public class Utility {
    public static boolean strToInt(String pageNumStr){
        try{
            Double.parseDouble(pageNumStr);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
}