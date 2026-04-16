package br.ufal.ic.myfood;

import easyaccept.EasyAccept;

public class Main {
    public static void main(String[] args) {
        EasyAccept.main(new String[] {"br.ufal.ic.myfood.Facade", "tests/us1_1.txt"});
        EasyAccept.main(new String[] {"br.ufal.ic.myfood.Facade", "tests/us1_2.txt"});
        EasyAccept.main(new String[] {"br.ufal.ic.myfood.Facade", "tests/us2_1.txt"});
        EasyAccept.main(new String[] {"br.ufal.ic.myfood.Facade", "tests/us2_2.txt"});
    }
}
