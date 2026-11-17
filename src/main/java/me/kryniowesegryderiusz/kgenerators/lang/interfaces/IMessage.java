package me.kryniowesegryderiusz.kgenerators.lang.interfaces;

public interface IMessage {
    String getKey();

    String getMessage();

    Enum<?> getEnum();
}
