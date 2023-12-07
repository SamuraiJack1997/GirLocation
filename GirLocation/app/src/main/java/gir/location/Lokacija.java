package gir.location;

public class Lokacija {
    int idLokacije;
    double geografskaSirina;
    double geografskaDuzina;
    double dozvoljenaDistanca;
    String imeLokacije;

    //Constructor
    public Lokacija(int idLokacije,double geografskaSirina, double geografskaDuzina,double dozvoljenaDistanca, String imeLokacije) {
        this.idLokacije=idLokacije;
        this.geografskaSirina = geografskaSirina;
        this.geografskaDuzina = geografskaDuzina;
        this.dozvoljenaDistanca=dozvoljenaDistanca;
        this.imeLokacije = imeLokacije;
    }

    //Haversine formula
    public boolean daLiSamUBliziniLokacije(double userSirina, double userDuzina) {
        double radiusZemlje = 6371000;
        double distancaGeografskeSirine = Math.toRadians(userSirina - geografskaSirina);
        double distancaGeografskeDuzine = Math.toRadians(userDuzina - geografskaDuzina);

        double a = Math.sin(distancaGeografskeSirine / 2) * Math.sin(distancaGeografskeSirine / 2)
                + Math.cos(Math.toRadians(userSirina)) * Math.cos(Math.toRadians(geografskaSirina))
                * Math.sin(distancaGeografskeDuzine / 2) * Math.sin(distancaGeografskeDuzine / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        if((Math.round(radiusZemlje * c))<dozvoljenaDistanca){
            return true;
        }else{
            return false;
        }
    }

    //Funckija koja vracu distancu od zeljenje lokacije
    public int distancaDoUseraUMetrima(double userSirina, double userDuzina) {
        double radiusZemlje = 6371000;
        double distancaGeografskeSirine = Math.toRadians(userSirina - geografskaSirina);
        double distancaGeografskeDuzine = Math.toRadians(userDuzina - geografskaDuzina);

        double a = Math.sin(distancaGeografskeSirine / 2) * Math.sin(distancaGeografskeSirine / 2)
                + Math.cos(Math.toRadians(userSirina)) * Math.cos(Math.toRadians(geografskaSirina))
                * Math.sin(distancaGeografskeDuzine / 2) * Math.sin(distancaGeografskeDuzine / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int)Math.round(radiusZemlje * c);

    }

    public double getGeografskaSirina() {
        return geografskaSirina;
    }

    public void setGeografskaSirina(double geografskaSirina) {
        this.geografskaSirina = geografskaSirina;
    }

    public double getGeografskaDuzina() {
        return geografskaDuzina;
    }

    public void setGeografskaDuzina(double geografskaDuzina) {
        this.geografskaDuzina = geografskaDuzina;
    }

    public String getImeLokacije() {
        return imeLokacije;
    }

    public void setImeLokacije(String imeLokacije) {
        this.imeLokacije = imeLokacije;
    }
}
