package io.github.mattidragon.extradebug.mixin;

import io.github.mattidragon.extradebug.ExtraDebug;
import io.github.mattidragon.extradebug.MatrixStackUser;
import io.github.mattidragon.extradebug.access.MatrixStackData;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

@Mixin(MatrixStack.class)
public class MatrixStackMixin implements MatrixStackData {
    @Unique
    private final ArrayList<MatrixStackUser> users = new ArrayList<>();
    
    @Inject(method = "push", at = @At("HEAD"))
    private void onPush(CallbackInfo ci) {
        handle(true);
    }
    
    @Inject(method = "pop", at = @At("HEAD"))
    private void onPop(CallbackInfo ci) {
        handle(false);
    }
    
    private void handle(boolean isPush) {
        StackWalker walker = StackWalker
                .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        Method method = walker.walk(frames -> frames
                .map(frame -> {
                    try {
                        return frame.getDeclaringClass().getDeclaredMethod(frame.getMethodName(), frame.getMethodType().parameterArray());
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                })
                .skip(3)
                .findFirst())
                .orElseThrow();
        
        
        MatrixStackUser user = null;
        for (int i = users.size() - 1; i >= 0; i--) {
            MatrixStackUser candidate = users.get(i);
            if (Objects.equals(candidate.getClazz(), method.getDeclaringClass().getTypeName())) {
                user = candidate;
                break;
            }
        }
        
        if (user == null) users.add(user = new MatrixStackUser(method.getDeclaringClass().getTypeName()));
        
        String name = method.getName();
        MixinMerged mixin = method.getAnnotation(MixinMerged.class);
        if (mixin != null) {
            name += " from mixin " + mixin.mixin();
        }
        
        if (user.addMethod(name, isPush))
            users.remove(user);
    }
    
    @Override
    public MatrixStackUser[] getUsers() {
        return users.toArray(new MatrixStackUser[0]);
    }
}
