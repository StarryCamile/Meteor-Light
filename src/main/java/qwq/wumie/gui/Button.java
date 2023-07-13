package qwq.wumie.gui;


import meteordevelopment.meteorclient.renderer.CRender;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class Button {
    public double width;
    public double height;
    public double x;
    public double y;
    public double buttonWidth;
    public double buttonHeight;
    public Color color;
    public boolean hoverd,inMove;
    public int brightness = 100;
    public int moveProgress = 0;

    public String text;

    public Button(String text,double width, double height, double buttonWidth, double buttonHeight) {
        this.text = text;
        this.width = width;
        this.height = height;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
    }

    public boolean isHoverd(double extendX,double extendY,double mouseX,double mouseY) {
        this.hoverd = isMouseHovering(x,y,extendX,extendY,mouseX,mouseY);
        return isMouseHovering(x,y,extendX,extendY,mouseX,mouseY);
    }

    public boolean isHoverd(double mouseX,double mouseY) {
        return isHoverd(buttonWidth,buttonHeight,mouseX,mouseY);
    }

    private boolean isMouseHovering(double x, double y, double width, double height, double mouseX, double mouseY){
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public Button update(double width,double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public Button setColor(Color color) {
        this.color = color;
        return this;
    }

    public double getX() {
        return x;
    }

    public Button setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return y;
    }

    public Button setY(double y) {
        this.y = y;
        return this;
    }

    public double getButtonWidth() {
        return buttonWidth;
    }

    public Button setButtonWidth(double buttonWidth) {
        this.buttonWidth = buttonWidth;
        return this;
    }

    public double getButtonHeight() {
        return buttonHeight;
    }

    public Button setButtonHeight(double buttonHeight) {
        this.buttonHeight = buttonHeight;
        return this;
    }

    public void renderWithHover(double mouseX,double mouseY) {
        if (isMouseHovering(x,y,buttonWidth,buttonHeight, mouseX, mouseY)) {
            if (brightness > 90) {
                brightness--;
            }
            if (brightness > 90) {
                brightness--;
            }
        } else {
            if (brightness < 100) {
                brightness++;
            }
            if (brightness < 100) {
                brightness++;
            }
        }
        setColor(new Color(java.awt.Color.HSBtoRGB(212 / 360f, 76 / 100f, brightness / 100f)));
        CRender.Immediate.drawRect(x,y,buttonWidth,buttonHeight,color,true);
    }
}
