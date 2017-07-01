package cn.snzo.utils;

import java.util.Random;

/**
 * Created by chentao on 2017/2/24 0024.
 */
public class RandomUtils {

    /**
     * 生成随机数(包含数字和字母)
     * @param length 随机数长度
     * @return 随机数
     */

    public static String getRandom(int length, boolean containsLetter){

        String  buffer = containsLetter ? "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" : "0123456789" ;
        StringBuffer saltStr = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();

        for(int i = 0;i < length;i++){
            saltStr.append(buffer.charAt(random.nextInt(range)));
        }

        return saltStr.toString();
    }


    /**
     * 生成随机数(纯数字)
     * @param length 随机数长度
     * @return 随机数
     */
    public static String getRandomNum(int length){
        return getRandom(length, false);
    }

//    public static void main(String[] args) {
//
//        System.out.println(getRandom(6, true));
//    }
}
