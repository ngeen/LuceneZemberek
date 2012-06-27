package org.LuceneZemberek.analysis.turkish;

import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;

public class KokBulucu {
    Zemberek zemberek = null;
    public KokBulucu(){
        // Create a zemberek turkish NLP lib instance.
        zemberek = new Zemberek(new TurkiyeTurkcesi());
    }
    
    public String kok(String token) {
        String[] kokler = zemberek.kokBulucu().stringKokBul(token);
        // Now we should disambiguate alternatives, but we dont have a decent disambiguator yet.
        if (kokler.length != 0){
            return kokler[0];
        }
        return token;
    }
}
