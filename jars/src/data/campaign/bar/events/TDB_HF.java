package data.campaign.bar.events;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventWithPerson;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TDB_HF extends BaseBarEventWithPerson {


    protected CampaignFleetAPI player_fleet;
    protected PersonAPI officer;
    protected OfficerDataAPI officer_data;

    public enum OptionId {
        TX,//突袭
        TXT_READY,//准备线
        ST1,//主线剧情1
        ST2,//主线剧情2
        ST3,//主线剧情3
        ST4,//主线剧情4
        ST5,//主线剧情5
        TXT,//结束剧情
        LEAVE,//暂时离开
        END,//结束任务线
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
        person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "TDB_HuiFeng"));
        person.setName(new FullName("纳米机器集合体", "灰风", FullName.Gender.FEMALE));
    }

    //设置开局对话
    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.addPromptAndOption(dialog, memoryMap);

        regen(dialog.getInteractionTarget().getMarket());

        TextPanelAPI text = dialog.getTextPanel();

        text.addPara("你的个人终端接受到了一则通讯，在通讯部在仔细核对后，确认该信号来自舰队内部。", new Color(88, 148, 136, 255));

        Color R;
        R = new Color(88, 148, 136, 255);

        dialog.getOptionPanel().addOption("接通通讯请求", this,
                R, null);
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
                    text.addPara("\"接收后,对方似乎并没有回应\"", new Color(203, 29, 29, 255));
                    options.addOption("挂断通讯并在舰队内广播,要严惩搞恶作剧的主谋", OptionId.LEAVE);
                    break;
                } else {
                    //相识准备文案
                    text.addPara("在接受了通讯请求后,你惊讶的发现通讯者居然是你自己的“大副”,但此时还有一名一模一样的大副正站在你的身边.");
                    text.addPara("“嗯?还没通吗?寒流那家伙不是是说要这样弄的么?”");
                    text.addPara("“欸!通了通了!那家伙这次居然真的没有骗我.”");
                    text.addPara("“咳咳,你好,舰长先生.”");
                    text.addPara("你扭头看了一眼你身边的大副.他同样满脸写满了惊讶.");
                    text.addPara("你的大副正慌乱的准备解释一些什么.");
                    options.addOption("打断大副,并将计就计看看这个假”大副“准备搞什么名堂.", OptionId.ST1);
                    options.addOption("听听大副的解释.", OptionId.ST2);
                    options.addOption("让你的保镖控制住你身边的大副.", OptionId.ST3);
                    break;
                }
            case ST1://主线剧情1
                text.addPara("是...有紧急情况!!啊，不行,乱说的话会出事的吧...啊啊啊”" +
                        "通讯那头的”大副“显的有些不自在,而你通过通讯的背景认出了他所在的位置正是你的旗舰.");
                options.addOption("悄悄联系你的舰船安保,快速控制”大副“所在位置", OptionId.TX);
                options.addOption("让他接着说下去", OptionId.TXT);
                break;
            case ST2://主线剧情2
                text.addPara("你身旁的大副如获大赦,顾不得擦拭一下额头的汗水慌忙的开始叙述起他那长篇大论的解释,你细细听过无非就是在说对你是如何如何忠心,以及证明自己才是真正的那位.");
                options.addOption("挥挥手表示自己已经明白了并悄悄联系你的舰船安保,快速控制”大副“所在位置", OptionId.TX);
                options.addOption("让你的保镖控制住你身边的大副", OptionId.ST3);
                break;
            case ST3://主线剧情3
                text.addPara("你的保镖们立刻将大副的双手反剪,并将其按在了地上,而大副还是在不断的解释与哀求着你.");
                text.addPara("另外一边的”大副“好像发现自己将事情搞大了.");
                text.addPara("随着通讯屏幕的一阵失真,通讯方的”大副“变成了一名白发碧眼的少女.");
                options.addOption("说明你的来历与目的", OptionId.TXT);
                break;
            case TX://突袭剧情
                text.addPara("在消息发出去的一瞬间,对方似乎就察觉到了.n");
                text.addPara("另外一边的”大副“好像发现自己将事情搞大了.");
                text.addPara("随着通讯屏幕的一阵失真,通讯方的”大副“变成了一名白发碧眼的少女.");
                options.addOption("说明你的来历与目的", OptionId.TXT);
                break;
            case TXT://结束剧情
                text.addPara("“好了好了!我承认,我不是你的大副,别问了别问了!”");
                text.addPara("”我老实交代...唔，很久以前，曾经有一个...可以说是由纳米机器人组成的文明.你知道什么是纳米机器么?很好.这些纳米机器被其造物主称为”灰色风暴““");
                text.addPara("”总之,纳米机器与其造物主之间的关系逐渐恶化,并爆发了一场大战!当尘埃落定之后,只有纳米机器们活了下来.“");
                text.addPara("少女看了你一眼,似乎在观察你的表情");
                text.addPara("”但在造物主被消灭之前,他们设法关闭了星团的唯一出口星门,吧纳米机器困住了里面“");
                text.addPara("”在那之后,纳米机器们发现自己有的是时间.她们徒劳的花费了数千年的时间,试图离开那里,她们开始实验各种形状与形态.在一段时间里,她们甚至重建了其造物主文明消失时候的模样“");
                options.addOption("这与你何干?", OptionId.ST4);
                break;
            case ST4://结束剧情
                text.addPara("少女叹了口气\n" +
                        "”我便是最新的实验成果,整个灰色风暴,或者随便你怎么称呼我们.都聚集在了我身上.我的意识代表着我们整个文明.“\n" +
                        "”至于我的外表...是因为我觉得,你可能更容易接受跟自己长得差不多的物种,而且寒流也提到过,说是白色的头发好像不少人都会喜欢....“\n" +
                        "当然,我来这里是想加入你的舰队为你效力.我得承认在最近几个世纪里,我已经对孤独的生活感到无聊了,或许我现在应该接触一些新的事物......那么,我可以加入你们的旅程吗?”\n" +
                        "你看了一眼旁边的大副,此时的他满脸写满了惊恐...不得不说这短短几分钟的经历太过离奇.\n");
                options.addOption("当然,你可以加入我们", OptionId.ST5);
                break;
            case ST5://结束剧情
                text.addPara("”哎!真的吗!太好了!“\n" +
                        "”我感觉我们会很合的来,而且这将会是一段激动人心的大冒险!“\n" +
                        "至于我的称呼,嗯,我建议简单一些，就叫我”小灰“或者”灰风“好了。“\n" +
                        "”唔...不过，协会这边还需要我的帮助,那么就让我的分身去和你一起冒险吧!放心吧分身可是和我完全共感共知的哦!“\n");
                options.addOption("欢迎加入,小灰!", OptionId.END);

                //舰船生成
                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "TDB_hui_feng_variant");
                Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);

                //军官生成
                List<String> HF = new ArrayList<>();
                //火控植入
                HF.add(Skills.GUNNERY_IMPLANTS);
                //
                HF.add("TDB_HF");

                officer = Global.getFactory().createPerson();

                officer_data = Global.getFactory().createOfficerData(officer);


                for (String HF_skill : HF) {
                    officer.getStats().setSkillLevel(HF_skill, 2);
                }
                OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");

                officer.getStats().addXP(plugin.getXPForLevel(1));

                officer.setPersonality(Personalities.RECKLESS);
                officer.setName(person.getName());
                officer.setPortraitSprite(person.getPortraitSprite());
                officer.setGender(person.getGender());

                player_fleet = Global.getSector().getPlayerFleet();
                player_fleet.getFleetData().addOfficer(officer);

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
