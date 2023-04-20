package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.combat.CombatUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TDB_hei_ru extends BaseShipSystemScript {

    public static String txt(String id) { return Global.getSettings().getString("scripts", id); }
    public static float RANGE = 1500f;
    public static float DISRUPTION_DUR = 5f;
    public boolean READY1=true;
//    public static ShipAPI tship;
    public static int tship2;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        final ShipAPI ship = (ShipAPI) stats.getEntity();
        //获取范围内的舰船
        for (final ShipAPI tship1 : CombatUtils.getShipsWithinRange(ship.getLocation(), RANGE))
        {
            if (tship1 != null && READY1)
            {
                //获取父模块
                final ShipAPI parent = tship1.getParentStation();
                //不是飞机/穿梭机/主力舰                                                              最小载员为0                                   与本船阵营不相等                        判断是否是拥有标签的特殊舰船                   通过判断父模块是否为空来确定是否是子模块
                if (!tship1.isFighter() && !tship1.isShuttlePod() && !tship1.isCapital() && tship1.getHullSpec().getMinCrew() == 0 && ship.getOwner() != tship1.getOwner() && !tship1.getHullSpec().hasTag("restricted") && tship1.getParentStation() == null)
                {
//                    tship = tship1;
                    tship2 = tship1.getOwner();
                    //控制所有子模块
                    for (ShipAPI child : tship1.getChildModulesCopy()) {
                        child.setOwner(ship.getOwner());
                        child.setAlly(true);
                        child.getFluxTracker().beginOverloadWithTotalBaseDuration(DISRUPTION_DUR);
                    }

                    tship1.setOwner(ship.getOwner());
                    tship1.setAlly(true);
                    tship1.getFluxTracker().beginOverloadWithTotalBaseDuration(DISRUPTION_DUR);
                    //改变目标的飞机阵营
                    for (FighterWingAPI twing : tship1.getAllWings()) {
                        twing.setWingOwner(ship.getOwner());
                    }

                    if (tship1.getFluxTracker().showFloaty() ||
                            ship == Global.getCombatEngine().getPlayerShip() ||
                            tship1 == Global.getCombatEngine().getPlayerShip()) {
                        tship1.getFluxTracker().playOverloadSound();
                        tship1.getFluxTracker().showOverloadFloatyIfNeeded(txt("Hai_ru"), TDB_ColorData.TDBpink, 6f, true);
                    }
                    READY1 = false;
                    //计时器
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //阵营改回（本体)
                            tship1.setOwner(tship2);
                            //阵营改回（目标飞机)
                            for (FighterWingAPI twing : tship1.getAllWings()) {
                                twing.setWingOwner(tship2);
                            }
                            //阵营改回（目标子模块)
                            for (ShipAPI child : tship1.getChildModulesCopy()) {
                                child.setOwner(tship2);
                            }
                        }
                    }, 30000);
                    READY1 = false;
                    break;
                }
            }
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        READY1 = true;
//        CombatEngineAPI engine = Global.getCombatEngine();
//        if (tship!=null)
//        {
//            tship.setOwner(tship2);
//        }
    }


    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        boolean READY=false;
        for (ShipAPI target : CombatUtils.getShipsWithinRange(ship.getLocation(), 1500f))
        {
            if (target != null)
            {
                if (!target.isFighter() && !target.isShuttlePod() && !target.isCapital() && target.getHullSpec().getMinCrew() == 0 && ship.getOwner() != target.getOwner() && !target.getHullSpec().hasTag("restricted") && target.getParentStation() == null)
                {
                    READY = true;
                }
            }
        }
        if (system.isActive()) return true;
        return READY;
    }

}
