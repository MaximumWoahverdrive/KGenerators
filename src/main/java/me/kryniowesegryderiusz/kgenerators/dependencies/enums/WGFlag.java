package me.kryniowesegryderiusz.kgenerators.dependencies.enums;

import lombok.Getter;

public enum WGFlag {
	PICK_UP ("kgenerators-pick-up", true),
	ONLY_GEN_BREAK ("kgenerators-only-gen-break", false),
	INTERACT ("kgenerators-interact", true),
	;
	
	@Getter
	final String flagId;
	@Getter
	final Boolean defaultState;

	WGFlag(String flagId, Boolean defaultState) {
		this.flagId = flagId;
		this.defaultState = defaultState;
	}
}
