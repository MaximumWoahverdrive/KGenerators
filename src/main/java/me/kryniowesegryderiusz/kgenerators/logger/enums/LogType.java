package me.kryniowesegryderiusz.kgenerators.logger.enums;

import lombok.Getter;

@Getter
public enum LogType {
    INFO(""),
    ERROR("!!! ERROR !!! "),
    DEBUG("[Debug] "),
    WARNING("!!! WARNING !!! "),
    ;
    final String header;

    LogType(String header) {
        this.header = header;
    }
}
