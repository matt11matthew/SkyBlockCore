package net.cloudescape.skyblock.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SkyblockCommandInfo {

    String description();

    String usage();

    int permissionValue() default -1;

    String[] aliases() default {};
}
