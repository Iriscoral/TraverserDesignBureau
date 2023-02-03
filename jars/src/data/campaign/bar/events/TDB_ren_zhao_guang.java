package data.campaign.bar.events;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventWithPerson;
//import com.fs.starfarer.api.impl.campaign.intel.contacts.ContactIntel;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//import static com.fs.starfarer.api.impl.campaign.intel.contacts.ContactIntel.DEFAULT_POTENTIAL_CONTACT_PROB;
import static data.campaign.bar.events.TDB_ren_zhao_guang.OptionId.*;

public class TDB_ren_zhao_guang extends BaseBarEventWithPerson {

    protected CampaignFleetAPI player_fleet;
    protected PersonAPI officer;
    protected OfficerDataAPI officer_data;
    public static String txt(String id) {
        return Global.getSettings().getString("campaign", id);
    }

    public enum OptionId {
        TXT_READY,//准备线
        STORY,//主线剧情1
        STORY2,//主线剧情2
        TXT,//结束剧情
        LEAVE,//暂时离开
        END,//结束任务线
        YB,//摸尾巴
        SYS,//实验室
    }


    public TDB_ren_zhao_guang() {
        super();
    }

    @Override
    public boolean shouldShowAtMarket(MarketAPI market) {
        if (!super.shouldShowAtMarket(market)) return false;
        //获取派系id
        if (!market.getFactionId().contentEquals("TDB")) {
            return false;
        }

        return Global.getSector().getPlayerStats().getLevel() >= 0 || DebugFlags.BAR_DEBUG;
    }

    @Override
    protected void regen(MarketAPI market) {
        if (this.market == market) return;
        super.regen(market);
        //设置剧情对话人/姓名/性别
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HanLiu"));
        person.setName(new FullName("局长", "寒流", FullName.Gender.FEMALE));
    }

    //设置开局对话
    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.addPromptAndOption(dialog, memoryMap);

        regen(dialog.getInteractionTarget().getMarket());

        TextPanelAPI text = dialog.getTextPanel();

        text.addPara("酒馆的角落里一个正在看着电脑戴着兜帽的女孩坐在酒馆的角落，而隐约透出的淡蓝色长发和巨大的尾巴表明了她很可能是来自异世界的人。", new Color(24, 71, 122, 255));

        Color R;
        R = new Color(24, 71, 122, 255);

        dialog.getOptionPanel().addOption("尝试点杯威士忌，坐到她的对面。", this, R, null);
    }

    @Override
    public void init(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.init(dialog, memoryMap);


        done = false;

        dialog.getVisualPanel().showPersonInfo(person, true);

        optionSelected(null, OptionId.TXT_READY);
    }

    public void optionSelected(String optionText, Object optionData) {
        if (!(optionData instanceof OptionId)) {
            return;
        }
        OptionId option = (OptionId) optionData;

        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI text = dialog.getTextPanel();
        options.clearOptions();
        //剧情部分
        switch (option) {
            case TXT_READY://准备剧情
                if (Global.getSector().getFaction("TDB").getRelToPlayer().getRel() < 0.90f) {
                    //不相识文案 好感小于90
                    text.addPara("\"你尝试上前去和少女搭话，但少女甚至都没有看你一眼便张口说到\"我想我们并不认识，还是尽量保持距离比较好，不是么？\"。\"", new Color(203, 29, 29, 255));
                    options.addOption("离开", LEAVE);
                    break;
                } else {
                    //相识准备文案
                    text.addPara("气氛沉静了片刻，少女面带微笑，掏出一封信递给了你，上面还有一朵蓝色的鸢尾花，信件没有被打开的痕迹。\n" +
                            "你对此感到惊讶，毕竟这个时代已经很少有用纸质文件记录东西的了。\n");
                    options.addOption("收好信件，并回敬一个善意的微笑。", STORY);
                    options.addOption("收好信件，并趁机摸一下她的尾巴。", YB);
                    break;
                }
            case STORY://主线剧情1
                text.addPara("很显然少女看见你非常的高兴，尽管并没有表现在脸上，但来回摆动的尾巴出卖了她心中的想法。“看起来您就是最近大家经常说的那位“星系中的传奇佣兵了””\n" +
                        "你好奇的打量着她，准备提出心中的疑问。\n" +
                        "少女看出了你的疑问并提议道“先看看这个再做出选择也不晚”说完少女摇着尾巴出去了，临走时还为你点了一杯抹茶饮品，留下有些迷惑的你和先前收下的那一封信件。\n" +
                        "你看向手中的信封，也许信里有你想知道的答案呢？\n");
                options.addOption("打开信件", STORY2);
                break;
            case STORY2://主线剧情2
                text.addPara("你打开并阅读信件，而当你拿起它的时候，你闻到了浓浓的鸢尾花的香味。");
                text.addPara("“您好，敬爱的佣兵先生，我越发的相信，您是我们的同伴之一了，您的传奇事迹在我们整个穿越者协会中都口口相传，您曾帮助我们许多，而现在，正如古老的诗歌中所述，这个世界，便需要您这样的英雄，我知道，您只是一名雇佣兵而我们之间的合作关系不可能一直持续下去。也许是明天，也许是明年您可能就会由于各种原因站在我们的对立面了……啊，当然这不是说您这样做不对，毕竟这是您的工作……只是，希望这一天能晚一些到来……谨以此信，表明对您的诚挚心意——穿越者协会”");
                text.addPara("于此同时从信件里掉落的还有一枚芯片。");

                //options.addOption("进入实验室",SYS);

                options.addOption("查看芯片内容", TXT);
                break;
            case TXT://结束剧情
                Calendar T = Calendar.getInstance();
                int month = T.get(Calendar.MONTH) + 1;
                int date = T.get(Calendar.DATE);
//                int hour = T.get(Calendar.HOUR_OF_DAY);
//                int minute = T.get(Calendar.MINUTE);
//                int second = T.get(Calendar.SECOND);
                if (month == 12 && date == 3)
                {
                    text.addPara("今天，是幸运日哦！", new Color(29, 151, 203, 255));
                    //军官生成
                    List<String> HL = new ArrayList<>();
                    HL.add(Skills.MISSILE_SPECIALIZATION);
                    HL.add(Skills.ELECTRONIC_WARFARE);

                    officer = Global.getFactory().createPerson();

                    officer_data = Global.getFactory().createOfficerData(officer);


                    for (String HL_skill : HL) {
                        officer.getStats().setSkillLevel(HL_skill, 2);
                    }

                    OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");

                    officer.getStats().addXP(plugin.getXPForLevel(0));

                    officer.setPersonality(Personalities.STEADY);
                    officer.setName(new FullName("", "", FullName.Gender.FEMALE));
                    officer.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HanLiu2"));
                    officer.setGender(person.getGender());

                    player_fleet = Global.getSector().getPlayerFleet();
                    player_fleet.getFleetData().addOfficer(officer);
                }
                text.addPara("你打开了那枚芯片，上面显示了一艘像是攻势级战列舰的船舶，但连你是第一次见到她的样貌...但你很明显的能感觉到这艘舰船肯定是经过一个庞大的团队精心打造，而不是一个三流海盗工程师魔改攻势级的作品。\n" +
                        "下面还有一行备注:\n。");
                text.addPara("“这是我一位朋友的杰作，这一艘船可是她的宝贝，为了感激您为穿越者协会所做的一切，我会以我的名义将她赠送与您，佣兵先生。”\n" +
                        "最后是各种类似某种机构开出的证明一样的东西，并且附有详细的港口所在地址与联系方式。\n" +
                        "这份莫名奇妙的文件中种种线索都表明了少女的身份不一般，而你已经隐约对此有了猜想\n" +
                        "但由于对方看起来完全只是一只还没成年的少女导致你无法完全确认自己的猜想。\n" +
                        "也许.日后可以通过联系方式再去询问？\n" +
                        "摇了摇头，你决定.....\n");
                options.addOption("前往港口领取舰船", END);

                //舰船生成
                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "TDB_ren_zhao_guang_variant");
                Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);

                //建立联系人
//                ContactIntel.addPotentialContact(DEFAULT_POTENTIAL_CONTACT_PROB, person, market, text);
                break;

            case YB://主线剧情2
                text.addPara("少女很显然被你的举动吓了一大跳，并快速的将尾巴藏在了身后");
                text.addPara("你很快便意识到了这不礼貌，正打算道歉，但对方并没有给你这个机会直接跑了出去。");
                text.addPara("你呆呆的站在原地，突然间有些后悔这个举动.但是回想了一下那毛绒绒并带着体温的手感...“嗯...不亏”");
                text.addPara("你看向手中的信封，看起来只能希望信里有你想知道的答案了。");
                CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
                playerCargo.getCredits().set(Math.max(0f, playerCargo.getCredits().get() - 20));
                Misc.addCreditsMessage("失去了 %s 星币. ", 20);
                options.addOption("打开信件", STORY2);
                break;

            case SYS://实验室
                text.addPara("现在你正处于实验室中，下面的一切选项都是实验");

                //实验用

                FleetParamsV3 params = new FleetParamsV3(
                        null,
                        Factions.LUDDIC_CHURCH,
                        1.25f,
                        "Type",
                        4000f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f);
                params.ignoreMarketFleetSizeMult = true;

                CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

                LocationAPI location = Global.getSector().getCurrentLocation();
                location.addEntity(fleet);
                fleet.setLocation(0, 0);

                options.addOption("结束并离开（不会再刷新）", END);
                options.addOption("离开（会再刷新）", LEAVE);
                break;

            case LEAVE://暂时离开
                noContinue = true;
                done = true;
                break;

            case END://结束任务线
                noContinue = true;
                done = true;
                BarEventManager.getInstance().notifyWasInteractedWith(this);
                break;
        }
    }

    @Override
    protected String getPersonFaction() {
        return "TDB";
    }

    @Override
    protected String getPersonRank() {
        return Ranks.SPACE_SAILOR;
    }

    @Override
    protected String getPersonPost() {
        return Ranks.CITIZEN;
    }

    @Override
    protected String getPersonPortrait() {
        return null;
    }

    @Override
    protected FullName.Gender getPersonGender() {
        return FullName.Gender.ANY;
    }


}
