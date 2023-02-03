package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import data.utils.tdb.TDB_ColorData;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TDB_you_ji extends BaseHullMod {

    private final Color color = new Color(80, 166, 166, 255);
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    //设置引擎颜色/扩展长度
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        ship.getEngineController().fadeToOtherColor(this, color, null, 1f, 0.4f);
        ship.getEngineController().extendFlame(this, 0.5f, 0.25f, 0.25f);

        //防爆
        if (ship.isAlive()) {
            FluxTrackerAPI flux = ship.getFluxTracker();
            MutableShipStatsAPI stats = ship.getMutableStats();

            HullSize hullSize = ship.getHullSize();

            if (hullSize == HullSize.CAPITAL_SHIP)
            {
                if (flux.getFluxLevel()>0f)
                {
                    float speed = 10f*flux.getFluxLevel();
                    if (Global.getCombatEngine().getPlayerShip() == ship) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                "TDB_you_ji",
                                "graphics/icons/hullsys/high_energy_focus.png",
                                "航速提升",
                                ""+(Math.floor(10f-speed)),
                                false
                        );
                    }
                    stats.getMaxSpeed().modifyFlat("TDB_you_ji", 10f-speed);
                }
                else
                {
                    stats.getMaxSpeed().modifyFlat("TDB_you_ji", 10f);
                    if (Global.getCombatEngine().getPlayerShip() == ship) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                "TDB_you_ji2",
                                "graphics/icons/hullsys/high_energy_focus.png",
                                "航速提升",
                                ""+10f,
                                false
                        );
                    }
                }
            }
            else if (hullSize == HullSize.CRUISER)
            {
                if (flux.getFluxLevel()>0f)
                {
                    float speed = 15f*flux.getFluxLevel();
                    if (Global.getCombatEngine().getPlayerShip() == ship) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                "TDB_you_ji",
                                "graphics/icons/hullsys/high_energy_focus.png",
                                "航速提升",
                                ""+(Math.floor(15f-speed)),
                                false
                        );
                    }
                    stats.getMaxSpeed().modifyFlat("TDB_you_ji", 15f-speed);
                }
                else
                {
                    stats.getMaxSpeed().modifyFlat("TDB_you_ji", 15f);
                    if (Global.getCombatEngine().getPlayerShip() == ship) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                "TDB_you_ji2",
                                "graphics/icons/hullsys/high_energy_focus.png",
                                "航速提升",
                                ""+15f,
                                false
                        );
                    }
                }
            }
            else
            {
                if (flux.getFluxLevel()>0f)
                {
                    float speed = 20f*flux.getFluxLevel();
                    if (Global.getCombatEngine().getPlayerShip() == ship) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                "TDB_you_ji",
                                "graphics/icons/hullsys/high_energy_focus.png",
                                "航速提升",
                                ""+(Math.floor(20f-speed)),
                                false
                        );
                    }
                    stats.getMaxSpeed().modifyFlat("TDB_you_ji", 20f-speed);
                }
                else
                {
                    stats.getMaxSpeed().modifyFlat("TDB_you_ji", 20f);
                    if (Global.getCombatEngine().getPlayerShip() == ship) {
                        Global.getCombatEngine().maintainStatusForPlayerShip(
                                "TDB_you_ji2",
                                "graphics/icons/hullsys/high_energy_focus.png",
                                "航速提升",
                                ""+20f,
                                false
                        );
                    }
                }
            }

            if (flux.getFluxLevel()>0.25f)
            {
                float maneuver = 40f*flux.getFluxLevel();
                if (Global.getCombatEngine().getPlayerShip() == ship) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(
                            "TDB_you_ji1",
                            "graphics/icons/hullsys/high_energy_focus.png",
                            "机动提升",
                            ""+Math.floor(maneuver),
                            false
                    );
                }
                stats.getAcceleration().modifyPercent("TDB_you_ji", maneuver);
                stats.getDeceleration().modifyPercent("TDB_you_ji", maneuver);
                stats.getTurnAcceleration().modifyPercent("TDB_you_ji", maneuver);
                stats.getMaxTurnRate().modifyPercent("TDB_you_ji", maneuver);
            }

        }

    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, HullSize hullSize) {

        if (index == 0) return 10 + "";
        if (index == 1) return 15 + "";
        if (index == 2) return 20 + "";
        if (index == 3) return 20 + "";
        if (index == 4) return 25 + "%";
        if (index == 5) return 40 + "%";
        return null;
    }


    //检测前置插件/检测冲突插件
    private final Set<String> BLOCKED_HULLMODS = new HashSet<>();

    {
        BLOCKED_HULLMODS.add("unstable_injector");
        BLOCKED_HULLMODS.add("auxiliarythrusters");
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, "TDB_you_ji");
            }
        }
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship.getVariant().getHullMods().contains(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng) && !ship.getVariant().getHullMods().contains("unstable_injector") && !ship.getVariant().getHullMods().contains("auxiliarythrusters");
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        if (!ship.getVariant().hasHullMod(TDB_ruo_ci_du_cheng.TDB_ruo_ci_du_cheng)) {
            return "只能安装在穿越者协会的船体上";
        }
        if (ship.getVariant().getHullMods().contains("unstable_injector")) {
            return "与不稳定喷射器[unstable_injector]冲突";
        }
        if (ship.getVariant().getHullMods().contains("auxiliarythrusters")) {
            return "与辅助推进器[auxiliarythrusters]冲突";
        }
        return "状态正常";
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading("备注", Alignment.TMID, 4f);
        tooltip.addPara("只能安装在穿越者协会的船体上", TDB_ColorData.TDBcolor1, 4f);
        tooltip.addPara("与[%s]冲突", 4f, Misc.getHighlightColor(), TDB_ColorData.TDBred, "辅助推进器");
        tooltip.addPara("与[%s]冲突", 4f, Misc.getHighlightColor(), TDB_ColorData.TDBred, "不稳定喷射器");
    }
}
