package data.utils.tdb;

import com.fs.starfarer.api.Global;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lwjgl.util.vector.Vector2f;

public class I18nUtil {
    //private static final String CATE_SHIP_SYSTEM = "shipSystem";
    private static final String CATE_STAR_SYSTEMS = "starSystems";
    //private static final String CATE_HULL_MOD = "hullMod";
    // --注释掉检查 (2022/9/28 10:54):private static final String PREFIX = "TDB_";

    public static String getFxName(String id) {
        return Global.getSettings().getSpriteName("fx", id);
    }

    public static String getString(String category, String id) {
        return Global.getSettings().getString(category, id);
    }

    /*public static String getShipSystemString(String id) {
        return getString(CATE_SHIP_SYSTEM, id);
    }*/

    public static String getStarSystemsString(String id) {
        return getString(CATE_STAR_SYSTEMS, id);
    }

    /*public static String getHullModString(String id) {
        return getString(CATE_HULL_MOD, id);
    }*/

    public static void easyRippleOut(Vector2f location, Vector2f velocity, float size, float intensity, float fadesize, float frameRate) {
        if (intensity == -1f) {
            intensity = size / 3f;
        }
        if (velocity == null) {
            velocity = nv;
        }
        RippleDistortion ripple = new RippleDistortion(location, velocity);
        ripple.setSize(size);
        ripple.setIntensity(intensity);
        ripple.setFrameRate(frameRate);
        ripple.fadeInSize(fadesize);
        ripple.fadeOutIntensity(fadesize);

        DistortionShader.addDistortion(ripple);
    }

    public static final Vector2f nv = new Vector2f();
}
