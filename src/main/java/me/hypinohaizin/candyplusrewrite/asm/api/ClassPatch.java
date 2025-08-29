package me.hypinohaizin.candyplusrewrite.asm.api;

public class ClassPatch
{
    public final String className;
    public final String notchName;
    
    public ClassPatch(final String nameIn, final String notchIn) {
        className = nameIn;
        notchName = notchIn;
    }
    
    public byte[] transform(final byte[] bytes) {
        return new byte[0];
    }
    
    public boolean equalName(final String name) {
        return className.equals(name) || notchName.equals(name);
    }
}
