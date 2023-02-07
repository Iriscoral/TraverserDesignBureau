package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.utils.tdb.I18nUtil;
import data.utils.tdb.TDB_ColorData;

import java.awt.*;

public class TDB_ruo_ci_du_cheng extends BaseLogisticsHullMod {

    protected static final Color COLOR = new Color(81, 106, 189);
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    //设置被引用
    public static final String TDB_ruo_ci_du_cheng = "TDB_ruo_ci_du_cheng";

    //日冕抗性设置
    public static final float CORONA_EFFECT_REDUCTION = 0.5f;
    public static final float EMP = 0.8f;

    public static final float Missile = 5f;
    public static final float HighExplosive = 10f;

    public void advanceInCombat(ShipAPI ship, float amount) {
        //护盾相关
        ShieldAPI shield = ship.getShield();
        if (shield != null) {
            shield.setRadius(shield.getRadius(), I18nUtil.getFxName("TDB_shields"), null);
            shield.setInnerRotationRate(shield.getInnerRotationRate() * 0);
        }
    }

    //日冕抗性
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipVariantAPI variant = stats.getVariant();

        //stats.getBeamDamageTakenMult().modifyMult(id, BEAM_DAMAGE_REDUCTION);
        stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, CORONA_EFFECT_REDUCTION);
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - EMP * 0.01f);

        //设定安装强化护盾后的效果
        if (variant.getHullMods().contains("hardenedshieldemitter")) {
            stats.getHighExplosiveShieldDamageTakenMult().modifyMult(id, 1f - HighExplosive * 0.01f);
            stats.getKineticShieldDamageTakenMult().modifyMult(id, 1f - HighExplosive * 0.01f);
            stats.getFragmentationDamageTakenMult().modifyMult(id, 1f - HighExplosive * 0.01f);
            stats.getMissileShieldDamageTakenMult().modifyMult(id, 1f - Missile * 0.01f);
        }
    }

    //让文本用%能检测到数值
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return Math.round((1f - CORONA_EFFECT_REDUCTION) * 100f) + "%";
        if (index == 1) return Math.round((1f - EMP) * 100f) + "%";
        return null;
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        TooltipMakerAPI text;
        float pad = 10f;
        tooltip.addSectionHeading(txt("RCDC_1"), Alignment.TMID, 4f);
        text = tooltip.beginImageWithText("graphics/hullmods/hardened_shields.png", 32);
        if (!ship.getVariant().hasHullMod("hardenedshieldemitter")) {
                text.addPara(txt("RCDC_2"), 0, Misc.getHighlightColor(), TDB_ColorData.TDBred, txt("RCDC_4"));
            text.addPara(txt("RCDC_3"), 4f);
        } else {
            text.addPara(txt("RCDC_2"), 0, Misc.getHighlightColor(), TDB_ColorData.TDBgreen, txt("RCDC_5"));
            text.addPara(txt("RCDC_3"), 4f);
        }
        tooltip.addImageWithText(pad);
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
