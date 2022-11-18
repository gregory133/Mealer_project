package com;

import static org.junit.Assert.*;

import com.example.homepageactivity.domain.Validator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestValidator {
    @Test
    public void validateName() {
        Validator val = new Validator();
        assertEquals("invalid password", true, val.isAlphabetic("EricEaton"));
        //assertEquals("invalid password", true, val.isAlphabetic("Eric Eaton"));   //has ' '
    }
    @Test
    public void validatePassword() {
        Validator val = new Validator();
        assertEquals("invalid password", true, val.checkValidPassword("45y945wu*&^%$7o8ton"));
        //assertEquals("invalid password", true, val.checkValidPassword("erere\0erer"));    //has \0
    }
    @Test
    public void validateEmail() {
        Validator val = new Validator();
        assertEquals("invalid password", true, val.checkValidEmail("45@eo.ere.e"));
        assertEquals("invalid password", true, val.checkValidEmail("45\"][\"45@eo.ere.e"));
        //assertEquals("invalid password", true, val.checkValidEmail("]45@eo.ere.e"));      // has ] outside of ""
    }
    @Test
    public void validateGetIntLength() {
        Validator val = new Validator();
        assertEquals("invalid password", 3, val.getIntLength(343));
        //assertEquals("invalid password", 3, val.getIntLength(3434));      //not 3 numbers long
    }
    @Test
    public void validateIsAlphanumericPhrase() {
        Validator val = new Validator();
        assertEquals("invalid Alphanumeric Phrase", true, val.isAlphanumericPhrase("This Has, It's #1 Problem"));
        //assertEquals("invalid password", 3, val.getIntLength(3434));      //not 3 numbers long
    }
    @Test
    public void validateGetStringIndex() {
        List<String> list = Arrays.asList("Appetizer", "Entree", "Dessert", "Other");
        Validator val = new Validator();
        assertEquals("incorrect string index", 3, val.getStringIndex(list, "Other"));
        //assertEquals("invalid password", 3, val.getIntLength(3434));      //not 3 numbers long
    }
    @Test
    public void validateGetStringLength() {
        Validator val = new Validator();
        assertEquals("incorrect string length", 7, val.getStringLength("1234567"));
        //assertEquals("invalid password", 3, val.getIntLength(3434));      //not 3 numbers long
    }
    @Test
    public void validateIsPhrase() {
        Validator val = new Validator();
        assertEquals("not a phrase", true, val.isPhrase("A phrase has some symbols and, it's"));
        //assertEquals("invalid password", 3, val.getIntLength(3434));      //not 3 numbers long
    }
}