package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static com.fs.starfarer.api.combat.CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER;

public class TDB_han_cao implements MissileAIPlugin, GuidedMissileAI {
    private static CombatEngineAPI engine;
    private final MissileAPI missile;
    private final IntervalUtil intervalUtil = new IntervalUtil(2f, 2f);

    public TDB_han_cao(MissileAPI missile) {
        this.missile = missile;
    }

    @Override
    public void advance(float amount) {
        if (engine != Global.getCombatEngine()) {
            engine = Global.getCombatEngine();
        }
        //cancelling IF: skip the AI if the game is paused, the missile is engineless or fading
        if (Global.getCombatEngine().isPaused()) {
            return;
        }
        Vector2f nv1 = new Vector2f(150,20);
        SpriteAPI l = Global.getSettings().getSprite("campaignEntities","fusion_lamp_glow");
        MagicRender.singleframe(l,missile.getLocation(),nv1,0, TDB_ColorData.TDBblue2,true,ABOVE_SHIPS_AND_MISSILES_LAYER);
        missile.giveCommand(ShipCommand.ACCELERATE);
        intervalUtil.advance(amount);
        if (intervalUtil.intervalElapsed()) {
            //mirv(missile);
            spawnShip(missile.getSource(), missile);

        }
    }

    private static void spawnShip(ShipAPI source, MissileAPI missile) {
        CombatFleetManagerAPI manager = engine.getFleetManager(source.getOwner());
        boolean orig = manager.isSuppressDeploymentMessages();
        manager.setSuppressDeploymentMessages(true);
        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "TDB_han_chao1", missile.getLocation(), missile.getFacing()+15, null);
        Global.getCombatEngine().spawnProjectile(missile.getSource(), missile.getWeapon(), "TDB_han_chao2", missile.getLocation(), missile.getFacing()-15, null);
        ShipAPI newShip = manager.spawnShipOrWing("TDB_wei_guang_zheng_wing", missile.getLocation(), missile.getFacing(),3);
        //ShipAPI newShip = manager.spawnShipOrWing("HP_wall_bomber");
        Global.getCombatEngine().removeEntity(missile);
        manager.setSuppressDeploymentMessages(orig);
        Global.getCombatEngine().spawnExplosion(missile.getLocation(), new Vector2f(0, 0), Color.darkGray, 200f, 2f);
    }

    @Override
    public CombatEntityAPI getTarget() {
        return null;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        //this.target = target;

    }

}
