package meteordevelopment.meteorclient.renderer.font;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.*;
import qwq.wumie.utils.render.RenderUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.lwjgl.opengl.GL11.*;

/**
 * Fixed by WuMie on 3/11/2023.
 */
public class TTFFontRenderer {

    /**
     * The font to be drawn.
     */
    private Font font;

    /**
     * If fractional metrics should be used in the font renderer.
     */
    private boolean fractionalMetrics = false;

    /**
     * All the character data information (regular).
     */
    private CharacterData[] regularData;

    /**
     * All the character data information (bold).
     */
    private CharacterData[] boldData;

    /**
     * All the character data information (italics).
     */
    private CharacterData[] italicsData;

    /**
     * All the color codes used in minecraft.
     */
    private int[] colorCodes = new int[32];

    /**
     * The margin on each texture.
     */
    private static final int MARGIN = 4;

    /**
     * The character that invokes color in a string when rendered.
     */
    private static final char COLOR_INVOKER = '\247';

    /**
     * The random offset in obfuscated text.
     */
    private static int RANDOM_OFFSET = 1;

    private static List<Character> loadedChars = new ArrayList<>();

    public TTFFontRenderer(Font font) {
        this(font, 0);
    }

    public TTFFontRenderer(Font font, int characterCount) {
        this(font, characterCount, true);
    }

    public TTFFontRenderer(Font font, boolean fractionalMetrics) {
        this(font, 256, fractionalMetrics);
    }

    public TTFFontRenderer(Font font, int characterCount, boolean fractionalMetrics) {
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        for (int index = 0; index < characterCount; index++) {
            if (!loadedChars.contains((char) index)) {
                char character = (char) index;
                loadedChars.add(character);
            }
        }

        addCanRenderChar("你干嘛哼哼哎哟");

        // Generates all the character textures.
        this.regularData = setup(loadedChars.toArray(new Character[0]), Font.PLAIN);
        this.boldData = setup(loadedChars.toArray(new Character[0]), Font.BOLD);
        this.italicsData = setup(loadedChars.toArray(new Character[0]), Font.ITALIC);
    }

    public void addCanRenderChar(String text) {
        addCanRenderChar(text.toCharArray());
    }

    public void addCanRenderChar(char... chars) {
        for (char c : chars) {
            if (!loadedChars.contains(c)) {
                loadedChars.add(c);
            }
        }
    }

    private void addChar(CharacterData[] characterData, Character[] characters, int type) {
        // Quickly generates the colors.

        generateColors();

        // Changes the type of the font to the given type.
        Font font = this.font.deriveFont(type);

        // An image just to get font data.
        BufferedImage utilityImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        // The graphics of the utility image.
        Graphics2D utilityGraphics = (Graphics2D) utilityImage.getGraphics();

        // Sets the font of the utility image to the font.
        utilityGraphics.setFont(font);

        // The font metrics of the utility image.
        FontMetrics fontMetrics = utilityGraphics.getFontMetrics();

        // Iterates through all the characters in the character set of the font renderer.
        for (char character : characters) {
            // The width and height of the character according to the font.
            Rectangle2D characterBounds = fontMetrics.getStringBounds(character + "", utilityGraphics);

            // The width of the character texture.
            float width = (float) characterBounds.getWidth() + (2 * MARGIN);

            // The height of the character texture.
            float height = (float) characterBounds.getHeight();

            // The image that the character will be rendered to.
            BufferedImage characterImage = new BufferedImage((int) Math.ceil(width), (int) Math.ceil(height), BufferedImage.TYPE_INT_ARGB);

            // The graphics of the character image.
            Graphics2D graphics = (Graphics2D) characterImage.getGraphics();

            // Sets the font to the input font/
            graphics.setFont(font);

            // Sets the color to white with no alpha.
            graphics.setColor(new Color(255, 255, 255, 0));

            // Fills the entire image with the color above, makes it transparent.
            graphics.fillRect(0, 0, characterImage.getWidth(), characterImage.getHeight());

            // Sets the color to white to draw the character.
            graphics.setColor(Color.WHITE);

            // Enables anti-aliasing so the font doesn't have aliasing.
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

            // Draws the character.
            graphics.drawString(character + "", MARGIN, fontMetrics.getAscent());

            // Generates a new texture id.
            int textureId = GlStateManager._genTexture();

            // Allocates the texture in opengl.
            createTexture(textureId, characterImage);

            // Initiates the character data and stores it in the data array.
            characterData[character] = new CharacterData(character, characterImage.getWidth(), characterImage.getHeight(), textureId);
            //Light.instance.logger.info("TTFFont: Register Char: " + characters[0] + " TID: " + textureId + "Type: " + type);
            /*try {
                ImageIO.write(characterImage,"PNG",new File("run/font_charId="+index+"txt="+character+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    /**
     * Sets up the character data and textures.
     *
     * @param characters The array of character data that should be filled.
     * @param type       The font type. (Regular, Bold, and Italics)
     */
    private CharacterData[] setup(Character[] characters, int type) {
        // Quickly generates the colors.
        generateColors();
        // Changes the type of the font to the given type.
        Font font = this.font.deriveFont(type);

        // An image just to get font data.
        BufferedImage utilityImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

        // The graphics of the utility image.
        Graphics2D utilityGraphics = utilityImage.createGraphics();

        // Sets the font of the utility image to the font.
        utilityGraphics.setFont(font);

        // The font metrics of the utility image.
        FontMetrics fontMetrics = utilityGraphics.getFontMetrics();

        CharacterData[] characterData = new CharacterData['\uffff'];

        // Iterates through all the characters in the character set of the font renderer.
        for (int index = 0; index < characters.length; index++) {
            // The character at the current index.
            char character = characters[index];

            // The width and height of the character according to the font.
            Rectangle2D characterBounds = fontMetrics.getStringBounds(character + "", utilityGraphics);

            // The width of the character texture.
            float width = (float) characterBounds.getWidth() + (2 * MARGIN);

            // The height of the character texture.
            float height = (float) characterBounds.getHeight();

            // The image that the character will be rendered to.
            BufferedImage characterImage = new BufferedImage((int) Math.ceil(width), (int) Math.ceil(height), BufferedImage.TYPE_INT_ARGB);

            // The graphics of the character image.
            Graphics2D graphics = characterImage.createGraphics();

            // Sets the font to the input font/
            graphics.setFont(font);

            // Sets the color to white with no alpha.
            graphics.setColor(new Color(255, 255, 255, 0));

            // Fills the entire image with the color above, makes it transparent.
            graphics.fillRect(0, 0, characterImage.getWidth(), characterImage.getHeight());

            // Sets the color to white to draw the character.
            graphics.setColor(Color.WHITE);

            // Enables anti-aliasing so the font doesn't have aliasing.
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

            // Draws the character.
            graphics.drawString(String.valueOf(character), MARGIN, fontMetrics.getAscent());

            // Generates a new texture id.
            int textureId = GlStateManager._genTexture();

            // Allocates the texture in opengl.
            createTexture(textureId, characterImage);

            // Initiates the character data and stores it in the data array.
            characterData[index] = new CharacterData(character, characterImage.getWidth(), characterImage.getHeight(), textureId);
            /*try {
                ImageIO.write(characterImage,"PNG",new File("run/font_charId="+index+"txt="+character+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        // Returns the filled character data array.
        return characterData;
    }

    /**
     * Uploads the opengl texture.
     *
     * @param textureId The texture id to upload to.
     * @param image     The image to upload.
     */
    private void createTexture(int textureId, BufferedImage image) {
        // Array of all the colors in the image.
        int[] pixels = new int[image.getWidth() * image.getHeight()];

        // Fetches all the colors in the image.
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        // Buffer that will store the texture data.
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

        // Puts all the pixel data into the buffer.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                // The pixel in the image.
                int pixel = pixels[y * image.getWidth() + x];

                // Puts the data into the byte buffer.
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        // Flips the byte buffer, not sure why this is needed.
        buffer.flip();

        // Binds the opengl texture by the texture id.
        GlStateManager._bindTexture(textureId);

        // Sets the texture parameter stuff.
        RenderUtil.doFixTexture();

        // Uploads the texture to opengl.
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer.asIntBuffer());

        // Binds the opengl texture 0.
        GlStateManager._bindTexture(0);
    }


    /**
     * Renders the given string.
     *
     * @param text  The text to be rendered.
     * @param x     The x position of the text.
     * @param y     The y position of the text.
     * @param color The color of the text.
     */
    public void drawString(MatrixStack matrices,String text, float x, float y, int color) {
        renderString(matrices,text, x, y, color, false);
    }

    public void drawString(MatrixStack matrices,String text, double x, double y, int color) {
        renderString(matrices,text, (float) x, (float) y, color, false);
    }

    /**
     * Renders the given string.
     *
     * @param text  The text to be rendered.
     * @param x     The x position of the text.
     * @param y     The y position of the text.
     * @param color The color of the text.
     */
    public void drawStringWithShadow(MatrixStack matrices,String text, float x, float y, int color) {
        matrices.translate(0.5, 0.5, 0);
        renderString(matrices,text, x, y, color, true);
        matrices.translate(-0.5, -0.5, 0);
        renderString(matrices,text, x, y, color, false);
    }

    /**
     * Renders the given string.
     *
     * @param text   The text to be rendered.
     * @param x      The x position of the text.
     * @param y      The y position of the text.
     * @param shadow If the text should be rendered with the shadow color.
     * @param color  The color of the text.
     */
    private void renderString(MatrixStack matrices, String text, float x, float y, int color, boolean shadow) {
        // Returns if the text is empty.
        if (text.length() == 0) return;

        SMesh.BMesh bMesh = new SMesh.BMesh();
        bMesh.setMatrix(matrices);
        // Pushes the matrix to store gl values.
        bMesh.glPushMatrix();

        // Scales down to make the font look better.
        bMesh.glScaled(0.5, 0.5, 1);

        // Removes half the margin to render in the right spot.
        x -= MARGIN / 2;
        y -= MARGIN / 2;

        // Adds 0.5 to x and y.
        x += 0.5F;
        y += 0.5F;

        // Doubles the position because of the scaling.
        x *= 2;
        y *= 2;
        for (char character : text.toCharArray()) {
            if (!loadedChars.contains(character)) {
                loadedChars.add(character);
                addChar(this.regularData, new Character[]{character}, Font.PLAIN);
                addChar(this.boldData, new Character[]{character}, Font.BOLD);
                addChar(this.italicsData, new Character[]{character}, Font.ITALIC);
            }
        }

        // The character texture set to be used. (Regular by default)
        CharacterData[] characterData = regularData;

        // Booleans to handle the style.
        boolean underlined = false;
        boolean strikethrough = false;
        boolean obfuscated = false;

        // The length of the text used for the draw loop.
        int length = text.length();

        // Loops through the text.
        for (int i = 0; i < length; i++) {
            // The character at the index of 'i'.
            char character = text.charAt(i);

            // The previous character.
            char previous = i > 0 ? text.charAt(i - 1) : '.';

            // Continues if the previous color was the color invoker.
            if (previous == COLOR_INVOKER) continue;

            // Sets the color if the character is the color invoker and the character index is less than the length.
            if (character == COLOR_INVOKER && i < length) {

                // The color index of the character after the current character.
                int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(Math.min(i + 1, length - 1)));

                // If the color character index is of the normal color invoking characters.
                if (index < 16) {
                    // Resets all the styles.
                    obfuscated = false;
                    strikethrough = false;
                    underlined = false;

                    // Sets the character data to the regular type.
                    characterData = regularData;

                    // Clamps the index just to be safe in case an odd character somehow gets in here.
                    if (index < 0 || index > 15) index = 15;

                    // Adds 16 to the color index to get the darker shadow color.
                    if (shadow) index += 16;

                    // Gets the text color from the color codes array.
                    int textColor = this.colorCodes[index];

                } else if (index == 16)
                    obfuscated = true;
                else if (index == 17)
                    // Sets the character data to the bold type.
                    characterData = boldData;
                else if (index == 18)
                    strikethrough = true;
                else if (index == 19)
                    underlined = true;
                else if (index == 20)
                    // Sets the character data to the italics type.
                    characterData = italicsData;
                else if (index == 21) {
                    // Resets the style.
                    obfuscated = false;
                    strikethrough = false;
                    underlined = false;

                    // Sets the character data to the regular type.
                    characterData = regularData;
                }
            } else {
                // Continues to not crash!
                if (characterData[character] == null) {
                    loadedChars.add(character);
                    addChar(this.regularData, new Character[]{character}, Font.PLAIN);
                    characterData = regularData;
                }

                // Sets the character to a random char if obfuscated is enabled.
                if (obfuscated)
                    character = (char) (((int) character) + RANDOM_OFFSET);

                // Draws the character.
                drawChar(bMesh,character, characterData, x, y,color);

                // The character data for the given character.
                CharacterData charData = characterData[character];

                // Draws the strikethrough line if enabled.
                if (strikethrough)
                    drawLine(bMesh,new Vec2f(0, charData.height / 2f), new Vec2f(charData.width, charData.height / 2f), 3,color);

                // Draws the underline if enabled.
                if (underlined)
                    drawLine(bMesh, new Vec2f(0, charData.height - 15), new Vec2f(charData.width, charData.height - 15), 3,color);

                // Adds to the offset.
                x += charData.width - (2 * MARGIN);
            }
        }

        // Restores previous values.
        bMesh.glPopMatrix();

        GlStateManager._bindTexture(0);
    }

    /**
     * Gets the width of the given text.
     *
     * @param text The text to get the width of.
     * @return The width of the given text.
     */
    public float getWidth(String text) {

        // The width of the string.
        float width = 0;

        for (char character : text.toCharArray()) {
            if (!loadedChars.contains(character)) {
                loadedChars.add(character);
                addChar(this.regularData, new Character[]{character}, Font.PLAIN);
                addChar(this.boldData, new Character[]{character}, Font.BOLD);
                addChar(this.italicsData, new Character[]{character}, Font.ITALIC);
            }
        }

        // The character texture set to be used. (Regular by default)
        CharacterData[] characterData = regularData;

        // The length of the text.
        int length = text.length();

        // Loops through the text.
        for (int i = 0; i < length; i++) {
            // The character at the index of 'i'.
            char character = text.charAt(i);

            // The previous character.
            char previous = i > 0 ? text.charAt(i - 1) : '.';

            // Continues if the previous color was the color invoker.
            if (previous == COLOR_INVOKER) continue;

            // Sets the color if the character is the color invoker and the character index is less than the length.
            if (character == COLOR_INVOKER && i < length) {

                // The color index of the character after the current character.
                int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(Math.min(i + 1, length - 1)));

                if (index == 17)
                    // Sets the character data to the bold type.
                    characterData = boldData;
                else if (index == 20)
                    // Sets the character data to the italics type.
                    characterData = italicsData;
                else if (index == 21)
                    // Sets the character data to the regular type.
                    characterData = regularData;
            } else {
                // Continues to not crash!

                if (characterData[character] == null) {
                    loadedChars.add(character);
                    addChar(this.regularData, new Character[]{character}, Font.PLAIN);
                    characterData = regularData;
                }

                // The character data for the given character.
                CharacterData charData = characterData[character];

                // Adds to the offset.
                width += (charData.width - (2 * MARGIN)) / 2;
            }
        }

        // Returns the width.
        return width + MARGIN / 2;
    }

    /**
     * Gets the height of the given text.
     *
     * @param text The text to get the height of.
     * @return The height of the given text.
     */
    public float getHeight(String text) {

        // The height of the string.
        float height = 0;

        for (char character : text.toCharArray()) {
            if (!loadedChars.contains(character)) {
                loadedChars.add(character);
                addChar(this.regularData, new Character[]{character}, Font.PLAIN);
                addChar(this.boldData, new Character[]{character}, Font.BOLD);
                addChar(this.italicsData, new Character[]{character}, Font.ITALIC);
            }
        }

        // The character texture set to be used. (Regular by default)
        CharacterData[] characterData = regularData;

        // The length of the text.
        int length = text.length();

        // Loops through the text.
        for (int i = 0; i < length; i++) {
            // The character at the index of 'i'.
            char character = text.charAt(i);

            // The previous character.
            char previous = i > 0 ? text.charAt(i - 1) : '.';

            // Continues if the previous color was the color invoker.
            if (previous == COLOR_INVOKER) continue;

            // Sets the color if the character is the color invoker and the character index is less than the length.
            if (character == COLOR_INVOKER && i < length) {

                // The color index of the character after the current character.
                int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(Math.min(i + 1, length - 1)));

                if (index == 17)
                    // Sets the character data to the bold type.
                    characterData = boldData;
                else if (index == 20)
                    // Sets the character data to the italics type.
                    characterData = italicsData;
                else if (index == 21)
                    // Sets the character data to the regular type.
                    characterData = regularData;
            } else {
                // Continues to not crash!
                if (characterData[character] == null) {
                    loadedChars.add(character);
                    addChar(this.regularData, new Character[]{character}, Font.PLAIN);
                    characterData = regularData;
                }

                // The character data for the given character.
                CharacterData charData = characterData[character];

                // Sets the height if its bigger.
                height = Math.max(height, charData.height);
            }
        }

        // Returns the height.
        return height / 2 - MARGIN / 2;
    }

    private final Renderer2D mesh = new Renderer2D(true);
    private final Renderer2D bM = new Renderer2D(false);



    /**
     * Draws the character.
     *
     * @param character     The character to be drawn.
     * @param characterData The character texture set to be used.
     */
    private void drawChar(SMesh.BMesh matrices, char character, CharacterData[] characterData, float x, float y,int color) {
        // The char data that stores the character data.
        CharacterData charData = characterData[character];

        // Binds the character data texture.
        charData.bind();

        // Enables blending.
        RenderSystem.enableBlend();

        // Sets the blending function.
        RenderSystem.defaultBlendFunc();

        mesh.begin();
        mesh.texQuad(x,y,charData.width,charData.height,new meteordevelopment.meteorclient.utils.render.color.Color(color));
        mesh.render(matrices.glGet());

        RenderSystem.disableBlend();

        // Binds the opengl texture by the texture id.
        RenderSystem.bindTexture(0);
    }



    /**
     * Draws a line from start to end with the given width.
     *
     * @param start The starting point of the line.
     * @param end   The ending point of the line.
     * @param width The thickness of the line.
     */
    private void drawLine(SMesh.BMesh meshA, Vec2f start, Vec2f end, float width,int color) {

        // Sets the width.
        RenderSystem.lineWidth(width);

        bM.begin();
        bM.line(start.x, start.y,end.x, end.y,new meteordevelopment.meteorclient.utils.render.color.Color(color));
        bM.render(meshA.glGet());

    }

    /**
     * Generates all the colors.
     */
    private void generateColors() {
        // Iterates through 32 colors.
        for (int i = 0; i < 32; i++) {
            // Not sure what this variable is.
            int thingy = (i >> 3 & 1) * 85;

            // The red value of the color.
            int red = (i >> 2 & 1) * 170 + thingy;

            // The green value of the color.
            int green = (i >> 1 & 1) * 170 + thingy;

            // The blue value of the color.
            int blue = (i >> 0 & 1) * 170 + thingy;

            // Increments the red by 85, not sure why does this in minecraft's font renderer.
            if (i == 6) red += 85;

            // Used to make the shadow darker.
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            // Sets the color in the color code at the index of 'i'.
            this.colorCodes[i] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }
    }

    public Font getFont() {
        return font;
    }

    /**
     * Class that holds the data for each character.
     */
    class CharacterData {

        /**
         * The character the data belongs to.
         */
        public char character;

        /**
         * The width of the character.
         */
        public float width;

        /**
         * The height of the character.
         */
        public float height;

        /**
         * The id of the character texture.
         */
        private int textureId;

        public CharacterData(char character, float width, float height, int textureId) {
            this.character = character;
            this.width = width;
            this.height = height;
            this.textureId = textureId;
        }

        /**
         * Binds the texture.
         */
        public void bind() {
            // Binds the opengl texture by the texture id.
            RenderSystem.bindTexture(textureId);
        }
    }
}