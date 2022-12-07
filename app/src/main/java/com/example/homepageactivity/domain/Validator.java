package com.example.homepageactivity.domain;

import static java.sql.DriverManager.println;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//String firstName, String lastName, String email, String address, String password, String confirmPassword
public class Validator {
    public static int getIntLength(int i){
        return (int) (Math.log10(i) + 1);
    }

    public static int getLongLength(long i){
        return (int) (Math.log10(i) + 1);
    }

    public boolean isAlphabetic(String word){
        if(word.length() <= 0){return false;}
        char[] chars = word.toCharArray();
        int i = 0;

        while(i < chars.length){
            if("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".indexOf(chars[i]) >= 0){
                i++;
                continue;
            }else{
                return false;       //not a valid pre-@ character
            }
        }
        return true;
    }

    public boolean isAlphanumeric(String word){
        if(word.length() <= 0){return false;}
        char[] chars = word.toCharArray();
        int i = 0;

        while(i < chars.length){
            if("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".indexOf(chars[i]) >= 0){
                i++;
                continue;
            }else{
                return false;       //not a valid pre-@ character
            }
        }
        return true;
    }

    public boolean checkValidEmail(String email){
        //https://en.wikipedia.org/wiki/Email_address
        if(email.length() <= 0){return false;}
        char[] emailChars = email.toCharArray();
        int i = 0;
        boolean inQuotes = false;
        if (emailChars[0] == '0'){return false;}
        while(emailChars[i] != '@'){
            if(emailChars[i] == '"'){
                inQuotes = !inQuotes;
            }else if("(),:;<>@[\\]".indexOf(emailChars[i]) >= 0 && !inQuotes){     //these chars are allowed only between quotes
                    return false;
            }else if(emailChars[i] == '.'){
                if(i == 0){                         //first char cannot be '.'
                    return false;
                }else if(emailChars[i-1] == '.'){   //cannot contain substring ".."
                    return false;
                }
            }
            if("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&'*+-/=?^_`{|}~\"(),.:;<>@[\\]".indexOf(emailChars[i]) >= 0){
                i++;
                if(i >= emailChars.length){return false;}      //.com has not been reached
                continue;
            }else{
                return false;       //not a valid pre-@ character
            }
        }

        if (inQuotes){return false;}    //illegal state where special characters are not guarded by quotes
        if(i == 1){return false;}       //nothing before @
        i++;    //get past '@'
        if (i >= emailChars.length){return false;}

        boolean hasDot = false;

        while(i < emailChars.length){
            if(emailChars[i] == '.'){
                hasDot = true;
                if(i >= emailChars.length-1){return false;}     //can have multiple '.', but not at the end
            }
            if("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.".indexOf(emailChars[i]) >= 0){
                i++;
                continue;
            }else{
                return false;       //not a valid pre-@ character
            }
        }
        return hasDot;      //all other checks are passed, so the final check is returned
    }

    public boolean checkValidPassword(String pass){
        if(pass.length() <= 0){return false;}
        char[] chars = pass.toCharArray();
        int i = 0;

        while(i < chars.length){
            if("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`! @#$%^&*()_-+={[}]|\\:;\"'<,>.?/".indexOf(chars[i]) >= 0){
                i++;
                continue;
            }else{
                return false;       //not a valid character
            }
        }
        return true;
    }

    public int getStringLength(String str){
        return str.length();
    }

    public boolean isPhrase(String phrase){
        if(phrase.length() <= 0){return false;}
        char[] chars = phrase.toCharArray();
        int i = 0;

        while(i < chars.length){
            if("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz', -".indexOf(chars[i]) >= 0){
                i++;
                continue;
            }else{
                return false;
            }
        }
        return true;
    }

    public boolean isAlphanumericPhrase(String phrase){
        if(phrase.length() <= 0){return false;}
        char[] chars = phrase.toCharArray();
        int i = 0;

        while(i < chars.length){
            if("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,:' .@#".indexOf(chars[i]) >= 0){
                i++;
                continue;
            }else{
                return false;
            }
        }
        return true;
    }

    public int getStringIndex(List<String> options, String selection){
        return options.indexOf(selection);
    }

    public boolean checkValidExpYear(int expiryYear){
        return !(expiryYear < 2022 || 2030 < expiryYear);
    }

    public boolean checkValidExpMonth(int expiryMonth) {
        return !(expiryMonth > 12 || expiryMonth < 1);
    }

    public boolean checkMatchingFields(String field1, String field2){
        return field1.equals(field2);
    }
}
