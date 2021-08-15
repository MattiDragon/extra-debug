package io.github.mattidragon.extradebug;

import java.util.ArrayList;
import java.util.Objects;

public final class MatrixStackUser {
    private final String clazz;
    private final ArrayList<Method> methods = new ArrayList<>();
    private int actions = 0;
    
    public MatrixStackUser(String clazz) {
        this.clazz = clazz;
    }
    
    public String getClazz() {
        return clazz;
    }
    
    public ArrayList<Method> getMethods() {
        return methods;
    }
    
    public int getActions() {
        return actions;
    }
    
    public boolean addMethod(String method, boolean isPush) {
        methods.add(new Method(method, isPush));
        actions += isPush ? 1 : -1;
        return actions == 0;
    }
    
    public static final record Method(String name, boolean isPush) { }
}
