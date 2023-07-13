package meteordevelopment.meteorclient.renderer.font;

import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.io.InputStream;

public class Fonts {
    public static TTFFontRenderer font = createFont(getJelloFontRegular(30));
    public static TTFFontRenderer font20 = createFont(getJelloFontRegular(20));

    public static TTFFontRenderer createFont(Font font) {
        return new TTFFontRenderer(font);
    }

    public static Font getJelloFontRegular(float size) {
        Font font;
        try {
            InputStream is = Fonts.class.getResourceAsStream("/assets/meteor-client/fonts/syht.otf");
            assert is != null;
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font,size "+size);
            font = new Font(null, Font.PLAIN, (int)size);
        }
        return font;
    }

    public static void drawCenteredString(TTFFontRenderer font, MatrixStack matrixStack, String text, double x, double y, double w, double h, Color color) {
        double fontWidth = font.getWidth(text);
        double fontHeight = font.getHeight(text);
        double fontX = x+w/2-fontWidth/2;
        double fontY = y+h/2-fontHeight/2;
        font.drawString(matrixStack,text,fontX,fontY,color.getRGB());
    }
}
