package meteordevelopment.meteorclient.renderer;

import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.apache.http.cookie.SM;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class SMesh {
    private static BMesh bMesh;

    public static BMesh getbMesh() {
        return bMesh;
    }

    public static void load() {
        bMesh = new BMesh();
    }

    public static MatrixStack glLoadIdentity() {
        return bMesh.glLoadIdentity();
    }

    public static MatrixStack glPushMatrix() {
        return bMesh.glPushMatrix();
    }

    public static MatrixStack glPopMatrix() {
        return bMesh.glPopMatrix();
    }

    public static Color glColor4f(float r, float g, float b, float a) {
        return bMesh.glColor4f(r, g, b, a);
    }

    public static Color glColor4i(int r, int g, int b, int a) {
        return bMesh.glColor4i(r, g, b, a);
    }

    public static MatrixStack glRotatef(float x, float y, float z, float w) {
        return bMesh.glRotatef(x, y, z, w);
    }

    public static MatrixStack glRotated(double x, double y, double z, double w) {
        return bMesh.glRotated(x, y, z, w);
    }

    public static MatrixStack glScalef(float x, float y, float z) {
        return bMesh.glScalef(x, y, z);
    }

    public static MatrixStack glScaled(double x, double y, double z) {
        return bMesh.glScaled(x, y, z);
    }

    public static MatrixStack glTranslatef(float x, float y, float z) {
        return bMesh.glTranslatef(x, y, z);
    }

    public static MatrixStack glTranslated(double x, double y, double z) {
        return bMesh.glTranslated(x, y, z);
    }

    public static MatrixStack setMatrix(MatrixStack matrix) {
        bMesh.setMatrix(matrix);
        return bMesh.glGet();
    }

    public static class BMesh {
        private MatrixStack matrices;
        private CRender renderer;
        private int beginMode = -1;
        private boolean building;
        private List<VertexPoint> vertexPoints = new ArrayList<>();
        private Color color = Color.WHITE;

        public BMesh() {
            assertMatrix();
        }

        public MatrixStack glLoadIdentity() {
            assertMatrix();
            matrices.loadIdentity();
            return matrices;
        }

        public MatrixStack glPushMatrix() {
            assertMatrix();
            matrices.push();
            return matrices;
        }

        public MatrixStack glPopMatrix() {
            assertMatrix();
            matrices.pop();
            return matrices;
        }

        public Color glColor4f(float r, float g, float b, float a) {
            color = new Color(r, g, b, a);
            return color;
        }

        public Color glColor4i(int r, int g, int b, int a) {
            color = new Color(r, g, b, a);
            return color;
        }

        public MatrixStack glScalef(float x, float y, float z) {
            assertMatrix();
            matrices.scale(x, y, z);
            return matrices;
        }

        public MatrixStack glScaled(double x, double y, double z) {
            glScalef((float) x, (float) y, (float) z);
            return matrices;
        }

        public MatrixStack glGet() {
            assertMatrix();
            return matrices;
        }

        public MatrixStack glRotatef(float x, float y, float z, float w) {
            assertMatrix();
            matrices.multiply(new Quaternionf(x,y,z,w));
            return matrices;
        }

        public MatrixStack glRotated(double x, double y, double z, double w) {
            glRotatef((float) x, (float) y, (float) z, (float) w);
            return matrices;
        }

        public MatrixStack glTranslatef(float x, float y, float z) {
            assertMatrix();
            matrices.translate(x, y, z);
            return matrices;
        }

        public MatrixStack glTranslated(double x, double y, double z) {
            return glTranslatef((float) x, (float) y, (float) z);
        }

        public BMesh setMatrix(MatrixStack matrices) {
            this.matrices = matrices;
            return this;
        }

        public void assertMatrix() {
            if (matrices == null) {
                matrices = new MatrixStack();
                matrices.loadIdentity();
            }
            if (renderer == null) {
                renderer = new CRender();
            }
        }
    }

    public static class VertexPoint {
        public double x;
        public double y;
        public double z;
        public double x1;
        public double y1;
        public double z1;

        public VertexPoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.x1 = x;
            this.y1 = y;
            this.z1 = z;
        }

        public VertexPoint(double x, double y, double z, double x1, double y1, double z1) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public double getX1() {
            return x1;
        }

        public void setX1(double x1) {
            this.x1 = x1;
        }

        public double getY1() {
            return y1;
        }

        public void setY1(double y1) {
            this.y1 = y1;
        }

        public double getZ1() {
            return z1;
        }

        public void setZ1(double z1) {
            this.z1 = z1;
        }
    }
}
