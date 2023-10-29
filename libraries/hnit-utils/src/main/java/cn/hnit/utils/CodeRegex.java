package cn.hnit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*@author created by bond
*@date  2019年7月2日---下午7:13:12
*/
public class CodeRegex {
	
	public static boolean CodeMatcher(String mc){
        //匹配6位顺增
        String pattern = "(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){5}\\d";
        Pattern pa = Pattern.compile(pattern);		
        Matcher ma = pa.matcher(mc);
        if(ma.matches()){
        	return true;
        }
        //匹配6位顺降
        pattern = "(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){5}\\d";		
        pa = Pattern.compile(pattern);		
        ma = pa.matcher(mc);
        if(ma.matches()){
        	return true;
        }		
        //匹配连同号如“abbabb”
        pattern = "(\\d)(\\d)\\2\\1\\2\\2$";
        pa = Pattern.compile(pattern);		
        ma = pa.matcher(mc);
        if(ma.matches()){
        	return true;
        }		
        
        /** 匹配成对出现的 2233*/
         pattern = "^.*([\\d])\\1{1,}([\\d])\\2{1,}.*$";
         pa = Pattern.compile(pattern);
         ma = pa.matcher(mc);
         if(ma.matches()){
         	return true;
         }        
         /** 匹配成对出现的 3位升降*/
         pattern = "^.*(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){2}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){2})\\d.*$";
         pa = Pattern.compile(pattern);
         ma = pa.matcher(mc);
         if(ma.matches()){
        	 return true;
         }        
         /** 匹配3位以上的重复数字 eg:333 */
         pattern = "^.*(.)\\1{2}.*$";  
         pa = Pattern.compile(pattern);
         ma = pa.matcher(mc);
         if(ma.matches()){
        	 return true;
         }        
         /** 匹配ABAB*/
         pattern = "^.*([\\d][\\d])\\1{1,}.*$";
         pa = Pattern.compile(pattern);
         ma = pa.matcher(mc);
         if(ma.matches()){
        	 return true;
         }        
        
         pattern = "^520.*$";
         pa = Pattern.compile(pattern);
         ma = pa.matcher(mc);
         if(ma.matches()){
        	 return true;
         }        
         pattern = "^1314.*$";
         pa = Pattern.compile(pattern);
         ma = pa.matcher(mc);
        return ma.matches();

    }
	
	 public static String getCode(){
		 Integer roomId=(int)((Math.random()*9+1)*100000);
		 if(CodeMatcher(String.valueOf(roomId))){
			 getCode(); 
		 }
		return String.valueOf(roomId);
	 }
}


