package me.hypinohaizin.candyplusrewrite.asm.api;

public class MappingName
{
    private final String mappingName;
    private final String notchName;
    private final String originName;
    
    public MappingName(final String mappingName, final String notchName, final String originName) {
        this.mappingName = mappingName;
        this.notchName = notchName;
        this.originName = originName;
    }
    
    public boolean equalName(final String name) {
        return mappingName.equals(name) || notchName.equals(name) || originName.equals(name);
    }
}
