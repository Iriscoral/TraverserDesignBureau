package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.utils.tdb.TDB_ColorData;

import java.awt.*;

@SuppressWarnings("ALL")
public class TDB_qiong_dong_G extends BaseHullMod {
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    protected static final Color COLOR = new Color(255, 77, 77);


    @Override
    public int getDisplaySortOrder() {
        return 1001;
    }

    @Override
    public int getDisplayCategoryIndex() {
        return 0;
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
    }

    public boolean isApplicableToShip(ShipAPI ship) {
        //前置插件检测
        return ship.getHullSpec().getHullId().contains("TDB_qiong_ding");
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        //将舰船的皮肤设定为XXX
           /* if (stats.getEntity() != null && ((ShipAPI) stats.getEntity()).getHullSpec() != null)
            {

                ShipHullSpecAPI ship = Global.getSettings().getHullSpec("TDB_qiong_ding_G");
                ((ShipAPI) stats.getEntity()).getVariant().setHullSpecAPI(ship);

            }*/
    }

    public String getUnapplicableReason(ShipAPI ship) {
        //显示无法安装的原因
        /*if (!ship.getHullSpec().getHullId().contains("TDB_qiong_ding")) {
            return "只能安装在穹顶级上";
        }*/
        return null;
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        return null;
    }

    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading(txt("QIONG_DING_G_1"), Alignment.TMID, 4f);
        tooltip.addPara(txt("QIONG_DING_G_2"), TDB_ColorData.TDBcolor1, 4f);
        tooltip.addPara(txt("QIONG_DING_G_3"), TDB_ColorData.TDBcolor1, 4f);
    }


    @Override
    public Color getBorderColor() {
        return COLOR;
    }

    @Override
    public Color getNameColor() {
        return COLOR;
    }

}
