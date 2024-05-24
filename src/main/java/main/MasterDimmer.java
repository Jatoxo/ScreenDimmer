package main;

import java.util.LinkedList;
import java.util.List;

public class MasterDimmer implements Dimmable {
    private final Dimmable[] dimmers;
    private List<DimListener> listeners = new LinkedList<>();

    /**
     * Virtual dimmer that controls multiple dimmers.
     * @param dimmers The dimmers to control.
     */
    public MasterDimmer(Dimmable... dimmers) {
        this.dimmers = dimmers;
    }

    /**
     * Set the dim level of all dimmers.
     * @param dimLevel The dim level. (0-100)
     */
    @Override
    public void setDim(int dimLevel) {
        //System.out.println("Setting master dim level to " + dimLevel);
        for(Dimmable dimmer : dimmers) {
            dimmer.setDim(dimLevel);
        }
        notifyListeners(dimLevel);
    }

    /**
     * Get the dim level of the first dimmer.
     * @return The dim level. (0-100)
     */
    @Override
    public int getDim() {
        //Just return the dim level of the first dimmer for now
        //TODO: Implement master dim somehow
        if(dimmers.length == 0) {
            return 0;
        }
        //Just return the dim level of the first dimmer for now
        return dimmers[0].getDim();
    }



    @Override
    public void addChangeListener(DimListener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(int dimLevel) {
        for(DimListener listener : listeners) {
            listener.dimChanged(dimLevel);
        }
    }
}
