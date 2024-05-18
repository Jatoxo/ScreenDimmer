package main;

import java.util.EventListener;

public interface DimListener extends EventListener {
    void dimChanged(int newDimLevel);
}
