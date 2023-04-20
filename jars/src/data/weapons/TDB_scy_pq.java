package data.weapons;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class TDB_scy_pq implements EveryFrameWeaponEffectPlugin {
    public boolean ready = true;
    private final IntervalUtil intervalUtil = new IntervalUtil(1f, 1f);

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
//        Color color1 = new Color(33, 73, 204, 16);
//        Color color2 = new Color(33, 93, 204, 80);
//        Color color3 = new Color(162, 92, 43, 126);
        if (ready && !engine.isPaused())
        {
            if (ship.getSystem().isActive() && ship.getSystem().getEffectLevel()>=0.4f)
            {
                    float[] angles = new float[]{-170f, 170f};
                    for (float Angle : angles) {
                        for (int i = 0; i < 5; ++i) {
                            float spawnangle = weapon.getCurrAngle() + Angle;
                            float Point = MathUtils.getRandomNumberInRange(spawnangle - 10f, spawnangle + 10f);

                            Vector2f vel = MathUtils.getPointOnCircumference(weapon.getShip().getVelocity(), MathUtils.getRandomNumberInRange(5f, 50f), Point);
                            Vector2f spawnLocation = MathUtils.getPointOnCircumference(weapon.getLocation(), MathUtils.getRandomNumberInRange(4f, 8f), Point);
                            engine.addSwirlyNebulaParticle(spawnLocation, vel, MathUtils.getRandomNumberInRange(10f, 15f), MathUtils.getRandomNumberInRange(0.1f, 0.5f), 0f, 0f, 1f, TDB_ColorData.TDBred3, true);
                        }
                    }
            }
            if (ship.getSystem().getEffectLevel() == 1)
            {
                CombatEntityAPI poj = engine.spawnProjectile(weapon.getShip(), null, "TDB_leng_que", weapon.getFirePoint(0), ship.getFacing() - 180f, weapon.getShip().getVelocity());
                ready = false;
            }
//            float opacity = MathUtils.getRandomNumberInRange(0.6f, 1f);
//            float duration = MathUtils.getRandomNumberInRange(0.4f, 0.8f);
//            for (int i = 0; i < 1; i++)
//            {
//
//                engine.addNegativeNebulaParticle(weapon.getLocation(), I18nUtil.nv, MathUtils.getRandomNumberInRange(5f, 15f), 1.2f, 0.25f, opacity, duration,color3);
//
//                engine.addNegativeNebulaParticle(weapon.getLocation(), I18nUtil.nv, MathUtils.getRandomNumberInRange(5f, 15f), 1.2f, 0.25f, opacity, duration, color3);
//
//                engine.addNebulaParticle(weapon.getLocation(), I18nUtil.nv, MathUtils.getRandomNumberInRange(10f, 30f), 1.2f, 0.25f, opacity, duration, color2);
//
//                engine.addNebulaSmokeParticle(weapon.getLocation(), I18nUtil.nv, MathUtils.getRandomNumberInRange(10f, 30f), 1.2f, 0.25f, opacity, duration, color1);
////                engine.addNegativeNebulaParticle(weapon.getLocation(), I18nUtil.nv, MathUtils.getRandomNumberInRange(5f, 15f), 1.2f, 0.25f, opacity, duration, TDB_ColorData.TDBblue);
////                engine.addNebulaParticle(weapon.getLocation(), I18nUtil.nv, MathUtils.getRandomNumberInRange(10f, 30f), 1.2f, 0.25f, opacity, duration, TDB_ColorData.TDBblue);
//            }
        }
        if (ship.getSystem().isCoolingDown())
        {
            ready = true;
        }
    }
}