package qwq.wumie.mixininterface;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public interface IScreen {
    List<Drawable> getDrawables();

    ButtonWidget getButtonWidget(Text message);
}
