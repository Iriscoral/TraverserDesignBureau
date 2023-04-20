package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.campaign.bar.events.TDB_HFBarEventCreator;
import data.campaign.bar.events.TDB_ren_zhao_guangBarEventCreator;
import data.scripts.world.systems.TDB_StarSystem;
import data.weapons.*;

import kentington.capturecrew.CaptiveInteractionDialogPlugin;
import kentington.capturecrew.LootAddScript;

import java.util.List;

import static com.fs.starfarer.api.Global.getSettings;

public class TDBModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        //前置mod支持

        boolean hasLazyLib = getSettings().getModManager().isModEnabled("lw_lazylib");
        if (!hasLazyLib) {
            throw new RuntimeException("TDB requires LazyLib! 穿越者协会需要LazyLib作为前置");
        }
        boolean hasMagicLib = getSettings().getModManager().isModEnabled("MagicLib");
        if (!hasMagicLib) {
            throw new RuntimeException("TDB requires MagicLib! 穿越者协会需要MagicLib作为前置");
        }
        boolean hasGraphicLib = getSettings().getModManager().isModEnabled("shaderLib");
        if (!hasGraphicLib) {
            throw new RuntimeException("TDB requires GraphicLib! 穿越者协会需要GraphicLib作为前置");
        }
    }

    @Override
    public void onNewGame() {
        //势力争霸支持
        //Nex compatibility setting, if there is no nex or corvus mode(Nex), just generate the system
        boolean haveNexerelin = getSettings().getModManager().isModEnabled("nexerelin");
        //if (!haveNexerelin || SectorManager.getCorvusMode()) {
        generate(Global.getSector());
        //}
    }

    private void generate(SectorAPI sector) {
        FactionAPI TDB = sector.getFaction("TDB");
        //设置与猫猫会长（地质协会）的初始好感为欢迎
        FactionAPI kantech = sector.getFaction("kantech");
        if (kantech != null) {
            kantech.setRelationship(TDB.getId(), RepLevel.WELCOMING);
        }
        // 设置星系
        new TDB_StarSystem().generate(sector);
        // 设置所属势力
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("TDB");
        // 设置势力与其他势力好感度
        TDB.setRelationship(Factions.LUDDIC_CHURCH, -0.7f);
        TDB.setRelationship(Factions.LUDDIC_PATH, -0.5f);
        TDB.setRelationship(Factions.PERSEAN, 0.3f);
        TDB.setRelationship(Factions.PIRATES, -0.75f);
        TDB.setRelationship(Factions.HEGEMONY, -0.6f);
    }

    //定义酒馆事件方法
    protected void addBarEvents() {
        BarEventManager bar = BarEventManager.getInstance();
        if (!bar.hasEventCreator(TDB_ren_zhao_guangBarEventCreator.class)) {
            bar.addEventCreator(new TDB_ren_zhao_guangBarEventCreator());
        }
        if (!bar.hasEventCreator(TDB_HFBarEventCreator.class)) {
            bar.addEventCreator(new TDB_HFBarEventCreator());
        }
    }

    //在游戏加载的时候使用定义的酒馆事件方法
    public void onGameLoad(boolean newGame) {
        addBarEvents();
        if (Global.getSettings().getModManager().isModEnabled("capturecrew")) {
            Global.getSector().addTransientListener(new TDBModPlugin.MarketCheckTariffs2());
        }
    }

    //导弹ai内容调用
    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        switch (missile.getProjectileSpecId()) {
            case "TDB_san_se_jin":
                return new PluginPick<MissileAIPlugin>(new TDB_san_se_jin(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_duan_hen1":
                return new PluginPick<MissileAIPlugin>(new TDB_duan_hen(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_yun_jian_shot2":
                return new PluginPick<MissileAIPlugin>(new TDB_yun_jian(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_hu_luo_bo_shot":
                return new PluginPick<MissileAIPlugin>(new TDB_hu_luo_bo(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_xiang_wei":
                return new PluginPick<MissileAIPlugin>(new TDB_xiang_wei(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case "TDB_han_chao":
                return new PluginPick<MissileAIPlugin>(new TDB_han_cao(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            default:
        }
        return null;
    }

    //Thanks for the code from Timid 此段代码来着Timid分享
    private static class MarketCheckTariffs2 extends BaseCampaignEventListener {
        private MarketCheckTariffs2() {
            super(false);
        }

        public void reportShownInteractionDialog (InteractionDialogAPI dialog) {
            if (dialog.getPlugin() instanceof CaptiveInteractionDialogPlugin) {
                for (EveryFrameScript script : Global.getSector().getScripts()) {
                    if (script instanceof LootAddScript) {
                        List<PersonAPI> Coolscript = ((LootAddScript) script).captiveOfficers;
                        for (int i=0;i<Coolscript.size();i++) {
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_GuGu"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_GuGu"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_XianYu"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_XianYu"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_LuoXue"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_LuoXue"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_YiRe"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_YiRe"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_YiFu"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_YiFu"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_XingKong"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_XingKong"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_HuoMao"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_HuoMao"));}
                            if (Coolscript.contains(Global.getSector().getImportantPeople().getPerson("TDB_782"))) {Coolscript.remove(Global.getSector().getImportantPeople().getPerson("TDB_782"));}
                        }
                    }
                }
            }
        }
    }

}
