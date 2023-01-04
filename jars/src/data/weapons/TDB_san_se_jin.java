package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicLensFlare;
import data.utils.tdb.TDB_ColorData;

public class TDB_san_se_jin implements MissileAIPlugin, GuidedMissileAI {
    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private final IntervalUtil intervalUtil = new IntervalUtil(1.5f, 1.5f);


    public TDB_san_se_jin(MissileAPI missile) {
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
        intervalUtil.advance(0.015f);
        if (intervalUtil.intervalElapsed()) {
            mirv(missile);
        }
    }

    private void mirv(MissileAPI missile) {
        MagicLensFlare.createSharpFlare(engine, missile.getSource(), missile.getLocation(), 10f, 300, 90, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
        MagicLensFlare.createSharpFlare(engine, missile.getSource(), missile.getLocation(), 10f, 300, 270, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
        //Global.getCombatEngine().spawnExplosion(missile.getLocation(), new Vector2f(0, 0), Color.darkGray, 200f, 2f);
        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "TDB_san_se_jin1", missile.getLocation(), missile.getFacing(), null);
        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "TDB_san_se_jin2", missile.getLocation(), missile.getFacing(), null);
        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "flarelauncher3", missile.getLocation(), missile.getFacing(), null);
        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "flarelauncher3", missile.getLocation(), missile.getFacing(), null);
        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "flarelauncher3", missile.getLocation(), missile.getFacing(), null);
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
