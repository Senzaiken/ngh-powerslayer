package org.dreambot.powerslayer.states;

import org.dreambot.powerslayer.abstracts.GoToState;
import org.dreambot.powerslayer.common.MethodBase;

public class GoToMasterState extends GoToState {
	//TODO: Write State
    public GoToMasterState(MethodBase methods) {
        super(methods);
    }

    public int loop() {
        return 0;
    }

    public boolean activeCondition() {
        return false;
    }
}
