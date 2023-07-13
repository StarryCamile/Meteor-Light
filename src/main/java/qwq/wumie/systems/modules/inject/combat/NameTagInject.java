package qwq.wumie.systems.modules.inject.combat;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import org.joml.Vector3d;
import qwq.wumie.renderer.text.CFont;
import qwq.wumie.renderer.text.TTFFontRender;
import qwq.wumie.systems.modules.inject.ModuleInject;
import qwq.wumie.utils.render.RenderUtil;
import qwq.wumie.utils.time.MSTimer;

import java.util.ArrayList;
import java.util.List;

public class NameTagInject extends ModuleInject<Nametags> {
    private final Setting<RenderMode> renderMode = getDefaultGroup().add(new EnumSetting.Builder<RenderMode>()
            .name("render-mode")
            .description("The mode for rendering")
            .defaultValue(RenderMode.Meteor)
            .build()
    );


    public enum RenderMode {
        Meteor,
        Jello,
        Other;

        @Override
        public String toString() {
            if (this == Other) {
                return "None";
            }
            return super.toString();
        }
    }

    public NameTagInject() {
        super(Nametags.class);

        timer.init();
    }

    private final MSTimer timer = new MSTimer();

    public boolean onTick() {
        return true;
    }

    public boolean onRender2D(Vector3d pos, Entity entity) {
        EntityType<?> type = entity.getType();

        if (renderMode.get().equals(RenderMode.Jello)) {
            if (entity instanceof LivingEntity e) {
                renderJello(pos, e, true);
                return false;
            }/* else if (type.equals(EntityType.ITEM)) {
                renderJelloItem(pos, entity, true);
                return false;
            } else if (type.equals(EntityType.TNT)) {
                renderJelloTnt(pos,(TntEntity) entity,true);
            }*/
        }
        return true;
    }

    private final Color bgColor = new Color(65,65,65,170);

    private void renderJelloItem(Vector3d pos, Entity entity, boolean shadow) {
        ItemStack item = ((ItemEntity) entity).getStack();
        TTFFontRender nameFont = CFont.jelloRegular;
        TTFFontRender stackFont = CFont.googleSans;
        double nameFontScale = 0.9;
        double fuseFontScale = 0.65;
        NametagUtils.begin(pos);
        String stackText = "Stack: " + item.getCount();
        if (item.getNbt() != null) {
            stackText = stackText +" * NBTs: "+item.getNbt().getSize();
        }
        double nameWidth = nameFont.getWidth(item.getName().getString(), shadow);
        double stackWidth = stackFont.getWidth(stackText,shadow);
        double nameHeight = nameFont.getWidth(item.getName().getString(), shadow);
        double stackHeight = stackFont.getWidth(stackText,shadow);
        double maxWidth = Math.max(nameWidth, stackWidth);
        double maxHeight = nameHeight + 1 + stackHeight;
        double bgX = maxWidth / 2;
        double bgY = nameFont.getHeight(shadow);
        gameRender.drawRect(-bgX, -bgY, maxWidth, maxHeight,bgColor);
        double nameX = bgX + 2;
        double healthStringY = bgY + stackFont.getHeight(shadow, nameFontScale) + 1;
        nameFont.render(entity.getDisplayName().getString(),nameX,-bgY-1,Color.WHITE,nameFontScale);
        nameFont.render(stackText,nameX,healthStringY,Color.WHITE,fuseFontScale);
        NametagUtils.end();
    }

    private void renderJelloTnt(Vector3d pos, TntEntity entity, boolean shadow) {
        TTFFontRender nameFont = CFont.jelloRegular;
        TTFFontRender fuseFont = CFont.googleSans;
        double nameFontScale = 0.9;
        double fuseFontScale = 0.65;
        NametagUtils.begin(pos);
        String fuseText = ticksToTime(entity.getFuse());
        double nameWidth = nameFont.getWidth(entity.getDisplayName().getString(), shadow);
        double fuseWidth = fuseFont.getWidth(fuseText,shadow);
        double nameHeight = nameFont.getWidth(entity.getDisplayName().getString(), shadow);
        double fuseHeight = fuseFont.getWidth(fuseText,shadow);
        double maxWidth = Math.max(nameWidth, fuseWidth);
        double maxHeight = nameHeight + 1 + fuseHeight;
        double bgX = maxWidth / 2;
        double bgY = nameFont.getHeight(shadow);
        gameRender.drawRect(-bgX, -bgY, maxWidth, maxHeight,bgColor);
        double nameX = bgX + 2;
        double healthStringY = bgY + fuseFont.getHeight(shadow, nameFontScale) + 1;

        gameRender.drawRect(-bgX,(-bgY + bgY) - 2, Math.min(entity.getFuse() / 80,1.0) * maxWidth,2,getTntTimeColor(entity, bgColor.a));
        nameFont.render(entity.getDisplayName().getString(),nameX,-bgY-1,Color.WHITE,nameFontScale);
        nameFont.render("Fuse: " + fuseText,nameX,healthStringY,getTntTimeColor(entity,255),fuseFontScale);

        NametagUtils.end();
    }

    private void renderJello(Vector3d pos, LivingEntity entity, boolean shadow) {
        TTFFontRender jw = CFont.jelloRegular;
        TTFFontRender hw = CFont.googleSans;
        TTFFontRender nameFont = CFont.jelloRegular;
        TTFFontRender healthFont = CFont.googleSans;
        double nameFontScale = 0.9;
        double healthFontScale = 0.65;

        NametagUtils.begin(pos);

        String entityName;

        Color healthBarColor;
        if (entity instanceof PlayerEntity player) {
            healthBarColor = PlayerUtils.getPlayerColor(player,new Color(255, 255, 255, 170));
            entityName = entity.getDisplayName().getString();
            if (Friends.get().isFriend(player)) {
                entityName = entityName + " *";
            }
        } else if (entity.getDisplayName().getString().startsWith("§")) {
            healthBarColor = gameRender.getColorForString(entity.getDisplayName().getString().substring(1, 2), 170);
            if (entity.getDisplayName().getString().charAt(1) == '1' ||
                    entity.getDisplayName().getString().charAt(1) == '2' ||
                    entity.getDisplayName().getString().charAt(1) == '3' ||
                    entity.getDisplayName().getString().charAt(1) == '4' ||
                    entity.getDisplayName().getString().charAt(1) == '5' ||
                    entity.getDisplayName().getString().charAt(1) == '6' ||
                    entity.getDisplayName().getString().charAt(1) == '7' ||
                    entity.getDisplayName().getString().charAt(1) == '8' ||
                    entity.getDisplayName().getString().charAt(1) == '9' ||
                    entity.getDisplayName().getString().charAt(1) == '0' ||
                    entity.getDisplayName().getString().charAt(1) == 'a' ||
                    entity.getDisplayName().getString().charAt(1) == 'b' ||
                    entity.getDisplayName().getString().charAt(1) == 'c' ||
                    entity.getDisplayName().getString().charAt(1) == 'd' ||
                    entity.getDisplayName().getString().charAt(1) == 'e' ||
                    entity.getDisplayName().getString().charAt(1) == 'f'
            ) {
                entityName = entity.getDisplayName().getString().replace("§" + entity.getDisplayName().getString().charAt(1), "");
            } else {
                entityName = entity.getDisplayName().getString().replace("§", "");
            }
        } else {
            healthBarColor = new Color(255, 255, 255, 170);
            entityName = entity.getDisplayName().getString();
        }

        float absorption = entity.getAbsorptionAmount();
        int health = Math.round(entity.getHealth() + absorption);
        double healthPercent = health / (entity.getMaxHealth() + absorption);

        int healthBarHeight = 2;
        double bgWidth = jw.getWidth(entityName, shadow, nameFontScale) +3;
        double bgY = 1 + jw.getHeight(shadow,nameFontScale) + 1 + hw.getHeight(shadow, healthFontScale) + 1 + healthBarHeight;
        double hpStrWidth = hw.getWidth("Health: " + health, shadow, healthFontScale)+3;

        // draw bg
        if (bgWidth < hpStrWidth) {
            double bgX = hpStrWidth / 2;
            double bgBarX = -bgX;
            double bgBarY = -bgY;
            double nameX = bgBarX + 2;
            double healthStringY = bgBarY + jw.getHeight(shadow, nameFontScale) + 1;
            // draw Bg
            gameRender.drawRect(-bgX,-bgY,hpStrWidth,bgY-2,bgColor);
            // draw Health Bar
            gameRender.drawRect(bgBarX,(bgBarY + bgY) -2, (hpStrWidth * healthPercent),healthBarHeight,healthBarColor);
            // draw Name
            nameFont.render(entityName,nameX, bgBarY-1,Color.WHITE,nameFontScale);
            // draw Health String
            healthFont.render("Health: " + health, nameX,healthStringY,Color.WHITE,healthFontScale);
        } else if (bgWidth >= hpStrWidth) {
            double bgX = bgWidth / 2;
            double bgBarX = -bgX;
            double bgBarY = -bgY;
            double nameX = bgBarX + 2;
            double healthStringY = bgBarY + jw.getHeight(shadow, nameFontScale) + 1;
            // draw Bg
            gameRender.drawRect(-bgX,-bgY,bgWidth,bgY-2,bgColor);
            // draw Health Bar
            gameRender.drawRect(bgBarX,(bgBarY + bgY)-2,(bgWidth * healthPercent),healthBarHeight,healthBarColor);
            // draw Name
            nameFont.render(entityName,nameX, bgBarY-1,Color.WHITE,nameFontScale);
            // draw Health String
            healthFont.render("Health: " + health, nameX,healthStringY,Color.WHITE,healthFontScale);
        }
        NametagUtils.end();
    }

    private Color getTntTimeColor(TntEntity e,int alpha) {
        int fuse = e.getFuse();
        final int DEFAULT_FUSE = 80;
        double offset = fuse / DEFAULT_FUSE;
        if (offset <= 1) {
            if (offset < 0.2) {
                return Color.RED.a(alpha);
            } else if (offset < 0.5) {
                return Color.YELLOW.a(alpha);
            } else return Color.GREEN.a(alpha);
        }

        return Color.GREEN.a(alpha);
    }

    private String ticksToTime(int ticks) {
        if (ticks > 20 * 3600) {
            int h = ticks / 20 / 3600;
            return h + " h";
        } else if (ticks > 20 * 60) {
            int m = ticks / 20 / 60;
            return m + " m";
        } else {
            int s = ticks / 20;
            int ms = (ticks % 20) / 2;
            return s + "." + ms + " s";
        }
    }

    private static class EntityInfo {
        public double last,current;
        public Entity entity;
        public int lastShowTime;

        public EntityInfo(Entity e,int maxTiem) {
            this.entity = e;
            this.lastShowTime = maxTiem;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EntityInfo that = (EntityInfo) o;

            return entity != null ? entity.equals(that.entity) : that.entity == null;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(last);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(current);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (entity != null ? entity.hashCode() : 0);
            return result;
        }
    }
}
