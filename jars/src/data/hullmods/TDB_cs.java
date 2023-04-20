package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.tdb.TDB_ColorData;
import org.lwjgl.input.Keyboard;

import java.util.*;


@SuppressWarnings("ALL")
public class TDB_cs extends BaseHullMod {
    boolean AI = false;
    private static final String id = "TDB_cs";

    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }

    public void advanceInCombat(final ShipAPI ship, float amount) {
        final CombatEngineAPI engine = Global.getCombatEngine();
        List <ShipEngineControllerAPI.ShipEngineAPI> engines = ship.getEngineController().getShipEngines();

        if (!engine.getCustomData().containsKey(id)) {
            engine.getCustomData().put(id, new HashMap<>());
        }
        final Map<ShipAPI, TDB_cs.TDBState1> shipsMap = (Map)engine.getCustomData().get(id);
        if (engine.isPaused() || !engine.isEntityInPlay(ship) || !ship.isAlive()) {
            if (!ship.isAlive()) {
                shipsMap.remove(ship);
            }
            return;
        }


            if (!shipsMap.containsKey(ship)) {
                shipsMap.put(ship, new TDB_cs.TDBState1());
            } else
            {
                final TDB_cs.TDBState1 data = shipsMap.get(ship);
                final MutableShipStatsAPI stats = ship.getMutableStats();
                boolean apply = ship == engine.getPlayerShip();
                if (apply && Keyboard.isKeyDown(Keyboard.KEY_L))
                {
                    if (!data.ready2)
                    {
                        data.ready = true;
                    }
                }

                if (!apply)
                {
                    Integer fraction=0;
                    for(ShipEngineControllerAPI.ShipEngineAPI eng : engines){
                        if (eng.isSystemActivated() || eng.isDisabled()){
                            fraction++;
                        }
                    }
                    boolean flameout = fraction == engines.size();
                    if (flameout)
                    {
                        AI = true;
                    }
                }
                if ((data.ready) || AI)
                {
                    if (!data.isActive && !data.done) {
                        data.isActive = true;
                    }

                    if (data.isActive) {

                        for(ShipEngineControllerAPI.ShipEngineAPI e : engines)
                        {
                            e.setHitpoints(e.getMaxHitpoints() - 0.01f);
                        }
                        if (ship.getShield()!=null)
                        {
                            stats.getShieldTurnRateMult().modifyPercent(id, 200f);
                            stats.getShieldUnfoldRateMult().modifyPercent(id, 1000f);
                            stats.getShieldDamageTakenMult().modifyMult(id, 1f - 25f * 0.01f);

                        }
                        else
                        {
                            stats.getArmorDamageTakenMult().modifyMult(id, 1f - 25f * 0.01f);
                        }
                        ship.setJitterUnder(ship, TDB_ColorData.TDBred, 20, 2, 2);

                        data.clock += amount;
                        if (data.clock >= 5) {
                            stats.getShieldTurnRateMult().unmodify(id);
                            stats.getShieldUnfoldRateMult().unmodify(id);
                            stats.getShieldDamageTakenMult().unmodify(id);

                            stats.getArmorDamageTakenMult().unmodify(id);
                            data.isActive = false;
                            data.done = true;
                            data.ready = false;
                            data.ready2=true;
                            AI=false;
                        }
                    }
                }

            }



    }

    private final static class TDBState1 {
        boolean isActive;
        boolean done;
        boolean ready;
        boolean ready2;
        float clock;

        private TDBState1() {
            isActive = false;
            done = false;
            ready = false;
            ready2 = false;
            clock = 0;
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().getHullMods().contains(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng)|| ship.getVariant().getHullMods().contains("BSF_core");
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (!ship.getVariant().hasHullMod(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng) || !ship.getVariant().getHullMods().contains("BSF_core")) {
            return  txt("TDB_CQ_1");
        }
        return txt("TDB_CQ_2");
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {

        if (index == 0) return 25 + "%";
        if (index == 1) return 25 + "%";
        return null;
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        float pad = 10f;
        tooltip.addSectionHeading(txt("TDB_CQ_3"), Alignment.TMID, 4f);
        tooltip.addPara(txt("TDB_CQ_1"), TDB_ColorData.TDBcolor1, 4f);
        tooltip.addPara(txt("TDB_CQ_4"), 4f);
    }
}
