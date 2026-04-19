package org.dreambot.powerslayer.abstracts;

import org.dreambot.powerslayer.common.DMethodProvider;
import org.dreambot.powerslayer.common.MethodBase;


public abstract class State extends DMethodProvider {

    public State(MethodBase methods) {
        super(methods);
    }

    public abstract int loop();

    public abstract boolean activeCondition();
}
