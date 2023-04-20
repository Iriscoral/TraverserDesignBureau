package data.weapons;

import com.fs.starfarer.api.combat.*;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class TDB_cai_yu implements OnFireEffectPlugin {

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        for (int a = 0; a < 15; a++) {
            Vector2f vel = MathUtils.getRandomPointInCone(I18nUtil.nv, (float) (a * 5), projectile.getFacing() - 2f, projectile.getFacing() + 10f);
            Vector2f pos = new Vector2f(projectile.getLocation());
            Vector2f.add(pos, vel, pos);
            Vector2f.add(vel, projectile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(0f, 20f);
            float duration = MathUtils.getRandomNumberInRange(1f, 2f);
            //engine.addSmokeParticle(pos, vel, (float) MathUtils.getRandomNumberInRange(10, 25), 1f, MathUtils.getRandomNumberInRange(0.5f, 2f), new Color(248, 230, 57, 231));
            int color = MathUtils.getRandomNumberInRange(64, 255);
            engine.addHitParticle(projectile.getLocation(), weapon.getShip().getVelocity(), 50.0F, 0.2F, 0.33F, new Color(0, 93, 200, 12));
            engine.addSmokeParticle(pos, vel, (float)MathUtils.getRandomNumberInRange(10, 25), 1.0F, MathUtils.getRandomNumberInRange(0.5F, 2.0F), new Color(color, color, color, 32));
        }
    }
}