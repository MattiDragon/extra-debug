package io.github.mattidragon.extradebug.mixin;

import io.github.mattidragon.extradebug.ExtraDebug;
import io.github.mattidragon.extradebug.MatrixStackUser;
import io.github.mattidragon.extradebug.access.MatrixStackData;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.mattidragon.extradebug.ExtraDebug.ERROR_LOG;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "checkEmpty", at = @At(value = "NEW", target = "java/lang/IllegalStateException"))
    private void betterError(MatrixStack matrices, CallbackInfo ci) {
        ERROR_LOG.println("Matrix stack error: Pose stack not empty");
        ERROR_LOG.println("Suspected classes:");
        
        for (MatrixStackUser user : ((MatrixStackData)matrices).getUsers()) {
            ERROR_LOG.printf("  %s: %s %s%n", user.getClazz(), Math.abs(user.getActions()), user.getActions() < 0 ? "pops" : "pushes");
            ERROR_LOG.println("    Suspected methods:");
            
            for (MatrixStackUser.Method method : user.getMethods()) {
                ERROR_LOG.println("      " + (method.isPush() ? "push " : "pop  ") + method.name());
            }
        }
    }
    
    @ModifyConstant(method = "checkEmpty")
    private String changeMessage(String message) {
        return "Pose stack not empty".equals(message) ? (message + ". Please check extra-debug.log") : message;
    }
}
