package data.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicAnim;
import data.utils.tdb.TDB_ColorData;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TDB_scy implements EveryFrameWeaponEffectPlugin {


    private WeaponAPI rpanel;
    private WeaponAPI lpanel;

    private ShipAPI ship;
    private ShipSystemAPI system;

    public final String lID = "z";
    public final String rID = "y";

    private boolean runOnce = false;

    private float panelWidth, panelHeight;

    private float sp = 1;
    private boolean Drive = false;


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        //遍历舰体武器
        if (!runOnce || ship == null || system == null) {
            ship = weapon.getShip();

            system = ship.getSystem();
            List<WeaponAPI> weapons = ship.getAllWeapons();
            for (WeaponAPI w : weapons) {
                switch (w.getSlot().getId()) {

                    case lID:
                        lpanel = w;
                        panelHeight = w.getSprite().getHeight();
                        panelWidth = w.getSprite().getWidth();
                        break;
                    case rID:
                        rpanel = w;
                        break;
                }
            }
            runOnce = true;
            return;
        }

        if (!this.ship.getTravelDrive().isActive() && !this.ship.getFluxTracker().isVenting()) {
            if (Drive) {
                this.sp = Math.max(0, this.sp - amount);
                if (this.sp == 0) {
                    Drive = false;
                }
            } else {
                this.sp = system.getEffectLevel();
            }
        } else {
            this.sp = Math.min(1, this.sp + amount);
            Drive = true;
        }

        //当系统激活时
        if (system.isActive() || sp > 0) {

            float rotate = MagicAnim.smoothNormalizeRange(this.sp, 0f, 0.6f);
            float recess = MagicAnim.smoothNormalizeRange(this.sp, 0f, 0.8f);


            lpanel.getSprite().setCenter(panelWidth / 2 + recess * 0, panelHeight / 2 + rotate * -8);
            rpanel.getSprite().setCenter(panelWidth / 2 - recess * 0, panelHeight / 2 + rotate * -8);


        }
    }
}