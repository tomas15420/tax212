package eu.tmach.trading212.model;

public enum OrderStatus {
    LOCAL,
    UNCONFIRMED,
    CONFIRMED,
    NEW,
    CANCELLING,
    CANCELLED,
    PARTIALLY_FILLED,
    FILLED,
    REJECTED,
    REPLACING,
    REPLACED
}