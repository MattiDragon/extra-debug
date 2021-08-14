package io.github.mattidragon.extradebug.mixin;

import io.github.mattidragon.extradebug.MatrixStackAction;
import io.github.mattidragon.extradebug.access.MatrixStackData;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(MatrixStack.class)
public class MatrixStackMixin implements MatrixStackData {
    @Unique
    private final ArrayList<MatrixStackAction> callers = new ArrayList<>();
    
    @Inject(method = "push", at = @At("HEAD"))
    private void onPush(CallbackInfo ci) {
        handle(true);
    }
    
    @Inject(method = "pop", at = @At("HEAD"))
    private void onPop(CallbackInfo ci) {
        handle(false);
    }
    
    private void handle(boolean isPush) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String method = caller.toString();
        boolean existed = callers.remove(new MatrixStackAction(method, !isPush));
        if (!existed) {
            callers.add(new MatrixStackAction(method, isPush));
        }
    }
    
    @Override
    public MatrixStackAction[] getProblems() {
        return callers.toArray(new MatrixStackAction[0]);
    }
}
