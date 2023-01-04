package data.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import data.scripts.util.MagicRender;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class TDB_yuen_ying implements EveryFrameWeaponEffectPlugin {

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (!engine.isPaused() && weapon.getShip().getOriginalOwner() != -1) {
            if (MagicRender.screenCheck(0.25f, weapon.getLocation()) && weapon.getChargeLevel() == 1f) {

                for (DamagingProjectileAPI poj : CombatUtils.getProjectilesWithinRange(weapon.getLocation(), 100f)) {

                    if (poj == null) {
                        break;
                    }
                    if(poj.getProjectileSpecId()!=null) {
                        if (poj.getProjectileSpecId().equals("TDB_yuen_ying_shot")) {
                            for (int a = 0; a < 10; ++a) {
                                Vector2f vel = MathUtils.getRandomPointInCone(I18nUtil.nv, (float) (a * 3), poj.getFacing() - 2f, poj.getFacing() + 15f);
                                Vector2f pos = new Vector2f(poj.getLocation());
                                Vector2f.add(pos, vel, pos);
                                Vector2f.add(vel, poj.getSource().getVelocity(), vel);
                                engine.addSwirlyNebulaParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(0, 15), MathUtils.getRandomNumberInRange(1f, 3f), 0f, 0f, 1f, TDB_ColorData.TDByellow, true);
                                engine.spawnExplosion(pos, vel, TDB_ColorData.TDByellow, 10f, 0.5f);
                            }
                        }
                    }
                }
            }
        }

        float[] angles = new float[]{-130f, 130f};

        if (weapon.isFiring()) {
            for (float Angle : angles) {
                for (int i = 0; i < 5; ++i) {
                    float spawnangle = weapon.getCurrAngle() + Angle;
                    float Point = MathUtils.getRandomNumberInRange(spawnangle - 10f, spawnangle + 10f);

                    Vector2f vel = MathUtils.getPointOnCircumference(weapon.getShip().getVelocity(), MathUtils.getRandomNumberInRange(5f, 50f), Point);
                    Vector2f spawnLocation = MathUtils.getPointOnCircumference(weapon.getLocation(), MathUtils.getRandomNumberInRange(16f, 20f), Point);
                    engine.addSwirlyNebulaParticle(spawnLocation, vel, MathUtils.getRandomNumberInRange(10f, 15f), MathUtils.getRandomNumberInRange(0.1f, 0.5f), 0f, 0f, 1f, TDB_ColorData.TDByellow2, true);
                }
            }
        }
    }
}