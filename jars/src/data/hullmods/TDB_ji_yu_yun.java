package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;

@SuppressWarnings("ALL")
public class TDB_ji_yu_yun extends BaseHullMod {
    //模块相关
    private static void advanceChild(ShipAPI child, ShipAPI parent) {

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

            //同步强制熄火
            ShipEngineControllerAPI cec = child.getEngineController();
            if ((cec.isFlamingOut() || cec.isFlamedOut()) && !ec.isFlamingOut() && !ec.isFlamedOut()) {
                parent.getEngineController().forceFlameout(true);
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
    /*private static void advanceParent(ShipAPI parent, java.util.List<ShipAPI> children) {
		CombatEngineAPI engine = Global.getCombatEngine();
		ShipEngineControllerAPI ec = parent.getEngineController();//本体引擎控制
		for( ShipAPI module : children)
		{
			if (module.getHullSpec().getBaseHullId().equals("TDB_ji_yu_yun_zuo"))
			{
				//如果模块死亡
				if (!module.isAlive())
				{
					engine.maintainStatusForPlayerShip("TDB_ji_yu_yun_zuo", "graphics/icons/hullsys/maneuvering_jets.png", "左翼模块损毁", "最高航速性下降", true);
					parent.getMutableStats().getMaxSpeed().modifyFlat("TDB_ji_yu_yun_zuo", 0.2f);
				}
			}

			if (module.getHullSpec().getBaseHullId().equals("TDB_ji_yu_yun_you"))
			{
				if (!module.isAlive())
				{
					engine.maintainStatusForPlayerShip("TDB_ji_yu_yun_you", "graphics/icons/hullsys/maneuvering_jets.png", "右翼模块损毁", "最高航速下降", true);
					parent.getMutableStats().getMaxSpeed().modifyFlat("TDB_ji_yu_yun_you" +
							"", 0.2f);
				}
			}
		}
    }*/

    public void advanceInCombat(ShipAPI ship, float amount) {
        //检查是模块还是本体来决定激活哪一个效果
        ShipAPI parent = ship.getParentStation();

        if (parent != null) {
            /*if (ship.getTravelDrive().isActive()) {

            }*/
            advanceChild(ship, parent);

        }

        //List<ShipAPI> children = ship.getChildModulesCopy();
        /*if (children != null && !children.isEmpty()) {
            advanceParent(ship, children);
        }*/

    }

    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        float FIGHTERS_MODIFIER = 10f;
        super.applyEffectsToFighterSpawnedByShip(fighter, ship, id);
        MutableShipStatsAPI fStats = fighter.getMutableStats();
        fStats.getBallisticWeaponDamageMult().modifyFlat(id, FIGHTERS_MODIFIER / 100);
        fStats.getEnergyWeaponDamageMult().modifyFlat(id, FIGHTERS_MODIFIER / 100);
        fStats.getMaxSpeed().modifyPercent(id, FIGHTERS_MODIFIER / 100);
    }
}
