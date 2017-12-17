package utils;

/**
 * Created by Li Wenzhao on 2017/11/1.
 */

public class URL_UNIVERSAL {
    //**************接口参数部分**********
    public static final String SYSID = "80001";
    public static final String APPKEY = "abcqwe#123";

    //**************接口部分**************
    //接口地址前缀
    public static final String BASE_URL = "http://121.42.172.18:8062/api/App/";
    //注册
    public static final String REGISTER = BASE_URL + "Register";
    //登录
    public static final String LOGIN = BASE_URL + "Login";
    //修改密码
    public static final String RESET_PASSWORD = BASE_URL + "Resetpwd";
    //上传用户基本信息
    public static final String SET_USER_BASIC_INFO = BASE_URL + "UserBasicInfo";
    //获取推送信息
    public static final String GET_MESSAGE = BASE_URL + "GetMessage";
    //获取用户信息
    public static final String GET_USER_INFO = BASE_URL + "GetUserInfo";
    //上传操作信息
    public static final String PUSH_OPERATE_INFO = BASE_URL + "Push";
    //新闻
    public static final String NEWS_URL = "http://cheerstech.cn:8062/api/app/GetNewsList";


    //**************指令部分**************
    //正转
    public static final String DIRECTION_POSITIVE = "60";
    //反转
    public static final String DIRECTION_NEGATIVE = "61";
    //灯开
    public static final String LIGHT_OPEN = "62";
    //灯关
    public static final String LIGHT_CLOSE = "63";
    //音乐开
    public static final String MUSIC_OPEN = "64";
    //音乐关
    public static final String MUSIC_CLOSE = "65";
    //音量一
    public static final String MUSIC_VOICE_ONE = "03";
    //音量二
    public static final String MUSIC_VOICE_TWO = "04";
    //音量三
    public static final String MUSIC_VOICE_THREE = "05";
    //音量四
    public static final String MUSIC_VOICE_FOUR = "06";
    //上一首
    public static final String MUSIC_LAST = "01";
    //下一首
    public static final String MUSIC_NEXT = "07";
    //模式一
    public static final String MODE_ONE = "67";
    //模式二
    public static final String MODE_TWO = "68";
    //模式三
    public static final String MODE_THREE = "69";
    //模式四
    public static final String MODE_FOUR = "70";
    //速度一档
    public static final String SPEED_ONE = "11";
    //速度二档
    public static final String SPEED_TWO = "12";
    //速度三档
    public static final String SPEED_THREE = "13";
    //速度四档
    public static final String SPEED_FOUR = "14";
    //速度五档
    public static final String SPEED_FIVE = "15";
    //速度六档
    public static final String SPEED_SIX = "16";
    //总开开
    public static final String SWITCH_OPEN = "99";
    //总开关
    public static final String SWITCH_CLOSE = "90";

}
