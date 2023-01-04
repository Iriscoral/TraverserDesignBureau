package data.weapons;

import com.fs.starfarer.api.combat.*;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class TDB_ning_hua implements OnFireEffectPlugin {

    public TDB_ning_hua() {
        //float a = 180f;
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        for (int a = 0; a < 10; a++) {
            Vector2f vel = MathUtils.getRandomPointInCone(I18nUtil.nv, (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(0f, 20f);
            float duration = MathUtils.getRandomNumberInRange(1f, 2f);
            //engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
            engine.spawnExplosion(pos, vel, TDB_ColorData.TDBblue3, 10f, 0.15f);
            engine.addNebulaParticle(pos, vel, size * 1.5f, 1.2f, 0.25f / duration, 0f, duration, TDB_ColorData.TDBblue3);
        }
    }
}