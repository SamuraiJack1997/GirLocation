package gir.location;

public class User {
    public static int id_user;
    public static String ime;
    public static String prezime;
    public static String macAdresa;
    public static String aktivan;
    public static boolean prijavljen=false;
    public static boolean macAdresaDodeljenja=false;

    public static int getId_user() {
        return id_user;
    }

    public static void setId_user(int id_user) {
        User.id_user = id_user;
    }

    public static String getAktivan() {
        return aktivan;
    }

    public static void setAktivan(String aktivan) {
        User.aktivan = aktivan;
    }

    public static boolean isMacAdresaDodeljenja() {
        return macAdresaDodeljenja;
    }

    public static void setMacAdresaDodeljenja(boolean macAdresaDodeljenja) {
        User.macAdresaDodeljenja = macAdresaDodeljenja;
    }

    public static boolean isPrijavljen() {
        return prijavljen;
    }

    public static void setPrijavljen(boolean prijavljen) {
        User.prijavljen = prijavljen;
    }

    public static int getId_usera() {
        return id_user;
    }
    public static void setId_usera(int id_usera) {
        User.id_user = id_usera;
    }

    public static String getIme() {
        return ime;
    }

    public static void setIme(String ime) {
        User.ime = ime;
    }

    public static String getPrezime() {
        return prezime;
    }

    public static void setPrezime(String prezime) {
        User.prezime = prezime;
    }

    public static String getMacAdresa() {
        return macAdresa;
    }

    public static void setMacAdresa(String macAdresa) {
        User.macAdresa = macAdresa;
    }
}
