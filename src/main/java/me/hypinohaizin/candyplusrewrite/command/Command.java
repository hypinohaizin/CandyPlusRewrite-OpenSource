package me.hypinohaizin.candyplusrewrite.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import net.minecraft.client.Minecraft;

/**
 * @author h_ypi
 * @since 2025/08/04 15:25
 */
public abstract class Command {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface Declaration {
        String name();
        String syntax();
        String[] alias() default {};
    }

    protected static final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private final String[] alias;
    private final String syntax;

    public Command() {
        Declaration dec = getClass().getAnnotation(Declaration.class);
        this.name = dec.name();
        this.alias = dec.alias();
        this.syntax = dec.syntax();
    }

    public String getName() {
        return name;
    }

    public String[] getAlias() {
        return alias;
    }

    public String getSyntax() {
        return CommandManager.getCommandPrefix() + syntax;
    }

    public abstract void onCommand(String label, String[] args, boolean isClientSide);
}
