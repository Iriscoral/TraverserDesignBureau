package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

public class TDB_wu_qi extends BaseShipSystemScript {

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEngineAPI engine = Global.getCombatEngine();
        ShipAPI ship = (ShipAPI) stats.getEntity();
        //电弧特效前置设置
        float angle;
        float radius;
        angle = 360f * (float) Math.random();
        radius = MathUtils.getRandomNumberInRange(2.7f, 3.5f);
        Vector2f point = MathUtils.getPointOnCircumference(ship.getLocation(), 105f * radius * 0.5f, angle);
        Vector2f Point1 = MathUtils.getPointOnCircumference(ship.getLocation(), 105f * radius * 0.5f, MathUtils.clampAngle(angle + MathUtils.getRandomNumberInRange(35f, 70f)));
        //生成电弧
        engine.spawnEmpArc(ship, point, null, new SimpleEntity(Point1), DamageType.ENERGY, 0f, 0f, 10000f, null, 0.2f, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
        //如果：当状态!=舰船战术系统为“OUT（运行时）”
        if (state != ShipSystemStatsScript.State.OUT) {
            //减少武器赋能产出
            stats.getBallisticWeaponFluxCostMod().modifyMult(id, 0.5f * effectLevel);
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, 0.5f * effectLevel);
            stats.getMissileWeaponFluxCostMod().modifyMult(id, 0.5f * effectLevel);

            stats.getEnergyWeaponDamageMult().modifyPercent(id, 15f * effectLevel);

            Global.getSoundPlayer().playSound("TDB_huan_liu", 1f, 1f, ship.getLocation(), ship.getVelocity());
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getMissileWeaponFluxCostMod().unmodify(id);

        stats.getEnergyWeaponDamageMult().unmodify(id);
    }


    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("能量武器伤害提升15%，武器幅能下降50%", false);
        }
        return null;
    }
}
