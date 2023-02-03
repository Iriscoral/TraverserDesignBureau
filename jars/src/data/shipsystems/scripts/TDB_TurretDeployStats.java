package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.impl.combat.MineStrikeStatsAIInfoProvider;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.utils.tdb.I18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class TDB_TurretDeployStats extends BaseShipSystemScript implements MineStrikeStatsAIInfoProvider {
    private static final float RANGE_FACTOR = 900f;
    private static final float MIN_SPAWN_DIST = 75f;
    private static boolean i = false;

    public static String txt(String id) {
        return Global.getSettings().getString("scripts", id);
    }

    public static float getRange(ShipAPI ship) {
        if (ship == null) {
            return RANGE_FACTOR;
        }
        return ship.getMutableStats().getSystemRangeBonus().computeEffective(RANGE_FACTOR);
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship == null) {
            return;
        }

        if (state == State.OUT) {
            effectLevel *= effectLevel;
        }

        if (effectLevel == 1) {
            Vector2f target = ship.getMouseTarget();
            if (ship.getShipAI() != null && ship.getAIFlags().hasFlag(AIFlags.SYSTEM_TARGET_COORDS)) {
                target = (Vector2f) ship.getAIFlags().getCustom(AIFlags.SYSTEM_TARGET_COORDS);
            }

            if (target != null) {
                float dist = MathUtils.getDistance(ship, target);
                float max = getMineRange(ship) + ship.getCollisionRadius();
                if (dist > max) {
                    target = VectorUtils.getDirectionalVector(ship.getLocation(), target);
                    target.scale(max);
                    Vector2f.add(target, ship.getLocation(), target);
                }

                target = findClearLocation(target);
                if (target != null) {
                    //检测舰船是否有对应的标记插件来更改部署的炮台类型
                    if (((ShipAPI) stats.getEntity()).getVariant().getHullMods().contains("TDB_qiong_ding_G")) {
                        i = true;
                    }
                    spawnMine(ship, target);
                }
            }

        }
    }

    private static void spawnMine(ShipAPI source, Vector2f mineLoc) {
        CombatEngineAPI engine = Global.getCombatEngine();
        Vector2f currLoc = MathUtils.getRandomPointOnCircumference(mineLoc, 20f + (float) Math.random() * 20f);

        float start = (float) Math.random() * 360f;
        for (float angle = start; angle < start + 390; angle += 30f) {
            if (angle != start) {
                Vector2f loc = MathUtils.getPointOnCircumference(null, 50f + (float) Math.random() * 30f, angle);
                currLoc = Vector2f.add(mineLoc, loc, I18nUtil.nv);
            }

            for (MissileAPI other : engine.getMissiles()) {
                if (!other.isMine()) {
                    continue;
                }
                float dist = MathUtils.getDistance(currLoc, other.getLocation());
                if (dist < other.getCollisionRadius() + 40f) {
                    currLoc = null;
                    break;
                }
            }

            if (currLoc != null) {
                break;
            }
        }

        if (currLoc == null) {
            currLoc = MathUtils.getRandomPointOnCircumference(mineLoc, 20f + (float) Math.random() * 20f);
        }

        CombatFleetManagerAPI manager = engine.getFleetManager(source.getOwner());
        boolean orig = manager.isSuppressDeploymentMessages();

        manager.setSuppressDeploymentMessages(true);
        if (i) {
            ShipAPI newShip = manager.spawnShipOrWing("TDB_pao_tai", currLoc, (float) Math.random() * 360f);
            manager.setSuppressDeploymentMessages(orig);
            Global.getSoundPlayer().playSound("mine_teleport", 1f, 1f, newShip.getLocation(), newShip.getVelocity());
            //生成扭曲
            I18nUtil.easyRippleOut(newShip.getLocation(), newShip.getVelocity(), newShip.getCollisionRadius() * 4f, 100f, 1f, 20f);
            i = false;
        } else {
            ShipAPI newShip = manager.spawnShipOrWing("TDB_pao_tai_variant", currLoc, (float) Math.random() * 360f);
            manager.setSuppressDeploymentMessages(orig);
            Global.getSoundPlayer().playSound("mine_teleport", 1f, 1f, newShip.getLocation(), newShip.getVelocity());
            //生成扭曲
            I18nUtil.easyRippleOut(newShip.getLocation(), newShip.getVelocity(), newShip.getCollisionRadius() * 4f, 100f, 1f, 20f);
        }
    }


    @Override
    public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
        if (system.isOutOfAmmo()) {
            return null;
        }
        if (system.getState() != SystemState.IDLE) {
            return null;
        }

        Vector2f target = ship.getMouseTarget();
        if (target != null) {
            float dist = MathUtils.getDistance(ship, target);
            float max = getMineRange(ship) + ship.getCollisionRadius();
            if (dist > max) {
                return "超出射程";
            } else {
                return "准备完毕";
            }
        }

        return null;
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return ship.getMouseTarget() != null;
    }

    private static Vector2f findClearLocation(Vector2f dest) {
        if (isLocationClear(dest)) {
            return dest;
        }
        float incr = 50f;

        WeightedRandomPicker<Vector2f> tested = new WeightedRandomPicker<>();
        for (float distIndex = 1; distIndex <= 32f; distIndex *= 2f) {
            float start = (float) Math.random() * 360f;
            for (float angle = start; angle < start + 360; angle += 60f) {
                Vector2f loc = MathUtils.getPointOnCircumference(null, incr * distIndex, angle);
                Vector2f.add(dest, loc, loc);
                tested.add(loc);
                if (isLocationClear(loc)) {
                    return loc;
                }
            }
        }

        if (tested.isEmpty()) {
            return dest; // shouldn't happen
        }
        return tested.pick();
    }

    private static boolean isLocationClear(Vector2f loc) {
        for (ShipAPI other : Global.getCombatEngine().getShips()) {
            if (other.isShuttlePod()) {
                continue;
            }
            if (other.isFighter()) {
                continue;
            }
            Vector2f otherLoc = other.getShieldCenterEvenIfNoShield();
            float otherR = other.getShieldRadiusEvenIfNoShield();
            if (MathUtils.getDistance(loc, otherLoc) < otherR + MIN_SPAWN_DIST) {
                return false;
            }
        }

        for (CombatEntityAPI other : Global.getCombatEngine().getAsteroids()) {
            float dist = MathUtils.getDistance(other, loc);
            if (dist < other.getCollisionRadius() + MIN_SPAWN_DIST) {
                return false;
            }
        }

        return true;
    }

    @Override
    public float getFuseTime() {
        return 3f;
    }

    @Override
    public float getMineRange(ShipAPI ship) {
        return getRange(ship);
    }
}