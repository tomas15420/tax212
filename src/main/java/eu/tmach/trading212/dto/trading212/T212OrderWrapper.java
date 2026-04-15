package eu.tmach.trading212.dto.trading212;

public record T212OrderWrapper(
        T212Fill fill,
        T212OrderDetails order
) {
}