package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import static data.utils.tdb.I18nUtil.easyRippleOut;

public class TDB_dao_dan extends BaseShipSystemScript {

    public static final float DAMAGE_BONUS_PERCENT = 15f;
    private boolean i = false;
    public static String txt(String id) {
        return Global.getSettings().getString("scripts", id);
    }

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        float bonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
        stats.getMissileWeaponDamageMult().modifyPercent(id, bonusPercent);

        if (!i) {
            i = true;
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {

        stats.getMissileWeaponDamageMult().unmodify(id);

        if (i) {
            ShipAPI ship = (ShipAPI) stats.getEntity();
            ShipAPI target = ship.getShipTarget();
            CombatEngineAPI engine = Global.getCombatEngine();
            float radius = ship.getCollisionRadius();
            Vector2f vel = new Vector2f(ship.getVelocity());

            ship.setJitter(ship, TDB_ColorData.TDBblue4, 2, 4, 4f, 2);

            for (int i = 0; i < 8; i++) {
                Vector2f missileloc = MathUtils.getRandomPointOnCircumference(ship.getLocation(), MathUtils.getRandomNumberInRange(radius - 50f, radius + 50f));

                easyRippleOut(missileloc, vel, 70, 100f, 1f, 20f);

                if (target == null || !target.isAlive() || target.getOwner() == ship.getOwner()) {

                    engine.spawnProjectile(
                            ship,
                            null,
                            "TDB_xiang_wei",
                            missileloc,
                            ship.getFacing(),
                            I18nUtil.nv
                    );
                } else {
                    float angle = VectorUtils.getAngle(missileloc, target.getLocation());
                    engine.spawnProjectile(
                            ship,
                            null,
                            "TDB_xiang_wei",
                            missileloc,
                            angle,
                            I18nUtil.nv
                    );
                }


            }
        }

    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        float bonusPercent = DAMAGE_BONUS_PERCENT * effectLevel;
        if (index == 0) {
            return new StatusData("+" + (int) bonusPercent + "% 导弹武器伤害", false);
        }
        return null;
    }
}
