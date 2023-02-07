package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.skills.NeuralLinkScript;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicLensFlare;
import data.utils.tdb.TDB_ColorData;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static data.utils.tdb.I18nUtil.easyRippleOut;

@SuppressWarnings("ALL")
public class TDB_ji_yu_yun extends BaseHullMod {
    static boolean yq = true;
    public static String txt(String id) {
        return Global.getSettings().getString("hullmods", id);
    }
    //模块相关
    private void advanceChild(ShipAPI child, ShipAPI parent) {

        //引擎同步
        ShipEngineControllerAPI ec = parent.getEngineController();
        if (ec != null) {
            if (parent.isAlive()) {
                if (ec.isAccelerating()) {
                    child.giveCommand(ShipCommand.ACCELERATE, null, 0);
                }

                if (ec.isAcceleratingBackwards()) {
                    child.giveCommand(ShipCommand.ACCELERATE_BACKWARDS, null, 0);
                }

                if (ec.isDecelerating()) {
                    child.giveCommand(ShipCommand.DECELERATE, null, 0);
                }

                if (ec.isStrafingLeft()) {
                    child.giveCommand(ShipCommand.STRAFE_LEFT, null, 0);
                }

                if (ec.isStrafingRight()) {
                    child.giveCommand(ShipCommand.STRAFE_RIGHT, null, 0);
                }

                if (ec.isTurningLeft()) {
                    child.giveCommand(ShipCommand.TURN_LEFT, null, 0);
                }

                if (ec.isTurningRight()) {
                    child.giveCommand(ShipCommand.TURN_RIGHT, null, 0);
                }
            }

            if (parent.getTravelDrive().isActive()) {
                child.toggleTravelDrive();
            } else {
                child.getTravelDrive().deactivate();
                //设置冷却以达到巡航同步关闭的效果（废弃）
                //child.getTravelDrive().setCooldown(0f);
            }

            float depCost = 0f;
            if (parent.getFleetMember() != null) {
                depCost = parent.getFleetMember().getDeployCost();
            }
            if (parent.getHitpoints()<3000 && parent.getCurrentCR()>depCost)
            {
                child.getEngineController().fadeToOtherColor(this, TDB_ColorData.TDBpurplish_red, TDB_ColorData.TDBblue4, 1f, 0.4f);
                child.getEngineController().extendFlame(this, 20f, 1f, 3f);
                child.setJitterUnder(child,TDB_ColorData.TDBblue,5,6,4);
                child.blockCommandForOneFrame(ShipCommand.USE_SYSTEM);
            }

            //同步模块引擎强制熄火
            ShipEngineControllerAPI cec = child.getEngineController();
            if ((ec.isFlamingOut() || ec.isFlamedOut()) && !cec.isFlamingOut() && !cec.isFlamedOut()) {
                child.getEngineController().forceFlameout(true);
            }
        }


        //如果都为集结状态
        if(parent.isPullBackFighters() ^ child.isPullBackFighters())
        {
            //使模块指令为召回舰载机
            child.giveCommand(ShipCommand.PULL_BACK_FIGHTERS, null, 0);
        }

        //如果核心目标不为空
        if (((Global.getCombatEngine().getPlayerShip() == parent) || (parent.getAIFlags() == null)) && (parent.getShipTarget() != null))
        {
            child.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET, 1f, parent.getShipTarget());
        }
        if (parent.getAIFlags() != null && parent.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET) != null)
        {
            //设置子模块目标
            child.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET, 1f, parent.getAIFlags().getCustom(ShipwideAIFlags.AIFlags.CARRIER_FIGHTER_TARGET));
        }



        //同步不稳定喷射器效果
        if (parent.getVariant().hasHullMod("unstableinjector")) {
            child.getMutableStats().getBallisticWeaponRangeBonus().modifyMult("TDB_ji_yu_yun", 0.85f);
            child.getMutableStats().getEnergyWeaponRangeBonus().modifyMult("TDB_ji_yu_yun", 0.85f);
            child.getMutableStats().getFighterRefitTimeMult().modifyPercent("TDB_ji_yu_yun", 25f);
        } else {
            child.getMutableStats().getBallisticWeaponRangeBonus().unmodify("TDB_ji_yu_yun");
            child.getMutableStats().getEnergyWeaponRangeBonus().unmodify("TDB_ji_yu_yun");
            child.getMutableStats().getFighterRefitTimeMult().unmodify("TDB_ji_yu_yun");
        }

    }

    //本体相关
    private void advanceParent(final ShipAPI parent, java.util.List<ShipAPI> children) {
		final CombatEngineAPI engine = Global.getCombatEngine();
		ShipEngineControllerAPI ec = parent.getEngineController();//本体引擎控制

        float depCost = 0f;
        if (parent.getFleetMember() != null) {
            depCost = parent.getFleetMember().getDeployCost();
        }

        float crLoss = 1f * depCost;
        if (parent.getHitpoints()<3000 && parent.getCurrentCR() >= crLoss) {
            //parent.setHitpoints(1f);
            //ship.setCurrentCR(Math.max(0f, ship.getCurrentCR() - crLoss));
            if (parent.getFleetMember() != null) { // fleet member is fake during simulation, so this is fine
                parent.getFleetMember().getRepairTracker().applyCREvent(-crLoss, "Emergency phase dive");
                //ship.getFleetMember().getRepairTracker().setCR(ship.getFleetMember().getRepairTracker().getBaseCR() + crLoss);
            }


            parent.setJitterUnder(parent,TDB_ColorData.TDBblue,5,6,4);

            parent.getMutableStats().getHullDamageTakenMult().modifyMult("TDB_ji_yu_yun", 0.15f);

            parent.getEngineController().fadeToOtherColor(this, TDB_ColorData.TDBpurplish_red, TDB_ColorData.TDBblue4, 1f, 0.4f);
            parent.getEngineController().extendFlame(this, 15f, 5f, 13f);

            float size = MathUtils.getRandomNumberInRange(18f, 12f);
            float angle = MathUtils.getRandomNumberInRange(-1f, 360f);
            Vector2f loc = MathUtils.getPointOnCircumference(parent.getLocation(), MathUtils.getRandomNumberInRange(100f, 500f), angle);
            Vector2f lvel = MathUtils.getPointOnCircumference(parent.getVelocity(), 150, angle);

            engine.addHitParticle(loc, lvel, size, 2f, MathUtils.getRandomNumberInRange(1f, 1.5f), TDB_ColorData.TDBblue4);

            if (yq)
            {
                Global.getSoundPlayer().playSound("TDB_YQ", 1f, 1f, parent.getLocation(), parent.getVelocity());
                float timeMult = parent.getMutableStats().getTimeMult().getModifiedValue();
                Global.getCombatEngine().addFloatingTextAlways(parent.getLocation(), txt("JYY_1"),
                        NeuralLinkScript.getFloatySize(parent) + 5f, TDB_ColorData.TDBred, parent, 5f, 3.2f / timeMult, 5f , 0f, 0f,
                        1f);
                yq = false;
            }

            if (parent.isRetreating())
            {
                engine.getCombatUI().addMessage(10,TDB_ColorData.TDBblue3 , parent ,txt("JYY_2") ,"(",parent.getHullSpec().getHullName(),")",parent.getName());
                yq = true;
            }


            final Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                public void run()
                {
                    if(engine.isPaused())
                    {
                        try {
                            Thread.sleep(100000000*50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else
                    {
                        Thread.interrupted();
                    }
                    if (parent.isAlive())
                    {

                        Vector2f ship_loc = parent.getLocation();
                        Vector2f vel = new Vector2f(parent.getVelocity());
                        Vector2f Location = new Vector2f(parent.getLocation());
                        //效果
                        float radius = 400f;

                        for(int i = -13; i < 34; ++i) {
                            float size = MathUtils.getRandomNumberInRange(12f, 24f);
                            float factor = MathUtils.getRandomNumberInRange(5f,15f);
                            Vector2f vel3 = MathUtils.getPointOnCircumference((Vector2f)null, (float)i * 25f * factor, parent.getFacing() + 90f);
                            Vector2f vel2 = MathUtils.getPointOnCircumference((Vector2f)null, (float)i * 25f * factor, parent.getFacing() + 90f);
                            engine.addHitParticle(parent.getLocation(), vel3, size, 1f, 1f, TDB_ColorData.TDBblue4);
                            engine.addSmoothParticle(parent.getLocation(), vel2, size, 1f, 1f, TDB_ColorData.TDBblue4);
                        }

                        MagicLensFlare.createSharpFlare(engine, parent, ship_loc, 10, 600, 0, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);

                        float lifetime = 0.8f;
                        RippleDistortion Rip = new RippleDistortion();
                        Rip.setLocation(Location);
                        Rip.setIntensity(150f);
                        Rip.setLifetime(lifetime);
                        Rip.setFrameRate(60f / lifetime);
                        Rip.setCurrentFrame(0.f);
                        Rip.setSize(radius);
                        Rip.fadeInSize(0.25f * lifetime);
                        Rip.fadeOutIntensity(lifetime);
                        Rip.flip(true);
                        DistortionShader.addDistortion(Rip);

                        //生成扭曲
                        easyRippleOut(parent.getLocation(), vel, parent.getCollisionRadius() * 4f, 100f, 1f, 20f);
                        MagicLensFlare.createSharpFlare(engine, parent, ship_loc, 19f, parent.getCollisionRadius() * 2, parent.getFacing() + 90f, TDB_ColorData.TDBblue, TDB_ColorData.TDBblue3);
                        parent.setExtraAlphaMult(1f);
                        parent.getLocation().set(0, -1000000f);
                        parent.setRetreating(true, false);
                    }
                }
            },3000);
        }
    }

    public void advanceInCombat(ShipAPI ship, float amount) {
        //检查是模块还是本体来决定激活哪一个效果
        ShipAPI parent = ship.getParentStation();

        if (parent != null) {
            /*if (ship.getTravelDrive().isActive()) {

            }*/
            advanceChild(ship, parent);

        }

        List<ShipAPI> children = ship.getChildModulesCopy();
        if (children != null && !children.isEmpty()) {
            advanceParent(ship, children);
        }
    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        float FIGHTERS_MODIFIER = 10f;
        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
        MutableShipStatsAPI fStats = fighter.getMutableStats();
        fStats.getBallisticWeaponDamageMult().modifyFlat(id, FIGHTERS_MODIFIER / 100);
        fStats.getEnergyWeaponDamageMult().modifyFlat(id, FIGHTERS_MODIFIER / 100);
        fStats.getMaxSpeed().modifyPercent(id, FIGHTERS_MODIFIER / 100);
    }

    //更多的描述拓展
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        if (ship == null) return;
        float pad = 10f;
        tooltip.addSectionHeading(txt("JYY_3"), Alignment.TMID, 4f);
        tooltip.addPara("", 4f);
        tooltip.addPara("[%s]", 2, Misc.getHighlightColor(), TDB_ColorData.TDBblue3,txt("JYY_4"));
        tooltip.addPara(txt("JYY_5"), 4f);
        tooltip.addPara("", 4f);
        tooltip.addPara("[%s]", 2, Misc.getHighlightColor(), TDB_ColorData.TDBblue3,txt("JYY_6"));
        tooltip.addPara(txt("JYY_7"), 4f);
    }
}
