/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.gui.jellogui.screens;

import net.minecraft.client.gui.DrawContext;
import qwq.wumie.gui.jellogui.GuiFront;
import meteordevelopment.meteorclient.renderer.*;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.Clipboard;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Deprecated
public class CalcGui extends GuiFront {
    public CalcGui() {
        super("CalcGui");
    }

    ArrayList<NumberButton> numberButtons = new ArrayList<>();
    float x = width - 340;
    float y = height - 650 + 200;
    String text = "0";
    private StringBuilder sBuilder = new StringBuilder();
    double a,b;
    Double sum;
    int i;

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            mc.setScreen(null);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        TextRenderer font = TextRenderer.get();
        numberButtons = new ArrayList<>();
        // 789
        int senOffsetX = (int) (1+x);
        int offsetY = (int) ((y+650-2)-76-76-76-75);
        numberButtons.add(new NumberButton(senOffsetX,offsetY,"7",75,75));
        senOffsetX += 76;
        numberButtons.add(new NumberButton(senOffsetX,offsetY,"8",75,75));
        senOffsetX += 76;
        numberButtons.add(new NumberButton(senOffsetX,offsetY,"9",75,75));
        // 456
        int ffsOffsetX = (int) (1+x);
        offsetY += 76;
        numberButtons.add(new NumberButton(ffsOffsetX,offsetY,"4",75,75));
        ffsOffsetX += 76;
        numberButtons.add(new NumberButton(ffsOffsetX,offsetY,"5",75,75));
        ffsOffsetX += 76;
        numberButtons.add(new NumberButton(ffsOffsetX,offsetY,"6",75,75));
        // 123
        int ottOffsetX = (int) (1+x);
        offsetY += 76;
        numberButtons.add(new NumberButton(ottOffsetX,offsetY,"1",75,75));
        ottOffsetX += 76;
        numberButtons.add(new NumberButton(ottOffsetX,offsetY,"2",75,75));
        ottOffsetX += 76;
        numberButtons.add(new NumberButton(ottOffsetX,offsetY,"3",75,75));
        // 0 - . - +/-
        int zzfOffsetX = (int) (1+x);
        offsetY += 76;
        numberButtons.add(new NumberButton(zzfOffsetX,offsetY,"±",75,75));
        zzfOffsetX += 65;
        numberButtons.add(new NumberButton(zzfOffsetX,offsetY,"0",75,75));
        zzfOffsetX += 65;
        numberButtons.add(new NumberButton(zzfOffsetX,offsetY,".",75,75));

        //+-x/
        int oY = (int) ((y+650-2)-76-76-76-76-75);
        int syX = (int) (1+x);
        numberButtons.add(new NumberButton(syX,oY,"copy",75,75));
        syX += 76;
        numberButtons.add(new NumberButton(syX,oY,"<x",75,75));
        syX += 76;
        String ctext = "c";
        numberButtons.add(new NumberButton(syX,oY,ctext,75,75));
        int idk;
        syX += 76;
        idk = syX;
        numberButtons.add(new NumberButton(syX,oY,"÷",75,75));

        oY += 76;
        numberButtons.add(new NumberButton(idk,oY,"x",75,75));
        oY += 76;
        numberButtons.add(new NumberButton(idk,oY,"-",75,75));
        oY += 76;
        numberButtons.add(new NumberButton(idk,oY,"+",75,75));
        oY += 76;
        numberButtons.add(new NumberButton(idk,oY,"=",75,75));

        double s = mc.getWindow().getScaleFactor();

        mouseX *= s;
        mouseY *= s;

        Utils.unscaledProjection();
        CRender render = new CRender();

        for (NumberButton button : numberButtons) {
            Color numberFontColor = new Color(255,255,255);
            Color buttonColor = button.isHover(mouseX,mouseY) ? new Color(105,105,105,230) : new Color(15,15,15,230);
            double fontWidth = font.getWidth(button.name) / 2;
            double fontHeight = font.getHeight() / 2;

            double fx = button.x + (button.buttonWidth/2 - fontWidth);
            double fy = button.y + (button.buttonHeight/2 - fontHeight);
            font.render(button.name, (float) fx, (float) fy,numberFontColor);
            render.drawRect(new MatrixStack(),button.x, button.y, button.buttonWidth, button.buttonHeight,buttonColor);
        }

        // Bg
        //fill(matrices, (int) x, (int) y,340,650,new Color(255,255,255,190).hashCode());
        // Render numbers
        font.render("Calc",x+1,y+1,new Color(0,0,0,225),true);
        font.render(text,x+1,y+35+35,new Color(0,0,0,225));
        // Render number button
        render.drawRect(new MatrixStack(),(int) x, (int) y,340,650,new Color(255,255,255,190));
        // Drag bar
        render.drawRect(new MatrixStack(),(int) x, (int) (y + 30),340,1,new Color(0,0,0,190));

        Utils.scaledProjection();
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && gameRender.isMouseHoveringRect(x, y,340,30,mouseX,mouseY)) {
            x = (float) mouseX;
            y = (float) mouseY;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (NumberButton clickButton : numberButtons) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                String lab = clickButton.name;
                if(lab.equals("0"))	{
                    sBuilder.append("0");
                    text=(sBuilder.toString());
                }
                if(lab.equals("1"))	{
                    sBuilder.append("1");
                    text=(sBuilder.toString());
                }
                if(lab.equals("2"))	{
                    sBuilder.append("2");
                    text=(sBuilder.toString());
                }
                if(lab.equals("3"))	{
                    sBuilder.append("3");
                    text=(sBuilder.toString());
                }
                if(lab.equals("4"))	{
                    sBuilder.append("4");
                    text=(sBuilder.toString());
                }
                if(lab.equals("5"))	{
                    sBuilder.append("5");
                    text=(sBuilder.toString());
                }
                if(lab.equals("6"))	{
                    sBuilder.append("6");
                    text=(sBuilder.toString());
                }
                if(lab.equals("7"))	{
                    sBuilder.append("7");
                    text=(sBuilder.toString());
                }
                if(lab.equals("8"))	{
                    sBuilder.append("8");
                    text=(sBuilder.toString());
                }
                if(lab.equals("9"))	{
                    sBuilder.append("9");
                    text=(sBuilder.toString());
                }
                if(lab.equals("c"))	{
                    sBuilder = new StringBuilder();
                    text="0";
                }
                if(lab.equals("."))	{
                    sBuilder.append(".");
                    text=sBuilder.toString();
                }
                if (!sBuilder.toString().isEmpty()) {
                    if (lab.equals("+")) {
                        a = Double.parseDouble(sBuilder.toString());
                        i = 0;
                        sBuilder = new StringBuilder();
                        text = "+";
                    }
                    if (lab.equals("-")) {
                        a = Double.parseDouble(sBuilder.toString());
                        i = 1;
                        sBuilder = new StringBuilder();
                        text = "-";
                    }
                    if (lab.equals("x")) {
                        a = Double.parseDouble(sBuilder.toString());
                        i = 2;
                        sBuilder = new StringBuilder();
                        text = "x";
                    }
                    if (lab.equals("÷")) {
                        a = Double.parseDouble(sBuilder.toString());
                        i = 3;
                        sBuilder = new StringBuilder();
                        text = "÷";
                    }
                    if (lab.equals("=")) {
                        b = Double.parseDouble(sBuilder.toString());
                        if (i == 0) {
                            sum = a + b;
                            text = sum.toString();
                            sBuilder = new StringBuilder();
                            sBuilder.append(sum);
                        }
                        if (i == 1) {
                            sum = a - b;
                            text = (sum.toString());
                            sBuilder = new StringBuilder();
                            sBuilder.append(sum);
                        }
                        if (i == 2) {
                            sum = a * b;
                            text = (sum.toString());
                            sBuilder = new StringBuilder();
                            sBuilder.append(sum);
                        }
                        if (i == 3) {
                            sum = a / b;
                            text = (sum.toString());
                            sBuilder = new StringBuilder();
                            sBuilder.append(sum);
                        }
                    }
                    if (lab.equals("copy")) {
                        Clipboard clipboard = new Clipboard();
                        clipboard.setClipboard(mc.getWindow().getHandle(), text);
                    }
                    if (lab.equals("<x")) {
                        sBuilder.setLength(sBuilder.length() - 1);
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    class NumberButton {
        public String name;
        public int buttonWidth;
        public int buttonHeight;
        public int x;
        public int y;

        public NumberButton(int x,int y,String name,int width,int height) {
            this.name = name;
            this.buttonWidth = width;
            this.buttonHeight = height;
            this.x = x;
            this.y = y;
        }

        public boolean isHover(double mouseX,double mouseY) {
            return gameRender.isMouseHoveringRect(x,y, buttonWidth, buttonHeight,mouseX,mouseY);
        }
    }
}
