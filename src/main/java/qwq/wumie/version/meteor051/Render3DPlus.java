package qwq.wumie.version.meteor051;

import com.mojang.blaze3d.systems.RenderSystem;
import kotlin.internal.ProgressionUtilKt;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import qwq.wumie.utils.pathfinding.pathfinding.CustomPathFinder;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Render3DPlus {
    private final Renderer3D r;

    public Render3DPlus(Renderer3D main) {
        r = main;
    }

    public void vecBox(Vector3d pos, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode.lines()) r.boxLines(pos.x(), pos.y(), pos.z(), pos.x() + 1, pos.y() + 2, pos.z() + 1, lineColor, excludeDir);
        if (mode.sides()) r.boxSides(pos.x(), pos.y(), pos.z(), pos.x() + 1, pos.y() + 2, pos.z() + 1, sideColor, excludeDir);
    }

    public void vecBox(Vector3d pos,Vector3d addPos, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode.lines()) r.boxLines(pos.x(), pos.y(), pos.z(), pos.x() + addPos.x, pos.y() + addPos.y, pos.z() + addPos.z, lineColor, excludeDir);
        if (mode.sides()) r.boxSides(pos.x(), pos.y(), pos.z(), pos.x() + addPos.x, pos.y() + addPos.y, pos.z() + addPos.z, sideColor, excludeDir);
    }

    public void vecBox(CustomPathFinder.Vec3 pos, Box bb, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode.lines()) r.boxLines(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + (bb.maxX - bb.minX), pos.getY() + (bb.maxY - bb.minY), pos.getZ() + (bb.maxZ - bb.minZ), lineColor, excludeDir);
        if (mode.sides()) r.boxSides(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + (bb.maxX - bb.minX), pos.getY() + (bb.maxY - bb.minY), pos.getZ() + (bb.maxZ - bb.minZ), lineColor, excludeDir);
    }

    public void vecBox(Vec3d pos, Box bb, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode.lines()) r.boxLines(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + (bb.maxX - bb.minX), pos.getY() + (bb.maxY - bb.minY), pos.getZ() + (bb.maxZ - bb.minZ), lineColor, excludeDir);
        if (mode.sides()) r.boxSides(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + (bb.maxX - bb.minX), pos.getY() + (bb.maxY - bb.minY), pos.getZ() + (bb.maxZ - bb.minZ), lineColor, excludeDir);
    }

    public void up2Dbox(Box box, Color sideColor, Color lineColor, ShapeMode mode,double y, int excludeDir) {
        if (mode.lines()) r.boxLines(box.minX, y-1, box.minZ, box.maxX, y, box.maxZ, lineColor, excludeDir);
        if (mode.sides()) r.boxSides(box.minX, y-1, box.minZ, box.maxX, y, box.maxZ, sideColor, excludeDir);
    }

    public void box(Box box, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        if (mode.lines()) r.boxLines(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, lineColor, excludeDir);
        if (mode.sides()) r.boxSides(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, sideColor, excludeDir);
    }

    public double easeInOutQuad(double x) {
        double percent;
        if (x < 0.5D) {
            percent = (double)2 * x * x;
        } else {
            percent = 1;
            double ii = (double)-2 * x + (double)2;
            byte i = 2;
            percent -= Math.pow(ii, i) / (double)2;
        }

        return percent;
    }

    public void Circle(Entity entity, Color lineColor, ShapeMode mode) {
        Box entitybb = entity.getBoundingBox();
        double maxY = entitybb.maxY + 1;
        double minY = entitybb.minY -1;
        double radius = ((entitybb.maxX - entitybb.minX) + (entitybb.maxZ - entitybb.minZ)) * 0.5f;
        int i = 5;

        double x1 = entity.lastRenderX + (entity.getX() - entity.lastRenderX) - Math.sin((double)i * 3.141592653589793D / (double)180.0F) * radius;
        double z1 = entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) + Math.cos((double)i * 3.141592653589793D / (double)180.0F) * radius;
        double x2 = entity.lastRenderX + (entity.getX() - entity.lastRenderX) - Math.sin((double)(i - 5) * 3.141592653589793D / (double)180.0F) * radius;
        double z2 = entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) + Math.cos((double)(i - 5) * 3.141592653589793D / (double)180.0F) * radius;


        if (mode.lines()) {
            r.line(x1, minY, z1, x2, maxY, z2, lineColor);
            r.line(x2, maxY, z2,x1, minY, z1 ,lineColor);
        }
    }
    public void drawTargetStrafeCircle(Entity target, double radius, Render3DEvent event, Color color) {
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        com.mojang.blaze3d.systems.RenderSystem.lineWidth(1f);
        double x = target.lastRenderX + (target.getX() - target.lastRenderX) * event.tickDelta - mc.cameraEntity.getX();
        double y = target.lastRenderY + (target.getY() - target.lastRenderY) * event.tickDelta - mc.cameraEntity.getY();
        double z = target.lastRenderZ + (target.getZ() - target.lastRenderZ) * event.tickDelta - mc.cameraEntity.getZ();
        for (int i = 0; i < 360; i++) {
            event.renderer.line(
                    x, y, z, x + radius * cos(i * 6.283185307179586 / 45.0), y, z + radius * sin(i * 6.283185307179586 / 45.0), color
            );
        }
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    public void drawFakeSigma(MatrixStack matrices, Entity entity, Color sideColor, Color lineColor, ShapeMode mode, int excludeDir) {
        int everyTime = 3000;

        int drawTime = (int) (System.currentTimeMillis() % everyTime);
        boolean drawMode = drawTime > (everyTime/2);
        double drawPercent = drawTime / (everyTime/2.0);
        if (drawMode) {drawPercent -= (double)1;} else {drawPercent = (double)1 - drawPercent;}
        drawPercent = easeInOutQuad(drawPercent);
        matrices.push();
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();

        Box entitybb = entity.getBoundingBox();
        double radius = ((entitybb.maxX - entitybb.minX) + (entitybb.maxZ - entitybb.minZ)) * 0.5f;
        double height = entitybb.maxY - entitybb.minY;
        double eased = height / (double)3 * (drawPercent > 0.5D ? (double)1 - drawPercent : drawPercent) * (double)(drawMode ? -1 : 1);
        int i = 5;
        int tr = ProgressionUtilKt.getProgressionLastElement(5,360,5);
        if ( i <= tr) {
            while(true) {
                double MPI = Math.PI;
                double x1 = entity.lastRenderX + (entity.getX() - entity.lastRenderX) - Math.sin((double)i * MPI / (double)180.0F) * radius;
                double z1 = entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) + Math.cos((double)i * MPI / (double)180.0F) * radius;
                double x2 = entity.lastRenderX + (entity.getX() - entity.lastRenderX) - Math.sin((double)(i - 5) * MPI / (double)180.0F) * radius;
                double z2 = entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) + Math.cos((double)(i - 5) * MPI / (double)180.0F) * radius;
                double y = entity.lastRenderY + (entity.getY() - entity.lastRenderY) + height * drawPercent;

                //Draw
                if (mode.lines()) r.line(x1, y + eased, z1, x2, y + eased, z2, lineColor);
                //End

                if (i == tr) {
                    break;
                }
                i += 5;
            }
        }

        RenderSystem.disableBlend();
        matrices.pop();
    }
}
