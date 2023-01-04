package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.utils.tdb.I18nUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class TDB_yun_jian implements MissileAIPlugin, GuidedMissileAI {
    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private final IntervalUtil intervalUtil = new IntervalUtil(1f, 1f);


    public TDB_yun_jian(MissileAPI missile) {
        this.missile = missile;
    }

    @Override
    public void advance(float amount) {
        if (engine != Global.getCombatEngine()) {
            this.engine = Global.getCombatEngine();
        }
        //cancelling IF: skip the AI if the game is paused, the missile is engineless or fading
        if (Global.getCombatEngine().isPaused()) {
            return;
        }
        missile.giveCommand(ShipCommand.ACCELERATE);
        intervalUtil.advance(0.01f);
        if (intervalUtil.intervalElapsed()) {
            mirv(missile);
        }
    }

    private void mirv(MissileAPI missile) {
        for (int a = 0; a < 10; ++a) {
            Vector2f vel = MathUtils.getRandomPointInCone(I18nUtil.nv, (float) (a * 50), missile.getFacing() - 182f, missile.getFacing() - 180f);
            Vector2f.add(vel, missile.getSource().getVelocity(), vel);
            float size = MathUtils.getRandomNumberInRange(20f, 60f);
            float duration = MathUtils.getRandomNumberInRange(1f, 2f);
            Global.getCombatEngine().addSmokeParticle(missile.getLocation(), vel, size, 100f, duration, Color.lightGray);
        }
        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "TDB_yun_jian3", missile.getLocation(), missile.getFacing(), null);
        Global.getCombatEngine().removeEntity(missile);
    }

    @Override
    public CombatEntityAPI getTarget() {
        return null;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
    }
}
