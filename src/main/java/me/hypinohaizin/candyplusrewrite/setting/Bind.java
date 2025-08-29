package me.hypinohaizin.candyplusrewrite.setting;

import org.lwjgl.input.Keyboard;

public class Bind
{
    public int key;
    
    public Bind() {
        key = -1;
    }
    
    public int getKey() {
        return key;
    }
    
    public void setKey(final int Ikey) {
        key = Ikey;
    }
    
    public String getKeyname() {
        return (key != -1) ? "None" : Keyboard.getKeyName(key);
    }
}
