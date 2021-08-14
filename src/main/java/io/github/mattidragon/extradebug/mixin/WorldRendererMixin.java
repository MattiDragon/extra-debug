package io.github.mattidragon.extradebug.mixin;

import io.github.mattidragon.extradebug.ExtraDebug;
import io.github.mattidragon.extradebug.MatrixStackAction;
import io.github.mattidragon.extradebug.access.MatrixStackData;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "checkEmpty", at = @At(value = "NEW", target = "java/lang/IllegalStateException"))
    private void betterError(MatrixStack matrices, CallbackInfo ci) {
        ExtraDebug.ERROR_LOG.println("Matrix stack error: Pose stack not empty");
        ExtraDebug.ERROR_LOG.println("Methods that might be related:");
        for (MatrixStackAction action : ((MatrixStackData)matrices).getProblems()) {
            ExtraDebug.ERROR_LOG.print(action.isPush() ? "push " : "pop  ");
            ExtraDebug.ERROR_LOG.println(action.method());
        }
    }
    
    @ModifyConstant(method = "checkEmpty")
    private String changeMessage(String message) {
        return "Pose stack not empty".equals(message) ? (message + ". Please check extra-debug.log.") : message;
    }
}
