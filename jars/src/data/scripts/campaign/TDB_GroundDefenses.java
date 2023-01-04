package data.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.RaidDangerLevel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import java.awt.*;

public class TDB_GroundDefenses extends BaseIndustry {

    public static final float DEFENSE_BONUS_BASE = 0.5f;
    public static final float DEFENSE_BONUS_BATTERIES = 1f;

    public static final float IMPROVE_DEFENSE_BONUS = 0.25f;

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        boolean hb = Industries.HEAVYBATTERIES.equals(getId());

        demand(Commodities.SUPPLIES, size);
        demand(Commodities.MARINES, size);
        demand(Commodities.HAND_WEAPONS, size - 2);

        //supply(Commodities.MARINES, size - 2);

        //		Pair<String, Integer> deficit = getMaxDeficit(Commodities.HAND_WEAPONS);
        //		applyDeficitToProduction(1, deficit, Commodities.MARINES);

        modifyStabilityWithBaseMod();

        float mult = getDeficitMult(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
        String extra = "";
        if (mult != 1) {
            String com = getMaxDeficit(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS).one;
            extra = " (" + getDeficitText(com).toLowerCase() + ")";
        }
        float bonus = hb ? DEFENSE_BONUS_BATTERIES : DEFENSE_BONUS_BASE;
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(getModId(), 1f + bonus * mult, getNameForModifier() + extra);
        //		market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
        //		.modifyPercent(getModId(), bonus * mult * 100f, getNameForModifier() + extra);

        if (!isFunctional()) {
            supply.clear();
            unapply();
        }

    }

    @Override
    public void unapply() {
        super.unapply();

        unmodifyStabilityWithBaseMod();

        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getModId());
        //market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyPercent(getModId());
        //market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyFlat(getModId());
    }

    @Override
    public String getCurrentImage() {
        boolean batteries = Industries.HEAVYBATTERIES.equals(getId());
        if (batteries) {
            PlanetAPI planet = market.getPlanetEntity();
            if (planet == null || planet.isGasGiant()) {
                return Global.getSettings().getSpriteName("industry", "heavy_batteries_orbital");
            }
        }
        return super.getCurrentImage();
    }

    @Override
    public boolean isDemandLegal(CommodityOnMarketAPI com) {
        return true;
    }

    @Override
    public boolean isSupplyLegal(CommodityOnMarketAPI com) {
        return true;
    }

    @Override
    protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
        //return mode == IndustryTooltipMode.NORMAL && isFunctional();
        return mode != IndustryTooltipMode.NORMAL || isFunctional();
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        //if (mode == IndustryTooltipMode.NORMAL && isFunctional()) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            addStabilityPostDemandSection(tooltip, hasDemand, mode);

            boolean hb = Industries.HEAVYBATTERIES.equals(getId());
            float bonus = hb ? DEFENSE_BONUS_BATTERIES : DEFENSE_BONUS_BASE;
            addGroundDefensesImpactSection(tooltip, bonus, Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
        }
    }

    @Override
    protected int getBaseStabilityMod() {
        return 1;
    }

    @Override
    protected Pair<String, Integer> getStabilityAffectingDeficit() {
        return getMaxDeficit(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
    }

    public static final float ALPHA_CORE_BONUS = 0.5f;

    @Override
    protected void applyAlphaCoreModifiers() {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(getModId(1), 1f + ALPHA_CORE_BONUS, "Alpha core (" + getNameForModifier() + ")");
    }

    @Override
    protected void applyNoAICoreModifiers() {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getModId(1));
    }

    @Override
    protected void applyAlphaCoreSupplyAndDemandModifiers() {
        demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
    }

    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        String pre = "Alpha-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha-level AI core. ";
        }
        //String str = Strings.X + (int)Math.round(a * 100f) + "%";
        String str = Strings.X + (1f + ALPHA_CORE_BONUS) + "";

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " + "Increases ground defenses by %s.", 0f, highlight, "" + (int) ((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION, str);
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " + "Increases ground defenses by %s.", opad, highlight, "" + (int) ((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION, str);

    }

    @Override
    public boolean canImprove() {
        return true;
    }

    @Override
    protected void applyImproveModifiers() {
        if (isImproved()) {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult("ground_defenses_improve", 1f + IMPROVE_DEFENSE_BONUS, getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
        } else {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult("ground_defenses_improve");
        }
    }

    @Override
    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        String str = Strings.X + (1f + IMPROVE_DEFENSE_BONUS) + "";

        if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
            info.addPara("Ground defenses increased by %s.", 0f, highlight, str);
        } else {
            info.addPara("Increases ground defenses by %s.", 0f, highlight, str);
        }

        info.addSpacer(opad);
        super.addImproveDesc(info, mode);
    }

    @Override
    public RaidDangerLevel adjustCommodityDangerLevel(String commodityId, RaidDangerLevel level) {
        return level.next();
    }

    @Override
    public RaidDangerLevel adjustItemDangerLevel(String itemId, String data, RaidDangerLevel level) {
        return level.next();
    }

}




