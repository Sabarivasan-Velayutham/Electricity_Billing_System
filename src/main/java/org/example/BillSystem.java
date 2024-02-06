package org.example;
class BillSystem {
    private static BillSystem instance;

    private BillSystem() {
        // private constructor for Singleton
    }

    public static synchronized BillSystem getInstance() {
        if (instance == null) {
            instance = new BillSystem();
        }
        return instance;
    }
}