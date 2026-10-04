package com.roudane.commerce.order.application.usecase.order;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TestMain {

    public static void main(String[] args) {
        Map<Client, String> map = new HashMap<>();

        Client c1 = new Client("A123");
        Client c2 = new Client("A123");

        // 1. Vérification logique
        System.out.println(c1.equals(c2)); // Affiche TRUE (ils ont le même ID)

        // 2. Stockage dans la HashMap
        map.put(c1, "Compte Premium");

        // 3. Recherche avec un objet logiquement identique
        String resultat = map.get(c2);

        System.out.println("Résultat : " + resultat); // Affiche NULL !
    }
}

class Client {
    private String id;

    public Client(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // hashCode() N'EST PAS redéfini !
    // Il hérite du hashCode() par défaut de Object (basé sur l'adresse mémoire).
}
