package com.example.laserusbdemo;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class ConversionUtils {
    private static final String UTF_8 = "UTF-8";
    public static String hexString = "0123456789ABCDEF";
    private final static char[] HEX_ARRAY = hexString.toCharArray();

    public static String bytesToString(byte[] data) {
        if (data != null) {
            try {
                return new String(data, UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static byte[] stringToBytes(String data) {
        return data.getBytes(Charset.forName(UTF_8));
    }

    public static int bytesToInt(byte[] data) {
        return ((Byte) data[data.length - 1]).intValue();
    }

    public static byte[] hexToByte(String hex) {
        int len = hex.length();
        byte[] value = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            value[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return value;
    }

    public static byte integerToUnsignedByte(int value) {
        byte signedByte = (byte) value;
        return (byte) (signedByte & 0xFF);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] concatBytes(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static String decode(String bytes){
        ByteArrayOutputStream baos=new ByteArrayOutputStream(bytes.length()/2);
        for(int i=0;i<bytes.length();i+=2) {
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        }
        return new String(baos.toByteArray()).toUpperCase();
    }

    public static String encode(String str){
        byte[] bytes=str.getBytes();
        StringBuilder sb=new StringBuilder(bytes.length*2);
        for(int i=0;i<bytes.length;i++){
            sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    public static String Dec2BinStr(int data, int length){
        String binary = "";
        int dec = Math.abs(data);

        //Convert to 2's
        while (dec>0){
            binary = String.valueOf(dec%2) + binary;
            if (dec/2 < 1){
                break;
            }
            dec = dec/2;
        }
        //fill 0 if it needs
        if (binary.length() <= length){
            String b = "";
            for (int i = 0; i < length - 1 - binary.length(); i++){
                b = b + "0";
            }
            binary = b + binary;
        }
        //+- signal
        if (data>0) {
            binary = "0" + binary;
        }else{
            binary = "1" + binary;
        }
        return binary;
    }

    public static double BinStr2Dec(int[] data, int length, int[] Cnt){
        String binary = "";
        for (int i = Cnt[0]; i<Cnt[0]+length; i++) {
            if (data[i]>400) {
                binary = binary + "1";
            }else{
                binary = binary + "0";
            }
        }
        double decimal = 0;
        for (int i=0; i<binary.length()-1; i++) {
            if (binary.substring(binary.length()-i-1, binary.length()-i).equals("1")){
                decimal += Math.pow(2, i);
            }
        }
        if (binary.substring(0, 1).equals("1")){
            decimal = -decimal;
        }

        Cnt[0] = Cnt[0] + length;
        return decimal;
    }
}
