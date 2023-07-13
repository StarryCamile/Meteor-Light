package qwq.wumie.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import qwq.wumie.mixininterface.IScreen;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin implements IScreen {
    @Shadow @Final private List<Drawable> drawables;

    @Override
    public List<Drawable> getDrawables() {
        return this.drawables;
    }

    @Override
    public ButtonWidget getButtonWidget(Text message) {
        for (Drawable drawable : drawables) {
            if (drawable instanceof ButtonWidget buttonWidget) {
                if (buttonWidget.getMessage().getString().equals(message.getString())) {
                    return buttonWidget;
                }
            }
        }
        return null;
    }


}
