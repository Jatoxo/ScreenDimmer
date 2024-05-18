package main;

import java.util.LinkedList;
import java.util.List;

public abstract class Dimming {
    private final List<DimListener> listeners = new LinkedList<>();

    void setDimInternal(int level) {
        int currentLevel = getDim();

        setDim(level);

        if(currentLevel != getDim()) {
            notifyListeners(level);
        }
    }
    public abstract int getDim();

    public abstract void setDim(int level);

    private void notifyListeners(int level) {
        for (DimListener listener : listeners) {
            listener.dimChanged(level);
        }
    }

    public void addChangeListener(DimListener listener) {
        listeners.add(listener);
    }
}

