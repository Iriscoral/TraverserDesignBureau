package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;


public class TDB_rzg extends BaseHullMod {


    public void advanceInCombat(ShipAPI ship, float amount) {

        CombatEngineAPI engine = Global.getCombatEngine();
        Vector2f goldenPoint = MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius());

        //防爆
        if (ship.isAlive()) {
            FluxTrackerAPI flux = ship.getFluxTracker();
            MutableShipStatsAPI stats = ship.getMutableStats();
            float level = (flux.getFluxLevel() - 0.5f) / 0.3f;
            float level2 = flux.getFluxLevel() / 0.3f;

            //判断舰船幅能状态给予不同效果
            if (flux.getFluxLevel() < 0.5f) {
                stats.getEnergyWeaponDamageMult().modifyPercent("TDB_rzg", 15f * level2);
            } else {
                stats.getEnergyWeaponDamageMult().unmodify("TDB_rzg");
            }

            if (flux.getFluxLevel() > 0.5f) {
                engine.addSmoothParticle(goldenPoint, I18nUtil.nv, MathUtils.getRandomNumberInRange(4f, 10f), 1f, MathUtils.getRandomNumberInRange(0.4f, 1f), TDB_ColorData.TDBpink);
                ship.getMutableStats().getFluxDissipation().modifyPercent("TDB_rzg", 20f * level);
                //ship.getMutableStats().getHardFluxDissipationFraction().modifyFlat("TDB_rzg",1f - 1f * 0.01f);
            } else {
                ship.getMutableStats().getFluxDissipation().unmodify("TDB_rzg");
                //ship.getMutableStats().getHardFluxDissipationFraction().unmodify("TDB_rzg");
            }


            if (Global.getCombatEngine().getPlayerShip() == ship) {
                if (flux.getFluxLevel() < 0.5f) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(
                            "TDB_rzg",
                            "graphics/icons/hullsys/high_energy_focus.png",
                            "辉光管控能系统",
                            "黑狐系统启动,武器伤害提升",
                            true
                    );
                }

                if (flux.getFluxLevel() > 0.5f) {
                    Global.getCombatEngine().maintainStatusForPlayerShip(
                            "TDB_rzg",
                            "graphics/icons/hullsys/missile_autoforge.png",
                            "辉光管控能系统",
                            "白狐系统启动,舰船幅能耗散加快",
                            true
                    );
                }
            }
        }
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {

        tooltip.addSectionHeading("备注", Alignment.TMID, 4f);

        tooltip.addPara("白狐冷却系统", TDB_ColorData.TDBcolor1, 4f);
        tooltip.addPara("当舰船幅能大于50%时,软幅能耗散速度随着幅能的上升而增加,最高增加20%", 4f);

        tooltip.addPara("", 2f);

        tooltip.addPara("黑狐增压系统", TDB_ColorData.TDBpink, 4f);
        tooltip.addPara("当舰船幅能小于50%时,武器伤害会随着幅能的上升而增加,最高增加15%", 4f);
    }
}
