package me.hypinohaizin.candyplusrewrite.setting;

import java.util.function.Predicate;

public class Setting<T>
{
    public String name;
    public T value;
    public T maxValue;
    public T minValue;
    public Predicate<T> visible;
    
    public Setting(final String name, final T defaultValue, final T maxValue, final T minValue) {
        this.name = name;
        value = defaultValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }
    
    public Setting(final String name, final T defaultValue) {
        this.name = name;
        value = defaultValue;
    }
    
    public Setting(final String name, final T defaultValue, final Predicate<T> visible) {
        this.name = name;
        value = defaultValue;
        this.visible = visible;
    }
    
    public Setting(final String name, final T defaultValue, final T maxValue, final T minValue, final Predicate<T> visible) {
        this.name = name;
        value = defaultValue;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.visible = visible;
    }
    
    public Setting(final String name, final Predicate<T> visible) {
        this.name = name;
        this.visible = visible;
    }
    
    public T getValue() {
        return value;
    }
    
    public boolean visible() {
        return visible == null || visible.test(getValue());
    }
    
    public void setValue(final Object value) {
        this.value = (T)value;
    }
    
    public int currentEnum() {
        return EnumConverter.currentEnum((Enum)value);
    }
    
    public void increaseEnum() {
        value = (T)EnumConverter.increaseEnum((Enum)value);
    }
    
    public void setEnum(final int index) {
        value = (T)EnumConverter.setEnum((Enum)value, index);
    }
}
