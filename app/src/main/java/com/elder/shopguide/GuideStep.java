package com.elder.shopguide;

public class GuideStep {
    public String icon;
    public String title;
    public String subtitle;
    public String text;
    public String tip;

    public GuideStep(String icon, String title, String subtitle, String text, String tip) {
        this.icon = icon;
        this.title = title;
        this.subtitle = subtitle;
        this.text = text;
        this.tip = tip;
    }

    public static GuideStep[] getSteps() {
        return new GuideStep[]{
            new GuideStep("\uD83D\uDCF1","打开购物APP","准备工作",
                "请打开手机上的淘宝、京东或拼多多APP。确保已经登录了您的账号（用手机号和验证码登录就行）。",
                "第一次用的话，建议选京东或淘宝"),
            new GuideStep("\uD83D\uDD0D","搜索商品","找到想买的东西",
                "在APP顶部搜索框里，输入您想买的东西。比如想买米就输入'东北大米'，想买鞋就输入'老人健步鞋'。输完后点搜索按钮。不会打字的话，点搜索框旁边的小话筒用语音说。",
                "语音搜索最方便：点话筒图标，对着手机说想买的东西就好了"),
            new GuideStep("\uD83D\uDC40","挑选商品","看看哪个好",
                "搜索结果出来了很多商品。先看销量——买的人多的通常不错。再看评价——点进去看带图的评价和差评。然后看价格——多比较几家。最后看店铺评分，选4.8分以上的更靠谱。",
                "看评价时重点看带图片的评价，那些是真实买家拍的"),
            new GuideStep("\uD83D\uDED2","加入购物车","把商品放进去",
                "点进您选中的商品页面。先选好规格——比如衣服的颜色尺码，食品的数量。然后找到'加入购物车'按钮（绿色或橙色的大按钮），点一下。商品就放进购物车了。",
                "第一次买建议先选便宜的小东西试试，比如一包纸巾"),
            new GuideStep("","去购物车结算","准备付钱",
                "选好了所有要买的东西。点APP右下角的'购物车'图标。在购物车页面勾选您要买的商品。然后点右下角的'结算'按钮。",
                "如果不想买了，可以在购物车页面点商品右边的'删除'去掉"),
            new GuideStep("\uD83D\uDCDD","填写收货地址","送到哪里",
                "现在需要填写收货地址。输入您的姓名、手机号和详细地址。地址要写清楚：xx省xx市xx区xx街道xx小区xx栋xx号。如果白天家里没人，可以写'放快递柜'或'放门卫处'。",
                "手机号一定要写对，快递员会打电话联系您"),
            new GuideStep("\uD83D\uDCB0","选择支付方式","怎么付钱",
                "到了支付页面，选择支付方式。推荐选'货到付款'——东西送到家，检查满意了再给钱，最放心。也可以选微信支付或支付宝（需要先让家人帮忙绑定银行卡）。选好后点'提交订单'。",
                "付款时仔细看金额对不对。不要告诉任何人您的支付密码！"),
            new GuideStep("\uD83C\uDF89","完成购物","等快递到家",
                "恭喜您！下单成功了！接下来等快递员给您打电话就行。您可以在APP里点'我的'→'我的订单'查看物流信息。收到货后检查一下，没问题就点'确认收货'。如果东西不满意，7天内可以申请退货。",
                "第一次网购成功！以后买东西就越来越熟练了")
        };
    }
}
