package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;



public class TDB_guang_shu implements BeamEffectPlugin {

    private boolean runOnce;


    public TDB_guang_shu() {
        runOnce = false;
    }

    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        WeaponAPI weapon = beam.getWeapon();
        CombatEntityAPI target = beam.getDamageTarget();
        ShipAPI ship = weapon.getShip();
        float width = beam.getWidth();
        float i;

        Vector2f point = beam.getRayEndPrevFrame();
        Vector2f weaponLocation = weapon.getLocation();
        Vector2f shipVelocity = ship.getVelocity();

        engine.addHitParticle(point, MathUtils.getPointOnCircumference((Vector2f)null, MathUtils.getRandomNumberInRange(100f, 200f), MathUtils.getRandomNumberInRange(0f, 360f)), 7f, 5f, MathUtils.getRandomNumberInRange(0.5f, 1f), beam.getFringeColor());

        //运行一次后方代码
        if (weapon.getChargeLevel() >= 1f && !this.runOnce) {
            this.runOnce = true;
            if (weapon.getChargeLevel() > 0)
            {
                engine.spawnExplosion(weaponLocation, shipVelocity, TDB_ColorData.TDBblue3, 50f, 0.15f);
            }
        }

        //运行
        if (this.runOnce) {
            //获取光束的距离
            i = width * 0.1f * MathUtils.getDistance(beam.getTo(), beam.getFrom()) * amount * 0.15f * weapon.getChargeLevel();

            for (int a = 0; a < i; ++a) {
                Vector2f loc = MathUtils.getRandomPointInCircle(MathUtils.getRandomPointOnLine(beam.getFrom(), beam.getTo()), width * 0.1f);
                if (Global.getCombatEngine().getViewport().isNearViewport(loc, 30f)) {
                    Vector2f vel = MathUtils.getRandomPointInCircle(new Vector2f(ship.getVelocity().x * 0.5f, ship.getVelocity().y * 0.5f), 50f);
                    engine.addSmoothParticle(loc, vel, MathUtils.getRandomNumberInRange(5f, 10f), weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.4f, 0.9f), beam.getFringeColor());
                    //生成和设定颜色相反的粒子
                    //engine.addNegativeParticle(loc, var27, MathUtils.getRandomNumberInRange(5f, 10f), weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.4f, 0.9f), beam.getFringeColor());
                    //生成螺旋状大片星云粒子
                    //engine.addSwirlyNebulaParticle(loc, var27, 40f * (0.75f + (float)Math.random() * 0.5f), MathUtils.getRandomNumberInRange(1f, 3f), 0f, 0f, 1f, new Color(beam.getFringeColor().getRed(), beam.getFringeColor().getGreen(), beam.getFringeColor().getBlue(), 100),true);
                }
            }
        }
    }
}
