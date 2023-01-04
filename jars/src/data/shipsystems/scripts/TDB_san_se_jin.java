package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import data.scripts.util.MagicRender;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class TDB_san_se_jin extends BaseShipSystemScript {

    public static final float FLUX_USE_MULT = 0.3f;

    private boolean runOnce = false;
    private WeaponAPI SYS1;

    private boolean a = false;


    //开火角度
    private static final Map<Integer, Float> LAUCH_ANGLE = new HashMap<>(8);

    static {
        LAUCH_ANGLE.put(0, 107.5f);
        LAUCH_ANGLE.put(1, -107.5f);
        LAUCH_ANGLE.put(2, 123.5f);
        LAUCH_ANGLE.put(3, -123.5f);
        LAUCH_ANGLE.put(4, 146.5f);
        LAUCH_ANGLE.put(5, -146.5f);
        LAUCH_ANGLE.put(6, 163.5f);
        LAUCH_ANGLE.put(7, -163.5f);
    }

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        CombatEngineAPI engine = Global.getCombatEngine();
        ShipAPI ship = (ShipAPI) stats.getEntity();
        Vector2f goldenPoint = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius());


        if (!a) {
            a = true;
        }

        //运行一次
        if (!runOnce) {
            for (WeaponAPI w : ship.getAllWeapons()) {
                if ("SYS1".equals(w.getSlot().getId())) {
                    SYS1 = w;
                }
            }
            runOnce = true;
        }

        //进行一个残影的拖
        MagicRender.battlespace(
                Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
                new Vector2f(ship.getLocation().getX(), ship.getLocation().getY()),
                new Vector2f(0, 0),
                new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
                new Vector2f(0, 0),
                ship.getFacing() - 90f,
                0f,
                TDB_ColorData.TDBblue5,
                true,
                0f,
                0f,
                0f,
                0f,
                1f,
                0.1f,
                0.1f,
                2.5f,
                CombatEngineLayers.BELOW_SHIPS_LAYER);

        //buff
        //如果：当状态=舰船战术系统为“OUT（运行时）”
        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id);
            stats.getMaxTurnRate().unmodify(id);
        } else {
            //最大航速
            stats.getMaxSpeed().modifyFlat(id, 110f);
            //加速度
            stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
            //减速度
            stats.getDeceleration().modifyPercent(id, 200f * effectLevel);
            //转向速度
            stats.getTurnAcceleration().modifyFlat(id, 30f * effectLevel);
            stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel);
            stats.getMaxTurnRate().modifyFlat(id, 20f);
            stats.getMaxTurnRate().modifyPercent(id, 100f);

            //武器幅能消耗减免
            stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - (1f - FLUX_USE_MULT) * effectLevel);
            stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - (1f - FLUX_USE_MULT) * effectLevel);
            stats.getMissileWeaponFluxCostMod().modifyMult(id, 1f - (1f - FLUX_USE_MULT) * effectLevel);

            //护盾伤害减免
            stats.getShieldDamageTakenMult().modifyMult(id, 1f - (1f - 0.1f) * effectLevel);

            //实弹/能量武器伤害提升
            stats.getEnergyWeaponDamageMult().modifyFlat(id, 0.20f * effectLevel);
            stats.getBallisticWeaponDamageMult().modifyFlat(id, 0.20f * effectLevel);

            engine.addSmoothParticle(goldenPoint, I18nUtil.nv, MathUtils.getRandomNumberInRange(4f, 10f), 1f, MathUtils.getRandomNumberInRange(0.4f, 1f), TDB_ColorData.TDBblue4);
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        if (stats.getEntity() instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) stats.getEntity();
            CombatEngineAPI engine = Global.getCombatEngine();


            if (a) {

                FluxTrackerAPI flux = ship.getFluxTracker();

                //不同幅能状态下触发的效果
                if (flux.getFluxLevel() == 0f) {

                    //导弹发射
                    for (int i = 0; i < 2; i++) {
                        engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                        engine.spawnMuzzleFlashOrSmoke(ship, SYS1.getSlot(), SYS1.getSpec(), 0, ship.getFacing() + LAUCH_ANGLE.get(i));

                    }
                }

                if (flux.getFluxLevel() > 0f && flux.getFluxLevel() <= 0.25f) {
                    for (int i = 0; i < 4; i++) {

                        engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                        engine.spawnMuzzleFlashOrSmoke(ship, SYS1.getSlot(), SYS1.getSpec(), 0, ship.getFacing() + LAUCH_ANGLE.get(i));

                    }
                }

                if (flux.getFluxLevel() > 0.25f && flux.getFluxLevel() <= 0.4f) {
                    for (int i = 0; i < 6; i++) {

                        engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                        engine.spawnMuzzleFlashOrSmoke(ship, SYS1.getSlot(), SYS1.getSpec(), 0, ship.getFacing() + LAUCH_ANGLE.get(i));


                    }
                }

                if (flux.getFluxLevel() > 0.4f) {
                    for (int i = 0; i < 8; i++) {

                        engine.spawnProjectile(ship, SYS1, "TDB_san_se_jin", SYS1.getLocation(), ship.getFacing() + LAUCH_ANGLE.get(i), null);
                        engine.spawnMuzzleFlashOrSmoke(ship, SYS1.getSlot(), SYS1.getSpec(), 0, ship.getFacing() + LAUCH_ANGLE.get(i));


                    }

                    a = false;
                }
            }
        }

        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);

        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getMissileWeaponFluxCostMod().unmodify(id);

        stats.getShieldDamageTakenMult().unmodify(id);

        stats.getEnergyWeaponDamageMult().unmodify(id);
        stats.getBallisticWeaponDamageMult().unmodify(id);
    }


    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) {
            return new StatusData("提高机动性与最高航速，武器所需幅能下降30%，护盾所受伤害减免10%，实弹/能量武器提升10%", false);
        }
        if (index == 1) {
            return new StatusData("技能结束时依据幅能水平释放压制弹幕", false);
        }
        return null;
    }

}
