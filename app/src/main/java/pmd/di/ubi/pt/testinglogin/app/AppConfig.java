package pmd.di.ubi.pt.testinglogin.app;

public class AppConfig {

//    private static String ServerIP = "188.251.6.95:80/android_api";
    private static String ServerIP = "188.251.6.95";

    // Server user login url
    //public static String URL_LOGIN = "http://188.251.6.95:80/Login.php";
    public static String URL_LOGIN = "http://"+ServerIP+"/Login.php";


    // Server edit user url
    public static String URL_EDIT_USER = "http://"+ServerIP+"/Edit_User.php";

    // Server user register url
    //public static String URL_REGISTER = "http://188.251.6.95:80/Register.php";
    public static String URL_REGISTER = "http://"+ServerIP+"/Register.php";

    public static String URL_REGISTERTOKEN = "http://"+ServerIP+"/insertToken.php";
}
