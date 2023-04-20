package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;


public class TDB_SY extends BaseShipSystemScript {

    private final IntervalUtil inte = new IntervalUtil(1.5f, 1.5f);
    public static float RANGE = 1500f;
    public static float FLUX_THRESHOLD_INCREASE_PERCENT = -50f;

    public static String txt(String id) {
        return Global.getSettings().getString("scripts", id);
    }
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        CombatEngineAPI engine = Global.getCombatEngine();

        if (stats.getEntity() != null && stats.getEntity() instanceof ShipAPI)
        {
            for (ShipAPI tship : engine.getShips()) {
                if (tship.isPhased()) {

                    if (MathUtils.getDistance(ship, tship) <= RANGE) {
                        if (ship.getOwner() != tship.getOwner()) {
                            tship.getMutableStats().getDynamic().getMod(Stats.PHASE_CLOAK_FLUX_LEVEL_FOR_MIN_SPEED_MOD).modifyPercent(id, FLUX_THRESHOLD_INCREASE_PERCENT);
                            tship.setJitterUnder(ship, TDB_ColorData.TDBred, effectLevel, 2, 5f);

                        }
                    } else {
                        tship.getMutableStats().getDynamic().getMod(Stats.PHASE_CLOAK_FLUX_LEVEL_FOR_MIN_SPEED_MOD).unmodify(id);
                    }
                }
            }

            inte.advance(0.015F);
            if (inte.intervalElapsed()) {
                SpriteAPI sp2 = Global.getSettings().getSprite("fx", "TDB_shao2");
                float dynamicSize = MathUtils.getRandomNumberInRange(30f, 90f);
                MagicRender.battlespace(sp2, ship.getLocation(), I18nUtil.nv, new Vector2f(dynamicSize, dynamicSize), new Vector2f(1000f, 1000f), 360f, MathUtils.getRandomNumberInRange(-60f, 60f), TDB_ColorData.TDBblue5, true, 0.25f, 0.01f, 2f);
                Global.getSoundPlayer().playSound("TDB_shao_miao", 1f, 1f, ship.getLocation(), ship.getVelocity());
            }
        }


        stats.getSightRadiusMod().modifyPercent(id, 80f);
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getSightRadiusMod().unmodify(id);

        ShipAPI ship = (ShipAPI) stats.getEntity();
        CombatEngineAPI engine = Global.getCombatEngine();

        if (!(stats.getEntity() instanceof ShipAPI)) {
            return;
        }

        for (ShipAPI tship : engine.getShips()) {
            if (tship.getOwner() != ship.getOwner()) {
                tship.getMutableStats().getDynamic().getMod(Stats.PHASE_CLOAK_FLUX_LEVEL_FOR_MIN_SPEED_MOD).unmodify(id);
            }
        }
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {

        if (index == 0) {
            return new StatusData(txt("SY_1"), false);
        }
        if (index == 2) {
            return new StatusData(txt("SY_2"), false);
        }
        return null;
    }


}
